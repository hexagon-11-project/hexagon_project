package statistics.model;

/**
 * 사원별 급여 통계의 지급/공제 항목 1건.
 * 도넛 차트와 표의 항목별 금액·구성비율을 담는다.
 */
public class SalaryItemStatistics {

	/** 지급항목 또는 공제항목 아이디 */
	private Long itemId;

	/** 항목명 (기본급, 국민연금 등) */
	private String itemName;

	/** 항목 금액 */
	private long amount;

	/** 구성비율 (%) — 지급이면 지급합계 대비, 공제면 공제합계 대비. 합계가 0이면 null */
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
