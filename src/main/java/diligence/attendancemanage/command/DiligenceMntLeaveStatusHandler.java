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

// [휴가일수 현황] 버튼 - 체크한 사원의 휴가항목별 전체/사용/잔여 일수를 팝업으로 표시
// / [休暇日数現況]ボタン - チェックした社員の休暇項目別日数をダイアログで表示
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
			// 사원 선택 없이 버튼 누른 경우 - 목록만 다시 표시 / 社員未選択でボタンが押された場合
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

		// 체크된 사원 전원 현황 이어붙이기 / チェックした全員分の現況を結合
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
		req.setAttribute("showRecordDialog", false); // 근태기록 팝업은 닫아둠 / 勤怠記録ダイアログは閉じておく

		return "/WEB-INF/pages/diligence/attendance-manage.jsp";
	}

	// employeeIds(콤마 구분) 있으면 다중, 없으면 employeeId 단일 사용
	// / employeeIds(カンマ区切り)があれば複数、なければemployeeIdを単一使用
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
