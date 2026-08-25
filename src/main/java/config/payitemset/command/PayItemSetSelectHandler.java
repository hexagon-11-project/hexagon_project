package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.dnLItemSet.service.AttendanceTypeListService;
import config.payitemset.service.DeductionItemSetListService;
import config.payitemset.service.PayItemSetListService;
import config.payitemset.service.PayItemSetSelectService;

/**
 * 선택한 지급항목을 조회해 편집 폼에 채운다.
 * 選択した支給項目を照会し編集フォームに埋める。
 */
public class PayItemSetSelectHandler implements CommandHandler {

	private PayItemSetListService listService = new PayItemSetListService();
	private PayItemSetSelectService selectService = new PayItemSetSelectService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();
	private DeductionItemSetListService deductionItemSetListService = new DeductionItemSetListService();

	/**
	 * 목록과 선택 지급항목을 담아 설정 화면으로 forward 한다.
	 * 一覧と選択支給項目を入れて設定画面へforwardする。
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// 조회 기준 회사 ID 와 선택 PK 를 준비한다
		// 照会基準の会社IDと選択PKを用意する。
		int companyId = 1001;
		int payItemId = Integer.parseInt(req.getParameter("payItemId"));

		// 화면 키를 ListHandler 와 같게 맞춘다.
		// 画面キーをListHandlerと同じにする。
		req.setAttribute("payItemList", listService.getList(companyId));
		req.setAttribute("attendanceTypeList", attendanceTypeListService.getList(companyId));
		req.setAttribute("deductionItemList", deductionItemSetListService.getList(companyId));
		// 선택 지급항목만 채운다.
		// 選択支給項目だけを埋める。
		req.setAttribute("selectedPayItem", selectService.getById(payItemId));
		req.setAttribute("selectedDeductionItem", null);

		// JSP 경로를 반환한다.
		// JSPパスを返す。
		return "/WEB-INF/pages/config/payItemSet.jsp";
	}

}
