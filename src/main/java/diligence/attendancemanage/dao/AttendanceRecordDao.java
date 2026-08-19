package diligence.attendancemanage.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.model.AttendanceRecord;
import config.model.EmployeeLeave;
import jdbc.JdbcUtil;

public class AttendanceRecordDao {

	// 왼쪽 목록에 뿌릴 사원 목록 (재직/퇴직 상관없이 회사 전체)
	public List<EmployeeLeave> selectEmployeesByCompanyId(Connection conn, int companyId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("SELECT EMPLOYEE_ID, EMPLOYMENT_TYPE, EMPLOYEE_NO, EMPLOYEE_NAME, "
					+ "DEPARTMENT, POSITION, HIRE_DATE, RESIGN_DATE " + "FROM EMPLOYEE "
					+ "WHERE COMPANY_ID = ? " + "ORDER BY EMPLOYEE_ID");
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<EmployeeLeave> result = new ArrayList<>();

			while (rs.next()) {
				// 사원 기본정보만 필요해서, 이미 만들어둔 EmployeeLeave 모델을 재사용 (사원정보 필드가 그대로 있음)
				EmployeeLeave row = new EmployeeLeave();
				row.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
				row.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
				row.setEmployeeNo(rs.getString("EMPLOYEE_NO"));
				row.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				row.setDepartment(rs.getString("DEPARTMENT"));
				row.setPosition(rs.getString("POSITION"));
				row.setHireDate(rs.getDate("HIRE_DATE"));
				row.setEmploymentStatus(rs.getDate("RESIGN_DATE") == null ? "재직" : "퇴직");
				result.add(row);
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	public void insert(Connection conn, AttendanceRecord item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("INSERT INTO ATTENDANCE_RECORD ("
					+ "ATTENDANCE_ID, EMPLOYEE_ID, ATTENDANCE_TYPE_ID, START_DATE, END_DATE, "
					+ "START_TIME, END_TIME, DAY_COUNT, HOUR_COUNT, ALLOWANCE_AMOUNT, DESCRIPTION, "
					+ "REG_ID, MOD_ID, CREATED_AT, UPDATED_AT" + ") VALUES ("
					+ "ATTENDANCE_RECORD_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, SYSDATE" + ")");

			pstmt.setInt(1, item.getEmployeeId());
			pstmt.setInt(2, item.getAttendanceTypeId());
			pstmt.setDate(3, item.getStartDate());
			pstmt.setDate(4, item.getEndDate());
			pstmt.setString(5, item.getStartTime());
			pstmt.setString(6, item.getEndTime());
			setBigDecimalOrNull(pstmt, 7, item.getDayCount());
			setBigDecimalOrNull(pstmt, 8, item.getHourCount());
			setBigDecimalOrNull(pstmt, 9, item.getAllowanceAmount());
			pstmt.setString(10, item.getDescription());
			pstmt.setString(11, "SYSTEM");
			pstmt.setString(12, "SYSTEM");

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	public void delete(Connection conn, int attendanceId) throws SQLException {

		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement("DELETE FROM ATTENDANCE_RECORD WHERE ATTENDANCE_ID = ?");
			pstmt.setInt(1, attendanceId);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 선택된 사원의 근태기록 이력 (최근순)
	public List<AttendanceRecord> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("SELECT r.ATTENDANCE_ID, r.EMPLOYEE_ID, r.ATTENDANCE_TYPE_ID, "
					+ "r.START_DATE, r.END_DATE, r.START_TIME, r.END_TIME, r.DAY_COUNT, r.HOUR_COUNT, r.ALLOWANCE_AMOUNT, "
					+ "r.DESCRIPTION, r.CREATED_AT, a.ATTENDANCE_NAME, a.UNIT_CODE, a.LEAVE_TYPE_ID "
					+ "FROM ATTENDANCE_RECORD r " + "JOIN ATTENDANCE_TYPE a ON r.ATTENDANCE_TYPE_ID = a.ATTENDANCE_TYPE_ID "
					+ "WHERE r.EMPLOYEE_ID = ? " + "ORDER BY r.START_DATE DESC, r.ATTENDANCE_ID DESC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();

			List<AttendanceRecord> result = new ArrayList<>();

			while (rs.next()) {
				result.add(mapRow(rs));
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	public AttendanceRecord selectById(Connection conn, int attendanceId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("SELECT r.ATTENDANCE_ID, r.EMPLOYEE_ID, r.ATTENDANCE_TYPE_ID, "
					+ "r.START_DATE, r.END_DATE, r.START_TIME, r.END_TIME, r.DAY_COUNT, r.HOUR_COUNT, r.ALLOWANCE_AMOUNT, "
					+ "r.DESCRIPTION, r.CREATED_AT, a.ATTENDANCE_NAME, a.UNIT_CODE, a.LEAVE_TYPE_ID "
					+ "FROM ATTENDANCE_RECORD r " + "JOIN ATTENDANCE_TYPE a ON r.ATTENDANCE_TYPE_ID = a.ATTENDANCE_TYPE_ID "
					+ "WHERE r.ATTENDANCE_ID = ?");
			pstmt.setInt(1, attendanceId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return mapRow(rs);
			}

			return null;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	public void update(Connection conn, AttendanceRecord item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("UPDATE ATTENDANCE_RECORD SET "
					+ "ATTENDANCE_TYPE_ID = ?, START_DATE = ?, END_DATE = ?, DAY_COUNT = ?, HOUR_COUNT = ?, ALLOWANCE_AMOUNT = ?, "
					+ "DESCRIPTION = ?, MOD_ID = ?, UPDATED_AT = SYSDATE " + "WHERE ATTENDANCE_ID = ?");

			pstmt.setInt(1, item.getAttendanceTypeId());
			pstmt.setDate(2, item.getStartDate());
			pstmt.setDate(3, item.getEndDate());
			setBigDecimalOrNull(pstmt, 4, item.getDayCount());
			setBigDecimalOrNull(pstmt, 5, item.getHourCount());
			setBigDecimalOrNull(pstmt, 6, item.getAllowanceAmount());
			pstmt.setString(7, item.getDescription());
			pstmt.setString(8, "SYSTEM");
			pstmt.setInt(9, item.getAttendanceId());

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	private void setBigDecimalOrNull(PreparedStatement pstmt, int index, BigDecimal value) throws SQLException {
		if (value == null) {
			pstmt.setNull(index, java.sql.Types.NUMERIC);
		} else {
			pstmt.setBigDecimal(index, value);
		}
	}

	private AttendanceRecord mapRow(ResultSet rs) throws SQLException {

		AttendanceRecord item = new AttendanceRecord();

		item.setAttendanceId(rs.getInt("ATTENDANCE_ID"));
		item.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
		item.setAttendanceTypeId(rs.getInt("ATTENDANCE_TYPE_ID"));
		item.setStartDate(rs.getDate("START_DATE"));
		item.setEndDate(rs.getDate("END_DATE"));
		item.setStartTime(rs.getString("START_TIME"));
		item.setEndTime(rs.getString("END_TIME"));
		item.setDayCount(rs.getBigDecimal("DAY_COUNT"));
		item.setHourCount(rs.getBigDecimal("HOUR_COUNT"));
		item.setAllowanceAmount(rs.getBigDecimal("ALLOWANCE_AMOUNT"));
		item.setDescription(rs.getString("DESCRIPTION"));
		item.setCreatedAt(rs.getDate("CREATED_AT"));
		item.setAttendanceName(rs.getString("ATTENDANCE_NAME"));
		item.setUnitCode(rs.getString("UNIT_CODE"));

		int leaveTypeId = rs.getInt("LEAVE_TYPE_ID");
		if (!rs.wasNull()) {
			item.setLeaveTypeId(leaveTypeId);
		}

		return item;
	}
}
