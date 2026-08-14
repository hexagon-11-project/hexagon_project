package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.DeductionItem;
import config.payitemset.service.DeductionItemSetUpdateService;

public class DeductionItemSetUpdateHandler implements CommandHandler {

	private DeductionItemSetUpdateService updateService = new DeductionItemSetUpdateService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		String id = req.getParameter("deductionItemId");

		if (id == null || id.isBlank()) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		DeductionItem item = new DeductionItem();

		item.setDeductionItemId(Integer.parseInt(id));
		item.setCompanyId(1001);
		item.setDeductionItemName(req.getParameter("deductionItemName"));
		item.setCalculationMethod(emptyToNull(req.getParameter("deductionCalculationMethod")));
		if (item.getCalculationMethod() == null) {
			item.setCalculationMethod("FIXED");
		}
		item.setTruncationUnit(parseIntOrDefault(req.getParameter("deductionTruncationUnit"), 0));
		item.setRemark(emptyToNull(req.getParameter("remark")));
		item.setUseYn(req.getParameter("deductionUseYn"));
		item.setModId("SYSTEM");

		updateService.update(item);

		res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

		return null;

	}

	private int parseIntOrDefault(String value, int defaultValue) {

		if (value == null || value.trim().isEmpty()) {

			return defaultValue;

		}

		return Integer.parseInt(value.trim());

	}

	private String emptyToNull(String value) {

		return (value == null || value.trim().isEmpty()) ? null : value.trim();

	}

}
