package payment.paymentPayList.controller;

import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentPayList.dto.PaymentPayListResult;
import payment.paymentPayList.service.PaymentPayListService;

// 사원별 급여내역 화면 컨트롤러
public class PaymentPayListController implements CommandHandler {

    private PaymentPayListService service = new PaymentPayListService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        String employeeName = request.getParameter("employeeName");
        String startYear = request.getParameter("startYear");
        String startMonth = request.getParameter("startMonth");
        String endYear = request.getParameter("endYear");
        String endMonth = request.getParameter("endMonth");

        // 파라미터가 없으면(최초 진입) 기본값으로 '올해 1월 ~ 이번달'을 사용한다
        LocalDate now = LocalDate.now();
        if (startYear == null || startYear.trim().isEmpty()) { startYear = String.valueOf(now.getYear()); }
        if (startMonth == null || startMonth.trim().isEmpty()) { startMonth = "01"; }
        if (endYear == null || endYear.trim().isEmpty()) { endYear = String.valueOf(now.getYear()); }
        if (endMonth == null || endMonth.trim().isEmpty()) { endMonth = String.format("%02d", now.getMonthValue()); }

        String startYearMonthDb = startYear + startMonth;
        String endYearMonthDb = endYear + endMonth;

        PaymentPayListResult result = service.getPayList(employeeName, startYearMonthDb, endYearMonthDb);

        request.setAttribute("employeeName", employeeName);
        request.setAttribute("startYear", startYear);
        request.setAttribute("startMonth", startMonth);
        request.setAttribute("endYear", endYear);
        request.setAttribute("endMonth", endMonth);
        request.setAttribute("result", result);

        return "/WEB-INF/pages/payment/paymentPayList.jsp";
    }
}
