package diligence.dayWorkerSearchMonth.command;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.DailyWorkRecord;
import diligence.dayWorkerSearchMonth.service.DayWorkerSearchService;

// [일용직 근무조회] - 조회월/현장/사원명 조건으로 일용직 근무기록과 지급 합계를 조회 (저장 없음, 조회 전용)
public class DayWorkerSearchMonthHandler implements CommandHandler {

	private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

	// 근태기록/관리(diligence.dailyworkrecord) 화면과 동일한 고정 현장 목록
	private static final String[] WORK_SITE_OPTIONS = { "현장1", "현장2", "연구소", "개발프로젝트", "제1공장" };

	private DayWorkerSearchService dayWorkerSearchService = new DayWorkerSearchService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;

		String monthParam = req.getParameter("searchMonth");
		String workSiteName = req.getParameter("workSiteName");
		String employeeNameKeyword = req.getParameter("employeeName");

		YearMonth yearMonth = parseYearMonth(monthParam);
		LocalDate startOfMonth = yearMonth.atDay(1);
		LocalDate endOfMonth = yearMonth.atEndOfMonth();

		List<DailyWorkRecord> recordList = dayWorkerSearchService.getListByMonth(companyId,
				Date.valueOf(startOfMonth), Date.valueOf(endOfMonth), workSiteName, employeeNameKeyword);

		BigDecimal payAmountTotal = BigDecimal.ZERO;
		BigDecimal netPayTotal = BigDecimal.ZERO;
		for (DailyWorkRecord record : recordList) {
			if (record.getPayAmount() != null) {
				payAmountTotal = payAmountTotal.add(record.getPayAmount());
			}
			if (record.getNetPayAmount() != null) {
				netPayTotal = netPayTotal.add(record.getNetPayAmount());
			}
		}

		req.setAttribute("recordList", recordList);
		req.setAttribute("workSiteOptions", WORK_SITE_OPTIONS);
		req.setAttribute("searchMonth", yearMonth.format(MONTH_FORMAT));
		req.setAttribute("selectedWorkSiteName", workSiteName);
		req.setAttribute("employeeNameKeyword", employeeNameKeyword);
		req.setAttribute("payAmountTotal", String.format("%,d", payAmountTotal.longValue()));
		req.setAttribute("netPayTotal", String.format("%,d", netPayTotal.longValue()));

		return "/WEB-INF/pages/diligence/daily-work-search.jsp";
	}

	private YearMonth parseYearMonth(String monthParam) {
		if (monthParam != null && !monthParam.isBlank()) {
			try {
				return YearMonth.parse(monthParam, MONTH_FORMAT);
			} catch (DateTimeParseException e) {
				// 잘못된 형식이면 이번 달로 대체
			}
		}
		return YearMonth.now();
	}
}
