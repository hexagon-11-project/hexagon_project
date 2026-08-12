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
import payment.paymentMnt.dto.PaymentMntPayItemDTO;
import payment.paymentMnt.dto.PaymentMntDeductionItemDTO;
import payment.paymentMnt.dto.PaymentMntSummaryDTO;

public class PaymentMntDAO {

    public List<PaymentMntEmployeeDTO> getPayrollEmployeeList(Connection conn, String payYearMonth, int paySequence) throws SQLException {
        List<PaymentMntEmployeeDTO> list = new ArrayList<>();
        String sql = "SELECT p.payroll_employee_id, p.payroll_id, p.employee_id, e.employee_name, e.employment_type, e.department,"
                   + "p.total_pay_amount, p.total_deduction_amount, p.net_pay_amount "
                   + "FROM PAYROLL_EMPLOYEE p JOIN EMPLOYEE e ON p.employee_id = e.employee_id "
                   + "JOIN PAYROLL pr ON p.payroll_id = pr.payroll_id "
                   + "WHERE pr.pay_year_month = ? AND pr.pay_sequence = ? ORDER BY e.employee_name";
        
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
                    dto.setDepartment(rs.getString("department"));
                    
                    // 금액 3종 세팅
                    dto.setTotalPayAmount(rs.getLong("total_pay_amount"));
                    dto.setTotalDeductionAmount(rs.getLong("total_deduction_amount"));
                    dto.setNetPayAmount(rs.getLong("net_pay_amount"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    public List<PaymentMntEmployeeDTO> selectEmployeeList(Connection conn, Long payrollId) throws SQLException {
        List<PaymentMntEmployeeDTO> list = new ArrayList<>();
        String sql = "SELECT p.payroll_employee_id, p.payroll_id, p.employee_id, e.employee_name, e.employment_type, "
                   + "e.department, " 
                   + "p.total_pay_amount, p.total_deduction_amount, p.net_pay_amount "
                   + "FROM PAYROLL_EMPLOYEE p JOIN EMPLOYEE e ON p.employee_id = e.employee_id "
                   + "WHERE p.payroll_id = ? ORDER BY e.employee_name";
                   
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
                    dto.setDepartment(rs.getString("department"));
                    
                    // 금액 3종 세팅
                    dto.setTotalPayAmount(rs.getLong("total_pay_amount"));
                    dto.setTotalDeductionAmount(rs.getLong("total_deduction_amount"));
                    dto.setNetPayAmount(rs.getLong("net_pay_amount"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    public List<PaymentMntPayDetailDTO> selectPayDetails(Connection conn, Long payrollEmployeeId) throws SQLException {
        List<PaymentMntPayDetailDTO> list = new ArrayList<>();
        String sql = "SELECT d.payroll_pay_detail_id, d.payroll_employee_id, d.pay_item_id, i.pay_item_name as item_name, d.amount "
                   + "FROM PAYROLL_PAY_DETAIL d JOIN PAY_ITEM i ON d.pay_item_id = i.pay_item_id "
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

    public List<PaymentMntDeductionDetailDTO> selectDeductionDetails(Connection conn, Long payrollEmployeeId) throws SQLException {
        List<PaymentMntDeductionDetailDTO> list = new ArrayList<>();
        String sql = "SELECT PAYROLL_DEDUCTION_DETAIL_ID, PAYROLL_EMPLOYEE_ID, DEDUCTION_ITEM_ID, AMOUNT "
                   + "FROM PAYROLL_DEDUCTION_DETAIL "
                   + "WHERE PAYROLL_EMPLOYEE_ID = ?";
                   
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollEmployeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntDeductionDetailDTO dto = new PaymentMntDeductionDetailDTO();
                    dto.setPayrollDeductionDetailId(rs.getLong("PAYROLL_DEDUCTION_DETAIL_ID"));
                    dto.setPayrollEmployeeId(rs.getLong("PAYROLL_EMPLOYEE_ID"));
                    dto.setDeductionItemId(rs.getLong("DEDUCTION_ITEM_ID"));
                    dto.setAmount(rs.getLong("AMOUNT"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    public List<PaymentMntEmployeeDTO> getModalEmployeeList(Connection conn, String keyword) throws SQLException {
        List<PaymentMntEmployeeDTO> list = new ArrayList<>();
        String sql = "SELECT employee_id, employee_name, employment_type, DEPARTMENT, POSITION, BASE_WAGE_AMOUNT FROM EMPLOYEE ";
        if (keyword != null && !keyword.trim().isEmpty()) { sql += "WHERE employee_name LIKE ? "; }
        sql += "ORDER BY employee_name";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (keyword != null && !keyword.trim().isEmpty()) { pstmt.setString(1, "%" + keyword.trim() + "%"); }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntEmployeeDTO dto = new PaymentMntEmployeeDTO();
                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setEmploymentType(rs.getString("employment_type"));
                    dto.setDepartment(rs.getString("DEPARTMENT"));
                    dto.setPosition(rs.getString("POSITION"));
                    dto.setBaseWageAmount(rs.getLong("BASE_WAGE_AMOUNT"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    public List<PaymentMntEmployeeDTO> getModalEmployeeList(Connection conn, String keyword, int limit, int offset, String department, String position, String status) throws SQLException {
        List<PaymentMntEmployeeDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT * FROM ( ");
        sql.append("  SELECT ROWNUM AS RNUM, A.* FROM ( ");
        sql.append("    SELECT employee_id, employee_name, employment_type, DEPARTMENT, POSITION, BASE_WAGE_AMOUNT, RESIGN_DATE ");
        sql.append("    FROM EMPLOYEE WHERE 1=1 ");
        
        if (keyword != null && !keyword.trim().isEmpty()) { sql.append(" AND employee_name LIKE ? "); }
        if (department != null && !department.trim().isEmpty()) { sql.append(" AND DEPARTMENT = ? "); }
        if (position != null && !position.trim().isEmpty()) { sql.append(" AND POSITION = ? "); }
        
        if ("재직".equals(status)) { sql.append(" AND RESIGN_DATE IS NULL "); } 
        else if ("퇴직".equals(status)) { sql.append(" AND RESIGN_DATE IS NOT NULL "); }

        sql.append("    ORDER BY employee_name ");
        sql.append("  ) A WHERE ROWNUM <= ? ");
        sql.append(") WHERE RNUM > ? ");
                    
        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (keyword != null && !keyword.trim().isEmpty()) { pstmt.setString(paramIndex++, "%" + keyword.trim() + "%"); }
            if (department != null && !department.trim().isEmpty()) { pstmt.setString(paramIndex++, department); }
            if (position != null && !position.trim().isEmpty()) { pstmt.setString(paramIndex++, position); }
            
            pstmt.setInt(paramIndex++, offset + limit); 
            pstmt.setInt(paramIndex++, offset);         
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntEmployeeDTO dto = new PaymentMntEmployeeDTO();
                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setEmploymentType(rs.getString("employment_type"));
                    dto.setDepartment(rs.getString("DEPARTMENT"));
                    dto.setPosition(rs.getString("POSITION"));
                    dto.setBaseWageAmount(rs.getLong("BASE_WAGE_AMOUNT"));
                    
                    String empStatus = (rs.getString("RESIGN_DATE") == null) ? "재직" : "퇴직";
                    dto.setStatus(empStatus); 
                    list.add(dto);
                }
            }
        }
        return list;
    }

    public int getModalEmployeeCount(Connection conn, String keyword, String department, String position, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM EMPLOYEE WHERE 1=1 ");
        
        if (keyword != null && !keyword.trim().isEmpty()) { sql.append(" AND employee_name LIKE ? "); }
        if (department != null && !department.trim().isEmpty()) { sql.append(" AND DEPARTMENT = ? "); }
        if (position != null && !position.trim().isEmpty()) { sql.append(" AND POSITION = ? "); }
        
        if ("재직".equals(status)) { sql.append(" AND RESIGN_DATE IS NULL "); } 
        else if ("퇴직".equals(status)) { sql.append(" AND RESIGN_DATE IS NOT NULL "); }
        
        int count = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (keyword != null && !keyword.trim().isEmpty()) { pstmt.setString(paramIndex++, "%" + keyword.trim() + "%"); }
            if (department != null && !department.trim().isEmpty()) { pstmt.setString(paramIndex++, department); }
            if (position != null && !position.trim().isEmpty()) { pstmt.setString(paramIndex++, position); }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { count = rs.getInt(1); }
            }
        }
        return count;
    }

    public void insertPayrollEmployees(Connection conn, Long payrollId, List<String> empIds) throws SQLException {
        // 1. PAYROLL_EMPLOYEE 테이블에 사원 먼저 등록
        String insertEmpSql = "INSERT INTO PAYROLL_EMPLOYEE ("
                   + "    payroll_employee_id, payroll_id, employee_id, "
                   + "    employment_type, income_type, "
                   + "    total_pay_amount, total_deduction_amount, net_pay_amount, reg_id, mod_id"
                   + ") "
                   + "SELECT "
                   + "    (SELECT NVL(MAX(payroll_employee_id), 0) FROM PAYROLL_EMPLOYEE) + ROWNUM, "
                   + "    ?, "
                   + "    e.employee_id, "
                   + "    e.employment_type, "
                   + "    '일반', "        
                   + "    NVL(e.base_wage_amount, 0), "  
                   + "    0, "            
                   + "    NVL(e.base_wage_amount, 0), "  
                   + "    'admin', "      
                   + "    'admin' "        
                   + "FROM EMPLOYEE e "
                   + "WHERE e.employee_id = ? "
                   + "  AND NOT EXISTS ("
                   + "      SELECT 1 FROM PAYROLL_EMPLOYEE p "
                   + "      WHERE p.payroll_id = ? AND p.employee_id = e.employee_id"
                   + "  )";

     // 2. PAYROLL_PAY_DETAIL 테이블에 지급항목 등록 (고용형태에 따라 분기)
     //    - DAILY(일용직) : '일용급여' 항목에 등록
     //    - 그 외(REGULAR/CONTRACT 등) : '기본급'(2801) 항목에 등록
        String insertPayDetailSql = "INSERT INTO PAYROLL_PAY_DETAIL ("
                   + "    payroll_pay_detail_id, payroll_employee_id, pay_item_id, amount, reg_id, mod_id"
                   + ") "
                   + "SELECT "
                   + "    (SELECT NVL(MAX(payroll_pay_detail_id), 0) FROM PAYROLL_PAY_DETAIL) + 1, "
                   + "    p.payroll_employee_id, "
                   + "    CASE WHEN e.employment_type = 'DAILY' "
                   + "         THEN (SELECT PAY_ITEM_ID FROM PAY_ITEM WHERE PAY_ITEM_NAME = '일용급여') "
                   + "         ELSE 2801 END, " // ★ 일용직은 일용급여 항목, 그 외는 기본급(2801)
                   + "    e.base_wage_amount, "
                   + "    'admin', 'admin' "
                   + "FROM PAYROLL_EMPLOYEE p JOIN EMPLOYEE e ON p.employee_id = e.employee_id "
                   + "WHERE p.payroll_id = ? AND p.employee_id = ? "
                   + "  AND NOT EXISTS (" 
                   + "      SELECT 1 FROM PAYROLL_PAY_DETAIL d "
                   + "      WHERE d.payroll_employee_id = p.payroll_employee_id "
                   + "        AND d.pay_item_id = CASE WHEN e.employment_type = 'DAILY' "
                   + "             THEN (SELECT PAY_ITEM_ID FROM PAY_ITEM WHERE PAY_ITEM_NAME = '일용급여') "
                   + "             ELSE 2801 END"
                   + "  )";

        try (PreparedStatement pstmtEmp = conn.prepareStatement(insertEmpSql);
             PreparedStatement pstmtDetail = conn.prepareStatement(insertPayDetailSql)) {
            
            for (String empId : empIds) {
                if (empId == null || empId.trim().isEmpty()) continue;
                
                pstmtEmp.setLong(1, payrollId);         
                pstmtEmp.setString(2, empId.trim());    
                pstmtEmp.setLong(3, payrollId);         
                pstmtEmp.executeUpdate();  

                pstmtDetail.setLong(1, payrollId);
                pstmtDetail.setString(2, empId.trim());
                pstmtDetail.executeUpdate();
            }
        }
    }

    public List<String> getDepartmentList(Connection conn) throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT DEPARTMENT FROM EMPLOYEE WHERE DEPARTMENT IS NOT NULL ORDER BY DEPARTMENT";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) { list.add(rs.getString("DEPARTMENT")); }
        }
        return list;
    }

    public List<String> getPositionList(Connection conn) throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT POSITION FROM EMPLOYEE WHERE POSITION IS NOT NULL ORDER BY POSITION";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) { list.add(rs.getString("POSITION")); }
        }
        return list;
    }

    // 지급 항목 마스터 조회
 // 지급 항목 마스터 조회
    public List<PaymentMntPayItemDTO> selectPayItemList(Connection conn) throws SQLException {
        List<PaymentMntPayItemDTO> list = new ArrayList<>();
        // ★ 쿼리에 BULK_PAY_AMOUNT 추가
        String sql = "SELECT PAY_ITEM_ID, PAY_ITEM_NAME, CALCULATION_METHOD, BULK_PAY_AMOUNT FROM PAY_ITEM WHERE USE_YN = 'Y' ORDER BY DISPLAY_ORDER";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                PaymentMntPayItemDTO dto = new PaymentMntPayItemDTO();
                dto.setPayItemId(rs.getInt("PAY_ITEM_ID"));
                dto.setPayItemName(rs.getString("PAY_ITEM_NAME"));
                dto.setCalculationMethod(rs.getString("CALCULATION_METHOD"));
                // ★ 식대 20만 원 가져오기
                dto.setBulkPayAmount(rs.getLong("BULK_PAY_AMOUNT")); 
                list.add(dto);
            }
        }
        return list;
    }

    // 공제 항목 마스터 조회
 // 공제 항목 마스터 조회
    public List<PaymentMntDeductionItemDTO> selectDeductionItemList(Connection conn) throws SQLException {
        List<PaymentMntDeductionItemDTO> list = new ArrayList<>();
        // ★ 수정: ITEM_NAME을 DEDUCTION_ITEM_NAME으로 변경
        String sql = "SELECT DEDUCTION_ITEM_ID, DEDUCTION_ITEM_NAME, CALCULATION_METHOD FROM DEDUCTION_ITEM WHERE USE_YN = 'Y' ORDER BY DISPLAY_ORDER";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                PaymentMntDeductionItemDTO dto = new PaymentMntDeductionItemDTO();
                dto.setDeductionItemId(rs.getInt("DEDUCTION_ITEM_ID"));
                
                // ★ 수정: 값을 꺼내올 때도 DEDUCTION_ITEM_NAME으로 변경
                dto.setDeductionItemName(rs.getString("DEDUCTION_ITEM_NAME")); 
                dto.setCalculationMethod(rs.getString("CALCULATION_METHOD"));
                list.add(dto);
            }
        }
        return list;
    }
    
    public void upsertPayDetail(Connection conn, Long empId, Integer itemId, Long amount) throws SQLException {
        String updateSql = "UPDATE PAYROLL_PAY_DETAIL SET AMOUNT = ?, MOD_ID = 'admin' WHERE PAYROLL_EMPLOYEE_ID = ? AND PAY_ITEM_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setLong(1, amount);
            pstmt.setLong(2, empId);
            pstmt.setInt(3, itemId);
            int count = pstmt.executeUpdate();

            if (count == 0) { // 수정된 게 없다면 (기존 데이터가 없다는 뜻이므로 INSERT)
                String insertSql = "INSERT INTO PAYROLL_PAY_DETAIL (PAYROLL_PAY_DETAIL_ID, PAYROLL_EMPLOYEE_ID, PAY_ITEM_ID, AMOUNT, REG_ID, MOD_ID) "
                                 + "VALUES ((SELECT NVL(MAX(PAYROLL_PAY_DETAIL_ID), 0) + 1 FROM PAYROLL_PAY_DETAIL), ?, ?, ?, 'admin', 'admin')";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setLong(1, empId);
                    insertStmt.setInt(2, itemId);
                    insertStmt.setLong(3, amount);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    // 2. 공제항목 갱신 (있으면 수정, 없으면 새로 추가)
    public void upsertDeductionDetail(Connection conn, Long empId, Integer itemId, Long amount) throws SQLException {
        String updateSql = "UPDATE PAYROLL_DEDUCTION_DETAIL SET AMOUNT = ?, MOD_ID = 'admin' WHERE PAYROLL_EMPLOYEE_ID = ? AND DEDUCTION_ITEM_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setLong(1, amount);
            pstmt.setLong(2, empId);
            pstmt.setInt(3, itemId);
            int count = pstmt.executeUpdate();

            if (count == 0) {
                String insertSql = "INSERT INTO PAYROLL_DEDUCTION_DETAIL (PAYROLL_DEDUCTION_DETAIL_ID, PAYROLL_EMPLOYEE_ID, DEDUCTION_ITEM_ID, AMOUNT, REG_ID, MOD_ID) "
                                 + "VALUES ((SELECT NVL(MAX(PAYROLL_DEDUCTION_DETAIL_ID), 0) + 1 FROM PAYROLL_DEDUCTION_DETAIL), ?, ?, ?, 'admin', 'admin')";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setLong(1, empId);
                    insertStmt.setInt(2, itemId);
                    insertStmt.setLong(3, amount);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    // 3. 사원 마스터(PAYROLL_EMPLOYEE) 총 금액 갱신
    public void updateEmployeeTotals(Connection conn, Long empId, long totalPay, long totalDed, long netPay) throws SQLException {
        String sql = "UPDATE PAYROLL_EMPLOYEE SET TOTAL_PAY_AMOUNT = ?, TOTAL_DEDUCTION_AMOUNT = ?, NET_PAY_AMOUNT = ? WHERE PAYROLL_EMPLOYEE_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, totalPay);
            pstmt.setLong(2, totalDed);
            pstmt.setLong(3, netPay);
            pstmt.setLong(4, empId);
            pstmt.executeUpdate();
        }
    }
    
 // 1. 기존 데이터 깔끔하게 지우기 (외래키 오류 방지를 위해 하위 테이블부터 삭제)
    public void deletePayrollEmployeesByPeriod(Connection conn, String currYearMonth, int currSeq) throws SQLException {
        // 공제 상세내역 삭제
        String delDed = "DELETE FROM PAYROLL_DEDUCTION_DETAIL WHERE PAYROLL_EMPLOYEE_ID IN (SELECT PAYROLL_EMPLOYEE_ID FROM PAYROLL_EMPLOYEE p JOIN PAYROLL pr ON p.PAYROLL_ID = pr.PAYROLL_ID WHERE pr.PAY_YEAR_MONTH = ? AND pr.PAY_SEQUENCE = ?)";
        try(PreparedStatement pstmt = conn.prepareStatement(delDed)) {
            pstmt.setString(1, currYearMonth);
            pstmt.setInt(2, currSeq);
            pstmt.executeUpdate();
        }
        
        // 지급 상세내역 삭제
        String delPay = "DELETE FROM PAYROLL_PAY_DETAIL WHERE PAYROLL_EMPLOYEE_ID IN (SELECT PAYROLL_EMPLOYEE_ID FROM PAYROLL_EMPLOYEE p JOIN PAYROLL pr ON p.PAYROLL_ID = pr.PAYROLL_ID WHERE pr.PAY_YEAR_MONTH = ? AND pr.PAY_SEQUENCE = ?)";
        try(PreparedStatement pstmt = conn.prepareStatement(delPay)) {
            pstmt.setString(1, currYearMonth);
            pstmt.setInt(2, currSeq);
            pstmt.executeUpdate();
        }
        
        // 급여 대상자 목록 삭제
        String delEmp = "DELETE FROM PAYROLL_EMPLOYEE WHERE PAYROLL_ID = (SELECT PAYROLL_ID FROM PAYROLL WHERE PAY_YEAR_MONTH = ? AND PAY_SEQUENCE = ?)";
        try(PreparedStatement pstmt = conn.prepareStatement(delEmp)) {
            pstmt.setString(1, currYearMonth);
            pstmt.setInt(2, currSeq);
            pstmt.executeUpdate();
        }
    }

    // 2. 복사 후 쿼리가 적용된 개수(행의 수) 반환
    public int copyPreviousEmployeesCount(Connection conn, String prevYearMonth, int prevSeq, String currYearMonth, int currSeq) throws SQLException {
        String sql = "INSERT INTO PAYROLL_EMPLOYEE ("
                   + "    PAYROLL_EMPLOYEE_ID, PAYROLL_ID, EMPLOYEE_ID, EMPLOYMENT_TYPE, INCOME_TYPE, "
                   + "    TOTAL_PAY_AMOUNT, TOTAL_DEDUCTION_AMOUNT, NET_PAY_AMOUNT, REG_ID, MOD_ID"
                   + ") "
                   + "SELECT "
                   + "    (SELECT NVL(MAX(PAYROLL_EMPLOYEE_ID), 0) FROM PAYROLL_EMPLOYEE) + ROWNUM, "
                   + "    curr_p.PAYROLL_ID, prev_e.EMPLOYEE_ID, prev_e.EMPLOYMENT_TYPE, prev_e.INCOME_TYPE, "
                   + "    prev_e.TOTAL_PAY_AMOUNT, prev_e.TOTAL_DEDUCTION_AMOUNT, prev_e.NET_PAY_AMOUNT, "
                   + "    'admin', 'admin' "
                   + "FROM PAYROLL_EMPLOYEE prev_e "
                   + "JOIN PAYROLL prev_p ON prev_e.PAYROLL_ID = prev_p.PAYROLL_ID "
                   + "JOIN PAYROLL curr_p ON curr_p.PAY_YEAR_MONTH = ? AND curr_p.PAY_SEQUENCE = ? "
                   + "WHERE prev_p.PAY_YEAR_MONTH = ? AND prev_p.PAY_SEQUENCE = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, currYearMonth);
            pstmt.setInt(2, currSeq);
            pstmt.setString(3, prevYearMonth);
            pstmt.setInt(4, prevSeq);
            
            // executeUpdate()는 DB에 삽입된 행(row)의 개수를 정수로 반환합니다 (이것이 8건의 정체입니다)
            return pstmt.executeUpdate(); 
        }
    }
    
 // ★ 급여 마스터(PAYROLL) 테이블에 해당 연월 폴더가 없으면 새로 생성해주는 메서드
    public void ensurePayrollExists(Connection conn, String yearMonth, int seq) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM PAYROLL WHERE PAY_YEAR_MONTH = ? AND PAY_SEQUENCE = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, yearMonth);
            pstmt.setInt(2, seq);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // ★ 수정된 부분: SYSDATE를 추가하여 날짜 빈칸(NULL) 에러 방지
                    String insertSql = "INSERT INTO PAYROLL (PAYROLL_ID, COMPANY_ID, PAY_YEAR_MONTH, PAY_SEQUENCE, SETTLEMENT_START_DATE, SETTLEMENT_END_DATE, PAYMENT_DATE, REG_ID, MOD_ID) "
                                     + "VALUES ((SELECT NVL(MAX(PAYROLL_ID), 0) + 1 FROM PAYROLL), 1001, ?, ?, SYSDATE, SYSDATE, SYSDATE, 'admin', 'admin')";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, yearMonth);
                        insertStmt.setInt(2, seq);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }
    
