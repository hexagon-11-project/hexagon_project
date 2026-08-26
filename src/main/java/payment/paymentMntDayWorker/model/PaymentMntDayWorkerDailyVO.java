package payment.paymentMntDayWorker.model;

/**
 * 일자별 지급내역 (DAILY_WORK_RECORD 1행)
 * 화면 우측 급여상세 테이블의 한 행: 일자 / 지급율 / 지급액 / 소득세 / 지방소득세
 * 日別支給内訳（DAILY_WORK_RECORD 1行）
 * 画面右側給与詳細テーブルの1行：日付／支給率／支給額／所得税／地方所得税
 */
public class PaymentMntDayWorkerDailyVO {

    private Long dailyId;              // DAILY_WORK_RECORD_ID
    private Long payrollDayWorkerEmployeeId; // PAYROLL_EMPLOYEE_ID (FK)
    private String workDate;           // 일자 (yyyy-MM-dd) / 日付（yyyy-MM-dd）
    private java.math.BigDecimal rate; // 지급율 (예: 1.0) / 支給率（例：1.0）
    private Long payAmt;               // 지급액 / 支給額
    private Long incomeTax;            // 소득세 / 所得税
    private Long localTax;             // 지방소득세 / 地方所得税

    public Long getDailyId() { return dailyId; }
    public void setDailyId(Long dailyId) { this.dailyId = dailyId; }

    public Long getPayrollDayWorkerEmployeeId() { return payrollDayWorkerEmployeeId; }
    public void setPayrollDayWorkerEmployeeId(Long payrollDayWorkerEmployeeId) { this.payrollDayWorkerEmployeeId = payrollDayWorkerEmployeeId; }

    public String getWorkDate() { return workDate; }
    public void setWorkDate(String workDate) { this.workDate = workDate; }

    public java.math.BigDecimal getRate() { return rate; }
    public void setRate(java.math.BigDecimal rate) { this.rate = rate; }

    public Long getPayAmt() { return payAmt; }
    public void setPayAmt(Long payAmt) { this.payAmt = payAmt; }

    public Long getIncomeTax() { return incomeTax; }
    public void setIncomeTax(Long incomeTax) { this.incomeTax = incomeTax; }

    public Long getLocalTax() { return localTax; }
    public void setLocalTax(Long localTax) { this.localTax = localTax; }
}
