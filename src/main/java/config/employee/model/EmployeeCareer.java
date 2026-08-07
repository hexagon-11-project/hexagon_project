package config.employee.model;

import java.sql.Date;

// EMPLOYEE_CAREER 테이블 매핑 (경력)
public class EmployeeCareer {
    private String companyName;  // COMPANY_NAME
    private String department;   // DEPARTMENT
    private String position;     // POSITION
    private Date startDate;      // START_DATE
    private Date endDate;        // END_DATE
    private int dutyYy;          // DUTY_YY (근무기간 년) - NUMBER(2,0)
    private int dutyMm;          // DUTY_MM (근무기간 월) - NUMBER(2,0)
    private String careerDescription; // CAREER_DESCRIPTION (담당직무)

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String v) { this.companyName = v; }
    public String getDepartment() { return department; }
    public void setDepartment(String v) { this.department = v; }
    public String getPosition() { return position; }
    public void setPosition(String v) { this.position = v; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date v) { this.startDate = v; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date v) { this.endDate = v; }
    public int getDutyYy() { return dutyYy; }
    public void setDutyYy(int v) { this.dutyYy = v; }
    public int getDutyMm() { return dutyMm; }
    public void setDutyMm(int v) { this.dutyMm = v; }
    public String getCareerDescription() { return careerDescription; }
    public void setCareerDescription(String v) { this.careerDescription = v; }
}
