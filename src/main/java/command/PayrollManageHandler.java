package command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import payroll.dto.PayrollEmployeeDTO;
import payroll.service.PayrollService;

public class PayrollManageHandler implements CommandHandler {

	private PayrollService payrollService = new PayrollService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 급여차수(payrollId)를 파라미터로 받음. 처음 페이지 로딩 시 없으면 기본값 1 세팅 (테스트용)
		String payrollIdStr = request.getParameter("payrollId");
		Long payrollId = (payrollIdStr != null && !payrollIdStr.isEmpty()) ? Long.parseLong(payrollIdStr) : 1L;

		// DB에서 사원 리스트 가져오기
		List<PayrollEmployeeDTO> empList = payrollService.getEmployeeList(payrollId);

		// JSP 화면에서 쓸 수 있게 request에 담기
		request.setAttribute("empList", empList);

		// 화면을 띄워줄 JSP 파일 경로 리턴
		return "/WEB-INF/pages/payroll/input.jsp";
	}
}