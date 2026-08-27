package config.membersinfo.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.membersinfo.service.UpdateMembersInfoService;
import config.model.CompanyInfo;

public class UpdateMembersInfoHandler implements CommandHandler {

	private UpdateMembersInfoService updateService = new UpdateMembersInfoService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (request.getMethod().equalsIgnoreCase("GET")) {
			return processForm(request, response);
		} else if (request.getMethod().equalsIgnoreCase("POST")) {
			return processSubmit(request, response);
		} else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}

	// 수정 폼을 보여줄 때 (GET)
	//編集フォームを表示する際（GET）
	private String processForm(HttpServletRequest request, HttpServletResponse response) {
		return "/WEB-INF/page/Config/membersInfo.jsp";
	}

	// 저장 버튼을 눌러 제출했을 때 (POST)
	//保存ボタンを押して送信する際（POST）
	private String processSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int companyId = Integer.parseInt(request.getParameter("companyId"));
		String companyName = request.getParameter("companyName");
		String businessNo = request.getParameter("businessNo");
		String ceoTitle = request.getParameter("ceoTitle");
		String ceoName = request.getParameter("ceoName");
		String corpNo = request.getParameter("corpNo");

		// 날짜 데이터 처리
		//日付データの処理
		java.sql.Date estDate = parseDate(request.getParameter("estDate"));
		
		String webSite = request.getParameter("webSite");
		String address = request.getParameter("address");
		String telNo = request.getParameter("telNo");
		String faxNo = request.getParameter("faxNo");
		String businessType = request.getParameter("businessType");
		String businessItem = request.getParameter("businessItem");

		int payDay = Integer.parseInt(request.getParameter("payDay"));
		int payPeriodStartDay = Integer.parseInt(request.getParameter("payPeriodStartDay"));
		int payPeriodEndDay = Integer.parseInt(request.getParameter("payPeriodEndDay"));

		String bankName = request.getParameter("bankName");
		String accountHolder = request.getParameter("accountHolder");
		String bankAccount = request.getParameter("bankAccount");
		String logoPath = request.getParameter("logoPath");
		String sealPath = request.getParameter("sealPath");
		
	
		String managerName = request.getParameter("managerName");
		String managerTel = request.getParameter("managerTel");
		String managerMobile = request.getParameter("managerMobile");
		String managerEmail = request.getParameter("managerEmail");

		CompanyInfo info = new CompanyInfo();
		info.setCompanyId(companyId);
		info.setCompanyName(companyName);
		info.setBusinessNo(businessNo);
		info.setCeoTitle(ceoTitle);
		info.setCeoName(ceoName);
		info.setCorpNo(corpNo);
		info.setEstDate(estDate);
		info.setWebSite(webSite);
		info.setTelNo(telNo);
		info.setFaxNo(faxNo);
		info.setBusinessType(businessType);
		info.setBusinessItem(businessItem);
		info.setPayDay(payDay);
		info.setPayPeriodStartDay(payPeriodStartDay);
		info.setPayPeriodEndDay(payPeriodEndDay);
		info.setBankName(bankName);
		info.setAccountHolder(accountHolder);
		info.setBankAccount(bankAccount);
		info.setLogoPath(logoPath);
		info.setSealPath(sealPath);
		
		// 담당자 정보
		//担当者情報
		info.setManagerName(managerName);
		info.setManagerTel(managerTel);
		info.setManagerMobile(managerMobile);
		info.setManagerEmail(managerEmail);

		updateService.update(info);

		// 수정 완료 후 알림창을 띄우기 위해 파라미터 추가
		//編集完了後にアラートを表示するため、パラメータを追加
		response.sendRedirect(request.getContextPath() + "/Config/membersInfo.do?id=" + companyId + "&save=success");
		return null;
	}

	// 화면에서 넘어온 날짜 문자열(yyyy-MM-dd 또는 yyyyMMdd)
	//画面から送信された日付文字列（yyyy-MM-ddまたはyyyyMMdd）
	private java.sql.Date parseDate(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null; 
		}
		String trimmed = value.trim();
		try {
			if (trimmed.contains("-")) {
				return java.sql.Date.valueOf(LocalDate.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE));
			} else {
				return java.sql.Date.valueOf(LocalDate.parse(trimmed, DateTimeFormatter.ofPattern("yyyyMMdd")));
			}
		} catch (Exception e) {
			return null; 
		}
	}
}