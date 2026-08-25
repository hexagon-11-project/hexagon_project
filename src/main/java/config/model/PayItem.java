package config.model;

import java.util.Date;

/**
 * 회사별 지급항목(급여 지급 항목) 한 건을 담는다.
 * 会社別支給項目（給与支給項目）1件を保持する。
 */
public class PayItem {

	private Integer payItemId;
	private Integer companyId;
	private String payItemName;
	private String taxableYn; // 과세 여부를 저장한다. / 課税区分を保存する。
	private String calculationMethod; // 금액 계산 방식을 저장한다. / 金額の計算方式を保存する。
	private Integer truncationUnit; // 절사 단위(원)를 저장한다. / 端数処理単位（円）を保存する。
	private String attendancePayRule; // 근태 연동 지급 규칙을 저장한다. / 勤怠連動の支給規則を保存する。
	private Long bulkPayAmount; // 일괄지급 금액을 저장한다. / 一括支給金額を保存する。
	private String useYn; // 사용 여부를 저장한다. / 使用区分を保存する。
	private Integer displayOrder; // 목록 정렬 순서를 저장한다. / 一覧の並び順を保存する。
	private String regId;
	private String modId;
	private Date createdAt;
	private Date updatedAt;
	private Integer nonTaxId; // 비과세 마스터 FK를 저장한다. / 非課税マスタFKを保存する。
	private Long nonPayAmount; // 비과세 한도 금액을 저장한다. / 非課税限度額を保存する。
	private String nonTaxCategory; // 비과세 구분명을 저장한다. / 非課税区分名を保存する。

	public PayItem() {
		// 인자 없는 생성자를 둔다.
		// 引数なしのコンストラクタを置く。
	}

	public PayItem(Integer payItemId, Integer companyId, String payItemName, String taxableYn,
			String calculationMethod, Integer truncationUnit, String attendancePayRule, Long bulkPayAmount,
			String useYn, Integer displayOrder, String regId, String modId, Date createdAt, Date updatedAt,
			Integer nonTaxId, Long nonPayAmount, String nonTaxCategory) {
		// 한 번에 모든 필드를 넣는다.
		// 一度に全フィールドを入れる。

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

	/**
	 * 과세/비과세 화면 라벨을 만든다.
	 * 課税/非課税の画面ラベルを作る。
	 */
	public String getTaxableLabel() {

		// 비과세(N)가 아니면 전체과세로 본다.
		// 非課税(N)でなければ全体課税とみなす。
		if (!"N".equalsIgnoreCase(taxableYn)) {
			return "전체과세";
		}
		// 비과세인데 구분명이 없으면 비과세만 표시한다.
		// 非課税で区分名がなければ非課税のみ表示する。
		if (nonTaxCategory == null || nonTaxCategory.isBlank()) {
			return "비과세";
		}
		// 구분명이 있으면 밑줄로 이어 붙인다.
		// 区分名があれば下線でつなげる。
		return "비과세_" + nonTaxCategory;

	}

	/**
	 * 사용 여부 화면 라벨을 만든다.
	 * 使用区分の画面ラベルを作る。
	 */
	public String getUseLabel() {

		// Y만 사용으로 표시한다.
		// Yのみ使用と表示する。
		return "Y".equalsIgnoreCase(useYn) ? "사용" : "사용안함";

	}

	/**
	 * 절사 단위 화면 라벨을 만든다.
	 * 端数処理単位の画面ラベルを作る。
	 */
	public String getTruncationLabel() {

		// 절사 값이 없거나 0이면 없음으로 표시한다.
		// 端数処理値がなければ、または0ならなしと表示する。
		if (truncationUnit == null || truncationUnit == 0) {

			return "없음";

		}

		// 0보다 크면 원 단위를 붙인다.
		// 0より大きければ円単位を付ける。
		return truncationUnit + "원 단위";

	}

	/**
	 * 비과세 한도 금액을 화면용 문자열로 만든다.
	 * 非課税限度額を画面用文字列にする。
	 */
	public String getNonPayAmountLabel() {

		// 한도가 없으면 빈 문자열을 반환한다.
		// 限度がなければ空文字を返す。
		if (nonPayAmount == null) {

			return "";

		}

		// 있으면 천 단위 콤마를 붙인다.
		// あれば千単位のカンマを付ける。
		return String.format("%,d", nonPayAmount);

	}

	/**
	 * 근태 지급 규칙 화면 라벨을 만든다.
	 * 勤怠支給規則の画面ラベルを作る。
	 */
	public String getAttendancePayRuleLabel() {

		// 규칙이 없으면 빈 문자열을 반환한다.
		// 規則がなければ空文字を返す。
		if (attendancePayRule == null || attendancePayRule.isBlank()) {
			return "";
		}

		// 일괄지급이면 금액을 라벨에 붙인다.
		// 一括支給なら金額をラベルに付ける。
		if ("일괄지급".equals(attendancePayRule)) {
			if (bulkPayAmount == null) {
				return "일괄지급";
			}
			return "일괄지급_" + bulkPayAmount;
		}

		// 그 외 규칙명은 그대로 보여 준다.
		// それ以外の規則名はそのまま表示する。
		return attendancePayRule;

	}

	/**
	 * 일괄지급 금액을 화면용 문자열로 만든다.
	 * 一括支給金額を画面用文字列にする。
	 */
	public String getBulkPayAmountLabel() {

		// 일괄지급액이 없으면 빈 문자열을 반환한다.
		// 一括支給額がなければ空文字を返す。
		if (bulkPayAmount == null) {
			return "";
		}

		// 있으면 콤마를 넣어 보여 준다.
		// あればカンマを入れて表示する。
		return String.format("%,d", bulkPayAmount);

	}

}
