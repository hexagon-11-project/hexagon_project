package config.model;

import java.sql.Date;

// EMPLOYEE_LEAVE 테이블 매핑 (사원별 휴가부과)
// 목록 화면에 같이 뿌려야 하는 사원 기본정보(이름/부서 등)도 조회 시 join해서 같이 담아둔다.
// EMPLOYEE_LEAVE テーブルマッピング（社員別休暇付与）
// 一覧画面に一緒に表示すべき社員基本情報（名前／部署など）も、照会時にJOINして一緒に保持する。
public class EmployeeLeave {

	private Integer employeeLeaveId;
	private Integer employeeId;
	private Integer leaveTypeId;
	private java.math.BigDecimal grantedDays; // 부과일수 (付与日数)

	// 화면 표시용 (EMPLOYEE 테이블 join 결과)
	// 画面表示用（EMPLOYEEテーブルJOIN結果）
	private String employmentType;
	private String employmentStatus; // 재직/퇴직 (在職／退職)
	private String employeeNo;
	private String employeeName;
	private String department;
	private String position;
	private Date hireDate; // 입사일 (入社日)

	public Integer getEmployeeLeaveId() {
		return employeeLeaveId;
	}

	public void setEmployeeLeaveId(Integer employeeLeaveId) {
		this.employeeLeaveId = employeeLeaveId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(Integer leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	public java.math.BigDecimal getGrantedDays() {
		return grantedDays;
	}

	public void setGrantedDays(java.math.BigDecimal grantedDays) {
		this.grantedDays = grantedDays;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public String getEmploymentStatus() {
		return employmentStatus;
	}

	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
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

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	// ===== 화면 표시용 헬퍼 =====
	// ===== 画面表示用ヘルパー =====

	// 부과일수를 소수점 아래 불필요한 0을 정리하여 깔끔한 문자열로 반환 (값이 없으면 "0")
	// 付与日数を小数点以下の不要なゼロを整理してすっきりした文字列として返却（値がなければ"0"）
	public String getGrantedDaysValue() {
		return grantedDays == null ? "0" : grantedDays.stripTrailingZeros().toPlainString();
	}
}