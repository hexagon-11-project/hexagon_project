package config.dnLItemSet.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeDeleteService;

public class AttendanceTypeDeleteHandler implements CommandHandler {

	private AttendanceTypeDeleteService deleteService = new AttendanceTypeDeleteService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
			return null;
		}

		String attendanceTypeIdParam = req.getParameter("attendanceTypeId");
		if (attendanceTypeIdParam == null || attendanceTypeIdParam.isBlank()) {
			res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
			return null;
		}

		deleteService.delete(Integer.parseInt(attendanceTypeIdParam));

		res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
		return null;
	}
}
