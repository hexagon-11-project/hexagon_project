package payroll.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import payroll.dto.PayrollDeductionDetailDTO;
import payroll.dto.PayrollEmployeeDTO;
import payroll.dto.PayrollPayDetailDTO;

public class PayrollDAO {

    // ★ [추가됨] 컨트롤러에서 호출하는 귀속연월, 급여차수 기반 리스트 조회
    public List<PayrollEmployeeDTO> getPayrollEmployeeList(Connection conn, String payYearMonth, int paySequence) throws SQLException {
        List<PayrollEmployeeDTO> list = new ArrayList<>();
        
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
                    PayrollEmployeeDTO dto = new PayrollEmployeeDTO();
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
    public List<PayrollEmployeeDTO> selectEmployeeList(Connection conn, Long payrollId) throws SQLException {
        List<PayrollEmployeeDTO> list = new ArrayList<>();
        
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
                    PayrollEmployeeDTO dto = new PayrollEmployeeDTO();
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
    public List<PayrollPayDetailDTO> selectPayDetails(Connection conn, Long payrollEmployeeId) throws SQLException {
        List<PayrollPayDetailDTO> list = new ArrayList<>();
        
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
                    PayrollPayDetailDTO dto = new PayrollPayDetailDTO();
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
    public List<PayrollDeductionDetailDTO> selectDeductionDetails(Connection conn, Long payrollEmployeeId) throws SQLException {
        List<PayrollDeductionDetailDTO> list = new ArrayList<>();
        
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
                    PayrollDeductionDetailDTO dto = new PayrollDeductionDetailDTO();
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
 // ★ [추가됨] 신규추가 모달창용 사원 목록 조회 (이름 검색 지원)
    public List<PayrollEmployeeDTO> getModalEmployeeList(Connection conn, String keyword) throws SQLException {
        List<PayrollEmployeeDTO> list = new ArrayList<>();
        
        // PAYROLL_EMPLOYEE가 아닌, 순수 EMPLOYEE(사원) 테이블에서 조회합니다.
        String sql = "SELECT employee_id, employee_name, employment_type " // (주의: 부서, 직위 등은 DTO에 없어서 임시 생략)
                   + "FROM EMPLOYEE ";
        
        // 검색어가 넘어왔을 경우 WHERE 조건 추가
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
                    PayrollEmployeeDTO dto = new PayrollEmployeeDTO();
                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setEmploymentType(rs.getString("employment_type"));
                    
                    // ★ 만약 화면에 부서(department), 직위(position)도 꼭 띄워야 한다면
                    // 나중에 PayrollEmployeeDTO 파일에 해당 변수들을 추가한 뒤 여기서 rs.getString으로 꺼내 담으시면 됩니다!
                    list.add(dto);
                }
            }
        }
        return list;
    }
}