package payment.paymentPayList.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import payment.model.PaymentInsuranceLedger;
import payment.paymentPayList.dto.PaymentPayListRowDTO;

public class PaymentPayListDAO {

    /** 사원명(정확히 일치)과 조회기간(YYYYMM)에 해당하는 월별 급여내역 + 4대보험/갑근세 공제액을 조회한다.
     *  4대보험 항목명은 payment.fourinsureList(4대보험 공제내역) 화면과 동일한 DEDUCTION_ITEM_NAME을 사용한다. */
    public List<PaymentPayListRowDTO> selectPayList(Connection conn, String employeeName,
            String startYearMonth, String endYearMonth) throws SQLException {
        List<PaymentPayListRowDTO> list = new ArrayList<>();

        String sql = "SELECT p.PAY_YEAR_MONTH, p.PAY_SEQUENCE, "
                   + "pe.TOTAL_PAY_AMOUNT, pe.TOTAL_DEDUCTION_AMOUNT, pe.NET_PAY_AMOUNT, "
                   + "NVL(SUM(CASE WHEN di.DEDUCTION_ITEM_NAME = ? THEN dd.AMOUNT END), 0) AS NATIONAL_PENSION, "
                   + "NVL(SUM(CASE WHEN di.DEDUCTION_ITEM_NAME = ? THEN dd.AMOUNT END), 0) AS HEALTH_INSURANCE, "
                   + "NVL(SUM(CASE WHEN di.DEDUCTION_ITEM_NAME = ? THEN dd.AMOUNT END), 0) AS LONG_TERM_CARE, "
                   + "NVL(SUM(CASE WHEN di.DEDUCTION_ITEM_NAME = ? THEN dd.AMOUNT END), 0) AS EMPLOYMENT_INSURANCE, "
                   + "NVL(SUM(CASE WHEN di.DEDUCTION_ITEM_NAME = '소득세' THEN dd.AMOUNT END), 0) AS INCOME_TAX, "
                   + "NVL(SUM(CASE WHEN di.DEDUCTION_ITEM_NAME = '지방소득세' THEN dd.AMOUNT END), 0) AS LOCAL_INCOME_TAX "
                   + "FROM PAYROLL p "
                   + "JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
                   + "JOIN EMPLOYEE e ON e.EMPLOYEE_ID = pe.EMPLOYEE_ID "
                   + "LEFT JOIN PAYROLL_DEDUCTION_DETAIL dd ON dd.PAYROLL_EMPLOYEE_ID = pe.PAYROLL_EMPLOYEE_ID "
                   + "LEFT JOIN DEDUCTION_ITEM di ON di.DEDUCTION_ITEM_ID = dd.DEDUCTION_ITEM_ID "
                   + "WHERE e.EMPLOYEE_NAME = ? AND p.PAY_YEAR_MONTH BETWEEN ? AND ? "
                   + "GROUP BY p.PAY_YEAR_MONTH, p.PAY_SEQUENCE, pe.TOTAL_PAY_AMOUNT, pe.TOTAL_DEDUCTION_AMOUNT, pe.NET_PAY_AMOUNT "
                   + "ORDER BY p.PAY_YEAR_MONTH, p.PAY_SEQUENCE";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int idx = 1;
            pstmt.setString(idx++, PaymentInsuranceLedger.DEDUCTION_NATIONAL_PENSION);
            pstmt.setString(idx++, PaymentInsuranceLedger.DEDUCTION_HEALTH_INSURANCE);
            pstmt.setString(idx++, PaymentInsuranceLedger.DEDUCTION_LONG_TERM_CARE);
            pstmt.setString(idx++, PaymentInsuranceLedger.DEDUCTION_EMPLOYMENT_INSURANCE);
            pstmt.setString(idx++, employeeName);
            pstmt.setString(idx++, startYearMonth);
            pstmt.setString(idx++, endYearMonth);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentPayListRowDTO dto = new PaymentPayListRowDTO();
                    dto.setPayYearMonth(rs.getString("PAY_YEAR_MONTH"));
                    dto.setPaySequence(rs.getInt("PAY_SEQUENCE"));
                    dto.setTotalPayAmount(rs.getLong("TOTAL_PAY_AMOUNT"));
                    dto.setTotalDeductionAmount(rs.getLong("TOTAL_DEDUCTION_AMOUNT"));
                    dto.setNetPayAmount(rs.getLong("NET_PAY_AMOUNT"));
                    dto.setNationalPension(rs.getLong("NATIONAL_PENSION"));
                    dto.setHealthInsurance(rs.getLong("HEALTH_INSURANCE"));
                    dto.setLongTermCare(rs.getLong("LONG_TERM_CARE"));
                    dto.setEmploymentInsurance(rs.getLong("EMPLOYMENT_INSURANCE"));
                    dto.setIncomeTax(rs.getLong("INCOME_TAX"));
                    dto.setLocalIncomeTax(rs.getLong("LOCAL_INCOME_TAX"));
                    list.add(dto);
                }
            }
        }
        return list;
    }
}
