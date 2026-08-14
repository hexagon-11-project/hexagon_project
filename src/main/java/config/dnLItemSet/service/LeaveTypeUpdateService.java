package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.dnLItemSet.dao.LeaveTypeDao;
import config.model.LeaveType;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class LeaveTypeUpdateService {

	private LeaveTypeDao leaveTypeDao = new LeaveTypeDao();

	public void update(LeaveType item) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			leaveTypeDao.update(conn, item);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
