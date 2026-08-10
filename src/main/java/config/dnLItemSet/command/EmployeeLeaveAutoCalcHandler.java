package config.dnLItemSet.command;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AnnualLeaveCalculator;
import config.dnLItemSet.service.AttendanceTypeListService;
import config.dnLItemSet.service.EmployeeLeaveManageService;
import config.dnLItemSet.service.LeaveTypeListService;
import config.dnLItemSet.service.LeaveTypeSelectService;
import config.model.EmployeeLeave;
import config.model.LeaveType;

// [휴가일수 자동계산] - 체크된 사원들만 계산해서 화면 입력칸에 미리 채워준다.
// 여기서는 DB에 저장하지 않고, [휴가일수 저장]을 따로 눌러야 실제로 반영된다.
public class EmployeeLeaveAutoCalcHandler implements CommandHandler {

	private LeaveTypeListService leaveTypeListService = new LeaveTypeListService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();
	private LeaveTypeSelectService leaveTypeSelectService = new LeaveTypeSelectService();
	private EmployeeLeaveManageService employeeLeaveManageService = new EmployeeLeaveManageService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Config/leavesettingslist.do");
			return null;
		}

		int companyId = 1001;
		int leaveTypeId = Integer.parseInt(req.getParameter("leaveTypeId"));
		String workTimeType = "40"; // 화면에서 근무시간제 선택칸을 없애서 40시간제 기준으로 고정
		String[] checkedEmployeeIds = req.getParameterValues("checkedEmployeeId");

		LeaveType manageLeaveType = leaveTypeSelectService.getById(leaveTypeId);
		List<EmployeeLeave> employeeLeaveList = employeeLeaveManageService.getList(leaveTypeId, companyId);

		if (manageLeaveType != null && checkedEmployeeIds != null && checkedEmployeeIds.length > 0) {

			Date refDate = manageLeaveType.getEffectiveStartDate(); // 적용기간 시작일 기준으로 계산

			java.util.Set<Integer> checkedSet = new java.util.HashSet<>();
			for (String idStr : checkedEmployeeIds) {
				checkedSet.add(Integer.parseInt(idStr));
			}

			for (EmployeeLeave row : employeeLeaveList) {
				if (checkedSet.contains(row.getEmployeeId())) {
					BigDecimal calculated = AnnualLeaveCalculator.calculate(row.getHireDate(), refDate, workTimeType);
					row.setGrantedDays(calculated); // 화면 표시용으로만 덮어씀 (DB 저장 아님)
				}
			}
		}

		req.setAttribute("leaveTypeList", leaveTypeListService.getList(companyId));
		req.setAttribute("attendanceTypeList", attendanceTypeListService.getAllList(companyId));
		req.setAttribute("selectedLeaveType", null);
		req.setAttribute("selectedAttendanceType", null);
		req.setAttribute("manageLeaveType", manageLeaveType);
		req.setAttribute("employeeLeaveList", employeeLeaveList);
		req.setAttribute("selectedWorkTimeType", workTimeType);

		return "/WEB-INF/pages/config/leave-settings.jsp";
	}
}
