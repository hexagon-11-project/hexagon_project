package payment.paymentMnt.command;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMnt.service.PaymentMntService;

// [전체삭제] - 현재 급여차수의 일용직이 아닌 사원 전체와 상세를 DB에서 실제 삭제
// [全体削除] - 現在の給与回の日雇いではない社員全体と詳細をDBから実際に削除
public class PaymentMntDeleteAllHandler implements CommandHandler {

    private PaymentMntService service = new PaymentMntService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        String payrollIdStr = request.getParameter("payrollId");
        if (payrollIdStr != null && !payrollIdStr.isEmpty()) {
            service.deleteAllEmployees(Long.parseLong(payrollIdStr));
        }

        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("SUCCESS");
        out.flush();
        return null;
    }
}
