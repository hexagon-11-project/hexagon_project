package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.payitemset.service.PayItemSetListService;
import config.payitemset.service.PayItemSetSelectService;

public class PayItemSetSelectHandler implements CommandHandler {

	private PayItemSetListService listService = new PayItemSetListService();
	private PayItemSetSelectService selectService = new PayItemSetSelectService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;
		int payItemId = Integer.parseInt(req.getParameter("payItemId"));

		req.setAttribute("payItemList", listService.getList(companyId));
		req.setAttribute("selectedPayItem", selectService.getById(payItemId));

		return "/WEB-INF/pages/environment/pay-item-settings.jsp";

	}

}
