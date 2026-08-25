package config.model;

import java.util.Date;

// 회사별 공제항목(급여 공제 항목) 한 건을 저장
// 会社別控除項目（給与控除項目）1件を保存する。
public class DeductionItem {

	private Integer deductionItemId;
	private Integer companyId;
	private String deductionItemName;
	private String calculationMethod; // 공제 금액 계산 방식을 저장 / 控除金額の計算方式を保存する。
	private Integer truncationUnit; // 절사 단위(원)를 저장 / 端数処理単位（円）を保存する。
	private String remark; // 비고를 저장한다. / 備考を保存する。
	private String useYn; // 사용 여부를 저장한다. / 使用区分を保存する。
	private Integer displayOrder; // 목록 정렬 순서를 저장한다. / 一覧の並び順を保存する。
	private String regId;
	private String modId;
	private Date createdAt;
	private Date updatedAt;

	public DeductionItem() {
		// 인자 없는 생성자를 둔다.
		// 引数なしのコンストラクタを置く。
	}

	public DeductionItem(Integer deductionItemId, Integer companyId, String deductionItemName, String calculationMethod,
			Integer truncationUnit, String remark, String useYn, Integer displayOrder, String regId, String modId,
			Date createdAt, Date updatedAt) {
		// 한 번에 모든 필드를 넣는다.
		// 一度に全フィールドを入れる。

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

		// Y만 사용으로 표시한다.
		// Yのみ使用と表示する。
		return "Y".equalsIgnoreCase(useYn) ? "사용" : "사용안함";

	}

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

	public String getRemarkLabel() {

		// 비고가 없으면 빈 문자열을 반환한다.
		// 備考がなければ空文字を返す。
		return remark == null ? "" : remark;

	}

}
