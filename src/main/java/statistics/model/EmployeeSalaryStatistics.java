package statistics.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 사원별 해당 연·월 급여 통계 1건.
 * 연도, 월, 이름으로 조회한 사원의 지급내역·공제항목과 합계를 담는다.
 */
public class EmployeeSalaryStatistics {

	/** 연도 */
	private int year;

	/** 월 (1~12) */
	private int month;

	/** 사원아이디 */
	private String employeeId;

	/** 사원이름 */
	private String employeeName;

	/** 지급합계 */
	private long totalPayAmount;

	/** 공제합계 */
	private long totalDeductionAmount;

	/** 실지급액 (지급합계 - 공제합계) */
	private long netPayAmount;

	/** 지급항목 비율 (%) — 지급합계 / (지급합계 + 공제합계). 합계가 0이면 null */
	private Double paymentRatio;

	/** 공제항목 비율 (%) — 공제합계 / (지급합계 + 공제합계). 합계가 0이면 null */
	private Double deductionRatio;

	/** 지급 세부항목 (기본급, 식비, 수당 등) */
	private List<SalaryItemStatistics> payItems = new ArrayList<>();

	/** 공제 세부항목 (국민연금, 건강보험 등) */
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
