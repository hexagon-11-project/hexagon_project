package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.model.DeductionItem;
import config.payitemset.dao.DeductionItemDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class DeductionItemSetInsertService {

	private DeductionItemDao deductionItemDao = new DeductionItemDao();

	public void insert(DeductionItem item) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			deductionItemDao.insert(conn, item);

			conn.commit();

		} catch (SQLException e) {

			JdbcUtil.rollback(conn);

			throw new RuntimeException(e);

		} finally {

			JdbcUtil.close(conn);

		}

	}

}
