package config.model;

import java.util.Date;

//--
public class DeductionItem {

	private Integer deductionItemId;
	private Integer companyId;
	private String deductionItemName;
	private String calculationMethod;
	private Integer truncationUnit;
	private String remark;
	private String useYn;
	private Integer displayOrder;
	private String regId;
	private String modId;
	private Date createdAt;
	private Date updatedAt;

	public DeductionItem() {

	}

	public DeductionItem(Integer deductionItemId, Integer companyId, String deductionItemName, String calculationMethod,
			Integer truncationUnit, String remark, String useYn, Integer displayOrder, String regId, String modId,
			Date createdAt, Date updatedAt) {

		this.deductionItemId = deductionItemId;
		this.companyId = companyId;
		this.deductionItemName = deductionItemName;
		this.calculationMethod = calculationMethod;
		this.truncationUnit = truncationUnit;
		this.remark = remark;
		this.useYn = useYn;
		this.displayOrder = displayOrder;
		this.regId = regId;
		this.modId = modId;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;

	}

	public Integer getDeductionItemId() {
		return deductionItemId;
	}

	public void setDeductionItemId(Integer deductionItemId) {
		this.deductionItemId = deductionItemId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getDeductionItemName() {
		return deductionItemName;
	}

	public void setDeductionItemName(String deductionItemName) {
		this.deductionItemName = deductionItemName;
	}

	public String getCalculationMethod() {
		return calculationMethod;
	}

	public void setCalculationMethod(String calculationMethod) {
		this.calculationMethod = calculationMethod;
	}

	public Integer getTruncationUnit() {
		return truncationUnit;
	}

	public void setTruncationUnit(Integer truncationUnit) {
		this.truncationUnit = truncationUnit;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getModId() {
		return modId;
	}

	public void setModId(String modId) {
		this.modId = modId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUseLabel() {

		return "Y".equalsIgnoreCase(useYn) ? "사용" : "사용안함";

	}

	public String getTruncationLabel() {

		if (truncationUnit == null || truncationUnit == 0) {

			return "없음";

		}

		return truncationUnit + "원 단위";

	}

	public String getRemarkLabel() {

		return remark == null ? "" : remark;

	}

}
