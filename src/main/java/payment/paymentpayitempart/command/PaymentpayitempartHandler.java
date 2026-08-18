package payment.paymentpayitempart.command;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
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
		String itemSelectValue = parseItemSelectValue(req.getParameter("itemSelectValue"), itemList);

		List<PaymentItemLedger> employeeList = Collections.emptyList();
		if (searched) {
			employeeList = paymentpayitempartService.getEmployeeItemLedger(companyId,
					startYearMonth.getYear(), startYearMonth.getMonthValue(),
					endYearMonth.getYear(), endYearMonth.getMonthValue(),
					itemSelectValue);
		}

		req.setAttribute("startYearMonth", startYearMonth.toString());
		req.setAttribute("endYearMonth", endYearMonth.toString());
		req.setAttribute("itemSelectValue", itemSelectValue);
		req.setAttribute("itemList", itemList);
		req.setAttribute("selectedItemName", findItemName(itemList, itemSelectValue));
		req.setAttribute("searched", searched);
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

	/** 선택한 항목이 목록에 없으면 첫 항목 value를 사용한다. */
	private String parseItemSelectValue(String itemSelectValue, List<PaymentItemLedger> itemList) {
		if (itemSelectValue != null && !itemSelectValue.trim().isEmpty()) {
			return itemSelectValue.trim();
		}
		if (itemList == null || itemList.isEmpty()) {
			return null;
		}
		return itemList.get(0).getSelectValue();
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
