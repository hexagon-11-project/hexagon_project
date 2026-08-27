package config.model;

import java.math.BigDecimal;
import java.sql.Date;

// 사원 근태 기록(휴가, 출장, 연장근무, 수당 등) 데이터를 담는 모델(DTO/VO) 클래스
// 社員の勤怠記録（休暇、出張、残業、手当など）データを保持するモデル（DTO/VO）クラス
public class AttendanceRecord {

	private Integer attendanceId;
	private Integer employeeId;
	private Integer attendanceTypeId;
	private Date startDate;
	private Date endDate;
	private String startTime;
	private String endTime;
	private BigDecimal dayCount;
	private BigDecimal hourCount;
	private BigDecimal allowanceAmount; // 금액(수당)
	private String description;
	private Date createdAt; // 입력일자 (등록된 시각)

	// 화면 표시용 (join 결과)
	private String attendanceName;
	private String unitCode;
	private Integer leaveTypeId;
	private String employeeName; // 근태조회 화면용 (勤怠照会画面用)
	private String department;   // 근태조회 화면용 (勤怠照会画面用)

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

	public Integer getAttendanceId() {
		return attendanceId;
	}

	public void setAttendanceId(Integer attendanceId) {
		this.attendanceId = attendanceId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getAttendanceTypeId() {
		return attendanceTypeId;
	}

	public void setAttendanceTypeId(Integer attendanceTypeId) {
		this.attendanceTypeId = attendanceTypeId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public BigDecimal getDayCount() {
		return dayCount;
	}

	public void setDayCount(BigDecimal dayCount) {
		this.dayCount = dayCount;
	}

	public BigDecimal getHourCount() {
		return hourCount;
	}

	public void setHourCount(BigDecimal hourCount) {
		this.hourCount = hourCount;
	}

	public BigDecimal getAllowanceAmount() {
		return allowanceAmount;
	}

	public void setAllowanceAmount(BigDecimal allowanceAmount) {
		this.allowanceAmount = allowanceAmount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getAttendanceName() {
		return attendanceName;
	}

	public void setAttendanceName(String attendanceName) {
		this.attendanceName = attendanceName;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public Integer getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(Integer leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	// ===== 화면 표시용 헬퍼 =====
	// ===== 画面表示用ヘルパー =====

	// 근태조회 화면 "금액(수당)" 컬럼 - 천단위 콤마, 값 없으면 "-"
	// 勤怠照会画面の「金額（手当）」カラム - 千単位カンマ、値がなければ"-"
	public String getAllowanceAmountValue() {
		if (allowanceAmount == null) {
			return "-";
		}
		return String.format("%,d", allowanceAmount.longValue());
	}

	// 근태조회 화면 "일수/시간" 컬럼 - 단위(unitCode)에 맞는 값 + 단위 라벨을 붙여서 반환
	// 勤怠照会画面の「日数／時間」カラム - 単位（unitCode）に合った値 ＋ 単位ラベルをつけて返却
	public String getCountDisplayValue() {
		BigDecimal count = "HOUR".equalsIgnoreCase(unitCode) ? hourCount : dayCount;
		if (count == null) {
			return "-";
		}
		String suffix = "HOUR".equalsIgnoreCase(unitCode) ? "시간" : "일";
		return count.stripTrailingZeros().toPlainString() + suffix;
	}
}