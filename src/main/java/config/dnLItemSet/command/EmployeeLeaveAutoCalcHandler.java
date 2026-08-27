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

// 체크된 사원만 연차 자동계산해서 화면 입력칸에 미리 채워줌
// DB 저장은 안 함 - [휴가일수 저장] 따로 눌러야 반영
// / チェックした社員の年次を自動計算して画面に表示
// / DB保存なし - [休暇日数保存]で別途反映
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
		String workTimeType = "40"; // 근무시간제 선택 없애고 40시간제 고정 / 勤務時間制は40時間固定
		String[] checkedEmployeeIds = req.getParameterValues("checkedEmployeeId");

		LeaveType manageLeaveType = leaveTypeSelectService.getById(leaveTypeId);
		List<EmployeeLeave> employeeLeaveList = employeeLeaveManageService.getList(leaveTypeId, companyId);

		if (manageLeaveType != null && checkedEmployeeIds != null && checkedEmployeeIds.length > 0) {

			Date refDate = manageLeaveType.getEffectiveStartDate(); // 적용기간 시작일 기준으로 계산 / 適用期間開始日を基準に計算

			java.util.Set<Integer> checkedSet = new java.util.HashSet<>();
			for (String idStr : checkedEmployeeIds) {
				checkedSet.add(Integer.parseInt(idStr));
			}

			for (EmployeeLeave row : employeeLeaveList) {
				if (checkedSet.contains(row.getEmployeeId())) {
					BigDecimal calculated = AnnualLeaveCalculator.calculate(row.getHireDate(), refDate, workTimeType);
					row.setGrantedDays(calculated); // 화면 표시용만, DB 반영 안 함 / 画面表示のみ、DB未反映
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
