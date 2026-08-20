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
        
        // 1. 화면(JSP)의 form에서 넘어온 계산된 데이터 수신
        RetirementMntModel model = new RetirementMntModel();
        model.setEmployeeId(req.getParameter("employeeId"));
        model.setHireDate(req.getParameter("hireDate"));
        model.setResignDate(req.getParameter("resignDate"));
        model.setServiceDays(Integer.parseInt(req.getParameter("serviceDays")));
        model.setTotalWageAmount(Long.parseLong(req.getParameter("totalWageAmount")));
        model.setAverageDailyWage(Double.parseDouble(req.getParameter("averageDailyWage")));
        model.setRetirementPayAmount(Long.parseLong(req.getParameter("retirementPayAmount")));

        // 2. Service를 통해 INSERT 실행
        int result = retirementService.saveRetirementData(model);

        // 3. 처리 후 결과 반환 (성공 시 목록 화면으로 리다이렉트)
        if (result > 0) {
      
        	res.sendRedirect(req.getContextPath() + "/Retire/retirementMnt.do");
            return null; // 직접 응답을 보냈으므로 프레임워크의 뷰 처리를 생략하기 위해 null 반환
        } else {
            // 실패 시 처리
            req.setAttribute("errorMsg", "저장에 실패했습니다.");
            return "/WEB-INF/pages/common/error.jsp";
        }
    }
}