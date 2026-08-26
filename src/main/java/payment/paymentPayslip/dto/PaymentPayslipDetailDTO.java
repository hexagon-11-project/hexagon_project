package payment.paymentPayslip.dto;

import java.util.ArrayList;
import java.util.List;

// 급여명세서 사원 1인 정보 - 좌측 목록(구분/성명)과 우측 미리보기(인적사항 + 지급/공제 내역)에 함께 쓰인다
// 給与明細書社員1人分の情報 - 左側一覧（区分/氏名）と右側プレビュー（人的事項＋支給・控除内訳）に共に使われる
public class PaymentPayslipDetailDTO {

    private Long payrollEmployeeId;
    private String employmentType; // 구분 / 区分
    private String employeeName;   // 성명 / 氏名
    private String residentRegNo;  // 생년월일(주민등록번호) / 生年月日（住民登録番号）
    private String department;     // 부서 / 部署
    private String position;       // 직급 / 職級
    private String hireDate;       // 입사일 / 入社日

    private List<PaymentPayslipItemDTO> payItems = new ArrayList<>();
    private long totalPayAmount;

    private List<PaymentPayslipItemDTO> deductionItems = new ArrayList<>();
    private long totalDeductionAmount;

    private long netPayAmount;

    public Long getPayrollEmployeeId() { return payrollEmployeeId; }
    public void setPayrollEmployeeId(Long payrollEmployeeId) { this.payrollEmployeeId = payrollEmployeeId; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getResidentRegNo() { return residentRegNo; }
    public void setResidentRegNo(String residentRegNo) { this.residentRegNo = residentRegNo; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public List<PaymentPayslipItemDTO> getPayItems() { return payItems; }
    public void setPayItems(List<PaymentPayslipItemDTO> payItems) { this.payItems = payItems; }

    public long getTotalPayAmount() { return totalPayAmount; }
    public void setTotalPayAmount(long totalPayAmount) { this.totalPayAmount = totalPayAmount; }

    public List<PaymentPayslipItemDTO> getDeductionItems() { return deductionItems; }
    public void setDeductionItems(List<PaymentPayslipItemDTO> deductionItems) { this.deductionItems = deductionItems; }

    public long getTotalDeductionAmount() { return totalDeductionAmount; }
    public void setTotalDeductionAmount(long totalDeductionAmount) { this.totalDeductionAmount = totalDeductionAmount; }

    public long getNetPayAmount() { return netPayAmount; }
    public void setNetPayAmount(long netPayAmount) { this.netPayAmount = netPayAmount; }
}
