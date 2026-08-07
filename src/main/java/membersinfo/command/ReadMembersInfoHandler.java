package membersinfo.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import membersinfo.model.CompanyInfo;
import membersinfo.service.CompanyNotFoundException;
import membersinfo.service.ReadmembersInfoService;

public class ReadMembersInfoHandler implements CommandHandler {

    // 회사 정보를 조회하는 비즈니스 로직을 처리할 서비스 객체 생성
    private ReadmembersInfoService readService = new ReadmembersInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // 1. 요청 파라미터에서 회사 ID 가져오기 (예: readCompany.do?companyId=1)
        
      
        
        
        
        

        try {
            // 2. 서비스 호출해서 DB에서 회사 정보 데이터(CompanyInfo) 가져오기
            CompanyInfo companyInfo = readService.getCompanyInfo(1001);
            
            // 3. JSP에서 ${companyInfo.companyName} 등으로 꺼내 쓸 수 있게 request에 저장
            req.setAttribute("companyInfo", companyInfo);
            System.out.println(companyInfo);
            // 4. 화면을 그려줄 JSP 뷰 경로 리턴
            return "/WEB-INF/pages/config/membersInfo.jsp";
            
        } catch (CompanyNotFoundException e) {
            // 5. DB에 해당 ID의 회사 정보가 없을 경우 404 Not Found 에러 응답
            req.getServletContext().log("no company info", e);
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }
}