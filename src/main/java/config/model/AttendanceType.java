package config.model;

public class AttendanceType {

	private Integer attendanceTypeId;
	private Integer companyId;
	private String attendanceCode;
	private String attendanceName;
	private String unitCode;            // UNIT_CODE: DAY/HOUR 등
	private String attendanceGroupCode; // ATTENDANCE_GROUP_CODE: 휴가/지각조퇴/기타/연장근무 등
	private Integer leaveTypeId;        // 휴가공제 연결 (LEAVE_TYPE FK, 없으면 null = "-")
	private String leaveTypeName;       // 목록에 보여줄 연결된 휴가항목명 (조회 시 join해서 채움)
	private String workTimeLinkCode;    // 근로시간 반영 방식 선택값 (nullable)
	private String useYn;

	public AttendanceType() {
	}

	public Integer getAttendanceTypeId() {
		return attendanceTypeId;
	}

	public void setAttendanceTypeId(Integer attendanceTypeId) {
		this.attendanceTypeId = attendanceTypeId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getAttendanceCode() {
		return attendanceCode;
	}

	public void setAttendanceCode(String attendanceCode) {
		this.attendanceCode = attendanceCode;
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

	public String getAttendanceGroupCode() {
		return attendanceGroupCode;
	}

	public void setAttendanceGroupCode(String attendanceGroupCode) {
		this.attendanceGroupCode = attendanceGroupCode;
	}

	public Integer getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(Integer leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	public String getLeaveTypeName() {
		return leaveTypeName;
	}

	public void setLeaveTypeName(String leaveTypeName) {
		this.leaveTypeName = leaveTypeName;
	}

	public String getWorkTimeLinkCode() {
		return workTimeLinkCode;
	}

	public void setWorkTimeLinkCode(String workTimeLinkCode) {
		this.workTimeLinkCode = workTimeLinkCode;
	}

	public String getUseYn() {
		return useYn;
	}

	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

	// ===== 화면 표시용 라벨 헬퍼 =====

	public String getUnitLabel() {
		if ("DAY".equalsIgnoreCase(unitCode)) return "일";
		if ("HOUR".equalsIgnoreCase(unitCode)) return "시간";
		return unitCode == null ? "-" : unitCode;
	}

	public String getLeaveTypeDeductionLabel() {
		return leaveTypeId == null ? "-" : (leaveTypeName == null ? "-" : leaveTypeName);
	}

	public String getUseLabel() {
		return "Y".equalsIgnoreCase(useYn) ? "사용" : "사용안함";
	}
}
