package person.employeeCard.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import person.employeeCard.service.EmployeeCardService;
import person.model.EmployeeCard;

public class EmployeeCardReadHandler implements CommandHandler {

	private EmployeeCardService employeeCardService = new EmployeeCardService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// 1. [핵심] 드롭박스(select)에 채워 넣을 전체 사원 목록을 무조건 조회해서 request에 담기!
		List<EmployeeCard> empList = employeeCardService.getAllEmployeeList();
		req.setAttribute("empList", empList);

		// 2. 사용자가 드롭박스에서 사원을 고르고 '조회' 버튼을 눌렀을 때 (파라미터가 넘어올 때)
		String empIdVal = req.getParameter("employeeId");
		
		if (empIdVal != null && !empIdVal.trim().isEmpty()) {
			int employeeId = Integer.parseInt(empIdVal);
			
			// 선택한 사원의 상세 인사기록카드 조회
			EmployeeCard card = employeeCardService.getEmployeeCard(employeeId);
			req.setAttribute("card", card);
		}

		// 3. JSP 페이지로 포워딩
		return "/WEB-INF/pages/Person/employeeCard.jsp";  // 님의 프로젝트 실제 JSP 경로에 맞춰주세요
	}
}

