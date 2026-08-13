package statistics.model;

/**
 * 연도별 전체급여 통계 1건.
 * 선택 연도부터 10년 구간을 표시할 때 연도 단위로 사용한다.
 */
public class AnnualTotalStatistics {

	/** 연도 */
	private int year;

	/** 연간 전체 급여액 */
	private long totalSalaryAmount;

	/** 전년 대비 급여 증가율 (%) — 전년 데이터가 없으면 null */
	private Double salaryGrowthRate;

	/** 연간 사원수 평균 (월별 급여인원 평균 등) */
	private double avgEmployeeCount;

	/** 전년 대비 사원수 증가율 (%) — 전년 데이터가 없으면 null */
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
