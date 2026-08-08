package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import config.employee.model.Employee;
import jdbc.JdbcUtil;

public class EmployeeDao {

    // 1. 현재 저장된 사원번호 중 가장 큰 값을 조회한다 (없으면 null).
    // 주의: 여기서는 순수하게 "현재 최댓값"만 리턴한다. 다음 번호 계산(+1)은
    // Service의 calcNextEmpNo()에서 한 곳에서만 처리한다 (여기서 미리 +1까지 해버리면
    // Service에서 또 +1이 되어 번호가 두 개씩 건너뛰는 문제가 생긴다).
    public String selectMaxEmpNo(Connection conn) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT MAX(TO_NUMBER(REPLACE(EMPLOYEE_NO, 'No-', ''))) FROM EMPLOYEE WHERE EMPLOYEE_NO LIKE 'No-%'";
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                String maxNum = rs.getString(1);
                if (maxNum != null) {
                    return "No-" + maxNum;
                }
            }
            return null; // 데이터가 아직 없음
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(stmt);
        }
    }

    // 2. 1페이지 사원 기본 정보 INSERT (REG_ID 필수값 포함 완벽 반영)
    public void insert(Connection conn, Employee emp) throws SQLException {
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "INSERT INTO EMPLOYEE ("
                       + "  EMPLOYEE_ID, COMPANY_ID, EMPLOYEE_NO, EMPLOYMENT_TYPE, EMPLOYEE_NAME, "
                       + "  EMPLOYEE_NAME_EN, HIRE_DATE, RESIGN_DATE, DEPARTMENT, POSITION, "
                       + "  DOM_FOR_YN, RESIDENT_REG_NO, PHONE, MOBILE, EMAIL, "
                       + "  SNS, BANK_NAME, BANK_ACCOUNT, EMP_INCOME_TYPE, BASE_WAGE_AMOUNT, "
                       + "  NATIONAL_PENSION_BASE_AMOUNT, HEALTH_INSURANCE_BASE_AMOUNT, EMPLOYMENT_INSURANCE_AMOUNT, "
                       + "  REG_ID, MOD_ID"
                       + ") VALUES ("
                       + "  EMPLOYEE_SEQ.NEXTVAL, 1001, ?, ?, ?, "
                       + "  ?, ?, ?, ?, ?, "
                       + "  ?, ?, ?, ?, ?, "
                       + "  ?, ?, ?, ?, ?, "
                       + "  ?, ?, ?, "
                       + "  'SYSTEM', 'SYSTEM'"
                       + ")";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, emp.getEmployeeNo());     // EMPLOYEE_NO
            pstmt.setString(2, emp.getEmploymentType()); // 고용형태
            pstmt.setString(3, emp.getEmployeeName());   // 성명(한글)
            pstmt.setString(4, emp.getEmployeeNameEn()); // 성명(영문)
            pstmt.setDate(5, emp.getHireDate() != null ? new java.sql.Date(emp.getHireDate().getTime()) : null);     // 입사일
            pstmt.setDate(6, emp.getResignDate() != null ? new java.sql.Date(emp.getResignDate().getTime()) : null); // 퇴사일
            pstmt.setString(7, emp.getDepartment());     // 부서
            pstmt.setString(8, emp.getPosition());       // 직위
            pstmt.setString(9, emp.getDomForYn());       // 내/외국인
            pstmt.setString(10, emp.getResidentRegNo()); // 주민번호
            pstmt.setString(11, emp.getPhone());         // 전화번호
            pstmt.setString(12, emp.getMobile());        // 휴대폰
            pstmt.setString(13, emp.getEmail());         // 이메일
            pstmt.setString(14, emp.getSns());           // SNS
            pstmt.setString(15, emp.getBankName());      // 급여은행
            pstmt.setString(16, emp.getBankAccount());   // 계좌번호
            pstmt.setString(17, emp.getEmpIncomeType());  // EMP_INCOME_TYPE (폼에서 선택한 갑근세 구분)
            pstmt.setInt(18, emp.getBaseWageAmount());   // BASE_WAGE_AMOUNT (int형)
            pstmt.setInt(19, emp.getNationalPensionBaseAmount()); // 국민연금 기준소득월액
            pstmt.setInt(20, emp.getHealthInsuranceBaseAmount()); // 건강보험 보수월액
            pstmt.setInt(21, emp.getEmploymentInsuranceAmount()); // 고용보험 보수월액
            pstmt.executeUpdate();

            // 방금 INSERT에 쓴 시퀀스 값을 같은 커넥션(같은 세션)에서 다시 읽어온다.
            // 이 값이 부양가족/학력/경력 등 하위 테이블의 EMPLOYEE_ID(FK)로 쓰일 진짜 PK다.
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT EMPLOYEE_SEQ.CURRVAL FROM DUAL");
            if (rs.next()) {
                emp.setEmployeeId(rs.getInt(1));
            }
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(stmt);
            JdbcUtil.close(pstmt);
        }
    }

    // 3. 사원번호로 1명의 사원 정보를 SELECT (2페이지 진입 시 데이터 불러오기용)
    public Employee selectEmployee(Connection conn, String employeeNo) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Employee emp = null;
        try {
            String sql = "SELECT * FROM EMPLOYEE WHERE EMPLOYEE_NO = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employeeNo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                emp = new Employee();
                emp.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
                emp.setEmployeeNo(rs.getString("EMPLOYEE_NO"));
                emp.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
                emp.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
                emp.setEmployeeNameEn(rs.getString("EMPLOYEE_NAME_EN"));
                emp.setHireDate(rs.getDate("HIRE_DATE"));
                emp.setResignDate(rs.getDate("RESIGN_DATE"));
                emp.setDepartment(rs.getString("DEPARTMENT"));
                emp.setPosition(rs.getString("POSITION"));
                emp.setDomForYn(rs.getString("DOM_FOR_YN"));
                emp.setResidentRegNo(rs.getString("RESIDENT_REG_NO"));
                emp.setPhone(rs.getString("PHONE"));
                emp.setMobile(rs.getString("MOBILE"));
                emp.setEmail(rs.getString("EMAIL"));
                emp.setSns(rs.getString("SNS"));
                emp.setBankName(rs.getString("BANK_NAME"));
                emp.setBankAccount(rs.getString("BANK_ACCOUNT"));
                emp.setRetirementTypeCode(rs.getString("RETIREMENT_TYPE_CODE"));
                emp.setRetirementReason(rs.getString("RETIREMENT_REASON"));
                emp.setPostRetirementPhone(rs.getString("POST_RETIREMENT_PHONE"));
                emp.setNationalPensionBaseAmount(rs.getInt("NATIONAL_PENSION_BASE_AMOUNT"));
                emp.setHealthInsuranceBaseAmount(rs.getInt("HEALTH_INSURANCE_BASE_AMOUNT"));
                emp.setEmploymentInsuranceAmount(rs.getInt("EMPLOYMENT_INSURANCE_AMOUNT"));
            }
            return emp;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }

    // 4. 2페이지 추가 정보 저장용 메서드
    public int insertAdditionalInfo(Connection conn, String employeeNo) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "UPDATE EMPLOYEE SET MOD_ID = 'SYSTEM' WHERE EMPLOYEE_NO = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employeeNo);
            return pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 5. 2페이지의 퇴직 섹션 저장용 - 퇴직일자는 1페이지의 RESIGN_DATE 컬럼을 그대로 같이 갱신한다
    public int updateRetirementInfo(Connection conn, int employeeId, String typeCode, java.sql.Date resignDate,
            String reason, String phone) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "UPDATE EMPLOYEE SET "
                       + "RETIREMENT_TYPE_CODE = ?, RESIGN_DATE = ?, RETIREMENT_REASON = ?, "
                       + "POST_RETIREMENT_PHONE = ?, MOD_ID = 'SYSTEM', UPDATED_AT = SYSDATE "
                       + "WHERE EMPLOYEE_ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, typeCode);
            pstmt.setDate(2, resignDate);
            pstmt.setString(3, reason);
            pstmt.setString(4, phone);
            pstmt.setInt(5, employeeId);
            return pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }
}