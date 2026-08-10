package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.model.NonTaxDetail;
import config.payitemset.dao.NonTaxDetailDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class NonTaxDetailListService {

	private NonTaxDetailDao nonTaxDetailDao = new NonTaxDetailDao();

	public List<NonTaxDetail> getList(int companyId) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();

			return nonTaxDetailDao.selectByCompanyId(conn, companyId);

		} catch (SQLException e) {

			throw new RuntimeException(e);

		} finally {

			JdbcUtil.close(conn);

		}

	}

}
