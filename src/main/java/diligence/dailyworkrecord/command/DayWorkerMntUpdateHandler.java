package diligence.dailyworkrecord.command;

import java.math.BigDecimal;
import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.DailyWorkRecord;
import diligence.dailyworkrecord.service.DailyWorkRecordService;

// 일용직 근무기록 수정 - dailyWorkRecordId 있을 때 이쪽으로 옴
// / 日雇労働者の勤務記録修正 - dailyWorkRecordIdがある場合にここへ
public class DayWorkerMntUpdateHandler implements CommandHandler {

	private DailyWorkRecordService dailyWorkRecordService = new DailyWorkRecordService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Diligence/dayWorkerMnt.do");
			return null;
		}

		String employeeIdParam = req.getParameter("employeeId");
		String workSiteName = req.getParameter("workSiteName");

		if (workSiteName == null || workSiteName.isBlank()) {
			res.sendRedirect(req.getContextPath() + "/Diligence/dayWorkerMntSelect.do?employeeId=" + employeeIdParam
					+ "&silent=1");
			return null;
		}

		int dailyWorkRecordId = Integer.parseInt(req.getParameter("dailyWorkRecordId"));

		DailyWorkRecord item = new DailyWorkRecord();
		item.setDailyWorkRecordId(dailyWorkRecordId);
		item.setEmployeeId(Integer.parseInt(employeeIdParam));
		item.setWorkSiteName(workSiteName);
		item.setWorkDate(Date.valueOf(req.getParameter("workDate")));
		item.setDailyWage(parseOrZero(req.getParameter("dailyWage")));
		item.setPayRate(parseOrOne(req.getParameter("payRate")));

		dailyWorkRecordService.update(item);

		res.sendRedirect(req.getContextPath() + "/Diligence/dayWorkerMntSelect.do?employeeId=" + employeeIdParam
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
