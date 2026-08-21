package diligence.dailyworkrecord.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import diligence.dailyworkrecord.service.DailyWorkRecordService;

// [근태관리 > 일용직 근무기록/관리] 최초 진입 화면 - 사원 미선택 상태
public class DayWorkerMntHandler implements CommandHandler {

	private DailyWorkRecordService dailyWorkRecordService = new DailyWorkRecordService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;

		req.setAttribute("employeeList", dailyWorkRecordService.getDailyWorkerEmployees(companyId));
		req.setAttribute("selectedEmployee", null);
		req.setAttribute("recordList", null);

		return "/WEB-INF/pages/diligence/daily-work-manage.jsp";
	}
}
