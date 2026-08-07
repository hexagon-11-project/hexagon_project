package config.employee.model;

import java.sql.Date;

// EMPLOYEE_EDUCATION 테이블 매핑 (학력)
public class EmployeeEducation {
    private String schoolName;   // SCHOOL_NAME
    private String majorName;    // MAJOR_NAME
    private Date startDate;      // START_DATE
    private Date endDate;        // END_DATE
    private String graduationStatus; // GRADUATION_STATUS

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String v) { this.schoolName = v; }
    public String getMajorName() { return majorName; }
    public void setMajorName(String v) { this.majorName = v; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date v) { this.startDate = v; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date v) { this.endDate = v; }
    public String getGraduationStatus() { return graduationStatus; }
    public void setGraduationStatus(String v) { this.graduationStatus = v; }
}
