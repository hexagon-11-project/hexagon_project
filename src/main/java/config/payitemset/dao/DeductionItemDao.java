package config.payitemset.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import config.model.DeductionItem;

public class DeductionItemDao {

	public List<DeductionItem> selectByCompanyId(Connection conn, int companyId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn
					.prepareStatement("SELECT DEDUCTION_ITEM_ID, COMPANY_ID, DEDUCTION_ITEM_NAME, CALCULATION_METHOD, "
							+ "TRUNCATION_UNIT, REMARK, USE_YN, DISPLAY_ORDER, REG_ID, MOD_ID, "
							+ "CREATED_AT, UPDATED_AT " + "FROM DEDUCTION_ITEM " + "WHERE COMPANY_ID = ? "
							+ "ORDER BY DISPLAY_ORDER, DEDUCTION_ITEM_ID");
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<DeductionItem> result = new ArrayList<>();

			while (rs.next()) {

				result.add(mapRow(rs));

			}

			return result;

		} finally {

			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

		}

	}

	private DeductionItem mapRow(ResultSet rs) throws SQLException {

		DeductionItem item = new DeductionItem();

		item.setDeductionItemId(rs.getInt("DEDUCTION_ITEM_ID"));
		item.setCompanyId(rs.getInt("COMPANY_ID"));
		item.setDeductionItemName(rs.getString("DEDUCTION_ITEM_NAME"));
		item.setCalculationMethod(rs.getString("CALCULATION_METHOD"));
		item.setTruncationUnit(rs.getInt("TRUNCATION_UNIT"));
		item.setRemark(rs.getString("REMARK"));
		item.setUseYn(rs.getString("USE_YN"));
		item.setDisplayOrder(rs.getInt("DISPLAY_ORDER"));
		item.setRegId(rs.getString("REG_ID"));
		item.setModId(rs.getString("MOD_ID"));
		item.setCreatedAt(rs.getTimestamp("CREATED_AT"));
		item.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

		return item;

	}

}
