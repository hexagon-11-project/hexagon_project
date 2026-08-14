package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.DeductionItem;
import config.payitemset.service.DeductionItemSetInsertService;

public class DeductionItemSetInsertHandler implements CommandHandler {

	private DeductionItemSetInsertService insertService = new DeductionItemSetInsertService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		String name = req.getParameter("deductionItemName");

		if (name == null || name.trim().isEmpty()) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		DeductionItem item = new DeductionItem();

		item.setCompanyId(1001);
		item.setDeductionItemName(name.trim());
		item.setCalculationMethod(emptyToNull(req.getParameter("deductionCalculationMethod")));
		if (item.getCalculationMethod() == null) {
			item.setCalculationMethod("FIXED");
		}
		item.setTruncationUnit(parseIntOrDefault(req.getParameter("deductionTruncationUnit"), 0));
		item.setRemark(emptyToNull(req.getParameter("remark")));
		item.setUseYn(req.getParameter("deductionUseYn"));
		item.setDisplayOrder(0);
		item.setRegId("SYSTEM");
		item.setModId("SYSTEM");

		insertService.insert(item);

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
