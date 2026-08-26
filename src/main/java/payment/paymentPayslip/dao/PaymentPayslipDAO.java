package payment.paymentPayslip.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import payment.paymentPayslip.dto.PaymentPayslipDetailDTO;
import payment.paymentPayslip.dto.PaymentPayslipItemDTO;

public class PaymentPayslipDAO {

    /** 귀속연월+급여차수에 해당하는 PAYROLL_ID 조회 (등록된 급여가 없으면 null)
     *  帰属年月＋給与回に該当するPAYROLL_IDを照会（登録された給与がなければnull） */
    public Long selectPayrollId(Connection conn, String payYearMonth, int paySequence) throws SQLException {
        String sql = "SELECT PAYROLL_ID FROM PAYROLL WHERE PAY_YEAR_MONTH = ? AND PAY_SEQUENCE = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payYearMonth);
            pstmt.setInt(2, paySequence);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getLong("PAYROLL_ID") : null;
            }
        }
    }

    /** 급여차수에 속한 사원 목록 + 인적사항 + 지급/공제 합계 (지급/공제 상세는 별도 배치 조회 후 Service에서 합쳐준다)
     *  給与回に属する社員一覧＋人的事項＋支給・控除合計（支給・控除詳細は別途バッチ照会後、Serviceで合わせる） */
    public List<PaymentPayslipDetailDTO> selectEmployeeList(Connection conn, Long payrollId) throws SQLException {
        List<PaymentPayslipDetailDTO> list = new ArrayList<>();
        // 일용직/DAILY 사원은 이 급여차수에 실제 근무기록(DAILY_WORK_RECORD)이 있을 때만 노출한다.
        // (근무기록 없이 지급/공제 내역이 전부 0인 유령 PAYROLL_EMPLOYEE 행 - 예: 예전 "지난급여 불러오기"가
        //  직군 구분 없이 복사하던 시절의 잔존 데이터 - 는 명세서 대상에서 제외)
        // 日雇い/DAILY社員はこの給与回に実際の勤務記録（DAILY_WORK_RECORD）がある時のみ表示する。
        // （勤務記録なしで支給・控除内訳がすべて0の幽霊PAYROLL_EMPLOYEE行 - 例：以前の「前回給与の読み込み」が
        //  職種区分なしにコピーしていた頃の残存データ - は明細書の対象から除外）
        String sql = "SELECT pe.PAYROLL_EMPLOYEE_ID, e.EMPLOYMENT_TYPE, e.EMPLOYEE_NAME, e.RESIDENT_REG_NO, "
                   + "e.DEPARTMENT, e.POSITION, e.HIRE_DATE, "
                   + "pe.TOTAL_PAY_AMOUNT, pe.TOTAL_DEDUCTION_AMOUNT, pe.NET_PAY_AMOUNT "
                   + "FROM PAYROLL_EMPLOYEE pe JOIN EMPLOYEE e ON e.EMPLOYEE_ID = pe.EMPLOYEE_ID "
                   + "WHERE pe.PAYROLL_ID = ? "
                   + "  AND (e.EMPLOYMENT_TYPE NOT IN ('일용직','DAILY') "
                   + "       OR EXISTS (SELECT 1 FROM DAILY_WORK_RECORD d WHERE d.PAYROLL_EMPLOYEE_ID = pe.PAYROLL_EMPLOYEE_ID)) "
                   + "ORDER BY e.EMPLOYEE_NAME";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentPayslipDetailDTO dto = new PaymentPayslipDetailDTO();
                    dto.setPayrollEmployeeId(rs.getLong("PAYROLL_EMPLOYEE_ID"));
                    dto.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
                    dto.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
                    dto.setResidentRegNo(rs.getString("RESIDENT_REG_NO"));
                    dto.setDepartment(rs.getString("DEPARTMENT"));
                    dto.setPosition(rs.getString("POSITION"));
                    dto.setHireDate(rs.getString("HIRE_DATE"));
                    dto.setTotalPayAmount(rs.getLong("TOTAL_PAY_AMOUNT"));
                    dto.setTotalDeductionAmount(rs.getLong("TOTAL_DEDUCTION_AMOUNT"));
                    dto.setNetPayAmount(rs.getLong("NET_PAY_AMOUNT"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    /** payrollId에 속한 모든 사원의 지급항목 내역을 한 번에 조회 (0원 항목은 명세서에 표시하지 않는다)
     *  payrollIdに属するすべての社員の支給項目内訳を一度に照会（0円項目は明細書に表示しない） */
    public Map<Long, List<PaymentPayslipItemDTO>> selectPayItemsByPayroll(Connection conn, Long payrollId) throws SQLException {
        String sql = "SELECT d.PAYROLL_EMPLOYEE_ID, pi.PAY_ITEM_NAME AS ITEM_NAME, pi.CALCULATION_METHOD, d.AMOUNT "
                   + "FROM PAYROLL_PAY_DETAIL d "
                   + "JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_EMPLOYEE_ID = d.PAYROLL_EMPLOYEE_ID "
                   + "JOIN PAY_ITEM pi ON pi.PAY_ITEM_ID = d.PAY_ITEM_ID "
                   + "WHERE pe.PAYROLL_ID = ? AND d.AMOUNT <> 0 "
                   + "ORDER BY d.PAYROLL_EMPLOYEE_ID, pi.DISPLAY_ORDER";
        return selectItemsByPayroll(conn, sql, payrollId);
    }

    /** payrollId에 속한 모든 사원의 공제항목 내역을 한 번에 조회 (0원 항목은 명세서에 표시하지 않는다)
     *  payrollIdに属するすべての社員の控除項目内訳を一度に照会（0円項目は明細書に表示しない） */
    public Map<Long, List<PaymentPayslipItemDTO>> selectDeductionItemsByPayroll(Connection conn, Long payrollId) throws SQLException {
        String sql = "SELECT d.PAYROLL_EMPLOYEE_ID, di.DEDUCTION_ITEM_NAME AS ITEM_NAME, di.CALCULATION_METHOD, d.AMOUNT "
                   + "FROM PAYROLL_DEDUCTION_DETAIL d "
                   + "JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_EMPLOYEE_ID = d.PAYROLL_EMPLOYEE_ID "
                   + "JOIN DEDUCTION_ITEM di ON di.DEDUCTION_ITEM_ID = d.DEDUCTION_ITEM_ID "
                   + "WHERE pe.PAYROLL_ID = ? AND d.AMOUNT <> 0 "
                   + "ORDER BY d.PAYROLL_EMPLOYEE_ID, di.DISPLAY_ORDER";
        return selectItemsByPayroll(conn, sql, payrollId);
    }

    /** 일용직 사원의 지급내역: 일자별로 DAILY_WORK_RECORD에 저장되므로 PAYROLL_PAY_DETAIL이 아닌 여기서 합산해 한 줄로 만들어준다.
     *  日雇い社員の支給内訳：日別にDAILY_WORK_RECORDに保存されるためPAYROLL_PAY_DETAILではなくここで合算して1行にまとめる。 */
    public Map<Long, PaymentPayslipItemDTO> selectDailyPayByPayroll(Connection conn, Long payrollId) throws SQLException {
        Map<Long, PaymentPayslipItemDTO> result = new HashMap<>();
        String sql = "SELECT d.PAYROLL_EMPLOYEE_ID, COUNT(*) AS WORK_DAYS, SUM(d.PAY_AMOUNT) AS TOTAL_PAY "
                   + "FROM DAILY_WORK_RECORD d JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_EMPLOYEE_ID = d.PAYROLL_EMPLOYEE_ID "
                   + "WHERE pe.PAYROLL_ID = ? GROUP BY d.PAYROLL_EMPLOYEE_ID";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentPayslipItemDTO dto = new PaymentPayslipItemDTO();
                    dto.setItemName("일용급여");
                    dto.setCalculationMethod("근무 " + rs.getInt("WORK_DAYS") + "일");
                    dto.setAmount(rs.getLong("TOTAL_PAY"));
                    result.put(rs.getLong("PAYROLL_EMPLOYEE_ID"), dto);
                }
            }
        }
        return result;
    }

    /** 일용직 사원의 원천징수 세액(소득세/지방소득세): PAYROLL_DEDUCTION_DETAIL이 아닌 DAILY_WORK_RECORD에 일자별로 저장된다.
     *  日雇い社員の源泉徴収税額（所得税・地方所得税）：PAYROLL_DEDUCTION_DETAILではなくDAILY_WORK_RECORDに日別で保存される。 */
    public Map<Long, List<PaymentPayslipItemDTO>> selectDailyTaxDeductionsByPayroll(Connection conn, Long payrollId) throws SQLException {
        Map<Long, List<PaymentPayslipItemDTO>> result = new HashMap<>();
        String sql = "SELECT d.PAYROLL_EMPLOYEE_ID, SUM(d.INCOME_TAX_AMOUNT) AS INCOME_TAX, "
                   + "SUM(d.LOCAL_INCOME_TAX_AMOUNT) AS LOCAL_TAX "
                   + "FROM DAILY_WORK_RECORD d JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_EMPLOYEE_ID = d.PAYROLL_EMPLOYEE_ID "
                   + "WHERE pe.PAYROLL_ID = ? GROUP BY d.PAYROLL_EMPLOYEE_ID";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long payrollEmployeeId = rs.getLong("PAYROLL_EMPLOYEE_ID");
                    long incomeTax = rs.getLong("INCOME_TAX");
                    long localTax = rs.getLong("LOCAL_TAX");
                    List<PaymentPayslipItemDTO> items = new ArrayList<>();
                    if (incomeTax != 0) {
                        PaymentPayslipItemDTO dto = new PaymentPayslipItemDTO();
                        dto.setItemName("소득세");
                        dto.setAmount(incomeTax);
                        items.add(dto);
                    }
                    if (localTax != 0) {
                        PaymentPayslipItemDTO dto = new PaymentPayslipItemDTO();
                        dto.setItemName("지방소득세");
                        dto.setAmount(localTax);
                        items.add(dto);
                    }
                    if (!items.isEmpty()) { result.put(payrollEmployeeId, items); }
                }
            }
        }
        return result;
    }

    private Map<Long, List<PaymentPayslipItemDTO>> selectItemsByPayroll(Connection conn, String sql, Long payrollId) throws SQLException {
        Map<Long, List<PaymentPayslipItemDTO>> result = new HashMap<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long payrollEmployeeId = rs.getLong("PAYROLL_EMPLOYEE_ID");
                    PaymentPayslipItemDTO dto = new PaymentPayslipItemDTO();
                    dto.setItemName(rs.getString("ITEM_NAME"));
                    dto.setCalculationMethod(rs.getString("CALCULATION_METHOD"));
                    dto.setAmount(rs.getLong("AMOUNT"));
                    result.computeIfAbsent(payrollEmployeeId, k -> new ArrayList<>()).add(dto);
                }
            }
        }
        return result;
    }
}
