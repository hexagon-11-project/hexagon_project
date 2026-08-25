package statistics.paymentStatistics.dto;

// 연도별 개인연봉 통계 1건 (한 사원의 특정 연도 급여 요약)
public class PersonalAnnualStatistics {

    private int year;
    private long totalPayAmount;        // 연봉액(연간 지급합계, 천원 단위 아님 - 원 단위 그대로 보관)
    private Double salaryGrowthRate;    // 전년 대비 증가율(%) - 전년 데이터 없거나 0이면 null
    private long totalDeductionAmount;  // 연간 공제합계
    private long netPayAmount;          // 연간 실지급액

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public long getTotalPayAmount() { return totalPayAmount; }
    public void setTotalPayAmount(long totalPayAmount) { this.totalPayAmount = totalPayAmount; }

    public Double getSalaryGrowthRate() { return salaryGrowthRate; }
    public void setSalaryGrowthRate(Double salaryGrowthRate) { this.salaryGrowthRate = salaryGrowthRate; }

    public long getTotalDeductionAmount() { return totalDeductionAmount; }
    public void setTotalDeductionAmount(long totalDeductionAmount) { this.totalDeductionAmount = totalDeductionAmount; }

    public long getNetPayAmount() { return netPayAmount; }
    public void setNetPayAmount(long netPayAmount) { this.netPayAmount = netPayAmount; }
}
