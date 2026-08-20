package diligence.holidayssearchresult.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.model.AttendanceRecord;
import config.model.EmployeeLeaveStatus;
import jdbc.JdbcUtil;

public class LeaveSearchDao {

	// 정렬 파라미터는 화이트리스트로만 SQL에 반영 (SQL 인젝션 방지)
	public static final String SORT_NAME = "성명순";
	public static final String SORT_DEPARTMENT = "부서순";
	public static final String SORT_REMAINING = "잔여일수순";

	// [휴가 현황] 표 - 선택된 휴가항목을 부여받은 사원 전원의 전체/사용(선택 연도)/잔여 일수
	public List<EmployeeLeaveStatus> selectStatusByLeaveType(Connection conn, int companyId, int leaveTypeId,
			int year, String sortKey) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// EMPLOYEE_LEAVE(부여 기록)가 아니라 EMPLOYEE에서 시작 - 부여를 안 받았어도
			// 실제 사용 기록(ATTENDANCE_RECORD)이 있으면 목록에 나와야 하기 때문
			String sql = "SELECT e.EMPLOYEE_ID, e.EMPLOYMENT_TYPE, e.EMPLOYEE_NAME, e.DEPARTMENT, e.POSITION, "
					+ "lt.LEAVE_NAME, NVL(el.GRANTED_DAYS, 0) AS GRANTED_DAYS, "
					+ "NVL((SELECT SUM(ar.DAY_COUNT) FROM ATTENDANCE_RECORD ar "
					+ "JOIN ATTENDANCE_TYPE at ON at.ATTENDANCE_TYPE_ID = ar.ATTENDANCE_TYPE_ID "
					+ "WHERE ar.EMPLOYEE_ID = e.EMPLOYEE_ID AND at.LEAVE_TYPE_ID = lt.LEAVE_TYPE_ID "
					+ "AND EXTRACT(YEAR FROM ar.START_DATE) = ?), 0) AS USED_DAYS, "
					+ "NVL(el.GRANTED_DAYS, 0) - NVL((SELECT SUM(ar.DAY_COUNT) FROM ATTENDANCE_RECORD ar "
					+ "JOIN ATTENDANCE_TYPE at ON at.ATTENDANCE_TYPE_ID = ar.ATTENDANCE_TYPE_ID "
					+ "WHERE ar.EMPLOYEE_ID = e.EMPLOYEE_ID AND at.LEAVE_TYPE_ID = lt.LEAVE_TYPE_ID "
					+ "AND EXTRACT(YEAR FROM ar.START_DATE) = ?), 0) AS REMAINING_DAYS "
					+ "FROM EMPLOYEE e "
					+ "JOIN LEAVE_TYPE lt ON lt.LEAVE_TYPE_ID = ? "
					+ "LEFT JOIN EMPLOYEE_LEAVE el ON el.EMPLOYEE_ID = e.EMPLOYEE_ID AND el.LEAVE_TYPE_ID = lt.LEAVE_TYPE_ID "
					+ "WHERE e.COMPANY_ID = ? "
					+ "AND (el.EMPLOYEE_LEAVE_ID IS NOT NULL OR EXISTS ("
					+ "SELECT 1 FROM ATTENDANCE_RECORD ar2 "
					+ "JOIN ATTENDANCE_TYPE at2 ON at2.ATTENDANCE_TYPE_ID = ar2.ATTENDANCE_TYPE_ID "
					+ "WHERE ar2.EMPLOYEE_ID = e.EMPLOYEE_ID AND at2.LEAVE_TYPE_ID = lt.LEAVE_TYPE_ID "
					+ "AND EXTRACT(YEAR FROM ar2.START_DATE) = ?)) "
					+ "ORDER BY " + resolveOrderBy(sortKey);

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, year);
			pstmt.setInt(2, year);
			pstmt.setInt(3, leaveTypeId);
			pstmt.setInt(4, companyId);
			pstmt.setInt(5, year);

			rs = pstmt.executeQuery();

			List<EmployeeLeaveStatus> result = new ArrayList<>();

			while (rs.next()) {

				EmployeeLeaveStatus item = new EmployeeLeaveStatus();

				item.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
				item.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
				item.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				item.setDepartment(rs.getString("DEPARTMENT"));
				item.setPosition(rs.getString("POSITION"));
				item.setLeaveName(rs.getString("LEAVE_NAME"));
				item.setTotalDays(rs.getBigDecimal("GRANTED_DAYS"));
				item.setUsedDays(rs.getBigDecimal("USED_DAYS"));

				result.add(item);
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// [선택 사원 휴가 사용내역] 표 - 특정 사원이 그 휴가항목으로, 선택 연도에 실제 사용한 근태기록 목록
	public List<AttendanceRecord> selectUsageDetail(Connection conn, int employeeId, int leaveTypeId, int year)
			throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement("SELECT ar.START_DATE, ar.DAY_COUNT, ar.DESCRIPTION, at.ATTENDANCE_NAME "
					+ "FROM ATTENDANCE_RECORD ar "
					+ "JOIN ATTENDANCE_TYPE at ON at.ATTENDANCE_TYPE_ID = ar.ATTENDANCE_TYPE_ID "
					+ "WHERE ar.EMPLOYEE_ID = ? AND at.LEAVE_TYPE_ID = ? AND EXTRACT(YEAR FROM ar.START_DATE) = ? "
					+ "ORDER BY ar.START_DATE DESC, ar.ATTENDANCE_ID DESC");
			pstmt.setInt(1, employeeId);
			pstmt.setInt(2, leaveTypeId);
			pstmt.setInt(3, year);

			rs = pstmt.executeQuery();

			List<AttendanceRecord> result = new ArrayList<>();

			while (rs.next()) {

				AttendanceRecord item = new AttendanceRecord();

				item.setStartDate(rs.getDate("START_DATE"));
				item.setDayCount(rs.getBigDecimal("DAY_COUNT"));
				item.setDescription(rs.getString("DESCRIPTION"));
				item.setAttendanceName(rs.getString("ATTENDANCE_NAME"));

				result.add(item);
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	private String resolveOrderBy(String sortKey) {
		if (SORT_DEPARTMENT.equals(sortKey)) {
			return "e.DEPARTMENT, e.EMPLOYEE_NAME";
		}
		if (SORT_REMAINING.equals(sortKey)) {
			return "REMAINING_DAYS DESC";
		}
		return "e.EMPLOYEE_NAME"; // 기본값: 성명순
	}
}
