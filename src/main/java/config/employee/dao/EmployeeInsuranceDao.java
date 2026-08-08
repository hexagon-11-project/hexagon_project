package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeInsurance;
import jdbc.JdbcUtil;

public class EmployeeInsuranceDao {

    // 참고: 실제 DB에 만들어져 있는 시퀀스 이름이 다른 테이블들과 패턴이 달라서(SEQ_EMPLOYEE_INSURANCE) 그대로 맞춤
    public void insert(Connection conn, int employeeId, EmployeeInsurance v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_INSURANCE ("
                       + "  EMPLOYEE_INSURANCE_ID, EMPLOYEE_ID, INSURANCE_TYPE_CODE, INSURANCE_NO, "
                       + "  ACQUISITION_DATE, LOSS_DATE, REG_ID, MOD_ID"
                       + ") VALUES (SEQ_EMPLOYEE_INSURANCE.NEXTVAL, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getInsuranceTypeCode());
            pstmt.setString(3, v.getInsuranceNo());
            pstmt.setDate(4, v.getAcquisitionDate());
            pstmt.setDate(5, v.getLossDate());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_INSURANCE WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    public List<EmployeeInsurance> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_INSURANCE WHERE EMPLOYEE_ID = ? ORDER BY EMPLOYEE_INSURANCE_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmployeeInsurance> list = new ArrayList<>();
            while (rs.next()) {
                EmployeeInsurance v = new EmployeeInsurance();
                v.setInsuranceTypeCode(rs.getString("INSURANCE_TYPE_CODE"));
                v.setInsuranceNo(rs.getString("INSURANCE_NO"));
                v.setAcquisitionDate(rs.getDate("ACQUISITION_DATE"));
                v.setLossDate(rs.getDate("LOSS_DATE"));
                list.add(v);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}
