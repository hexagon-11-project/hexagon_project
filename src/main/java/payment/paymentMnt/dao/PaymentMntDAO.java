package payment.paymentMnt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import payment.paymentMnt.dto.PaymentMntDeductionDetailDTO;
import payment.paymentMnt.dto.PaymentMntEmployeeDTO;
import payment.paymentMnt.dto.PaymentMntPayDetailDTO;

public class PaymentMntDAO {

    // ★ [추가됨] 컨트롤러에서 호출하는 귀속연월, 급여차수 기반 리스트 조회
    public List<PaymentMntEmployeeDTO> getPayrollEmployeeList(Connection conn, String payYearMonth, int paySequence) throws SQLException {
        List<PaymentMntEmployeeDTO> list = new ArrayList<>();
        
        // PAYROLL 테이블(pr)과 조인하여 귀속연월과 급여차수 조건으로 조회합니다.
        String sql = "SELECT p.payroll_employee_id, p.payroll_id, p.employee_id, "
                   + "e.employee_name, e.employment_type, "
                   + "p.total_pay_amount, p.total_deduction_amount, p.net_pay_amount "
                   + "FROM PAYROLL_EMPLOYEE p "
                   + "JOIN EMPLOYEE e ON p.employee_id = e.employee_id "
                   + "JOIN PAYROLL pr ON p.payroll_id = pr.payroll_id "
                   + "WHERE pr.pay_year_month = ? AND pr.pay_sequence = ? "
                   + "ORDER BY e.employee_name";
                   
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payYearMonth);
            pstmt.setInt(2, paySequence);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntEmployeeDTO dto = new PaymentMntEmployeeDTO();
                    dto.setPayrollEmployeeId(rs.getLong("payroll_employee_id"));
                    dto.setPayrollId(rs.getLong("payroll_id"));
                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setEmploymentType(rs.getString("employment_type"));
                    dto.setTotalPayAmount(rs.getLong("total_pay_amount"));
                    dto.setTotalDeductionAmount(rs.getLong("total_deduction_amount"));
                    dto.setNetPayAmount(rs.getLong("net_pay_amount"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 1. 좌측 사원별 급여 요약 리스트 조회 (payroll_id만으로 조회 - 기존 유지)
    public List<PaymentMntEmployeeDTO> selectEmployeeList(Connection conn, Long payrollId) throws SQLException {
        List<PaymentMntEmployeeDTO> list = new ArrayList<>();
        
        // PAYROLL_EMPLOYEE 테이블과 EMPLOYEE 테이블을 조인하여 이름과 고용형태를 가져옵니다.
        String sql = "SELECT p.payroll_employee_id, p.payroll_id, p.employee_id, "
                   + "e.employee_name, e.employment_type, "
                   + "p.total_pay_amount, p.total_deduction_amount, p.net_pay_amount "
                   + "FROM PAYROLL_EMPLOYEE p "
                   + "JOIN EMPLOYEE e ON p.employee_id = e.employee_id "
                   + "WHERE p.payroll_id = ? "
                   + "ORDER BY e.employee_name";
                   
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntEmployeeDTO dto = new PaymentMntEmployeeDTO();
                    dto.setPayrollEmployeeId(rs.getLong("payroll_employee_id"));
                    dto.setPayrollId(rs.getLong("payroll_id"));
                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setEmploymentType(rs.getString("employment_type"));
                    dto.setTotalPayAmount(rs.getLong("total_pay_amount"));
                    dto.setTotalDeductionAmount(rs.getLong("total_deduction_amount"));
                    dto.setNetPayAmount(rs.getLong("net_pay_amount"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 2. 우측 지급 상세 내역 조회
    public List<PaymentMntPayDetailDTO> selectPayDetails(Connection conn, Long payrollEmployeeId) throws SQLException {
        List<PaymentMntPayDetailDTO> list = new ArrayList<>();
        
        // PAYROLL_PAY_DETAIL과 PAY_ITEM을 조인하여 실제 화면에 보일 항목명(item_name)을 가져옵니다.
        String sql = "SELECT d.payroll_pay_detail_id, d.payroll_employee_id, d.pay_item_id, "
                   + "i.item_name, d.amount "
                   + "FROM PAYROLL_PAY_DETAIL d "
                   + "JOIN PAY_ITEM i ON d.pay_item_id = i.pay_item_id "
                   + "WHERE d.payroll_employee_id = ?";
                   
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollEmployeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntPayDetailDTO dto = new PaymentMntPayDetailDTO();
                    dto.setPayrollPayDetailId(rs.getLong("payroll_pay_detail_id"));
                    dto.setPayrollEmployeeId(rs.getLong("payroll_employee_id"));
                    dto.setPayItemId(rs.getLong("pay_item_id"));
                    dto.setItemName(rs.getString("item_name"));
                    dto.setAmount(rs.getLong("amount"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 3. 우측 공제 상세 내역 조회
    public List<PaymentMntDeductionDetailDTO> selectDeductionDetails(Connection conn, Long payrollEmployeeId) throws SQLException {
        List<PaymentMntDeductionDetailDTO> list = new ArrayList<>();
        
        // PAYROLL_DEDUCTION_DETAIL과 DEDUCTION_ITEM을 조인하여 항목명(item_name)을 가져옵니다.
        String sql = "SELECT d.payroll_deduction_detail_id, d.payroll_employee_id, d.deduction_item_id, "
                   + "i.item_name, d.amount "
                   + "FROM PAYROLL_DEDUCTION_DETAIL d "
                   + "JOIN DEDUCTION_ITEM i ON d.deduction_item_id = i.deduction_item_id "
                   + "WHERE d.payroll_employee_id = ?";
                   
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollEmployeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntDeductionDetailDTO dto = new PaymentMntDeductionDetailDTO();
                    dto.setPayrollDeductionDetailId(rs.getLong("payroll_deduction_detail_id"));
                    dto.setPayrollEmployeeId(rs.getLong("payroll_employee_id"));
                    dto.setDeductionItemId(rs.getLong("deduction_item_id"));
                    dto.setItemName(rs.getString("item_name"));
                    dto.setAmount(rs.getLong("amount"));
                    list.add(dto);
                }
            }
        }
        return list;
    }
 // ★ [추가됨] // ★ [수정완료] 신규추가 모달창용 사원 목록 조회 (실제 DB 컬럼명 적용)
    public List<PaymentMntEmployeeDTO> getModalEmployeeList(Connection conn, String keyword) throws SQLException {
        List<PaymentMntEmployeeDTO> list = new ArrayList<>();
        
        // 1. SELECT 쿼리에 부서, 직위, 상태와 함께 'BASE_WAGE_AMOUNT'(기본급)를 추가합니다.
        String sql = "SELECT employee_id, employee_name, employment_type, " 
                   + "DEPARTMENT, POSITION, BASE_WAGE_AMOUNT " 
                   + "FROM EMPLOYEE ";
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += "WHERE employee_name LIKE ? ";
        }
        sql += "ORDER BY employee_name";
                    
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                pstmt.setString(1, "%" + keyword.trim() + "%");
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntEmployeeDTO dto = new PaymentMntEmployeeDTO();
                    
                    // 기존 데이터
                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setEmploymentType(rs.getString("employment_type"));
                    
                    // 2. 바구니에 담을 때도 진짜 컬럼명으로 정확하게 꺼냅니다!
                    dto.setDepartment(rs.getString("DEPARTMENT"));
                    dto.setPosition(rs.getString("POSITION"));
                    
                    // ★ 추가완료: DB에서 기본급 데이터를 꺼내서 DTO에 담아줍니다!
                    dto.setBaseWageAmount(rs.getLong("BASE_WAGE_AMOUNT"));
                    
                    list.add(dto);
                }
            }
        }
        return list;
    }
 // 선택된 사원들을 급여 대장에 추가하는 DAO 메서드 예시
    public void insertPayrollEmployees(Connection conn, Long payrollId, List<String> empIds) throws SQLException {
        String sql = "INSERT INTO 급여사원테이블명 (payroll_id, emp_id) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String empId : empIds) {
                pstmt.setLong(1, payrollId);
                pstmt.setString(2, empId);
                pstmt.addBatch(); // 여러명을 한 번에 처리하기 위해 batch 사용
            }
            pstmt.executeBatch(); // 일괄 실행
        }
    }
}