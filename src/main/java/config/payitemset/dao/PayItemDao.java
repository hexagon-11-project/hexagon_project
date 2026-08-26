package config.payitemset.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import config.model.PayItem;

/**
 * 지급항목(PAY_ITEM) CRUD를 담당한다.
 * 支給項目(PAY_ITEM)のCRUDを担当する。
 */
public class PayItemDao {

	/**
	 * 회사별 지급 항목 목록을 조회한다.
	 * 会社別支給項目一覧を照会する。
	 */
	public List<PayItem> selectByCompanyId(Connection conn, int companyId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			// 비과세 구분명은 JOIN으로 붙인다.
			// 非課税区分名はJOINで付ける。
			// WHERE는 회사만 거른다.
			// WHEREは会社だけを絞り込む。
			// ORDER BY는 화면 순서다.
			// ORDER BYは画面順である。
			pstmt = conn.prepareStatement("SELECT p.PAY_ITEM_ID, p.COMPANY_ID, p.PAY_ITEM_NAME, p.TAXABLE_YN, "
					+ "p.CALCULATION_METHOD, p.TRUNCATION_UNIT, p.ATTENDANCE_PAY_RULE, "
					+ "p.BULK_PAY_AMOUNT, p.USE_YN, p.DISPLAY_ORDER, p.REG_ID, p.MOD_ID, "
					+ "p.CREATED_AT, p.UPDATED_AT, p.NON_TAX_ID, p.NON_PAY_AMOUT, " + "n.NON_TAX_CATEGORY "
					+ "FROM PAY_ITEM p " + "LEFT JOIN NON_TAX_DETAIL n ON p.NON_TAX_ID = n.NON_TAX_ID "
					+ "WHERE p.COMPANY_ID = ? " + "ORDER BY p.DISPLAY_ORDER, p.PAY_ITEM_ID");
			// 회사 조건을 바인딩한다.
			// 会社条件をバインドする。
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<PayItem> result = new ArrayList<>();

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

	// ResultSet 한 행을 PayItem으로 옮긴다.
	// ResultSetの1行をPayItemへ移す。
	private PayItem mapRow(ResultSet rs) throws SQLException {

		PayItem item = new PayItem();

		// PK는 NOT NULL이다.
		// PKはNOT NULLである。
		item.setPayItemId(rs.getInt("PAY_ITEM_ID"));
		// 회사 ID도 NOT NULL이다.
		// 会社IDもNOT NULLである。
		item.setCompanyId(rs.getInt("COMPANY_ID"));
		// 문자열은 getString이 DB NULL을 Java null로 준다.
		// 文字列はgetStringがDB NULLをJava nullで返す。
		item.setPayItemName(rs.getString("PAY_ITEM_NAME"));
		item.setTaxableYn(rs.getString("TAXABLE_YN"));
		item.setCalculationMethod(rs.getString("CALCULATION_METHOD"));
		// 절사 단위는 getInt로 읽는다.
		// 端数処理単位はgetIntで読む。
		item.setTruncationUnit(rs.getInt("TRUNCATION_UNIT"));
		item.setAttendancePayRule(rs.getString("ATTENDANCE_PAY_RULE"));

		long bulkPayAmount = rs.getLong("BULK_PAY_AMOUNT");

		// 일괄지급액 NULL과 0을 구분한다.
		// 一括支給額のNULLと0を区別する。
		if (!rs.wasNull()) {

			item.setBulkPayAmount(bulkPayAmount);

		}

		item.setUseYn(rs.getString("USE_YN"));
		// 표시순서는 getInt다.
		// 表示順はgetIntである。
		item.setDisplayOrder(rs.getInt("DISPLAY_ORDER"));
		item.setRegId(rs.getString("REG_ID"));
		item.setModId(rs.getString("MOD_ID"));
		// 날짜는 Timestamp로 읽는다.
		// 日付はTimestampで読む。
		item.setCreatedAt(rs.getTimestamp("CREATED_AT"));
		item.setUpdatedAt(rs.getTimestamp("UPDATED_AT"));

		int nonTaxId = rs.getInt("NON_TAX_ID");

		// 비과세 FK NULL과 0을 구분한다.
		// 非課税FKのNULLと0を区別する。
		if (!rs.wasNull()) {

			item.setNonTaxId(nonTaxId);

		}

		// DB 컬럼명 NON_PAY_AMOUT를 그대로 읽는다.
		// DBカラム名NON_PAY_AMOUTをそのまま読む。
		long nonPayAmount = rs.getLong("NON_PAY_AMOUT");

		// 비과세 한도 NULL과 0원을 구분한다.
		// 非課税限度のNULLと0円を区別する。
		if (!rs.wasNull()) {

			item.setNonPayAmount(nonPayAmount);

		}

		// 구분명은 JOIN 결과다.
		// 区分名はJOINの結果である。
		item.setNonTaxCategory(rs.getString("NON_TAX_CATEGORY"));

		return item;

	}

	/**
	 * 지급항목 PK로 한 건을 조회한다.
	 * 支給項目PKで1件を照会する。
	 */
	public PayItem selectById(Connection conn, int payItemId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			// 비과세 구분명은 JOIN으로 붙인다.
			// 非課税区分名はJOINで付ける。
			// WHERE는 PK만 본다.
			// WHEREはPKだけを見る。
			pstmt = conn.prepareStatement("SELECT p.PAY_ITEM_ID, p.COMPANY_ID, p.PAY_ITEM_NAME, p.TAXABLE_YN, "
					+ "p.CALCULATION_METHOD, p.TRUNCATION_UNIT, p.ATTENDANCE_PAY_RULE, "
					+ "p.BULK_PAY_AMOUNT, p.USE_YN, p.DISPLAY_ORDER, p.REG_ID, p.MOD_ID, "
					+ "p.CREATED_AT, p.UPDATED_AT, p.NON_TAX_ID, p.NON_PAY_AMOUT, " + "n.NON_TAX_CATEGORY "
					+ "FROM PAY_ITEM p " + "LEFT JOIN NON_TAX_DETAIL n ON p.NON_TAX_ID = n.NON_TAX_ID "
					+ "WHERE p.PAY_ITEM_ID = ?");
			pstmt.setInt(1, payItemId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				// 있으면 목록과 같은 mapRow로 채운다.
				// あれば一覧と同じmapRowで埋める。

				return mapRow(rs);

			}

			// 행이 없으면 null이다.
			// 行がなければnullである。
			return null;

		} finally {

			// ResultSet·Statement만 닫는다.
			// ResultSet・Statementだけを閉じる。
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

		}

	}

