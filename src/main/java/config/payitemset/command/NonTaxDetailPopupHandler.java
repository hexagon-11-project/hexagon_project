package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.payitemset.service.NonTaxDetailListService;

public class NonTaxDetailPopupHandler implements CommandHandler {

	private NonTaxDetailListService listService = new NonTaxDetailListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;

		req.setAttribute("nonTaxDetailList", listService.getList(companyId));

		return "/WEB-INF/pages/config/nonTaxDetailPopup.jsp";
	}

}
