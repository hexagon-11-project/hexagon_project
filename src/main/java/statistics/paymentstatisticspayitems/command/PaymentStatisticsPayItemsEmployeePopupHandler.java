package statistics.paymentstatisticspayitems.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import statistics.paymentstatisticspayitems.service.PaymentStatisticsPayItemsService;

/**
 * 사원별 급여 항목 통계의 사원 선택 팝업 핸들러.
 */
public class PaymentStatisticsPayItemsEmployeePopupHandler implements CommandHandler {

	private static final String POPUP_VIEW = "/WEB-INF/pages/statistics/paymentstatisticspayitems_employee_popup.jsp";
	private static final int DEFAULT_COMPANY_ID = 1001;

	private PaymentStatisticsPayItemsService paymentStatisticsPayItemsService = new PaymentStatisticsPayItemsService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setAttribute("employeeList", paymentStatisticsPayItemsService.getEmployeeList(DEFAULT_COMPANY_ID));
		return POPUP_VIEW;
	}
}
