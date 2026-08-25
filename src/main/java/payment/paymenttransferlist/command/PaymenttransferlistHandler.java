package payment.paymenttransferlist.command;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.model.PaymentTransferRequest;
import payment.paymenttransferlist.service.PaymenttransferlistService;

/**
 * 급여이체 신청 조회 화면 컨트롤러.
 * 給与振込申請照会画面のコントローラー。
 *
 */
public class PaymenttransferlistHandler implements CommandHandler {

	// 결과 조회 화면 JSP 경로
	// 結果照会画面のJSPパス。
	private static final String FORM_VIEW = "/WEB-INF/pages/payroll/paymenttransferlist.jsp";

	private PaymenttransferlistService transferListService = new PaymenttransferlistService();

	/**
	 * 신청기간으로 이체 신청 결과를 조회한다.
	 * 申請期間で振込申請結果を照会する。
	 *
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String startDate = req.getParameter("startDate");
		String endDate = req.getParameter("endDate");
		// hidden search=Y가 있을 때만 DB를 친다
		// hidden search=YがあるときだけDBを照会する。
		boolean searched = "Y".equals(req.getParameter("search"));

		LocalDate today = LocalDate.now();
		if (startDate == null || startDate.trim().isEmpty()) {
			startDate = today.withDayOfMonth(1).toString();
		}
		if (endDate == null || endDate.trim().isEmpty()) {
			endDate = today.toString();
		}

		List<PaymentTransferRequest> transferRequestList = Collections.emptyList();
		if (searched) {
			transferRequestList = transferListService.getTransferRequestList(startDate, endDate);
		}

		req.setAttribute("startDate", startDate);
		req.setAttribute("endDate", endDate);
		req.setAttribute("searched", searched);
		req.setAttribute("transferRequestList", transferRequestList);
		req.setAttribute("targetCount", transferRequestList.size());
		req.setAttribute("totalAmount", transferListService.sumTransferAmount(transferRequestList));

		return FORM_VIEW;
	}
}
