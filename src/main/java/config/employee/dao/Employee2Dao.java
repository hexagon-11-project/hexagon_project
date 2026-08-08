package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import config.employee.model.Employee2;
import jdbc.JdbcUtil;

public class Employee2Dao {

    // 1. 특정 사원의 2페이지 추가 정보를 조회하는 메서드 (나중에 필요할 때 쿼리 완성)
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

    // 2. 2페이지에서 입력한 추가 정보(보증, 퇴직 등)를 DB에 저장(INSERT 또는 UPDATE)하는 메서드
    public int saveAdditionalInfo(Connection conn, Employee2 emp2) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            // 예시 쿼리 (프로젝트 DB 설계에 맞게 컬럼과 쿼리를 수정해서 사용하세요)
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