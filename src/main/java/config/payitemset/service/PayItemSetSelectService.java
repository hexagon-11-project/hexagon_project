package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.payitemset.dao.PayItemDao;
import config.payitemset.model.PayItemModel;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class PayItemSetSelectService {

	private PayItemDao payItemDao = new PayItemDao();

	public PayItemModel getById(int payItemId) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();

			return payItemDao.selectById(conn, payItemId);

		} catch (SQLException e) {

			throw new RuntimeException(e);

		} finally {

			JdbcUtil.close(conn);

		}

	}

}
