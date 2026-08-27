package retirement.retirePayslip.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.Employee;
import config.model.CompanyInfo;
import jdbc.JdbcUtil;
import retirement.model.RetirementMntModel;

public class RetirePayslipDao {

	
	public void selectRetirementStatement(Connection conn, String employeeId, RetirementMntModel statement,
			CompanyInfo company) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			String sql = "SELECT " + "    E.EMPLOYEE_NAME AS employeeName, "
					+ "    TO_CHAR(R.CALC_START_DATE, 'YYYY-MM-DD') AS hireDate, "
					+ "    TO_CHAR(R.CALC_END_DATE, 'YYYY-MM-DD') AS resignDate, "
					+ "    R.SERVICE_DAYS AS serviceDays, " + "    R.AVERAGE_DAILY_WAGE AS averageDailyWage, "
					+ "    R.RETIREMENT_PAY_AMOUNT AS retirementPayAmount, " + "    C.COMPANY_NAME AS companyName, "
					+ "    C.SEAL_PATH AS sealPath " + "FROM " + "    RETIREMENT_PAY R "
					+ "INNER JOIN EMPLOYEE E ON R.EMPLOYEE_ID = E.EMPLOYEE_ID "
					+ "LEFT JOIN COMPANY_INFO C ON R.COMPANY_ID = C.COMPANY_ID " + // ✅ INNER JOIN을 LEFT JOIN으로 변경 (회사
																					// 정보가 꼬여도 사원 정보는 무조건 나오게)
					"WHERE " + "    R.EMPLOYEE_ID = ? " + "    AND R.RETIREMENT_SETTLEMENT_YN = 'Y'";

			pstmt = conn.prepareStatement(sql);


			pstmt.setInt(1, Integer.parseInt(employeeId.trim()));

			rs = pstmt.executeQuery();

			if (rs.next()) {
				statement.setEmployeeName(rs.getString("employeeName"));
				statement.setHireDate(rs.getString("hireDate"));
				statement.setResignDate(rs.getString("resignDate"));
				statement.setServiceDays(rs.getInt("serviceDays"));
				statement.setAverageDailyWage(rs.getDouble("averageDailyWage"));
				statement.setRetirementPayAmount(rs.getLong("retirementPayAmount"));

				company.setCompanyName(rs.getString("companyName"));
				company.setSealPath(rs.getString("sealPath"));
			}

		} catch (Exception e) {
			throw new SQLException(e);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	//  정산 완료(Y) 사원 목록 조회
	// 精算完了(Y)社員リストを照会
	public List<Employee> selectSettledEmployeeList(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Employee> list = new ArrayList<>();

		try {
			String sql = "SELECT DISTINCT E.EMPLOYEE_ID, E.EMPLOYEE_NAME, E.EMPLOYEE_NO " + "FROM EMPLOYEE E "
					+ "INNER JOIN RETIREMENT_PAY R ON E.EMPLOYEE_ID = R.EMPLOYEE_ID "
					+ "WHERE R.RETIREMENT_SETTLEMENT_YN = 'Y' " + "ORDER BY E.EMPLOYEE_NAME";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Employee emp = new Employee();
				emp.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
				emp.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				emp.setEmployeeNo(rs.getString("EMPLOYEE_NO"));
				list.add(emp);
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}