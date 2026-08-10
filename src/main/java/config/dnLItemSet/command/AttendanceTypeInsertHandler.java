package config.dnLItemSet.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.AttendanceType;
import config.dnLItemSet.service.AttendanceTypeInsertService;

public class AttendanceTypeInsertHandler implements CommandHandler {

	private AttendanceTypeInsertService insertService = new AttendanceTypeInsertService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
			return null;
		}

		String attendanceName = req.getParameter("attendanceName");
		if (attendanceName == null || attendanceName.trim().isEmpty()) {
			res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
			return null;
		}

		AttendanceType item = new AttendanceType();
		item.setCompanyId(1001);
		item.setAttendanceName(attendanceName.trim());
		item.setAttendanceCode(attendanceName.trim()); // 화면에 별도 코드 입력칸이 없어서 이름과 동일하게 채움
		item.setUnitCode(req.getParameter("unitCode"));
		item.setAttendanceGroupCode(req.getParameter("attendanceGroupCode"));
		item.setLeaveTypeId(parseIntOrNull(req.getParameter("leaveTypeId")));
		item.setUseYn(req.getParameter("useYn"));

		insertService.insert(item);

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
