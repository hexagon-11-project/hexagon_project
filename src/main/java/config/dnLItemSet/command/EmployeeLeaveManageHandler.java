package config.dnLItemSet.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeListService;
import config.dnLItemSet.service.EmployeeLeaveManageService;
import config.dnLItemSet.service.LeaveTypeListService;
import config.dnLItemSet.service.LeaveTypeSelectService;

// 휴가항목 목록의 [관리] 버튼 - 사원별 휴가일수 모달을 연 상태로 같은 화면을 다시 보여줌
public class EmployeeLeaveManageHandler implements CommandHandler {

	private LeaveTypeListService leaveTypeListService = new LeaveTypeListService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();
	private LeaveTypeSelectService leaveTypeSelectService = new LeaveTypeSelectService();
	private EmployeeLeaveManageService employeeLeaveManageService = new EmployeeLeaveManageService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;
		int leaveTypeId = Integer.parseInt(req.getParameter("leaveTypeId"));

		req.setAttribute("leaveTypeList", leaveTypeListService.getList(companyId));
		req.setAttribute("attendanceTypeList", attendanceTypeListService.getAllList(companyId));
		req.setAttribute("selectedLeaveType", null);
		req.setAttribute("selectedAttendanceType", null);

		// 모달 표시용
		req.setAttribute("manageLeaveType", leaveTypeSelectService.getById(leaveTypeId));
		req.setAttribute("employeeLeaveList", employeeLeaveManageService.getList(leaveTypeId, companyId));

		return "/WEB-INF/pages/config/leave-settings.jsp";
	}
}
