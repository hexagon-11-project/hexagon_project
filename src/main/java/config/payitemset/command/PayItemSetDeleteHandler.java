package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.payitemset.service.PayItemSetDeleteService;

/**
 * 선택한 지급항목을 삭제한다.
 * 選択した支給項目を削除する。
 */
public class PayItemSetDeleteHandler implements CommandHandler {

	private PayItemSetDeleteService deleteService = new PayItemSetDeleteService();

	/**
	 * 지급항목 PK 를 받아 삭제한다.
	 * 支給項目PKを受けて削除する。
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// POST 가 아니면 삭제하지 않는다
		// POSTでなければ削除しない。
		if (!"POST".equalsIgnoreCase(req.getMethod())) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		String payItemIdParam = req.getParameter("payItemId");

		// 삭제 대상 PK 가 있어야 한다
		// 削除対象PKがなければならない。
		if (payItemIdParam == null || payItemIdParam.isBlank()) {
			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		int payItemId = Integer.parseInt(req.getParameter("payItemId"));

		deleteService.delete(payItemId);

		// 목록으로 redirect 한다.
		// 一覧へredirectする。
		res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

		return null;

	}

}
