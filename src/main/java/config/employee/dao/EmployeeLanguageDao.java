package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeLanguage;
import jdbc.JdbcUtil;

// 사원 어학능력(어학시험, 공인점수, 회화 수준 등) 데이터를 처리하는 DAO
// 社員の語学能力（語学試験、公認スコア、会話レベルなど）データを処理する DAO
public class EmployeeLanguageDao {

    // 어학 데이터 한 줄을 DB에 밀어 넣는(인서트 치는) 메서드
    // 語学データを1行DBに押し込む（インサートをかける）メソッド
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

    // 특정 사원의 어학 데이터를 통째로 날림
    // (화면에서 수정할 때 부분 업데이트 안 하고, 기존 데이터를 싹 지운 다음 새로 엎어치기 하려는 용도)
    // 特定社員の語学データを丸ごと飛ばす
    // （画面での修正時、部分アップデートを行わず既存データを全消しして新しく洗い替えするための用途）
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

    // 특정 사원의 전체 어학/시험 내역을 쫙 긁어옴 (화면 표에 뿌려줄 때 씀)
    // 特定社員の全語学・試験履歴をざっとかき集める（画面の表に描画する時に使う）
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