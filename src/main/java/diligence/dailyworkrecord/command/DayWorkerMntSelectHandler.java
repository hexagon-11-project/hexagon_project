package diligence.dailyworkrecord.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.EmployeeLeave;
import diligence.dailyworkrecord.service.DailyWorkRecordService;

// 왼쪽 목록에서 [관리] 클릭 - 그 사원을 오른쪽 입력폼 대상으로 잡고 일용직 근무기록 이력을 보여줌
public class DayWorkerMntSelectHandler implements CommandHandler {

	private DailyWorkRecordService dailyWorkRecordService = new DailyWorkRecordService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;
		int employeeId = Integer.parseInt(req.getParameter("employeeId"));

		List<EmployeeLeave> employeeList = dailyWorkRecordService.getDailyWorkerEmployees(companyId);

		EmployeeLeave selectedEmployee = null;
		for (EmployeeLeave e : employeeList) {
			if (e.getEmployeeId() == employeeId) {
				selectedEmployee = e;
				break;
			}
		}

		req.setAttribute("employeeList", employeeList);
		req.setAttribute("selectedEmployee", selectedEmployee);
		req.setAttribute("recordList", dailyWorkRecordService.getListByEmployeeId(employeeId));

		String editIdParam = req.getParameter("editId");
		boolean silent = "1".equals(req.getParameter("silent"));

		if (editIdParam != null && !editIdParam.isBlank()) {
			req.setAttribute("editRecord", dailyWorkRecordService.getById(Integer.parseInt(editIdParam)));
			req.setAttribute("showRecordDialog", false); // [수정] 클릭 - 팝업 닫고 입력폼에서 편집
		} else if (silent) {
			req.setAttribute("showRecordDialog", false); // 왼쪽 목록 체크박스로 선택만 한 경우 - 팝업 열지 않음
		} else {
			req.setAttribute("showRecordDialog", true); // [관리] 클릭 - 팝업 보여줌
		}

		if ("1".equals(req.getParameter("saved"))) {
			req.setAttribute("saveMessage", "저장되었습니다."); // [저장] 후 복귀 - 팝업 대신 알림만
		}

		return "/WEB-INF/pages/diligence/daily-work-manage.jsp";
	}
}
