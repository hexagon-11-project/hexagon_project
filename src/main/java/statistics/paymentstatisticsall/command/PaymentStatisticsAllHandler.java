package statistics.paymentstatisticsall.command;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import statistics.model.AnnualTotalStatistics;
import statistics.paymentstatisticsall.service.PaymentStatisticsAllService;

/**
 * 연도별 전체급여 통계 화면 핸들러.
 * 年度別給与総額統計画面ハンドラ。
 *
 */
public class PaymentStatisticsAllHandler implements CommandHandler {

	// 통계 화면 JSP 경로
	// 統計画面JSPパス。
	private static final String FORM_VIEW = "/WEB-INF/pages/statistics/paymentstatisticsall.jsp";
	// 기본 회사 ID
	// 既定の会社ID。
	private static final int DEFAULT_COMPANY_ID = 1001;

	private PaymentStatisticsAllService paymentStatisticsAllService = new PaymentStatisticsAllService();

	/**
	 * 기준 연도 10년 구간 통계를 조회해 화면에 넘긴다.
	 * 基準年の10年区間統計を照会して画面に渡す。
	 *
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		int companyId = DEFAULT_COMPANY_ID;
		int endYear = parseEndYear(req.getParameter("endYear"));

		List<AnnualTotalStatistics> annualTotalList =
				paymentStatisticsAllService.getAnnualTotalList(companyId, endYear);

		req.setAttribute("endYear", endYear);
		req.setAttribute("annualTotalList", annualTotalList);
		return FORM_VIEW;
	}

	/**
	 * 요청 연도가 없거나 잘못되면 올해를 사용한다.
	 * リクエスト年がないか不正なら今年を使う。
	 *
	 */
	private int parseEndYear(String endYearParam) {
		int currentYear = LocalDate.now().getYear();
		if (endYearParam == null || endYearParam.trim().isEmpty()) {
			return currentYear;
		}
		try {
			int endYear = Integer.parseInt(endYearParam.trim());
			if (endYear < 1900 || endYear > currentYear + 1) {
				return currentYear;
			}
			return endYear;
		} catch (NumberFormatException e) {
			return currentYear;
		}
	}
}
