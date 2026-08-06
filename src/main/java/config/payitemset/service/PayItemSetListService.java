package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.payitemset.dao.PayItemDao;
import config.payitemset.model.PayItemModel;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class PayItemSetListService {

	private PayItemDao payItemDao = new PayItemDao();

	public List<PayItemModel> getList(int companyId) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();

			return payItemDao.selectByCompanyId(conn, companyId);

		} catch (SQLException e) {

			throw new RuntimeException(e);

		} finally {

			JdbcUtil.close(conn);

		}

	}

}
