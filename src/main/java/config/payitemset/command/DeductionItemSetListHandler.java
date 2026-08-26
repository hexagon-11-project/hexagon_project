package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;

/**
 * 공제항목 설정 화면의 목록 데이터를 조회한다.
 * 控除項目設定画面の一覧データを照会する。
 */
public class DeductionItemSetListHandler implements CommandHandler {

	private PayItemSetFormHelper formHelper = new PayItemSetFormHelper();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		formHelper.bindLists(req);
		req.setAttribute("selectedPayItem", null);
		req.setAttribute("selectedDeductionItem", null);
		return PayItemSetFormHelper.VIEW;
	}

}
