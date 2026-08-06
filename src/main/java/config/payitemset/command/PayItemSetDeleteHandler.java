package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.payitemset.service.PayItemSetDeleteService;

public class PayItemSetDeleteHandler implements CommandHandler {

	private PayItemSetDeleteService deleteService = new PayItemSetDeleteService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		String payItemIdParam = req.getParameter("payItemId");

		if (payItemIdParam == null || payItemIdParam.isBlank()) {
			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		int payItemId = Integer.parseInt(req.getParameter("payItemId"));

		deleteService.delete(payItemId);

		res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

		return null;

	}

}
