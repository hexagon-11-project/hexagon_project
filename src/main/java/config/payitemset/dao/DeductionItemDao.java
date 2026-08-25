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

/**
 * 공제항목(DEDUCTION_ITEM) CRUD를 담당한다.
 * 控除項目(DEDUCTION_ITEM)のCRUDを担当する。
 */
public class DeductionItemDao {

	/**
	 * 회사별 공제항목 목록을 조회한다.
	 * 会社別控除項目一覧を照会する。
	 */
	public List<DeductionItem> selectByCompanyId(Connection conn, int companyId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			// WHERE는 회사만 거른다.
			// WHEREは会社だけを絞り込む。
			// ORDER BY는 화면 순서다.
			// ORDER BYは画面順である。
			pstmt = conn
					.prepareStatement("SELECT DEDUCTION_ITEM_ID, COMPANY_ID, DEDUCTION_ITEM_NAME, CALCULATION_METHOD, "
							+ "TRUNCATION_UNIT, REMARK, USE_YN, DISPLAY_ORDER, REG_ID, MOD_ID, "
							+ "CREATED_AT, UPDATED_AT " + "FROM DEDUCTION_ITEM " + "WHERE COMPANY_ID = ? "
							+ "ORDER BY DISPLAY_ORDER, DEDUCTION_ITEM_ID");
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<DeductionItem> result = new ArrayList<>();

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

	/**
	 * 공제항목 PK로 한 건을 조회한다.
	 * 控除項目PKで1件を照会する。
	 */
	public DeductionItem selectById(Connection conn, int deductionItemId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			// WHERE는 PK만 본다.
			// WHEREはPKだけを見る。
			pstmt = conn
					.prepareStatement("SELECT DEDUCTION_ITEM_ID, COMPANY_ID, DEDUCTION_ITEM_NAME, CALCULATION_METHOD, "
							+ "TRUNCATION_UNIT, REMARK, USE_YN, DISPLAY_ORDER, REG_ID, MOD_ID, "
							+ "CREATED_AT, UPDATED_AT " + "FROM DEDUCTION_ITEM " + "WHERE DEDUCTION_ITEM_ID = ?");
			pstmt.setInt(1, deductionItemId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return mapRow(rs);
			}

			// 없으면 null이다.
			// なければnullである。
			return null;

		} finally {

			// ResultSet·Statement만 닫는다.
			// ResultSet・Statementだけを閉じる。
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

		}

	}

	// ResultSet 한 행을 DeductionItem으로 옮긴다.
	// ResultSetの1行をDeductionItemへ移す。
	private DeductionItem mapRow(ResultSet rs) throws SQLException {

		DeductionItem item = new DeductionItem();

		// PK·회사는 NOT NULL로 본다.
		// PK・会社はNOT NULLとみなす。
		item.setDeductionItemId(rs.getInt("DEDUCTION_ITEM_ID"));
		item.setCompanyId(rs.getInt("COMPANY_ID"));
		// 문자열은 getString이 DB NULL을 Java null로 준다.
		// 文字列はgetStringがDB NULLをJava nullで返す。
		item.setDeductionItemName(rs.getString("DEDUCTION_ITEM_NAME"));
		item.setCalculationMethod(rs.getString("CALCULATION_METHOD"));
		// 절사 단위는 getInt다.
		// 端数処理単位はgetIntである。
		item.setTruncationUnit(rs.getInt("TRUNCATION_UNIT"));
		item.setRemark(rs.getString("REMARK"));
		item.setUseYn(rs.getString("USE_YN"));
		item.setDisplayOrder(rs.getInt("DISPLAY_ORDER"));
		item.setRegId(rs.getString("REG_ID"));
		item.setModId(rs.getString("MOD_ID"));
		// Timestamp는 NULL을 Java null로 준다.
		// TimestampはNULLをJava nullで返す。
		item.setCreatedAt(rs.getTimestamp("CREATED_AT"));
		item.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

		return item;

	}

