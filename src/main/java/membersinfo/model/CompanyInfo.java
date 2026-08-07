package membersinfo.model;

import java.sql.Timestamp;

public class CompanyInfo {
	private int companyId;
	private String companyName;
	private String businessNo;
	private String ceoTitle;
	private String ceoName;
	private String corpNo;
	private String estDate;
	private String webSite;
	private String address;
	private String telNo;
	private String faxNo;
	private String businessType;
	private String businessItem;
	private int payDay;
	private int payPeriodStartDay;
    private int payPeriodEndDay;
    private String bankName;
    private String accountHolder;
    private String bankAccount;
    private String logoPath;
    private String sealPath;
    private String createdAt;
    private String updatedAt;
    private String managerName;   // 담당자명
    private String managerTel;    // 담당자 전화번호
    private String managerMobile; // 담당자 휴대폰번호
    private String managerEmail;  // 담당자 이메일
    
    
    
    
    
    
	public CompanyInfo(int companyId, String companyName, String businessNo, String ceoTitle, String ceoName,
			String corpNo, String estDate, String webSite, String address, String telNo, String faxNo,
			String businessType, String businessItem, int payDay, int payPeriodStartDay, int payPeriodEndDay,
			String bankName, String accountHolder, String bankAccount, String logoPath, String sealPath,
			String createdAt, String updatedAt,String managerName, String managerTel, String managerMobile, String managerEmail) {
		super();
		this.companyId = companyId;
		this.companyName = companyName;
		this.businessNo = businessNo;
		this.ceoTitle = ceoTitle;
		this.ceoName = ceoName;
		this.corpNo = corpNo;
		this.estDate = estDate;
		this.webSite = webSite;
		this.address = address;
		this.telNo = telNo;
		this.faxNo = faxNo;
		this.businessType = businessType;
		this.businessItem = businessItem;
		this.payDay = payDay;
		this.payPeriodStartDay = payPeriodStartDay;
		this.payPeriodEndDay = payPeriodEndDay;
		this.bankName = bankName;
		this.accountHolder = accountHolder;
		this.bankAccount = bankAccount;
		this.logoPath = logoPath;
		this.sealPath = sealPath;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		
	








	
		this.managerName = managerName;
		this.managerTel = managerTel;
		this.managerMobile = managerMobile;
		this.managerEmail = managerEmail;
	}







	public CompanyInfo() {
		// TODO Auto-generated constructor stub
	}







	public int getCompanyId() {
		return companyId;
	}







	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}







	public String getCompanyName() {
		return companyName;
	}







	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}







	public String getBusinessNo() {
		return businessNo;
	}







	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}







	public String getCeoTitle() {
		return ceoTitle;
	}







	public void setCeoTitle(String ceoTitle) {
		this.ceoTitle = ceoTitle;
	}







	public String getCeoName() {
		return ceoName;
	}







	public void setCeoName(String ceoName) {
		this.ceoName = ceoName;
	}







	public String getCorpNo() {
		return corpNo;
	}







	public void setCorpNo(String corpNo) {
		this.corpNo = corpNo;
	}







	public String getEstDate() {
		return estDate;
	}







	public void setEstDate(String estDate) {
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







	public int getPayDay() {
		return payDay;
	}







	public void setPayDay(int payDay) {
		this.payDay = payDay;
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







	public String getBankName() {
		return bankName;
	}







	public void setBankName(String bankName) {
		this.bankName = bankName;
	}







	public String getAccountHolder() {
		return accountHolder;
	}







	public void setAccountHolder(String accountHolder) {
		this.accountHolder = accountHolder;
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







	public String getCreatedAt() {
		return createdAt;
	}







	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}







	public String getUpdatedAt() {
		return updatedAt;
	}







	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
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
