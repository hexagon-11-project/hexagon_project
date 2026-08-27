package diligence.attendancemanage.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import diligence.attendancemanage.service.AttendanceRecordManageService;

// 근태 기록 삭제 요청을 처리하는 컨트롤러(Command 핸들러) 클래스
// 勤怠記録の削除リクエストを処理するコントローラー（Commandハンドラー）クラス
public class DiligenceMntDeleteHandler implements CommandHandler {

	private AttendanceRecordManageService attendanceRecordManageService = new AttendanceRecordManageService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// 비정상적인 접근(GET 방식 등) 방지 - POST 요청이 아니면 메인 근태관리 화면으로 튕겨냄(리다이렉트)
		// 不正なアクセス（GET方式など）の防止 - POSTリクエストでなければメイン勤怠管理画面へリダイレクト
		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMnt.do");
			return null;
		}

		// 삭제할 근태 기록의 PK(attendanceId)와 작업 후 돌아갈 사원의 ID(employeeId)를 파라미터로 받음
		// 削除する勤怠記録のPK（attendanceId）と、作業後に戻る社員のID（employeeId）をパラメータとして受け取る
		String attendanceIdParam = req.getParameter("attendanceId");
		String employeeIdParam = req.getParameter("employeeId");

		// 삭제할 아이디가 유효하게 넘어왔을 경우에만 서비스단을 통해 DB에서 삭제 처리 수행
		// 削除するIDが有効に渡ってきた場合のみ、サービス層を通じてDBから削除処理を実行
		if (attendanceIdParam != null && !attendanceIdParam.isBlank()) {
			attendanceRecordManageService.delete(Integer.parseInt(attendanceIdParam));
		}

		// 삭제가 끝나면 해당 사원의 근태 조회 화면으로 다시 리다이렉트하여 변경된 내역을 반영함
		// 削除が完了したら、該当社員の勤怠照会画面へ再びリダイレクトして変更履歴を反映する
		res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMntSelect.do?employeeId=" + employeeIdParam);
		return null;
	}
}