package statistics.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 사원별 해당 연·월 급여 통계 1건.
 * 社員別の該当年・月給与統計1件。
 *
 */
public class EmployeeSalaryStatistics {

	/** 연도
	 * 年度。 */
	private int year;

	/** 월 (1~12)
	 * 月 (1〜12)。 */
	private int month;

	/** 사원아이디
	 * 社員ID。 */
	private String employeeId;

	/** 사원이름
	 * 社員名。 */
	private String employeeName;

	/** 지급합계
	 * 支給合計。 */
	private long totalPayAmount;

	/** 공제합계
	 * 控除合計。 */
	private long totalDeductionAmount;

	/** 실지급액 (지급합계 - 공제합계)
	 * 実支給額（支給合計 - 控除合計）。 */
	private long netPayAmount;

	// 지급항목 비율 (%)
	// 支給項目比率 (%)。
	private Double paymentRatio;

	// 공제항목 비율 (%)
	// 控除項目比率 (%)。
	private Double deductionRatio;

	// 지급 세부항목 (기본급, 식비, 수당 등)
	// 支給明細項目（基本給、食費、手当など）。
	private List<SalaryItemStatistics> payItems = new ArrayList<>();

	// 공제 세부항목 (국민연금, 건강보험 등)
	// 控除明細項目（国民年金、健康保険など）。
	private List<SalaryItemStatistics> deductionItems = new ArrayList<>();

	public EmployeeSalaryStatistics() {
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

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public long getTotalPayAmount() {
		return totalPayAmount;
	}

	public void setTotalPayAmount(long totalPayAmount) {
		this.totalPayAmount = totalPayAmount;
	}

	public long getTotalDeductionAmount() {
		return totalDeductionAmount;
	}

	public void setTotalDeductionAmount(long totalDeductionAmount) {
		this.totalDeductionAmount = totalDeductionAmount;
	}

	public long getNetPayAmount() {
		return netPayAmount;
	}

	public void setNetPayAmount(long netPayAmount) {
		this.netPayAmount = netPayAmount;
	}

	public Double getPaymentRatio() {
		return paymentRatio;
	}

	public void setPaymentRatio(Double paymentRatio) {
		this.paymentRatio = paymentRatio;
	}

	public Double getDeductionRatio() {
		return deductionRatio;
	}

	public void setDeductionRatio(Double deductionRatio) {
		this.deductionRatio = deductionRatio;
	}

	public List<SalaryItemStatistics> getPayItems() {
		return payItems;
	}

	public void setPayItems(List<SalaryItemStatistics> payItems) {
		this.payItems = payItems != null ? payItems : new ArrayList<>();
	}

	public List<SalaryItemStatistics> getDeductionItems() {
		return deductionItems;
	}

	public void setDeductionItems(List<SalaryItemStatistics> deductionItems) {
		this.deductionItems = deductionItems != null ? deductionItems : new ArrayList<>();
	}
}
