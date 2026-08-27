package diligence.attendancemanage.command;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeInsertService;
import config.dnLItemSet.service.AttendanceTypeSelectService;
import config.dnLItemSet.service.LeaveTypeSelectService;
import config.model.AttendanceRecord;
import config.model.AttendanceType;
import config.model.LeaveType;
import diligence.attendancemanage.service.AttendanceRecordManageService;

// 근태기록 수정 저장 - attendanceId 있을 때 이쪽으로 옴
// / 勤怠記録の修正保存 - attendanceIdがある場合にここへ
public class DiligenceMntUpdateHandler implements CommandHandler {

	private static final String LEAVE_PREFIX = "leave-";

	private AttendanceRecordManageService attendanceRecordManageService = new AttendanceRecordManageService();
	private AttendanceTypeSelectService attendanceTypeSelectService = new AttendanceTypeSelectService();
	private AttendanceTypeInsertService attendanceTypeInsertService = new AttendanceTypeInsertService();
	private LeaveTypeSelectService leaveTypeSelectService = new LeaveTypeSelectService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMnt.do");
			return null;
		}

		String attendanceIdParam = req.getParameter("attendanceId");
		String employeeIdParam = req.getParameter("employeeId");
		String attendanceTypeIdParam = req.getParameter("attendanceTypeId");
		String startDateParam = req.getParameter("startDate");
		String endDateParam = req.getParameter("endDate");
		String countParam = req.getParameter("count");

		if (isBlank(attendanceIdParam) || isBlank(attendanceTypeIdParam) || isBlank(startDateParam)
				|| isBlank(endDateParam)) {
			res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMntSelect.do?employeeId=" + employeeIdParam);
			return null;
		}

		int attendanceTypeId = resolveAttendanceTypeId(attendanceTypeIdParam);
		AttendanceType type = attendanceTypeSelectService.getById(attendanceTypeId);

		AttendanceRecord item = new AttendanceRecord();
		item.setAttendanceId(Integer.parseInt(attendanceIdParam));
		item.setAttendanceTypeId(attendanceTypeId);
		item.setStartDate(java.sql.Date.valueOf(startDateParam));
		item.setEndDate(java.sql.Date.valueOf(endDateParam));
		item.setDescription(req.getParameter("description"));
		item.setAllowanceAmount(parseOrNull(req.getParameter("amount")));

		BigDecimal count = parseOrNull(countParam);

		// 시간 단위면 HOUR_COUNT, 그 외엔 DAY_COUNT / 時間単位ならHOUR_COUNT、それ以外はDAY_COUNT
		if (type != null && "HOUR".equalsIgnoreCase(type.getUnitCode())) {
			item.setHourCount(count);
		} else {
			item.setDayCount(count);
		}

		attendanceRecordManageService.update(item);

		res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMntSelect.do?employeeId=" + employeeIdParam
				+ "&silent=1&saved=1");
		return null;
	}

	// "leave-{id}" 형태면 휴가항목 - 매핑되는 근태항목 찾거나 새로 만들기
	// / "leave-{id}"形式なら休暇項目 - 対応する勤怠項目を検索または新規作成
	private int resolveAttendanceTypeId(String attendanceTypeIdParam) {

		if (!attendanceTypeIdParam.startsWith(LEAVE_PREFIX)) {
			return Integer.parseInt(attendanceTypeIdParam);
		}

		int companyId = 1001;
		int leaveTypeId = Integer.parseInt(attendanceTypeIdParam.substring(LEAVE_PREFIX.length()));
		LeaveType leaveType = leaveTypeSelectService.getById(leaveTypeId);

		return attendanceTypeInsertService.resolveAttendanceTypeIdForLeaveType(companyId, leaveTypeId,
				leaveType.getLeaveName(), leaveType.getLeaveCode());
	}

	private BigDecimal parseOrNull(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			return new BigDecimal(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
