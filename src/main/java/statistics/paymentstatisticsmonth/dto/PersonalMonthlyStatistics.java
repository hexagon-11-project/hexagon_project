package statistics.paymentstatisticsmonth.dto;

// 월별 개인급여 통계 1건 (한 사원의 특정 연도, 특정 월의 급여 요약)
public class PersonalMonthlyStatistics {

    private int year;
    private int month;
    private long totalPayAmount;        // 월급여액(원 단위 그대로 보관)
    private long totalDeductionAmount;  // 월 공제합계
    private long netPayAmount;          // 월 실지급액

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public long getTotalPayAmount() { return totalPayAmount; }
    public void setTotalPayAmount(long totalPayAmount) { this.totalPayAmount = totalPayAmount; }

    public long getTotalDeductionAmount() { return totalDeductionAmount; }
    public void setTotalDeductionAmount(long totalDeductionAmount) { this.totalDeductionAmount = totalDeductionAmount; }

    public long getNetPayAmount() { return netPayAmount; }
    public void setNetPayAmount(long netPayAmount) { this.netPayAmount = netPayAmount; }
}
