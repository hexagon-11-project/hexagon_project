package config.payitemset.command;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import config.dnLItemSet.service.AttendanceTypeListService;
import config.model.DeductionItem;
import config.model.PayItem;
import config.payitemset.service.DeductionItemSetListService;
import config.payitemset.service.PayItemSetException;
import config.payitemset.service.PayItemSetListService;

/**
 * 지급·공제항목 설정 화면의 폼 파싱·검증·오류 표시를 맡는다.
 * 支給・控除項目設定画面のフォーム解析・検証・エラー表示を担う。
 */
public class PayItemSetFormHelper {

	public static final int COMPANY_ID = 1001;
	public static final String VIEW = "/WEB-INF/pages/config/payItemSet.jsp";

	private PayItemSetListService payItemListService = new PayItemSetListService();
	private AttendanceTypeListService attendanceTypeListService = new AttendanceTypeListService();
	private DeductionItemSetListService deductionItemListService = new DeductionItemSetListService();

	public void bindLists(HttpServletRequest req) {
		req.setAttribute("payItemList", payItemListService.getList(COMPANY_ID));
		req.setAttribute("attendanceTypeList", attendanceTypeListService.getList(COMPANY_ID));
		req.setAttribute("deductionItemList", deductionItemListService.getList(COMPANY_ID));
	}

	public String redirectList(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.sendRedirect(req.getContextPath() + "/Config/payitemsetlist.do");
		return null;
	}

	public String errorPay(HttpServletRequest req, String message, PayItem selected) {
		bindLists(req);
		req.setAttribute("selectedPayItem", selected);
		req.setAttribute("selectedDeductionItem", null);
		req.setAttribute("errorMessage", message);
		return VIEW;
	}

	public String errorDeduction(HttpServletRequest req, String message, DeductionItem selected) {
		bindLists(req);
		req.setAttribute("selectedPayItem", null);
		req.setAttribute("selectedDeductionItem", selected);
		req.setAttribute("errorMessage", message);
		return VIEW;
	}

	public boolean isPost(HttpServletRequest req) {
		return "POST".equalsIgnoreCase(req.getMethod());
	}

	public PayItem buildPayItem(HttpServletRequest req, boolean forUpdate, boolean strict) {
		PayItem item = new PayItem();
		item.setCompanyId(COMPANY_ID);

		String idParam = req.getParameter("payItemId");
		if (forUpdate) {
			if (idParam == null || idParam.trim().isEmpty()) {
				if (strict) {
					throw new PayItemSetException("修正する項目をリストから選択してください。");
				}
			} else {
				Integer payItemId = parseInt(idParam, "対象の支給項目が見つかりません。", strict);
				if (payItemId != null) {
					item.setPayItemId(payItemId);
				}
			}
		}

		String payItemName = req.getParameter("payItemName");
		item.setPayItemName(payItemName == null ? null : payItemName.trim());
		item.setTaxableYn(defaultYn(req.getParameter("taxableYn")));
		item.setCalculationMethod(emptyToNull(req.getParameter("calculationMethod")));
		item.setTruncationUnit(parseIntOrDefault(req.getParameter("truncationUnit"), 0, strict));
		item.setAttendancePayRule(emptyToNull(req.getParameter("attendancePayRule")));
		item.setUseYn(defaultYn(req.getParameter("useYn")));
		item.setNonPayAmount(parseLongOrNull(req.getParameter("nonPayAmount"), strict));
		item.setNonTaxCategory(emptyToNull(req.getParameter("nonTaxCategory")));
		item.setModId("SYSTEM");

		if (item.getCalculationMethod() == null) {
			item.setCalculationMethod("FIXED");
		}

		if ("일괄지급".equals(item.getAttendancePayRule())) {
			item.setBulkPayAmount(parseLongOrNull(req.getParameter("bulkPayAmount"), strict));
		} else {
			item.setBulkPayAmount(null);
		}

		if ("N".equalsIgnoreCase(item.getTaxableYn())) {
			item.setNonTaxId(parseInt(req.getParameter("nonTaxId"), "非課税項目の選択が正しくありません。", strict));
		} else {
			item.setNonTaxId(null);
			item.setNonPayAmount(null);
			item.setNonTaxCategory(null);
		}

		if (!forUpdate) {
			item.setDisplayOrder(0);
			item.setRegId("SYSTEM");
		}

		return item;
	}

