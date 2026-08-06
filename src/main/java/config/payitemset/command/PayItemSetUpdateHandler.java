package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.payitemset.model.PayItemModel;
import config.payitemset.service.PayItemSetUpdateService;

public class PayItemSetUpdateHandler implements CommandHandler {

	private PayItemSetUpdateService updateService = new PayItemSetUpdateService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		String payItemIdParam = req.getParameter("payItemId");

		if (payItemIdParam == null || payItemIdParam.isBlank()) {

			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

			return null;

		}

		PayItemModel item = new PayItemModel();

		item.setPayItemId(Integer.parseInt(req.getParameter("payItemId")));
		item.setCompanyId(1001);
		item.setPayItemName(req.getParameter("payItemName"));
		item.setTaxableYn(req.getParameter("taxableYn"));
		item.setCalculationMethod(emptyToNull(req.getParameter("calculationMethod")));
		item.setNonPayAmount(parseLongOrNull(req.getParameter("nonPayAmount")));
		item.setTruncationUnit(parseIntOrDefault(req.getParameter("truncationUnit"), 0));
		item.setAttendancePayRule(emptyToNull(req.getParameter("attendancePayRule")));
		item.setUseYn(req.getParameter("useYn"));
		item.setNonTaxCategory(emptyToNull(req.getParameter("nonTaxCategory")));
		item.setModId("SYSTEM");

		if (item.getCalculationMethod() == null) {
			item.setCalculationMethod("FIXED");
		}

		updateService.update(item);

		res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");

		return null;

	}

	private Long parseLongOrNull(String value) {

		if (value == null || value.isBlank()) {

			return null;

		}

		return Long.parseLong(value.replace(",", ""));

	}

	private int parseIntOrDefault(String value, int defaultValue) {

		if (value == null || value.isBlank()) {

			return defaultValue;

		}

		return Integer.parseInt(value);

	}

	private String emptyToNull(String value) {

		return (value == null || value.isBlank()) ? null : value;

	}

}
