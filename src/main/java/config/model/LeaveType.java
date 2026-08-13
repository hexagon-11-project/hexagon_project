package config.model;

import java.sql.Date;

public class LeaveType {

	private Integer leaveTypeId;
	private Integer companyId;
	private String leaveCode;
	private String leaveName;
	private Date effectiveStartDate; // 적용시작일
	private Date effectiveEndDate;   // 적용종료일
	private String useYn;
	private Integer displayOrder;

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

	public String getPeriodLabel() {
		if (effectiveStartDate == null || effectiveEndDate == null) {
			return "-";
		}
		return effectiveStartDate.toString() + " ~ " + effectiveEndDate.toString();
	}

	public String getUseLabel() {
		return "Y".equalsIgnoreCase(useYn) ? "사용" : "사용안함";
	}
}
