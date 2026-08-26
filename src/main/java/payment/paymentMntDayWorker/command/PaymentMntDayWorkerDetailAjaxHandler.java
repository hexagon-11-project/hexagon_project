package payment.paymentMntDayWorker.command;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMntDayWorker.model.PaymentMntDayWorkerDailyVO;
import payment.paymentMntDayWorker.model.PaymentMntDayWorkerDeductionVO;
import payment.paymentMntDayWorker.service.PaymentMntDayWorkerService;

// 좌측 근로자 목록에서 행을 클릭했을 때, 우측 급여상세(일자별 내역 + 공제항목)를 JSON으로 반환
// 左側労働者一覧で行をクリックした時、右側給与詳細（日別内訳＋控除項目）をJSONで返す
public class PaymentMntDayWorkerDetailAjaxHandler implements CommandHandler {

    private PaymentMntDayWorkerService service = new PaymentMntDayWorkerService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String empIdStr = request.getParameter("payrollDayWorkerEmployeeId");
        if (empIdStr == null || empIdStr.isEmpty()) {
            return null;
        }
        Long payrollDayWorkerEmployeeId = Long.parseLong(empIdStr);

        List<PaymentMntDayWorkerDailyVO> dailyList = service.getDailyList(payrollDayWorkerEmployeeId);
        PaymentMntDayWorkerDeductionVO deduction = service.getDeduction(payrollDayWorkerEmployeeId);

        // 공제항목(PAYROLL_DEDUCTION_DETAIL)이 이 급여차수엔 아직 저장 안 돼 있으면, 좌측 일자별 지급내역에 이미
        // 계산되어 있는 소득세/지방소득세 합계를 공제항목의 '소득세'/'지방소득세'에 기본값으로 채워서 보여준다.
        // (하단 [급여 종합정보]의 공제총액은 이 일자별 합계 기준이라, 공제항목 패널이 0으로만 보이면 서로 안 맞아 보임)
        // 控除項目（PAYROLL_DEDUCTION_DETAIL）がこの給与回にまだ保存されていなければ、左側の日別支給内訳ですでに
        // 計算されている所得税・地方所得税の合計を控除項目の「所得税」/「地方所得税」に初期値として埋めて表示する。
        // （下部[給与総合情報]の控除総額はこの日別合計基準のため、控除項目パネルが0のままだと互いに合わなく見える）
        if ((deduction == null || deduction.getAmounts().isEmpty()) && dailyList != null && !dailyList.isEmpty()) {
            long sumIncomeTax = 0, sumLocalTax = 0;
            for (PaymentMntDayWorkerDailyVO d : dailyList) {
                sumIncomeTax += (d.getIncomeTax() == null ? 0L : d.getIncomeTax());
                sumLocalTax += (d.getLocalTax() == null ? 0L : d.getLocalTax());
            }
            Long incomeTaxItemId = service.getDeductionItemIdByName("소득세");
            Long localTaxItemId = service.getDeductionItemIdByName("지방소득세");
            if (incomeTaxItemId != null || localTaxItemId != null) {
                if (deduction == null) {
                    deduction = new PaymentMntDayWorkerDeductionVO();
                    deduction.setPayrollDayWorkerEmployeeId(payrollDayWorkerEmployeeId);
                }
                if (incomeTaxItemId != null) deduction.putAmount(incomeTaxItemId.intValue(), sumIncomeTax);
                if (localTaxItemId != null) deduction.putAmount(localTaxItemId.intValue(), sumLocalTax);
            }
        }

        StringBuilder json = new StringBuilder();
        json.append("{");

        // --- 일자별 지급내역 배열 --- / --- 日別支給内訳配列 ---
        json.append("\"dailyList\": [");
        for (int i = 0; i < dailyList.size(); i++) {
            PaymentMntDayWorkerDailyVO d = dailyList.get(i);
            json.append("{")
                .append("\"workDate\":\"").append(nvl(d.getWorkDate())).append("\",")
                .append("\"rate\":").append(d.getRate() == null ? "1.0" : d.getRate()).append(",")
                .append("\"payAmt\":").append(nz(d.getPayAmt())).append(",")
                .append("\"incomeTax\":").append(nz(d.getIncomeTax())).append(",")
                .append("\"localTax\":").append(nz(d.getLocalTax()))
                .append("}");
            if (i < dailyList.size() - 1) json.append(",");
        }
        json.append("],");

        // --- 공제항목 객체 (DEDUCTION_ITEM_ID -> 금액) --- / --- 控除項目オブジェクト（DEDUCTION_ITEM_ID -> 金額） ---
        json.append("\"deductionMode\":\"").append(deduction == null ? "" : nvl(deduction.getDeductionMode())).append("\",");
        json.append("\"deductionAmounts\": {");
        if (deduction != null) {
            int i = 0;
            int size = deduction.getAmounts().size();
            for (Map.Entry<Integer, Long> entry : deduction.getAmounts().entrySet()) {
                json.append("\"").append(entry.getKey()).append("\":").append(nz(entry.getValue()));
                if (++i < size) json.append(",");
            }
        }
        json.append("}");

        json.append("}");

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();

        return null;
    }

    private String nvl(String s) { return s == null ? "" : s; }
    private long nz(Long v) { return v == null ? 0L : v; }
}