	/**
	 * 공제항목을 신규 등록한다.
	 * 控除項目を新規登録する。
	 */
	public void insert(Connection conn, DeductionItem item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			// 시퀀스로 PK를 채번한다.
			// シーケンスでPKを採番する。
			// CREATED_AT·UPDATED_AT는 SYSDATE다.
			// CREATED_AT・UPDATED_ATはSYSDATEである。
			pstmt = conn.prepareStatement("INSERT INTO DEDUCTION_ITEM ("
					+ "DEDUCTION_ITEM_ID, COMPANY_ID, DEDUCTION_ITEM_NAME, CALCULATION_METHOD, "
					+ "TRUNCATION_UNIT, REMARK, USE_YN, DISPLAY_ORDER, REG_ID, MOD_ID, CREATED_AT, UPDATED_AT"
					+ ") VALUES (" + "DEDUCTION_ITEM_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, SYSDATE)");
			pstmt.setInt(1, item.getCompanyId());
			pstmt.setString(2, item.getDeductionItemName());
			pstmt.setString(3, item.getCalculationMethod());
			// 절사 단위 null은 0으로 넣는다.
			// 端数処理単位のnullは0で入れる。
			pstmt.setInt(4, item.getTruncationUnit() == null ? 0 : item.getTruncationUnit());

			// 비고가 없으면 DB NULL로 넣는다.
			// 備考がなければDB NULLで入れる。
			if (item.getRemark() == null)
				pstmt.setNull(5, Types.VARCHAR);
			else
				pstmt.setString(5, item.getRemark());

			pstmt.setString(6, item.getUseYn());
			pstmt.setInt(7, item.getDisplayOrder() == null ? 0 : item.getDisplayOrder());
			pstmt.setString(8, item.getRegId());
			pstmt.setString(9, item.getModId());
			// INSERT를 실행한다.
			// INSERTを実行する。
			pstmt.executeUpdate();

		} finally {

			// Statement만 닫는다.
			// Statementだけを閉じる。
			JdbcUtil.close(pstmt);

		}

	}

	/**
	 * 공제항목을 수정한다.
	 * 控除項目を更新する。
	 */
	public void update(Connection conn, DeductionItem item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			// UPDATED_AT는 SYSDATE다.
			// UPDATED_ATはSYSDATEである。
			// WHERE는 PK와 회사를 함께 본다.
			// WHEREはPKと会社を合わせて見る。
			pstmt = conn.prepareStatement(
					"UPDATE DEDUCTION_ITEM SET DEDUCTION_ITEM_NAME = ?, CALCULATION_METHOD = ?, TRUNCATION_UNIT = ?, "
							+ "REMARK = ?, USE_YN = ?, MOD_ID = ?, UPDATED_AT = SYSDATE "
							+ "WHERE DEDUCTION_ITEM_ID = ? AND COMPANY_ID = ?");
			pstmt.setString(1, item.getDeductionItemName());
			pstmt.setString(2, item.getCalculationMethod());
			// 절사 단위 null은 0으로 넣는다.
			// 端数処理単位のnullは0で入れる。
			pstmt.setInt(3, item.getTruncationUnit() == null ? 0 : item.getTruncationUnit());

			// 비고가 없으면 DB NULL로 넣는다.
			// 備考がなければDB NULLで入れる。
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

			// Statement만 닫는다.
			// Statementだけを閉じる。
			JdbcUtil.close(pstmt);

		}

	}

	/**
	 * 공제항목을 PK로 삭제한다.
	 * 控除項目をPKで削除する。
	 */
	public void delete(Connection conn, int deductionItemId) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			// WHERE는 PK만 본다.
			// WHEREはPKだけを見る。
			pstmt = conn.prepareStatement("DELETE FROM DEDUCTION_ITEM WHERE DEDUCTION_ITEM_ID = ?");
			pstmt.setInt(1, deductionItemId);
			pstmt.executeUpdate();

		} finally {

			// Statement만 닫는다.
			// Statementだけを閉じる。
			JdbcUtil.close(pstmt);

		}

	}

}
