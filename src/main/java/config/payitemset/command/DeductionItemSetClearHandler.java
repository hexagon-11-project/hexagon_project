package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;

/**
 * 공제항목 편집 폼을 비운다.
 * 控除項目編集フォームを空にする。
 */
public class DeductionItemSetClearHandler implements CommandHandler {

	/**
	 * 목록 화면으로 리다이렉트한다.
	 * 一覧画面へリダイレクトする。
	 */
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// 지급 목록 URI 로 redirect 한다.
		// 支給一覧URIへredirectする。
		res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

		// FrontController 가 JSP 를 열지 않게 null 을 반환한다.
		// FrontControllerがJSPを開かないようnullを返す。
		return null;

	}

}
