package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.payitemset.service.NonTaxDetailListService;

/**
 * 비과세 상세 선택 팝업을 연다.
 * 非課税詳細選択ポップアップを開く。
 */
public class NonTaxDetailPopupHandler implements CommandHandler {

	private NonTaxDetailListService listService = new NonTaxDetailListService();

	/**
	 * 비과세 코드 목록을 담아 팝업 JSP 경로를 반환한다.
	 * 非課税コード一覧を入れてポップアップJSPパスを返す。
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// 조회 기준 회사 ID 를 정한다
		// 照会基準の会社IDを決める。
		int companyId = 1001;

		// 팝업 테이블에 쓸 목록을 바인딩한다.
		// ポップアップテーブルに使う一覧をバインドする。
		req.setAttribute("nonTaxDetailList", listService.getList(companyId));

		// 팝업 JSP 경로를 반환한다.
		// ポップアップJSPパスを返す。
		return "/WEB-INF/pages/config/nonTaxDetailPopup.jsp";
	}

}
