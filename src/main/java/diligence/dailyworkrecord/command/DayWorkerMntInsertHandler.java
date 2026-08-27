package diligence.dailyworkrecord.command;

import java.math.BigDecimal;
import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.DailyWorkRecord;
import diligence.dailyworkrecord.service.DailyWorkRecordService;

// 일용직 근무기록 저장 - 소득세/지방소득세/실지급액은 서버에서 계산
// / 日雇労働者の勤務記録保存 - 所得税/地方所得税/実支給額はサーバーで計算
public class DayWorkerMntInsertHandler implements CommandHandler {

	private DailyWorkRecordService dailyWorkRecordService = new DailyWorkRecordService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Diligence/dayWorkerMnt.do");
			return null;
		}

		String employeeIdParam = req.getParameter("employeeId");
		String employeeIdsParam = req.getParameter("employeeIds"); // 여러 명 체크 시 콤마 구분 / 複数選択時はカンマ区切り
		String workSiteName = req.getParameter("workSiteName");

		if (workSiteName == null || workSiteName.isBlank()) {
			res.sendRedirect(req.getContextPath() + "/Diligence/dayWorkerMntSelect.do?employeeId=" + employeeIdParam
					+ "&silent=1");
			return null;
		}

		// employeeIds 있으면 다중, 없으면 단일 처리 / employeeIdsがあれば複数、なければ単一
		String[] employeeIds;
		if (employeeIdsParam != null && !employeeIdsParam.isBlank()) {
			employeeIds = employeeIdsParam.split(",");
		} else if (employeeIdParam != null && !employeeIdParam.isBlank()) {
			employeeIds = new String[]{employeeIdParam};
		} else {
			res.sendRedirect(req.getContextPath() + "/Diligence/dayWorkerMnt.do");
			return null;
		}

		// 선택된 사원 전원에 INSERT / 選択した全員にINSERT
		for (String idStr : employeeIds) {
			if (idStr == null || idStr.isBlank()) continue;
			int employeeId = Integer.parseInt(idStr.trim());

			DailyWorkRecord item = new DailyWorkRecord();
			item.setEmployeeId(employeeId);
			item.setWorkSiteName(workSiteName);
			item.setWorkDate(Date.valueOf(req.getParameter("workDate")));
			item.setDailyWage(parseOrZero(req.getParameter("dailyWage")));
			item.setPayRate(parseOrOne(req.getParameter("payRate")));

			dailyWorkRecordService.insert(item);
		}

		// 저장 후 첫 번째 사원 화면으로 이동 / 保存後は先頭の社員画面へ
		String firstId = employeeIds[0].trim();
		res.sendRedirect(req.getContextPath() + "/Diligence/dayWorkerMntSelect.do?employeeId=" + firstId
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
