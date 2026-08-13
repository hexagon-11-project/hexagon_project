package payment.paymentMnt.dto;

import java.util.List;

/** 사원별급여(payrollEmployeeId) 하나의 "최종" 지급/공제 상세 - 저장된 값에 기본값 보정(기본급/일용급여/공제 기본값)까지
 *  반영한 결과. 급여입력/관리 화면의 좌측 사원목록 총액과 우측 상세패널이 항상 같은 계산으로 나오도록
 *  하나의 로직(PaymentMntDAO#computeEffectiveDetail)만 사용한다. */
public class PaymentMntEffectiveDetail {

    private List<PaymentMntPayDetailDTO> payDetails;
    private List<PaymentMntDeductionDetailDTO> deductionDetails;
    private long totalPayAmount;
    private long totalDeductionAmount;
    private long netPayAmount;

    public List<PaymentMntPayDetailDTO> getPayDetails() { return payDetails; }
    public void setPayDetails(List<PaymentMntPayDetailDTO> payDetails) { this.payDetails = payDetails; }

    public List<PaymentMntDeductionDetailDTO> getDeductionDetails() { return deductionDetails; }
    public void setDeductionDetails(List<PaymentMntDeductionDetailDTO> deductionDetails) { this.deductionDetails = deductionDetails; }

    public long getTotalPayAmount() { return totalPayAmount; }
    public void setTotalPayAmount(long totalPayAmount) { this.totalPayAmount = totalPayAmount; }

    public long getTotalDeductionAmount() { return totalDeductionAmount; }
    public void setTotalDeductionAmount(long totalDeductionAmount) { this.totalDeductionAmount = totalDeductionAmount; }

    public long getNetPayAmount() { return netPayAmount; }
    public void setNetPayAmount(long netPayAmount) { this.netPayAmount = netPayAmount; }
}
