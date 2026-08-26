package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.DeductionItem;
import config.payitemset.service.DeductionItemSetSelectService;
import config.payitemset.service.PayItemSetException;

public class DeductionItemSetSelectHandler implements CommandHandler {

	private DeductionItemSetSelectService selectService = new DeductionItemSetSelectService();
	private PayItemSetFormHelper formHelper = new PayItemSetFormHelper();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		try {
			int deductionItemId = formHelper.parseRequiredId(req.getParameter("deductionItemId"),
					"対象の控除項目が見つかりません。");
			DeductionItem selected = selectService.getById(deductionItemId);
			if (selected == null) {
				return formHelper.errorDeduction(req, "対象の控除項目が見つかりません。", null);
			}
			formHelper.bindLists(req);
			req.setAttribute("selectedPayItem", null);
			req.setAttribute("selectedDeductionItem", selected);
			return PayItemSetFormHelper.VIEW;
		} catch (PayItemSetException e) {
			return formHelper.errorDeduction(req, e.getMessage(), null);
		} catch (RuntimeException e) {
			return formHelper.errorDeduction(req, "照会に失敗しました。", null);
		}
	}

}
