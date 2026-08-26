package payment.paymentMnt.dto;

// 급여 종합정보(하단 요약 박스) DTO 클래스
// 給与総合情報（下部要約ボックス）DTOクラス
public class PaymentMntSummaryDTO {

	private int totalCount;        // 월 합계 - 재직중인 사원 수 / 月合計 - 在職中の社員数
	private long totalGiveAmount;  // 지급 총액 / 支給総額
	private long totalDeduAmount;  // 공제 총액 / 控除総額
	private long totalRealAmount;  // 실지급액 (지급총액 - 공제총액) / 実支給額（支給総額－控除総額）

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public long getTotalGiveAmount() {
		return totalGiveAmount;
	}

	public void setTotalGiveAmount(long totalGiveAmount) {
		this.totalGiveAmount = totalGiveAmount;
	}

	public long getTotalDeduAmount() {
		return totalDeduAmount;
	}

	public void setTotalDeduAmount(long totalDeduAmount) {
		this.totalDeduAmount = totalDeduAmount;
	}

	public long getTotalRealAmount() {
		return totalRealAmount;
	}

	public void setTotalRealAmount(long totalRealAmount) {
		this.totalRealAmount = totalRealAmount;
	}
}
