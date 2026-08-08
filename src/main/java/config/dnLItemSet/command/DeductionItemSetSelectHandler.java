package config.dnLItemSet.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.DeductionItemSetListService;
import config.dnLItemSet.service.DeductionItemSetSelectService;

public class DeductionItemSetSelectHandler implements CommandHandler {

	private DeductionItemSetListService listService = new DeductionItemSetListService();
	private DeductionItemSetSelectService selectService = new DeductionItemSetSelectService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;
		int deductionItemId = Integer.parseInt(req.getParameter("deductionItemId"));

		req.setAttribute("deductionItemList", listService.getList(companyId));
		req.setAttribute("selectedDeductionItem", selectService.getById(deductionItemId));

		return "/WEB-INF/pages/config/payItemSet.jsp";

	}

}
