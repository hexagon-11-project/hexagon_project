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
    
    public List<Employee> selectEmployeeList(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Employee> list = new ArrayList<>();
        
        try {
            // employee_no는 식별용으로 유지
            String sql = "SELECT employee_no, employment_type, employee_name, "
                       + "department, position, retirement_yn "
                       + "FROM employee "
                       + "ORDER BY employee_no DESC";
                       
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Employee emp = new Employee();
                emp.setEmployeeNo(rs.getString("employee_no"));       // 숨겨진 식별자용
                emp.setEmploymentType(rs.getString("employment_type")); // 구분
                emp.setEmployeeName(rs.getString("employee_name"));     // 성명
                emp.setDepartment(rs.getString("department"));          // 부서
                emp.setPosition(rs.getString("position"));              // 직위
                emp.setRetirementYn(rs.getString("retirement_yn"));   // 상태(재직여부) 추가  
                
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
                       + "    certificate_issue_seq.NEXTVAL, " // 1. 발급 고유아이디
                       + "    e.company_id, "                  // 2. 회사 아이디
                       + "    e.employee_id, "                 // 3. 사원 고유아이디
                       + "    ?, "                             // 4. [Model] 증명서종류코드 (pstmt 1번)
                       + "    TO_CHAR(SYSDATE, 'YYYY'), "      // 5. 발급연도
                       + "    (SELECT NVL(MAX(issue_sequence), 0) + 1 FROM certificate_issue WHERE issue_year = TO_CHAR(SYSDATE, 'YYYY')), " // 6. 순번
                       // 7. 발급번호: 4자리 연도(YYYY) || '-' || 6자리 순번(000001) 자동 생성
                       + "    TO_CHAR(SYSDATE, 'YYYY') || '-' || LPAD((SELECT NVL(MAX(issue_sequence), 0) + 1 FROM certificate_issue WHERE issue_year = TO_CHAR(SYSDATE, 'YYYY')), 6, '0'), "
                       + "    SYSDATE, "                       // 8. 발급일
                       + "    ?, "                             // 9. [Model] 용도 (pstmt 2번)
                       + "    ?, "                             // 10.[Model] 제출처 (pstmt 3번)
                       + "    ?, "                             // 11. 등록자 (pstmt 4번)
                       + "    ?, "                             // 12. 수정자 (pstmt 5번)
                       + "    SYSDATE, "                       // 13. 생성일시
                       + "    SYSDATE "                        // 14. 수정일시
                       + "FROM employee e "
                       + "WHERE e.employee_no = ?";            // 사원번호 조건 (pstmt 6번)

            pstmt = conn.prepareStatement(sql);
            
            // 쿼리의 물음표(?) 순서에 맞게 Model의 값을 세팅합니다.
            pstmt.setString(1, model.getCertificateTypeCode()); 
            pstmt.setString(2, model.getPurpose());             
            pstmt.setString(3, model.getSubmissionTarget());    
            pstmt.setString(4, model.getRegId());               // 등록자
            pstmt.setString(5, model.getRegId());               // 수정자
            pstmt.setString(6, model.getEmployeeNo());          // 사원번호
            result = pstmt.executeUpdate();
            
        } finally {
            JdbcUtil.close(pstmt); // 기존 코드의 자원 반납 방식 사용
        }
        
        return result;
    }
}