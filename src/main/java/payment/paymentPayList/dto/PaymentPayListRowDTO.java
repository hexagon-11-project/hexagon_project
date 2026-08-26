package payment.paymentPayList.dto;

// 사원별 급여내역 화면의 월별(급여차수별) 한 줄
// 社員別給与内訳画面の月別（給与回別）1行
public class PaymentPayListRowDTO {

    private String payYearMonth;   // 급여월 (YYYYMM) / 給与月（YYYYMM）
    private int paySequence;       // 급여차수 / 給与回

    private long totalPayAmount;       // 지급합계 (보수월액도 동일 값을 사용) / 支給合計（報酬月額も同一値を使用）
    private long totalDeductionAmount; // 공제합계 / 控除合計
    private long netPayAmount;         // 실지급액 / 実支給額

    private long nationalPension;      // 국민연금 / 国民年金
    private long healthInsurance;      // 건강보험 / 健康保険
    private long longTermCare;         // 노인장기요양보험 / 老人長期療養保険
    private long employmentInsurance;  // 고용보험 / 雇用保険
    private long incomeTax;            // 소득세 / 所得税
    private long localIncomeTax;       // 주민세(지방소득세) / 住民税（地方所得税）

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
