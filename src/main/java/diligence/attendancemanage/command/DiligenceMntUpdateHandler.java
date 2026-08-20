package diligence.attendancemanage.command;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeSelectService;
import config.model.AttendanceRecord;
import config.model.AttendanceType;
import diligence.attendancemanage.service.AttendanceRecordManageService;

// [수정] - 팝업에서 [수정] 눌러서 입력폼에 채워진 내용을 [저장]했을 때 처리 (attendanceId가 있으면 이쪽으로 옴)
public class DiligenceMntUpdateHandler implements CommandHandler {

	private AttendanceRecordManageService attendanceRecordManageService = new AttendanceRecordManageService();
	private AttendanceTypeSelectService attendanceTypeSelectService = new AttendanceTypeSelectService();

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

		int attendanceTypeId = Integer.parseInt(attendanceTypeIdParam);
		AttendanceType type = attendanceTypeSelectService.getById(attendanceTypeId);

		AttendanceRecord item = new AttendanceRecord();
		item.setAttendanceId(Integer.parseInt(attendanceIdParam));
		item.setAttendanceTypeId(attendanceTypeId);
		item.setStartDate(java.sql.Date.valueOf(startDateParam));
		item.setEndDate(java.sql.Date.valueOf(endDateParam));
		item.setDescription(req.getParameter("description"));
		item.setAllowanceAmount(parseOrNull(req.getParameter("amount")));

		BigDecimal count = parseOrNull(countParam);

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
