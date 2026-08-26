package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.DeductionItem;
import config.payitemset.service.DeductionItemSetUpdateService;
import config.payitemset.service.PayItemSetException;

/**
 * 선택한 공제항목을 수정한다.
 * 選択した控除項目を更新する。
 */
public class DeductionItemSetUpdateHandler implements CommandHandler {

	private DeductionItemSetUpdateService updateService = new DeductionItemSetUpdateService();
	private PayItemSetFormHelper formHelper = new PayItemSetFormHelper();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!formHelper.isPost(req)) {
			return formHelper.redirectList(req, res);
		}

		DeductionItem item = null;
		try {
			item = formHelper.buildDeductionItem(req, true, true);
			formHelper.validateDeductionItem(item, true);
			updateService.update(item);
			return formHelper.redirectList(req, res);
		} catch (PayItemSetException e) {
			DeductionItem redisplay = item != null ? item : formHelper.buildDeductionItem(req, true, false);
			return formHelper.errorDeduction(req, e.getMessage(), redisplay);
		} catch (RuntimeException e) {
			DeductionItem redisplay = item != null ? item : formHelper.buildDeductionItem(req, true, false);
			return formHelper.errorDeduction(req, "保存に失敗しました。", redisplay);
		}
	}

}
