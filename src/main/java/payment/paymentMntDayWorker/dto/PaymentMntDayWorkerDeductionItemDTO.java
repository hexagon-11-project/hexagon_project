package payment.paymentMntDayWorker.dto;

// 공제항목 마스터(DEDUCTION_ITEM) 1행 - 화면 공제항목 패널을 DB 기준으로 동적으로 그리기 위한 DTO
public class PaymentMntDayWorkerDeductionItemDTO {

    private int deductionItemId;
    private String deductionItemName;

    public int getDeductionItemId() { return deductionItemId; }
    public void setDeductionItemId(int deductionItemId) { this.deductionItemId = deductionItemId; }

    public String getDeductionItemName() { return deductionItemName; }
    public void setDeductionItemName(String deductionItemName) { this.deductionItemName = deductionItemName; }
}
