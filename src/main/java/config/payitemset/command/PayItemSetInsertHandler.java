package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.PayItem;
import config.payitemset.service.PayItemSetException;
import config.payitemset.service.PayItemSetInsertService;

/**
 * 지급항목을 신규 등록한다.
 * 支給項目を新規登録する。
 */
public class PayItemSetInsertHandler implements CommandHandler {

	private PayItemSetInsertService insertService = new PayItemSetInsertService();
	private PayItemSetFormHelper formHelper = new PayItemSetFormHelper();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!formHelper.isPost(req)) {
			return formHelper.redirectList(req, res);
		}

		PayItem item = null;
		try {
			item = formHelper.buildPayItem(req, false, true);
			formHelper.validatePayItem(item, false);
			insertService.insert(item);
			return formHelper.redirectList(req, res);
		} catch (PayItemSetException e) {
			PayItem redisplay = item != null ? item : formHelper.buildPayItem(req, false, false);
			return formHelper.errorPay(req, e.getMessage(), redisplay);
		} catch (RuntimeException e) {
			PayItem redisplay = item != null ? item : formHelper.buildPayItem(req, false, false);
			return formHelper.errorPay(req, "保存に失敗しました。", redisplay);
		}
	}

}
