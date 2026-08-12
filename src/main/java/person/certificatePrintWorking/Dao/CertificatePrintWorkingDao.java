package person.certificatePrintWorking.Dao;

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
	
	public int insertCertificatePrintWorking(Connection conn, CertificatePrintWorkingModel model) throws SQLException {
	    PreparedStatement pstmt = null;
	    int result = 0;
	    
	    try {
	        // Model(화면)에서 받은 최소한의 데이터만 넣고, 
	        // 필수값(회사ID, 사원ID, 날짜, 시스템계정 등)은 서브쿼리와 오라클 함수(SYSDATE)로 자동 완성합니다.
	        String sql = "INSERT INTO CERTIFICATE_ISSUE ( "
	                   + "    CERTIFICATE_ISSUE_ID, COMPANY_ID, EMPLOYEE_ID, CERTIFICATE_TYPE_CODE, "
	                   + "    ISSUE_YEAR, ISSUE_SEQUENCE, ISSUE_NO, ISSUE_DATE, PURPOSE, "
	                   + "    SUBMISSION_TARGET, REG_ID, MOD_ID, CREATED_AT, UPDATED_AT, CERTIFICATE_YN "
	                   + ") "
	                   + "SELECT "
	                   + "    CERTIFICATE_ISSUE_SEQ.NEXTVAL, " // 1. 발급 고유아이디 (시퀀스)
	                   + "    E.COMPANY_ID, "                  // 2. 회사 아이디 (EMPLOYEE에서 꺼냄)
	                   //필요없으면 회사아이디 삭제
	                   + "    E.EMPLOYEE_ID, "                 // 3. 사원 고유아이디 (EMPLOYEE에서 꺼냄)
	                   + "    ?, "                             // 4. [Model] 증명서종류코드
	                   + "    TO_CHAR(SYSDATE, 'YYYY'), "      // 5. 발급연도 (현재 연도)
	                   // ★ 이 부분이 핵심 수정 사항입니다. (하드코딩 1 ➔ 자동 증가 서브쿼리)
           + "    (SELECT NVL(MAX(ISSUE_SEQUENCE), 0) + 1 FROM CERTIFICATE_ISSUE WHERE ISSUE_YEAR = TO_CHAR(SYSDATE, 'YYYY')), "
	                   + "    ?, "                             // 7. [Model] 발급번호
	                   + "    SYSDATE, "                       // 8. 발급일 (오늘)
	                   + "    ?, "                             // 9. [Model] 용도 (필수값)
	                   + "    ?, "                             // 10.[Model] 제출처
	                   + "    ?, "                      // 11. 등록자 
	                   + "    ?, "                      // 12. 수정자 
	                   + "    SYSDATE, "                       // 13. 생성일시 (오늘)
	                   + "    SYSDATE, "                       // 14. 수정일시 (오늘)
	                   + "    ? "                              // 15. [Model] 상태확인 (Y/N)
	                   + "FROM EMPLOYEE E "
	                   + "WHERE E.EMPLOYEE_NO = ?";            // [Model] 사원번호를 조건으로 조회 (동명이인 방지)

	        pstmt = conn.prepareStatement(sql);
	        
	        // 쿼리의 물음표(?) 순서에 맞게 Model의 값을 세팅합니다.
	        pstmt.setString(1, model.getCertificateTypeCode()); 
	        pstmt.setString(2, model.getIssueNo());             
	        pstmt.setString(3, model.getPurpose());             
	        pstmt.setString(4, model.getSubmissionTarget());    
	        pstmt.setString(5, model.getReg_Id());               // ★ 5번 자리에 등록자(김민수) 추가
	        pstmt.setString(6, model.getReg_Id());               // ★ 6번 자리에 수정자(김민수) 추가
	        pstmt.setString(7, model.getCertificateYn());       // ★ 기존 5번이 7번으로 밀려남
	        pstmt.setString(8, model.getEmployeeNo());          // ★ 기존 6번이 8번으로 밀려남
	        result = pstmt.executeUpdate();
	        
	    } finally {
	        JdbcUtil.close(pstmt); // 기존 코드의 자원 반납 방식 사용
	    }
	    
	    return result;
	}
}


