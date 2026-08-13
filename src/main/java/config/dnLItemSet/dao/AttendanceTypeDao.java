package config.dnLItemSet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.model.AttendanceType;
import jdbc.JdbcUtil;

public class AttendanceTypeDao {

	// 사용중(Y)인 것만 - 다른 화면(급여항목설정 드롭다운 등)에서 그대로 쓰고 있어서 필터 유지
	public List<AttendanceType> selectByCompanyId(Connection conn, int companyId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement(baseSelectSql() + "WHERE a.COMPANY_ID = ? AND a.USE_YN = 'Y' "
					+ "ORDER BY a.ATTENDANCE_TYPE_ID");
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<AttendanceType> result = new ArrayList<>();

			while (rs.next()) {
				result.add(mapRow(rs));
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 휴가/근태설정 관리화면 목록용 - 사용여부 상관없이 전부 조회
	public List<AttendanceType> selectAllByCompanyId(Connection conn, int companyId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement(baseSelectSql() + "WHERE a.COMPANY_ID = ? " + "ORDER BY a.ATTENDANCE_TYPE_ID");
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<AttendanceType> result = new ArrayList<>();

			while (rs.next()) {
				result.add(mapRow(rs));
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	public AttendanceType selectById(Connection conn, int attendanceTypeId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement(baseSelectSql() + "WHERE a.ATTENDANCE_TYPE_ID = ?");
			pstmt.setInt(1, attendanceTypeId);
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

	public void insert(Connection conn, AttendanceType item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("INSERT INTO ATTENDANCE_TYPE ("
					+ "ATTENDANCE_TYPE_ID, COMPANY_ID, ATTENDANCE_CODE, ATTENDANCE_NAME, UNIT_CODE, "
					+ "ATTENDANCE_GROUP_CODE, LEAVE_TYPE_ID, WORK_TIME_LINK_CODE, USE_YN, "
					+ "REG_ID, MOD_ID, CREATED_AT, UPDATED_AT" + ") VALUES ("
					+ "ATTENDANCE_TYPE_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, SYSDATE" + ")");

			pstmt.setInt(1, item.getCompanyId());
			pstmt.setString(2, item.getAttendanceCode());
			pstmt.setString(3, item.getAttendanceName());
			pstmt.setString(4, item.getUnitCode());
			pstmt.setString(5, item.getAttendanceGroupCode());

			if (item.getLeaveTypeId() == null) {
				pstmt.setNull(6, java.sql.Types.INTEGER);
			} else {
				pstmt.setInt(6, item.getLeaveTypeId());
			}

			pstmt.setString(7, item.getWorkTimeLinkCode());
			pstmt.setString(8, item.getUseYn());
			pstmt.setString(9, "SYSTEM");
			pstmt.setString(10, "SYSTEM");

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	public void update(Connection conn, AttendanceType item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("UPDATE ATTENDANCE_TYPE SET "
					+ "ATTENDANCE_NAME = ?, UNIT_CODE = ?, ATTENDANCE_GROUP_CODE = ?, LEAVE_TYPE_ID = ?, "
					+ "WORK_TIME_LINK_CODE = ?, USE_YN = ?, MOD_ID = ?, UPDATED_AT = SYSDATE "
					+ "WHERE ATTENDANCE_TYPE_ID = ? AND COMPANY_ID = ?");

			pstmt.setString(1, item.getAttendanceName());
			pstmt.setString(2, item.getUnitCode());
			pstmt.setString(3, item.getAttendanceGroupCode());

			if (item.getLeaveTypeId() == null) {
				pstmt.setNull(4, java.sql.Types.INTEGER);
			} else {
				pstmt.setInt(4, item.getLeaveTypeId());
			}

			pstmt.setString(5, item.getWorkTimeLinkCode());
			pstmt.setString(6, item.getUseYn());
			pstmt.setString(7, "SYSTEM");
			pstmt.setInt(8, item.getAttendanceTypeId());
			pstmt.setInt(9, item.getCompanyId());

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	public void delete(Connection conn, int attendanceTypeId) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("DELETE FROM ATTENDANCE_TYPE WHERE ATTENDANCE_TYPE_ID = ?");
			pstmt.setInt(1, attendanceTypeId);
			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	private String baseSelectSql() {
		return "SELECT a.ATTENDANCE_TYPE_ID, a.COMPANY_ID, a.ATTENDANCE_CODE, a.ATTENDANCE_NAME, a.UNIT_CODE, "
				+ "a.ATTENDANCE_GROUP_CODE, a.LEAVE_TYPE_ID, a.WORK_TIME_LINK_CODE, a.USE_YN, l.LEAVE_NAME "
				+ "FROM ATTENDANCE_TYPE a " + "LEFT JOIN LEAVE_TYPE l ON a.LEAVE_TYPE_ID = l.LEAVE_TYPE_ID ";
	}

	private AttendanceType mapRow(ResultSet rs) throws SQLException {

		AttendanceType item = new AttendanceType();

		item.setAttendanceTypeId(rs.getInt("ATTENDANCE_TYPE_ID"));
		item.setCompanyId(rs.getInt("COMPANY_ID"));
		item.setAttendanceCode(rs.getString("ATTENDANCE_CODE"));
		item.setAttendanceName(rs.getString("ATTENDANCE_NAME"));
		item.setUnitCode(rs.getString("UNIT_CODE"));
		item.setAttendanceGroupCode(rs.getString("ATTENDANCE_GROUP_CODE"));

		int leaveTypeId = rs.getInt("LEAVE_TYPE_ID");
		if (!rs.wasNull()) {
			item.setLeaveTypeId(leaveTypeId);
		}

		item.setWorkTimeLinkCode(rs.getString("WORK_TIME_LINK_CODE"));
		item.setLeaveTypeName(rs.getString("LEAVE_NAME"));
		item.setUseYn(rs.getString("USE_YN"));

		return item;
	}
}
