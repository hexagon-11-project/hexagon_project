package config.employee.model;

import java.sql.Date;

// EMPLOYEE_INSURANCE 테이블 매핑 (사원 보험가입 상태)
public class EmployeeInsurance {
    private String insuranceTypeCode; // INSURANCE_TYPE_CODE (보험종류)
    private String insuranceNo;       // INSURANCE_NO (기호번호)
    private Date acquisitionDate;     // ACQUISITION_DATE (취득일)
    private Date lossDate;            // LOSS_DATE (상실일)

    public String getInsuranceTypeCode() { return insuranceTypeCode; }
    public void setInsuranceTypeCode(String v) { this.insuranceTypeCode = v; }
    public String getInsuranceNo() { return insuranceNo; }
    public void setInsuranceNo(String v) { this.insuranceNo = v; }
    public Date getAcquisitionDate() { return acquisitionDate; }
    public void setAcquisitionDate(Date v) { this.acquisitionDate = v; }
    public Date getLossDate() { return lossDate; }
    public void setLossDate(Date v) { this.lossDate = v; }
}
