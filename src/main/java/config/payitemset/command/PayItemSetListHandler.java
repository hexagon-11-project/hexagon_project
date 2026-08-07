package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeListService;
import config.payitemset.service.PayItemSetListService;

public class PayItemSetListHandler implements CommandHandler {

	private PayItemSetListService listService = new PayItemSetListService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;

		req.setAttribute("payItemList", listService.getList(companyId));
		req.setAttribute("attendanceTypeList", attendanceTypeListService.getList(companyId));
		req.setAttribute("selectedPayItem", null);

		return "/WEB-INF/pages/config/payItemSet.jsp";
	}

}
