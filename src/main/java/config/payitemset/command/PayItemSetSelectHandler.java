package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeListService;
import config.payitemset.service.DeductionItemSetListService;
import config.payitemset.service.PayItemSetListService;
import config.payitemset.service.PayItemSetSelectService;

public class PayItemSetSelectHandler implements CommandHandler {

	private PayItemSetListService listService = new PayItemSetListService();
	private PayItemSetSelectService selectService = new PayItemSetSelectService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();
	private DeductionItemSetListService deductionItemSetListService = new DeductionItemSetListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;
		int payItemId = Integer.parseInt(req.getParameter("payItemId"));

		req.setAttribute("payItemList", listService.getList(companyId));
		req.setAttribute("attendanceTypeList", attendanceTypeListService.getList(companyId));
		req.setAttribute("deductionItemList", deductionItemSetListService.getList(companyId));
		req.setAttribute("selectedPayItem", selectService.getById(payItemId));
		req.setAttribute("selectedDeductionItem", null);

		return "/WEB-INF/pages/config/payItemSet.jsp";
	}

}
