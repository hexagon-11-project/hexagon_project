package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.dnLItemSet.dao.AttendanceTypeDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class AttendanceTypeDeleteService {

	private AttendanceTypeDao attendanceTypeDao = new AttendanceTypeDao();

	public void delete(int attendanceTypeId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			attendanceTypeDao.delete(conn, attendanceTypeId);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
