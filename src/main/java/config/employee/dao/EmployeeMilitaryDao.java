package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.employee.model.EmployeeMilitary;
import jdbc.JdbcUtil;

// EMPLOYEE_MILITARY는 사원 한 명당 한 줄뿐인 테이블이라(PK가 EMPLOYEE_ID 그 자체),
// 시퀀스로 채번하는 다른 테이블들과 다르게 insert/update/select 모두 employeeId로 직접 다룬다.
public class EmployeeMilitaryDao {

    public void insert(Connection conn, int employeeId, EmployeeMilitary v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_MILITARY ("
                       + "  EMPLOYEE_ID, MILITARY_STATUS_CODE, MILITARY_BRANCH_CODE, "
                       + "  MILITARY_SERVICE_START_DATE, MILITARY_SERVICE_END_DATE, "
                       + "  MILITARY_SPECIALTY, MILITARY_EXEMPT_REASON, MILITARY_GRADE, MILITARY_BRANCH, "
                       + "  REG_ID, MOD_ID"
                       + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getMilitaryStatusCode());
            pstmt.setString(3, v.getMilitaryBranchCode());
            pstmt.setDate(4, v.getServiceStartDate());
            pstmt.setDate(5, v.getServiceEndDate());
            pstmt.setString(6, v.getMilitarySpecialty());
            pstmt.setString(7, v.getMilitaryExemptReason());
            pstmt.setString(8, v.getMilitaryGrade());
            pstmt.setString(9, v.getMilitaryBranch());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 이미 등록된 병역기록이 있으면 새로 넣지 않고 덮어쓴다 (1:1 관계라 여러 줄이 되면 안 됨)
    public int update(Connection conn, int employeeId, EmployeeMilitary v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "UPDATE EMPLOYEE_MILITARY SET "
                       + "MILITARY_STATUS_CODE=?, MILITARY_BRANCH_CODE=?, "
                       + "MILITARY_SERVICE_START_DATE=?, MILITARY_SERVICE_END_DATE=?, "
                       + "MILITARY_SPECIALTY=?, MILITARY_EXEMPT_REASON=?, MILITARY_GRADE=?, MILITARY_BRANCH=?, "
                       + "MOD_ID='SYSTEM', UPDATED_AT=SYSDATE "
                       + "WHERE EMPLOYEE_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, v.getMilitaryStatusCode());
            pstmt.setString(2, v.getMilitaryBranchCode());
            pstmt.setDate(3, v.getServiceStartDate());
            pstmt.setDate(4, v.getServiceEndDate());
            pstmt.setString(5, v.getMilitarySpecialty());
            pstmt.setString(6, v.getMilitaryExemptReason());
            pstmt.setString(7, v.getMilitaryGrade());
            pstmt.setString(8, v.getMilitaryBranch());
            pstmt.setInt(9, employeeId);
            return pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 등록이면 insert, 이미 있으면 update - 호출하는 쪽에서 매번 이거 하나만 부르면 됨
    public void save(Connection conn, int employeeId, EmployeeMilitary v) throws SQLException {
        if (exists(conn, employeeId)) {
            update(conn, employeeId, v);
        } else {
            insert(conn, employeeId, v);
        }
    }

    public boolean exists(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM EMPLOYEE_MILITARY WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }

    public EmployeeMilitary selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_MILITARY WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                EmployeeMilitary v = new EmployeeMilitary();
                v.setMilitaryStatusCode(rs.getString("MILITARY_STATUS_CODE"));
                v.setMilitaryBranchCode(rs.getString("MILITARY_BRANCH_CODE"));
                v.setServiceStartDate(rs.getDate("MILITARY_SERVICE_START_DATE"));
                v.setServiceEndDate(rs.getDate("MILITARY_SERVICE_END_DATE"));
                v.setMilitarySpecialty(rs.getString("MILITARY_SPECIALTY"));
                v.setMilitaryExemptReason(rs.getString("MILITARY_EXEMPT_REASON"));
                v.setMilitaryGrade(rs.getString("MILITARY_GRADE"));
                v.setMilitaryBranch(rs.getString("MILITARY_BRANCH"));
                return v;
            }
            return null;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}
