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
        
       

        // (사원 선택)용 전체 퇴직자 목록 조회
        // (社員選択)用の全体退職者リストを照会
        List<RetirementMntModel> retiredEmpList = retirementService.getRetirementMntList(null, null);
        
        // 하단 테이블용 목록 조회 (선택한 조건 적용)
        // 下段テーブル用のリストを照会 (選択した条件を適用)
        List<RetirementMntModel> payList = retirementService.getRetirementMntList(retirementYear, employeeId);
        
        req.setAttribute("retiredEmpList", retiredEmpList); 
        req.setAttribute("payList", payList); 
        req.setAttribute("retirementYear", retirementYear); 
        req.setAttribute("employeeId", employeeId); 

        return "/WEB-INF/pages/retirement/retirementMnt.jsp"; 
    }
}