package config.model;

/**
 * 비과세 항목 마스터 한 건을 담는다.
 * 非課税項目マスタ1件を保持する。
 */
public class NonTaxDetail {

	private Integer nonTaxId;
	private Integer companyId; // 회사 ID를 저장한다. / 会社IDを保存する。
	private String legalProvision; // 관련 법령 조항을 저장한다. / 関連法令条項を保存する。
	private String legalCode; // 법령 코드를 저장한다. / 法令コードを保存する。
	private String nonTaxNote; // 비과세 비고를 저장한다. / 非課税備考を保存する。
	private String nonTaxCategory; // 비과세 구분명을 저장한다. / 非課税区分名を保存する。
	private Long limitAmount; // 비과세 한도 금액을 저장한다. / 非課税限度額を保存する。
	private String statementPayment; // 지급명세서 지급 구분을 저장한다. / 支払明細書の支給区分を保存する。

	public NonTaxDetail() {
		// 인자 없는 생성자를 둔다.
		// 引数なしのコンストラクタを置く。
	}

	public NonTaxDetail(Integer nonTaxId, Integer companyId, String legalProvision, String legalCode,
			String nonTaxNote, String nonTaxCategory, Long limitAmount, String statementPayment) {
		// 한 번에 모든 필드를 넣는다.
		// 一度に全フィールドを入れる。

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

	/**
	 * 한도 금액을 화면용 문자열로 만든다.
	 * 限度額を画面用文字列にする。
	 */
	public String getLimitAmountLabel() {

		// 한도가 없으면 빈 문자열을 반환한다.
		// 限度がなければ空文字を返す。
		if (limitAmount == null) {

			return "";

		}

		// 있으면 천 단위 콤마를 붙인다.
		// あれば千単位のカンマを付ける。
		return String.format("%,d", limitAmount);

	}

}
