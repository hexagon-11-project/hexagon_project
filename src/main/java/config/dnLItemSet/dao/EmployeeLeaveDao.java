package config.dnLItemSet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.model.EmployeeLeave;
import jdbc.JdbcUtil;

public class EmployeeLeaveDao {

	// 특정 휴가항목 기준으로 회사 전체 사원 + 그 사원의 부과일수(없으면 0)를 같이 조회
	public List<EmployeeLeave> selectByLeaveTypeId(Connection conn, int leaveTypeId, int companyId)
			throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("SELECT e.EMPLOYEE_ID, e.EMPLOYMENT_TYPE, e.EMPLOYEE_NO, "
					+ "e.EMPLOYEE_NAME, e.DEPARTMENT, e.POSITION, e.HIRE_DATE, e.RESIGN_DATE, "
					+ "el.EMPLOYEE_LEAVE_ID, el.GRANTED_DAYS " + "FROM EMPLOYEE e "
					+ "LEFT JOIN EMPLOYEE_LEAVE el ON el.EMPLOYEE_ID = e.EMPLOYEE_ID AND el.LEAVE_TYPE_ID = ? "
					+ "WHERE e.COMPANY_ID = ? " + "ORDER BY e.EMPLOYEE_ID");
			pstmt.setInt(1, leaveTypeId);
			pstmt.setInt(2, companyId);
			rs = pstmt.executeQuery();

			List<EmployeeLeave> result = new ArrayList<>();

			while (rs.next()) {

				EmployeeLeave item = new EmployeeLeave();

				item.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
				item.setLeaveTypeId(leaveTypeId);
				item.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
				item.setEmployeeNo(rs.getString("EMPLOYEE_NO"));
				item.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				item.setDepartment(rs.getString("DEPARTMENT"));
				item.setPosition(rs.getString("POSITION"));
				item.setHireDate(rs.getDate("HIRE_DATE"));
				java.sql.Date resignDate = rs.getDate("RESIGN_DATE");
				item.setEmploymentStatus(resignDate == null ? "재직" : "퇴직");

				int employeeLeaveId = rs.getInt("EMPLOYEE_LEAVE_ID");
				if (!rs.wasNull()) {
					item.setEmployeeLeaveId(employeeLeaveId);
				}
				item.setGrantedDays(rs.getBigDecimal("GRANTED_DAYS"));

				result.add(item);
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	public Integer selectEmployeeLeaveId(Connection conn, int employeeId, int leaveTypeId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement(
					"SELECT EMPLOYEE_LEAVE_ID FROM EMPLOYEE_LEAVE WHERE EMPLOYEE_ID = ? AND LEAVE_TYPE_ID = ?");
			pstmt.setInt(1, employeeId);
			pstmt.setInt(2, leaveTypeId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("EMPLOYEE_LEAVE_ID");
			}

			return null;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	public void insert(Connection conn, int employeeId, int leaveTypeId, java.math.BigDecimal grantedDays)
			throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("INSERT INTO EMPLOYEE_LEAVE ("
					+ "EMPLOYEE_LEAVE_ID, EMPLOYEE_ID, LEAVE_TYPE_ID, GRANTED_DAYS, "
					+ "REG_ID, MOD_ID, CREATED_AT, UPDATED_AT" + ") VALUES ("
					+ "EMP_LEAVE_SEQ.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE, SYSDATE" + ")");

			pstmt.setInt(1, employeeId);
			pstmt.setInt(2, leaveTypeId);
			pstmt.setBigDecimal(3, grantedDays);
			pstmt.setString(4, "SYSTEM");
			pstmt.setString(5, "SYSTEM");

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	public void update(Connection conn, int employeeLeaveId, java.math.BigDecimal grantedDays) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement(
					"UPDATE EMPLOYEE_LEAVE SET GRANTED_DAYS = ?, MOD_ID = ?, UPDATED_AT = SYSDATE "
							+ "WHERE EMPLOYEE_LEAVE_ID = ?");

			pstmt.setBigDecimal(1, grantedDays);
			pstmt.setString(2, "SYSTEM");
			pstmt.setInt(3, employeeLeaveId);

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	public void deleteByEmployeeAndLeaveType(Connection conn, int employeeId, int leaveTypeId) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_LEAVE WHERE EMPLOYEE_ID = ? AND LEAVE_TYPE_ID = ?");
			pstmt.setInt(1, employeeId);
			pstmt.setInt(2, leaveTypeId);
			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}
