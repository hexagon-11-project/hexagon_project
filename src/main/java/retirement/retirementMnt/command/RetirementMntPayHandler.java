package retirement.retirementMnt.command;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import retirement.model.RetirementMntModel.MonthlyWage;
import retirement.retirementMnt.service.RetirementMntService;

public class RetirementMntPayHandler implements CommandHandler {
	private RetirementMntService retirementService = new RetirementMntService();
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 1. 파라미터 수신
        String employeeId = req.getParameter("employeeId");
        String resignDate = req.getParameter("resignDate"); 

        // 2. 비즈니스 로직 실행 (Service 호출)
        List<MonthlyWage> recent3MonthsWages = retirementService.getRecent3MonthsPayroll(employeeId, resignDate);

        // 3. 순수 문자열 텍스트로 응답 세팅 (JSON 사용 안 함)
        res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        
        // 데이터를 "월,금액|월,금액|월,금액" 형식의 문자열로 조립
        String resultString = "";
        for (int i = 0; i < recent3MonthsWages.size(); i++) {
            MonthlyWage wage = recent3MonthsWages.get(i);
            resultString += wage.getWageMonth() + "," + wage.getPaymentAmount();
            
            // 마지막 요소가 아니면 파이프(|)로 구분
            if (i < recent3MonthsWages.size() - 1) {
                resultString += "|";
            }
        }
        
        // 화면으로 조립된 문자열 전송
        out.print(resultString);
        out.flush();

        // 4. AJAX 통신이므로 뷰 포워딩 생략
        return null; 
    }

}
