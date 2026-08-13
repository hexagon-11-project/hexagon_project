package payment.paymentMntDayWorker.command;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMntDayWorker.service.PaymentMntDayWorkerService;

// 하단 [급여 종합정보] 갱신용 - 공제항목 수정 등으로 금액이 바뀔 때마다 페이지 새로고침 없이 최신 합계를 JSON으로 반환
public class PaymentMntDayWorkerSummaryAjaxHandler implements CommandHandler {

    private PaymentMntDayWorkerService service = new PaymentMntDayWorkerService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String payrollDayWorkerIdStr = request.getParameter("payrollDayWorkerId");
        if (payrollDayWorkerIdStr == null || payrollDayWorkerIdStr.isEmpty()) {
            return null;
        }
        Long payrollDayWorkerId = Long.parseLong(payrollDayWorkerIdStr);

        Map<String, Object> summary = service.getSummary(payrollDayWorkerId);

        StringBuilder json = new StringBuilder();
        json.append("{")
            .append("\"workerCount\":").append(nz(summary.get("workerCount"))).append(",")
            .append("\"payTotal\":").append(nz(summary.get("payTotal"))).append(",")
            .append("\"deductionTotal\":").append(nz(summary.get("deductionTotal"))).append(",")
            .append("\"netPay\":").append(nz(summary.get("netPay")))
            .append("}");

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();

        return null;
    }

    private Object nz(Object v) { return v == null ? 0 : v; }
}
