package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeRewardPunishment;
import jdbc.JdbcUtil;

public class RewardPunishmentDao {

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
