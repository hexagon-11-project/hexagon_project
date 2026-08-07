package config.payitemset.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import config.model.NonTaxDetail;

public class NonTaxDetailDao {

	public List<NonTaxDetail> selectByCompanyId(Connection conn, int companyId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("SELECT NON_TAX_ID, COMPANY_ID, LEGAL_PROVISION, LEGAL_CODE, "
					+ "NON_TAX_NOTE, NON_TAX_CATEGORY, LIMIT_AMOUNT, STATEMENT_PAYMENT " + "FROM NON_TAX_DETAIL "
					+ "ORDER BY NON_TAX_ID");
			rs = pstmt.executeQuery();

			List<NonTaxDetail> result = new ArrayList<>();

			while (rs.next()) {

				result.add(mapRow(rs));

			}

			return result;

		} finally {

			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

		}

	}

	private NonTaxDetail mapRow(ResultSet rs) throws SQLException {

		NonTaxDetail item = new NonTaxDetail();

		item.setNonTaxId(rs.getInt("NON_TAX_ID"));

		int companyId = rs.getInt("COMPANY_ID");

		if (!rs.wasNull()) {

			item.setCompanyId(companyId);

		}

		item.setLegalProvision(rs.getString("LEGAL_PROVISION"));
		item.setLegalCode(rs.getString("LEGAL_CODE"));
		item.setNonTaxNote(rs.getString("NON_TAX_NOTE"));
		item.setNonTaxCategory(rs.getString("NON_TAX_CATEGORY"));

		long limitAmount = rs.getLong("LIMIT_AMOUNT");

		if (!rs.wasNull()) {

			item.setLimitAmount(limitAmount);

		}

		item.setStatementPayment(rs.getString("STATEMENT_PAYMENT"));

		return item;

	}

}
