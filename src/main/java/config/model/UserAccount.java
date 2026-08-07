package config.model;

import java.sql.Timestamp;

public class UserAccount {
	private int userId ;
	private int companyId;
	private int employeeId;
	private String loginId ;
	private String passwardHash ;
	private String userName ;
	private String email ;
	private String phone ;
	private String roleCode ;
	private String status ;
	private String regId ;
	private String modId ;
	private Timestamp createdAt;
    private Timestamp updatedAt;
    
    
    
    
	public UserAccount(int userId, int companyId, int employeeId, String loginId, String passwardHash, String userName,
			String email, String phone, String roleCode, String status, String regId, String modId, Timestamp createdAt,
			Timestamp updatedAt) {
		super();
		this.userId = userId;
		this.companyId = companyId;
		this.employeeId = employeeId;
		this.loginId = loginId;
		this.passwardHash = passwardHash;
		this.userName = userName;
		this.email = email;
		this.phone = phone;
		this.roleCode = roleCode;
		this.status = status;
		this.regId = regId;
		this.modId = modId;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}




	public int getUserId() {
		return userId;
	}




	public void setUserId(int userId) {
		this.userId = userId;
	}




	public int getCompanyId() {
		return companyId;
	}




	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}




	public int getEmployeeId() {
		return employeeId;
	}




	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}




	public String getLoginId() {
		return loginId;
	}




	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}




	public String getPasswardHash() {
		return passwardHash;
	}




	public void setPasswardHash(String passwardHash) {
		this.passwardHash = passwardHash;
	}




	public String getUserName() {
		return userName;
	}




	public void setUserName(String userName) {
		this.userName = userName;
	}




	public String getEmail() {
		return email;
	}




	public void setEmail(String email) {
		this.email = email;
	}




	public String getPhone() {
		return phone;
	}




	public void setPhone(String phone) {
		this.phone = phone;
	}




	public String getRoleCode() {
		return roleCode;
	}




	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}




	public String getStatus() {
		return status;
	}




	public void setStatus(String status) {
		this.status = status;
	}




	public String getRegId() {
		return regId;
	}




	public void setRegId(String regId) {
		this.regId = regId;
	}




	public String getModId() {
		return modId;
	}




	public void setModId(String modId) {
		this.modId = modId;
	}




	public Timestamp getCreatedAt() {
		return createdAt;
	}




	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}




	public Timestamp getUpdatedAt() {
		return updatedAt;
	}




	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	
	
		
	

}
