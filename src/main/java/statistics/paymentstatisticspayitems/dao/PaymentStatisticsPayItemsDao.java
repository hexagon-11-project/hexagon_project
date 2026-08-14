package statistics.paymentstatisticspayitems.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.Employee;
import jdbc.JdbcUtil;
import statistics.model.EmployeeSalaryStatistics;
import statistics.model.SalaryItemStatistics;

/**
 * 사원별 급여 항목 통계 Dao.
 * 연도, 월, 사원이름으로 해당 월 지급내역·공제항목을 집계한다.
 *
 * PAY_YEAR_MONTH 컬럼이 CHAR(6) 이므로 연월은 'YYYYMM' 문자열로 조회한다.
 * 같은 월에 급여차수가 여러 건이면 항목별 금액을 합산한다.
 */
public class PaymentStatisticsPayItemsDao {

	/**
	 * 사원 선택 팝업용 목록을 조회한다.
	 * 사원 구분, 사원번호, 이름, 부서, 직위, 재직상태를 반환한다.
	 */
	public List<Employee> selectEmployeeList(Connection conn, int companyId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT EMPLOYEE_ID, EMPLOYEE_NO, EMPLOYMENT_TYPE, EMPLOYEE_NAME, "
					+ "DEPARTMENT, POSITION, RETIREMENT_YN "
					+ "FROM EMPLOYEE "
					+ "WHERE COMPANY_ID = ? "
					+ "ORDER BY EMPLOYEE_NAME, EMPLOYEE_NO";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<Employee> result = new ArrayList<>();
			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
				emp.setEmployeeNo(rs.getString("EMPLOYEE_NO"));
				emp.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
				emp.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				emp.setDepartment(rs.getString("DEPARTMENT"));
				emp.setPosition(rs.getString("POSITION"));
				emp.setRetirementYn(rs.getString("RETIREMENT_YN"));
				result.add(emp);
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * 연도, 월, 사원이름으로 해당 사원의 월 급여 항목 통계를 조회한다.
	 * 해당 월 급여 데이터가 없으면 null을 반환한다.
	 */
	public EmployeeSalaryStatistics selectByYearMonthAndName(Connection conn, int companyId, int year, int month,
			String employeeName) throws SQLException {
		if (employeeName == null || employeeName.trim().isEmpty()) {
			return null;
		}

		String payYearMonth = toPayYearMonth(year, month);
		String name = employeeName.trim();

		EmployeeSalaryStatistics result = selectHeader(conn, companyId, payYearMonth, name);
		if (result == null) {
			return null;
		}

		result.setYear(year);
		result.setMonth(month);
		result.setPayItems(selectPayItems(conn, companyId, payYearMonth, result.getEmployeeId()));
		result.setDeductionItems(selectDeductionItems(conn, companyId, payYearMonth, result.getEmployeeId()));
		fillRatios(result);
		return result;
	}

	/** 사원 식별 정보와 지급/공제/실지급 합계를 조회한다. 동명이인이면 EMPLOYEE_ID 오름차순 1명만 사용한다. */
	private EmployeeSalaryStatistics selectHeader(Connection conn, int companyId, String payYearMonth,
			String employeeName) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT * FROM ( "
					+ "  SELECT e.EMPLOYEE_ID, e.EMPLOYEE_NAME, "
					+ "         NVL(SUM(pe.TOTAL_PAY_AMOUNT), 0) AS TOTAL_PAY_AMOUNT, "
					+ "         NVL(SUM(pe.TOTAL_DEDUCTION_AMOUNT), 0) AS TOTAL_DEDUCTION_AMOUNT, "
					+ "         NVL(SUM(pe.NET_PAY_AMOUNT), 0) AS NET_PAY_AMOUNT "
					+ "  FROM PAYROLL p "
					+ "  JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
					+ "  JOIN EMPLOYEE e ON pe.EMPLOYEE_ID = e.EMPLOYEE_ID "
					+ "  WHERE p.COMPANY_ID = ? "
					+ "    AND p.PAY_YEAR_MONTH = ? "
					+ "    AND e.EMPLOYEE_NAME = ? "
					+ "  GROUP BY e.EMPLOYEE_ID, e.EMPLOYEE_NAME "
					+ "  ORDER BY e.EMPLOYEE_ID "
					+ ") WHERE ROWNUM = 1";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, companyId);
			pstmt.setString(2, payYearMonth);
			pstmt.setString(3, employeeName);
			rs = pstmt.executeQuery();

			if (!rs.next()) {
				return null;
			}

			EmployeeSalaryStatistics row = new EmployeeSalaryStatistics();
			row.setEmployeeId(rs.getString("EMPLOYEE_ID"));
			row.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
			row.setTotalPayAmount(rs.getLong("TOTAL_PAY_AMOUNT"));
			row.setTotalDeductionAmount(rs.getLong("TOTAL_DEDUCTION_AMOUNT"));
			row.setNetPayAmount(rs.getLong("NET_PAY_AMOUNT"));
			return row;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/** 해당 월 지급항목을 항목별로 합산한다. */
	private List<SalaryItemStatistics> selectPayItems(Connection conn, int companyId, String payYearMonth,
			String employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT i.PAY_ITEM_ID AS ITEM_ID, i.PAY_ITEM_NAME AS ITEM_NAME, "
					+ "       NVL(SUM(d.AMOUNT), 0) AS AMOUNT "
					+ "FROM PAYROLL p "
					+ "JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
					+ "JOIN PAYROLL_PAY_DETAIL d ON d.PAYROLL_EMPLOYEE_ID = pe.PAYROLL_EMPLOYEE_ID "
					+ "JOIN PAY_ITEM i ON d.PAY_ITEM_ID = i.PAY_ITEM_ID "
					+ "WHERE p.COMPANY_ID = ? "
					+ "  AND p.PAY_YEAR_MONTH = ? "
					+ "  AND pe.EMPLOYEE_ID = ? "
					+ "GROUP BY i.PAY_ITEM_ID, i.PAY_ITEM_NAME, i.DISPLAY_ORDER "
					+ "ORDER BY i.DISPLAY_ORDER, i.PAY_ITEM_ID";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, companyId);
			pstmt.setString(2, payYearMonth);
			pstmt.setString(3, employeeId);
			rs = pstmt.executeQuery();

			List<SalaryItemStatistics> result = new ArrayList<>();
			while (rs.next()) {
				result.add(mapItem(rs));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/** 해당 월 공제항목을 항목별로 합산한다. */
	private List<SalaryItemStatistics> selectDeductionItems(Connection conn, int companyId, String payYearMonth,
			String employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT di.DEDUCTION_ITEM_ID AS ITEM_ID, di.DEDUCTION_ITEM_NAME AS ITEM_NAME, "
					+ "       NVL(SUM(dd.AMOUNT), 0) AS AMOUNT "
					+ "FROM PAYROLL p "
					+ "JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
					+ "JOIN PAYROLL_DEDUCTION_DETAIL dd ON dd.PAYROLL_EMPLOYEE_ID = pe.PAYROLL_EMPLOYEE_ID "
					+ "JOIN DEDUCTION_ITEM di ON dd.DEDUCTION_ITEM_ID = di.DEDUCTION_ITEM_ID "
					+ "WHERE p.COMPANY_ID = ? "
					+ "  AND p.PAY_YEAR_MONTH = ? "
					+ "  AND pe.EMPLOYEE_ID = ? "
					+ "GROUP BY di.DEDUCTION_ITEM_ID, di.DEDUCTION_ITEM_NAME, di.DISPLAY_ORDER "
					+ "ORDER BY di.DISPLAY_ORDER, di.DEDUCTION_ITEM_ID";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, companyId);
			pstmt.setString(2, payYearMonth);
			pstmt.setString(3, employeeId);
			rs = pstmt.executeQuery();

			List<SalaryItemStatistics> result = new ArrayList<>();
			while (rs.next()) {
				result.add(mapItem(rs));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	private SalaryItemStatistics mapItem(ResultSet rs) throws SQLException {
		SalaryItemStatistics item = new SalaryItemStatistics();
		item.setItemId(rs.getLong("ITEM_ID"));
		item.setItemName(rs.getString("ITEM_NAME"));
		item.setAmount(rs.getLong("AMOUNT"));
		return item;
	}

	/** 지급/공제 합계 비율과 항목별 구성비율을 채운다. */
	private void fillRatios(EmployeeSalaryStatistics result) {
		long totalPay = result.getTotalPayAmount();
		long totalDeduction = result.getTotalDeductionAmount();
		long grandTotal = totalPay + totalDeduction;

		result.setPaymentRatio(calcRatio(totalPay, grandTotal));
		result.setDeductionRatio(calcRatio(totalDeduction, grandTotal));

		for (SalaryItemStatistics item : result.getPayItems()) {
			item.setCompositionRatio(calcRatio(item.getAmount(), totalPay));
		}
		for (SalaryItemStatistics item : result.getDeductionItems()) {
			item.setCompositionRatio(calcRatio(item.getAmount(), totalDeduction));
		}
	}

	/** 구성비율(%). 분모가 0이면 null. */
	private Double calcRatio(long part, long total) {
		if (total == 0L) {
			return null;
		}
		return (part * 100D) / total;
	}

	private String toPayYearMonth(int year, int month) {
		return String.format("%04d%02d", year, month);
	}
}
