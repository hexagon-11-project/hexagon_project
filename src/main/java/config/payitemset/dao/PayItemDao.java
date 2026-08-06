package config.payitemset.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import config.payitemset.model.PayItemModel;

public class PayItemDao {

	public List<PayItemModel> selectByCompanyId(Connection conn, int companyId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("SELECT p.PAY_ITEM_ID, p.COMPANY_ID, p.PAY_ITEM_NAME, p.TAXABLE_YN, "
					+ "p.CALCULATION_METHOD, p.TRUNCATION_UNIT, p.ATTENDANCE_PAY_RULE, "
					+ "p.BULK_PAY_AMOUNT, p.USE_YN, p.DISPLAY_ORDER, p.REG_ID, p.MOD_ID, "
					+ "p.CREATED_AT, p.UPDATED_AT, p.NON_TAX_ID, p.NON_PAY_AMOUT, " + "n.NON_TAX_CATEGORY "
					+ "FROM PAY_ITEM p " + "LEFT JOIN NON_TAX_DETAIL n ON p.NON_TAX_ID = n.NON_TAX_ID "
					+ "WHERE p.COMPANY_ID = ? " + "ORDER BY p.DISPLAY_ORDER, p.PAY_ITEM_ID");
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<PayItemModel> result = new ArrayList<>();

			while (rs.next()) {

				result.add(mapRow(rs));

			}

			return result;

		} finally {

			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

		}

	}

	private PayItemModel mapRow(ResultSet rs) throws SQLException {

		PayItemModel item = new PayItemModel();

		item.setPayItemId(rs.getInt("PAY_ITEM_ID"));
		item.setCompanyId(rs.getInt("COMPANY_ID"));
		item.setPayItemName(rs.getString("PAY_ITEM_NAME"));
		item.setTaxableYn(rs.getString("TAXABLE_YN"));
		item.setCalculationMethod(rs.getString("CALCULATION_METHOD"));
		item.setTruncationUnit(rs.getInt("TRUNCATION_UNIT"));
		item.setAttendancePayRule(rs.getString("ATTENDANCE_PAY_RULE"));

		long bulkPayAmount = rs.getLong("BULK_PAY_AMOUNT");

		if (!rs.wasNull()) {

			item.setBulkPayAmount(bulkPayAmount);

		}

		item.setUseYn(rs.getString("USE_YN"));
		item.setDisplayOrder(rs.getInt("DISPLAY_ORDER"));
		item.setRegId(rs.getString("REG_ID"));
		item.setModId(rs.getString("MOD_ID"));
		item.setCreatedAt(rs.getTimestamp("CREATED_AT"));
		item.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

		int nonTaxId = rs.getInt("NON_TAX_ID");

		if (!rs.wasNull()) {

			item.setNonTaxId(nonTaxId);

		}

		long nonPayAmount = rs.getLong("NON_PAY_AMOUT");

		if (!rs.wasNull()) {

			item.setNonPayAmount(nonPayAmount);

		}

		item.setNonTaxCategory(rs.getString("NON_TAX_CATEGORY"));

		return item;

	}

	public PayItemModel selectById(Connection conn, int payItemId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("SELECT p.PAY_ITEM_ID, p.COMPANY_ID, p.PAY_ITEM_NAME, p.TAXABLE_YN, "
					+ "p.CALCULATION_METHOD, p.TRUNCATION_UNIT, p.ATTENDANCE_PAY_RULE, "
					+ "p.BULK_PAY_AMOUNT, p.USE_YN, p.DISPLAY_ORDER, p.REG_ID, p.MOD_ID, "
					+ "p.CREATED_AT, p.UPDATED_AT, p.NON_TAX_ID, p.NON_PAY_AMOUT, " + "n.NON_TAX_CATEGORY "
					+ "FROM PAY_ITEM p " + "LEFT JOIN NON_TAX_DETAIL n ON p.NON_TAX_ID = n.NON_TAX_ID "
					+ "WHERE p.PAY_ITEM_ID = ?");
			pstmt.setInt(1, payItemId);
			rs = pstmt.executeQuery();

			if (rs.next()) {

				return mapRow(rs);

			}

			return null;

		} finally {

			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

		}

	}

	public void insert(Connection conn, PayItemModel item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("INSERT INTO PAY_ITEM ("
					+ "PAY_ITEM_ID, COMPANY_ID, PAY_ITEM_NAME, TAXABLE_YN, CALCULATION_METHOD, "
					+ "TRUNCATION_UNIT, ATTENDANCE_PAY_RULE, BULK_PAY_AMOUNT, USE_YN, "
					+ "DISPLAY_ORDER, REG_ID, MOD_ID, CREATED_AT, UPDATED_AT, " + "NON_TAX_ID, NON_PAY_AMOUT"
					+ ") VALUES (" + "PAY_ITEM_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, SYSDATE, ?, ?"
					+ ")");

			pstmt.setInt(1, item.getCompanyId());
			pstmt.setString(2, item.getPayItemName());
			pstmt.setString(3, item.getTaxableYn());
			pstmt.setString(4, item.getCalculationMethod());
			pstmt.setInt(5, item.getTruncationUnit() == null ? 0 : item.getTruncationUnit());

			if (item.getAttendancePayRule() == null) {
				pstmt.setNull(6, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(6, item.getAttendancePayRule());
			}

			if (item.getBulkPayAmount() == null) {
				pstmt.setNull(7, java.sql.Types.BIGINT);
			} else {
				pstmt.setLong(7, item.getBulkPayAmount());
			}

			pstmt.setString(8, item.getUseYn());
			pstmt.setInt(9, item.getDisplayOrder() == null ? 0 : item.getDisplayOrder());
			pstmt.setString(10, item.getRegId());
			pstmt.setString(11, item.getModId());

			if (item.getNonTaxId() == null) {
				pstmt.setNull(12, java.sql.Types.INTEGER);
			} else {
				pstmt.setInt(12, item.getNonTaxId());
			}

			if (item.getNonPayAmount() == null) {
				pstmt.setNull(13, java.sql.Types.BIGINT);
			} else {
				pstmt.setLong(13, item.getNonPayAmount());
			}

			pstmt.executeUpdate();

		} finally {

			JdbcUtil.close(pstmt);

		}

	}

	public void update(Connection conn, PayItemModel item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement(
					"UPDATE PAY_ITEM SET " + "PAY_ITEM_NAME = ?, TAXABLE_YN = ?, CALCULATION_METHOD = ?, "
							+ "TRUNCATION_UNIT = ?, ATTENDANCE_PAY_RULE = ?, USE_YN = ?, NON_TAX_ID = ?, "
							+ "NON_PAY_AMOUT = ?, MOD_ID = ?, UPDATED_AT = CURRENT_TIMESTAMP "
							+ "WHERE PAY_ITEM_ID = ? AND COMPANY_ID = ?");
			pstmt.setString(1, item.getPayItemName());
			pstmt.setString(2, item.getTaxableYn());

			if (item.getCalculationMethod() == null) {
				pstmt.setNull(3, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(3, item.getCalculationMethod());
			}

			pstmt.setInt(4, item.getTruncationUnit() == null ? 0 : item.getTruncationUnit());

			if (item.getAttendancePayRule() == null) {

				pstmt.setNull(5, java.sql.Types.VARCHAR);

			} else {

				pstmt.setString(5, item.getAttendancePayRule());

			}

			pstmt.setString(6, item.getUseYn());

			if (item.getNonTaxId() == null) {

				pstmt.setNull(7, java.sql.Types.INTEGER);

			} else {

				pstmt.setInt(7, item.getNonTaxId());

			}

			if (item.getNonPayAmount() == null) {

				pstmt.setNull(8, java.sql.Types.BIGINT);

			} else {

				pstmt.setLong(8, item.getNonPayAmount());

			}

			pstmt.setString(9, item.getModId());
			pstmt.setInt(10, item.getPayItemId());
			pstmt.setInt(11, item.getCompanyId());
			pstmt.executeUpdate();

		} finally {

			JdbcUtil.close(pstmt);

		}

	}

	public void delete(Connection conn, int payItemId) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("DELETE FROM PAY_ITEM WHERE PAY_ITEM_ID = ?");
			pstmt.setInt(1, payItemId);
			pstmt.executeUpdate();

		} finally {

			JdbcUtil.close(pstmt);

		}

	}

}
