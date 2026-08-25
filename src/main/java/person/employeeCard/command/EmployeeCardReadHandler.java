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
		
		// 전체 사원 목록을 조회 후 List에 담기
		List<EmployeeCard> empList = employeeCardService.getAllEmployeeList();
		
		
		req.setAttribute("empList", empList);

		// '조회' 버튼을 눌렀을 때
		String empIdVal = req.getParameter("employeeId");
		
		if (empIdVal != null && !empIdVal.trim().isEmpty()) {
			int employeeId = Integer.parseInt(empIdVal);
			
			//  상세 인사기록카드 조회
			EmployeeCard card = employeeCardService.getEmployeeCard(employeeId);
			req.setAttribute("card", card);
		}

		
		return "/WEB-INF/pages/person/employeeCard.jsp";  
}
}

