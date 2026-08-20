package payment.paymentRegisterList.controller;

import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentRegisterList.dto.PaymentRegisterListDetailResult;
import payment.paymentRegisterList.service.PaymentRegisterListDetailService;

// 급여대장 상세(사원별 지급/공제 내역) 화면 컨트롤러
public class PaymentRegisterListDetailController implements CommandHandler {

    private PaymentRegisterListDetailService service = new PaymentRegisterListDetailService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        String payYear = request.getParameter("payYear");
        String payMonth = request.getParameter("payMonth");
        String paySequenceParam = request.getParameter("paySequence");

        LocalDate now = LocalDate.now();
        if (payYear == null || payYear.trim().isEmpty()) { payYear = String.valueOf(now.getYear()); }
        if (payMonth == null || payMonth.trim().isEmpty()) { payMonth = String.format("%02d", now.getMonthValue()); }
        int paySequence = 1;
        if (paySequenceParam != null && !paySequenceParam.trim().isEmpty()) {
            paySequence = Integer.parseInt(paySequenceParam);
        }

        String empType = request.getParameter("empType");
        String department = request.getParameter("department");
        String incomeType = request.getParameter("incomeType");
        String layout = request.getParameter("layout");
        if (layout == null || layout.trim().isEmpty()) { layout = "long"; }

        PaymentRegisterListDetailResult result = service.getDetail(payYear, payMonth, paySequence, empType, department, incomeType);

        request.setAttribute("payYear", payYear);
        request.setAttribute("payMonth", payMonth);
        request.setAttribute("paySequence", paySequence);
        request.setAttribute("empType", empType);
        request.setAttribute("department", department);
        request.setAttribute("incomeType", incomeType);
        request.setAttribute("layout", layout);
        request.setAttribute("result", result);

        return "/WEB-INF/pages/payment/paymentRegisterListDetail.jsp";
    }
}
