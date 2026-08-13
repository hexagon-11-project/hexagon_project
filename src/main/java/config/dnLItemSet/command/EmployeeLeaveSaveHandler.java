package config.dnLItemSet.command;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeListService;
import config.dnLItemSet.service.EmployeeLeaveManageService;
import config.dnLItemSet.service.LeaveTypeListService;
import config.dnLItemSet.service.LeaveTypeSelectService;

// [휴가일수 저장] - 체크된 사원들만 골라서 입력된 일수로 일괄 저장
public class EmployeeLeaveSaveHandler implements CommandHandler {

	private EmployeeLeaveManageService employeeLeaveManageService = new EmployeeLeaveManageService();
	private LeaveTypeListService leaveTypeListService = new LeaveTypeListService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();
	private LeaveTypeSelectService leaveTypeSelectService = new LeaveTypeSelectService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
			return null;
		}

		int companyId = 1001;
		String leaveTypeIdParam = req.getParameter("leaveTypeId");
		String[] checkedEmployeeIds = req.getParameterValues("checkedEmployeeId");

		boolean justSaved = false;

		if (leaveTypeIdParam != null && !leaveTypeIdParam.isBlank() && checkedEmployeeIds != null
				&& checkedEmployeeIds.length > 0) {

			int leaveTypeId = Integer.parseInt(leaveTypeIdParam);

			int[] employeeIds = new int[checkedEmployeeIds.length];
			BigDecimal[] grantedDaysList = new BigDecimal[checkedEmployeeIds.length];

			for (int i = 0; i < checkedEmployeeIds.length; i++) {
				int employeeId = Integer.parseInt(checkedEmployeeIds[i]);
				employeeIds[i] = employeeId;

				String daysParam = req.getParameter("grantedDays_" + employeeId);
				grantedDaysList[i] = parseDaysOrZero(daysParam);
			}

			employeeLeaveManageService.saveGrantedDays(leaveTypeId, employeeIds, grantedDaysList);
			justSaved = true;
		}

		// 저장 후 목록으로 이동하지 않고, 같은 모달을 최신 데이터로 다시 보여준다 (alert 띄우기 위해 forward 사용)
		int leaveTypeId = Integer.parseInt(leaveTypeIdParam);

		req.setAttribute("leaveTypeList", leaveTypeListService.getList(companyId));
		req.setAttribute("attendanceTypeList", attendanceTypeListService.getAllList(companyId));
		req.setAttribute("selectedLeaveType", null);
		req.setAttribute("selectedAttendanceType", null);
		req.setAttribute("manageLeaveType", leaveTypeSelectService.getById(leaveTypeId));
		req.setAttribute("employeeLeaveList", employeeLeaveManageService.getList(leaveTypeId, companyId));
		req.setAttribute("employeeLeaveJustSaved", justSaved);

		return "/WEB-INF/pages/config/leave-settings.jsp";
	}

	private BigDecimal parseDaysOrZero(String value) {
		if (value == null || value.trim().isEmpty()) {
			return BigDecimal.ZERO;
		}
		try {
			return new BigDecimal(value.trim());
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO;
		}
	}
}
