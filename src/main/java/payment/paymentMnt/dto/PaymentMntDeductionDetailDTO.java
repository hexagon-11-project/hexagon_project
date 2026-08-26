package payment.paymentMnt.dto;

import java.util.Date;

public class PaymentMntDeductionDetailDTO {
    
    private Long payrollDeductionDetailId; // 급여공제상세아이디 / 給与控除詳細ID
    private Long payrollEmployeeId;        // 사원별급여아이디 / 社員別給与ID
    private Long deductionItemId;          // 공제항목아이디 / 控除項目ID
    private String itemName;               // 공제항목명 (화면 표시용으로 DEDUCTION_ITEM 테이블과 JOIN해서 가져올 값) / 控除項目名（画面表示用にDEDUCTION_ITEMテーブルとJOINして取得する値）
    private Long amount;                   // 실제공제금액 / 実際の控除金額
    private String regId;                  // 등록자 아이디 / 登録者ID
    private String modId;                  // 수정자 아이디 / 修正者ID
    private Date createdAt;                // 생성일시 / 作成日時
    private Date updatedAt;                // 수정일시 / 修正日時

    // Getter / Setter
    public Long getPayrollDeductionDetailId() {
        return payrollDeductionDetailId;
    }
    public void setPayrollDeductionDetailId(Long payrollDeductionDetailId) {
        this.payrollDeductionDetailId = payrollDeductionDetailId;
    }

    public Long getPayrollEmployeeId() {
        return payrollEmployeeId;
    }
    public void setPayrollEmployeeId(Long payrollEmployeeId) {
        this.payrollEmployeeId = payrollEmployeeId;
    }

    public Long getDeductionItemId() {
        return deductionItemId;
    }
    public void setDeductionItemId(Long deductionItemId) {
        this.deductionItemId = deductionItemId;
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