package config.dnLItemSet.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.LeaveType;
import config.dnLItemSet.service.LeaveTypeUpdateService;

public class LeaveTypeUpdateHandler implements CommandHandler {

	private LeaveTypeUpdateService updateService = new LeaveTypeUpdateService();

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

		LeaveType item = new LeaveType();
		item.setLeaveTypeId(Integer.parseInt(leaveTypeIdParam));
		item.setCompanyId(1001);
		item.setLeaveName(req.getParameter("leaveName"));
		item.setEffectiveStartDate(parseDateOrNull(req.getParameter("startDate")));
		item.setEffectiveEndDate(parseDateOrNull(req.getParameter("endDate")));
		item.setUseYn(req.getParameter("useYn"));

		updateService.update(item);

		res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
		return null;
	}

	private java.sql.Date parseDateOrNull(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		return java.sql.Date.valueOf(value);
	}
}
