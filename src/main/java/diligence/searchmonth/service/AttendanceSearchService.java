package diligence.searchmonth.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import config.model.AttendanceRecord;
import connection.ConnectionProvider;
import diligence.searchmonth.dao.AttendanceSearchDao;
import jdbc.JdbcUtil;

public class AttendanceSearchService {

	private AttendanceSearchDao attendanceSearchDao = new AttendanceSearchDao();

	public List<AttendanceRecord> getListByMonth(int companyId, Date monthStart, Date monthEnd,
			Integer attendanceTypeId, String sortKey) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return attendanceSearchDao.selectByMonth(conn, companyId, monthStart, monthEnd, attendanceTypeId, sortKey);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
