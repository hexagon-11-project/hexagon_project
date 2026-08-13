package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.model.DeductionItem;
import config.payitemset.dao.DeductionItemDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class DeductionItemSetSelectService {

	private DeductionItemDao deductionItemDao = new DeductionItemDao();

	public DeductionItem getById(int deductionItemId) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			return deductionItemDao.selectById(conn, deductionItemId);

		} catch (SQLException e) {

			throw new RuntimeException(e);

		} finally {

			JdbcUtil.close(conn);

		}

	}

}
