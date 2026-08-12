package payment.paymentMntDayWorker.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import payment.paymentMntDayWorker.dto.PaymentMntDayWorkerDeductionItemDTO;
import payment.paymentMntDayWorker.dto.PaymentMntDayWorkerEmployeeDTO;
import payment.paymentMntDayWorker.model.PaymentMntDayWorkerDailyVO;
import payment.paymentMntDayWorker.model.PaymentMntDayWorkerDeductionVO;

// 일용직 급여입력/관리 - 정규직 급여관리(payment.paymentMnt)와 동일한 PAYROLL/PAYROLL_EMPLOYEE/
// PAYROLL_DEDUCTION_DETAIL 테이블을 공유하고, 일자별 지급내역만 DAILY_WORK_RECORD 테이블을 사용한다.
// (설계서 PAYROLL.PAYROLL_ID 컬럼 설명: "일반급여와 일용직 입력화면이 같은 PAYROLL_ID 사용")
public class PaymentMntDayWorkerDAO {

    // 실데이터의 EMPLOYMENT_TYPE 값이 '일용직'/'DAILY' 두 가지로 섞여 있어 둘 다 포함
    private static final String DAILY_TYPE_COND = "e.EMPLOYMENT_TYPE IN ('일용직','DAILY')";

    // ============ 급여차수 헤더 (PAYROLL 공유) ============

