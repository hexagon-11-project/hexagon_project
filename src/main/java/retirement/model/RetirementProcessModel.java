package retirement.model;

public class RetirementProcessModel {

	private String retirementYn;           // 재직/퇴직 상태 (Y/N)
    private String employeeNo;             // 사원번호
    private String employeeName;           // 성명
    private String department;             // 부서
    private String position;               // 직위
    private String hireDate;               // 입사일 (yyyy-MM-dd)
    private String resignDate;             // 퇴직일 (yyyy-MM-dd 또는 null)
    private String workYears;              // 근속연수 (ex: "8년")
    private String interimSettlementYn;    // 중간정산 여부 (Y/N)
    private String retirementSettlementYn; // 퇴직정산 여부 (Y/N)

    
    public String getRetirementYn() { return retirementYn; }
    public void setRetirementYn(String retirementYn) { this.retirementYn = retirementYn; }

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public String getResignDate() { return resignDate; }
    public void setResignDate(String resignDate) { this.resignDate = resignDate; }

    public String getWorkYears() { return workYears; }
    public void setWorkYears(String workYears) { this.workYears = workYears; }

    public String getInterimSettlementYn() { return interimSettlementYn; }
    public void setInterimSettlementYn(String interimSettlementYn) { this.interimSettlementYn = interimSettlementYn; }

    public String getRetirementSettlementYn() { return retirementSettlementYn; }
    public void setRetirementSettlementYn(String retirementSettlementYn) { this.retirementSettlementYn = retirementSettlementYn; }
}
