package config.employee.model;

import java.sql.Date;

// EMPLOYEE_TRAINING 테이블 매핑 (교육훈련)
public class EmployeeTraining {
    private String trainingTypeCode; // TRAINING_TYPE_CODE
    private String trainingName;     // TRAINING_NAME
    private Date startDate;          // TRAINING_START_DATE
    private Date endDate;            // TRAINING_END_DATE
    private String trainingInstitution; // TRAINING_INSTITUTION
    private long trainingCost;       // TRAINING_COST
    private long refundTrainingCost; // REFUND_TRAINING_COST

    public String getTrainingTypeCode() { return trainingTypeCode; }
    public void setTrainingTypeCode(String v) { this.trainingTypeCode = v; }
    public String getTrainingName() { return trainingName; }
    public void setTrainingName(String v) { this.trainingName = v; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date v) { this.startDate = v; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date v) { this.endDate = v; }
    public String getTrainingInstitution() { return trainingInstitution; }
    public void setTrainingInstitution(String v) { this.trainingInstitution = v; }
    public long getTrainingCost() { return trainingCost; }
    public void setTrainingCost(long v) { this.trainingCost = v; }
    public long getRefundTrainingCost() { return refundTrainingCost; }
    public void setRefundTrainingCost(long v) { this.refundTrainingCost = v; }
}
