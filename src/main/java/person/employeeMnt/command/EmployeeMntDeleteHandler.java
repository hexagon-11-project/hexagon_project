package person.employeeMnt.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import person.employeeMnt.service.EmployeeMntDeleteService;

public class EmployeeMntDeleteHandler implements CommandHandler {

    private EmployeeMntDeleteService deleteService = new EmployeeMntDeleteService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        //  POST일 때만 삭제 처리
    	//  POSTの時のみ削除処理
        if (req.getMethod().equalsIgnoreCase("POST")) {
            
            String[] empIds = req.getParameterValues("empId");
            
            if (empIds != null && empIds.length > 0) {
                deleteService.deleteEmployees(empIds);
            }
            
            // 삭제 완료 후 조회 페이지로 이동
            // 削除完了後、照会ページへ移動
            res.sendRedirect(req.getContextPath() + "/Person/employeeMnt.do?delete=success");
            return null;
            
        } else {
            // GET 방식 등으로 잘못 접근시 405 에러
        	// GETメソッド等による不正アクセス時は405エラー
            res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }
}