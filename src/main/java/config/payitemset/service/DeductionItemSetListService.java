package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.model.DeductionItem;
import config.payitemset.dao.DeductionItemDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class DeductionItemSetListService {

	private DeductionItemDao deductionItemDao = new DeductionItemDao();

	public List<DeductionItem> getList(int companyId) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			return deductionItemDao.selectByCompanyId(conn, companyId);

		} catch (SQLException e) {

			throw new RuntimeException(e);

		} finally {

			JdbcUtil.close(conn);

		}

	}

}
