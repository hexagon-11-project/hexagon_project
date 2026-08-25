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
 * 4대보험 대장 화면 요청을 처리한다.
 * 社会保険(4大保険)台帳画面のリクエストを処理する。
 */
public class FourinsureListHandler implements CommandHandler {

	// 4대보험 대장 JSP 경로
	// 社会保険(4大保険)台帳のJSPパス。
	private static final String FORM_VIEW = "/WEB-INF/pages/payment/fourinsurelist.jsp";

	private FourinsureListService fourinsureListService = new FourinsureListService();

	/**
	 * 4대보험 대장 화면을 열고 조건으로 조회한다.
	 * 社会保険(4大保険)台帳画面を開き、条件で照会する。
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 조회 버튼으로 들어왔는지 판별
		// 照会ボタンで入ってきたかを判定する。
		boolean searched = "Y".equals(req.getParameter("search"));

		// 화면 type=month(YYYY-MM)를 우선하고, 없으면 연·월을 이어 붙인다
		// 画面のtype=month(YYYY-MM)を優先し、なければ年・月をつなげる。
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
			// 연·월·차수로 PAYROLL 헤더와 사원 공제 피벗을 읽는다
			// 年・月・次数でPAYROLLヘッダーと社員控除のピボットを読む。
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
		// 항목별 전 사원 합(사업주/근로자 칸에 넣을 조회 금액)
		// 項目別の全社員合計(事業主/労働者欄に入れる照会金額)。
		req.setAttribute("columnTotals", fourinsureListService.sumColumnTotals(employeeList));
		req.setAttribute("targetCount", employeeList.size());
		// 사원별 grandTotal(사업주+근로자)을 더한 화면 하단 전체 합
		// 社員別grandTotal(事業主+労働者)を足した画面下部の全体合計。
		req.setAttribute("totalAmount", fourinsureListService.sumInsuranceAmount(employeeList));
		req.setAttribute("errorMessage", errorMessage);

		return FORM_VIEW;
	}

	/**
	 * 귀속연월 문자열을 YearMonth로 바꾼다.
	 * 帰属年月の文字列をYearMonthに変換する。
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

	/**
	 * 급여차수 문자열을 정수로 바꾼다.
	 * 給与次数の文字列を整数に変換する。
	 */
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
