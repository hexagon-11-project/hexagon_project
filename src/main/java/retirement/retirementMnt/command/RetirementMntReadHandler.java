package retirement.retirementMnt.command;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import command.CommandHandler;
import retirement.model.RetirementMntModel;
import retirement.retirementMnt.service.RetirementMntService;

public class RetirementMntReadHandler implements CommandHandler {

    private RetirementMntService retirementService = new RetirementMntService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        String retirementYear = req.getParameter("retirementYear");
        String employeeId = req.getParameter("employeeId");
        
        // [수정된 부분] 
        // 2026년 등 강제 세팅 로직을 완전히 삭제했습니다!
        // "전체"를 누르면 retirementYear가 "" 상태 그대로 DAO에 전달되어 전체 목록이 조회됩니다.

        // 콤보박스(사원 선택)용 전체 퇴직자 목록 조회
        List<RetirementMntModel> retiredEmpList = retirementService.getRetirementMntList(null, null);
        
        // 하단 테이블용 목록 조회 (선택한 조건 적용)
        List<RetirementMntModel> payList = retirementService.getRetirementMntList(retirementYear, employeeId);
        
        // 데이터 세팅
        req.setAttribute("retiredEmpList", retiredEmpList); 
        req.setAttribute("payList", payList); 
        req.setAttribute("retirementYear", retirementYear); 
        req.setAttribute("employeeId", employeeId); 

        // JSP View 포워딩
        return "/WEB-INF/pages/retirement/retirementMnt.jsp"; 
    }
}