package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.model.PayItem;
import config.payitemset.service.PayItemSetInsertService;

public class PayItemSetInsertHandler implements CommandHandler {

	private PayItemSetInsertService insertService = new PayItemSetInsertService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");
			return null;
		}

		String payItemName = req.getParameter("payItemName");
		if (payItemName == null || payItemName.trim().isEmpty()) {
			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");
			return null;
		}

		PayItem item = new PayItem();

		item.setPayItemName(payItemName.trim());
		item.setTaxableYn(req.getParameter("taxableYn"));
		item.setCalculationMethod(emptyToNull(req.getParameter("calculationMethod")));
		item.setTruncationUnit(parseIntOrDefault(req.getParameter("truncationUnit"), 0));
		item.setAttendancePayRule(emptyToNull(req.getParameter("attendancePayRule")));
		item.setUseYn(req.getParameter("useYn"));
		item.setNonPayAmount(parseLongOrNull(req.getParameter("nonPayAmount")));
		item.setNonTaxCategory(emptyToNull(req.getParameter("nonTaxCategory")));

		item.setCompanyId(1001);
		if (item.getCalculationMethod() == null) {
			item.setCalculationMethod("FIXED");
		}

		if ("일괄지급".equals(item.getAttendancePayRule())) {
			item.setBulkPayAmount(parseLongOrNull(req.getParameter("bulkPayAmount")));
		} else {
			item.setBulkPayAmount(null);
		}

		item.setDisplayOrder(0);
		item.setRegId("SYSTEM");
		item.setModId("SYSTEM");

		if ("N".equalsIgnoreCase(item.getTaxableYn())) {
			item.setNonTaxId(parseIntOrNull(req.getParameter("nonTaxId")));
		} else {
			item.setNonTaxId(null);
			item.setNonPayAmount(null);
			item.setNonTaxCategory(null);
		}

		insertService.insert(item);
		res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");
		return null;
	}

	private Long parseLongOrNull(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		return Long.parseLong(value.replace(",", ""));
	}

	private int parseIntOrDefault(String value, int defaultValue) {
		if (value == null || value.trim().isEmpty()) {
			return defaultValue;
		}
		return Integer.parseInt(value);
	}

	private Integer parseIntOrNull(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		return Integer.parseInt(value.trim());
	}

	private String emptyToNull(String value) {
		return (value == null || value.trim().isEmpty()) ? null : value;
	}

}
