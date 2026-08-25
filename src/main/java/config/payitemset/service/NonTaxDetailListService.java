package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.model.NonTaxDetail;
import config.payitemset.dao.NonTaxDetailDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

/**
 * 비과세 마스터 목록을 조회한다.
 * 非課税マスタ一覧を照会する。
 */
public class NonTaxDetailListService {

	private NonTaxDetailDao nonTaxDetailDao = new NonTaxDetailDao();

	/**
	 * 비과세 항목 목록을 반환한다.
	 * 非課税項目一覧を返す。
	 */
	public List<NonTaxDetail> getList(int companyId) {

		Connection conn = null;

		try {

			// 커넥션을 연다.
			// コネクションを開く。
			conn = ConnectionProvider.getConnection();

			// 마스터 목록을 읽는다.
			// マスタ一覧を読む。
			return nonTaxDetailDao.selectByCompanyId(conn, companyId);

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
