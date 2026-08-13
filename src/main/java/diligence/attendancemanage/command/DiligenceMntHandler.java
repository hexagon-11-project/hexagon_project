package diligence.attendancemanage.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeListService;
import diligence.attendancemanage.service.AttendanceRecordListService;

// 근태기록/관리 화면 진입점 (GET) - 사원이 선택 안 된 빈 화면
public class DiligenceMntHandler implements CommandHandler {

	private AttendanceRecordListService attendanceRecordListService = new AttendanceRecordListService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;

		req.setAttribute("employeeList", attendanceRecordListService.getEmployeeList(companyId));
		req.setAttribute("attendanceTypeList", attendanceTypeListService.getAllList(companyId));
		req.setAttribute("selectedEmployee", null);
		req.setAttribute("recordList", null);

		return "/WEB-INF/pages/diligence/attendance-manage.jsp";
	}
}
