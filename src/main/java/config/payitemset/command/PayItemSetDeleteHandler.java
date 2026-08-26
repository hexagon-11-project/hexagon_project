package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.PayItem;
import config.payitemset.service.PayItemSetDeleteService;
import config.payitemset.service.PayItemSetException;
import config.payitemset.service.PayItemSetSelectService;

/**
 * 선택한 지급항목을 삭제한다.
 * 選択した支給項目を削除する。
 */
public class PayItemSetDeleteHandler implements CommandHandler {

	private PayItemSetDeleteService deleteService = new PayItemSetDeleteService();
	private PayItemSetSelectService selectService = new PayItemSetSelectService();
	private PayItemSetFormHelper formHelper = new PayItemSetFormHelper();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!formHelper.isPost(req)) {
			return formHelper.redirectList(req, res);
		}

		try {
			int payItemId = formHelper.parseRequiredId(req.getParameter("payItemId"),
					"削除する項目をリストから選択してください。");
			deleteService.delete(payItemId);
			return formHelper.redirectList(req, res);
		} catch (PayItemSetException e) {
			return formHelper.errorPay(req, e.getMessage(), selectedOrNull(req));
		} catch (RuntimeException e) {
			return formHelper.errorPay(req, "保存に失敗しました。", selectedOrNull(req));
		}
	}

	private PayItem selectedOrNull(HttpServletRequest req) {
		Integer payItemId = formHelper.parseIdOrNull(req.getParameter("payItemId"));
		if (payItemId == null) {
			return null;
		}
		try {
			return selectService.getById(payItemId);
		} catch (RuntimeException e) {
			return null;
		}
	}

}
