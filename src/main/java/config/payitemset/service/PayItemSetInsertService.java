package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.payitemset.dao.PayItemDao;
import config.model.PayItem;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

/**
 * 지급항목을 신규 등록한다.
 * 支給項目を新規登録する。
 */
public class PayItemSetInsertService {

	private PayItemDao payItemDao = new PayItemDao();

	/**
	 * 지급항목 한 건을 저장한다.
	 * 支給項目1件を保存する。
	 */
	public void insert(PayItem item) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			if (payItemDao.existsByName(conn, item.getCompanyId(), item.getPayItemName(), null)) {
				throw new PayItemSetException("同じ名前の支給項目がすでに登録されています。");
			}

			payItemDao.insert(conn, item);
			conn.commit();

		} catch (PayItemSetException e) {

			JdbcUtil.rollback(conn);
			throw e;

		} catch (SQLException e) {

			JdbcUtil.rollback(conn);
			throw PayItemSetException.fromSql(e);

		} finally {

			JdbcUtil.close(conn);

		}

	}

}
