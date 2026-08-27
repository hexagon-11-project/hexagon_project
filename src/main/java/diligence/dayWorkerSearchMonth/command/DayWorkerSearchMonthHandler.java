package diligence.dayWorkerSearchMonth.command;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.DailyWorkRecord;
import diligence.dayWorkerSearchMonth.service.DayWorkerSearchService;

// [일용직 근무조회] - 조회월/현장/사원명 조건으로 일용직 근무기록과 지급 합계를 조회 (저장 없음, 조회 전용)
// [日雇い勤務照会] - 照会月／現場／社員名条件で日雇い勤務記録と支給合計を照会（保存なし、照会専用）
public class DayWorkerSearchMonthHandler implements CommandHandler {

	private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

	// 근태기록/관리(diligence.dailyworkrecord) 화면과 동일한 고정 현장 목록
	// 勤怠記録・管理（diligence.dailyworkrecord）画面と同一の固定現場リスト
	private static final String[] WORK_SITE_OPTIONS = { "現場1", "現場2", "研究所", "開発プロジェクト", "第1工場" };

	private DayWorkerSearchService dayWorkerSearchService = new DayWorkerSearchService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001; // 회사 고유 ID 기본값 (会社固有IDのデフォルト値)

		// 화면에서 넘어온 검색 파라미터(조회월, 현장명, 사원명 검색어) 수집
		// 画面から渡された検索パラメータ（照会月、現場名、社員名検索ワード）を収集
		String monthParam = req.getParameter("searchMonth");
		String workSiteName = req.getParameter("workSiteName");
		String employeeNameKeyword = req.getParameter("employeeName");

		// 조회월 파싱 (유효하지 않으면 현재 연월로 안전하게 대체)
		// 照会月のパース（無効な場合は現在の年月で安全に代替）
		YearMonth yearMonth = parseYearMonth(monthParam);
		LocalDate startOfMonth = yearMonth.atDay(1);             // 해당 월의 1일 (該当月の1日)
		LocalDate endOfMonth = yearMonth.atEndOfMonth();          // 해당 월의 말일 (該当月の末日)

		// 서비스단을 통해 조건에 맞는 일용직 근무 기록 리스트를 긁어옴
		// サービス層を通じて条件に合う日雇い勤務記録リストをかき集める
		List<DailyWorkRecord> recordList = dayWorkerSearchService.getListByMonth(companyId,
				Date.valueOf(startOfMonth), Date.valueOf(endOfMonth), workSiteName, employeeNameKeyword);

		// 조회된 목록의 지급액 총합과 실지급액 총합을 반복문으로 누적 계산
		// 照会されたリストの支給額総額と実支給額総額をループで累積計算
		BigDecimal payAmountTotal = BigDecimal.ZERO;
		BigDecimal netPayTotal = BigDecimal.ZERO;
		for (DailyWorkRecord record : recordList) {
			if (record.getPayAmount() != null) {
				payAmountTotal = payAmountTotal.add(record.getPayAmount());
			}
			if (record.getNetPayAmount() != null) {
				netPayTotal = netPayTotal.add(record.getNetPayAmount());
			}
		}

		// 조회 결과, 검색 조건, 현장 옵션, 계산된 합계 금액을 request에 세팅
		// 照会結果、検索条件、現場オプション、計算された合計金額をrequestにセット
		req.setAttribute("recordList", recordList);
		req.setAttribute("workSiteOptions", WORK_SITE_OPTIONS);
		req.setAttribute("searchMonth", yearMonth.format(MONTH_FORMAT));
		req.setAttribute("selectedWorkSiteName", workSiteName);
		req.setAttribute("employeeNameKeyword", employeeNameKeyword);
		req.setAttribute("payAmountTotal", String.format("%,d", payAmountTotal.longValue()));
		req.setAttribute("netPayTotal", String.format("%,d", netPayTotal.longValue()));

		// 일용직 근무 조회 전용 JSP 페이지로 포워딩
		// 日雇い勤務照会専用のJSPページへフォワード
		return "/WEB-INF/pages/diligence/daily-work-search.jsp";
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