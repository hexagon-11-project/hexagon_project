package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.model.DeductionItem;
import config.payitemset.dao.DeductionItemDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

/**
 * 공제항목을 수정한다.
 * 控除項目を更新する。
 */
public class DeductionItemSetUpdateService {

	private DeductionItemDao deductionItemDao = new DeductionItemDao();

	/**
	 * 공제항목 한 건을 갱신한다.
	 * 控除項目1件を更新する。
	 */
	public void update(DeductionItem item) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			if (deductionItemDao.selectById(conn, item.getDeductionItemId()) == null) {
				throw new PayItemSetException("対象の控除項目が見つかりません。");
			}

			if (deductionItemDao.existsByName(conn, item.getCompanyId(), item.getDeductionItemName(),
					item.getDeductionItemId())) {
				throw new PayItemSetException("同じ名前の控除項目がすでに登録されています。");
			}

			int updated = deductionItemDao.update(conn, item);
			if (updated == 0) {
				throw new PayItemSetException("対象の控除項目が見つかりません。");
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
