package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmpolyeeAppointment;
import jdbc.JdbcUtil;

public class AppointmentDao {

    public void insert(Connection conn, int employeeId, EmpolyeeAppointment v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_APPOINTMENT ("
                       + "  APPOINTMENT_ID, EMPLOYEE_ID, APPOINTMENT_TYPE_CODE, APPOINTMENT_DATE, "
                       + "  DEPARTMENT, POSITION, DUTY_TITLE, MEMO, REG_ID, MOD_ID"
                       + ") VALUES (EMP_APPOINTMENT_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getTypeCode());
            pstmt.setDate(3, v.getDate());
            pstmt.setString(4, v.getDepartment());
            pstmt.setString(5, v.getPosition());
            pstmt.setString(6, v.getDutyTitle());
            pstmt.setString(7, v.getMemo());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_APPOINTMENT WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    public List<EmpolyeeAppointment> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_APPOINTMENT WHERE EMPLOYEE_ID = ? ORDER BY APPOINTMENT_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmpolyeeAppointment> list = new ArrayList<>();
            while (rs.next()) {
                EmpolyeeAppointment v = new EmpolyeeAppointment();
                v.setTypeCode(rs.getString("APPOINTMENT_TYPE_CODE"));
                v.setDate(rs.getDate("APPOINTMENT_DATE"));
                v.setDepartment(rs.getString("DEPARTMENT"));
                v.setPosition(rs.getString("POSITION"));
                v.setDutyTitle(rs.getString("DUTY_TITLE"));
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
