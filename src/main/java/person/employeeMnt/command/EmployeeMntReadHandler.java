package person.employeeMnt.command; 

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import person.employeeMnt.service.EmployeeMntService;
import person.employeeMnt.service.EmployeePage; // 👈 새로 만든 페이징 상자 import

public class EmployeeMntReadHandler implements CommandHandler {

    // 1. 서비스 객체 생성
    private EmployeeMntService employeeService = new EmployeeMntService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
      
        
        // 사용자가 클릭한 페이지 번호 가져오기 (없으면 1페이지)
        String pageVal = req.getParameter("page");
        int pageNum = 1;
        if (pageVal != null && !pageVal.isEmpty()) {
            pageNum = Integer.parseInt(pageVal);
        }
        
        try {
            // 1. 30개씩 잘린 데이터 상자 가져오기 (페이징 정보 포함)
            EmployeePage employeePage = employeeService.getEmployeePage(pageNum);
            
            // 2. 상단 상태별 카운트 버튼 정보 가져오기
            java.util.Map<String, Integer> countMap = employeeService.getEmployeeCounts();
            
            // 3. JSP에서 쓸 수 있게 넘겨주기
            req.setAttribute("employeePage", employeePage);
            req.setAttribute("countMap", countMap);
            
            return "/WEB-INF/pages/person/employeeMnt.jsp";
            
        } catch (Exception e) {
            // DB 조회 중 에러 발생 시 처리
            System.out.println("🚨 [Handler 에러 발생!] " + e.getMessage());
            e.printStackTrace();
            req.getServletContext().log("사원 목록 조회 실패", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}