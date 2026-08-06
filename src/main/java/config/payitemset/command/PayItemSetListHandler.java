package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.payitemset.service.PayItemSetListService;

public class PayItemSetListHandler implements CommandHandler {

	private PayItemSetListService listService = new PayItemSetListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;

		req.setAttribute("payItemList", listService.getList(companyId));
		req.setAttribute("selectedPayItem", null);

		return "/WEB-INF/pages/environment/pay-item-settings.jsp";
	}

}
