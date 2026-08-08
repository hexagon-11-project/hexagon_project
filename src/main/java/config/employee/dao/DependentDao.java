package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeDependent;
import jdbc.JdbcUtil;

public class DependentDao {

    public void insert(Connection conn, int employeeId, EmployeeDependent v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_DEPENDENT ("
                       + "  DEPENDENT_ID, EMPLOYEE_ID, DEPENDENT_NAME, RELATION_CODE, DOM_FOR_YN, BIRTH_DATE, "
                       + "  DISABLED_YN, PERSONAL_DEDUCTION_YN, HEALTH_INSURANCE_YN, COHABITATION_YN, "
                       + "  WAGE_INCOME_TAX_YN, CHILD_UNDER_20_YN, REG_ID, MOD_ID"
                       + ") VALUES (EMP_DEPENDENT_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getDependentName());
            pstmt.setString(3, v.getRelationCode());
            pstmt.setString(4, v.getDomForYn());
            pstmt.setDate(5, v.getBirthDate());
            pstmt.setString(6, v.getDisabledYn());
            pstmt.setString(7, v.getPersonalDeductionYn());
            pstmt.setString(8, v.getHealthInsuranceYn());
            pstmt.setString(9, v.getCohabitationYn());
            pstmt.setString(10, v.getWageIncomeTaxYn());
            pstmt.setString(11, v.getChildUnder20Yn());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 사원 한 명의 부양가족을 통째로 지운다 (저장할 때마다 전체 재입력 방식으로 처리하기 위함)
    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_DEPENDENT WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    public List<EmployeeDependent> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_DEPENDENT WHERE EMPLOYEE_ID = ? ORDER BY DEPENDENT_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmployeeDependent> list = new ArrayList<>();
            while (rs.next()) {
                EmployeeDependent v = new EmployeeDependent();
                v.setDependentName(rs.getString("DEPENDENT_NAME"));
                v.setRelationCode(rs.getString("RELATION_CODE"));
                v.setDomForYn(rs.getString("DOM_FOR_YN"));
                v.setBirthDate(rs.getDate("BIRTH_DATE"));
                v.setDisabledYn(rs.getString("DISABLED_YN"));
                v.setPersonalDeductionYn(rs.getString("PERSONAL_DEDUCTION_YN"));
                v.setHealthInsuranceYn(rs.getString("HEALTH_INSURANCE_YN"));
                v.setCohabitationYn(rs.getString("COHABITATION_YN"));
                v.setWageIncomeTaxYn(rs.getString("WAGE_INCOME_TAX_YN"));
                v.setChildUnder20Yn(rs.getString("CHILD_UNDER_20_YN"));
                list.add(v);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}
