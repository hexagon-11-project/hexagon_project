package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.payitemset.dao.PayItemDao;
import config.model.PayItem;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class PayItemSetUpdateService {

	private PayItemDao payItemDao = new PayItemDao();

	public void update(PayItem item) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			payItemDao.update(conn, item);

			conn.commit();

		} catch (SQLException e) {

			JdbcUtil.rollback(conn);

			throw new RuntimeException(e);

		} finally {

			JdbcUtil.close(conn);

		}

	}

}
