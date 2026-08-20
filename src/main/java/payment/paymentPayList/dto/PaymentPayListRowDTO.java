package payment.paymentPayList.dto;

// 사원별 급여내역 화면의 월별(급여차수별) 한 줄
public class PaymentPayListRowDTO {

    private String payYearMonth;   // 급여월 (YYYYMM)
    private int paySequence;       // 급여차수

    private long totalPayAmount;       // 지급합계 (보수월액도 동일 값을 사용)
    private long totalDeductionAmount; // 공제합계
    private long netPayAmount;         // 실지급액

    private long nationalPension;      // 국민연금
    private long healthInsurance;      // 건강보험
    private long longTermCare;         // 노인장기요양보험
    private long employmentInsurance;  // 고용보험
    private long incomeTax;            // 소득세
    private long localIncomeTax;       // 주민세(지방소득세)

    public String getPayYearMonth() { return payYearMonth; }
    public void setPayYearMonth(String payYearMonth) { this.payYearMonth = payYearMonth; }

    public int getPaySequence() { return paySequence; }
    public void setPaySequence(int paySequence) { this.paySequence = paySequence; }

    public long getTotalPayAmount() { return totalPayAmount; }
    public void setTotalPayAmount(long totalPayAmount) { this.totalPayAmount = totalPayAmount; }

    public long getTotalDeductionAmount() { return totalDeductionAmount; }
    public void setTotalDeductionAmount(long totalDeductionAmount) { this.totalDeductionAmount = totalDeductionAmount; }

    public long getNetPayAmount() { return netPayAmount; }
    public void setNetPayAmount(long netPayAmount) { this.netPayAmount = netPayAmount; }

    public long getNationalPension() { return nationalPension; }
    public void setNationalPension(long nationalPension) { this.nationalPension = nationalPension; }

    public long getHealthInsurance() { return healthInsurance; }
    public void setHealthInsurance(long healthInsurance) { this.healthInsurance = healthInsurance; }

    public long getLongTermCare() { return longTermCare; }
    public void setLongTermCare(long longTermCare) { this.longTermCare = longTermCare; }

    public long getEmploymentInsurance() { return employmentInsurance; }
    public void setEmploymentInsurance(long employmentInsurance) { this.employmentInsurance = employmentInsurance; }

    public long getIncomeTax() { return incomeTax; }
    public void setIncomeTax(long incomeTax) { this.incomeTax = incomeTax; }

    public long getLocalIncomeTax() { return localIncomeTax; }
    public void setLocalIncomeTax(long localIncomeTax) { this.localIncomeTax = localIncomeTax; }
}
