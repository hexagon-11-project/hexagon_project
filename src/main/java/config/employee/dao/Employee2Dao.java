package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import config.employee.model.Employee2;
import jdbc.JdbcUtil;

// 사원등록 2페이지(추가 정보, 퇴직 정보 등) 데이터를 처리하는 DAO
// 社員登録2ページ目（追加情報、退職情報など）のデータを処理する DAO
public class Employee2Dao {

    // 사원등록 2페이지 정보 조회용 
    // 社員登録2ページ目の情報照会用
    public Employee2 selectAdditionalInfo(Connection conn, String employeeNo) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Employee2 emp2 = null;
        try {
            // 예시 쿼리: 2페이지 관련 테이블이나 기존 EMPLOYEE 테이블에서 조회
            String sql = "SELECT * FROM EMPLOYEE WHERE EMPLOYEE_NO = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employeeNo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                emp2 = new Employee2();
                emp2.setEmployeeNo(rs.getString("EMPLOYEE_NO"));
                // 필요한 추가 컬럼들이 있다면 여기에 세팅하면 됩니다.
            }
            return emp2;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }

    // 2페이지에서 입력받은 퇴직/보증 정보 등을 기존 사원 테이블에 업데이트(저장) 치는 메서드
    // 2ページ目で入力された退職・保証情報などを、既存の社員テーブルにアップデート（保存）をかけるメソッド
    public int saveAdditionalInfo(Connection conn, Employee2 emp2) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "UPDATE EMPLOYEE SET "
                       + "RETIRE_TYPE = ?, RETIRE_DATE = ?, RETIRE_REASON = ? "
                       + "WHERE EMPLOYEE_NO = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, emp2.getRetireType());
            pstmt.setDate(2, emp2.getRetireDate() != null ? new java.sql.Date(emp2.getRetireDate().getTime()) : null);
            pstmt.setString(3, emp2.getRetireReason());
            pstmt.setString(4, emp2.getEmployeeNo());
            
            return pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }
}