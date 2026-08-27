package config.dnLItemSet.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeListService;
import config.dnLItemSet.service.AttendanceTypeSelectService;
import config.dnLItemSet.service.LeaveTypeListService;

// 근태항목 목록에서 한 줄 클릭 - 오른쪽 편집폼에 데이터 채워서 표시
// / 勤怠項目一覧で行クリック - 右の編集フォームにデータを表示
public class AttendanceTypeSelectHandler implements CommandHandler {

	private LeaveTypeListService leaveTypeListService = new LeaveTypeListService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();
	private AttendanceTypeSelectService attendanceTypeSelectService = new AttendanceTypeSelectService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;
		int attendanceTypeId = Integer.parseInt(req.getParameter("attendanceTypeId"));

		req.setAttribute("leaveTypeList", leaveTypeListService.getList(companyId));
		req.setAttribute("attendanceTypeList", attendanceTypeListService.getAllList(companyId));
		req.setAttribute("selectedLeaveType", null);
		req.setAttribute("selectedAttendanceType", attendanceTypeSelectService.getById(attendanceTypeId));

		return "/WEB-INF/pages/config/leave-settings.jsp";
	}
}
