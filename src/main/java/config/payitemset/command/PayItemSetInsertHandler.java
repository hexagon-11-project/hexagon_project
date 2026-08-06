package config.payitemset.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.payitemset.model.PayItemModel;
import config.payitemset.service.PayItemSetInsertService;

public class PayItemSetInsertHandler implements CommandHandler {

	private PayItemSetInsertService insertService = new PayItemSetInsertService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");
			return null;
		}

		PayItemModel item = new PayItemModel();

		// JSP에서 입력받는 값
		item.setPayItemName(req.getParameter("payItemName"));
		item.setTaxableYn(req.getParameter("taxableYn")); // Y / N
		item.setCalculationMethod(emptyToNull(req.getParameter("calculationMethod")));
		item.setTruncationUnit(parseIntOrDefault(req.getParameter("truncationUnit"), 0));
		item.setAttendancePayRule(emptyToNull(req.getParameter("attendancePayRule")));
		item.setUseYn(req.getParameter("useYn")); // Y / N
		item.setNonPayAmount(parseLongOrNull(req.getParameter("nonPayAmount")));

		// JSP에서 입력받지 않는 값 (기본 설정)
		// PAY_ITEM_ID: DAO에서 PAY_ITEM_SEQ.NEXTVAL
		item.setCompanyId(1001);
		if (item.getCalculationMethod() == null) {
			item.setCalculationMethod("FIXED");
		}
		item.setBulkPayAmount(null);
		item.setDisplayOrder(0);
		item.setRegId("SYSTEM");
		item.setModId("SYSTEM");
		// CREATED_AT / UPDATED_AT: DAO에서 SYSDATE
		item.setNonTaxId(null);
		// NON_PAY_AMOUT: 위에서 JSP 값 사용 (없으면 null)

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

	private String emptyToNull(String value) {
		return (value == null || value.trim().isEmpty()) ? null : value;
	}

}
