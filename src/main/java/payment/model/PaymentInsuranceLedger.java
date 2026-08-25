package payment.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 4대보험 대장 한 건을 담는 모델.
 * 社会保険(4大保険)台帳の1件を格納するモデル。
 */
public class PaymentInsuranceLedger {

	// 국민연금 공제항목명
	// 国民年金の控除項目名。
	public static final String DEDUCTION_NATIONAL_PENSION = "국민연금";
	// 건강보험 공제항목명
	// 健康保険の控除項目名。
	public static final String DEDUCTION_HEALTH_INSURANCE = "건강보험";
	// 장기요양보험 공제항목명
	// 介護保険の控除項目名。
	public static final String DEDUCTION_LONG_TERM_CARE = "장기요양보험";
	// 고용보험 공제항목명
	// 雇用保険の控除項目名。
	public static final String DEDUCTION_EMPLOYMENT_INSURANCE = "고용보험";
	// 사장 직위 판별 문자열
	// 社長職位の判定文字列。
	public static final String POSITION_PRESIDENT = "사장";

	// 급여아이디
	// 給与ID。
	private int payrollId;

	// 회사아이디
	// 会社ID。
	private int companyId;

	// 귀속연월
	// 帰属年月。
	private String payYearMonth;

	// 급여차수
	// 給与次数。
	private int paySequence;

	// 정산시작일
	// 精算開始日。
	private Date settlementStartDate;

	// 정산종료일
	// 精算終了日。
	private Date settlementEndDate;

	// 급여지급일
	// 給与支給日。
	private Date paymentDate;

	// 사원별급여아이디
	// 社員別給与ID。
	private int payrollEmployeeId;

	// 사원아이디
	// 社員ID。
	private int employeeId;

	// 구분(고용형태)
	// 区分(雇用形態)。
	private String employmentType;

	// 성명
	// 氏名。
	private String employeeName;

	// 입사일
	// 入社日。
	private Date hireDate;

	// 부서
	// 部署。
	private String department;

	// 직위
	// 職位。
	private String position;

	// 국민연금 공제액
	// 国民年金の控除額。
	private long nationalPension;

	// 건강보험 공제액
	// 健康保険の控除額。
	private long healthInsurance;

	// 장기요양보험 공제액
	// 介護保険の控除額。
	private long longTermCare;

	// 고용보험 공제액
	// 雇用保険の控除額。
	private long employmentInsurance;

	// 해당 급여 지급 사원 목록
	// 当該給与支給の社員一覧。
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

	/**
	 * 직위가 사장인지 판별한다.
	 * 職位が社長かを判定する。
	 */
	public boolean isPresident() {
		// null이면 trim을 건너뛰고 equals 비교만 한다
		// nullならtrimを省略し、equals比較だけを行う。
		return POSITION_PRESIDENT.equals(position == null ? null : position.trim());
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

	/**
	 * 4대보험 근로자(또는 사업주) 한쪽 합계를 구한다.
	 * 社会保険(4大保険)の労働者(または事業主)一方の合計を求める。
	 */
	public long getInsuranceTotal() {
		return nationalPension + healthInsurance + longTermCare + employmentInsurance;
	}

	/**
	 * 4대보험 사업주+근로자 총합을 구한다.
	 * 社会保険(4大保険)の事業主+労働者の総計を求める。
	 */
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
