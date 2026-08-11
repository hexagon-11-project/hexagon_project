package person.certificatePrintWorking.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.Employee;
import jdbc.JdbcUtil;

public class CertificatePrintWorkingDao {
	public List<Employee> selectEmployeeList(Connection conn) throws SQLException {
	PreparedStatement pstmt = null;
    ResultSet rs = null;
    List<Employee> list = new ArrayList<>();
    
    try {
        // EMPLOYEE_NO는 식별용으로 유지
        String sql = "SELECT EMPLOYEE_NO, EMPLOYMENT_TYPE, EMPLOYEE_NAME, "
                   + "DEPARTMENT, POSITION, RETIREMENT_YN "
                   + "FROM EMPLOYEE "
                   + "ORDER BY EMPLOYEE_NO DESC";
                   
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Employee emp = new Employee();
            emp.setEmployeeNo(rs.getString("EMPLOYEE_NO"));       // 숨겨진 식별자용
            emp.setEmploymentType(rs.getString("EMPLOYMENT_TYPE")); // 구분
            emp.setEmployeeName(rs.getString("EMPLOYEE_NAME"));     // 성명
            emp.setDepartment(rs.getString("DEPARTMENT"));          // 부서
            emp.setPosition(rs.getString("POSITION"));              // 직위
            emp.setRetirementYn(rs.getString("RETIREMENT_YN"));   //상태(재직여부) 추가  
            
            list.add(emp);
        }
        return list;
    } finally {
        JdbcUtil.close(rs);
        JdbcUtil.close(pstmt);
    }
}

	public Employee selectEmployeeDetail(Connection conn, String employeeNo) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Employee emp = null;
        
        try {
            String sql = "SELECT EMPLOYEE_NO, EMPLOYEE_NAME, DEPARTMENT, POSITION, "
                       + "RESIDENT_REG_NO, HIRE_DATE, RESIGN_DATE, RETIREMENT_YN "
                       + "FROM EMPLOYEE WHERE EMPLOYEE_NO = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employeeNo);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                emp = new Employee();
                emp.setEmployeeNo(rs.getString("EMPLOYEE_NO"));
                emp.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
                emp.setDepartment(rs.getString("DEPARTMENT"));
                emp.setPosition(rs.getString("POSITION"));
                emp.setResidentRegNo(rs.getString("RESIDENT_REG_NO"));
                emp.setHireDate(rs.getDate("HIRE_DATE"));     // 근속기간 계산용
                emp.setResignDate(rs.getDate("RESIGN_DATE")); // 근속기간 계산용
                emp.setRetirementYn(rs.getString("RETIREMENT_YN"));
               
            }
            return emp;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}