    /** 귀속연월+차수에 해당하는 PAYROLL이 없으면 생성하고, PAYROLL_ID를 반환 */
    public Long ensurePayrollDayWorkerExists(Connection conn, String payYearMonth, int paySequence) throws SQLException {
        Long id = selectPayrollId(conn, payYearMonth, paySequence);
        if (id != null) return id;

        String insertSql = "INSERT INTO PAYROLL "
                + "(PAYROLL_ID, COMPANY_ID, PAY_YEAR_MONTH, PAY_SEQUENCE, "
                + " SETTLEMENT_START_DATE, SETTLEMENT_END_DATE, PAYMENT_DATE, REG_ID, MOD_ID) "
                + "VALUES ((SELECT NVL(MAX(PAYROLL_ID), 0) + 1 FROM PAYROLL), "
                + " 1001, ?, ?, SYSDATE, SYSDATE, SYSDATE, 'admin', 'admin')";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, payYearMonth);
            pstmt.setInt(2, paySequence);
            pstmt.executeUpdate();
        }

        return selectPayrollId(conn, payYearMonth, paySequence);
    }

    private Long selectPayrollId(Connection conn, String payYearMonth, int paySequence) throws SQLException {
        String sql = "SELECT PAYROLL_ID FROM PAYROLL WHERE PAY_YEAR_MONTH = ? AND PAY_SEQUENCE = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payYearMonth);
            pstmt.setInt(2, paySequence);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong("PAYROLL_ID");
            }
        }
        return null;
    }

    /** 정산기간/급여지급일 갱신 (PAYROLL은 정규직 화면과 공유하는 테이블) */
    public void updatePeriod(Connection conn, Long payrollId, String periodStart, String periodEnd, String payDate) throws SQLException {
        String sql = "UPDATE PAYROLL SET SETTLEMENT_START_DATE = TO_DATE(?, 'YYYY-MM-DD'), "
                + "SETTLEMENT_END_DATE = TO_DATE(?, 'YYYY-MM-DD'), PAYMENT_DATE = TO_DATE(?, 'YYYY-MM-DD') "
                + "WHERE PAYROLL_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, periodStart);
            pstmt.setString(2, periodEnd);
            pstmt.setString(3, payDate);
            pstmt.setLong(4, payrollId);
            pstmt.executeUpdate();
        }
    }

    // ============ 근로자 목록 (좌측 테이블) ============

    /** 좌측 목록: 해당 급여차수(PAYROLL_ID)에 이미 등록된 일용직 근로자만 (PAYROLL_EMPLOYEE는 정규직과 공유) */
    public List<PaymentMntDayWorkerEmployeeDTO> getPayrollDayWorkerEmployeeList(Connection conn, Long payrollId) throws SQLException {
        List<PaymentMntDayWorkerEmployeeDTO> list = new ArrayList<>();
        String sql = "SELECT pe.PAYROLL_EMPLOYEE_ID, pe.PAYROLL_ID, pe.EMPLOYEE_ID, "
                + "e.EMPLOYEE_NAME, e.EMPLOYMENT_TYPE, e.DEPARTMENT, "
                + "pe.TOTAL_PAY_AMOUNT, pe.TOTAL_DEDUCTION_AMOUNT, pe.NET_PAY_AMOUNT "
                + "FROM PAYROLL_EMPLOYEE pe JOIN EMPLOYEE e ON pe.EMPLOYEE_ID = e.EMPLOYEE_ID "
                + "WHERE pe.PAYROLL_ID = ? AND " + DAILY_TYPE_COND + " ORDER BY e.EMPLOYEE_NAME";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEmployeeRow(rs));
                }
            }
        }
        return list;
    }

    /** [신규추가] 모달용 - 일용직 근로자 검색 (EMPLOYMENT_TYPE = '일용직'/'DAILY') */
    public List<PaymentMntDayWorkerEmployeeDTO> getModalEmployeeList(Connection conn, String keyword) throws SQLException {
        List<PaymentMntDayWorkerEmployeeDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT EMPLOYEE_ID, EMPLOYEE_NAME, EMPLOYMENT_TYPE, DEPARTMENT FROM EMPLOYEE e "
                        + "WHERE " + DAILY_TYPE_COND + " ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND EMPLOYEE_NAME LIKE ? ");
        }
        sql.append("ORDER BY EMPLOYEE_NAME");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                pstmt.setString(1, "%" + keyword.trim() + "%");
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntDayWorkerEmployeeDTO dto = new PaymentMntDayWorkerEmployeeDTO();
                    dto.setEmployeeId(rs.getString("EMPLOYEE_ID"));
                    dto.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
                    dto.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
                    dto.setDepartment(rs.getString("DEPARTMENT"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    /** 신규추가: 선택한 사원들을 이 급여차수(PAYROLL_EMPLOYEE)에 등록 (이미 있으면 건너뜀) */
    public void insertDayWorkerEmployees(Connection conn, Long payrollId, List<String> empIds) throws SQLException {
        String sql = "INSERT INTO PAYROLL_EMPLOYEE "
                + "(PAYROLL_EMPLOYEE_ID, PAYROLL_ID, EMPLOYEE_ID, EMPLOYMENT_TYPE, INCOME_TYPE, "
                + " TOTAL_PAY_AMOUNT, TOTAL_DEDUCTION_AMOUNT, NET_PAY_AMOUNT, REG_ID, MOD_ID) "
                + "SELECT (SELECT NVL(MAX(PAYROLL_EMPLOYEE_ID), 0) FROM PAYROLL_EMPLOYEE) + 1, "
                + " ?, e.EMPLOYEE_ID, e.EMPLOYMENT_TYPE, '일반', 0, 0, 0, 'admin', 'admin' "
                + "FROM EMPLOYEE e WHERE e.EMPLOYEE_ID = ? "
                + "  AND NOT EXISTS (SELECT 1 FROM PAYROLL_EMPLOYEE p "
                + "      WHERE p.PAYROLL_ID = ? AND p.EMPLOYEE_ID = e.EMPLOYEE_ID)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String empId : empIds) {
                if (empId == null || empId.trim().isEmpty()) continue;
                pstmt.setLong(1, payrollId);
                pstmt.setString(2, empId.trim());
                pstmt.setLong(3, payrollId);
                pstmt.executeUpdate();
            }
        }
    }

    /** 선택삭제: 일자별내역+공제상세 포함 실제 DB 삭제 (PAYROLL_EMPLOYEE는 정규직과 공유하므로 지정된 행만 삭제) */
    public void deleteDayWorkerEmployees(Connection conn, List<Long> payrollEmployeeIds) throws SQLException {
        if (payrollEmployeeIds == null || payrollEmployeeIds.isEmpty()) return;
        for (Long id : payrollEmployeeIds) {
            deleteDailyByEmployee(conn, id);
            deleteDeductionByEmployee(conn, id);
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM PAYROLL_EMPLOYEE WHERE PAYROLL_EMPLOYEE_ID = ?")) {
                pstmt.setLong(1, id);
                pstmt.executeUpdate();
            }
        }
    }

    /** 전체삭제: 해당 급여차수(PAYROLL_ID)의 "일용직" 근로자만 삭제 (정규직 근로자는 같은 PAYROLL_ID를 공유하므로 건드리지 않음) */
    public void deleteAllDayWorkerEmployees(Connection conn, Long payrollId) throws SQLException {
        String subSelect = "(SELECT pe.PAYROLL_EMPLOYEE_ID FROM PAYROLL_EMPLOYEE pe JOIN EMPLOYEE e "
                + "ON pe.EMPLOYEE_ID = e.EMPLOYEE_ID WHERE pe.PAYROLL_ID = ? AND " + DAILY_TYPE_COND + ")";

        try (PreparedStatement p1 = conn.prepareStatement(
                "DELETE FROM DAILY_WORK_RECORD WHERE PAYROLL_EMPLOYEE_ID IN " + subSelect)) {
            p1.setLong(1, payrollId);
            p1.executeUpdate();
        }
        try (PreparedStatement p2 = conn.prepareStatement(
                "DELETE FROM PAYROLL_DEDUCTION_DETAIL WHERE PAYROLL_EMPLOYEE_ID IN " + subSelect)) {
            p2.setLong(1, payrollId);
            p2.executeUpdate();
        }
        try (PreparedStatement p3 = conn.prepareStatement(
                "DELETE FROM PAYROLL_EMPLOYEE WHERE PAYROLL_EMPLOYEE_ID IN " + subSelect)) {
            p3.setLong(1, payrollId);
            p3.executeUpdate();
        }
    }

    // ============ 일자별 지급내역 (DAILY_WORK_RECORD) ============

    public List<PaymentMntDayWorkerDailyVO> selectDailyList(Connection conn, Long payrollEmployeeId) throws SQLException {
        List<PaymentMntDayWorkerDailyVO> list = new ArrayList<>();
        String sql = "SELECT DAILY_WORK_RECORD_ID, PAYROLL_EMPLOYEE_ID, "
                + "TO_CHAR(WORK_DATE, 'YYYY-MM-DD') AS WORK_DATE, PAY_RATE, PAY_AMOUNT, "
                + "INCOME_TAX_AMOUNT, LOCAL_INCOME_TAX_AMOUNT "
                + "FROM DAILY_WORK_RECORD WHERE PAYROLL_EMPLOYEE_ID = ? ORDER BY WORK_DATE";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollEmployeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntDayWorkerDailyVO vo = new PaymentMntDayWorkerDailyVO();
                    vo.setDailyId(rs.getLong("DAILY_WORK_RECORD_ID"));
                    vo.setPayrollDayWorkerEmployeeId(rs.getLong("PAYROLL_EMPLOYEE_ID"));
                    vo.setWorkDate(rs.getString("WORK_DATE"));
                    vo.setRate(rs.getBigDecimal("PAY_RATE"));
                    vo.setPayAmt(rs.getLong("PAY_AMOUNT"));
                    vo.setIncomeTax(rs.getLong("INCOME_TAX_AMOUNT"));
                    vo.setLocalTax(rs.getLong("LOCAL_INCOME_TAX_AMOUNT"));
                    list.add(vo);
                }
            }
        }
        return list;
    }

    public void deleteDailyByEmployee(Connection conn, Long payrollEmployeeId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM DAILY_WORK_RECORD WHERE PAYROLL_EMPLOYEE_ID = ?")) {
            pstmt.setLong(1, payrollEmployeeId);
            pstmt.executeUpdate();
        }
    }

    /** DAILY_WORK_RECORD는 EMPLOYEE_ID/WORK_SITE_NAME/DAILY_WAGE가 NOT NULL이라 사원정보를 함께 조회해 채운다.
     *  이 화면에는 현장(site) 입력 UI가 없어 WORK_SITE_NAME은 사원 부서명으로 대체한다. */
    public void insertDailyList(Connection conn, Long payrollEmployeeId, List<PaymentMntDayWorkerDailyVO> dailyList) throws SQLException {
        if (dailyList == null || dailyList.isEmpty()) return;

        String empId = null;
        String dept = null;
        String empSql = "SELECT e.EMPLOYEE_ID, e.DEPARTMENT FROM PAYROLL_EMPLOYEE pe "
                + "JOIN EMPLOYEE e ON pe.EMPLOYEE_ID = e.EMPLOYEE_ID WHERE pe.PAYROLL_EMPLOYEE_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(empSql)) {
            pstmt.setLong(1, payrollEmployeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    empId = rs.getString("EMPLOYEE_ID");
                    dept = rs.getString("DEPARTMENT");
                }
            }
        }
        if (empId == null) return; // 존재하지 않는 사원별급여 행이면 스킵

        String sql = "INSERT INTO DAILY_WORK_RECORD "
                + "(DAILY_WORK_RECORD_ID, EMPLOYEE_ID, PAYROLL_EMPLOYEE_ID, WORK_SITE_NAME, WORK_DATE, "
                + " DAILY_WAGE, PAY_RATE, PAY_AMOUNT, INCOME_TAX_AMOUNT, LOCAL_INCOME_TAX_AMOUNT, NET_PAY_AMOUNT, REG_ID, MOD_ID) "
                + "VALUES ((SELECT NVL(MAX(DAILY_WORK_RECORD_ID), 0) + 1 FROM DAILY_WORK_RECORD), "
                + "?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?, ?, ?, 'admin', 'admin')";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (PaymentMntDayWorkerDailyVO d : dailyList) {
                BigDecimal rate = d.getRate() == null ? BigDecimal.ONE : d.getRate();
                long payAmt = d.getPayAmt() == null ? 0L : d.getPayAmt();
                long incomeTax = d.getIncomeTax() == null ? 0L : d.getIncomeTax();
                long localTax = d.getLocalTax() == null ? 0L : d.getLocalTax();
                long dailyWage = rate.compareTo(BigDecimal.ZERO) == 0 ? payAmt : Math.round(payAmt / rate.doubleValue());
                long netPay = payAmt - incomeTax - localTax;

                pstmt.setString(1, empId);
                pstmt.setLong(2, payrollEmployeeId);
                pstmt.setString(3, (dept == null || dept.isEmpty()) ? "미지정" : dept);
                pstmt.setString(4, d.getWorkDate());
                pstmt.setLong(5, dailyWage);
                pstmt.setBigDecimal(6, rate);
                pstmt.setLong(7, payAmt);
                pstmt.setLong(8, incomeTax);
                pstmt.setLong(9, localTax);
                pstmt.setLong(10, netPay);
                pstmt.executeUpdate();
            }
        }
    }

    // ============ 공제항목 (PAYROLL_DEDUCTION_DETAIL, DEDUCTION_ITEM 마스터 공유) ============
    // 화면 공제항목 패널은 DEDUCTION_ITEM 마스터에 실제 등록된 항목만큼만 동적으로 그려진다.

    /** 공제항목 마스터 목록 (화면 공제항목 패널을 이 목록 기준으로 렌더링) */
    public List<PaymentMntDayWorkerDeductionItemDTO> selectDeductionItemList(Connection conn) throws SQLException {
        List<PaymentMntDayWorkerDeductionItemDTO> list = new ArrayList<>();
        String sql = "SELECT DEDUCTION_ITEM_ID, DEDUCTION_ITEM_NAME FROM DEDUCTION_ITEM WHERE USE_YN = 'Y' ORDER BY DISPLAY_ORDER, DEDUCTION_ITEM_ID";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                PaymentMntDayWorkerDeductionItemDTO dto = new PaymentMntDayWorkerDeductionItemDTO();
                dto.setDeductionItemId(rs.getInt("DEDUCTION_ITEM_ID"));
                dto.setDeductionItemName(rs.getString("DEDUCTION_ITEM_NAME"));
                list.add(dto);
            }
        }
        return list;
    }

    public PaymentMntDayWorkerDeductionVO selectDeduction(Connection conn, Long payrollEmployeeId) throws SQLException {
        String sql = "SELECT DEDUCTION_ITEM_ID, AMOUNT FROM PAYROLL_DEDUCTION_DETAIL WHERE PAYROLL_EMPLOYEE_ID = ?";
        PaymentMntDayWorkerDeductionVO vo = null;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollEmployeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (vo == null) {
                        vo = new PaymentMntDayWorkerDeductionVO();
                        vo.setPayrollDayWorkerEmployeeId(payrollEmployeeId);
                    }
                    vo.putAmount(rs.getInt("DEDUCTION_ITEM_ID"), rs.getLong("AMOUNT"));
                }
            }
        }
        return vo;
    }

    private void deleteDeductionByEmployee(Connection conn, Long payrollEmployeeId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM PAYROLL_DEDUCTION_DETAIL WHERE PAYROLL_EMPLOYEE_ID = ?")) {
            pstmt.setLong(1, payrollEmployeeId);
            pstmt.executeUpdate();
        }
    }

    /** 전달된 항목(DEDUCTION_ITEM_ID)마다 있으면 UPDATE, 없으면 INSERT */
    public void upsertDeduction(Connection conn, PaymentMntDayWorkerDeductionVO d) throws SQLException {
        for (Map.Entry<Integer, Long> entry : d.getAmounts().entrySet()) {
            upsertOneDeductionItem(conn, d.getPayrollDayWorkerEmployeeId(), entry.getKey(), nz(entry.getValue()));
        }
    }

    private void upsertOneDeductionItem(Connection conn, Long payrollEmployeeId, int deductionItemId, long amount) throws SQLException {
        String updateSql = "UPDATE PAYROLL_DEDUCTION_DETAIL SET AMOUNT = ?, MOD_ID = 'admin' "
                + "WHERE PAYROLL_EMPLOYEE_ID = ? AND DEDUCTION_ITEM_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setLong(1, amount);
            pstmt.setLong(2, payrollEmployeeId);
            pstmt.setInt(3, deductionItemId);
            int count = pstmt.executeUpdate();

            if (count == 0) {
                String insertSql = "INSERT INTO PAYROLL_DEDUCTION_DETAIL "
                        + "(PAYROLL_DEDUCTION_DETAIL_ID, PAYROLL_EMPLOYEE_ID, DEDUCTION_ITEM_ID, AMOUNT, REG_ID, MOD_ID) "
                        + "VALUES ((SELECT NVL(MAX(PAYROLL_DEDUCTION_DETAIL_ID), 0) + 1 FROM PAYROLL_DEDUCTION_DETAIL), "
                        + "?, ?, ?, 'admin', 'admin')";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setLong(1, payrollEmployeeId);
                    insertStmt.setInt(2, deductionItemId);
                    insertStmt.setLong(3, amount);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    // ============ 합계 갱신 ============

    public void updateEmployeeTotals(Connection conn, Long payrollEmployeeId, long totalPay, long totalDed, long netPay) throws SQLException {
        String sql = "UPDATE PAYROLL_EMPLOYEE SET TOTAL_PAY_AMOUNT = ?, TOTAL_DEDUCTION_AMOUNT = ?, NET_PAY_AMOUNT = ? "
                + "WHERE PAYROLL_EMPLOYEE_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, totalPay);
            pstmt.setLong(2, totalDed);
            pstmt.setLong(3, netPay);
            pstmt.setLong(4, payrollEmployeeId);
            pstmt.executeUpdate();
        }
    }

    // ============ 지난급여 불러오기 ============

    /** 이전 달 "일용직" 근로자 + 일자별내역 + 공제항목을 통째로 복사, 복사된 근로자 수 반환 */
    public int copyPreviousEmployees(Connection conn, String prevYearMonth, int prevSeq, Long currPayrollId) throws SQLException {
        Long prevPayrollId = selectPayrollId(conn, prevYearMonth, prevSeq);
        if (prevPayrollId == null) return 0;

        List<PaymentMntDayWorkerEmployeeDTO> prevEmployees = getPayrollDayWorkerEmployeeList(conn, prevPayrollId);
        int count = 0;
        for (PaymentMntDayWorkerEmployeeDTO prevEmp : prevEmployees) {
            insertDayWorkerEmployees(conn, currPayrollId, Collections.singletonList(prevEmp.getEmployeeId()));

            Long newEmpId = selectPayrollEmployeeId(conn, currPayrollId, prevEmp.getEmployeeId());
            if (newEmpId == null) continue;

            List<PaymentMntDayWorkerDailyVO> dailyList = selectDailyList(conn, prevEmp.getPayrollDayWorkerEmployeeId());
            insertDailyList(conn, newEmpId, dailyList);

            PaymentMntDayWorkerDeductionVO ded = selectDeduction(conn, prevEmp.getPayrollDayWorkerEmployeeId());
            if (ded != null) {
                ded.setPayrollDayWorkerEmployeeId(newEmpId);
                upsertDeduction(conn, ded);
            }

            updateEmployeeTotals(conn, newEmpId,
                    nz(prevEmp.getTotalPayAmount()), nz(prevEmp.getTotalDeductionAmount()), nz(prevEmp.getNetPayAmount()));
            count++;
        }
        return count;
    }

    private Long selectPayrollEmployeeId(Connection conn, Long payrollId, String employeeId) throws SQLException {
        String sql = "SELECT PAYROLL_EMPLOYEE_ID FROM PAYROLL_EMPLOYEE WHERE PAYROLL_ID = ? AND EMPLOYEE_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollId);
            pstmt.setString(2, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    // ============ 종합정보 ============

    /** 해당 급여차수(PAYROLL_ID)의 "일용직" 근로자만 집계 (정규직 근로자는 제외) */
    public Map<String, Object> selectSummary(Connection conn, Long payrollId) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        String sql = "SELECT COUNT(*) AS CNT, NVL(SUM(pe.TOTAL_PAY_AMOUNT), 0) AS PAY_TOTAL, "
                + "NVL(SUM(pe.TOTAL_DEDUCTION_AMOUNT), 0) AS DED_TOTAL, NVL(SUM(pe.NET_PAY_AMOUNT), 0) AS NET_TOTAL "
                + "FROM PAYROLL_EMPLOYEE pe JOIN EMPLOYEE e ON pe.EMPLOYEE_ID = e.EMPLOYEE_ID "
                + "WHERE pe.PAYROLL_ID = ? AND " + DAILY_TYPE_COND;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    map.put("workerCount", rs.getLong("CNT"));
                    map.put("payTotal", rs.getLong("PAY_TOTAL"));
                    map.put("deductionTotal", rs.getLong("DED_TOTAL"));
                    map.put("netPay", rs.getLong("NET_TOTAL"));
                }
            }
        }
        return map;
    }

    private PaymentMntDayWorkerEmployeeDTO mapEmployeeRow(ResultSet rs) throws SQLException {
        PaymentMntDayWorkerEmployeeDTO dto = new PaymentMntDayWorkerEmployeeDTO();
        dto.setPayrollDayWorkerEmployeeId(rs.getLong("PAYROLL_EMPLOYEE_ID"));
        dto.setPayrollDayWorkerId(rs.getLong("PAYROLL_ID"));
        dto.setEmployeeId(rs.getString("EMPLOYEE_ID"));
        dto.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
        dto.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
        dto.setDepartment(rs.getString("DEPARTMENT"));
        dto.setTotalPayAmount(rs.getLong("TOTAL_PAY_AMOUNT"));
        dto.setTotalDeductionAmount(rs.getLong("TOTAL_DEDUCTION_AMOUNT"));
        dto.setNetPayAmount(rs.getLong("NET_PAY_AMOUNT"));
        return dto;
    }

    private long nz(Long v) { return v == null ? 0L : v; }
}
