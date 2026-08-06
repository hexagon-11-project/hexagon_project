package payment.paymentMnt.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMnt.dto.PaymentMntDeductionDetailDTO;
import payment.paymentMnt.dto.PaymentMntEmployeeDTO;
import payment.paymentMnt.dto.PaymentMntPayDetailDTO;
import payment.paymentMnt.service.PaymentMntService;

public class PaymentMntManageHandler implements CommandHandler {

	private PaymentMntService payrollService = new PaymentMntService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    String payrollIdStr = request.getParameter("payrollId");
	    Long payrollId = (payrollIdStr != null && !payrollIdStr.isEmpty()) ? Long.parseLong(payrollIdStr) : 1L;

	    // 1. 사원 리스트 먼저 조회
	    List<PaymentMntEmployeeDTO> empList = payrollService.getEmployeeList(payrollId);

	    // 2. 선택된 사원 ID 파라미터 받기
	    String payrollEmpIdStr = request.getParameter("payrollEmployeeId");
	    Long payrollEmployeeId = null;

	    if (payrollEmpIdStr != null && !payrollEmpIdStr.isEmpty()) {
	        payrollEmployeeId = Long.parseLong(payrollEmpIdStr);
	    } else if (empList != null && !empList.isEmpty()) {
	        // 파라미터가 없는데 사원 목록이 존재한다면, 첫 번째 사원의 ID를 기본값으로 지정
	        // (※ getPayrollEmployeeId 부분은 본인의 DTO 메서드 이름에 맞게 확인해주세요!)
	        payrollEmployeeId = empList.get(0).getPayrollEmployeeId(); 
	    }

	    // 3. 우측 상세 내역 조회 (사원 ID가 있을 때만 조회)
	    List<PaymentMntPayDetailDTO> payDetails = null;
	    List<PaymentMntDeductionDetailDTO> deductionDetails = null;

	    if (payrollEmployeeId != null) {
	        payDetails = payrollService.getPayDetails(payrollEmployeeId);
	        deductionDetails = payrollService.getDeductionDetails(payrollEmployeeId);
	    }

	    // 4. JSP로 데이터 전달
	    request.setAttribute("empList", empList);
	    request.setAttribute("payDetails", payDetails);
	    request.setAttribute("deductionDetails", deductionDetails);

	    return "/WEB-INF/pages/payment/paymentMnt.jsp";
	}
}