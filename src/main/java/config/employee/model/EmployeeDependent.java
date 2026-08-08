package config.employee.model;

import java.sql.Date;

// EMPLOYEE_DEPENDENT 테이블 매핑 (부양가족)
public class EmployeeDependent {
    private String dependentName;      // DEPENDENT_NAME
    private String relationCode;       // RELATION_CODE (관계코드)
    private String domForYn;           // DOM_FOR_YN (내/외국인)
    private Date birthDate;            // BIRTH_DATE
    private String disabledYn;         // DISABLED_YN (장애인여부)
    private String personalDeductionYn;// PERSONAL_DEDUCTION_YN (인적공제여부)
    private String healthInsuranceYn;  // HEALTH_INSURANCE_YN (건강보험여부)
    private String cohabitationYn;     // COHABITATION_YN (동거여부)
    private String wageIncomeTaxYn;    // WAGE_INCOME_TAX_YN (갑근세여부)
    private String childUnder20Yn;     // CHILD_UNDER_20_YN (20세이하자녀여부)

    public String getDependentName() { return dependentName; }
    public void setDependentName(String v) { this.dependentName = v; }
    public String getRelationCode() { return relationCode; }
    public void setRelationCode(String v) { this.relationCode = v; }
    public String getDomForYn() { return domForYn; }
    public void setDomForYn(String v) { this.domForYn = v; }
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date v) { this.birthDate = v; }
    public String getDisabledYn() { return disabledYn; }
    public void setDisabledYn(String v) { this.disabledYn = v; }
    public String getPersonalDeductionYn() { return personalDeductionYn; }
    public void setPersonalDeductionYn(String v) { this.personalDeductionYn = v; }
    public String getHealthInsuranceYn() { return healthInsuranceYn; }
    public void setHealthInsuranceYn(String v) { this.healthInsuranceYn = v; }
    public String getCohabitationYn() { return cohabitationYn; }
    public void setCohabitationYn(String v) { this.cohabitationYn = v; }
    public String getWageIncomeTaxYn() { return wageIncomeTaxYn; }
    public void setWageIncomeTaxYn(String v) { this.wageIncomeTaxYn = v; }
    public String getChildUnder20Yn() { return childUnder20Yn; }
    public void setChildUnder20Yn(String v) { this.childUnder20Yn = v; }
}
