package statistics.model;

/**
 * 월별 전체급여 통계 1건.
 * 선택 연도의 1월부터 12월까지를 표시할 때 월 단위로 사용한다.
 */
public class MonthlyTotalStatistics {

	/** 연도 */
	private int year;

	/** 월 (1~12) */
	private int month;

	/** 해당 월 전체 급여액 */
	private long totalSalaryAmount;

	/** 전월 대비 급여 증가율 (%) — 전월 데이터가 없으면 null */
	private Double salaryGrowthRate;

	/** 해당 월 사원수 */
	private int employeeCount;

	/** 전월 대비 사원수 증가율 (%) — 전월 데이터가 없으면 null */
	private Double employeeGrowthRate;

	public MonthlyTotalStatistics() {
	}

	public MonthlyTotalStatistics(int year, int month, long totalSalaryAmount, Double salaryGrowthRate,
			int employeeCount, Double employeeGrowthRate) {
		this.year = year;
		this.month = month;
		this.totalSalaryAmount = totalSalaryAmount;
		this.salaryGrowthRate = salaryGrowthRate;
		this.employeeCount = employeeCount;
		this.employeeGrowthRate = employeeGrowthRate;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
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

	public int getEmployeeCount() {
		return employeeCount;
	}

	public void setEmployeeCount(int employeeCount) {
		this.employeeCount = employeeCount;
	}

	public Double getEmployeeGrowthRate() {
		return employeeGrowthRate;
	}

	public void setEmployeeGrowthRate(Double employeeGrowthRate) {
		this.employeeGrowthRate = employeeGrowthRate;
	}
}
