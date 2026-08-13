package config.dnLItemSet.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.EmployeeLeaveManageService;

// [휴가일수 삭제] - 체크된 사원들의 부과기록을 통째로 지움
public class EmployeeLeaveDeleteHandler implements CommandHandler {

	private EmployeeLeaveManageService employeeLeaveManageService = new EmployeeLeaveManageService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
			return null;
		}

		String leaveTypeIdParam = req.getParameter("leaveTypeId");
		String[] checkedEmployeeIds = req.getParameterValues("checkedEmployeeId");

		if (leaveTypeIdParam == null || leaveTypeIdParam.isBlank() || checkedEmployeeIds == null
				|| checkedEmployeeIds.length == 0) {
			res.sendRedirect(req.getContextPath() + "/Config/employeeleavemanage.do?leaveTypeId=" + leaveTypeIdParam);
			return null;
		}

		int leaveTypeId = Integer.parseInt(leaveTypeIdParam);

		int[] employeeIds = new int[checkedEmployeeIds.length];
		for (int i = 0; i < checkedEmployeeIds.length; i++) {
			employeeIds[i] = Integer.parseInt(checkedEmployeeIds[i]);
		}

		employeeLeaveManageService.deleteGrantedDays(leaveTypeId, employeeIds);

		res.sendRedirect(req.getContextPath() + "/Config/employeeleavemanage.do?leaveTypeId=" + leaveTypeId);
		return null;
	}
}
