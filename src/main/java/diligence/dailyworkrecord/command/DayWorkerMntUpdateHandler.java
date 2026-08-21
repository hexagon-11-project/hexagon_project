package diligence.dailyworkrecord.command;

import java.math.BigDecimal;
import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.DailyWorkRecord;
import diligence.dailyworkrecord.service.DailyWorkRecordService;

// [수정] - 팝업에서 [수정] 눌러서 입력폼에 채워진 내용을 [저장]했을 때 처리 (dailyWorkRecordId가 있으면 이쪽으로 옴)
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
