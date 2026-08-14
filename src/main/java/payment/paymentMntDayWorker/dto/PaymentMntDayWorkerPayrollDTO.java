package payment.paymentMntDayWorker.dto;

import java.util.Date;

// 일용직 급여차수(헤더) 정보 DTO - PAYROLL_DAYWORKER 1행에 대응
public class PaymentMntDayWorkerPayrollDTO {

    private int payrollDayWorkerId;    // 급여(일용직)아이디
    private int companyId;             // 회사아이디
    private String payYearMonth;       // 귀속연월 - YYYYMM
    private int paySequence;           // 급여차수
    private Date settlementStartDate;  // 정산시작일
    private Date settlementEndDate;    // 정산종료일
    private Date paymentDate;          // 급여지급일

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
