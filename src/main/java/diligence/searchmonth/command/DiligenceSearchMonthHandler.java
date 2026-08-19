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
public class DiligenceSearchMonthHandler implements CommandHandler {

	private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

	private AttendanceSearchService attendanceSearchService = new AttendanceSearchService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;

		String monthParam = req.getParameter("searchMonth");
		String attendanceTypeIdParam = req.getParameter("attendanceTypeId");
		String sortKey = req.getParameter("sortKey");

		YearMonth yearMonth = parseYearMonth(monthParam);

		Integer attendanceTypeId = null;
		if (attendanceTypeIdParam != null && !attendanceTypeIdParam.isBlank()) {
			attendanceTypeId = Integer.parseInt(attendanceTypeIdParam);
		}

		if (sortKey == null || sortKey.isBlank()) {
			sortKey = AttendanceSearchDao.SORT_NAME;
		}

		LocalDate startOfMonth = yearMonth.atDay(1);
		LocalDate endOfMonth = yearMonth.atEndOfMonth();

		List<AttendanceRecord> recordList = attendanceSearchService.getListByMonth(companyId,
				Date.valueOf(startOfMonth), Date.valueOf(endOfMonth), attendanceTypeId, sortKey);

		List<AttendanceType> attendanceTypeList = attendanceTypeListService.getAllList(companyId);

		req.setAttribute("recordList", recordList);
		req.setAttribute("attendanceTypeList", attendanceTypeList);
		req.setAttribute("searchMonth", yearMonth.format(MONTH_FORMAT));
		req.setAttribute("selectedAttendanceTypeId", attendanceTypeId);
		req.setAttribute("sortKey", sortKey);

		return "/WEB-INF/pages/diligence/attendance-search.jsp";
	}

	private YearMonth parseYearMonth(String monthParam) {
		if (monthParam != null && !monthParam.isBlank()) {
			try {
				return YearMonth.parse(monthParam, MONTH_FORMAT);
			} catch (DateTimeParseException e) {
				// 잘못된 형식이면 이번 달로 대체
			}
		}
		return YearMonth.now();
	}
}