	/**
	 * 지급항목을 신규 등록한다.
	 * 支給項目を新規登録する。
	 */
	public void insert(Connection conn, PayItem item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			// 시퀀스로 PK를 채번한다.
			// シーケンスでPKを採番する。
			// CREATED_AT·UPDATED_AT는 SYSDATE다.
			// CREATED_AT・UPDATED_ATはSYSDATEである。
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
			// 절사 단위 null은 0으로 넣는다.
			// 端数処理単位のnullは0で入れる。
			pstmt.setInt(5, item.getTruncationUnit() == null ? 0 : item.getTruncationUnit());

			// 근태지급규칙이 없으면 DB NULL로 넣는다.
			// 勤怠支給規則がなければDB NULLで入れる。
			if (item.getAttendancePayRule() == null) {
				pstmt.setNull(6, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(6, item.getAttendancePayRule());
			}

			// 일괄지급액이 없으면 DB NULL로 넣는다.
			// 一括支給額がなければDB NULLで入れる。
			if (item.getBulkPayAmount() == null) {
				pstmt.setNull(7, java.sql.Types.BIGINT);
			} else {
				pstmt.setLong(7, item.getBulkPayAmount());
			}

			pstmt.setString(8, item.getUseYn());
			// 표시순서 null도 0이다.
			// 表示順のnullも0である。
			pstmt.setInt(9, item.getDisplayOrder() == null ? 0 : item.getDisplayOrder());
			pstmt.setString(10, item.getRegId());
			pstmt.setString(11, item.getModId());

			// 비과세 FK가 없으면 DB NULL로 넣는다.
			// 非課税FKがなければDB NULLで入れる。
			if (item.getNonTaxId() == null) {
				pstmt.setNull(12, java.sql.Types.INTEGER);
			} else {
				pstmt.setInt(12, item.getNonTaxId());
			}

			// 비과세 한도가 없으면 NON_PAY_AMOUT에 NULL을 넣는다.
			// 非課税限度がなければNON_PAY_AMOUTにNULLを入れる。
			if (item.getNonPayAmount() == null) {
				pstmt.setNull(13, java.sql.Types.BIGINT);
			} else {
				pstmt.setLong(13, item.getNonPayAmount());
			}

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
	 * 같은 회사·같은 지급항목명이 있는지 본다.
	 * 同じ会社・同じ支給項目名があるかを見る。
	 */
	public boolean existsByName(Connection conn, int companyId, String payItemName, Integer excludePayItemId)
			throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			if (excludePayItemId == null) {
				pstmt = conn.prepareStatement(
						"SELECT 1 FROM PAY_ITEM WHERE COMPANY_ID = ? AND PAY_ITEM_NAME = ? AND ROWNUM = 1");
				pstmt.setInt(1, companyId);
				pstmt.setString(2, payItemName);
			} else {
				pstmt = conn.prepareStatement(
						"SELECT 1 FROM PAY_ITEM WHERE COMPANY_ID = ? AND PAY_ITEM_NAME = ? AND PAY_ITEM_ID <> ? AND ROWNUM = 1");
				pstmt.setInt(1, companyId);
				pstmt.setString(2, payItemName);
				pstmt.setInt(3, excludePayItemId);
			}

			rs = pstmt.executeQuery();
			return rs.next();

		} finally {

			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

		}

	}

