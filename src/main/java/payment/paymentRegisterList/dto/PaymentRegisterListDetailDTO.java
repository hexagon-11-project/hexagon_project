package payment.paymentRegisterList.dto;

import java.util.HashMap;
import java.util.Map;

// 급여대장 상세화면의 사원 1행 (지급/공제 항목별 금액은 itemId를 key로 하는 Map으로 보관)
public class PaymentRegisterListDetailDTO {

    private Long payrollEmployeeId;
    private String employmentType;   // 구분
    private String employeeName;     // 성명
    private String hireDate;         // 입사일
    private String department;       // 부서
    private String position;         // 직위

    private Map<Long, Long> payAmountByItemId = new HashMap<>();
    private long totalPayAmount;

    private Map<Long, Long> deductionAmountByItemId = new HashMap<>();
    private long totalDeductionAmount;

    private long netPayAmount;

    public Long getPayrollEmployeeId() { return payrollEmployeeId; }
    public void setPayrollEmployeeId(Long payrollEmployeeId) { this.payrollEmployeeId = payrollEmployeeId; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public Map<Long, Long> getPayAmountByItemId() { return payAmountByItemId; }
    public void setPayAmountByItemId(Map<Long, Long> payAmountByItemId) { this.payAmountByItemId = payAmountByItemId; }
    public long getPayAmount(Long itemId) { return payAmountByItemId.getOrDefault(itemId, 0L); }

    public long getTotalPayAmount() { return totalPayAmount; }
    public void setTotalPayAmount(long totalPayAmount) { this.totalPayAmount = totalPayAmount; }

    public Map<Long, Long> getDeductionAmountByItemId() { return deductionAmountByItemId; }
    public void setDeductionAmountByItemId(Map<Long, Long> deductionAmountByItemId) { this.deductionAmountByItemId = deductionAmountByItemId; }
    public long getDeductionAmount(Long itemId) { return deductionAmountByItemId.getOrDefault(itemId, 0L); }

    public long getTotalDeductionAmount() { return totalDeductionAmount; }
    public void setTotalDeductionAmount(long totalDeductionAmount) { this.totalDeductionAmount = totalDeductionAmount; }

    public long getNetPayAmount() { return netPayAmount; }
    public void setNetPayAmount(long netPayAmount) { this.netPayAmount = netPayAmount; }
}
