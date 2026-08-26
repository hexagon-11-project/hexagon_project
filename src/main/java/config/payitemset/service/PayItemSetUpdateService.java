package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.payitemset.dao.PayItemDao;
import config.model.PayItem;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

/**
 * 지급항목을 수정한다.
 * 支給項目を更新する。
 */
public class PayItemSetUpdateService {

	private PayItemDao payItemDao = new PayItemDao();

	/**
	 * 지급항목 한 건을 갱신한다.
	 * 支給項目1件を更新する。
	 */
	public void update(PayItem item) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			if (payItemDao.selectById(conn, item.getPayItemId()) == null) {
				throw new PayItemSetException("対象の支給項目が見つかりません。");
			}

			if (payItemDao.existsByName(conn, item.getCompanyId(), item.getPayItemName(), item.getPayItemId())) {
				throw new PayItemSetException("同じ名前の支給項目がすでに登録されています。");
			}

			int updated = payItemDao.update(conn, item);
			if (updated == 0) {
				throw new PayItemSetException("対象の支給項目が見つかりません。");
			}

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