	/**
	 * 급여 지급상세에서 이 항목을 쓰는지 본다.
	 * 給与支給明細でこの項目を使っているかを見る。
	 */
	public boolean existsPayrollUsage(Connection conn, int payItemId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement(
					"SELECT 1 FROM PAYROLL_PAY_DETAIL WHERE PAY_ITEM_ID = ? AND ROWNUM = 1");
			pstmt.setInt(1, payItemId);
			rs = pstmt.executeQuery();
			return rs.next();

		} finally {

			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

		}

	}

	/**
	 * 지급항목을 수정한다.
	 * 支給項目を更新する。
	 */
	public int update(Connection conn, PayItem item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			// SET 목록에 DISPLAY_ORDER·REG_ID는 없다.
			// SET句にDISPLAY_ORDER・REG_IDはない。
			// WHERE는 PK와 회사를 함께 본다.
			// WHEREはPKと会社を合わせて見る。
			pstmt = conn.prepareStatement(
					"UPDATE PAY_ITEM SET " + "PAY_ITEM_NAME = ?, TAXABLE_YN = ?, CALCULATION_METHOD = ?, "
							+ "TRUNCATION_UNIT = ?, ATTENDANCE_PAY_RULE = ?, BULK_PAY_AMOUNT = ?, USE_YN = ?, NON_TAX_ID = ?, "
							+ "NON_PAY_AMOUT = ?, MOD_ID = ?, UPDATED_AT = CURRENT_TIMESTAMP "
							+ "WHERE PAY_ITEM_ID = ? AND COMPANY_ID = ?");
			pstmt.setString(1, item.getPayItemName());
			pstmt.setString(2, item.getTaxableYn());

			// 계산방법이 없으면 DB NULL로 넣는다.
			// 計算方法がなければDB NULLで入れる。
			if (item.getCalculationMethod() == null) {
				pstmt.setNull(3, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(3, item.getCalculationMethod());
			}

			// 절사 단위 null은 0으로 넣는다.
			// 端数処理単位のnullは0で入れる。
			pstmt.setInt(4, item.getTruncationUnit() == null ? 0 : item.getTruncationUnit());

			// 근태지급규칙이 없으면 DB NULL로 넣는다.
			// 勤怠支給規則がなければDB NULLで入れる。
			if (item.getAttendancePayRule() == null) {

				pstmt.setNull(5, java.sql.Types.VARCHAR);

			} else {

				pstmt.setString(5, item.getAttendancePayRule());

			}

			// 일괄지급액이 없으면 DB NULL로 넣는다.
			// 一括支給額がなければDB NULLで入れる。
			if (item.getBulkPayAmount() == null) {
				pstmt.setNull(6, java.sql.Types.BIGINT);
			} else {
				pstmt.setLong(6, item.getBulkPayAmount());
			}

			pstmt.setString(7, item.getUseYn());

			// 비과세 FK가 없으면 DB NULL로 넣는다.
			// 非課税FKがなければDB NULLで入れる。
			if (item.getNonTaxId() == null) {

				pstmt.setNull(8, java.sql.Types.INTEGER);

			} else {

				pstmt.setInt(8, item.getNonTaxId());

			}

			// 비과세 한도가 없으면 NON_PAY_AMOUT에 NULL을 넣는다.
			// 非課税限度がなければNON_PAY_AMOUTにNULLを入れる。
			if (item.getNonPayAmount() == null) {

				pstmt.setNull(9, java.sql.Types.BIGINT);

			} else {

				pstmt.setLong(9, item.getNonPayAmount());

			}

			pstmt.setString(10, item.getModId());
			// WHERE 바인딩은 SET 다음 순서다.
			// WHEREのバインドはSETの次の順である。
			pstmt.setInt(11, item.getPayItemId());
			pstmt.setInt(12, item.getCompanyId());
			return pstmt.executeUpdate();

		} finally {

			// Statement만 닫는다.
			// Statementだけを閉じる。
			JdbcUtil.close(pstmt);

		}

	}

	/**
	 * 지급항목을 PK로 삭제한다.
	 * 支給項目をPKで削除する。
	 */
	public int delete(Connection conn, int payItemId) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			// WHERE는 PK만 본다.
			// WHEREはPKだけを見る。
			pstmt = conn.prepareStatement("DELETE FROM PAY_ITEM WHERE PAY_ITEM_ID = ?");
			pstmt.setInt(1, payItemId);
			// DELETE를 실행한다.
			// DELETEを実行する。
			return pstmt.executeUpdate();

		} finally {

			// Statement만 닫는다.
			// Statementだけを閉じる。
			JdbcUtil.close(pstmt);

		}

	}

}
