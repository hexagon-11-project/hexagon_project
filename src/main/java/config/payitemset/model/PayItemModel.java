package config.payitemset.model;

import java.util.Date;

public class PayItemModel {

	private Integer payItemId;
	private Integer companyId;
	private String payItemName;
	private String taxableYn;
	private String calculationMethod;
	private Integer truncationUnit;
	private String attendancePayRule;
	private Long bulkPayAmount;
	private String useYn;
	private Integer displayOrder;
	private String regId;
	private String modId;
	private Date createdAt;
	private Date updatedAt;
	private Integer nonTaxId;
	private Long nonPayAmount;
	private String nonTaxCategory;

	public PayItemModel() {

	}

	public PayItemModel(Integer payItemId, Integer companyId, String payItemName, String taxableYn,
			String calculationMethod, Integer truncationUnit, String attendancePayRule, Long bulkPayAmount,
			String useYn, Integer displayOrder, String regId, String modId, Date createdAt, Date updatedAt,
			Integer nonTaxId, Long nonPayAmount, String nonTaxCategory) {

		this.payItemId = payItemId;
		this.companyId = companyId;
		this.payItemName = payItemName;
		this.taxableYn = taxableYn;
		this.calculationMethod = calculationMethod;
		this.truncationUnit = truncationUnit;
		this.attendancePayRule = attendancePayRule;
		this.bulkPayAmount = bulkPayAmount;
		this.useYn = useYn;
		this.displayOrder = displayOrder;
		this.regId = regId;
		this.modId = modId;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.nonTaxId = nonTaxId;
		this.nonPayAmount = nonPayAmount;
		this.nonTaxCategory = nonTaxCategory;

	}

	public Integer getPayItemId() {
		return payItemId;
	}

	public void setPayItemId(Integer payItemId) {
		this.payItemId = payItemId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getPayItemName() {
		return payItemName;
	}

	public void setPayItemName(String payItemName) {
		this.payItemName = payItemName;
	}

	public String getTaxableYn() {
		return taxableYn;
	}

	public void setTaxableYn(String taxableYn) {
		this.taxableYn = taxableYn;
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

	public String getAttendancePayRule() {
		return attendancePayRule;
	}

	public void setAttendancePayRule(String attendancePayRule) {
		this.attendancePayRule = attendancePayRule;
	}

	public Long getBulkPayAmount() {
		return bulkPayAmount;
	}

	public void setBulkPayAmount(Long bulkPayAmount) {
		this.bulkPayAmount = bulkPayAmount;
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

	public Integer getNonTaxId() {
		return nonTaxId;
	}

	public void setNonTaxId(Integer nonTaxId) {
		this.nonTaxId = nonTaxId;
	}

	public Long getNonPayAmount() {
		return nonPayAmount;
	}

	public void setNonPayAmount(Long nonPayAmount) {
		this.nonPayAmount = nonPayAmount;
	}

	public String getNonTaxCategory() {
		return nonTaxCategory;
	}

	public void setNonTaxCategory(String nonTaxCategory) {
		this.nonTaxCategory = nonTaxCategory;
	}

	public String getTaxableLabel() {

		return "N".equalsIgnoreCase(taxableYn) ? "비과세" : "전체과세";

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

	public String getNonPayAmountLabel() {

		if (nonPayAmount == null) {

			return "";

		}

		return String.format("%,d", nonPayAmount);

	}

	public String getAttendancePayRuleLabel() {

		return attendancePayRule == null ? "" : attendancePayRule;

	}

}
