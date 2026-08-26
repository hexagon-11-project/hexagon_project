package payment.paymentMntDayWorker.dto;

import java.util.Date;

// 일용직 급여차수(헤더) 정보 DTO - PAYROLL_DAYWORKER 1행에 대응
// 日雇い給与回（ヘッダー）情報DTO - PAYROLL_DAYWORKER 1行に対応
public class PaymentMntDayWorkerPayrollDTO {

    private int payrollDayWorkerId;    // 급여(일용직)아이디 / 給与（日雇い）ID
    private int companyId;             // 회사아이디 / 会社ID
    private String payYearMonth;       // 귀속연월 - YYYYMM / 帰属年月 - YYYYMM
    private int paySequence;           // 급여차수 / 給与回
    private Date settlementStartDate;  // 정산시작일 / 精算開始日
    private Date settlementEndDate;    // 정산종료일 / 精算終了日
    private Date paymentDate;          // 급여지급일 / 給与支給日

    public int getPayrollDayWorkerId() { return payrollDayWorkerId; }
    public void setPayrollDayWorkerId(int payrollDayWorkerId) { this.payrollDayWorkerId = payrollDayWorkerId; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getPayYearMonth() { return payYearMonth; }
    public void setPayYearMonth(String payYearMonth) { this.payYearMonth = payYearMonth; }

    public int getPaySequence() { return paySequence; }
    public void setPaySequence(int paySequence) { this.paySequence = paySequence; }

    public Date getSettlementStartDate() { return settlementStartDate; }
    public void setSettlementStartDate(Date settlementStartDate) { this.settlementStartDate = settlementStartDate; }

    public Date getSettlementEndDate() { return settlementEndDate; }
    public void setSettlementEndDate(Date settlementEndDate) { this.settlementEndDate = settlementEndDate; }

    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
}
