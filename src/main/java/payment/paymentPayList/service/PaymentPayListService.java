package payment.paymentPayList.service;

import java.sql.Connection;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import payment.paymentPayList.dao.PaymentPayListDAO;
import payment.paymentPayList.dto.PaymentPayListResult;
import payment.paymentPayList.dto.PaymentPayListRowDTO;

public class PaymentPayListService {

    private static final DateTimeFormatter YEAR_MONTH_FMT = DateTimeFormatter.ofPattern("yyyyMM");

    private PaymentPayListDAO dao = new PaymentPayListDAO();

    public PaymentPayListResult getPayList(String employeeName, String startYearMonth, String endYearMonth) {
        PaymentPayListResult result = new PaymentPayListResult();

        List<PaymentPayListRowDTO> rows;
        if (employeeName == null || employeeName.trim().isEmpty()) {
            rows = new ArrayList<>();
        } else {
            Connection conn = null;
            try {
                conn = ConnectionProvider.getConnection();
                List<PaymentPayListRowDTO> existingRows = dao.selectPayList(conn, employeeName.trim(), startYearMonth, endYearMonth);
                rows = fillMissingMonths(existingRows, startYearMonth, endYearMonth);
            } catch (Exception e) {
                throw new RuntimeException("사원별 급여내역 조회 중 오류 발생", e);
            } finally {
                JdbcUtil.close(conn);
            }
        }

        result.setRows(rows);
        result.setTotals(buildTotals(rows));
        return result;
    }

    /** 조회기간 안의 모든 달을 채워서 반환한다 - 실제 급여 데이터가 있는 달은 그대로, 없는 달은 0으로 채운 빈 행을 넣는다. */
    private List<PaymentPayListRowDTO> fillMissingMonths(List<PaymentPayListRowDTO> existingRows, String startYearMonth, String endYearMonth) {
        Map<String, PaymentPayListRowDTO> byMonth = new HashMap<>();
        for (PaymentPayListRowDTO row : existingRows) {
            byMonth.put(row.getPayYearMonth(), row);
        }

        List<PaymentPayListRowDTO> padded = new ArrayList<>();
        YearMonth start = YearMonth.parse(startYearMonth, YEAR_MONTH_FMT);
        YearMonth end = YearMonth.parse(endYearMonth, YEAR_MONTH_FMT);
        for (YearMonth ym = start; !ym.isAfter(end); ym = ym.plusMonths(1)) {
            String key = ym.format(YEAR_MONTH_FMT);
            PaymentPayListRowDTO row = byMonth.get(key);
            if (row == null) {
                row = new PaymentPayListRowDTO();
                row.setPayYearMonth(key);
                row.setPaySequence(1);
            }
            padded.add(row);
        }
        return padded;
    }

    private PaymentPayListRowDTO buildTotals(List<PaymentPayListRowDTO> rows) {
        PaymentPayListRowDTO totals = new PaymentPayListRowDTO();
        for (PaymentPayListRowDTO row : rows) {
            totals.setTotalPayAmount(totals.getTotalPayAmount() + row.getTotalPayAmount());
            totals.setTotalDeductionAmount(totals.getTotalDeductionAmount() + row.getTotalDeductionAmount());
            totals.setNetPayAmount(totals.getNetPayAmount() + row.getNetPayAmount());
            totals.setNationalPension(totals.getNationalPension() + row.getNationalPension());
            totals.setHealthInsurance(totals.getHealthInsurance() + row.getHealthInsurance());
            totals.setLongTermCare(totals.getLongTermCare() + row.getLongTermCare());
            totals.setEmploymentInsurance(totals.getEmploymentInsurance() + row.getEmploymentInsurance());
            totals.setIncomeTax(totals.getIncomeTax() + row.getIncomeTax());
            totals.setLocalIncomeTax(totals.getLocalIncomeTax() + row.getLocalIncomeTax());
        }
        return totals;
    }
}
