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
public class HolidaysSearchResultHandler implements CommandHandler {

	private LeaveSearchService leaveSearchService = new LeaveSearchService();
	private LeaveTypeListService leaveTypeListService = new LeaveTypeListService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		int companyId = 1001;

		List<LeaveType> leaveTypeList = leaveTypeListService.getList(companyId);

		Integer leaveTypeId = parseLeaveTypeId(req.getParameter("leaveTypeId"), leaveTypeList);
		int year = parseYear(req.getParameter("year"));

		String sortKey = req.getParameter("sortKey");
		if (sortKey == null || sortKey.isBlank()) {
			sortKey = LeaveSearchDao.SORT_NAME;
		}

		req.setAttribute("leaveTypeList", leaveTypeList);
		req.setAttribute("selectedLeaveTypeId", leaveTypeId);
		req.setAttribute("year", year);
		req.setAttribute("sortKey", sortKey);

		if (leaveTypeId == null) {
			// 회사에 등록된 휴가항목이 하나도 없는 경우 - 조회할 대상이 없어 빈 화면만 보여줌
			return "/WEB-INF/pages/diligence/leave-search.jsp";
		}

		List<EmployeeLeaveStatus> statusList = leaveSearchService.getStatusByLeaveType(companyId, leaveTypeId, year,
				sortKey);
		req.setAttribute("statusList", statusList);

		String selectedEmployeeIdParam = req.getParameter("selectedEmployeeId");
		if (selectedEmployeeIdParam != null && !selectedEmployeeIdParam.isBlank()) {

			int selectedEmployeeId = Integer.parseInt(selectedEmployeeIdParam);

			EmployeeLeaveStatus selectedStatus = null;
			for (EmployeeLeaveStatus status : statusList) {
				if (selectedEmployeeId == status.getEmployeeId()) {
					selectedStatus = status;
					break;
				}
			}

			if (selectedStatus != null) {
				List<AttendanceRecord> usageList = leaveSearchService.getUsageDetail(selectedEmployeeId, leaveTypeId,
						year);
				req.setAttribute("usageList", usageList);
				req.setAttribute("selectedStatus", selectedStatus);
				req.setAttribute("selectedEmployeeId", selectedEmployeeId);
			}
		}

		return "/WEB-INF/pages/diligence/leave-search.jsp";
	}

	private Integer parseLeaveTypeId(String param, List<LeaveType> leaveTypeList) {

		if (param != null && !param.isBlank()) {
			return Integer.parseInt(param);
		}

		// 파라미터가 없는 첫 진입 - 등록된 휴가항목 중 첫 번째를 기본으로 사용
		if (leaveTypeList != null && !leaveTypeList.isEmpty()) {
			return leaveTypeList.get(0).getLeaveTypeId();
		}

		return null;
	}

	private int parseYear(String param) {
		if (param != null && !param.isBlank()) {
			try {
				return Integer.parseInt(param);
			} catch (NumberFormatException e) {
				// 잘못된 형식이면 올해로 대체
			}
		}
		return Year.now().getValue();
	}
}
