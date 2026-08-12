package payment.paymentMntDayWorker.command;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMntDayWorker.service.PaymentMntDayWorkerService;

// 모달에서 선택한 사원들을 메인 화면(급여차수)에 추가
public class PaymentMntDayWorkerEmployeeInsertHandler implements CommandHandler {

    private PaymentMntDayWorkerService service = new PaymentMntDayWorkerService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        String payrollDayWorkerIdStr = request.getParameter("payrollDayWorkerId");
        String employeeIdsStr = request.getParameter("employeeIds");

        if (payrollDayWorkerIdStr != null && employeeIdsStr != null && !employeeIdsStr.isEmpty()) {
            Long payrollDayWorkerId = Long.parseLong(payrollDayWorkerIdStr);
            List<String> empIds = Arrays.asList(employeeIdsStr.split(","));
            service.insertEmployees(payrollDayWorkerId, empIds);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        return null;
    }
}
