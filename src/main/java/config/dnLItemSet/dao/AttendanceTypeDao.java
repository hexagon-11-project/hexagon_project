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

	public List<AttendanceType> selectByCompanyId(Connection conn, int companyId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement(
					"SELECT ATTENDANCE_TYPE_ID, COMPANY_ID, ATTENDANCE_CODE, ATTENDANCE_NAME, USE_YN, DISPLAY_ORDER "
							+ "FROM ATTENDANCE_TYPE " + "WHERE COMPANY_ID = ? AND USE_YN = 'Y' "
							+ "ORDER BY DISPLAY_ORDER, ATTENDANCE_TYPE_ID");
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<AttendanceType> result = new ArrayList<>();

			while (rs.next()) {

				AttendanceType item = new AttendanceType();
				item.setAttendanceTypeId(rs.getInt("ATTENDANCE_TYPE_ID"));
				item.setCompanyId(rs.getInt("COMPANY_ID"));
				item.setAttendanceCode(rs.getString("ATTENDANCE_CODE"));
				item.setAttendanceName(rs.getString("ATTENDANCE_NAME"));
				item.setUseYn(rs.getString("USE_YN"));
				item.setDisplayOrder(rs.getInt("DISPLAY_ORDER"));
				result.add(item);

			}

			return result;

		} finally {

			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

		}

	}

}
