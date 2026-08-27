package person.employeeMnt.command; 

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import person.employeeMnt.service.EmployeeMntService;
import person.employeeMnt.service.EmployeePage; // 👈 새로 만든 페이징 상자 import

public class EmployeeMntReadHandler implements CommandHandler {

    private EmployeeMntService employeeService = new EmployeeMntService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
      
        
        // 사용자가 클릭한 페이지 번호 가져오기 (없으면 1페이지)
    	// ユーザーがクリックしたページ番号を取得 (なければ1ページ)
        String pageVal = req.getParameter("page");
        int pageNum = 1;
        if (pageVal != null && !pageVal.isEmpty()) {
            pageNum = Integer.parseInt(pageVal);
        }
        // 검색 파라미터
        // 検索パラメータ
        String searchType = req.getParameter("searchType");
        String keyword = req.getParameter("keyword");
        
        try {
            // 30개씩 페이징
        	// 30件ずつページング
        	EmployeePage employeePage = employeeService.getEmployeePage(pageNum, searchType, keyword);
            
            java.util.Map<String, Integer> countMap = employeeService.getEmployeeCounts();
            
            req.setAttribute("employeePage", employeePage);
            req.setAttribute("countMap", countMap);
            
            return "/WEB-INF/pages/person/employeeMnt.jsp";
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getServletContext().log("社員一覧の照会失敗", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}