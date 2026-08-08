package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeEducation;
import jdbc.JdbcUtil;

public class EmployeeEducationDao {

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
