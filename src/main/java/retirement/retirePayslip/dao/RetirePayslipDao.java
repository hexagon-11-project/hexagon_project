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

	// 리턴 타입 없이 파라미터로 객체를 받아서 값을 채워줌
	public void selectRetirementStatement(Connection conn, String employeeId, RetirementMntModel statement,
			CompanyInfo company) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
// 세금 제외하고 DTO에 있는 항목만 조회
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

// ✅ 파라미터를 세팅할 때 공백을 제거하고 명확하게 정수형(Int)으로 변환해서 던짐
			pstmt.setInt(1, Integer.parseInt(employeeId.trim()));

			rs = pstmt.executeQuery();

			if (rs.next()) {
// 1. 기존 RetirementMntModel에 데이터 세팅
				statement.setEmployeeName(rs.getString("employeeName"));
				statement.setHireDate(rs.getString("hireDate"));
				statement.setResignDate(rs.getString("resignDate"));
				statement.setServiceDays(rs.getInt("serviceDays"));
				statement.setAverageDailyWage(rs.getDouble("averageDailyWage"));
				statement.setRetirementPayAmount(rs.getLong("retirementPayAmount"));

// 2. 기존 CompanyInfo에 회사 데이터 세팅
				company.setCompanyName(rs.getString("companyName"));
				company.setSealPath(rs.getString("sealPath"));
			}

		} catch (Exception e) {
			System.out.println("명세서 조회 중 오류 발생: " + e.getMessage());
			throw new SQLException(e);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 2. 상단 콤보박스용 정산 완료(Y) 사원 목록 조회
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