package diligence.attendancemanage.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import diligence.attendancemanage.service.AttendanceRecordManageService;

public class DiligenceMntDeleteHandler implements CommandHandler {

	private AttendanceRecordManageService attendanceRecordManageService = new AttendanceRecordManageService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMnt.do");
			return null;
		}

		String attendanceIdParam = req.getParameter("attendanceId");
		String employeeIdParam = req.getParameter("employeeId");

		if (attendanceIdParam != null && !attendanceIdParam.isBlank()) {
			attendanceRecordManageService.delete(Integer.parseInt(attendanceIdParam));
		}

		res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMntSelect.do?employeeId=" + employeeIdParam);
		return null;
	}
}
