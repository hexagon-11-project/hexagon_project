package config.employee.model;

import java.sql.Date;

// EMPLOYEE_REWARD_PUNISHMENT 테이블 매핑 (상벌)
public class EmployeeRewardPunishment {
    private String typeCode;    // REWARD_PUNISHMENT_TYPE_CODE (포상/징계)
    private String name;        // REWARD_PUNISHMENT_NAME
    private String authorityName; // AUTHORITY_NAME (상벌권자)
    private Date date;          // REWARD_PUNISHMENT_DATE
    private String content;     // REWARD_PUNISHMENT_CONTENT
    private String memo;        // MEMO

    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String v) { this.typeCode = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getAuthorityName() { return authorityName; }
    public void setAuthorityName(String v) { this.authorityName = v; }
    public Date getDate() { return date; }
    public void setDate(Date v) { this.date = v; }
    public String getContent() { return content; }
    public void setContent(String v) { this.content = v; }
    public String getMemo() { return memo; }
    public void setMemo(String v) { this.memo = v; }
}
