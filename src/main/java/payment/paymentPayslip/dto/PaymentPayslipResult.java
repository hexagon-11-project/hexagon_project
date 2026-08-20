package payment.paymentPayslip.dto;

import java.util.List;

// 급여명세서 화면 조회 결과 (필터 헤더 정보 + 사원 목록, 항목 내역은 각 사원 DTO 안에 포함)
public class PaymentPayslipResult {

    private String payYearMonth;
    private int paySequence;
    private String settlementStartDate;
    private String settlementEndDate;
    private String paymentDate;

    private List<PaymentPayslipDetailDTO> employeeList;

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

    public List<PaymentPayslipDetailDTO> getEmployeeList() { return employeeList; }
    public void setEmployeeList(List<PaymentPayslipDetailDTO> employeeList) { this.employeeList = employeeList; }
}
