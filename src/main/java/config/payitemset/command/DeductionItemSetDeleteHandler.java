package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.payitemset.service.DeductionItemSetDeleteService;

public class DeductionItemSetDeleteHandler implements CommandHandler {

	private DeductionItemSetDeleteService deleteService = new DeductionItemSetDeleteService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		String id = req.getParameter("deductionItemId");

		if (id == null || id.isBlank()) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		deleteService.delete(Integer.parseInt(id));

		res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

		return null;

	}

}
