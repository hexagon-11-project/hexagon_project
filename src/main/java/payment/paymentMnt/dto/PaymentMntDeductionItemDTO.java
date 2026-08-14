package payment.paymentMnt.dto;

public class PaymentMntDeductionItemDTO {
    private int deductionItemId;
    private String deductionItemName;
    private String calculationMethod;

    public int getDeductionItemId() {
        return deductionItemId;
    }

    public void setDeductionItemId(int deductionItemId) {
        this.deductionItemId = deductionItemId;
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
}