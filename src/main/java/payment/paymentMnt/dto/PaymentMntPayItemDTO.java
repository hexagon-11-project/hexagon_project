package payment.paymentMnt.dto;

public class PaymentMntPayItemDTO {
    private int payItemId;
    private String payItemName;
    private String calculationMethod;

    public int getPayItemId() {
        return payItemId;
    }

    public void setPayItemId(int payItemId) {
        this.payItemId = payItemId;
    }

    public String getPayItemName() {
        return payItemName;
    }

    public void setPayItemName(String payItemName) {
        this.payItemName = payItemName;
    }

    public String getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(String calculationMethod) {
        this.calculationMethod = calculationMethod;
    }
    
 // 기존 변수들 아래에 추가
    private Long bulkPayAmount; // 일괄지급액 (식대 20만원 등)

    // Getter, Setter 추가
    public Long getBulkPayAmount() {
        return bulkPayAmount;
    }
    public void setBulkPayAmount(Long bulkPayAmount) {
        this.bulkPayAmount = bulkPayAmount;
    }
}