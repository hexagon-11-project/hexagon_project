package config.model;

// 근태 항목 설정(휴가, 지각, 조퇴, 연장근무 등 마스터 정의) 정보를 담는 모델(DTO/VO) 클래스
// 勤怠項目設定（休暇、遅刻、早退、残業などのマスター定義）情報を保持するモデル（DTO/VO）クラス
public class AttendanceType {

	private Integer attendanceTypeId;
	private Integer companyId;
	private String attendanceCode;
	private String attendanceName;
	private String unitCode;            // UNIT_CODE: DAY/HOUR 등 (단위코드: 일/시간 등)
	private String attendanceGroupCode; // ATTENDANCE_GROUP_CODE: 휴가/지각조퇴/기타/연장근무 등 (근태그룹코드)
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
	// ===== 画面表示用ラベルヘルパー =====

	// 단위 코드(DAY/HOUR)를 사용자가 보기 편한 한글 라벨("일"/"시간")로 변환해 주는 헬퍼 메서드
	// 単位コード（DAY/HOUR）をユーザーが分かりやすいハングルラベル（"日"/"時間"）に変換してくれるヘルパーメソッド
	public String getUnitLabel() {
		if ("DAY".equalsIgnoreCase(unitCode)) return "일";
		if ("HOUR".equalsIgnoreCase(unitCode)) return "시간";
		return unitCode == null ? "-" : unitCode;
	}

	// 연계된 휴가 공제 항목명이 있으면 그 이름을, 없으면 "-"를 리턴
	// 連携された休暇控除項目名があればその名前を、なければ"-"をリターン
	public String getLeaveTypeDeductionLabel() {
		return leaveTypeId == null ? "-" : (leaveTypeName == null ? "-" : leaveTypeName);
	}

	// 사용 여부 플래그("Y"/"N")를 화면에 보여줄 텍스트("사용"/"사용안함")로 변환
	// 使用有無フラグ（"Y"/"N"）を画面に表示するテキスト（"使用"/"使用しない"）に変換
	public String getUseLabel() {
		return "Y".equalsIgnoreCase(useYn) ? "사용" : "사용안함";
	}
}