package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.PayItem;
import config.payitemset.service.PayItemSetException;
import config.payitemset.service.PayItemSetSelectService;

/**
 * 선택한 지급항목을 조회해 편집 폼에 채운다.
 * 選択した支給項目を照会し編集フォームに埋める。
 */
public class PayItemSetSelectHandler implements CommandHandler {

	private PayItemSetSelectService selectService = new PayItemSetSelectService();
	private PayItemSetFormHelper formHelper = new PayItemSetFormHelper();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		try {
			int payItemId = formHelper.parseRequiredId(req.getParameter("payItemId"),
					"対象の支給項目が見つかりません。");
			PayItem selected = selectService.getById(payItemId);
			if (selected == null) {
				return formHelper.errorPay(req, "対象の支給項目が見つかりません。", null);
			}
			formHelper.bindLists(req);
			req.setAttribute("selectedPayItem", selected);
			req.setAttribute("selectedDeductionItem", null);
			return PayItemSetFormHelper.VIEW;
		} catch (PayItemSetException e) {
			return formHelper.errorPay(req, e.getMessage(), null);
		} catch (RuntimeException e) {
			return formHelper.errorPay(req, "照会に失敗しました。", null);
		}
	}

}
