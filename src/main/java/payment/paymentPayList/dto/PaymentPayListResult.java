package payment.paymentPayList.dto;

import java.util.List;

// 사원별 급여내역 조회 결과 (조회된 월별 행 + 합계행)
// 社員別給与内訳照会結果（照会された月別行＋合計行）
public class PaymentPayListResult {

    private List<PaymentPayListRowDTO> rows;
    private PaymentPayListRowDTO totals;

    public List<PaymentPayListRowDTO> getRows() { return rows; }
    public void setRows(List<PaymentPayListRowDTO> rows) { this.rows = rows; }

    public PaymentPayListRowDTO getTotals() { return totals; }
    public void setTotals(PaymentPayListRowDTO totals) { this.totals = totals; }
}
