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
 * 항목별 대장 화면 요청을 처리한다.
 * 項目別台帳画面のリクエストを処理する。
 */
public class PaymentpayitempartHandler implements CommandHandler {

	// 항목별 대장 JSP 경로
	// 項目別台帳のJSPパス。
	private static final String FORM_VIEW = "/WEB-INF/pages/payment/paymentitemledger.jsp";
	// 조회 회사아이디
	// 照会する会社ID。
	private static final int DEFAULT_COMPANY_ID = 1001;
	// 조회기간 최대 개월 수
	// 照会期間の最大月数。
	private static final int MAX_MONTHS = 12;

	private PaymentpayitempartService paymentpayitempartService = new PaymentpayitempartService();

	/**
	 * 항목별 대장 화면을 열고 조건으로 조회한다.
	 * 項目別台帳画面を開き、条件で照会する。
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		int companyId = DEFAULT_COMPANY_ID;
		// hidden search=Y가 있을 때만 조회한다
		// hidden search=Yがあるときだけ照会する。
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
			// 양끝 포함 13개월부터 막는다 (1월~다음 1월 = 13)
			// 両端を含め13ヶ月から防ぐ (1月〜翌年1月 = 13)。
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
	 * type=month 값(YYYY-MM)을 YearMonth로 바꾼다.
	 * type=month値(YYYY-MM)をYearMonthに変換する。
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

	/**
	 * 시작~종료 연월이 12개월을 넘는지 본다.
	 * 開始〜終了の年月が12ヶ月を超えるかを見る。
	 */
	private boolean isPeriodOver12Months(YearMonth startYearMonth, YearMonth endYearMonth) {
		return monthCount(startYearMonth, endYearMonth) > MAX_MONTHS;
	}

	/**
	 * 조회기간 시작~종료의 연월 목록을 만든다.
	 * 照会期間の開始〜終了の年月一覧を作る。
	 */
	private List<String> toMonthColumns(YearMonth startYearMonth, YearMonth endYearMonth) {
		List<String> months = new ArrayList<>();
		if (startYearMonth == null || endYearMonth == null || startYearMonth.isAfter(endYearMonth)) {
			return months;
		}
		YearMonth cursor = startYearMonth;
		// 시작월 포함 최대 12칸: plusMonths(11)이 마지막 칸이다
		// 開始月を含め最大12欄: plusMonths(11)が最後の欄である。
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

	/**
	 * 시작~종료의 포함 개월 수를 센다.
	 * 開始〜終了の含まれる月数を数える。
	 */
	private int monthCount(YearMonth startYearMonth, YearMonth endYearMonth) {
		return (endYearMonth.getYear() - startYearMonth.getYear()) * 12
				+ (endYearMonth.getMonthValue() - startYearMonth.getMonthValue()) + 1;
	}

	/**
	 * 항목 셀렉트 값을 정리한다.
	 * 項目セレクト値を整理する。
	 */
	private String parseItemSelectValue(String itemSelectValue) {
		if (itemSelectValue == null || itemSelectValue.trim().isEmpty()) {
			return "";
		}
		return itemSelectValue.trim();
	}

	/**
	 * 셀렉트 value에 맞는 항목명을 찾는다.
	 * セレクトvalueに合う項目名を探す。
	 */
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
