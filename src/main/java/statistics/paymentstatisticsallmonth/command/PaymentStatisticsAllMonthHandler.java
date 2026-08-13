package statistics.paymentstatisticsallmonth.command;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import statistics.model.MonthlyTotalStatistics;
import statistics.paymentstatisticsallmonth.service.PaymentStatisticsAllMonthService;

/**
 * 월별 전체급여 통계 화면 핸들러.
 *
 * 선택 연도의 1월~12월 통계를 조회한다.
 */
public class PaymentStatisticsAllMonthHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/pages/statistics/paymentstatisticsallmonth.jsp";
	private static final int DEFAULT_COMPANY_ID = 1001;

	private PaymentStatisticsAllMonthService paymentStatisticsAllMonthService = new PaymentStatisticsAllMonthService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		int companyId = DEFAULT_COMPANY_ID;
		int year = parseYear(req.getParameter("year"));

		List<MonthlyTotalStatistics> monthlyTotalList =
				paymentStatisticsAllMonthService.getMonthlyTotalList(companyId, year);

		req.setAttribute("year", year);
		req.setAttribute("monthlyTotalList", monthlyTotalList);
		return FORM_VIEW;
	}

	/** 요청 연도가 없거나 잘못되면 올해를 사용한다. */
	private int parseYear(String yearParam) {
		int currentYear = LocalDate.now().getYear();
		if (yearParam == null || yearParam.trim().isEmpty()) {
			return currentYear;
		}
		try {
			int year = Integer.parseInt(yearParam.trim());
			if (year < 1900 || year > currentYear + 1) {
				return currentYear;
			}
			return year;
		} catch (NumberFormatException e) {
			return currentYear;
		}
	}
}
