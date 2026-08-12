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
 *
 * [조회] GET + search=Y
 *   - 신청기간(startDate ~ endDate) 안의 REQUEST_DATE 이체신청 결과 조회
 *   - 리스트: 출금은행, 출금계좌, 입금은행, 입금계좌, 예금주, 이체금액
 */
public class PaymenttransferlistHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/pages/payroll/paymenttransferlist.jsp";

	private PaymenttransferlistService transferListService = new PaymenttransferlistService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String startDate = req.getParameter("startDate");
		String endDate = req.getParameter("endDate");
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
