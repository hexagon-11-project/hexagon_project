package statistics.paymentstatisticsall.command;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import statistics.model.AnnualTotalStatistics;
import statistics.paymentstatisticsall.dao.PaymentStatisticsAllDao;

/**
 * 연도별 전체급여 통계 화면 핸들러.
 *
 * 선택 연도(endYear) 기준 과거 10년 통계를 조회한다.
 * 예: endYear=2026 → 2017~2026
 */
public class PaymentStatisticsAllHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/pages/statistics/annual-total.jsp";
	private static final int DEFAULT_COMPANY_ID = 1001;

	private PaymentStatisticsAllDao paymentStatisticsAllDao = new PaymentStatisticsAllDao();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		int companyId = DEFAULT_COMPANY_ID;
		int endYear = parseEndYear(req.getParameter("endYear"));

		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			List<AnnualTotalStatistics> annualTotalList =
					paymentStatisticsAllDao.selectAnnualTotalByEndYear(conn, companyId, endYear);

			req.setAttribute("endYear", endYear);
			req.setAttribute("annualTotalList", annualTotalList);
			return FORM_VIEW;
		} catch (SQLException e) {
			throw new RuntimeException("연도별 전체급여 통계 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/** 요청 연도가 없거나 잘못되면 올해를 사용한다. */
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
