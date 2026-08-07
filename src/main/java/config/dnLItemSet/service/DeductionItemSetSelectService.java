package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.dnLItemSet.dao.DeductionItemDao;
import config.dnLItemSet.model.DeductionItem;
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
