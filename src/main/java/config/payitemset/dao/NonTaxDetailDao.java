package config.payitemset.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import config.model.NonTaxDetail;

/**
 * 비과세 항목 마스터(NON_TAX_DETAIL) 조회를 담당한다.
 * 非課税項目マスタ(NON_TAX_DETAIL)の照会を担当する。
 */
public class NonTaxDetailDao {

	/**
	 * 비과세 마스터 목록을 조회한다.
	 * 非課税マスタ一覧を照会する。
	 */
	public List<NonTaxDetail> selectByCompanyId(Connection conn, int companyId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			// 회사 조건 없이 전 건을 읽는다.
			// 会社条件なしで全件を読む。
			// WHERE COMPANY_ID = ? 가 없는 이유다.
			// WHERE COMPANY_ID = ? がない理由である。
			// ORDER BY는 PK 오름차순이다.
			// ORDER BYはPK昇順である。
			pstmt = conn.prepareStatement("SELECT NON_TAX_ID, COMPANY_ID, LEGAL_PROVISION, LEGAL_CODE, "
					+ "NON_TAX_NOTE, NON_TAX_CATEGORY, LIMIT_AMOUNT, STATEMENT_PAYMENT " + "FROM NON_TAX_DETAIL "
					+ "ORDER BY NON_TAX_ID");
			// companyId는 여기서 쓰지 않는다.
			// companyIdはここでは使わない。
			rs = pstmt.executeQuery();

			List<NonTaxDetail> result = new ArrayList<>();

			while (rs.next()) {
				// 한 행을 모델로 옮긴다.
				// 1行をモデルへ移す。

				result.add(mapRow(rs));

			}

			return result;

		} finally {

			// ResultSet·Statement만 닫는다.
			// ResultSet・Statementだけを閉じる。
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

		}

	}

	// ResultSet 한 행을 NonTaxDetail로 옮긴다.
	// ResultSetの1行をNonTaxDetailへ移す。
	private NonTaxDetail mapRow(ResultSet rs) throws SQLException {

		NonTaxDetail item = new NonTaxDetail();

		// PK는 NOT NULL이다.
		// PKはNOT NULLである。
		item.setNonTaxId(rs.getInt("NON_TAX_ID"));

		int companyId = rs.getInt("COMPANY_ID");

		// 회사 ID NULL과 0을 구분한다.
		// 会社IDのNULLと0を区別する。
		if (!rs.wasNull()) {

			item.setCompanyId(companyId);

		}

		// 문자열은 getString이 DB NULL을 Java null로 준다.
		// 文字列はgetStringがDB NULLをJava nullで返す。
		item.setLegalProvision(rs.getString("LEGAL_PROVISION"));
		item.setLegalCode(rs.getString("LEGAL_CODE"));
		item.setNonTaxNote(rs.getString("NON_TAX_NOTE"));
		item.setNonTaxCategory(rs.getString("NON_TAX_CATEGORY"));

		long limitAmount = rs.getLong("LIMIT_AMOUNT");

		// 한도 금액 NULL과 0원을 구분한다.
		// 限度額のNULLと0円を区別する。
		if (!rs.wasNull()) {

			item.setLimitAmount(limitAmount);

		}

		item.setStatementPayment(rs.getString("STATEMENT_PAYMENT"));

		return item;

	}

}
