package payment.paymentRegisterList.dto;

// 급여대장 목록 화면(1행 = 1개 급여차수)의 화면 표시용 DTO
// 給与台帳一覧画面（1行＝1つの給与回）の画面表示用DTO
public class PaymentRegisterListDTO {

    private Long payrollId;
    private String payYearMonth;       // 귀속연월 (예: "202601") / 帰属年月（例："202601"）
    private int paySequence;           // 급여차수 (예: 1) / 給与回（例：1）
    private String settlementStartDate; // 정산기간 시작일 (YYYY-MM-DD) / 精算期間開始日（YYYY-MM-DD）
    private String settlementEndDate;   // 정산기간 종료일 (YYYY-MM-DD) / 精算期間終了日（YYYY-MM-DD）
    private String paymentDate;         // 지급일 (YYYY-MM-DD) / 支給日（YYYY-MM-DD）
    private long employeeCount;         // 인원 / 人員
    private long totalPayAmount;        // 지급총액 / 支給総額
    private long totalDeductionAmount;  // 공제총액 / 控除総額
    private long netPayAmount;          // 실지급액 / 実支給額

    public Long getPayrollId() { return payrollId; }
    public void setPayrollId(Long payrollId) { this.payrollId = payrollId; }

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

    public long getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(long employeeCount) { this.employeeCount = employeeCount; }

    public long getTotalPayAmount() { return totalPayAmount; }
    public void setTotalPayAmount(long totalPayAmount) { this.totalPayAmount = totalPayAmount; }

    public long getTotalDeductionAmount() { return totalDeductionAmount; }
    public void setTotalDeductionAmount(long totalDeductionAmount) { this.totalDeductionAmount = totalDeductionAmount; }

    public long getNetPayAmount() { return netPayAmount; }
    public void setNetPayAmount(long netPayAmount) { this.netPayAmount = netPayAmount; }
}
