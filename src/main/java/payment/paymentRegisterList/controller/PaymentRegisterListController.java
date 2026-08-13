package payment.paymentRegisterList.controller;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentRegisterList.dto.PaymentRegisterListDTO;
import payment.paymentRegisterList.service.PaymentRegisterListService;

// 급여대장 화면 초기 진입 컨트롤러
public class PaymentRegisterListController implements CommandHandler {

    private PaymentRegisterListService service = new PaymentRegisterListService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        // 1. 귀속연도 파라미터 수집 (없으면 올해를 기본값으로)
        String payYear = request.getParameter("payYear");
        if (payYear == null || payYear.trim().isEmpty()) {
            payYear = String.valueOf(LocalDate.now().getYear());
        }

        // 2. 귀속연도의 급여차수 목록 조회
        List<PaymentRegisterListDTO> payrollList = service.getPayrollList(payYear);

        // 3. 합계 (지급총액/공제총액/실지급액)
        long totalPay = 0, totalDeduction = 0, totalNet = 0;
        for (PaymentRegisterListDTO dto : payrollList) {
            totalPay += dto.getTotalPayAmount();
            totalDeduction += dto.getTotalDeductionAmount();
            totalNet += dto.getNetPayAmount();
        }

        request.setAttribute("payYear", payYear);
        request.setAttribute("payrollList", payrollList);
        request.setAttribute("totalPay", totalPay);
        request.setAttribute("totalDeduction", totalDeduction);
        request.setAttribute("totalNet", totalNet);

        return "/WEB-INF/pages/payment/paymentRegisterList.jsp";
    }
}