	public void validatePayItem(PayItem item, boolean forUpdate) {
		if (forUpdate && item.getPayItemId() == null) {
			throw new PayItemSetException("修正する項目をリストから選択してください。");
		}
		if (item.getPayItemName() == null || item.getPayItemName().isEmpty()) {
			throw new PayItemSetException("支給項目を入力してください。");
		}
		if ("N".equalsIgnoreCase(item.getTaxableYn())) {
			if (item.getNonTaxCategory() == null || item.getNonTaxCategory().trim().isEmpty()) {
				throw new PayItemSetException("非課税項目名を入力してください。");
			}
			if (item.getNonPayAmount() == null) {
				throw new PayItemSetException("非課税限度額を入力してください。");
			}
		}
		if ("일괄지급".equals(item.getAttendancePayRule()) && item.getBulkPayAmount() == null) {
			throw new PayItemSetException("一括支給額を入力してください。");
		}
	}

	public DeductionItem buildDeductionItem(HttpServletRequest req, boolean forUpdate, boolean strict) {
		DeductionItem item = new DeductionItem();
		item.setCompanyId(COMPANY_ID);

		String idParam = req.getParameter("deductionItemId");
		if (forUpdate) {
			if (idParam == null || idParam.trim().isEmpty()) {
				if (strict) {
					throw new PayItemSetException("修正する項目をリストから選択してください。");
				}
			} else {
				Integer deductionItemId = parseInt(idParam, "対象の控除項目が見つかりません。", strict);
				if (deductionItemId != null) {
					item.setDeductionItemId(deductionItemId);
				}
			}
		}

		String name = req.getParameter("deductionItemName");
		item.setDeductionItemName(name == null ? null : name.trim());
		item.setCalculationMethod(emptyToNull(req.getParameter("deductionCalculationMethod")));
		if (item.getCalculationMethod() == null) {
			item.setCalculationMethod("FIXED");
		}
		item.setTruncationUnit(parseIntOrDefault(req.getParameter("deductionTruncationUnit"), 0, strict));
		item.setRemark(emptyToNull(req.getParameter("remark")));
		item.setUseYn(defaultYn(req.getParameter("deductionUseYn")));
		item.setModId("SYSTEM");

		if (!forUpdate) {
			item.setDisplayOrder(0);
			item.setRegId("SYSTEM");
		}

		return item;
	}

	public void validateDeductionItem(DeductionItem item, boolean forUpdate) {
		if (forUpdate && item.getDeductionItemId() == null) {
			throw new PayItemSetException("修正する項目をリストから選択してください。");
		}
		if (item.getDeductionItemName() == null || item.getDeductionItemName().isEmpty()) {
			throw new PayItemSetException("控除項目を入力してください。");
		}
	}

	public Integer parseRequiredId(String value, String emptyMessage) {
		if (value == null || value.trim().isEmpty()) {
			throw new PayItemSetException(emptyMessage);
		}
		Integer id = parseInt(value, "対象項目が見つかりません。", true);
		if (id == null) {
			throw new PayItemSetException("対象項目が見つかりません。");
		}
		return id;
	}

	public Integer parseIdOrNull(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		return parseInt(value, "対象項目が見つかりません。", false);
	}

	private Integer parseInt(String value, String errorMessage, boolean strict) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException e) {
			if (strict) {
				throw new PayItemSetException(errorMessage);
			}
			return null;
		}
	}

	private int parseIntOrDefault(String value, int defaultValue, boolean strict) {
		if (value == null || value.trim().isEmpty()) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			if (strict) {
				throw new PayItemSetException("端数処理単位の指定が正しくありません。");
			}
			return defaultValue;
		}
	}

	private Long parseLongOrNull(String value, boolean strict) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			return Long.valueOf(value.replace(",", "").trim());
		} catch (NumberFormatException e) {
			if (strict) {
				throw new PayItemSetException("金額は数字で入力してください。");
			}
			return null;
		}
	}

	private String emptyToNull(String value) {
		return (value == null || value.trim().isEmpty()) ? null : value.trim();
	}

	private String defaultYn(String value) {
		if (value == null || value.trim().isEmpty()) {
			return "Y";
		}
		return value.trim();
	}

}
