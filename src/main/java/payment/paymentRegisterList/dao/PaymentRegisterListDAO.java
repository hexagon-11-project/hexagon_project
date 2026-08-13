package payment.paymentRegisterList.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import payment.paymentRegisterList.dto.PaymentRegisterListDTO;

// 급여대장 - 귀속연도별 급여차수 목록 (PAYROLL 헤더 + PAYROLL_EMPLOYEE 집계)
public class PaymentRegisterListDAO {

    /** 귀속연도(payYear)의 급여-01차 실적만 조회 (달력상 1~12월 채우기는 Service에서 처리).
     *  정산기간/지급일은 PAYROLL에 저장된 값이 아니라 화면에서 귀속연월 기준으로 항상 계산해서 보여주므로 조회하지 않는다.
     *  인원/금액은 "등록만 되고 저장은 안 한" 사원을 제외하기 위해, 실제 급여상세가 저장된 사원만 집계한다.
     *  (일용직은 DAILY_WORK_RECORD, 그 외는 PAYROLL_PAY_DETAIL 존재 여부로 판단 - paymentMnt/paymentMntDayWorker와 동일한 기준) */
    public List<PaymentRegisterListDTO> selectPayrollSummaryByYear(Connection conn, String payYear) throws SQLException {
        List<PaymentRegisterListDTO> list = new ArrayList<>();
        String sql = "SELECT p.PAYROLL_ID, p.PAY_YEAR_MONTH, "
                + "COUNT(pe.PAYROLL_EMPLOYEE_ID) AS EMPLOYEE_COUNT, "
                + "NVL(SUM(pe.TOTAL_PAY_AMOUNT), 0) AS PAY_TOTAL, "
                + "NVL(SUM(pe.TOTAL_DEDUCTION_AMOUNT), 0) AS DED_TOTAL, "
                + "NVL(SUM(pe.NET_PAY_AMOUNT), 0) AS NET_TOTAL "
                + "FROM PAYROLL p "
                + "LEFT JOIN ( "
                + "    SELECT pe.PAYROLL_EMPLOYEE_ID, pe.PAYROLL_ID, pe.TOTAL_PAY_AMOUNT, pe.TOTAL_DEDUCTION_AMOUNT, pe.NET_PAY_AMOUNT "
                + "    FROM PAYROLL_EMPLOYEE pe JOIN EMPLOYEE e ON e.EMPLOYEE_ID = pe.EMPLOYEE_ID "
                + "    WHERE (e.EMPLOYMENT_TYPE IN ('일용직','DAILY') "
                + "           AND EXISTS (SELECT 1 FROM DAILY_WORK_RECORD d WHERE d.PAYROLL_EMPLOYEE_ID = pe.PAYROLL_EMPLOYEE_ID)) "
                + "       OR (e.EMPLOYMENT_TYPE NOT IN ('일용직','DAILY') "
                + "           AND EXISTS (SELECT 1 FROM PAYROLL_PAY_DETAIL pd WHERE pd.PAYROLL_EMPLOYEE_ID = pe.PAYROLL_EMPLOYEE_ID)) "
                + ") pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
                + "WHERE SUBSTR(p.PAY_YEAR_MONTH, 1, 4) = ? AND p.PAY_SEQUENCE = 1 "
                + "GROUP BY p.PAYROLL_ID, p.PAY_YEAR_MONTH "
                + "ORDER BY p.PAY_YEAR_MONTH";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payYear);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentRegisterListDTO dto = new PaymentRegisterListDTO();
                    dto.setPayrollId(rs.getLong("PAYROLL_ID"));
                    dto.setPayYearMonth(rs.getString("PAY_YEAR_MONTH"));
                    dto.setPaySequence(1);
                    dto.setEmployeeCount(rs.getLong("EMPLOYEE_COUNT"));
                    dto.setTotalPayAmount(rs.getLong("PAY_TOTAL"));
                    dto.setTotalDeductionAmount(rs.getLong("DED_TOTAL"));
                    dto.setNetPayAmount(rs.getLong("NET_TOTAL"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    /** 급여차수 삭제: 하위 일자별내역/공제상세/이체신청 내역까지 함께 삭제 후 PAYROLL 헤더 삭제 */
    public void deletePayroll(Connection conn, Long payrollId) throws SQLException {
        String empSubSelect = "(SELECT PAYROLL_EMPLOYEE_ID FROM PAYROLL_EMPLOYEE WHERE PAYROLL_ID = ?)";

        try (PreparedStatement p1 = conn.prepareStatement(
                "DELETE FROM DAILY_WORK_RECORD WHERE PAYROLL_EMPLOYEE_ID IN " + empSubSelect)) {
            p1.setLong(1, payrollId);
            p1.executeUpdate();
        }
        try (PreparedStatement p2 = conn.prepareStatement(
                "DELETE FROM PAYROLL_DEDUCTION_DETAIL WHERE PAYROLL_EMPLOYEE_ID IN " + empSubSelect)) {
            p2.setLong(1, payrollId);
            p2.executeUpdate();
        }
        try (PreparedStatement p3 = conn.prepareStatement(
                "DELETE FROM PAYROLL_TRANSFER_REQUEST WHERE PAYROLL_ID = ?")) {
            p3.setLong(1, payrollId);
            p3.executeUpdate();
        }
        try (PreparedStatement p4 = conn.prepareStatement(
                "DELETE FROM PAYROLL_EMPLOYEE WHERE PAYROLL_ID = ?")) {
            p4.setLong(1, payrollId);
            p4.executeUpdate();
        }
        try (PreparedStatement p5 = conn.prepareStatement(
                "DELETE FROM PAYROLL WHERE PAYROLL_ID = ?")) {
            p5.setLong(1, payrollId);
            p5.executeUpdate();
        }
    }
}
