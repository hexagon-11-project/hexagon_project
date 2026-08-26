package person.certificateRegister.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import person.certificateRegister.service.CertificateRegisterUpdateService;

public class CertificateRegisterUpdateHandler implements CommandHandler {

    private CertificateRegisterUpdateService updateService = new CertificateRegisterUpdateService();

    
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        //  GET 방식 접근 차단 
        if (request.getMethod().equalsIgnoreCase("GET")) {
            
            response.sendRedirect(request.getContextPath() + "/Person/certificateRegister.do");
            return null;
        }

        String[] issueNos = request.getParameterValues("issueNo");

        //  Service 호출하여 상태 업데이트 (Y -> N)
        if (issueNos != null && issueNos.length > 0) {
            updateService.softDeleteCertificates(issueNos);
        }

       
        response.sendRedirect(request.getContextPath() + "/Person/certificateRegister.do?delete=success");
        
        return null; 
    }
}