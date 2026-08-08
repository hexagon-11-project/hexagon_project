package config.employee.model;

import java.sql.Date;

// EMPLOYEE_QUALIFICATION 테이블 매핑 (자격면허)
public class EmployeeQualification {
    private String qualificationName; // QUALIFICATION_NAME
    private Date acquisitionDate;     // ACQUISITION_DATE
    private String issuingOrganization; // ISSUING_ORGANIZATION
    private String certificateNo;     // CERTIFICATE_NO
    private String memo;              // MEMO

    public String getQualificationName() { return qualificationName; }
    public void setQualificationName(String v) { this.qualificationName = v; }
    public Date getAcquisitionDate() { return acquisitionDate; }
    public void setAcquisitionDate(Date v) { this.acquisitionDate = v; }
    public String getIssuingOrganization() { return issuingOrganization; }
    public void setIssuingOrganization(String v) { this.issuingOrganization = v; }
    public String getCertificateNo() { return certificateNo; }
    public void setCertificateNo(String v) { this.certificateNo = v; }
    public String getMemo() { return memo; }
    public void setMemo(String v) { this.memo = v; }
}
