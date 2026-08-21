package statistics.paymentstatisticsmonth.controller;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import statistics.paymentstatisticsmonth.dto.PersonalMonthlyStatistics;
import statistics.paymentstatisticsmonth.service.PaymentStatisticsMonthService;

// 월별 개인급여 통계 화면 컨트롤러
public class PaymentStatisticsMonthController implements CommandHandler {

    private PaymentStatisticsMonthService service = new PaymentStatisticsMonthService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        String yearParam = request.getParameter("year");
        String employeeName = request.getParameter("employeeName");

        // 파라미터 없이 처음 들어왔을 때(최초 진입)는 기본으로 강해린 사원의 화면을 보여준다.
        boolean firstVisit = request.getParameterMap().isEmpty();
        if (firstVisit && (employeeName == null || employeeName.trim().isEmpty())) {
            employeeName = "강해린";
        }

        int year = (yearParam == null || yearParam.trim().isEmpty())
                ? LocalDate.now().getYear()
                : Integer.parseInt(yearParam.trim());

        List<PersonalMonthlyStatistics> monthlyList = service.getPersonalMonthlyList(employeeName, year);

        request.setAttribute("year", year);
        request.setAttribute("employeeName", employeeName);
        request.setAttribute("monthlyList", monthlyList);

        return "/WEB-INF/pages/statistics/paymentStatisticsMonth.jsp";
    }
}
