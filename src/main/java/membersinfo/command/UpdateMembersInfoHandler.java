package membersinfo.command;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import membersinfo.model.CompanyInfo;
import membersinfo.service.UpdateMembersInfoService; // ★ 올바른 서비스 임포트

public class UpdateMembersInfoHandler implements CommandHandler {

    // ★ 서비스 객체 이름을 작성하신 클래스명과 똑같이 맞춤
    private UpdateMembersInfoService updateService = new UpdateMembersInfoService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getMethod().equalsIgnoreCase("GET")) {
            return processForm(request, response);
        } else if (request.getMethod().equalsIgnoreCase("POST")) {
        	System.out.println("=============================");
        	System.out.println("=============================");
        	System.out.println("=============================");
            return processSubmit(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }

    // 수정 폼을 보여줄 때 (GET)
    private String processForm(HttpServletRequest request, HttpServletResponse response) {
        return "/WEB-INF/page/config/membersInfo.jsp";
    }

    // 저장 버튼을 눌러 제출했을 때 (POST)
    private String processSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 1. 폼에서 전달된 파라미터 값 읽기
        int companyId = Integer.parseInt(request.getParameter("companyId"));
        String companyName = request.getParameter("companyName");
        String businessNo = request.getParameter("businessNo");
        String ceoTitle = request.getParameter("ceoTitle");
        String ceoName = request.getParameter("ceoName");
        String corpNo = request.getParameter("corpNo");
        
        // 날짜 데이터 처리
//        String estDateStr = request.getParameter("estDate");
//        Timestamp estDate = (estDateStr != null && !estDateStr.isEmpty()) ? Timestamp.valueOf(estDateStr + " 00:00:00") : null;
        String estDate = request.getParameter("estDate");
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

        // 2. CompanyInfo 객체에 값 세팅
        CompanyInfo info = new CompanyInfo();
        info.setCompanyId(companyId);
        info.setCompanyName(companyName);
        info.setBusinessNo(businessNo);
        info.setCeoTitle(ceoTitle);
        info.setCeoName(ceoName);
        info.setCorpNo(corpNo);
        info.setEstDate(estDate);
        info.setWebSite(webSite);
        info.setAddress(address);
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

        // 3. 서비스 실행하여 DB 반영
        updateService.update(info);

        // 4. 수정 완료 후 리다이렉트 (하드코딩 1001 대신 companyId 변수 사용)
        response.sendRedirect(request.getContextPath() + "/config/membersInfo.do?id=" + 1001);
        return null;
    }
}