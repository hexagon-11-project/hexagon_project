package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.dnLItemSet.dao.DeductionItemDao;
import config.dnLItemSet.model.DeductionItem;
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
