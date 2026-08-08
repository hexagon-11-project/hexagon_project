package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeTraining;
import jdbc.JdbcUtil;

public class TrainingDao {

    public void insert(Connection conn, int employeeId, EmployeeTraining v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_TRAINING ("
                       + "  TRAINING_ID, EMPLOYEE_ID, TRAINING_TYPE_CODE, TRAINING_NAME, "
                       + "  TRAINING_START_DATE, TRAINING_END_DATE, TRAINING_INSTITUTION, "
                       + "  TRAINING_COST, REFUND_TRAINING_COST, REG_ID, MOD_ID"
                       + ") VALUES (EMP_TRAINING_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getTrainingTypeCode());
            pstmt.setString(3, v.getTrainingName());
            pstmt.setDate(4, v.getStartDate());
            pstmt.setDate(5, v.getEndDate());
            pstmt.setString(6, v.getTrainingInstitution());
            pstmt.setLong(7, v.getTrainingCost());
            pstmt.setLong(8, v.getRefundTrainingCost());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_TRAINING WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    public List<EmployeeTraining> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_TRAINING WHERE EMPLOYEE_ID = ? ORDER BY TRAINING_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmployeeTraining> list = new ArrayList<>();
            while (rs.next()) {
                EmployeeTraining v = new EmployeeTraining();
                v.setTrainingTypeCode(rs.getString("TRAINING_TYPE_CODE"));
                v.setTrainingName(rs.getString("TRAINING_NAME"));
                v.setStartDate(rs.getDate("TRAINING_START_DATE"));
                v.setEndDate(rs.getDate("TRAINING_END_DATE"));
                v.setTrainingInstitution(rs.getString("TRAINING_INSTITUTION"));
                v.setTrainingCost(rs.getLong("TRAINING_COST"));
                v.setRefundTrainingCost(rs.getLong("REFUND_TRAINING_COST"));
                list.add(v);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}
