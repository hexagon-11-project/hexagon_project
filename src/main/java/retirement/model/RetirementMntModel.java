package retirement.model;

import java.util.List;

public class RetirementMntModel {
    // 기본 정보
    private String employeeId;
    private String employeeNo;
    private String employeeName;
    private String hireDate;
    private String resignDate;
    
    
    private String retirementSettlementYn; 
    
    // 계산 정보 (RETIREMENT_PAY 테이블)
    private int serviceDays;
    private long totalWageAmount;     
    private double averageDailyWage;  
    private long retirementPayAmount; 
    
    // 3개월 급여 리스트
    private List<MonthlyWage> monthlyWages;

    public static class MonthlyWage {
        private String wageMonth;   
        private long paymentAmount; 
        private int days;           
        
        public String getWageMonth() { return wageMonth; }
        public void setWageMonth(String wageMonth) { this.wageMonth = wageMonth; }
        public long getPaymentAmount() { return paymentAmount; }
        public void setPaymentAmount(long paymentAmount) { this.paymentAmount = paymentAmount; }
        public int getDays() { return days; }
        public void setDays(int days) { this.days = days; }
    }

    // Getters & Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }
    public String getResignDate() { return resignDate; }
    public void setResignDate(String resignDate) { this.resignDate = resignDate; }
    
    public String getRetirementSettlementYn() { return retirementSettlementYn; }
    public void setRetirementSettlementYn(String retirementSettlementYn) { this.retirementSettlementYn = retirementSettlementYn; }
    
    public int getServiceDays() { return serviceDays; }
    public void setServiceDays(int serviceDays) { this.serviceDays = serviceDays; }
    public long getTotalWageAmount() { return totalWageAmount; }
    public void setTotalWageAmount(long totalWageAmount) { this.totalWageAmount = totalWageAmount; }
    public double getAverageDailyWage() { return averageDailyWage; }
    public void setAverageDailyWage(double averageDailyWage) { this.averageDailyWage = averageDailyWage; }
    public long getRetirementPayAmount() { return retirementPayAmount; }
    public void setRetirementPayAmount(long retirementPayAmount) { this.retirementPayAmount = retirementPayAmount; }
    public List<MonthlyWage> getMonthlyWages() { return monthlyWages; }
    public void setMonthlyWages(List<MonthlyWage> monthlyWages) { this.monthlyWages = monthlyWages; }
}