package payment.paymentMntDayWorker.dto;

// 좌측 근로자 목록 1행 DTO (EMPLOYEE + PAYROLL_EMPLOYEE JOIN 결과, 정규직 화면과 테이블 공유)
public class PaymentMntDayWorkerEmployeeDTO {

    private Long payrollDayWorkerEmployeeId; // PAYROLL_EMPLOYEE_ID (PK)
    private Long payrollDayWorkerId;         // PAYROLL_ID (FK)
    private String employeeId;               // 사원아이디 (EMPLOYEE PK)
    private String employeeNo;               // 사원번호
    private String employeeName;             // 성명
    private String employmentType;           // 구분 (예: 일용직)
    private String department;               // 부서
    private String position;                 // 직위
    private String status;                   // 상태 (재직/퇴직) - [신규추가] 모달용
    private Long totalPayAmount;             // 지급총액
    private Long totalDeductionAmount;       // 공제총액
    private Long netPayAmount;               // 실지급액

    public Long getPayrollDayWorkerEmployeeId() { return payrollDayWorkerEmployeeId; }
    public void setPayrollDayWorkerEmployeeId(Long payrollDayWorkerEmployeeId) { this.payrollDayWorkerEmployeeId = payrollDayWorkerEmployeeId; }

    public Long getPayrollDayWorkerId() { return payrollDayWorkerId; }
    public void setPayrollDayWorkerId(Long payrollDayWorkerId) { this.payrollDayWorkerId = payrollDayWorkerId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getTotalPayAmount() { return totalPayAmount; }
    public void setTotalPayAmount(Long totalPayAmount) { this.totalPayAmount = totalPayAmount; }

    public Long getTotalDeductionAmount() { return totalDeductionAmount; }
    public void setTotalDeductionAmount(Long totalDeductionAmount) { this.totalDeductionAmount = totalDeductionAmount; }

    public Long getNetPayAmount() { return netPayAmount; }
    public void setNetPayAmount(Long netPayAmount) { this.netPayAmount = netPayAmount; }
}
