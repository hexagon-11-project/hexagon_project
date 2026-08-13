package config.dnLItemSet.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.AttendanceType;
import config.dnLItemSet.service.AttendanceTypeUpdateService;

public class AttendanceTypeUpdateHandler implements CommandHandler {

	private AttendanceTypeUpdateService updateService = new AttendanceTypeUpdateService();

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

		AttendanceType item = new AttendanceType();
		item.setAttendanceTypeId(Integer.parseInt(attendanceTypeIdParam));
		item.setCompanyId(1001);
		item.setAttendanceName(req.getParameter("attendanceName"));
		item.setUnitCode(req.getParameter("unitCode"));
		item.setAttendanceGroupCode(req.getParameter("attendanceGroupCode"));
		item.setLeaveTypeId(parseIntOrNull(req.getParameter("leaveTypeId")));
		item.setUseYn(req.getParameter("useYn"));

		updateService.update(item);

		res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
		return null;
	}

	private Integer parseIntOrNull(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		return Integer.parseInt(value);
	}
}
