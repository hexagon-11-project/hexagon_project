package payment.paymentPayslip.dto;

// 급여명세서의 지급/공제 항목 한 줄 (항목명 / 금액 / 산출식 또는 산출방법)
public class PaymentPayslipItemDTO {

    private String itemName;
    private long amount;
    private String calculationMethod;

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }

    public String getCalculationMethod() { return calculationMethod; }
    public void setCalculationMethod(String calculationMethod) { this.calculationMethod = calculationMethod; }
}
