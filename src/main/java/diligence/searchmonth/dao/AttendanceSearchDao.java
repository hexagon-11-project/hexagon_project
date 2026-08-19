package diligence.searchmonth.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.model.AttendanceRecord;
import jdbc.JdbcUtil;

public class AttendanceSearchDao {

	// 정렬 파라미터는 사용자 입력값을 그대로 SQL에 이어붙이면 안 되니, 화이트리스트로만 매핑해서 사용
	public static final String SORT_NAME = "성명순";
	public static final String SORT_DEPARTMENT = "부서순";
	public static final String SORT_DATE = "일자순";

	public List<AttendanceRecord> selectByMonth(Connection conn, int companyId, Date monthStart, Date monthEnd,
			Integer attendanceTypeId, String sortKey) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ar.ATTENDANCE_ID, ar.EMPLOYEE_ID, ar.ATTENDANCE_TYPE_ID, ar.START_DATE, ar.END_DATE, ");
			sql.append("ar.DAY_COUNT, ar.HOUR_COUNT, ar.ALLOWANCE_AMOUNT, ar.DESCRIPTION, ");
			sql.append("e.EMPLOYEE_NAME, e.DEPARTMENT, ");
			sql.append("at.ATTENDANCE_NAME, at.UNIT_CODE ");
			sql.append("FROM ATTENDANCE_RECORD ar ");
			sql.append("JOIN EMPLOYEE e ON e.EMPLOYEE_ID = ar.EMPLOYEE_ID ");
			sql.append("JOIN ATTENDANCE_TYPE at ON at.ATTENDANCE_TYPE_ID = ar.ATTENDANCE_TYPE_ID ");
			sql.append("WHERE e.COMPANY_ID = ? AND ar.START_DATE <= ? AND ar.END_DATE >= ? ");
			if (attendanceTypeId != null) {
				sql.append("AND ar.ATTENDANCE_TYPE_ID = ? ");
			}
			sql.append("ORDER BY ").append(resolveOrderBy(sortKey));

			pstmt = conn.prepareStatement(sql.toString());

			int idx = 1;
			pstmt.setInt(idx++, companyId);
			pstmt.setDate(idx++, monthEnd);
			pstmt.setDate(idx++, monthStart);
			if (attendanceTypeId != null) {
				pstmt.setInt(idx++, attendanceTypeId);
			}

			rs = pstmt.executeQuery();

			List<AttendanceRecord> result = new ArrayList<>();

			while (rs.next()) {

				AttendanceRecord item = new AttendanceRecord();

				item.setAttendanceId(rs.getInt("ATTENDANCE_ID"));
				item.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
				item.setAttendanceTypeId(rs.getInt("ATTENDANCE_TYPE_ID"));
				item.setStartDate(rs.getDate("START_DATE"));
				item.setEndDate(rs.getDate("END_DATE"));
				item.setDayCount(rs.getBigDecimal("DAY_COUNT"));
				item.setHourCount(rs.getBigDecimal("HOUR_COUNT"));
				item.setAllowanceAmount(rs.getBigDecimal("ALLOWANCE_AMOUNT"));
				item.setDescription(rs.getString("DESCRIPTION"));
				item.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				item.setDepartment(rs.getString("DEPARTMENT"));
				item.setAttendanceName(rs.getString("ATTENDANCE_NAME"));
				item.setUnitCode(rs.getString("UNIT_CODE"));

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
		if (SORT_DATE.equals(sortKey)) {
			return "ar.START_DATE, e.EMPLOYEE_NAME";
		}
		return "e.EMPLOYEE_NAME, ar.START_DATE"; // 기본값: 성명순
	}
}