 // ★ 귀속연월 + 급여차수에 해당하는 정확한 PAYROLL_ID 조회
    // (신규 사원 추가 시, 화면에서 JS가 엉뚱한 payrollId를 보내는 버그 방지용)
    public Long selectPayrollId(Connection conn, String payYearMonth, int paySequence) throws SQLException {
        String sql = "SELECT PAYROLL_ID FROM PAYROLL WHERE PAY_YEAR_MONTH = ? AND PAY_SEQUENCE = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payYearMonth);
            pstmt.setInt(2, paySequence);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("PAYROLL_ID");
                }
            }
        }
        return null;
    }
    

 // ★ 하단 [급여 종합정보] 집계 조회
    // - 월 합계 : 재직중(RESIGN_DATE IS NULL)인 사원 수
    // - 지급/공제/실지급 총액 : 선택한 귀속연월(payYearMonth) + 급여차수(paySequence)에 등록된
    //   PAYROLL_EMPLOYEE 전체 사원의 금액을 합산 (사원 급여정보가 저장/수정될 때마다
    //   PAYROLL_EMPLOYEE.TOTAL_PAY_AMOUNT / TOTAL_DEDUCTION_AMOUNT 가 같이 갱신되므로,
    //   화면을 다시 조회할 때마다 최신값이 자동 반영됨)
    public PaymentMntSummaryDTO selectPayrollSummary(Connection conn, String payYearMonth, int paySequence) throws SQLException {
        PaymentMntSummaryDTO summary = new PaymentMntSummaryDTO();

        // 1. 월 합계 - 재직중인 사원 수 (별도 쿼리로 분리)
        String countSql = "SELECT COUNT(*) AS TOTAL_COUNT FROM EMPLOYEE WHERE RESIGN_DATE IS NULL";
        try (PreparedStatement pstmt = conn.prepareStatement(countSql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                summary.setTotalCount(rs.getInt("TOTAL_COUNT"));
            }
        }

        // 2. 지급/공제/실지급 총액 - 선택한 귀속연월+급여차수 기준 집계
        String sql = "SELECT "
                   + "    NVL(SUM(pe.TOTAL_PAY_AMOUNT), 0) AS TOTAL_GIVE_AMOUNT, "
                   + "    NVL(SUM(pe.TOTAL_DEDUCTION_AMOUNT), 0) AS TOTAL_DEDU_AMOUNT "
                   + "FROM PAYROLL_EMPLOYEE pe "
                   + "JOIN PAYROLL pr ON pe.PAYROLL_ID = pr.PAYROLL_ID "
                   + "WHERE pr.PAY_YEAR_MONTH = ? AND pr.PAY_SEQUENCE = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payYearMonth);
            pstmt.setInt(2, paySequence);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long totalGive = rs.getLong("TOTAL_GIVE_AMOUNT");
                    long totalDedu = rs.getLong("TOTAL_DEDU_AMOUNT");

                    summary.setTotalGiveAmount(totalGive);
                    summary.setTotalDeduAmount(totalDedu);
                    summary.setTotalRealAmount(totalGive - totalDedu); // 실지급액 = 지급총액 - 공제총액
                }
            }
        }

        return summary;
    }
}