package payment.paymentMnt.dto;

/** 일용직 사원의 DAILY_WORK_RECORD(일자별 근무기록) 합계.
 *  급여입력/관리 일반 화면에서 일용직 사원 클릭 시 지급/공제 상세를 자동으로 채우기 위함. */
public class PaymentMntDailyWorkSummary {

    public long sumPay;
    public long sumIncomeTax;
    public long sumLocalTax;
    public long recordCount;

    public long getSumPay() { return sumPay; }
    public long getSumIncomeTax() { return sumIncomeTax; }
    public long getSumLocalTax() { return sumLocalTax; }
    public long getRecordCount() { return recordCount; }
}
