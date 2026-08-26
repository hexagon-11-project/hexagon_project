package payment.paymentMntDayWorker.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 공제항목 - PAYROLL_DEDUCTION_DETAIL(사원 1명당 여러 행)을 DEDUCTION_ITEM_ID 기준으로 담은 뷰 객체.
 * 화면 공제항목 패널은 DEDUCTION_ITEM 마스터에 있는 항목만큼만 동적으로 그려지므로,
 * 항목을 고정 필드가 아닌 Map(DEDUCTION_ITEM_ID -> 금액)으로 보관한다.
 * 控除項目 - PAYROLL_DEDUCTION_DETAIL（社員1人あたり複数行）をDEDUCTION_ITEM_ID基準で保持するビューオブジェクト。
 * 画面の控除項目パネルはDEDUCTION_ITEMマスタにある項目分だけ動的に描画されるため、
 * 項目を固定フィールドではなくMap（DEDUCTION_ITEM_ID -> 金額）で保持する。
 */
public class PaymentMntDayWorkerDeductionVO {

    private Long payrollDayWorkerEmployeeId;    // PAYROLL_EMPLOYEE_ID
    private String deductionMode;               // '4대보험' | '기간단위 소득세' (화면 토글, 현재 DB 미저장) / '4대보험' | '기간단위 소득세'（画面トグル、現在DB未保存）
    private Map<Integer, Long> amounts = new HashMap<>(); // DEDUCTION_ITEM_ID -> 금액 / DEDUCTION_ITEM_ID -> 金額

    public Long getPayrollDayWorkerEmployeeId() { return payrollDayWorkerEmployeeId; }
    public void setPayrollDayWorkerEmployeeId(Long payrollDayWorkerEmployeeId) { this.payrollDayWorkerEmployeeId = payrollDayWorkerEmployeeId; }

    public String getDeductionMode() { return deductionMode; }
    public void setDeductionMode(String deductionMode) { this.deductionMode = deductionMode; }

    public Map<Integer, Long> getAmounts() { return amounts; }
    public void setAmounts(Map<Integer, Long> amounts) { this.amounts = amounts; }

    public void putAmount(int deductionItemId, Long amount) { amounts.put(deductionItemId, amount); }
    public Long getAmount(int deductionItemId) { return amounts.get(deductionItemId); }

    /** 공제총액 = 등록된 모든 항목 합계
     *  控除総額＝登録されたすべての項目の合計 */
    public long getTotal() {
        long total = 0;
        for (Long v : amounts.values()) total += (v == null ? 0L : v);
        return total;
    }
}
