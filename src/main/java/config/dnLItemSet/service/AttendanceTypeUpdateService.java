package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.dnLItemSet.dao.AttendanceTypeDao;
import config.model.AttendanceType;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class AttendanceTypeUpdateService {

	private AttendanceTypeDao attendanceTypeDao = new AttendanceTypeDao();

	public void update(AttendanceType item) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			attendanceTypeDao.update(conn, item);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
