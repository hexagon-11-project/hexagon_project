package diligence.attendancemanage.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeListService;
import diligence.attendancemanage.service.AttendanceRecordListService;

// 근태기록/관리 화면 진입점 (GET) - 사원이 선택 안 된 빈 화면
// 勤怠記録・管理画面の入口（GET） - 社員が選択されていない空の画面
public class DiligenceMntHandler implements CommandHandler {

	private AttendanceRecordListService attendanceRecordListService = new AttendanceRecordListService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001; // 회사 고유 ID 기본값 (会社固有IDのデフォルト値)

		// 폼 입력에 사용할 근태 항목 전체 목록을 조회
		// フォーム入力に使用する勤怠項目の全体リストを照会
		java.util.List<config.model.AttendanceType> attendanceTypeList = attendanceTypeListService.getListForEntryForm(companyId);

		// 화면에 뿌려줄 사원 목록, 근태 항목, 휴가 전용 옵션 등을 request에 담음
		// 画面に描画する社員リスト、勤怠項目、休暇専用オプションなどをrequestに詰める
		req.setAttribute("employeeList", attendanceRecordListService.getEmployeeList(companyId));
		req.setAttribute("attendanceTypeList", attendanceTypeList);
		req.setAttribute("leaveOnlyTypeList", attendanceTypeListService.getLeaveOnlyOptions(companyId, attendanceTypeList));
		req.setAttribute("selectedEmployee", null); // 최초 진입이라 선택된 사원은 없음 (初回進入のため選択された社員はなし)
		req.setAttribute("recordList", null);      // 최초 진입이라 근태 기록 목록도 없음 (初回進入のため勤怠記録リストもなし)

		// 근태 관리 메인 JSP 페이지로 포워딩
		// 勤怠管理メインJSPページへフォ워딩（フォワード）
		return "/WEB-INF/pages/diligence/attendance-manage.jsp";
	}
}