package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;

public class PayItemSetClearHandler implements CommandHandler {

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

		return null;

	}

}
