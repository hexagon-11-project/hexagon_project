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
 * 月別給与総額統計画面ハンドラ。
 *
 */
public class PaymentStatisticsAllMonthHandler implements CommandHandler {

	// 통계 화면 JSP 경로
	// 統計画面JSPパス。
	private static final String FORM_VIEW = "/WEB-INF/pages/statistics/paymentstatisticsallmonth.jsp";
	// 기본 회사 ID
	// 既定の会社ID。
	private static final int DEFAULT_COMPANY_ID = 1001;

	private PaymentStatisticsAllMonthService paymentStatisticsAllMonthService = new PaymentStatisticsAllMonthService();

	/**
	 * 선택 연도 1~12월 통계를 조회해 화면에 넘긴다.
	 * 選択年の1〜12月統計を照会して画面に渡す。
	 *
	 */
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

	/**
	 * 요청 연도가 없거나 잘못되면 올해를 사용한다.
	 * リクエスト年がないか不正なら今年を使う。
	 *
	 */
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
