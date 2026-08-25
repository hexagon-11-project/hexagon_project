package statistics.model;

/**
 * 연도별 전체급여 통계 1건.
 * 年度別給与総額統計1件。
 *
 */
public class AnnualTotalStatistics {

	/** 연도
	 * 年度。 */
	private int year;

	/** 연간 전체 급여액
	 * 年間給与総額。 */
	private long totalSalaryAmount;

	// 전년 대비 급여 증가율 (%)
	// 前年比給与増加率 (%)。
	private Double salaryGrowthRate;

	/** 연간 사원수 평균 (월별 급여인원 평균 등)
	 * 年間社員数平均（月別給与人数平均など）。 */
	private double avgEmployeeCount;

	// 전년 대비 사원수 증가율 (%)
	// 前年比社員数増加率 (%)。
	private Double employeeGrowthRate;

	public AnnualTotalStatistics() {
	}

	public AnnualTotalStatistics(int year, long totalSalaryAmount, Double salaryGrowthRate,
			double avgEmployeeCount, Double employeeGrowthRate) {
		this.year = year;
		this.totalSalaryAmount = totalSalaryAmount;
		this.salaryGrowthRate = salaryGrowthRate;
		this.avgEmployeeCount = avgEmployeeCount;
		this.employeeGrowthRate = employeeGrowthRate;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public long getTotalSalaryAmount() {
		return totalSalaryAmount;
	}

	public void setTotalSalaryAmount(long totalSalaryAmount) {
		this.totalSalaryAmount = totalSalaryAmount;
	}

	public Double getSalaryGrowthRate() {
		return salaryGrowthRate;
	}

	public void setSalaryGrowthRate(Double salaryGrowthRate) {
		this.salaryGrowthRate = salaryGrowthRate;
	}

	public double getAvgEmployeeCount() {
		return avgEmployeeCount;
	}

	public void setAvgEmployeeCount(double avgEmployeeCount) {
		this.avgEmployeeCount = avgEmployeeCount;
	}

	public Double getEmployeeGrowthRate() {
		return employeeGrowthRate;
	}

	public void setEmployeeGrowthRate(Double employeeGrowthRate) {
		this.employeeGrowthRate = employeeGrowthRate;
	}
}
