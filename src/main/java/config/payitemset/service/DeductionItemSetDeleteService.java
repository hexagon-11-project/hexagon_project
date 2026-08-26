package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.payitemset.dao.DeductionItemDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

/**
 * 공제항목을 삭제한다.
 * 控除項目を削除する。
 */
public class DeductionItemSetDeleteService {

	private DeductionItemDao deductionItemDao = new DeductionItemDao();

	/**
	 * 공제항목 PK로 한 건을 삭제한다.
	 * 控除項目PKで1件を削除する。
	 */
	public void delete(int deductionItemId) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			if (deductionItemDao.selectById(conn, deductionItemId) == null) {
				throw new PayItemSetException("対象の控除項目が見つかりません。");
			}

			if (deductionItemDao.existsPayrollUsage(conn, deductionItemId)) {
				throw new PayItemSetException("給与明細で使用されている控除項目は削除できません。");
			}

			int deleted = deductionItemDao.delete(conn, deductionItemId);
			if (deleted == 0) {
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
