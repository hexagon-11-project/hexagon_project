package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeLanguage;
import jdbc.JdbcUtil;

public class EmployeeLanguageDao {

    public void insert(Connection conn, int employeeId, EmployeeLanguage v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_LANGUAGE ("
                       + "  LANGUAGE_ID, EMPLOYEE_ID, LANGUAGE_NAME, TEST_NAME, OFFICIAL_SCORE, "
                       + "  ACQUISITION_DATE, READING_LEVEL_CODE, WRITING_LEVEL_CODE, SPEAKING_LEVEL_CODE, "
                       + "  REG_ID, MOD_ID"
                       + ") VALUES (EMP_LANGUAGE_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getLanguageName());
            pstmt.setString(3, v.getTestName());
            pstmt.setString(4, v.getOfficialScore());
            pstmt.setDate(5, v.getAcquisitionDate());
            pstmt.setString(6, v.getReadingLevelCode());
            pstmt.setString(7, v.getWritingLevelCode());
            pstmt.setString(8, v.getSpeakingLevelCode());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_LANGUAGE WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    public List<EmployeeLanguage> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_LANGUAGE WHERE EMPLOYEE_ID = ? ORDER BY DISPLAY_ORDER, LANGUAGE_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmployeeLanguage> list = new ArrayList<>();
            while (rs.next()) {
                EmployeeLanguage v = new EmployeeLanguage();
                v.setLanguageName(rs.getString("LANGUAGE_NAME"));
                v.setTestName(rs.getString("TEST_NAME"));
                v.setOfficialScore(rs.getString("OFFICIAL_SCORE"));
                v.setAcquisitionDate(rs.getDate("ACQUISITION_DATE"));
                v.setReadingLevelCode(rs.getString("READING_LEVEL_CODE"));
                v.setWritingLevelCode(rs.getString("WRITING_LEVEL_CODE"));
                v.setSpeakingLevelCode(rs.getString("SPEAKING_LEVEL_CODE"));
                list.add(v);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}
