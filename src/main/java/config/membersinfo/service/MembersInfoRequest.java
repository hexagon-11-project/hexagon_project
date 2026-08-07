package config.membersinfo.service;

import java.sql.Date;

public class MembersInfoRequest {
	
	private int companyId;
	//USER_ACOUNT PK
    private int userId;
    //COMPANY_INFO PK
    
 // 1. COMPANY_INFO 
    private String companyName;
    private String ceoName;
    private String businessNo;
    private String corpNo;
    private Date estDate;
    private String webSite;
    private String address;
    private String telNo;
    private String faxNo;
    private String businessType;
    private String businessItem;
    private int payPeriodStartDay;
    private int payPeriodEndDay;
    private int payDay;
    private String bankName;
    private String bankAccount;
    private String logoPath;
    private String sealPath;

    // 2. USER_ACCOUNT (성명, 전화번호, 휴대전화, 이메일)
    private String managerName;   // 담당자명
    private String managerTel;    // 담당자 전화번호
    private String managerMobile; // 담당자 휴대폰번호
    private String managerEmail;  // 담당자 이메일
	
    
    
    
    
    public MembersInfoRequest(int companyId, int userId, String companyName, String ceoName, String businessNo,
			String corpNo, Date estDate, String webSite, String address, String telNo, String faxNo,
			String businessType, String businessItem, int payPeriodStartDay, int payPeriodEndDay, int payDay,
			String bankName, String bankAccount, String logoPath, String sealPath, String managerName,
			String managerTel, String managerMobile, String managerEmail) {
		super();
		this.companyId = companyId;
		this.userId = userId;
		this.companyName = companyName;
		this.ceoName = ceoName;
		this.businessNo = businessNo;
		this.corpNo = corpNo;
		this.estDate = estDate;
		this.webSite = webSite;
		this.address = address;
		this.telNo = telNo;
		this.faxNo = faxNo;
		this.businessType = businessType;
		this.businessItem = businessItem;
		this.payPeriodStartDay = payPeriodStartDay;
		this.payPeriodEndDay = payPeriodEndDay;
		this.payDay = payDay;
		this.bankName = bankName;
		this.bankAccount = bankAccount;
		this.logoPath = logoPath;
		this.sealPath = sealPath;
		this.managerName = managerName;
		this.managerTel = managerTel;
		this.managerMobile = managerMobile;
		this.managerEmail = managerEmail;
	}





	public int getCompanyId() {
		return companyId;
	}





	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}





	public int getUserId() {
		return userId;
	}





	public void setUserId(int userId) {
		this.userId = userId;
	}





	public String getCompanyName() {
		return companyName;
	}





	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}





	public String getCeoName() {
		return ceoName;
	}





	public void setCeoName(String ceoName) {
		this.ceoName = ceoName;
	}





	public String getBusinessNo() {
		return businessNo;
	}





	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}





	public String getCorpNo() {
		return corpNo;
	}





	public void setCorpNo(String corpNo) {
		this.corpNo = corpNo;
	}





	public Date getEstDate() {
		return estDate;
	}





	public void setEstDate(Date estDate) {
		this.estDate = estDate;
	}





	public String getWebSite() {
		return webSite;
	}





	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}





	public String getAddress() {
		return address;
	}





	public void setAddress(String address) {
		this.address = address;
	}





	public String getTelNo() {
		return telNo;
	}





	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}





	public String getFaxNo() {
		return faxNo;
	}





	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}





	public String getBusinessType() {
		return businessType;
	}





	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}





	public String getBusinessItem() {
		return businessItem;
	}





	public void setBusinessItem(String businessItem) {
		this.businessItem = businessItem;
	}





	public int getPayPeriodStartDay() {
		return payPeriodStartDay;
	}





	public void setPayPeriodStartDay(int payPeriodStartDay) {
		this.payPeriodStartDay = payPeriodStartDay;
	}





	public int getPayPeriodEndDay() {
		return payPeriodEndDay;
	}





	public void setPayPeriodEndDay(int payPeriodEndDay) {
		this.payPeriodEndDay = payPeriodEndDay;
	}





	public int getPayDay() {
		return payDay;
	}





	public void setPayDay(int payDay) {
		this.payDay = payDay;
	}





	public String getBankName() {
		return bankName;
	}





	public void setBankName(String bankName) {
		this.bankName = bankName;
	}





	public String getBankAccount() {
		return bankAccount;
	}





	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}





	public String getLogoPath() {
		return logoPath;
	}





	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}





	public String getSealPath() {
		return sealPath;
	}





	public void setSealPath(String sealPath) {
		this.sealPath = sealPath;
	}





	public String getManagerName() {
		return managerName;
	}





	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}





	public String getManagerTel() {
		return managerTel;
	}





	public void setManagerTel(String managerTel) {
		this.managerTel = managerTel;
	}





	public String getManagerMobile() {
		return managerMobile;
	}





	public void setManagerMobile(String managerMobile) {
		this.managerMobile = managerMobile;
	}





	public String getManagerEmail() {
		return managerEmail;
	}





	public void setManagerEmail(String managerEmail) {
		this.managerEmail = managerEmail;
	}
    
    
    
    
}