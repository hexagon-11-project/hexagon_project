package diligence.attendancemanage.command;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeListService;
import config.dnLItemSet.service.EmployeeLeaveManageService;
import config.model.AttendanceType;
import config.model.EmployeeLeave;
import config.model.EmployeeLeaveStatus;
import diligence.attendancemanage.service.AttendanceRecordListService;
import diligence.attendancemanage.service.AttendanceRecordManageService;

// [휴가일수 현황] 버튼 - 저장하지 않고, 왼쪽 목록에서 체크한 사원(1명 이상)이
// 부여받은 휴가항목별로 전체/사용/잔여 일수를 모달 팝업 표로 보여준다.
public class DiligenceMntLeaveStatusHandler implements CommandHandler {

	private AttendanceRecordListService attendanceRecordListService = new AttendanceRecordListService();
	private AttendanceRecordManageService attendanceRecordManageService = new AttendanceRecordManageService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();
	private EmployeeLeaveManageService employeeLeaveManageService = new EmployeeLeaveManageService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMnt.do");
			return null;
		}

		int companyId = 1001;

		List<EmployeeLeave> employeeList = attendanceRecordListService.getEmployeeList(companyId);

		List<Integer> employeeIds = parseEmployeeIds(req);

		if (employeeIds.isEmpty()) {
			// 사원을 먼저 선택하지 않고 버튼을 누른 경우 - 조회 없이 목록만 다시 보여준다.
			List<AttendanceType> attendanceTypeList = attendanceTypeListService.getListForEntryForm(companyId);
			req.setAttribute("employeeList", employeeList);
			req.setAttribute("attendanceTypeList", attendanceTypeList);
			req.setAttribute("leaveOnlyTypeList", attendanceTypeListService.getLeaveOnlyOptions(companyId, attendanceTypeList));
			req.setAttribute("leaveStatusMessage", "사원을 먼저 선택해주세요.");
			return "/WEB-INF/pages/diligence/attendance-manage.jsp";
		}

		int primaryEmployeeId = employeeIds.get(employeeIds.size() - 1);

		EmployeeLeave selectedEmployee = null;
		for (EmployeeLeave e : employeeList) {
			if (e.getEmployeeId() == primaryEmployeeId) {
				selectedEmployee = e;
				break;
			}
		}

		// 체크된 사원 전원의 휴가일수 현황을 순서대로 이어붙임
		List<EmployeeLeaveStatus> leaveStatusList = new ArrayList<>();
		for (int employeeId : employeeIds) {
			leaveStatusList.addAll(employeeLeaveManageService.getStatusByEmployeeId(employeeId));
		}

		List<AttendanceType> attendanceTypeList = attendanceTypeListService.getListForEntryForm(companyId);

		req.setAttribute("employeeList", employeeList);
		req.setAttribute("attendanceTypeList", attendanceTypeList);
		req.setAttribute("leaveOnlyTypeList", attendanceTypeListService.getLeaveOnlyOptions(companyId, attendanceTypeList));
		req.setAttribute("selectedEmployee", selectedEmployee);
		req.setAttribute("recordList", attendanceRecordManageService.getListByEmployeeId(primaryEmployeeId));
		req.setAttribute("leaveStatusList", leaveStatusList);
		req.setAttribute("showLeaveStatusDialog", true);
		req.setAttribute("showRecordDialog", false); // 입력폼에서 누른 거라 근태기록 팝업은 그대로 닫혀있어야 함

		return "/WEB-INF/pages/diligence/attendance-manage.jsp";
	}

	// employeeIds(콤마 구분, 체크박스 다중 선택)가 있으면 그걸 쓰고, 없으면 employeeId(단일) 하나만 사용
	private List<Integer> parseEmployeeIds(HttpServletRequest req) {

		Set<Integer> ids = new LinkedHashSet<>();

		String employeeIdsParam = req.getParameter("employeeIds");
		if (employeeIdsParam != null && !employeeIdsParam.isBlank()) {
			for (String token : employeeIdsParam.split(",")) {
				if (!token.isBlank()) {
					ids.add(Integer.parseInt(token.trim()));
				}
			}
		}

		if (ids.isEmpty()) {
			String employeeIdParam = req.getParameter("employeeId");
			if (employeeIdParam != null && !employeeIdParam.isBlank()) {
				ids.add(Integer.parseInt(employeeIdParam.trim()));
			}
		}

		return new ArrayList<>(ids);
	}
}
