package config.dnLItemSet.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.LeaveTypeDeleteService;

public class LeaveTypeDeleteHandler implements CommandHandler {

	private LeaveTypeDeleteService deleteService = new LeaveTypeDeleteService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
			return null;
		}

		String leaveTypeIdParam = req.getParameter("leaveTypeId");
		if (leaveTypeIdParam == null || leaveTypeIdParam.isBlank()) {
			res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
			return null;
		}

		deleteService.delete(Integer.parseInt(leaveTypeIdParam));

		res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
		return null;
	}
}
