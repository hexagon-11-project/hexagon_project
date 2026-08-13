package payment.paymentMntDayWorker.command;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMntDayWorker.model.PaymentMntDayWorkerDailyVO;
import payment.paymentMntDayWorker.model.PaymentMntDayWorkerDeductionVO;
import payment.paymentMntDayWorker.service.PaymentMntDayWorkerService;

// [저장] 버튼 - 일자별 지급내역(배열) + 공제항목을 한번에 저장
// 화면에서 workDate[], rate[], payAmt[], incomeTax[], localTax[] 형태의 배열 파라미터로 전송
public class PaymentMntDayWorkerSaveHandler implements CommandHandler {

    private PaymentMntDayWorkerService service = new PaymentMntDayWorkerService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        String empIdStr = request.getParameter("payrollDayWorkerEmployeeId");
        if (empIdStr == null || empIdStr.isEmpty()) {
            writeResult(response, "FAIL");
            return null;
        }
        Long payrollDayWorkerEmployeeId = Long.parseLong(empIdStr);

        // 1. 일자별 지급내역 배열 파싱
        String[] workDates = request.getParameterValues("workDate");
        String[] rates = request.getParameterValues("rate");
        String[] payAmts = request.getParameterValues("payAmt");
        String[] incomeTaxes = request.getParameterValues("incomeTax");
        String[] localTaxes = request.getParameterValues("localTax");

        List<PaymentMntDayWorkerDailyVO> dailyList = new ArrayList<>();
        if (workDates != null) {
            for (int i = 0; i < workDates.length; i++) {
                if (workDates[i] == null || workDates[i].isEmpty()) continue; // 일자 없는 빈 행 제외
                PaymentMntDayWorkerDailyVO d = new PaymentMntDayWorkerDailyVO();
                d.setWorkDate(workDates[i]);
                d.setRate(parseBigDecimal(get(rates, i), BigDecimal.ONE));
                d.setPayAmt(parseLong(get(payAmts, i)));
                d.setIncomeTax(parseLong(get(incomeTaxes, i)));
                d.setLocalTax(parseLong(get(localTaxes, i)));
                dailyList.add(d);
            }
        }

        // 2. 공제항목 파싱 (dedItemId[], dedAmount[] 배열 - 공제항목 패널이 DB 마스터 기준으로 동적 렌더링되므로 항목 수가 고정이 아님)
        PaymentMntDayWorkerDeductionVO deduction = new PaymentMntDayWorkerDeductionVO();
        deduction.setDeductionMode(request.getParameter("deductionMode") != null ? request.getParameter("deductionMode") : "4대보험");

        String[] dedItemIds = request.getParameterValues("dedItemId");
        String[] dedAmounts = request.getParameterValues("dedAmount");
        if (dedItemIds != null) {
            for (int i = 0; i < dedItemIds.length; i++) {
                if (dedItemIds[i] == null || dedItemIds[i].isEmpty()) continue;
                deduction.putAmount(Integer.parseInt(dedItemIds[i]), parseLong(get(dedAmounts, i)));
            }
        }

        // 3. 저장 (Service에서 합계 계산 및 트랜잭션 처리)
        service.saveDetail(payrollDayWorkerEmployeeId, dailyList, deduction);

        writeResult(response, "SUCCESS");
        return null;
    }

    private void writeResult(HttpServletResponse response, String result) throws Exception {
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();
    }

    private String get(String[] arr, int i) {
        return (arr != null && i < arr.length) ? arr[i] : null;
    }

    private Long parseLong(String s) {
        if (s == null || s.trim().isEmpty()) return 0L;
        try {
            return Long.parseLong(s.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private BigDecimal parseBigDecimal(String s, BigDecimal defaultVal) {
        if (s == null || s.trim().isEmpty()) return defaultVal;
        try {
            return new BigDecimal(s.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
