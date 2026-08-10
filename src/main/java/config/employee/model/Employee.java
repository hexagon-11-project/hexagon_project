package config.employee.model;

import java.sql.Date;

public class Employee {
	// DB EMPLOYEE 테이블 컬럼에 맞춘 변수명 선언
	private int employeeId; // 실제 PK (EMPLOYEE_ID, 시퀀스 채번) - 하위 테이블 FK로 사용
	private String employeeNo;
	private String employmentType;
	private String employeeName;
	private String employeeNameEn;
	private Date hireDate;
	private Date resignDate;
	private String department;
	private String position;
	private String domForYn;
	private String residentRegNo;
	private String phone;
	private String mobile;
	private String email;
	private String sns;
	private String bankName;    // 급여를 입금할 은행명
	private String bankAccount; // 급여를 입금할 계좌번호
	private String retirementTypeCode;  // 퇴직구분코드
	private String retirementReason;    // 퇴직사유
	private String postRetirementPhone; // 퇴직 후 연락처
	private String empIncomeType; // 갑근세
	private int baseWageAmount; // 기본급
	private int nationalPensionBaseAmount;  // 국민연금 기준소득월액
	private int healthInsuranceBaseAmount;  // 건강보험 보수월액
	private int employmentInsuranceAmount;  // 고용보험 보수월액
	
	private String retirementYn; // 재직상태 구분 추가(이승준/ 260809)
	private String birthDate; // 사원현황관리 생년월일 추가(이승준/ 260809)

	// ==========================================
	// Getters and Setters
	// ==========================================

	// [수정 완료] String -> int 로 변경!
	public int getBaseWageAmount() {
		return baseWageAmount;
	}

	public void setBaseWageAmount(int baseWageAmount) {
		this.baseWageAmount = baseWageAmount;
	}

	public int getNationalPensionBaseAmount() {
		return nationalPensionBaseAmount;
	}

	public void setNationalPensionBaseAmount(int nationalPensionBaseAmount) {
		this.nationalPensionBaseAmount = nationalPensionBaseAmount;
	}

	public int getHealthInsuranceBaseAmount() {
		return healthInsuranceBaseAmount;
	}

	public void setHealthInsuranceBaseAmount(int healthInsuranceBaseAmount) {
		this.healthInsuranceBaseAmount = healthInsuranceBaseAmount;
	}

	public int getEmploymentInsuranceAmount() {
		return employmentInsuranceAmount;
	}

	public void setEmploymentInsuranceAmount(int employmentInsuranceAmount) {
		this.employmentInsuranceAmount = employmentInsuranceAmount;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
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

	public String getEmployeeNameEn() {
		return employeeNameEn;
	}

	public void setEmployeeNameEn(String employeeNameEn) {
		this.employeeNameEn = employeeNameEn;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	public Date getResignDate() {
		return resignDate;
	}

	public void setResignDate(Date resignDate) {
		this.resignDate = resignDate;
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

	public String getDomForYn() {
		return domForYn;
	}

	public void setDomForYn(String domForYn) {
		this.domForYn = domForYn;
	}

	public String getResidentRegNo() {
		return residentRegNo;
	}

	public void setResidentRegNo(String residentRegNo) {
		this.residentRegNo = residentRegNo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSns() {
		return sns;
	}

	public void setSns(String sns) {
		this.sns = sns;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getRetirementTypeCode() {
		return retirementTypeCode;
	}

	public void setRetirementTypeCode(String retirementTypeCode) {
		this.retirementTypeCode = retirementTypeCode;
	}

	public String getRetirementReason() {
		return retirementReason;
	}

	public void setRetirementReason(String retirementReason) {
		this.retirementReason = retirementReason;
	}

	public String getPostRetirementPhone() {
		return postRetirementPhone;
	}

	public void setPostRetirementPhone(String postRetirementPhone) {
		this.postRetirementPhone = postRetirementPhone;
	}

	public String getEmpIncomeType() {
		return empIncomeType;
	}

	public void setEmpIncomeType(String empIncomeType) {
		this.empIncomeType = empIncomeType;
	}

	// 재직상태 구분 추가 (이승준/260809)
	public String getRetirementYn() {
		return retirementYn;
	}

	public void setRetirementYn(String retirementYn) {
		this.retirementYn = retirementYn;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	
	
	
	
}