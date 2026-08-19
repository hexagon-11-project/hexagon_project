package statistics.paymentstatisticspayitems.command;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import statistics.model.EmployeeSalaryStatistics;
import statistics.model.SalaryItemStatistics;
import statistics.paymentstatisticspayitems.service.EmployeeSalaryNotFoundException;
import statistics.paymentstatisticspayitems.service.PaymentStatisticsPayItemsService;

/**
 * 사원별 급여 항목 통계 화면 핸들러.
 *
 * 연도, 월, 사원이름으로 해당 월 지급내역·공제항목을 조회한다.
 */
public class PaymentStatisticsPayItemsHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/pages/statistics/paymentstatisticspayitems.jsp";
	private static final int DEFAULT_COMPANY_ID = 1001;

	private PaymentStatisticsPayItemsService paymentStatisticsPayItemsService = new PaymentStatisticsPayItemsService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		int companyId = DEFAULT_COMPANY_ID;
		YearMonth yearMonth = parseYearMonth(req);
		int year = yearMonth.getYear();
		int month = yearMonth.getMonthValue();
		String employeeName = parseEmployeeName(req.getParameter("employeeName"));

		EmployeeSalaryStatistics employeeSalaryStatistics = null;
		if (employeeName != null) {
			try {
				employeeSalaryStatistics = paymentStatisticsPayItemsService
						.getEmployeeSalaryStatistics(companyId, year, month, employeeName);
			} catch (EmployeeSalaryNotFoundException e) {
				req.setAttribute("errorMessage", e.getMessage());
			}
		}

		List<SalaryItemStatistics> payItemColumns = paymentStatisticsPayItemsService.getPayItemColumns(companyId);
		List<SalaryItemStatistics> deductionItemColumns =
				paymentStatisticsPayItemsService.getDeductionItemColumns(companyId);
		if (employeeSalaryStatistics != null) {
			paymentStatisticsPayItemsService.fillItemAmounts(payItemColumns,
					employeeSalaryStatistics.getPayItems(), employeeSalaryStatistics.getTotalPayAmount());
			paymentStatisticsPayItemsService.fillItemAmounts(deductionItemColumns,
					employeeSalaryStatistics.getDeductionItems(),
					employeeSalaryStatistics.getTotalDeductionAmount());
		}

		req.setAttribute("year", year);
		req.setAttribute("month", month);
		req.setAttribute("yearMonth", String.format("%04d-%02d", year, month));
		req.setAttribute("employeeName", employeeName);
		req.setAttribute("employeeSalaryStatistics", employeeSalaryStatistics);
		req.setAttribute("payItemColumns", payItemColumns);
		req.setAttribute("deductionItemColumns", deductionItemColumns);
		return FORM_VIEW;
	}

	/**
	 * 귀속연월 파싱.
	 * yearMonth(YYYY-MM)가 있으면 우선 사용하고, 없으면 year / month를 사용한다.
	 * 값이 없거나 잘못되면 이번 달을 사용한다.
	 */
	private YearMonth parseYearMonth(HttpServletRequest req) {
		YearMonth current = YearMonth.now();
		String yearMonthParam = req.getParameter("yearMonth");
		if (yearMonthParam != null && !yearMonthParam.trim().isEmpty()) {
			try {
				YearMonth yearMonth = YearMonth.parse(yearMonthParam.trim());
				if (isValidYearMonth(yearMonth, current)) {
					return yearMonth;
				}
			} catch (DateTimeParseException e) {
				return current;
			}
			return current;
		}

		int year = parseYear(req.getParameter("year"), current.getYear());
		int month = parseMonth(req.getParameter("month"), current.getMonthValue());
		return YearMonth.of(year, month);
	}

	/** 요청 연도가 없거나 잘못되면 기본 연도를 사용한다. */
	private int parseYear(String yearParam, int defaultYear) {
		if (yearParam == null || yearParam.trim().isEmpty()) {
			return defaultYear;
		}
		try {
			int year = Integer.parseInt(yearParam.trim());
			int currentYear = LocalDate.now().getYear();
			if (year < 1900 || year > currentYear + 1) {
				return defaultYear;
			}
			return year;
		} catch (NumberFormatException e) {
			return defaultYear;
		}
	}

	/** 요청 월이 없거나 잘못되면 기본 월을 사용한다. */
	private int parseMonth(String monthParam, int defaultMonth) {
		if (monthParam == null || monthParam.trim().isEmpty()) {
			return defaultMonth;
		}
		try {
			int month = Integer.parseInt(monthParam.trim());
			if (month < 1 || month > 12) {
				return defaultMonth;
			}
			return month;
		} catch (NumberFormatException e) {
			return defaultMonth;
		}
	}

	private boolean isValidYearMonth(YearMonth yearMonth, YearMonth current) {
		int year = yearMonth.getYear();
		return year >= 1900 && year <= current.getYear() + 1;
	}

	/** 사원이름이 비어 있으면 null. */
	private String parseEmployeeName(String employeeNameParam) {
		if (employeeNameParam == null) {
			return null;
		}
		String employeeName = employeeNameParam.trim();
		return employeeName.isEmpty() ? null : employeeName;
	}
}
