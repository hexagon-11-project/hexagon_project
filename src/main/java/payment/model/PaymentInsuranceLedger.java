package payment.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 4대보험 대장 모델.
 * 귀속연월·급여차수 조회 헤더(정산기간, 급여지급일)와
 * 사원별 지급 건의 구분·성명·입사일·부서·직위·4대보험 공제액을 한 파일에서 담는다.
 * 공제금액이 없으면 0원으로 둔다.
 */
public class PaymentInsuranceLedger {

	public static final String DEDUCTION_NATIONAL_PENSION = "국민연금";
	public static final String DEDUCTION_HEALTH_INSURANCE = "건강보험";
	public static final String DEDUCTION_LONG_TERM_CARE = "장기요양보험";
	public static final String DEDUCTION_EMPLOYMENT_INSURANCE = "고용보험";

	/** 급여아이디 */
	private int payrollId;

	/** 회사아이디 */
	private int companyId;

	/** 귀속연월 YYYYMM */
	private String payYearMonth;

	/** 급여차수 */
	private int paySequence;

	/** 정산시작일 */
	private Date settlementStartDate;

	/** 정산종료일 */
	private Date settlementEndDate;

	/** 급여지급일 */
	private Date paymentDate;

	/** 사원별급여아이디 */
	private int payrollEmployeeId;

	/** 사원아이디 */
	private int employeeId;

	/** 구분 (고용형태) */
	private String employmentType;

	/** 성명 */
	private String employeeName;

	/** 입사일 */
	private Date hireDate;

	/** 부서 */
	private String department;

	/** 직위 */
	private String position;

	/** 국민연금 */
	private long nationalPension;

	/** 건강보험 */
	private long healthInsurance;

	/** 장기요양보험 */
	private long longTermCare;

	/** 고용보험 */
	private long employmentInsurance;

	/** 해당 급여 지급 내역이 있는 사원 목록 */
	private List<PaymentInsuranceLedger> employees = new ArrayList<>();

	public PaymentInsuranceLedger() {
	}

	public int getPayrollId() {
		return payrollId;
	}

	public void setPayrollId(int payrollId) {
		this.payrollId = payrollId;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getPayYearMonth() {
		return payYearMonth;
	}

	public void setPayYearMonth(String payYearMonth) {
		this.payYearMonth = payYearMonth;
	}

	public int getPaySequence() {
		return paySequence;
	}

	public void setPaySequence(int paySequence) {
		this.paySequence = paySequence;
	}

	public Date getSettlementStartDate() {
		return settlementStartDate;
	}

	public void setSettlementStartDate(Date settlementStartDate) {
		this.settlementStartDate = settlementStartDate;
	}

	public Date getSettlementEndDate() {
		return settlementEndDate;
	}

	public void setSettlementEndDate(Date settlementEndDate) {
		this.settlementEndDate = settlementEndDate;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public int getPayrollEmployeeId() {
		return payrollEmployeeId;
	}

	public void setPayrollEmployeeId(int payrollEmployeeId) {
		this.payrollEmployeeId = payrollEmployeeId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public long getNationalPension() {
		return nationalPension;
	}

	public void setNationalPension(long nationalPension) {
		this.nationalPension = nationalPension;
	}

	public long getHealthInsurance() {
		return healthInsurance;
	}

	public void setHealthInsurance(long healthInsurance) {
		this.healthInsurance = healthInsurance;
	}

	public long getLongTermCare() {
		return longTermCare;
	}

	public void setLongTermCare(long longTermCare) {
		this.longTermCare = longTermCare;
	}

	public long getEmploymentInsurance() {
		return employmentInsurance;
	}

	public void setEmploymentInsurance(long employmentInsurance) {
		this.employmentInsurance = employmentInsurance;
	}

	/** 국민연금+건강보험+장기요양보험+고용보험 근로자(또는 사업주) 합계. 값이 없으면 0원. */
	public long getInsuranceTotal() {
		return nationalPension + healthInsurance + longTermCare + employmentInsurance;
	}

	/** 4대보험 사업주+근로자 총합. 항목별 합계(2배)를 모두 더한 값. */
	public long getGrandTotal() {
		return getInsuranceTotal() * 2;
	}

	public List<PaymentInsuranceLedger> getEmployees() {
		return employees;
	}

	public void setEmployees(List<PaymentInsuranceLedger> employees) {
		this.employees = employees != null ? employees : new ArrayList<>();
	}
}
