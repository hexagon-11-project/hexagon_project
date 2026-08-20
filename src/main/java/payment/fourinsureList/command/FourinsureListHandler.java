package payment.fourinsureList.command;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.fourinsureList.service.FourinsureListService;
import payment.model.PaymentInsuranceLedger;

/**
 * 4대보험 대장 화면 핸들러.
 *
 * [조회] GET + search=Y
 *   - 귀속연월·급여차수로 정산기간·급여지급일과 사원별 4대보험 공제액 조회
 */
public class FourinsureListHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/pages/payment/fourinsurelist.jsp";

	private FourinsureListService fourinsureListService = new FourinsureListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		boolean searched = "Y".equals(req.getParameter("search"));

		YearMonth selected = parseYearMonth(
				req.getParameter("payYearMonth"),
				req.getParameter("payYear"),
				req.getParameter("payMonth"),
				YearMonth.now());
		String payYear = String.valueOf(selected.getYear());
		String payMonth = String.format("%02d", selected.getMonthValue());
		int paySequence = parsePaySequence(req.getParameter("paySequence"));

		PaymentInsuranceLedger ledger = null;
		List<PaymentInsuranceLedger> employeeList = Collections.emptyList();
		String errorMessage = null;

		if (searched) {
			ledger = fourinsureListService.getInsuranceLedger(payYear, payMonth, paySequence);
			if (ledger == null) {
				errorMessage = "해당 귀속연월/차수의 급여작업이 없습니다.";
			} else {
				employeeList = ledger.getEmployees();
			}
		}

		req.setAttribute("payYear", payYear);
		req.setAttribute("payMonth", payMonth);
		req.setAttribute("payYearMonth", payYear + "-" + payMonth);
		req.setAttribute("paySequence", paySequence);
		req.setAttribute("searched", searched);
		req.setAttribute("ledger", ledger);
		req.setAttribute("employeeList", employeeList);
		req.setAttribute("targetCount", employeeList.size());
		req.setAttribute("totalAmount", fourinsureListService.sumInsuranceAmount(employeeList));
		req.setAttribute("errorMessage", errorMessage);

		return FORM_VIEW;
	}

	/**
	 * 귀속연월 파싱.
	 * type=month 값(YYYY-MM)을 우선하고, 없으면 귀속연·월을 사용한다.
	 * 값이 없거나 잘못되면 기본 연월을 사용한다.
	 */
	private YearMonth parseYearMonth(String payYearMonth, String payYear, String payMonth, YearMonth defaultValue) {
		if (payYearMonth != null && !payYearMonth.trim().isEmpty()) {
			try {
				return YearMonth.parse(payYearMonth.trim());
			} catch (DateTimeParseException e) {
				return defaultValue;
			}
		}
		if (payYear != null && !payYear.trim().isEmpty() && payMonth != null && !payMonth.trim().isEmpty()) {
			try {
				return YearMonth.of(Integer.parseInt(payYear.trim()), Integer.parseInt(payMonth.trim()));
			} catch (RuntimeException e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	private int parsePaySequence(String paySeqVal) {
		if (paySeqVal == null || paySeqVal.trim().isEmpty()) {
			return 1;
		}
		try {
			return Integer.parseInt(paySeqVal.trim().replace("차", ""));
		} catch (NumberFormatException e) {
			return 1;
		}
	}
}
