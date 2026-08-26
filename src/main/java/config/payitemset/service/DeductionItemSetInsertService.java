package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.model.DeductionItem;
import config.payitemset.dao.DeductionItemDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

/**
 * 공제항목을 신규 등록한다.
 * 控除項目を新規登録する。
 */
public class DeductionItemSetInsertService {

	private DeductionItemDao deductionItemDao = new DeductionItemDao();

	/**
	 * 공제항목 한 건을 저장한다.
	 * 控除項目1件を保存する。
	 */
	public void insert(DeductionItem item) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			if (deductionItemDao.existsByName(conn, item.getCompanyId(), item.getDeductionItemName(), null)) {
				throw new PayItemSetException("同じ名前の控除項目がすでに登録されています。");
			}

			deductionItemDao.insert(conn, item);
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
