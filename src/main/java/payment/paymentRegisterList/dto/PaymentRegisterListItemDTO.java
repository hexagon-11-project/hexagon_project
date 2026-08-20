package payment.paymentRegisterList.dto;

// 급여대장 상세화면의 지급/공제 항목 컬럼 헤더 (PAY_ITEM, DEDUCTION_ITEM 공용)
public class PaymentRegisterListItemDTO {

    private Long itemId;
    private String itemName;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
}
