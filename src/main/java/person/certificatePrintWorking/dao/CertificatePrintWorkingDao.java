package person.certificatePrintWorking.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.Employee;
import jdbc.JdbcUtil;
import person.model.CertificatePrintWorkingModel;

public class CertificatePrintWorkingDao {
    
	public List<Employee> selectEmployeeList(Connection conn, String searchName) throws SQLException {
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    List<Employee> list = new ArrayList<>();
	    
	    try {
	       
	        String sql = "SELECT employee_no, employment_type, employee_name, "
	                   + "department, position, retirement_yn "
	                   + "FROM employee ";
	        
	        // 검색어가 넘어온 경우 WHERE 조건 추가
	        if (searchName != null && !searchName.trim().isEmpty()) {
	            sql += "WHERE employee_name LIKE ? ";
	        }
	        
	        sql += "ORDER BY employee_no DESC";
	                   
	        pstmt = conn.prepareStatement(sql);
	        
	        // 검색어가 있을 때만 파라미터 세팅
	        if (searchName != null && !searchName.trim().isEmpty()) {
	            pstmt.setString(1, "%" + searchName.trim() + "%");
	        }
	        
	        rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            Employee emp = new Employee();
	            emp.setEmployeeNo(rs.getString("employee_no"));
	            emp.setEmploymentType(rs.getString("employment_type"));
	            emp.setEmployeeName(rs.getString("employee_name"));
	            emp.setDepartment(rs.getString("department"));
	            emp.setPosition(rs.getString("position"));
	            emp.setRetirementYn(rs.getString("retirement_yn"));
	            
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
            String sql = "SELECT employee_no, employee_name, department, position, "
                       + "resident_reg_no, hire_date, resign_date, retirement_yn "
                       + "FROM employee WHERE employee_no = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employeeNo);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                emp = new Employee();
                emp.setEmployeeNo(rs.getString("employee_no"));
                emp.setEmployeeName(rs.getString("employee_name"));
                emp.setDepartment(rs.getString("department"));
                emp.setPosition(rs.getString("position"));
                emp.setResidentRegNo(rs.getString("resident_reg_no"));
                emp.setHireDate(rs.getDate("hire_date"));     // 근속기간 계산용
                emp.setResignDate(rs.getDate("resign_date")); // 근속기간 계산용
                emp.setRetirementYn(rs.getString("retirement_yn"));
            }
            return emp;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
    
    public int insertCertificatePrintWorking(Connection conn, CertificatePrintWorkingModel model) throws SQLException {
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            String sql = "INSERT INTO certificate_issue ( "
                       + "    certificate_issue_id, company_id, employee_id, certificate_type_code, "
                       + "    issue_year, issue_sequence, issue_no, issue_date, purpose, "
                       + "    submission_target, reg_id, mod_id, created_at, updated_at  "
                       + ") "
                       + "SELECT "
                       + "    certificate_issue_seq.NEXTVAL, " 
                       + "    e.company_id, "                 
                       + "    e.employee_id, "               
                       + "    ?, "                            
                       + "    TO_CHAR(SYSDATE, 'YYYY'), "     
                       + "    (SELECT NVL(MAX(issue_sequence), 0) + 1 FROM certificate_issue WHERE issue_year = TO_CHAR(SYSDATE, 'YYYY')), " // 6. 순번
                       // 7. 발급번호: 4자리 연도(YYYY) || '-' || 6자리 순번(000001) 자동 생성
                       + "    TO_CHAR(SYSDATE, 'YYYY') || '-' || LPAD((SELECT NVL(MAX(issue_sequence), 0) + 1 FROM certificate_issue WHERE issue_year = TO_CHAR(SYSDATE, 'YYYY')), 6, '0'), "
                       + "    SYSDATE, "                       
                       + "    ?, "                             
                       + "    ?, "                             
                       + "    ?, "                             
                       + "    ?, "                            
                       + "    SYSDATE, "                      
                       + "    SYSDATE "                        
                       + "FROM employee e "
                       + "WHERE e.employee_no = ?";            

            pstmt = conn.prepareStatement(sql);
            
           
            pstmt.setString(1, model.getCertificateTypeCode()); 
            pstmt.setString(2, model.getPurpose());             
            pstmt.setString(3, model.getSubmissionTarget());    
            pstmt.setString(4, model.getRegId());               
            pstmt.setString(5, model.getRegId());               
            pstmt.setString(6, model.getEmployeeNo());          
            result = pstmt.executeUpdate();
            
        } finally {
            JdbcUtil.close(pstmt); 
        }
        
        return result;
    }
}