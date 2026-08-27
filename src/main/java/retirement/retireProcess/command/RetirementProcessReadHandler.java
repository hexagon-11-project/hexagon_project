package retirement.retireProcess.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import retirement.retireProcess.service.RetirementProcessReadService;
import retirement.retireProcess.service.RetirementProcessPage;

public class RetirementProcessReadHandler implements CommandHandler {

    private RetirementProcessReadService retirementService = new RetirementProcessReadService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        //  페이지 번호 받기 (기본값 1)
    	// // ページ番号を受け取る (デフォルト値 1)
        String pageVal = req.getParameter("page");
        int pageNum = 1;
        if (pageVal != null && !pageVal.isEmpty()) {
            pageNum = Integer.parseInt(pageVal);
        }

       
        String searchName = req.getParameter("searchName");
        String status = req.getParameter("status"); 
        
        if (status == null || status.trim().isEmpty()) {
            status = "전체보기"; 
        }

        //  30개씩 분할된 데이터를 가진 Page 객체 호출
        // // 30件ずつ分割されたデータを持つPageオブジェクトを呼び出す
        RetirementProcessPage retirementPage = retirementService.getRetirementProcessPage(pageNum, searchName, status);
        
       
        req.setAttribute("retirementPage", retirementPage);
        req.setAttribute("searchName", searchName);
        req.setAttribute("status", status);

        return "/WEB-INF/pages/retirement/retireProcess.jsp";
    }
}