package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeCareer;
import jdbc.JdbcUtil;

public class EmployeeCareerDao {

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
