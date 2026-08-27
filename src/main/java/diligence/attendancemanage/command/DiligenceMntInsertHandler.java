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

// 근태기록 저장 - 단위(일/시간)에 따라 DAY_COUNT or HOUR_COUNT에 넣음
// / 勤怠記録保存 - 単位(日/時間)に応じてDAY_COUNTかHOUR_COUNTに格納
// 드롭다운에서 휴가항목 선택 시 매핑되는 근태항목 자동 생성
// / 休暇項目選択時は対応する勤怠項目を自動生成
public class DiligenceMntInsertHandler implements CommandHandler {

	private static final String LEAVE_PREFIX = "leave-";

	private AttendanceRecordManageService attendanceRecordManageService = new AttendanceRecordManageService();
	private AttendanceTypeSelectService attendanceTypeSelectService = new AttendanceTypeSelectService();
	private AttendanceTypeInsertService attendanceTypeInsertService = new AttendanceTypeInsertService();
	private LeaveTypeSelectService leaveTypeSelectService = new LeaveTypeSelectService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// 비정상적인 접근(GET 방식 등) 방지 - POST 요청이 아니면 메인 근태관리 화면으로 리다이렉트
		// 不正なアクセス（GET方式など）の防止 - POSTリクエストでなければメイン勤怠管理画面へリダイレクト
		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMnt.do");
			return null;
		}

		String employeeIdParam = req.getParameter("employeeId");
		String employeeIdsParam = req.getParameter("employeeIds"); // 여러 명 체크 시 콤마 구분 / 複数選択時はカンマ区切り
		String attendanceTypeIdParam = req.getParameter("attendanceTypeId");
		String startDateParam = req.getParameter("startDate");
		String endDateParam = req.getParameter("endDate");
		String countParam = req.getParameter("count");

		// 필수 파라미터가 누락된 경우 대상 사원의 조회 화면으로 우회(리다이렉트)
		// 必須パラメータが漏れている場合、対象社員の照会画面へ迂回（リダイレクト）
		if (isBlank(attendanceTypeIdParam) || isBlank(startDateParam) || isBlank(endDateParam)) {
			res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMntSelect.do?employeeId=" + employeeIdParam);
			return null;
		}

		// employeeIds 있으면 다중, 없으면 단일 처리 / employeeIdsがあれば複数、なければ単一
		String[] employeeIds;
		if (!isBlank(employeeIdsParam)) {
			employeeIds = employeeIdsParam.split(",");
		} else if (!isBlank(employeeIdParam)) {
			employeeIds = new String[]{employeeIdParam};
		} else {
			res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMnt.do");
			return null;
		}

		int attendanceTypeId = resolveAttendanceTypeId(attendanceTypeIdParam);
		AttendanceType type = attendanceTypeSelectService.getById(attendanceTypeId);
		BigDecimal count = parseOrNull(countParam);

		// 선택된 사원 전원에 INSERT / 選択した全員にINSERT
		for (String idStr : employeeIds) {
			if (isBlank(idStr)) continue;
			int employeeId = Integer.parseInt(idStr.trim());

			AttendanceRecord item = new AttendanceRecord();
			item.setEmployeeId(employeeId);
			item.setAttendanceTypeId(attendanceTypeId);
			item.setStartDate(java.sql.Date.valueOf(startDateParam));
			item.setEndDate(java.sql.Date.valueOf(endDateParam));
			item.setDescription(req.getParameter("description"));
			item.setAllowanceAmount(parseOrNull(req.getParameter("amount")));

			// 시간 단위면 HOUR_COUNT 그 외엔 DAY_COUNT / 時間単位ならHOUR_COUNT、それ以外はDAY_COUNT
			if (type != null && "HOUR".equalsIgnoreCase(type.getUnitCode())) {
				item.setHourCount(count);
			} else {
				item.setDayCount(count);
			}

			attendanceRecordManageService.insert(item);
		}

		// 저장 후 첫 번째 사원 화면으로 이동 / 保存後は先頭の社員画面へ
		String firstId = employeeIds[0].trim();
		res.sendRedirect(req.getContextPath() + "/Diligence/diligenceMntSelect.do?employeeId=" + firstId
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