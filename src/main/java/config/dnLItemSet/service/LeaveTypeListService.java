package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.dnLItemSet.dao.LeaveTypeDao;
import config.model.LeaveType;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class LeaveTypeListService {

	private LeaveTypeDao leaveTypeDao = new LeaveTypeDao();

	public List<LeaveType> getList(int companyId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return leaveTypeDao.selectByCompanyId(conn, companyId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
