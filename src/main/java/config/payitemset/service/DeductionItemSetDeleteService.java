package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.payitemset.dao.DeductionItemDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class DeductionItemSetDeleteService {

	private DeductionItemDao deductionItemDao = new DeductionItemDao();

	public void delete(int deductionItemId) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			deductionItemDao.delete(conn, deductionItemId);

			conn.commit();

		} catch (SQLException e) {

			JdbcUtil.rollback(conn);

			throw new RuntimeException(e);

		} finally {

			JdbcUtil.close(conn);

		}

	}

}
