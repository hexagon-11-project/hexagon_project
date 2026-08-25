package payment.paymenttransfer.command;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.model.PaymentTransfer;
import payment.paymenttransfer.service.PaymenttransferService;

/**
 * 급여이체 신청 화면 컨트롤러.
 * 給与振込申請画面のコントローラー。
 *
 */
public class PaymenttransferHandler implements CommandHandler {

	// 신청 화면 JSP 경로
	// 申請画面のJSPパス。
	private static final String FORM_VIEW = "/WEB-INF/pages/payroll/paymenttransfer.jsp";

	private PaymenttransferService transferService = new PaymenttransferService();

	/**
	 * 조회 또는 이체 신청을 처리한 뒤 화면을 반환한다.
	 * 照会または振込申請を処理したあと画面を返す。
	 *
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String payYear = req.getParameter("payYear");
		String payMonth = req.getParameter("payMonth");
		String paySeqVal = req.getParameter("paySequence");
		String action = req.getParameter("action");

		// 신청 후에도 목록을 다시 보여주기 위해 searched=true 유지
		// 申請後も一覧を再表示するためsearched=trueを維持する。
		boolean searched = "Y".equals(req.getParameter("search")) || "apply".equals(action);

		if (payYear == null || payYear.trim().isEmpty()) {
			payYear = "2026";
		}
		if (payMonth == null || payMonth.trim().isEmpty()) {
			payMonth = "08";
		}

		int paySequence = 1;
		if (paySeqVal != null && !paySeqVal.trim().isEmpty()) {
			paySequence = Integer.parseInt(paySeqVal);
		}

		// ===== 급여이체 신청 버튼(POST) 처리 =====
		// ===== 給与振込申請ボタン(POST)の処理 =====
		// 체크된 체크박스만 request에 넘어온다. (체크 안 한 행은 파라미터 자체가 없음)
		// チェックされたチェックボックスだけがrequestに渡る。(チェックしていない行はパラメータ自体がない)。
		if ("apply".equals(action) && "POST".equalsIgnoreCase(req.getMethod())) {
			// name=payrollEmployeeId 인 체크박스만 배열로 받는다
			// name=payrollEmployeeIdのチェックボックスだけを配列で受け取る。
			String[] selected = req.getParameterValues("payrollEmployeeId"); // 체크된 행들의 ID 배열 / チェックされた行のID配列。
			try {
				int appliedCount = transferService.applyTransferRequest(payYear, payMonth, paySequence, selected);
				if (appliedCount > 0) {
					req.setAttribute("message", appliedCount + "명 선택 기준으로 급여이체 신청이 저장되었습니다.");
				} else {
					req.setAttribute("errorMessage", "이체 신청할 사원을 선택해 주세요.");
				}
			} catch (IllegalArgumentException e) {
				req.setAttribute("errorMessage", e.getMessage());
			}
		}

		List<PaymentTransfer> transferList = Collections.emptyList();
		if (searched) {
			transferList = transferService.getTransferList(payYear, payMonth, paySequence);
		}

		req.setAttribute("payYear", payYear);
		req.setAttribute("payMonth", payMonth);
		req.setAttribute("paySequence", paySequence);
		req.setAttribute("searched", searched);
		req.setAttribute("transferList", transferList);
		req.setAttribute("targetCount", transferList.size());
		req.setAttribute("totalAmount", transferService.sumNetPayAmount(transferList));

		return FORM_VIEW;
	}
}
