package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.PayItem;
import config.payitemset.service.PayItemSetException;
import config.payitemset.service.PayItemSetUpdateService;

/**
 * 선택한 지급항목을 수정한다.
 * 選択した支給項目を更新する。
 */
public class PayItemSetUpdateHandler implements CommandHandler {

	private PayItemSetUpdateService updateService = new PayItemSetUpdateService();
	private PayItemSetFormHelper formHelper = new PayItemSetFormHelper();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!formHelper.isPost(req)) {
			return formHelper.redirectList(req, res);
		}

		PayItem item = null;
		try {
			item = formHelper.buildPayItem(req, true, true);
			formHelper.validatePayItem(item, true);
			updateService.update(item);
			return formHelper.redirectList(req, res);
		} catch (PayItemSetException e) {
			PayItem redisplay = item != null ? item : formHelper.buildPayItem(req, true, false);
			return formHelper.errorPay(req, e.getMessage(), redisplay);
		} catch (RuntimeException e) {
			PayItem redisplay = item != null ? item : formHelper.buildPayItem(req, true, false);
			return formHelper.errorPay(req, "保存に失敗しました。", redisplay);
		}
	}

}
