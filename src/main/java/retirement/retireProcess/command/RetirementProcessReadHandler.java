package retirement.retireProcess.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import retirement.model.RetirementProcessModel;
import retirement.retireProcess.service.RetirementProcessReadService;

public class RetirementProcessReadHandler implements CommandHandler {

	private RetirementProcessReadService retirementService = new RetirementProcessReadService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        // 1. 파라미터 수신
        String searchName = req.getParameter("searchName");
        String status = req.getParameter("status"); 
        
        if (status == null || status.trim().isEmpty()) {
            status = "전체보기"; 
        }

        // 2. 비즈니스 로직 실행 (Service 호출)
        List<RetirementProcessModel> retirementList = retirementService.getRetirementEmployeeList(searchName, status);
        
        // 3. JSP 출력을 위한 데이터 세팅
        req.setAttribute("retirementList", retirementList);
        req.setAttribute("searchName", searchName);
        req.setAttribute("status", status);

        // 4. JSP View 포워딩
        return "/WEB-INF/pages/retirement/retireProcess.jsp";
    }
}
