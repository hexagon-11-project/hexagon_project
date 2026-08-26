package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.DeductionItem;
import config.payitemset.service.DeductionItemSetDeleteService;
import config.payitemset.service.DeductionItemSetSelectService;
import config.payitemset.service.PayItemSetException;

/**
 * 선택한 공제항목을 삭제한다.
 * 選択した控除項目を削除する。
 */
public class DeductionItemSetDeleteHandler implements CommandHandler {

	private DeductionItemSetDeleteService deleteService = new DeductionItemSetDeleteService();
	private DeductionItemSetSelectService selectService = new DeductionItemSetSelectService();
	private PayItemSetFormHelper formHelper = new PayItemSetFormHelper();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!formHelper.isPost(req)) {
			return formHelper.redirectList(req, res);
		}

		try {
			int deductionItemId = formHelper.parseRequiredId(req.getParameter("deductionItemId"),
					"削除する項目をリストから選択してください。");
			deleteService.delete(deductionItemId);
			return formHelper.redirectList(req, res);
		} catch (PayItemSetException e) {
			return formHelper.errorDeduction(req, e.getMessage(), selectedOrNull(req));
		} catch (RuntimeException e) {
			return formHelper.errorDeduction(req, "保存に失敗しました。", selectedOrNull(req));
		}
	}

	private DeductionItem selectedOrNull(HttpServletRequest req) {
		Integer deductionItemId = formHelper.parseIdOrNull(req.getParameter("deductionItemId"));
		if (deductionItemId == null) {
			return null;
		}
		try {
			return selectService.getById(deductionItemId);
		} catch (RuntimeException e) {
			return null;
		}
	}

}
