package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.payitemset.service.DeductionItemSetDeleteService;

/**
 * 선택한 공제항목을 삭제한다.
 * 選択した控除項目を削除する。
 */
public class DeductionItemSetDeleteHandler implements CommandHandler {

	private DeductionItemSetDeleteService deleteService = new DeductionItemSetDeleteService();

	/**
	 * 공제항목 PK 를 받아 삭제한다.
	 * 控除項目PKを受けて削除する。
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// POST 가 아니면 삭제하지 않는다
		// POSTでなければ削除しない。
		if (!"POST".equalsIgnoreCase(req.getMethod())) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		String id = req.getParameter("deductionItemId");

		// 삭제 대상 PK 가 있어야 한다
		// 削除対象PKがなければならない。
		if (id == null || id.isBlank()) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		deleteService.delete(Integer.parseInt(id));

		// 지급 목록 URI 로 redirect 한다.
		// 支給一覧URIへredirectする。
		res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

		return null;

	}

}
