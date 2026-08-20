package payment.paymentPayList.dto;

import java.util.List;

// 사원별 급여내역 조회 결과 (조회된 월별 행 + 합계행)
public class PaymentPayListResult {

    private List<PaymentPayListRowDTO> rows;
    private PaymentPayListRowDTO totals;

    public List<PaymentPayListRowDTO> getRows() { return rows; }
    public void setRows(List<PaymentPayListRowDTO> rows) { this.rows = rows; }

    public PaymentPayListRowDTO getTotals() { return totals; }
    public void setTotals(PaymentPayListRowDTO totals) { this.totals = totals; }
}
