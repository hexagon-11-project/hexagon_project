package config.dnLItemSet.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.DeductionItemSetListService;

public class DeductionItemSetListHandler implements CommandHandler {

	private DeductionItemSetListService listService = new DeductionItemSetListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;

		req.setAttribute("deductionItemList", listService.getList(companyId));
		req.setAttribute("selectedDeductionItem", null);

		return "/WEB-INF/pages/config/payItemSet.jsp";
	}

}
