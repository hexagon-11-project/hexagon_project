package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.dnLItemSet.dao.LeaveTypeDao;
import config.model.LeaveType;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class LeaveTypeSelectService {

	private LeaveTypeDao leaveTypeDao = new LeaveTypeDao();

	public LeaveType getById(int leaveTypeId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return leaveTypeDao.selectById(conn, leaveTypeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
