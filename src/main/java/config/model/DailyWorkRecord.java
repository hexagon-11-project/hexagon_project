package config.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;

// DAILY_WORK_RECORD 테이블 모델. 이 테이블은 payment.paymentMntDayWorker(일용직 급여입력) 기능과
// 공유한다 - 근무일자가 속한 귀속연월 + 급여-01차의 PAYROLL_EMPLOYEE_ID로 자동 귀속시켜 저장하므로,
// 이 화면(diligence.dailyworkrecord)에서 근무기록을 등록하면 급여입력관리 화면에도 바로 반영된다.
public class DailyWorkRecord {

	private Integer dailyWorkRecordId;
	private Integer employeeId;
	private String workSiteName;
	private Date workDate;
	private BigDecimal dailyWage;         // 일당
	private BigDecimal payRate;           // 지급율
	private BigDecimal payAmount;         // 지급액 (일당 * 지급율)
	private BigDecimal incomeTaxAmount;   // 소득세
	private BigDecimal localIncomeTaxAmount; // 지방소득세
	private BigDecimal netPayAmount;      // 실지급액

	// 화면 표시용 (join 결과)
	private String employeeName;
	private String department;

	public Integer getDailyWorkRecordId() {
		return dailyWorkRecordId;
	}

	public void setDailyWorkRecordId(Integer dailyWorkRecordId) {
		this.dailyWorkRecordId = dailyWorkRecordId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public String getWorkSiteName() {
		return workSiteName;
	}

	public void setWorkSiteName(String workSiteName) {
		this.workSiteName = workSiteName;
	}

	public Date getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}

	public BigDecimal getDailyWage() {
		return dailyWage;
	}

	public void setDailyWage(BigDecimal dailyWage) {
		this.dailyWage = dailyWage;
	}

	public BigDecimal getPayRate() {
		return payRate;
	}

	public void setPayRate(BigDecimal payRate) {
		this.payRate = payRate;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public BigDecimal getIncomeTaxAmount() {
		return incomeTaxAmount;
	}

	public void setIncomeTaxAmount(BigDecimal incomeTaxAmount) {
		this.incomeTaxAmount = incomeTaxAmount;
	}

	public BigDecimal getLocalIncomeTaxAmount() {
		return localIncomeTaxAmount;
	}

	public void setLocalIncomeTaxAmount(BigDecimal localIncomeTaxAmount) {
		this.localIncomeTaxAmount = localIncomeTaxAmount;
	}

	public BigDecimal getNetPayAmount() {
		return netPayAmount;
	}

	public void setNetPayAmount(BigDecimal netPayAmount) {
		this.netPayAmount = netPayAmount;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	// ===== 화면 표시용 헬퍼 =====

	public String getDailyWageValue() {
		return formatWon(dailyWage);
	}

	public String getPayAmountValue() {
		return formatWon(payAmount);
	}

	public String getIncomeTaxAmountValue() {
		return formatWon(incomeTaxAmount);
	}

	public String getLocalIncomeTaxAmountValue() {
		return formatWon(localIncomeTaxAmount);
	}

	public String getNetPayAmountValue() {
		return formatWon(netPayAmount);
	}

	// 일용직 근무조회 화면 "세금" 컬럼 - 소득세 + 지방소득세 합계
	public String getTotalTaxValue() {
		BigDecimal income = incomeTaxAmount == null ? BigDecimal.ZERO : incomeTaxAmount;
		BigDecimal local = localIncomeTaxAmount == null ? BigDecimal.ZERO : localIncomeTaxAmount;
		return formatWon(income.add(local));
	}

	private String formatWon(BigDecimal value) {
		if (value == null) {
			return "-";
		}
		return String.format("%,d", value.setScale(0, RoundingMode.DOWN).longValue());
	}
}
