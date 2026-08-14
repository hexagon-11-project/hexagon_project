package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeListService;
import config.payitemset.service.DeductionItemSetListService;
import config.payitemset.service.PayItemSetListService;

public class DeductionItemSetListHandler implements CommandHandler {

	private PayItemSetListService payItemListService = new PayItemSetListService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();
	private DeductionItemSetListService listService = new DeductionItemSetListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;

		req.setAttribute("payItemList", payItemListService.getList(companyId));
		req.setAttribute("attendanceTypeList", attendanceTypeListService.getList(companyId));
		req.setAttribute("deductionItemList", listService.getList(companyId));
		req.setAttribute("selectedPayItem", null);
		req.setAttribute("selectedDeductionItem", null);

		return "/WEB-INF/pages/config/payItemSet.jsp";
	}

}
