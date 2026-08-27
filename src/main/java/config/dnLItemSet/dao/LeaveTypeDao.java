package config.dnLItemSet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.model.LeaveType;
import jdbc.JdbcUtil;

// 회사별 휴가 항목(연차, 반차 등)을 관리하는 DAO
// 会社別の休暇項目（有休、半休など）を管理する DAO
public class LeaveTypeDao {

	// 특정 회사의 전체 휴가 항목 목록을 가져옴 (화면 표시 순서대로 정렬)
	// 特定の会社の全休暇項目リストを取得（画面の表示順にソート）
	public List<LeaveType> selectByCompanyId(Connection conn, int companyId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("SELECT LEAVE_TYPE_ID, COMPANY_ID, LEAVE_CODE, LEAVE_NAME, "
					+ "EFFECTIVE_START_DATE, EFFECTIVE_END_DATE, USE_YN, DISPLAY_ORDER " + "FROM LEAVE_TYPE "
					+ "WHERE COMPANY_ID = ? " + "ORDER BY DISPLAY_ORDER, LEAVE_TYPE_ID");
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<LeaveType> result = new ArrayList<>();

			while (rs.next()) {
				result.add(mapRow(rs));
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// ID로 휴가 항목 딱 하나만 조회 (단건 조회)
	// IDで休暇項目を1件だけ照会（単件照会）
	public LeaveType selectById(Connection conn, int leaveTypeId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("SELECT LEAVE_TYPE_ID, COMPANY_ID, LEAVE_CODE, LEAVE_NAME, "
					+ "EFFECTIVE_START_DATE, EFFECTIVE_END_DATE, USE_YN, DISPLAY_ORDER " + "FROM LEAVE_TYPE "
					+ "WHERE LEAVE_TYPE_ID = ?");
			pstmt.setInt(1, leaveTypeId);
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

	// 휴가 항목 신규 등록
	// 休暇項目の新規登録
	public void insert(Connection conn, LeaveType item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("INSERT INTO LEAVE_TYPE ("
					+ "LEAVE_TYPE_ID, COMPANY_ID, LEAVE_CODE, LEAVE_NAME, EFFECTIVE_START_DATE, EFFECTIVE_END_DATE, "
					+ "USE_YN, DISPLAY_ORDER, REG_ID, MOD_ID, CREATED_AT, UPDATED_AT" + ") VALUES ("
					+ "LEAVE_TYPE_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, SYSDATE" + ")");

			pstmt.setInt(1, item.getCompanyId());
			pstmt.setString(2, item.getLeaveCode());
			pstmt.setString(3, item.getLeaveName());
			pstmt.setDate(4, item.getEffectiveStartDate());
			pstmt.setDate(5, item.getEffectiveEndDate());
			pstmt.setString(6, item.getUseYn());
			pstmt.setInt(7, item.getDisplayOrder() == null ? 1 : item.getDisplayOrder());
			pstmt.setString(8, "SYSTEM");
			pstmt.setString(9, "SYSTEM");

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 기존 휴가 항목 내용 수정
	// 既存の休暇項目の内容を修正
	public void update(Connection conn, LeaveType item) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("UPDATE LEAVE_TYPE SET "
					+ "LEAVE_NAME = ?, EFFECTIVE_START_DATE = ?, EFFECTIVE_END_DATE = ?, USE_YN = ?, MOD_ID = ?, "
					+ "UPDATED_AT = SYSDATE " + "WHERE LEAVE_TYPE_ID = ? AND COMPANY_ID = ?");

			pstmt.setString(1, item.getLeaveName());
			pstmt.setDate(2, item.getEffectiveStartDate());
			pstmt.setDate(3, item.getEffectiveEndDate());
			pstmt.setString(4, item.getUseYn());
			pstmt.setString(5, "SYSTEM");
			pstmt.setInt(6, item.getLeaveTypeId());
			pstmt.setInt(7, item.getCompanyId());

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 휴가 항목 완전 삭제 (DB에서 날림)
	// 休暇項目の完全削除（DBから物理削除）
	public void delete(Connection conn, int leaveTypeId) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("DELETE FROM LEAVE_TYPE WHERE LEAVE_TYPE_ID = ?");
			pstmt.setInt(1, leaveTypeId);
			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	private LeaveType mapRow(ResultSet rs) throws SQLException {

		LeaveType item = new LeaveType();

		item.setLeaveTypeId(rs.getInt("LEAVE_TYPE_ID"));
		item.setCompanyId(rs.getInt("COMPANY_ID"));
		item.setLeaveCode(rs.getString("LEAVE_CODE"));
		item.setLeaveName(rs.getString("LEAVE_NAME"));
		item.setEffectiveStartDate(rs.getDate("EFFECTIVE_START_DATE"));
		item.setEffectiveEndDate(rs.getDate("EFFECTIVE_END_DATE"));
		item.setUseYn(rs.getString("USE_YN"));
		item.setDisplayOrder(rs.getInt("DISPLAY_ORDER"));

		return item;
	}
}