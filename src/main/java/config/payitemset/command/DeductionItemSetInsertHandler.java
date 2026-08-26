package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.DeductionItem;
import config.payitemset.service.DeductionItemSetInsertService;
import config.payitemset.service.PayItemSetException;

/**
 * 공제항목을 신규 등록한다.
 * 控除項目を新規登録する。
 */
public class DeductionItemSetInsertHandler implements CommandHandler {

	private DeductionItemSetInsertService insertService = new DeductionItemSetInsertService();
	private PayItemSetFormHelper formHelper = new PayItemSetFormHelper();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!formHelper.isPost(req)) {
			return formHelper.redirectList(req, res);
		}

		DeductionItem item = null;
		try {
			item = formHelper.buildDeductionItem(req, false, true);
			formHelper.validateDeductionItem(item, false);
			insertService.insert(item);
			return formHelper.redirectList(req, res);
		} catch (PayItemSetException e) {
			DeductionItem redisplay = item != null ? item : formHelper.buildDeductionItem(req, false, false);
			return formHelper.errorDeduction(req, e.getMessage(), redisplay);
		} catch (RuntimeException e) {
			DeductionItem redisplay = item != null ? item : formHelper.buildDeductionItem(req, false, false);
			return formHelper.errorDeduction(req, "保存に失敗しました。", redisplay);
		}
	}

}
