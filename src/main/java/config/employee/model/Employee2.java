package config.employee.model;

import java.util.Date;

public class Employee2 {
    private String employeeNo;       // 사원번호 (공통)
    
    // -- 추천 & 신원보증 관련 --
    private String recommender;      // 추천인
    private String recommenderPhone; // 추천인 연락처
    private String guarantor;        // 신원보증인
    private String guarantorRel;     // 관계
    private String guarantorPhone;   // 보증인 연락처
    private Date   guaranteeStart;   // 보증기간 시작일
    private Date   guaranteeEnd;     // 보증기간 종료일
    private String guarantorAddress; // 보증인 주소

    // -- 퇴직 관련 --
    private String retireType;       // 퇴직구분
    private Date   retireDate;       // 퇴직일자
    private String retireReason;     // 퇴직사유
    private String retirePhone;      // 퇴직 후 연락처
    private String retireAddress;    // 퇴직 후 주소

    // Getter & Setter
    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getRecommender() { return recommender; }
    public void setRecommender(String recommender) { this.recommender = recommender; }

    public String getRecommenderPhone() { return recommenderPhone; }
    public void setRecommenderPhone(String recommenderPhone) { this.recommenderPhone = recommenderPhone; }

    public String getGuarantor() { return guarantor; }
    public void setGuarantor(String guarantor) { this.guarantor = guarantor; }

    public String getGuarantorRel() { return guarantorRel; }
    public void setGuarantorRel(String guarantorRel) { this.guarantorRel = guarantorRel; }

    public String getGuarantorPhone() { return guarantorPhone; }
    public void setGuarantorPhone(String guarantorPhone) { this.guarantorPhone = guarantorPhone; }

    public Date getGuaranteeStart() { return guaranteeStart; }
    public void setGuaranteeStart(Date guaranteeStart) { this.guaranteeStart = guaranteeStart; }

    public Date getGuaranteeEnd() { return guaranteeEnd; }
    public void setGuaranteeEnd(Date guaranteeEnd) { this.guaranteeEnd = guaranteeEnd; }

    public String getGuarantorAddress() { return guarantorAddress; }
    public void setGuarantorAddress(String guarantorAddress) { this.guarantorAddress = guarantorAddress; }

    public String getRetireType() { return retireType; }
    public void setRetireType(String retireType) { this.retireType = retireType; }

    public Date getRetireDate() { return retireDate; }
    public void setRetireDate(Date retireDate) { this.retireDate = retireDate; }

    public String getRetireReason() { return retireReason; }
    public void setRetireReason(String retireReason) { this.retireReason = retireReason; }

    public String getRetirePhone() { return retirePhone; }
    public void setRetirePhone(String retirePhone) { this.retirePhone = retirePhone; }

    public String getRetireAddress() { return retireAddress; }
    public void setRetireAddress(String retireAddress) { this.retireAddress = retireAddress; }
}