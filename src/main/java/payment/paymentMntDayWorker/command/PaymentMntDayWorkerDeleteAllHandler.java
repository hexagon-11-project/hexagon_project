package payment.paymentMntDayWorker.command;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMntDayWorker.service.PaymentMntDayWorkerService;

// [전체삭제] - 현재 급여차수의 모든 근로자+상세를 DB에서 실제 삭제
public class PaymentMntDayWorkerDeleteAllHandler implements CommandHandler {

    private PaymentMntDayWorkerService service = new PaymentMntDayWorkerService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        String payrollDayWorkerIdStr = request.getParameter("payrollDayWorkerId");
        if (payrollDayWorkerIdStr != null && !payrollDayWorkerIdStr.isEmpty()) {
            service.deleteAllEmployees(Long.parseLong(payrollDayWorkerIdStr));
        }

        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("SUCCESS");
        out.flush();
        return null;
    }
}
