package config.employee.model;

import java.sql.Date;

// EMPLOYEE_MILITARY 테이블 매핑 (사원 병역기록)
// 주의: 이 테이블은 PK가 별도 시퀀스가 아니라 EMPLOYEE_ID 그 자체다 (사원 한 명당 병역기록 한 줄, 1:1 관계)
public class EmployeeMilitary {
    private String militaryStatusCode;  // MILITARY_STATUS_CODE (병역구분코드)
    private String militaryBranchCode;  // MILITARY_BRANCH_CODE (군별코드: 육군/해군/공군/해병대)
    private Date serviceStartDate;      // MILITARY_SERVICE_START_DATE (복무시작일)
    private Date serviceEndDate;        // MILITARY_SERVICE_END_DATE (복무종료일)
    private String militarySpecialty;   // MILITARY_SPECIALTY (특기)
    private String militaryExemptReason;// MILITARY_EXEMPT_REASON (미필/면제사유)
    private String militaryGrade;       // MILITARY_GRADE (계급)
    private String militaryBranch;      // MILITARY_BRANCH (병과) - MILITARY_BRANCH_CODE(군별)와 다른 컬럼이라 헷갈리지 않도록 주의

    public String getMilitaryStatusCode() { return militaryStatusCode; }
    public void setMilitaryStatusCode(String v) { this.militaryStatusCode = v; }
    public String getMilitaryBranchCode() { return militaryBranchCode; }
    public void setMilitaryBranchCode(String v) { this.militaryBranchCode = v; }
    public Date getServiceStartDate() { return serviceStartDate; }
    public void setServiceStartDate(Date v) { this.serviceStartDate = v; }
    public Date getServiceEndDate() { return serviceEndDate; }
    public void setServiceEndDate(Date v) { this.serviceEndDate = v; }
    public String getMilitarySpecialty() { return militarySpecialty; }
    public void setMilitarySpecialty(String v) { this.militarySpecialty = v; }
    public String getMilitaryExemptReason() { return militaryExemptReason; }
    public void setMilitaryExemptReason(String v) { this.militaryExemptReason = v; }
    public String getMilitaryGrade() { return militaryGrade; }
    public void setMilitaryGrade(String v) { this.militaryGrade = v; }
    public String getMilitaryBranch() { return militaryBranch; }
    public void setMilitaryBranch(String v) { this.militaryBranch = v; }
}
