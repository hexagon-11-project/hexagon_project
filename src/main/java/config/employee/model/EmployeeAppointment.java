package config.employee.model;

import java.sql.Date;

// EMPLOYEE_APPOINTMENT 테이블 매핑 (발령)
public class EmployeeAppointment {
    private String typeCode;   // APPOINTMENT_TYPE_CODE
    private Date date;         // APPOINTMENT_DATE
    private String department; // DEPARTMENT (발령 당시 부서)
    private String position;   // POSITION (발령 당시 직위)
    private String dutyTitle;  // DUTY_TITLE (직책)
    private String memo;       // MEMO

    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String v) { this.typeCode = v; }
    public Date getDate() { return date; }
    public void setDate(Date v) { this.date = v; }
    public String getDepartment() { return department; }
    public void setDepartment(String v) { this.department = v; }
    public String getPosition() { return position; }
    public void setPosition(String v) { this.position = v; }
    public String getDutyTitle() { return dutyTitle; }
    public void setDutyTitle(String v) { this.dutyTitle = v; }
    public String getMemo() { return memo; }
    public void setMemo(String v) { this.memo = v; }
}
