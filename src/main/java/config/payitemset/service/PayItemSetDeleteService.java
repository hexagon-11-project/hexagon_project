package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.payitemset.dao.PayItemDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

/**
 * 지급항목을 삭제한다.
 * 支給項目を削除する。
 */
public class PayItemSetDeleteService {

	private PayItemDao payItemDao = new PayItemDao();

	/**
	 * 지급항목 PK로 한 건을 삭제한다.
	 * 支給項目PKで1件を削除する。
	 */
	public void delete(int payItemId) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			if (payItemDao.selectById(conn, payItemId) == null) {
				throw new PayItemSetException("対象の支給項目が見つかりません。");
			}

			if (payItemDao.existsPayrollUsage(conn, payItemId)) {
				throw new PayItemSetException("給与明細で使用されている支給項目は削除できません。");
			}

			int deleted = payItemDao.delete(conn, payItemId);
			if (deleted == 0) {
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
