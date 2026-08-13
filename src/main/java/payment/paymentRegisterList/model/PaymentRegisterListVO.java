package payment.paymentRegisterList.model;

// PAYROLL 테이블 1행에 대응하는 모델(VO). 급여대장의 급여차수 신규등록/수정 기능에서 사용 예정
public class PaymentRegisterListVO {

    private Long payrollId;
    private Long companyId;
    private String payYearMonth;
    private Integer paySequence;
    private String settlementStartDate;
    private String settlementEndDate;
    private String paymentDate;
    private String regId;
    private String modId;

    public Long getPayrollId() { return payrollId; }
    public void setPayrollId(Long payrollId) { this.payrollId = payrollId; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getPayYearMonth() { return payYearMonth; }
    public void setPayYearMonth(String payYearMonth) { this.payYearMonth = payYearMonth; }

    public Integer getPaySequence() { return paySequence; }
    public void setPaySequence(Integer paySequence) { this.paySequence = paySequence; }

    public String getSettlementStartDate() { return settlementStartDate; }
    public void setSettlementStartDate(String settlementStartDate) { this.settlementStartDate = settlementStartDate; }

    public String getSettlementEndDate() { return settlementEndDate; }
    public void setSettlementEndDate(String settlementEndDate) { this.settlementEndDate = settlementEndDate; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public String getRegId() { return regId; }
    public void setRegId(String regId) { this.regId = regId; }

    public String getModId() { return modId; }
    public void setModId(String modId) { this.modId = modId; }
}
