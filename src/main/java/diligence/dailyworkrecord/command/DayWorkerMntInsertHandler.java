package diligence.dailyworkrecord.command;

import java.math.BigDecimal;
import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.DailyWorkRecord;
import diligence.dailyworkrecord.service.DailyWorkRecordService;

// [저장] - 일용직 근무기록 한 건 등록. 소득세/지방소득세/실지급액은 항상 서버에서 계산(클라이언트 값 신뢰 안 함)
public class DayWorkerMntInsertHandler implements CommandHandler {

	private DailyWorkRecordService dailyWorkRecordService = new DailyWorkRecordService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Diligence/dayWorkerMnt.do");
			return null;
		}

		String employeeIdParam = req.getParameter("employeeId");
		String workSiteName = req.getParameter("workSiteName");

		if (employeeIdParam == null || employeeIdParam.isBlank()) {
			res.sendRedirect(req.getContextPath() + "/Diligence/dayWorkerMnt.do");
			return null;
		}

		if (workSiteName == null || workSiteName.isBlank()) {
			res.sendRedirect(req.getContextPath() + "/Diligence/dayWorkerMntSelect.do?employeeId=" + employeeIdParam
					+ "&silent=1");
			return null;
		}

		int employeeId = Integer.parseInt(employeeIdParam);

		DailyWorkRecord item = new DailyWorkRecord();
		item.setEmployeeId(employeeId);
		item.setWorkSiteName(workSiteName);
		item.setWorkDate(Date.valueOf(req.getParameter("workDate")));
		item.setDailyWage(parseOrZero(req.getParameter("dailyWage")));
		item.setPayRate(parseOrOne(req.getParameter("payRate")));

		dailyWorkRecordService.insert(item);

		res.sendRedirect(req.getContextPath() + "/Diligence/dayWorkerMntSelect.do?employeeId=" + employeeId
				+ "&silent=1&saved=1");
		return null;
	}

	private BigDecimal parseOrZero(String value) {
		if (value == null || value.isBlank()) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(value.trim());
	}

	private BigDecimal parseOrOne(String value) {
		if (value == null || value.isBlank()) {
			return BigDecimal.ONE;
		}
		return new BigDecimal(value.trim());
	}
}
