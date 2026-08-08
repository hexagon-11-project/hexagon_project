package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeQualification;
import jdbc.JdbcUtil;

public class EmployeeQualificationDao {

    public void insert(Connection conn, int employeeId, EmployeeQualification v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_QUALIFICATION ("
                       + "  QUALIFICATION_ID, EMPLOYEE_ID, QUALIFICATION_NAME, ACQUISITION_DATE, "
                       + "  ISSUING_ORGANIZATION, CERTIFICATE_NO, MEMO, REG_ID, MOD_ID"
                       + ") VALUES (EMP_QUALIFICATION_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getQualificationName());
            pstmt.setDate(3, v.getAcquisitionDate());
            pstmt.setString(4, v.getIssuingOrganization());
            pstmt.setString(5, v.getCertificateNo());
            pstmt.setString(6, v.getMemo());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_QUALIFICATION WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    public List<EmployeeQualification> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_QUALIFICATION WHERE EMPLOYEE_ID = ? ORDER BY QUALIFICATION_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmployeeQualification> list = new ArrayList<>();
            while (rs.next()) {
                EmployeeQualification v = new EmployeeQualification();
                v.setQualificationName(rs.getString("QUALIFICATION_NAME"));
                v.setAcquisitionDate(rs.getDate("ACQUISITION_DATE"));
                v.setIssuingOrganization(rs.getString("ISSUING_ORGANIZATION"));
                v.setCertificateNo(rs.getString("CERTIFICATE_NO"));
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
