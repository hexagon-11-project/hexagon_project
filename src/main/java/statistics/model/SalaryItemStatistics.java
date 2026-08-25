package statistics.model;

/**
 * 사원별 급여 통계의 지급/공제 항목 1건.
 * 社員別給与統計の支給/控除項目1件。
 *
 */
public class SalaryItemStatistics {

	// 지급항목 또는 공제항목 아이디
	// 支給項目または控除項目ID。
	private Long itemId;

	/** 항목명 (기본급, 국민연금 등)
	 * 項目名（基本給、国民年金など）。 */
	private String itemName;

	/** 항목 금액
	 * 項目金額。 */
	private long amount;

	// 구성비율 (%)
	// 構成比 (%)。
	private Double compositionRatio;

	public SalaryItemStatistics() {
	}

	public SalaryItemStatistics(Long itemId, String itemName, long amount, Double compositionRatio) {
		this.itemId = itemId;
		this.itemName = itemName;
		this.amount = amount;
		this.compositionRatio = compositionRatio;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public Double getCompositionRatio() {
		return compositionRatio;
	}

	public void setCompositionRatio(Double compositionRatio) {
		this.compositionRatio = compositionRatio;
	}
}
