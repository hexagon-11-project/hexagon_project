package payment.paymentpayitempart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdbc.JdbcUtil;
import payment.model.PaymentItemLedger;

/**
 * 항목별 대장 Dao.
 * 지급/공제 통합 항목 목록과, 기간·항목으로 사원별 내역·합계를 조회한다.
 *
 * PAY_YEAR_MONTH 컬럼이 CHAR(6) 이므로 연월은 'YYYYMM' 문자열 범위로 조회한다.
 * 같은 월에 급여차수가 여러 건이면 차수별로 내역을 담는다.
 */
public class PaymentpayitempartDao {

	/**
	 * 셀렉트 박스용 지급항목+공제항목 통합 목록.
	 * 지급항목을 먼저, 그다음 공제항목을 표시 순서대로 반환한다.
	 */
	public List<PaymentItemLedger> selectItemList(Connection conn, int companyId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT ITEM_TYPE, ITEM_ID, ITEM_NAME FROM ( "
					+ "  SELECT '" + PaymentItemLedger.TYPE_PAY + "' AS ITEM_TYPE, "
					+ "         PAY_ITEM_ID AS ITEM_ID, PAY_ITEM_NAME AS ITEM_NAME, "
					+ "         1 AS TYPE_ORDER, DISPLAY_ORDER "
					+ "  FROM PAY_ITEM "
					+ "  WHERE COMPANY_ID = ? AND USE_YN = 'Y' "
					+ "  UNION ALL "
					+ "  SELECT '" + PaymentItemLedger.TYPE_DEDUCTION + "' AS ITEM_TYPE, "
					+ "         DEDUCTION_ITEM_ID AS ITEM_ID, DEDUCTION_ITEM_NAME AS ITEM_NAME, "
					+ "         2 AS TYPE_ORDER, DISPLAY_ORDER "
					+ "  FROM DEDUCTION_ITEM "
					+ "  WHERE COMPANY_ID = ? AND USE_YN = 'Y' "
					+ ") ORDER BY TYPE_ORDER, DISPLAY_ORDER, ITEM_ID";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, companyId);
			pstmt.setInt(2, companyId);
			rs = pstmt.executeQuery();

			List<PaymentItemLedger> result = new ArrayList<>();
			while (rs.next()) {
				result.add(new PaymentItemLedger(
						rs.getString("ITEM_TYPE"),
						rs.getLong("ITEM_ID"),
						rs.getString("ITEM_NAME")));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * 시작·종료 연월과 지급/공제 항목으로 기간 안 모든 사원의 내역을 조회한다.
	 * 사원별로 월(차수) 내역과 기간 합계를 채운다.
	 * 해당 기간에 항목 금액이 있는 사원만 반환한다.
	 */
	public List<PaymentItemLedger> selectByPeriodAndItem(Connection conn, int companyId,
			int startYear, int startMonth, int endYear, int endMonth,
			String itemType, Long itemId) throws SQLException {
		if (itemId == null || !isSupportedItemType(itemType)) {
			return new ArrayList<>();
		}

		String startYearMonth = toPayYearMonth(startYear, startMonth);
		String endYearMonth = toPayYearMonth(endYear, endMonth);
		if (startYearMonth.compareTo(endYearMonth) > 0) {
			return new ArrayList<>();
		}

		if (PaymentItemLedger.TYPE_PAY.equals(itemType)) {
			return selectPayItemLedger(conn, companyId, startYearMonth, endYearMonth, itemType, itemId);
		}
		return selectDeductionItemLedger(conn, companyId, startYearMonth, endYearMonth, itemType, itemId);
	}

	/** 지급항목 기간 조회. PAYROLL_PAY_DETAIL을 사원·연월·차수별로 합산한다. */
	private List<PaymentItemLedger> selectPayItemLedger(Connection conn, int companyId,
			String startYearMonth, String endYearMonth, String itemType, Long itemId) throws SQLException {
		String sql = "SELECT e.EMPLOYEE_ID, e.EMPLOYMENT_TYPE, e.EMPLOYEE_NAME, e.DEPARTMENT, e.POSITION, "
				+ "       i.PAY_ITEM_ID AS ITEM_ID, i.PAY_ITEM_NAME AS ITEM_NAME, "
				+ "       p.PAY_YEAR_MONTH, p.PAY_SEQUENCE, "
				+ "       TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 1, 4)) AS PAY_YEAR, "
				+ "       TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 5, 2)) AS PAY_MONTH, "
				+ "       NVL(SUM(d.AMOUNT), 0) AS AMOUNT "
				+ "FROM PAYROLL p "
				+ "JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
				+ "JOIN EMPLOYEE e ON pe.EMPLOYEE_ID = e.EMPLOYEE_ID "
				+ "JOIN PAYROLL_PAY_DETAIL d ON d.PAYROLL_EMPLOYEE_ID = pe.PAYROLL_EMPLOYEE_ID "
				+ "JOIN PAY_ITEM i ON d.PAY_ITEM_ID = i.PAY_ITEM_ID "
				+ "WHERE p.COMPANY_ID = ? "
				+ "  AND p.PAY_YEAR_MONTH BETWEEN ? AND ? "
				+ "  AND d.PAY_ITEM_ID = ? "
				+ "GROUP BY e.EMPLOYEE_ID, e.EMPLOYMENT_TYPE, e.EMPLOYEE_NAME, e.DEPARTMENT, e.POSITION, "
				+ "         i.PAY_ITEM_ID, i.PAY_ITEM_NAME, p.PAY_YEAR_MONTH, p.PAY_SEQUENCE "
				+ "ORDER BY e.EMPLOYEE_NAME, e.EMPLOYEE_ID, p.PAY_YEAR_MONTH, p.PAY_SEQUENCE";

		return selectLedger(conn, sql, companyId, startYearMonth, endYearMonth, itemType, itemId);
	}

	/** 공제항목 기간 조회. PAYROLL_DEDUCTION_DETAIL을 사원·연월·차수별로 합산한다. */
	private List<PaymentItemLedger> selectDeductionItemLedger(Connection conn, int companyId,
			String startYearMonth, String endYearMonth, String itemType, Long itemId) throws SQLException {
		String sql = "SELECT e.EMPLOYEE_ID, e.EMPLOYMENT_TYPE, e.EMPLOYEE_NAME, e.DEPARTMENT, e.POSITION, "
				+ "       di.DEDUCTION_ITEM_ID AS ITEM_ID, di.DEDUCTION_ITEM_NAME AS ITEM_NAME, "
				+ "       p.PAY_YEAR_MONTH, p.PAY_SEQUENCE, "
				+ "       TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 1, 4)) AS PAY_YEAR, "
				+ "       TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 5, 2)) AS PAY_MONTH, "
				+ "       NVL(SUM(dd.AMOUNT), 0) AS AMOUNT "
				+ "FROM PAYROLL p "
				+ "JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
				+ "JOIN EMPLOYEE e ON pe.EMPLOYEE_ID = e.EMPLOYEE_ID "
				+ "JOIN PAYROLL_DEDUCTION_DETAIL dd ON dd.PAYROLL_EMPLOYEE_ID = pe.PAYROLL_EMPLOYEE_ID "
				+ "JOIN DEDUCTION_ITEM di ON dd.DEDUCTION_ITEM_ID = di.DEDUCTION_ITEM_ID "
				+ "WHERE p.COMPANY_ID = ? "
				+ "  AND p.PAY_YEAR_MONTH BETWEEN ? AND ? "
				+ "  AND dd.DEDUCTION_ITEM_ID = ? "
				+ "GROUP BY e.EMPLOYEE_ID, e.EMPLOYMENT_TYPE, e.EMPLOYEE_NAME, e.DEPARTMENT, e.POSITION, "
				+ "         di.DEDUCTION_ITEM_ID, di.DEDUCTION_ITEM_NAME, p.PAY_YEAR_MONTH, p.PAY_SEQUENCE "
				+ "ORDER BY e.EMPLOYEE_NAME, e.EMPLOYEE_ID, p.PAY_YEAR_MONTH, p.PAY_SEQUENCE";

		return selectLedger(conn, sql, companyId, startYearMonth, endYearMonth, itemType, itemId);
	}

	private List<PaymentItemLedger> selectLedger(Connection conn, String sql, int companyId,
			String startYearMonth, String endYearMonth, String itemType, Long itemId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, companyId);
			pstmt.setString(2, startYearMonth);
			pstmt.setString(3, endYearMonth);
			pstmt.setLong(4, itemId);
			rs = pstmt.executeQuery();
			return groupByEmployee(rs, itemType);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/** 조회 결과를 사원 단위로 묶고, 월(차수) 내역과 기간 합계를 채운다. */
	private List<PaymentItemLedger> groupByEmployee(ResultSet rs, String itemType) throws SQLException {
		Map<Integer, PaymentItemLedger> employeeMap = new LinkedHashMap<>();

		while (rs.next()) {
			int employeeId = rs.getInt("EMPLOYEE_ID");
			PaymentItemLedger employee = employeeMap.get(employeeId);
			if (employee == null) {
				employee = new PaymentItemLedger();
				employee.setItemType(itemType);
				employee.setItemId(rs.getLong("ITEM_ID"));
				employee.setItemName(rs.getString("ITEM_NAME"));
				employee.setEmployeeId(employeeId);
				employee.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
				employee.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				employee.setDepartment(rs.getString("DEPARTMENT"));
				employee.setPosition(rs.getString("POSITION"));
				employeeMap.put(employeeId, employee);
			}

			int paySequence = rs.getInt("PAY_SEQUENCE");
			Integer paySequenceValue = rs.wasNull() ? null : paySequence;
			long amount = rs.getLong("AMOUNT");

			PaymentItemLedger detail = new PaymentItemLedger(
					rs.getString("PAY_YEAR_MONTH"),
					rs.getInt("PAY_YEAR"),
					rs.getInt("PAY_MONTH"),
					paySequenceValue,
					amount);
			employee.getDetails().add(detail);
			employee.setTotalAmount(employee.getTotalAmount() + amount);
		}

		return new ArrayList<>(employeeMap.values());
	}

	private boolean isSupportedItemType(String itemType) {
		return PaymentItemLedger.TYPE_PAY.equals(itemType)
				|| PaymentItemLedger.TYPE_DEDUCTION.equals(itemType);
	}

	private String toPayYearMonth(int year, int month) {
		return String.format("%04d%02d", year, month);
	}
}
