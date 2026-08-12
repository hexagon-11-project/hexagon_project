package payment.paymentMntDayWorker.command;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMntDayWorker.service.PaymentMntDayWorkerService;

// [지난급여 불러오기] - 이전 귀속연월/차수의 근로자+일자별내역+공제항목을 현재 차수로 복사
public class PaymentMntDayWorkerLoadPrevAjaxHandler implements CommandHandler {

    private PaymentMntDayWorkerService service = new PaymentMntDayWorkerService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        String currYear = request.getParameter("currYear");
        String currMonth = request.getParameter("currMonth");
        String currSeqStr = request.getParameter("currSeq");
        String prevYearMonth = request.getParameter("prevYearMonth");
        String prevSeqStr = request.getParameter("prevSeq");

        String currYearMonth = currYear + currMonth;
        int currSeq = Integer.parseInt(currSeqStr);
        int prevSeq = Integer.parseInt(prevSeqStr);

        int count = service.loadPreviousPayrollData(prevYearMonth, prevSeq, currYearMonth, currSeq);

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("{\"status\":\"SUCCESS\", \"count\":" + count + "}");
        out.flush();

        return null;
    }
}
