package payment.paymentMnt.command;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMnt.service.PaymentMntService;

public class PaymentEmployeeInsertHandler implements CommandHandler {

    private PaymentMntService paymentMntService = new PaymentMntService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 1. 파라미터 받기
        String payrollIdStr = request.getParameter("payrollId");
        String employeeIdsStr = request.getParameter("employeeIds");

        if (payrollIdStr != null && employeeIdsStr != null && !employeeIdsStr.isEmpty()) {
            Long payrollId = Long.parseLong(payrollIdStr);
            List<String> empIds = Arrays.asList(employeeIdsStr.split(","));

            // 2. 서비스 호출하여 DB에 INSERT 실행
            paymentMntService.insertEmployees(payrollId, empIds);
        }

        // 3. AJAX 요청이므로 뷰를 리턴하지 않고 성공 상태코드만 응답
        response.setStatus(HttpServletResponse.SC_OK);
        return null; 
    }
}