package config.dnLItemSet.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.LeaveType;
import config.dnLItemSet.service.LeaveTypeInsertService;

public class LeaveTypeInsertHandler implements CommandHandler {

	private LeaveTypeInsertService insertService = new LeaveTypeInsertService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
			return null;
		}

		String leaveName = req.getParameter("leaveName");
		if (leaveName == null || leaveName.trim().isEmpty()) {
			res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
			return null;
		}

		LeaveType item = new LeaveType();
		item.setCompanyId(1001);
		item.setLeaveName(leaveName.trim());
		item.setLeaveCode(leaveName.trim()); // 화면에 별도 코드 입력칸이 없어서 이름과 동일하게 채움
		item.setEffectiveStartDate(parseDateOrNull(req.getParameter("startDate")));
		item.setEffectiveEndDate(parseDateOrNull(req.getParameter("endDate")));
		item.setUseYn(req.getParameter("useYn"));
		item.setDisplayOrder(1);

		insertService.insert(item);

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
