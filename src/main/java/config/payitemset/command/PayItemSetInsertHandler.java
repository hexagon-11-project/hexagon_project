package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.PayItem;
import config.payitemset.service.PayItemSetInsertService;

/**
 * 지급항목을 신규 등록한다.
 * 支給項目を新規登録する。
 */
public class PayItemSetInsertHandler implements CommandHandler {

	private PayItemSetInsertService insertService = new PayItemSetInsertService();

	/**
	 * 폼 파라미터를 검증·변환한 뒤 지급항목을 저장한다.
	 * フォームパラメータを検証・変換したあと支給項目を保存する。
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// POST 가 아니면 저장하지 않는다
		// POSTでなければ保存しない。
		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");
			return null;
		}

		// 지급항목명은 필수이다
		// 支給項目名は必須である。
		String payItemName = req.getParameter("payItemName");
		if (payItemName == null || payItemName.trim().isEmpty()) {
			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");
			return null;
		}

		PayItem item = new PayItem();

		item.setPayItemName(payItemName.trim());
		item.setTaxableYn(req.getParameter("taxableYn"));
		// 빈 select 는 "" 로 오므로 null 로 바꾼다.
		// 空のselectは""で来るのでnullに変える。
		item.setCalculationMethod(emptyToNull(req.getParameter("calculationMethod")));
		item.setTruncationUnit(parseIntOrDefault(req.getParameter("truncationUnit"), 0));
		item.setAttendancePayRule(emptyToNull(req.getParameter("attendancePayRule")));
		item.setUseYn(req.getParameter("useYn"));
		item.setNonPayAmount(parseLongOrNull(req.getParameter("nonPayAmount")));
		item.setNonTaxCategory(emptyToNull(req.getParameter("nonTaxCategory")));

		// 회사 ID 는 학습용 고정값이다.
		// 会社IDは学習用の固定値である。
		item.setCompanyId(1001);
		// 계산방법이 비면 정액으로 둔다
		// 計算方法が空なら定額にする。
		if (item.getCalculationMethod() == null) {
			item.setCalculationMethod("FIXED");
		}

		// 일괄지급일 때만 일괄금액을 저장한다
		// 一括支給のときだけ一括金額を保存する。
		if ("일괄지급".equals(item.getAttendancePayRule())) {
			item.setBulkPayAmount(parseLongOrNull(req.getParameter("bulkPayAmount")));
		} else {
			item.setBulkPayAmount(null);
		}

		item.setDisplayOrder(0);
		item.setRegId("SYSTEM");
		item.setModId("SYSTEM");

		// 비과세가 아니면 비과세 관련 컬럼을 비운다
		// 非課税でなければ非課税関連カラムを空にする。
		if ("N".equalsIgnoreCase(item.getTaxableYn())) {
			item.setNonTaxId(parseIntOrNull(req.getParameter("nonTaxId")));
		} else {
			item.setNonTaxId(null);
			item.setNonPayAmount(null);
			item.setNonTaxCategory(null);
		}

		insertService.insert(item);
		// 목록으로 redirect 한다.
		// 一覧へredirectする。
		res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");
		return null;
	}

	/**
	 * 금액 문자열을 Long 으로 변환한다.
	 * 金額文字列をLongへ変換する。
	 */
	private Long parseLongOrNull(String value) {
		// 미입력이면 null 이다.
		// 未入力ならnullである。
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		// 콤마를 제거하고 파싱한다.
		// カンマを除いてパースする。
		return Long.parseLong(value.replace(",", ""));
	}

	/**
	 * 정수 파라미터를 파싱하고 공백이면 기본값을 쓴다.
	 * 整数パラメータをパースし空白なら既定値を使う。
	 */
	private int parseIntOrDefault(String value, int defaultValue) {
		// 비면 호출측 기본값을 쓴다.
		// 空なら呼び出し側の既定値を使う。
		if (value == null || value.trim().isEmpty()) {
			return defaultValue;
		}
		return Integer.parseInt(value);
	}

	/**
	 * 정수 파라미터를 Integer 로 변환한다.
	 * 整数パラメータをIntegerへ変換する。
	 */
	private Integer parseIntOrNull(String value) {
		// 팝업을 안 고르면 hidden 이 비어 null 이다.
		// ポップアップを選ばなければhiddenが空でnullである。
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		return Integer.parseInt(value.trim());
	}

	/**
	 * 빈 문자열을 null 로 바꾼다.
	 * 空文字をnullに変える。
	 */
	private String emptyToNull(String value) {
		// "" 를 null 로 바꾼다.
		// ""をnullに変える。
		return (value == null || value.trim().isEmpty()) ? null : value;
	}

}
