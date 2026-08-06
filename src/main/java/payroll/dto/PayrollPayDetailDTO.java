package payroll.dto;

import java.util.Date;

public class PayrollPayDetailDTO {
    
    private Long payrollPayDetailId;   // 급여지급상세아이디
    private Long payrollEmployeeId;    // 사원별급여아이디
    private Long payItemId;            // 지급항목아이디
    private String itemName;           // 지급항목명 (화면 표시용으로 PAY_ITEM 테이블과 JOIN해서 가져올 값)
    private Long amount;               // 실제지급금액
    private String regId;              // 등록자 아이디
    private String modId;              // 수정자 아이디
    private Date createdAt;            // 생성일시
    private Date updatedAt;            // 수정일시

    // Getter / Setter
    public Long getPayrollPayDetailId() {
        return payrollPayDetailId;
    }
    public void setPayrollPayDetailId(Long payrollPayDetailId) {
        this.payrollPayDetailId = payrollPayDetailId;
    }
    
    public Long getPayrollEmployeeId() {
        return payrollEmployeeId;
    }
    public void setPayrollEmployeeId(Long payrollEmployeeId) {
        this.payrollEmployeeId = payrollEmployeeId;
    }
    
    public Long getPayItemId() {
        return payItemId;
    }
    public void setPayItemId(Long payItemId) {
        this.payItemId = payItemId;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getAmount() {
        return amount;
    }
    public void setAmount(Long amount) {
        this.amount = amount;
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
    
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}