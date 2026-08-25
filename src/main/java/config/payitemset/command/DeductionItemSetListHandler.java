package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeListService;
import config.payitemset.service.DeductionItemSetListService;
import config.payitemset.service.PayItemSetListService;

/**
 * 공제항목 설정 화면의 목록 데이터를 조회한다.
 * 控除項目設定画面の一覧データを照会する。
 */
public class DeductionItemSetListHandler implements CommandHandler {

	private PayItemSetListService payItemListService = new PayItemSetListService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();
	private DeductionItemSetListService listService = new DeductionItemSetListService();

	/**
	 * 목록을 request 에 담고 설정 JSP 경로를 반환한다.
	 * 一覧をrequestに入れて設定JSPパスを返す。
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// 조회 기준 회사 ID 를 정한다
		// 照会基準の会社IDを決める。
		int companyId = 1001;

		// 화면 키를 지급 목록 핸들러와 같게 맞춘다.
		// 画面キーを支給一覧ハンドラと同じにする。
		req.setAttribute("payItemList", payItemListService.getList(companyId));
		req.setAttribute("attendanceTypeList", attendanceTypeListService.getList(companyId));
		req.setAttribute("deductionItemList", listService.getList(companyId));
		req.setAttribute("selectedPayItem", null);
		req.setAttribute("selectedDeductionItem", null);

		// JSP 경로를 반환한다.
		// JSPパスを返す。
		return "/WEB-INF/pages/config/payItemSet.jsp";
	}

}
