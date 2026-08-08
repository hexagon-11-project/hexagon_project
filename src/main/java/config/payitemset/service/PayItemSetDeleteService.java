package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.payitemset.dao.PayItemDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class PayItemSetDeleteService {

	private PayItemDao payItemDao = new PayItemDao();

	public void delete(int payItemId) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			payItemDao.delete(conn, payItemId);

			conn.commit();

		} catch (SQLException e) {

			JdbcUtil.rollback(conn);

			throw new RuntimeException(e);

		} finally {

			JdbcUtil.close(conn);

		}
	}

}
