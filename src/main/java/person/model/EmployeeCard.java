package person.model;

import java.sql.Date;
import java.util.List;

import config.employee.model.EmployeeAppointment;
import config.employee.model.EmployeeCareer;
import config.employee.model.EmployeeDependent;
import config.employee.model.EmployeeEducation;
import config.employee.model.EmployeeInsurance;
import config.employee.model.EmployeeLanguage;
import config.employee.model.EmployeeMilitary;
import config.employee.model.EmployeeQualification;
import config.employee.model.EmployeeRewardPunishment;
import config.employee.model.EmployeeTraining;

public class EmployeeCard {
	private int employeeId;
	
	// ==========================================
	// 1. 기본 인적사항 & 퇴직사항 (Employee + Employee2 통합)
	// ==========================================
	private String employeeNo;
	private Date hireDate;
	private String photoPath;
	private String employeeName;
	private String employeeNameEn;
	private String residentRegNo;
	private String employmentType;
	private String email;
	private String phone;
	private String mobile;

	private String retireType;      // 퇴직구분
	private Date retireDate;        // 퇴직일자
	private String retireReason;    // 퇴직사유
	private String retirePhone;     // 퇴직 후 연락처

	// ==========================================
	// 2. 하위 1:N 데이터 리스트
	// ==========================================
	private List<EmployeeDependent> dependentList;
	private List<EmployeeInsurance> insuranceList;
	private List<EmployeeEducation> educationList;
	private EmployeeMilitary militaryInfo; 
	private List<EmployeeCareer> careerList;
	private List<EmployeeQualification> qualificationList;
	private List<EmployeeLanguage> languageList;
	private List<EmployeeTraining> trainingList;
	private List<EmployeeRewardPunishment> rewardPunishmentList;
	private List<EmployeeAppointment> appointmentList;

	// ==========================================
	// Getter & Setter
	// ==========================================
	
	public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
	
	public String getEmployeeNo() { return employeeNo; }
	public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
	
	public Date getHireDate() { return hireDate; }
	public void setHireDate(Date hireDate) { this.hireDate = hireDate; }
	
	public String getPhotoPath() { return photoPath; }
	public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
	
	public String getEmployeeName() { return employeeName; }
	public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
	
	public String getEmployeeNameEn() { return employeeNameEn; }
	public void setEmployeeNameEn(String employeeNameEn) { this.employeeNameEn = employeeNameEn; }
	
	public String getResidentRegNo() { return residentRegNo; }
	public void setResidentRegNo(String residentRegNo) { this.residentRegNo = residentRegNo; }
	
	public String getEmploymentType() { return employmentType; }
	public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
	
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
	
	public String getMobile() { return mobile; }
	public void setMobile(String mobile) { this.mobile = mobile; }

	public String getRetireType() { return retireType; }
	public void setRetireType(String retireType) { this.retireType = retireType; }
	
	public Date getRetireDate() { return retireDate; }
	public void setRetireDate(Date retireDate) { this.retireDate = retireDate; }
	
	public String getRetireReason() { return retireReason; }
	public void setRetireReason(String retireReason) { this.retireReason = retireReason; }
	
	public String getRetirePhone() { return retirePhone; }
	public void setRetirePhone(String retirePhone) { this.retirePhone = retirePhone; }

	public List<EmployeeDependent> getDependentList() { return dependentList; }
	public void setDependentList(List<EmployeeDependent> dependentList) { this.dependentList = dependentList; }
	
	public List<EmployeeInsurance> getInsuranceList() { return insuranceList; }
	public void setInsuranceList(List<EmployeeInsurance> insuranceList) { this.insuranceList = insuranceList; }
	
	public List<EmployeeEducation> getEducationList() { return educationList; }
	public void setEducationList(List<EmployeeEducation> educationList) { this.educationList = educationList; }
	
	public EmployeeMilitary getMilitaryInfo() { return militaryInfo; }
	public void setMilitaryInfo(EmployeeMilitary militaryInfo) { this.militaryInfo = militaryInfo; }
	
	public List<EmployeeCareer> getCareerList() { return careerList; }
	public void setCareerList(List<EmployeeCareer> careerList) { this.careerList = careerList; }
	
	public List<EmployeeQualification> getQualificationList() { return qualificationList; }
	public void setQualificationList(List<EmployeeQualification> qualificationList) { this.qualificationList = qualificationList; }
	
	public List<EmployeeLanguage> getLanguageList() { return languageList; }
	public void setLanguageList(List<EmployeeLanguage> languageList) { this.languageList = languageList; }
	
	public List<EmployeeTraining> getTrainingList() { return trainingList; }
	public void setTrainingList(List<EmployeeTraining> trainingList) { this.trainingList = trainingList; }
	
	public List<EmployeeRewardPunishment> getRewardPunishmentList() { return rewardPunishmentList; }
	public void setRewardPunishmentList(List<EmployeeRewardPunishment> rewardPunishmentList) { this.rewardPunishmentList = rewardPunishmentList; }
	
	public List<EmployeeAppointment> getAppointmentList() { return appointmentList; }
	public void setAppointmentList(List<EmployeeAppointment> appointmentList) { this.appointmentList = appointmentList; }
}