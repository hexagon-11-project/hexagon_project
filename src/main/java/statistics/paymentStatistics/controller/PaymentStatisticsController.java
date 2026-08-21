package statistics.paymentStatistics.controller;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import statistics.paymentStatistics.dto.PersonalAnnualStatistics;
import statistics.paymentStatistics.service.PaymentStatisticsService;

// 연도별 개인연봉 통계 화면 컨트롤러
public class PaymentStatisticsController implements CommandHandler {

    private PaymentStatisticsService service = new PaymentStatisticsService();

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

        List<PersonalAnnualStatistics> annualList = service.getPersonalAnnualList(employeeName, year);

        request.setAttribute("year", year);
        request.setAttribute("employeeName", employeeName);
        request.setAttribute("annualList", annualList);

        return "/WEB-INF/pages/statistics/paymentStatistics.jsp";
    }
}
