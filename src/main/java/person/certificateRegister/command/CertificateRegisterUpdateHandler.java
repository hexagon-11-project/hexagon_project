package person.certificateRegister.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import person.certificateRegister.service.CertificateRegisterUpdateService;

public class CertificateRegisterUpdateHandler implements CommandHandler {

    private CertificateRegisterUpdateService updateService = new CertificateRegisterUpdateService();

    
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // 1. GET 방식 접근 차단 
        if (request.getMethod().equalsIgnoreCase("GET")) {
            
            response.sendRedirect(request.getContextPath() + "/Person/certificateRegister.do");
            return null;
        }

        // 2. 화면에서 체크박스로 선택한 발급번호(issueNo) 배열로 받기
        // 주의: JSP의 체크박스 name 속성이 "issueNo" 여야 합니다.
        String[] issueNos = request.getParameterValues("issueNo");

        // 3. Service 호출하여 상태 업데이트 (Y -> N)
        if (issueNos != null && issueNos.length > 0) {
            updateService.softDeleteCertificates(issueNos);
        }

        // 4. 처리 후 다시 목록 화면(조회 Handler)으로 리다이렉트
       
        response.sendRedirect(request.getContextPath() + "/Person/certificateRegister.do");
        
        // 리다이렉트를 수행했으므로 JSP 경로를 리턴하지 않고 null을 리턴합니다.
        return null; 
    }
}