package diligence.searchmonth.command;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeListService;
import config.model.AttendanceRecord;
import config.model.AttendanceType;
import diligence.searchmonth.dao.AttendanceSearchDao;
import diligence.searchmonth.service.AttendanceSearchService;

// [근태조회] - 조회월/근태항목/정렬 조건으로 그 달에 걸쳐있는 전체 사원의 근태기록을 조회 (저장 없음, 조회 전용)
// [勤怠照会] - 照会月／勤怠項目／並び替え条件でその月にわたる全社員の勤怠記録を照会（保存なし、照会専用）
public class DiligenceSearchMonthHandler implements CommandHandler {

	private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

	private AttendanceSearchService attendanceSearchService = new AttendanceSearchService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001; // 회사 고유 ID 기본값 (会社固有IDのデフォルト値)

		// 화면에서 넘어온 검색 파라미터(조회월, 근태항목ID, 정렬키) 수집
		// 画面から渡された検索パラメータ（照会月、勤怠項目ID、ソートキー）を収集
		String monthParam = req.getParameter("searchMonth");
		String attendanceTypeIdParam = req.getParameter("attendanceTypeId");
		String sortKey = req.getParameter("sortKey");

		// 조회월 파싱 (유효하지 않으면 현재 연월로 대체)
		// 照会月のパース（無効な場合は現在の年月で代替）
		YearMonth yearMonth = parseYearMonth(monthParam);

		// 근태항목 ID가 존재하면 정수형으로 변환
		// 勤怠項目IDが存在すれば整数型に変換
		Integer attendanceTypeId = null;
		if (attendanceTypeIdParam != null && !attendanceTypeIdParam.isBlank()) {
			attendanceTypeId = Integer.parseInt(attendanceTypeIdParam);
		}

		// 정렬 조건이 없으면 기본값(이름순 정렬)으로 세팅
		// 並び替え条件がなければデフォルト値（名前順並び替え）に設定
		if (sortKey == null || sortKey.isBlank()) {
			sortKey = AttendanceSearchDao.SORT_NAME;
		}

		// 해당 월의 시작일(1일)과 말일 계산
		// 該当月の初日（1日）と末日を計算
		LocalDate startOfMonth = yearMonth.atDay(1);
		LocalDate endOfMonth = yearMonth.atEndOfMonth();

		// 서비스단을 통해 조건에 맞는 전체 사원의 월별 근태 기록 목록을 조회
		// サービス層を通じて条件に合う全社員の月別勤怠記録リストを照会
		List<AttendanceRecord> recordList = attendanceSearchService.getListByMonth(companyId,
				Date.valueOf(startOfMonth), Date.valueOf(endOfMonth), attendanceTypeId, sortKey);

		// 드롭다운(셀렉트 박스)에 뿌려줄 전체 근태 항목 목록을 조회
		// ドロップダウン（セレクトボックス）に描画する全勤怠項目のリストを照会
		List<AttendanceType> attendanceTypeList = attendanceTypeListService.getAllList(companyId);

		// 조회 결과 및 검색 조건 값들을 request에 담아 JSP로 전달
		// 照会結果および検索条件の値をrequestに詰めてJSPへ渡す
		req.setAttribute("recordList", recordList);
		req.setAttribute("attendanceTypeList", attendanceTypeList);
		req.setAttribute("searchMonth", yearMonth.format(MONTH_FORMAT));
		req.setAttribute("selectedAttendanceTypeId", attendanceTypeId);
		req.setAttribute("sortKey", sortKey);

		// 근태 조회 전용 JSP 페이지로 포워딩
		// 勤怠照会専用のJSPページへフォワード
		return "/WEB-INF/pages/diligence/attendance-search.jsp";
	}

	// 문자열 월 파라미터를 YearMonth 객체로 안전하게 변환하는 헬퍼 메서드 (실패 시 현재 연월 반환)
	// 文字列の月パラメータをYearMonthオブジェクトに安全に変換するヘルパーメソッド（失敗時は現在の年月を返却）
	private YearMonth parseYearMonth(String monthParam) {
		if (monthParam != null && !monthParam.isBlank()) {
			try {
				return YearMonth.parse(monthParam, MONTH_FORMAT);
			} catch (DateTimeParseException e) {
				// 잘못된 형식이면 이번 달로 대체 (不正なフォーマットなら今月に代替)
			}
		}
		return YearMonth.now();
	}
}