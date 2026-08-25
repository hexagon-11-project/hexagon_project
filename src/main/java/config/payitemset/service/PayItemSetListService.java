package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.payitemset.dao.PayItemDao;
import config.model.PayItem;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

/**
 * 회사별 지급항목 목록을 조회한다.
 * 会社別支給項目一覧を照会する。
 */
public class PayItemSetListService {

	private PayItemDao payItemDao = new PayItemDao();

	/**
	 * 해당 회사의 지급항목 목록을 반환한다.
	 * 当該会社の支給項目一覧を返す。
	 */
	public List<PayItem> getList(int companyId) {

		Connection conn = null;

		try {

			// 커넥션을 연다.
			// コネクションを開く。
			conn = ConnectionProvider.getConnection();

			// 회사별 목록을 읽는다.
			// 会社別一覧を読む。
			return payItemDao.selectByCompanyId(conn, companyId);

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
