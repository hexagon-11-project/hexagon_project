package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeCareer;
import jdbc.JdbcUtil;

// 사원 경력(이전 직장, 이력 등) 데이터를 처리하는 DAO
// 社員の経歴（前職、履歴など）データを処理する DAO
public class EmployeeCareerDao {

    // 경력 데이터 한 줄을 DB에 밀어 넣는(인서트 치는) 메서드
    // 経歴データを1行DBに押し込む（インサートをかける）メソッド
    public void insert(Connection conn, int employeeId, EmployeeCareer v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_CAREER ("
                       + "  CAREER_ID, EMPLOYEE_ID, COMPANY_NAME, DEPARTMENT, POSITION, START_DATE, END_DATE, "
                       + "  DUTY_YY, DUTY_MM, CAREER_DESCRIPTION, REG_ID, MOD_ID"
                       + ") VALUES (EMP_CAREER_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getCompanyName());
            pstmt.setString(3, v.getDepartment());
            pstmt.setString(4, v.getPosition());
            pstmt.setDate(5, v.getStartDate());
            pstmt.setDate(6, v.getEndDate());
            pstmt.setInt(7, v.getDutyYy());
            pstmt.setInt(8, v.getDutyMm());
            pstmt.setString(9, v.getCareerDescription());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 경력 이력을 통째로 날림
    // (화면에서 수정 시 부분 업데이트 안 하고, 기존 데이터 싹 지운 다음 새로 엎어치기 하려는 용도)
    // 特定社員の経歴履歴を丸ごと飛ばす
    // （画面修正時に部分アップデートせず、既存データを全消しして新しく洗い替えするための用途）
    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_CAREER WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 전체 경력 내역을 쫙 긁어옴 (화면 표에 뿌려줄 때 씀)
    // 特定社員の全経歴履歴をざっとかき集める（画面の表に描画する時に使う）
    public List<EmployeeCareer> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_CAREER WHERE EMPLOYEE_ID = ? ORDER BY CAREER_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmployeeCareer> list = new ArrayList<>();
            while (rs.next()) {
                EmployeeCareer v = new EmployeeCareer();
                v.setCompanyName(rs.getString("COMPANY_NAME"));
                v.setDepartment(rs.getString("DEPARTMENT"));
                v.setPosition(rs.getString("POSITION"));
                v.setStartDate(rs.getDate("START_DATE"));
                v.setEndDate(rs.getDate("END_DATE"));
                v.setDutyYy(rs.getInt("DUTY_YY"));
                v.setDutyMm(rs.getInt("DUTY_MM"));
                v.setCareerDescription(rs.getString("CAREER_DESCRIPTION"));
                list.add(v);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}