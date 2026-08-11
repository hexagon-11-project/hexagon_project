package config.payitemset.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import config.model.DeductionItem;
import jdbc.JdbcUtil;

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

	public DeductionItem selectById(Connection conn, int deductionItemId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn
					.prepareStatement("SELECT DEDUCTION_ITEM_ID, COMPANY_ID, DEDUCTION_ITEM_NAME, CALCULATION_METHOD, "
							+ "TRUNCATION_UNIT, REMARK, USE_YN, DISPLAY_ORDER, REG_ID, MOD_ID, "
							+ "CREATED_AT, UPDATED_AT " + "FROM DEDUCTION_ITEM " + "WHERE DEDUCTION_ITEM_ID = ?");
			pstmt.setInt(1, deductionItemId);
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

	public void insert(Connection conn, DeductionItem item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("INSERT INTO DEDUCTION_ITEM ("
					+ "DEDUCTION_ITEM_ID, COMPANY_ID, DEDUCTION_ITEM_NAME, CALCULATION_METHOD, "
					+ "TRUNCATION_UNIT, REMARK, USE_YN, DISPLAY_ORDER, REG_ID, MOD_ID, CREATED_AT, UPDATED_AT"
					+ ") VALUES (" + "DEDUCTION_ITEM_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, SYSDATE)");
			pstmt.setInt(1, item.getCompanyId());
			pstmt.setString(2, item.getDeductionItemName());
			pstmt.setString(3, item.getCalculationMethod());
			pstmt.setInt(4, item.getTruncationUnit() == null ? 0 : item.getTruncationUnit());

			if (item.getRemark() == null)
				pstmt.setNull(5, Types.VARCHAR);
			else
				pstmt.setString(5, item.getRemark());

			pstmt.setString(6, item.getUseYn());
			pstmt.setInt(7, item.getDisplayOrder() == null ? 0 : item.getDisplayOrder());
			pstmt.setString(8, item.getRegId());
			pstmt.setString(9, item.getModId());
			pstmt.executeUpdate();

		} finally {

			JdbcUtil.close(pstmt);

		}

	}

	public void update(Connection conn, DeductionItem item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement(
					"UPDATE DEDUCTION_ITEM SET DEDUCTION_ITEM_NAME = ?, CALCULATION_METHOD = ?, TRUNCATION_UNIT = ?, "
							+ "REMARK = ?, USE_YN = ?, MOD_ID = ?, UPDATED_AT = SYSDATE "
							+ "WHERE DEDUCTION_ITEM_ID = ? AND COMPANY_ID = ?");
			pstmt.setString(1, item.getDeductionItemName());
			pstmt.setString(2, item.getCalculationMethod());
			pstmt.setInt(3, item.getTruncationUnit() == null ? 0 : item.getTruncationUnit());

			if (item.getRemark() == null)
				pstmt.setNull(4, Types.VARCHAR);
			else
				pstmt.setString(4, item.getRemark());

			pstmt.setString(5, item.getUseYn());
			pstmt.setString(6, item.getModId());
			pstmt.setInt(7, item.getDeductionItemId());
			pstmt.setInt(8, item.getCompanyId());
			pstmt.executeUpdate();

		} finally {

			JdbcUtil.close(pstmt);

		}

	}

	public void delete(Connection conn, int deductionItemId) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("DELETE FROM DEDUCTION_ITEM WHERE DEDUCTION_ITEM_ID = ?");
			pstmt.setInt(1, deductionItemId);
			pstmt.executeUpdate();

		} finally {

			JdbcUtil.close(pstmt);

		}

	}

}
