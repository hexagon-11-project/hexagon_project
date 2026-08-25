package statistics.paymentstatisticspayitems.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import statistics.paymentstatisticspayitems.service.PaymentStatisticsPayItemsService;

/**
 * 사원별 급여 항목 통계의 사원 선택 팝업 핸들러.
 * 社員別給与項目統計の社員選択ポップアップハンドラ。
 *
 */
public class PaymentStatisticsPayItemsEmployeePopupHandler implements CommandHandler {

	// 사원 선택 팝업 JSP 경로
	// 社員選択ポップアップJSPパス。
	private static final String POPUP_VIEW = "/WEB-INF/pages/statistics/paymentstatisticspayitems_employee_popup.jsp";
	// 기본 회사 ID
	// 既定の会社ID。
	private static final int DEFAULT_COMPANY_ID = 1001;

	private PaymentStatisticsPayItemsService paymentStatisticsPayItemsService = new PaymentStatisticsPayItemsService();

	/**
	 * 필터 조건으로 사원 목록과 부서/상태 콤보를 조회한다.
	 * フィルタ条件で社員一覧と部署/状態コンボを照会する。
	 *
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String empName = trimToNull(req.getParameter("empName"));
		String department = trimToNull(req.getParameter("department"));
		String status = trimToNull(req.getParameter("status"));

		req.setAttribute("employeeList",
				paymentStatisticsPayItemsService.getEmployeeList(DEFAULT_COMPANY_ID, empName, department, status));
		req.setAttribute("deptList", paymentStatisticsPayItemsService.getDepartmentList());
		req.setAttribute("statusList", paymentStatisticsPayItemsService.getStatusList());
		req.setAttribute("empName", empName == null ? "" : empName);
		req.setAttribute("selectedDept", department == null ? "" : department);
		req.setAttribute("selectedStatus", status == null ? "" : status);
		return POPUP_VIEW;
	}

	/**
	 * 빈 문자열을 null로 바꾼다.
	 * 空文字列をnullに変える。
	 *
	 */
	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
