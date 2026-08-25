package retirement.retirementMnt.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import retirement.model.RetirementMntModel;
import retirement.retirementMnt.service.RetirementMntInsertService;

public class RetirementMntInsertHandler implements CommandHandler {

    private RetirementMntInsertService retirementService = new RetirementMntInsertService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        RetirementMntModel model = new RetirementMntModel();
        model.setEmployeeId(req.getParameter("employeeId"));
        model.setHireDate(req.getParameter("hireDate"));
        model.setResignDate(req.getParameter("resignDate"));
        model.setServiceDays(Integer.parseInt(req.getParameter("serviceDays")));
        model.setTotalWageAmount(Long.parseLong(req.getParameter("totalWageAmount")));
        model.setAverageDailyWage(Double.parseDouble(req.getParameter("averageDailyWage")));
        model.setRetirementPayAmount(Long.parseLong(req.getParameter("retirementPayAmount")));

        int result = retirementService.saveRetirementData(model);

        if (result > 0) {
      
        	res.sendRedirect(req.getContextPath() + "/Retire/retirementMnt.do");
            return null; 
        }else if(result == 0) {
        	// [중복] 이미 해당 사원의 데이터가 존재하는 경우 (DB에서 INSERT를 수행하지 않음)
            res.sendRedirect(req.getContextPath() + "/Retire/retirementMnt.do?error=dup");
            return null;
        }else {
            req.setAttribute("errorMsg", "저장에 실패했습니다.");
            return "/WEB-INF/pages/common/error.jsp";
        }
    }
}