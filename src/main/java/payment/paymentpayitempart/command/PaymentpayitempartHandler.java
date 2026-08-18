package payment.paymentpayitempart.command;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.model.PaymentItemLedger;
import payment.paymentpayitempart.service.PaymentpayitempartService;

/**
 * 항목별 대장 화면 핸들러.
 *
 * [조회] GET + search=Y
 *   - 조회기간(startYearMonth ~ endYearMonth)과 지급/공제 항목으로 사원별 내역·합계 조회
 */
public class PaymentpayitempartHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/pages/payment/paymentitemledger.jsp";
	private static final int DEFAULT_COMPANY_ID = 1001;
	private static final int MAX_MONTHS = 12;

	private PaymentpayitempartService paymentpayitempartService = new PaymentpayitempartService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		int companyId = DEFAULT_COMPANY_ID;
		boolean searched = "Y".equals(req.getParameter("search"));

		YearMonth current = YearMonth.now();
		YearMonth startYearMonth = parseYearMonth(req.getParameter("startYearMonth"),
				YearMonth.of(current.getYear(), 1));
		YearMonth endYearMonth = parseYearMonth(req.getParameter("endYearMonth"), current);

		List<PaymentItemLedger> itemList = paymentpayitempartService.getItemList(companyId);
		String payItemKey = parseItemSelectValue(req.getParameter("payItemKey"));
		boolean keepSelection = searched;
		String errorMessage = null;

		List<PaymentItemLedger> employeeList = Collections.emptyList();
		if (searched && isPeriodOver12Months(startYearMonth, endYearMonth)) {
			errorMessage = "조회기간은 12개월을 초과할 수 없습니다.";
			searched = false;
		} else if (searched) {
			employeeList = paymentpayitempartService.getEmployeeItemLedger(companyId,
					startYearMonth.getYear(), startYearMonth.getMonthValue(),
					endYearMonth.getYear(), endYearMonth.getMonthValue(),
					payItemKey);
		}

		req.setAttribute("startYearMonth", startYearMonth.toString());
		req.setAttribute("endYearMonth", endYearMonth.toString());
		req.setAttribute("payItemKey", keepSelection ? payItemKey : "");
		req.setAttribute("itemList", itemList);
		req.setAttribute("selectedItemName", searched ? findItemName(itemList, payItemKey) : "");
		req.setAttribute("searched", searched);
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("monthColumns", toMonthColumns(startYearMonth, endYearMonth));
		req.setAttribute("employeeList", employeeList);
		req.setAttribute("targetCount", employeeList.size());
		req.setAttribute("totalAmount", paymentpayitempartService.sumTotalAmount(employeeList));

		return FORM_VIEW;
	}

	/**
	 * type=month 값(YYYY-MM) 파싱.
	 * 값이 없거나 잘못되면 기본 연월을 사용한다.
	 */
	private YearMonth parseYearMonth(String yearMonthParam, YearMonth defaultValue) {
		if (yearMonthParam == null || yearMonthParam.trim().isEmpty()) {
			return defaultValue;
		}
		try {
			YearMonth yearMonth = YearMonth.parse(yearMonthParam.trim());
			int currentYear = LocalDate.now().getYear();
			if (yearMonth.getYear() < 1900 || yearMonth.getYear() > currentYear + 1) {
				return defaultValue;
			}
			return yearMonth;
		} catch (DateTimeParseException e) {
			return defaultValue;
		}
	}

	/** 시작~종료 연월이 12개월을 넘으면 true. 같은 달부터 12개월(예: 1월~12월)은 허용한다. */
	private boolean isPeriodOver12Months(YearMonth startYearMonth, YearMonth endYearMonth) {
		return monthCount(startYearMonth, endYearMonth) > MAX_MONTHS;
	}

	/** 조회기간 시작~종료의 연월 목록. 경고 기준과 같이 최대 12개월만 표시한다. */
	private List<String> toMonthColumns(YearMonth startYearMonth, YearMonth endYearMonth) {
		List<String> months = new ArrayList<>();
		if (startYearMonth == null || endYearMonth == null || startYearMonth.isAfter(endYearMonth)) {
			return months;
		}
		YearMonth cursor = startYearMonth;
		YearMonth cappedEnd = startYearMonth.plusMonths(MAX_MONTHS - 1);
		if (endYearMonth.isBefore(cappedEnd)) {
			cappedEnd = endYearMonth;
		}
		while (!cursor.isAfter(cappedEnd) && months.size() < MAX_MONTHS) {
			months.add(String.format("%04d.%02d", cursor.getYear(), cursor.getMonthValue()));
			cursor = cursor.plusMonths(1);
		}
		return months;
	}

	private int monthCount(YearMonth startYearMonth, YearMonth endYearMonth) {
		return (endYearMonth.getYear() - startYearMonth.getYear()) * 12
				+ (endYearMonth.getMonthValue() - startYearMonth.getMonthValue()) + 1;
	}

	/** 항목을 고르지 않았으면 기본값(급여항목 선택)을 유지한다. */
	private String parseItemSelectValue(String itemSelectValue) {
		if (itemSelectValue == null || itemSelectValue.trim().isEmpty()) {
			return "";
		}
		return itemSelectValue.trim();
	}

	private String findItemName(List<PaymentItemLedger> itemList, String itemSelectValue) {
		if (itemList == null || itemSelectValue == null) {
			return "";
		}
		for (PaymentItemLedger item : itemList) {
			if (itemSelectValue.equals(item.getSelectValue())) {
				return item.getItemName();
			}
		}
		return "";
	}
}
