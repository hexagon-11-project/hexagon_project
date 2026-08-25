package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.payitemset.dao.PayItemDao;
import config.model.PayItem;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

/**
 * 지급항목 한 건을 PK로 조회한다.
 * 支給項目1件をPKで照会する。
 */
public class PayItemSetSelectService {

	private PayItemDao payItemDao = new PayItemDao();

	/**
	 * 지급항목 ID로 한 건을 조회한다.
	 * 支給項目IDで1件を照会する。
	 */
	public PayItem getById(int payItemId) {

		Connection conn = null;

		try {

			// 커넥션을 연다.
			// コネクションを開く。
			conn = ConnectionProvider.getConnection();

			// PK로 한 건을 읽는다.
			// PKで1件を読む。
			return payItemDao.selectById(conn, payItemId);

		} catch (SQLException e) {

			// 조회 실패를 런타임으로 올린다.
			// 照会失敗をランタイムで上げる。
			throw new RuntimeException(e);

		} finally {

			// 커넥션을 반드시 닫는다.
			// コネクションは必ず閉じる。
			JdbcUtil.close(conn);

		}

	}

}
