package payment.paymentMntDayWorker.command;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMntDayWorker.service.PaymentMntDayWorkerService;

// [선택삭제] - 체크된 근로자(payrollDayWorkerEmployeeIds, 콤마구분)를 DB에서 실제 삭제
public class PaymentMntDayWorkerDeleteSelectedHandler implements CommandHandler {

    private PaymentMntDayWorkerService service = new PaymentMntDayWorkerService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        String idsStr = request.getParameter("payrollDayWorkerEmployeeIds");

        List<Long> ids = new ArrayList<>();
        if (idsStr != null && !idsStr.isEmpty()) {
            for (String s : idsStr.split(",")) {
                if (!s.trim().isEmpty()) ids.add(Long.parseLong(s.trim()));
            }
        }
        service.deleteEmployees(ids);

        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("SUCCESS");
        out.flush();
        return null;
    }
}
