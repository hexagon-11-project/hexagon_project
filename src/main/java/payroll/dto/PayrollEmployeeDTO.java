package payroll.dto;

import java.util.Date;

public class PayrollEmployeeDTO {
    
    private Long payrollEmployeeId;        // 사원별급여아이디
    private Long payrollId;                // 급여아이디
    private String employeeId;             // 사원아이디
    private String employeeName;           // 사원이름
    private String employmentType;         // 급여당시 고용형태
    private String incomeType;             // 소득구분
    private Long totalPayAmount;           // 지급합계
    private Long totalDeductionAmount;     // 공제합계
    private Long netPayAmount;             // 실지급액
    private Long regId;                    // 등록자 아이디
    private Long modId;                    // 수정자 아이디
    private Date createdAt;                // 생성일시
    private Date updatedAt;                // 수정일시
    private String department;  // 부서
    private String position;    // 직위
    private String status;      // 상태

    // Getter / Setter
    public Long getPayrollEmployeeId() { return payrollEmployeeId; }
    public void setPayrollEmployeeId(Long payrollEmployeeId) { this.payrollEmployeeId = payrollEmployeeId; }

    public Long getPayrollId() { return payrollId; }
    public void setPayrollId(Long payrollId) { this.payrollId = payrollId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getIncomeType() { return incomeType; }
    public void setIncomeType(String incomeType) { this.incomeType = incomeType; }

    public Long getTotalPayAmount() { return totalPayAmount; }
    public void setTotalPayAmount(Long totalPayAmount) { this.totalPayAmount = totalPayAmount; }

    public Long getTotalDeductionAmount() { return totalDeductionAmount; }
    public void setTotalDeductionAmount(Long totalDeductionAmount) { this.totalDeductionAmount = totalDeductionAmount; }

    public Long getNetPayAmount() { return netPayAmount; }
    public void setNetPayAmount(Long netPayAmount) { this.netPayAmount = netPayAmount; }

    public Long getRegId() { return regId; }
    public void setRegId(Long regId) { this.regId = regId; }

    public Long getModId() { return modId; }
    public void setModId(Long modId) { this.modId = modId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}