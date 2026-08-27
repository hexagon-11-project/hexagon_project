package diligence.holidayssearchresult.command;

import java.time.Year;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.LeaveTypeListService;
import config.model.AttendanceRecord;
import config.model.EmployeeLeaveStatus;
import config.model.LeaveType;
import diligence.holidayssearchresult.dao.LeaveSearchDao;
import diligence.holidayssearchresult.service.LeaveSearchService;

// [휴가조회] - 휴가항목/기준연도/정렬 조건으로 휴가 현황을 조회하고,
// 그 목록에서 사원을 선택하면 그 사원의 휴가 사용내역까지 같이 보여준다. (저장 없음, 조회 전용)
// [休暇照会] - 休暇項目／基準年度／並び替え条件で休暇現状を照会し、
// そのリストから社員を選択するとその社員の休暇使用履歴まで一緒に表示する。（保存なし、照会専用）
public class HolidaysSearchResultHandler implements CommandHandler {

	private LeaveSearchService leaveSearchService = new LeaveSearchService();
	private LeaveTypeListService leaveTypeListService = new LeaveTypeListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001; // 회사 고유 ID 기본값 (会社固有IDのデフォルト値)

		// 회사에 등록된 전체 휴가 항목 목록을 조회
		// 会社に登録された全休暇項目のリストを照会
		List<LeaveType> leaveTypeList = leaveTypeListService.getList(companyId);

		// 요청 파라미터에서 휴가항목 ID와 기준 연도를 안전하게 파싱 (없으면 기본값 처리)
		// リクエストパラメータから休暇項目IDと基準年度を安全にパース（なければデフォルト値処理）
		Integer leaveTypeId = parseLeaveTypeId(req.getParameter("leaveTypeId"), leaveTypeList);
		int year = parseYear(req.getParameter("year"));

		// 정렬 조건(sortKey)이 없으면 기본값(이름순 정렬)으로 세팅
		// 並び替え条件（sortKey）がなければデフォルト値（名前順並び替え）に設定
		String sortKey = req.getParameter("sortKey");
		if (sortKey == null || sortKey.isBlank()) {
			sortKey = LeaveSearchDao.SORT_NAME;
		}

		// 기본 검색 조건들을 request에 담아 화면(JSP)으로 전달
		// 基本検索条件をrequestに詰めて画面（JSP）へ渡す
		req.setAttribute("leaveTypeList", leaveTypeList);
		req.setAttribute("selectedLeaveTypeId", leaveTypeId);
		req.setAttribute("year", year);
		req.setAttribute("sortKey", sortKey);

		// 휴가 항목이 아예 등록되어 있지 않은 경우 빈 화면 리턴
		// 休暇項目が全く登録されていない場合、空画面をリターン
		if (leaveTypeId == null) {
			// 회사에 등록된 휴가항목이 하나도 없는 경우 - 조회할 대상이 없어 빈 화면만 보여줌
			// 会社に登録された休暇項目が一つもない場合 - 照会対象がないため空画面のみ表示
			return "/WEB-INF/pages/diligence/leave-search.jsp";
		}

		// 선택된 휴가 항목과 연도, 정렬 기준으로 전체 사원의 휴가 현황 목록 조회
		// 選択された休暇項目と年度、並び替え基準で全社員の休暇現状リストを照会
		List<EmployeeLeaveStatus> statusList = leaveSearchService.getStatusByLeaveType(companyId, leaveTypeId, year,
				sortKey);
		req.setAttribute("statusList", statusList);

		// 목록에서 특정 사원을 클릭(선택)하여 상세 사용 내역을 요청한 경우 처리
		// リストから特定の社員をクリック（選択）して詳細使用履歴をリクエストした場合の処理
		String selectedEmployeeIdParam = req.getParameter("selectedEmployeeId");
		if (selectedEmployeeIdParam != null && !selectedEmployeeIdParam.isBlank()) {

			int selectedEmployeeId = Integer.parseInt(selectedEmployeeIdParam);

			// 조회된 전체 현황 목록 중에서 클릭한 사원의 데이터를 매칭
			// 照会された全体現状リストの中からクリックした社員のデータをマッチング
			EmployeeLeaveStatus selectedStatus = null;
			for (EmployeeLeaveStatus status : statusList) {
				if (selectedEmployeeId == status.getEmployeeId()) {
					selectedStatus = status;
					break;
				}
			}

			// 해당 사원이 존재하면, 해당 연도/휴가항목에 대한 상세 사용 이력을 긁어옴
			// 該当社員が存在すれば、該当年度／休暇項目に対する詳細使用履歴をかき集める
			if (selectedStatus != null) {
				List<AttendanceRecord> usageList = leaveSearchService.getUsageDetail(selectedEmployeeId, leaveTypeId,
						year);
				req.setAttribute("usageList", usageList);
				req.setAttribute("selectedStatus", selectedStatus);
				req.setAttribute("selectedEmployeeId", selectedEmployeeId);
			}
		}

		// 휴가 조회 및 상세 내역이 포함된 JSP 페이지로 포워딩
		// 休暇照会および詳細履歴が含まれるJSPページへフォワード
		return "/WEB-INF/pages/diligence/leave-search.jsp";
	}

	// 휴가항목 ID 파라미터가 없으면 등록된 첫 번째 항목을 기본값으로 지정하는 헬퍼
	// 休暇項目IDパラメータがなければ登録された最初の項目をデフォルト値に指定するヘルパー
	private Integer parseLeaveTypeId(String param, List<LeaveType> leaveTypeList) {

		if (param != null && !param.isBlank()) {
			return Integer.parseInt(param);
		}

		// 파라미터가 없는 첫 진입 - 등록된 휴가항목 중 첫 번째를 기본으로 사용
		// パラメータのない初回進入 - 登録された休暇項目の中から最初をデフォルトとして使用
		if (leaveTypeList != null && !leaveTypeList.isEmpty()) {
			return leaveTypeList.get(0).getLeaveTypeId();
		}

		return null;
	}

	// 연도 파라미터를 안전하게 파싱하며, 유효하지 않으면 올해 연도로 대체하는 헬퍼
	// 年度パラメータを安全にパースし、有効でなければ今年の年度に代替するヘルパー
	private int parseYear(String param) {
		if (param != null && !param.isBlank()) {
			try {
				return Integer.parseInt(param);
			} catch (NumberFormatException e) {
				// 잘못된 형식이면 올해로 대체 (不正なフォーマットなら今年に代替)
			}
		}
		return Year.now().getValue();
	}
}