package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeRewardPunishment;
import jdbc.JdbcUtil;

// 사원 상벌(포상 및 징계) 내역 데이터를 처리하는 DAO
// 社員の賞罰（表彰および懲戒）履歴データを処理する DAO
public class RewardPunishmentDao {

    // 상벌 데이터 한 줄을 DB에 밀어 넣는(인서트 치는) 메서드
    // 賞罰データを1行DBに押し込む（インサートをかける）メソッド
    public void insert(Connection conn, int employeeId, EmployeeRewardPunishment v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_REWARD_PUNISHMENT ("
                       + "  REWARD_PUNISHMENT_ID, EMPLOYEE_ID, REWARD_PUNISHMENT_TYPE_CODE, "
                       + "  REWARD_PUNISHMENT_NAME, AUTHORITY_NAME, REWARD_PUNISHMENT_DATE, "
                       + "  REWARD_PUNISHMENT_CONTENT, MEMO, REG_ID, MOD_ID"
                       + ") VALUES (EMP_REWARD_PUNISH_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getTypeCode());
            pstmt.setString(3, v.getName());
            pstmt.setString(4, v.getAuthorityName());
            pstmt.setDate(5, v.getDate());
            pstmt.setString(6, v.getContent());
            pstmt.setString(7, v.getMemo());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 상벌 이력을 통째로 날림
    // (화면에서 수정 시 부분 업데이트 안 하고, 기존 데이터를 싹 지운 다음 새로 엎어치기 하려는 용도)
    // 特定社員の賞罰履歴を丸ごと飛ばす
    // （画面での修正時、部分アップデートを行わず既存データを全消しして新しく洗い替えするための用途）
    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_REWARD_PUNISHMENT WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 전체 상벌 내역을 쫙 긁어옴 (화면 표에 뿌려줄 때 씀)
    // 特定社員の全賞罰履歴をざっとかき集める（画面の表に描画する時に使う）
    public List<EmployeeRewardPunishment> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_REWARD_PUNISHMENT WHERE EMPLOYEE_ID = ? ORDER BY REWARD_PUNISHMENT_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmployeeRewardPunishment> list = new ArrayList<>();
            while (rs.next()) {
                EmployeeRewardPunishment v = new EmployeeRewardPunishment();
                v.setTypeCode(rs.getString("REWARD_PUNISHMENT_TYPE_CODE"));
                v.setName(rs.getString("REWARD_PUNISHMENT_NAME"));
                v.setAuthorityName(rs.getString("AUTHORITY_NAME"));
                v.setDate(rs.getDate("REWARD_PUNISHMENT_DATE"));
                v.setContent(rs.getString("REWARD_PUNISHMENT_CONTENT"));
                v.setMemo(rs.getString("MEMO"));
                list.add(v);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}