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

			// 커넥션을 연다.
			// コネクションを開く。
			conn = ConnectionProvider.getConnection();
			// 자동 커밋을 끈다.
			// 自動コミットを切る。
			conn.setAutoCommit(false);

			payItemDao.insert(conn, item);

			// 여기까지 오면 DB에 확정한다.
			// ここまで来ればDBへ確定する。
			conn.commit();

		} catch (SQLException e) {

			// 실패하면 롤백한다.
			// 失敗すればロールバックする。
			JdbcUtil.rollback(conn);

			throw new RuntimeException(e);

		} finally {

			// 커넥션을 반드시 닫는다.
			// コネクションは必ず閉じる。
			JdbcUtil.close(conn);

		}

	}

}
