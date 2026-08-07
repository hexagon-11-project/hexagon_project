package config.model;

//--
public class NonTaxDetail {

	private Integer nonTaxId;
	private Integer companyId;
	private String legalProvision;
	private String legalCode;
	private String nonTaxNote;
	private String nonTaxCategory;
	private Long limitAmount;
	private String statementPayment;

	public NonTaxDetail() {

	}

	public NonTaxDetail(Integer nonTaxId, Integer companyId, String legalProvision, String legalCode,
			String nonTaxNote, String nonTaxCategory, Long limitAmount, String statementPayment) {

		this.nonTaxId = nonTaxId;
		this.companyId = companyId;
		this.legalProvision = legalProvision;
		this.legalCode = legalCode;
		this.nonTaxNote = nonTaxNote;
		this.nonTaxCategory = nonTaxCategory;
		this.limitAmount = limitAmount;
		this.statementPayment = statementPayment;

	}

	public Integer getNonTaxId() {
		return nonTaxId;
	}

	public void setNonTaxId(Integer nonTaxId) {
		this.nonTaxId = nonTaxId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getLegalProvision() {
		return legalProvision;
	}

	public void setLegalProvision(String legalProvision) {
		this.legalProvision = legalProvision;
	}

	public String getLegalCode() {
		return legalCode;
	}

	public void setLegalCode(String legalCode) {
		this.legalCode = legalCode;
	}

	public String getNonTaxNote() {
		return nonTaxNote;
	}

	public void setNonTaxNote(String nonTaxNote) {
		this.nonTaxNote = nonTaxNote;
	}

	public String getNonTaxCategory() {
		return nonTaxCategory;
	}

	public void setNonTaxCategory(String nonTaxCategory) {
		this.nonTaxCategory = nonTaxCategory;
	}

	public Long getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(Long limitAmount) {
		this.limitAmount = limitAmount;
	}

	public String getStatementPayment() {
		return statementPayment;
	}

	public void setStatementPayment(String statementPayment) {
		this.statementPayment = statementPayment;
	}

	public String getLimitAmountLabel() {

		if (limitAmount == null) {

			return "";

		}

		return String.format("%,d", limitAmount);

	}

}
