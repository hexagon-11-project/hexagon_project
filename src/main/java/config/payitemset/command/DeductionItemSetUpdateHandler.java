package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.DeductionItem;
import config.payitemset.service.DeductionItemSetUpdateService;

/**
 * 선택한 공제항목을 수정한다.
 * 選択した控除項目を更新する。
 */
public class DeductionItemSetUpdateHandler implements CommandHandler {

	private DeductionItemSetUpdateService updateService = new DeductionItemSetUpdateService();

	/**
	 * 공제항목 폼을 검증한 뒤 기존 행을 갱신한다.
	 * 控除項目フォームを検証したあと既存行を更新する。
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// POST 가 아니면 수정하지 않는다
		// POSTでなければ更新しない。
		if (!"POST".equalsIgnoreCase(req.getMethod())) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		String id = req.getParameter("deductionItemId");

		// 수정 대상 PK 가 있어야 한다
		// 更新対象PKがなければならない。
		if (id == null || id.isBlank()) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		DeductionItem item = new DeductionItem();

		item.setDeductionItemId(Integer.parseInt(id));
		// 회사 ID 는 학습용 고정값이다.
		// 会社IDは学習用の固定値である。
		item.setCompanyId(1001);
		item.setDeductionItemName(req.getParameter("deductionItemName"));
		item.setCalculationMethod(emptyToNull(req.getParameter("deductionCalculationMethod")));
		// 계산방법이 비면 정액으로 둔다
		// 計算方法が空なら定額にする。
		if (item.getCalculationMethod() == null) {
			item.setCalculationMethod("FIXED");
		}
		item.setTruncationUnit(parseIntOrDefault(req.getParameter("deductionTruncationUnit"), 0));
		item.setRemark(emptyToNull(req.getParameter("remark")));
		item.setUseYn(req.getParameter("deductionUseYn"));
		item.setModId("SYSTEM");

		updateService.update(item);

		// 지급 목록 URI 로 redirect 한다.
		// 支給一覧URIへredirectする。
		res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

		return null;

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

		return Integer.parseInt(value.trim());

	}

	/**
	 * 빈 문자열을 null 로 바꾼다.
	 * 空文字をnullに変える。
	 */
	private String emptyToNull(String value) {

		// "" 와 공백만 있는 값을 null 로 바꾼다.
		// ""と空白だけの値をnullに変える。
		return (value == null || value.trim().isEmpty()) ? null : value.trim();

	}

}
