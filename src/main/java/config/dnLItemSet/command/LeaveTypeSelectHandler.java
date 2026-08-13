package config.dnLItemSet.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeListService;
import config.dnLItemSet.service.LeaveTypeListService;
import config.dnLItemSet.service.LeaveTypeSelectService;

// 목록에서 휴가항목 한 줄을 클릭했을 때 - 오른쪽 편집폼에 그 데이터를 채워서 다시 보여줌
public class LeaveTypeSelectHandler implements CommandHandler {

	private LeaveTypeListService leaveTypeListService = new LeaveTypeListService();
	private LeaveTypeSelectService leaveTypeSelectService = new LeaveTypeSelectService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;
		int leaveTypeId = Integer.parseInt(req.getParameter("leaveTypeId"));

		req.setAttribute("leaveTypeList", leaveTypeListService.getList(companyId));
		req.setAttribute("attendanceTypeList", attendanceTypeListService.getAllList(companyId));
		req.setAttribute("selectedLeaveType", leaveTypeSelectService.getById(leaveTypeId));
		req.setAttribute("selectedAttendanceType", null);

		return "/WEB-INF/pages/config/leave-settings.jsp";
	}
}
