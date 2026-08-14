package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.dnLItemSet.dao.LeaveTypeDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class LeaveTypeDeleteService {

	private LeaveTypeDao leaveTypeDao = new LeaveTypeDao();

	public void delete(int leaveTypeId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			leaveTypeDao.delete(conn, leaveTypeId);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
