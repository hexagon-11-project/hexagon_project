package payment.paymentPayslip.controller;

import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentPayslip.dto.PaymentPayslipResult;
import payment.paymentPayslip.service.PaymentPayslipService;

// 급여명세서 화면 초기 진입 컨트롤러
public class PaymentPayslipController implements CommandHandler {

    private PaymentPayslipService service = new PaymentPayslipService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        String payYear = request.getParameter("payYear");
        String payMonth = request.getParameter("payMonth");
        String paySequenceParam = request.getParameter("paySequence");

        // 파라미터가 없으면(최초 진입) 기본값으로 '전월'을 사용한다 (paymentMnt.jsp와 동일한 기본값 규칙)
        LocalDate defaultMonth = LocalDate.now().minusMonths(1);
        if (payYear == null || payYear.trim().isEmpty()) { payYear = String.valueOf(defaultMonth.getYear()); }
        if (payMonth == null || payMonth.trim().isEmpty()) { payMonth = String.format("%02d", defaultMonth.getMonthValue()); }
        int paySequence = 1;
        if (paySequenceParam != null && !paySequenceParam.trim().isEmpty()) {
            paySequence = Integer.parseInt(paySequenceParam);
        }

        PaymentPayslipResult result = service.getPayslipData(payYear, payMonth, paySequence);
        String employeeDetailJson = service.buildEmployeeDetailJson(result.getEmployeeList());

        request.setAttribute("payYear", payYear);
        request.setAttribute("payMonth", payMonth);
        request.setAttribute("paySequence", paySequence);
        request.setAttribute("result", result);
        request.setAttribute("employeeDetailJson", employeeDetailJson);

        return "/WEB-INF/pages/payment/paymentPayslip.jsp";
    }
}
