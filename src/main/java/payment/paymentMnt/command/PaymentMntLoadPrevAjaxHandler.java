package payment.paymentMnt.command;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import command.CommandHandler;
import payment.paymentMnt.service.PaymentMntService;

public class PaymentMntLoadPrevAjaxHandler implements CommandHandler {
    private PaymentMntService service = new PaymentMntService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String currYear = request.getParameter("currYear");
        String currMonth = request.getParameter("currMonth");
        String currSeqStr = request.getParameter("currSeq");
        String prevYearMonth = request.getParameter("prevYearMonth");
        String prevSeqStr = request.getParameter("prevSeq");

        String currYearMonth = currYear + currMonth;
        int currSeq = Integer.parseInt(currSeqStr);
        int prevSeq = Integer.parseInt(prevSeqStr);

        // Service 호출 (기존 데이터 삭제 후 복사, 복사된 건수 받아오기)
        int count = service.loadPreviousPayrollDataWithDelete(prevYearMonth, prevSeq, currYearMonth, currSeq);

        // JSON 형태로 결과 응답 (예: {"status":"SUCCESS", "count":8})
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("{\"status\":\"SUCCESS\", \"count\":" + count + "}");
        out.flush();

        return null;
    }
}