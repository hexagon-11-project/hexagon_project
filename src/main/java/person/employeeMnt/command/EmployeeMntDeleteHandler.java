package person.employeeMnt.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import person.employeeMnt.service.EmployeeMntDeleteService;

public class EmployeeMntDeleteHandler implements CommandHandler {

    // 1번에서 만든 삭제 전용 서비스 연결
    private EmployeeMntDeleteService deleteService = new EmployeeMntDeleteService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        // 🚦 오직 POST 요청(폼 전송)일 때만 삭제 처리!
        if (req.getMethod().equalsIgnoreCase("POST")) {
            
            String[] empIds = req.getParameterValues("empId");
            
            if (empIds != null && empIds.length > 0) {
                deleteService.deleteEmployees(empIds);
            }
            
            // 삭제 완료 후 조회 페이지로 강제 이동 (리다이렉트)
            res.sendRedirect(req.getContextPath() + "/person/employeeMnt.do");
            return null;
            
        } else {
            // GET 방식 등으로 잘못 접근하면 "허용되지 않은 메서드(405)" 에러 뱉기
            res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }
}