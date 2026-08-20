package payment.paymentRegisterList.dto;

import java.util.List;

// 급여대장 상세화면 조회 결과 (헤더 정보 + 항목 컬럼 + 사원 목록 + 합계행)
public class PaymentRegisterListDetailResult {

    private String payYearMonth;
    private int paySequence;
    private String settlementStartDate;
    private String settlementEndDate;
    private String paymentDate;

    private List<PaymentRegisterListItemDTO> payItemList;
    private List<PaymentRegisterListItemDTO> deductionItemList;
    private List<String> departmentList;

    private List<PaymentRegisterListDetailDTO> employeeList;
    private PaymentRegisterListDetailDTO totalRow;

    public String getPayYearMonth() { return payYearMonth; }
    public void setPayYearMonth(String payYearMonth) { this.payYearMonth = payYearMonth; }

    public int getPaySequence() { return paySequence; }
    public void setPaySequence(int paySequence) { this.paySequence = paySequence; }

    public String getSettlementStartDate() { return settlementStartDate; }
    public void setSettlementStartDate(String settlementStartDate) { this.settlementStartDate = settlementStartDate; }

    public String getSettlementEndDate() { return settlementEndDate; }
    public void setSettlementEndDate(String settlementEndDate) { this.settlementEndDate = settlementEndDate; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public List<PaymentRegisterListItemDTO> getPayItemList() { return payItemList; }
    public void setPayItemList(List<PaymentRegisterListItemDTO> payItemList) { this.payItemList = payItemList; }

    public List<PaymentRegisterListItemDTO> getDeductionItemList() { return deductionItemList; }
    public void setDeductionItemList(List<PaymentRegisterListItemDTO> deductionItemList) { this.deductionItemList = deductionItemList; }

    public List<String> getDepartmentList() { return departmentList; }
    public void setDepartmentList(List<String> departmentList) { this.departmentList = departmentList; }

    public List<PaymentRegisterListDetailDTO> getEmployeeList() { return employeeList; }
    public void setEmployeeList(List<PaymentRegisterListDetailDTO> employeeList) { this.employeeList = employeeList; }

    public PaymentRegisterListDetailDTO getTotalRow() { return totalRow; }
    public void setTotalRow(PaymentRegisterListDetailDTO totalRow) { this.totalRow = totalRow; }
}
