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
		String empName = trimToNull(req.getParameter("empName"));
		String department = trimToNull(req.getParameter("department"));
		String status = trimToNull(req.getParameter("status"));

		req.setAttribute("employeeList",
				paymentStatisticsPayItemsService.getEmployeeList(DEFAULT_COMPANY_ID, empName, department, status));
		req.setAttribute("deptList", paymentStatisticsPayItemsService.getDepartmentList(DEFAULT_COMPANY_ID));
		req.setAttribute("empName", empName == null ? "" : empName);
		req.setAttribute("selectedDept", department == null ? "" : department);
		req.setAttribute("selectedStatus", status == null ? "" : status);
		return POPUP_VIEW;
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
