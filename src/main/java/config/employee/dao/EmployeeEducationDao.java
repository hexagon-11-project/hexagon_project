package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeEducation;
import jdbc.JdbcUtil;

// 사원 학력(출신 학교, 전공, 졸업 여부 등) 데이터를 처리하는 DAO
// 社員の学歴（出身校、専攻、卒業可否など）データを処理する DAO
public class EmployeeEducationDao {

    // 학력 데이터 한 줄을 DB에 밀어 넣는(인서트 치는) 메서드
    // 学歴データを1行DBに押し込む（インサートをかける）メソッド
    public void insert(Connection conn, int employeeId, EmployeeEducation v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_EDUCATION ("
                       + "  EDUCATION_ID, EMPLOYEE_ID, SCHOOL_NAME, MAJOR_NAME, START_DATE, END_DATE, "
                       + "  GRADUATION_STATUS, REG_ID, MOD_ID"
                       + ") VALUES (EMP_EDUCATION_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getSchoolName());
            pstmt.setString(3, v.getMajorName());
            pstmt.setDate(4, v.getStartDate());
            pstmt.setDate(5, v.getEndDate());
            pstmt.setString(6, v.getGraduationStatus());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 학력 데이터를 통째로 날림
    // (화면에서 정보 수정 시, 부분 업데이트를 안 하고 기존 데이터를 싹 지운 다음 새로 엎어치기 하려는 용도)
    // 特定社員の学歴データを丸ごと飛ばす
    // （画面での情報修正時、部分アップデートを行わず既存データを全消しして新しく洗い替えするための用途）
    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_EDUCATION WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 전체 학력 내역을 쫙 긁어옴 (화면 표에 뿌려줄 때 씀)
    // 特定社員の全学歴履歴をざっとかき集める（画面の表に描画する時に使う）
    public List<EmployeeEducation> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_EDUCATION WHERE EMPLOYEE_ID = ? ORDER BY EDUCATION_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmployeeEducation> list = new ArrayList<>();
            while (rs.next()) {
                EmployeeEducation v = new EmployeeEducation();
                v.setSchoolName(rs.getString("SCHOOL_NAME"));
                v.setMajorName(rs.getString("MAJOR_NAME"));
                v.setStartDate(rs.getDate("START_DATE"));
                v.setEndDate(rs.getDate("END_DATE"));
                v.setGraduationStatus(rs.getString("GRADUATION_STATUS"));
                list.add(v);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}