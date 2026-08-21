package diligence.dailyworkrecord.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import diligence.dailyworkrecord.service.DailyWorkRecordService;

public class DayWorkerMntDeleteHandler implements CommandHandler {

	private DailyWorkRecordService dailyWorkRecordService = new DailyWorkRecordService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Diligence/dayWorkerMnt.do");
			return null;
		}

		String dailyWorkRecordIdParam = req.getParameter("dailyWorkRecordId");
		String employeeIdParam = req.getParameter("employeeId");

		if (dailyWorkRecordIdParam != null && !dailyWorkRecordIdParam.isBlank()) {
			dailyWorkRecordService.delete(Integer.parseInt(dailyWorkRecordIdParam));
		}

		res.sendRedirect(req.getContextPath() + "/Diligence/dayWorkerMntSelect.do?employeeId=" + employeeIdParam);
		return null;
	}
}
