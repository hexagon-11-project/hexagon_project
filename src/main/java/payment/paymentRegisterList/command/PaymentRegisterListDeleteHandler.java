package payment.paymentRegisterList.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentRegisterList.service.PaymentRegisterListService;

// 급여대장 목록에서 [삭제] 버튼 클릭 시 급여차수(PAYROLL) 삭제
public class PaymentRegisterListDeleteHandler implements CommandHandler {

    private PaymentRegisterListService service = new PaymentRegisterListService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        String payrollIdStr = request.getParameter("payrollId");

        String result = "FAIL";
        if (payrollIdStr != null && !payrollIdStr.trim().isEmpty()) {
            try {
                service.deletePayroll(Long.parseLong(payrollIdStr.trim()));
                result = "SUCCESS";
            } catch (Exception e) {
                result = "FAIL";
            }
        }

        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().write(result);
        return null;
    }
}
