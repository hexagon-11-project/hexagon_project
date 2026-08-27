package retirement.retirePayslip.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.employee.model.Employee;
import config.model.CompanyInfo;
import retirement.model.RetirementMntModel;
import retirement.retirePayslip.service.RetirePayslipService;

public class RetirePayslipReadHandler implements CommandHandler {

    private RetirePayslipService retirePayslipService = new RetirePayslipService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        // 사원 콤보박스 데이터(정산 완료된 사람만) 세팅
    	// 社員コンボボックスデータ (精算完了者のみ) をセッティング
        List<Employee> empList = retirePayslipService.getSettledEmployeeList();
        req.setAttribute("empList", empList);
        
     
        String employeeId = req.getParameter("employeeId");
        
        RetirementMntModel statement = new RetirementMntModel();
        CompanyInfo company = new CompanyInfo();
        
        try {
            // 조건이 있을 때만 명세서 조회 실행
        	// 条件がある場合のみ明細書照会を実行
            if (employeeId != null && !employeeId.trim().isEmpty()) {
                
                retirePayslipService.getRetirementStatement(employeeId, statement, company);
                
                // 성공적으로 조회됐을 때만 어트리뷰트 세팅
                // 正常に照会された場合のみアトリビュートを設定
                if (statement.getEmployeeName() != null) {
                    req.setAttribute("retirePayslip", statement);
                    req.setAttribute("company", company);
                }
            }
            
            return "/WEB-INF/pages/retirement/retirePayslip.jsp";
            
        } catch (Exception e) {
            System.out.println(" [明細書照会ハンドラーでエラーが発生!] " + e.getMessage());
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}