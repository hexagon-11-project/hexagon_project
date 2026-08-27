package config.model;

import java.sql.Date;

// 휴가 종류/유형 마스터(연차, 경조휴가, 포상휴가 등 정의) 정보를 담는 모델(DTO/VO) 클래스
// 休暇種類・タイプマスター（有給休暇、慶弔休暇、表彰休暇などの定義）情報を保持するモデル（DTO/VO）クラス
public class LeaveType {

	private Integer leaveTypeId;
	private Integer companyId;
	private String leaveCode;
	private String leaveName;
	private Date effectiveStartDate; // 적용시작일 (適用開始日)
	private Date effectiveEndDate;   // 적용종료일 (適用終了日)
	private String useYn;
	private Integer displayOrder;    // 화면 정렬 순서 (画面の並び順)

	public LeaveType() {
	}

	public Integer getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(Integer leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getLeaveCode() {
		return leaveCode;
	}

	public void setLeaveCode(String leaveCode) {
		this.leaveCode = leaveCode;
	}

	public String getLeaveName() {
		return leaveName;
	}

	public void setLeaveName(String leaveName) {
		this.leaveName = leaveName;
	}

	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}

	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	public Date getEffectiveEndDate() {
		return effectiveEndDate;
	}

	public void setEffectiveEndDate(Date effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}

	public String getUseYn() {
		return useYn;
	}

	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	// ===== 화면 표시용 라벨 헬퍼 =====
	// ===== 画面表示用ラベルヘルパー =====

	// 적용 시작일부터 종료일까지의 기간을 "YYYY-MM-DD ~ YYYY-MM-DD" 형태로 이쁘게 묶어주는 헬퍼
	// 適用開始日から終了日までの期間を "YYYY-MM-DD ~ YYYY-MM-DD" の形できれいにまとめるヘルパー
	public String getPeriodLabel() {
		if (effectiveStartDate == null || effectiveEndDate == null) {
			return "-";
		}
		return effectiveStartDate.toString() + " ~ " + effectiveEndDate.toString();
	}

	// 사용 여부("Y"/"N")를 화면용 한글 텍스트("사용"/"사용안함")로 변환
	// 使用有無（"Y"/"N"）を画面用のハングルテキスト（"使用"/"使用しない"）に変換
	public String getUseLabel() {
		return "Y".equalsIgnoreCase(useYn) ? "사용" : "사용안함";
	}
}