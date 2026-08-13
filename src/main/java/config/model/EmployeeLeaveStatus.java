package config.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

// [휴가일수 현황] 팝업 전용 표시 모델
// EMPLOYEE_LEAVE + LEAVE_TYPE + EMPLOYEE 조회 결과에, 근태기록 합계로 계산한 사용일수까지 같이 담는다.
public class EmployeeLeaveStatus {

	private String employmentType; // 구분
	private String employeeName;   // 성명
	private String position;       // 직위
	private String leaveName;      // 휴가항목
	private BigDecimal totalDays;  // 전체 (EMPLOYEE_LEAVE.GRANTED_DAYS)
	private BigDecimal usedDays;   // 사용 (해당 휴가항목에 연결된 근태기록 DAY_COUNT 합계)

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

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getLeaveName() {
		return leaveName;
	}

	public void setLeaveName(String leaveName) {
		this.leaveName = leaveName;
	}

	public BigDecimal getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(BigDecimal totalDays) {
		this.totalDays = totalDays;
	}

	public BigDecimal getUsedDays() {
		return usedDays;
	}

	public void setUsedDays(BigDecimal usedDays) {
		this.usedDays = usedDays;
	}

	// ===== 화면 표시용 헬퍼 =====

	public String getTotalDaysValue() {
		BigDecimal value = totalDays == null ? BigDecimal.ZERO : totalDays;
		return value.setScale(1, RoundingMode.HALF_UP).toPlainString();
	}

	public String getUsedDaysValue() {
		BigDecimal value = usedDays == null ? BigDecimal.ZERO : usedDays;
		return value.setScale(1, RoundingMode.HALF_UP).toPlainString();
	}

	public String getRemainingDaysValue() {
		BigDecimal total = totalDays == null ? BigDecimal.ZERO : totalDays;
		BigDecimal used = usedDays == null ? BigDecimal.ZERO : usedDays;
		return total.subtract(used).stripTrailingZeros().toPlainString();
	}
}
