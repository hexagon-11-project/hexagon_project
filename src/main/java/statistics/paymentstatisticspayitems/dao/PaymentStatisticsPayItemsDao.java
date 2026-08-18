package statistics.paymentstatisticspayitems.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
	 * employeeName / department / status가 있으면 해당 조건으로 필터한다.
	 */
	public List<Employee> selectEmployeeList(Connection conn, int companyId, String employeeName,
			String department, String status) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT EMPLOYEE_ID, EMPLOYEE_NO, EMPLOYMENT_TYPE, EMPLOYEE_NAME, ");
			sql.append("DEPARTMENT, POSITION, RETIREMENT_YN ");
			sql.append("FROM EMPLOYEE ");
			sql.append("WHERE COMPANY_ID = ? ");
			if (hasText(employeeName)) {
				sql.append("AND EMPLOYEE_NAME LIKE ? ");
			}
			if (hasText(department)) {
				sql.append("AND TRIM(DEPARTMENT) = ? ");
			}
			if (hasText(status)) {
				sql.append("AND CASE WHEN NVL(RETIREMENT_YN, 'N') = 'Y' THEN '퇴직' ELSE '재직' END = ? ");
			}
			sql.append("ORDER BY EMPLOYEE_NAME, EMPLOYEE_NO");

			pstmt = conn.prepareStatement(sql.toString());
			int index = 1;
			pstmt.setInt(index++, companyId);
			if (hasText(employeeName)) {
				pstmt.setString(index++, "%" + employeeName.trim() + "%");
			}
			if (hasText(department)) {
				pstmt.setString(index++, department.trim());
			}
			if (hasText(status)) {
				pstmt.setString(index++, status.trim());
			}
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

	/** 사원등록 화면과 동일한 기본 부서. 사원이 없는 부서도 필터에 보이게 한다. */
	private static final String[] DEFAULT_DEPARTMENTS = {
			"사장실", "개발팀", "업무지원팀", "디자인팀", "관리팀", "기획전략팀", "콘텐츠팀"
	};

	/** 사원 선택 팝업의 부서 필터 목록. 기본 부서 + EMPLOYEE에 있는 부서. */
	public List<String> selectDepartmentList(Connection conn) throws SQLException {
		Set<String> result = new LinkedHashSet<String>();
		for (String dept : DEFAULT_DEPARTMENTS) {
			result.add(dept);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT TRIM(DEPARTMENT) AS DEPARTMENT FROM EMPLOYEE "
					+ "WHERE DEPARTMENT IS NOT NULL "
					+ "ORDER BY TRIM(DEPARTMENT)";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String dept = rs.getString("DEPARTMENT");
				if (dept != null && !dept.isEmpty()) {
					result.add(dept);
				}
			}
			return new ArrayList<String>(result);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/** 사원 선택 팝업의 상태 필터 목록. EMPLOYEE 재직여부를 재직/퇴직으로 가져온다. */
	public List<String> selectStatusList(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT DISTINCT CASE WHEN NVL(RETIREMENT_YN, 'N') = 'Y' THEN '퇴직' ELSE '재직' END AS EMP_STATUS "
					+ "FROM EMPLOYEE "
					+ "ORDER BY EMP_STATUS";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			List<String> result = new ArrayList<>();
			while (rs.next()) {
				result.add(rs.getString("EMP_STATUS"));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	private boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
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

	/** 사용 중인 지급항목 마스터를 표시 순서대로 조회한다. */
	public List<SalaryItemStatistics> selectPayItemColumns(Connection conn, int companyId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT PAY_ITEM_ID AS ITEM_ID, PAY_ITEM_NAME AS ITEM_NAME "
					+ "FROM PAY_ITEM "
					+ "WHERE COMPANY_ID = ? AND USE_YN = 'Y' "
					+ "ORDER BY DISPLAY_ORDER, PAY_ITEM_ID";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<SalaryItemStatistics> result = new ArrayList<>();
			while (rs.next()) {
				SalaryItemStatistics item = new SalaryItemStatistics();
				item.setItemId(rs.getLong("ITEM_ID"));
				item.setItemName(rs.getString("ITEM_NAME"));
				item.setAmount(0L);
				item.setCompositionRatio(0D);
				result.add(item);
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/** 사용 중인 공제항목 마스터를 표시 순서대로 조회한다. */
	public List<SalaryItemStatistics> selectDeductionItemColumns(Connection conn, int companyId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT DEDUCTION_ITEM_ID AS ITEM_ID, DEDUCTION_ITEM_NAME AS ITEM_NAME "
					+ "FROM DEDUCTION_ITEM "
					+ "WHERE COMPANY_ID = ? AND USE_YN = 'Y' "
					+ "ORDER BY DISPLAY_ORDER, DEDUCTION_ITEM_ID";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<SalaryItemStatistics> result = new ArrayList<>();
			while (rs.next()) {
				SalaryItemStatistics item = new SalaryItemStatistics();
				item.setItemId(rs.getLong("ITEM_ID"));
				item.setItemName(rs.getString("ITEM_NAME"));
				item.setAmount(0L);
				item.setCompositionRatio(0D);
				result.add(item);
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
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
