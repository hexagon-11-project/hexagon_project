package diligence.attendancemanage.command;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeSelectService;
import config.model.AttendanceRecord;
import config.model.AttendanceType;
import diligence.attendancemanage.service.AttendanceRecordManageService;

// [저장] - 근태기록 한 건 등록. 근태항목의 단위(일/시간)에 따라 DAY_COUNT/HOUR_COUNT 중 알맞은 쪽에 저장
public class DiligenceMntInsertHandler implements CommandHandler {

	private AttendanceRecordManageService attendanceRecordManageService = new AttendanceRecordManageService();
	private AttendanceTypeSelectService attendanceTypeSelectService = new AttendanceTypeSelectService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMnt.do");
			return null;
		}

		String employeeIdParam = req.getParameter("employeeId");
		String attendanceTypeIdParam = req.getParameter("attendanceTypeId");
		String startDateParam = req.getParameter("startDate");
		String endDateParam = req.getParameter("endDate");
		String countParam = req.getParameter("count");

		if (isBlank(employeeIdParam) || isBlank(attendanceTypeIdParam) || isBlank(startDateParam)
				|| isBlank(endDateParam)) {
			res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMntSelect.do?employeeId=" + employeeIdParam);
			return null;
		}

		int employeeId = Integer.parseInt(employeeIdParam);
		int attendanceTypeId = Integer.parseInt(attendanceTypeIdParam);

		AttendanceType type = attendanceTypeSelectService.getById(attendanceTypeId);

		AttendanceRecord item = new AttendanceRecord();
		item.setEmployeeId(employeeId);
		item.setAttendanceTypeId(attendanceTypeId);
		item.setStartDate(java.sql.Date.valueOf(startDateParam));
		item.setEndDate(java.sql.Date.valueOf(endDateParam));
		item.setDescription(req.getParameter("description"));
		item.setAllowanceAmount(parseOrNull(req.getParameter("amount")));

		BigDecimal count = parseOrNull(countParam);

		// 단위가 시간(HOUR)이면 HOUR_COUNT에, 그 외(일 단위 등)에는 DAY_COUNT에 저장
		if (type != null && "HOUR".equalsIgnoreCase(type.getUnitCode())) {
			item.setHourCount(count);
		} else {
			item.setDayCount(count);
		}

		attendanceRecordManageService.insert(item);

		res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMntSelect.do?employeeId=" + employeeId
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
