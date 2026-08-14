package payment.paymentMntDayWorker.command;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMntDayWorker.dto.PaymentMntDayWorkerEmployeeDTO;
import payment.paymentMntDayWorker.service.PaymentMntDayWorkerService;

// 모달에서 선택한 사원들을 메인 화면(급여차수)에 추가
// 전체 새로고침 없이 방금 등록한 사원 행만 화면에 붙일 수 있도록, 등록된 사원들의 표시정보를 JSON으로 반환
public class PaymentMntDayWorkerEmployeeInsertHandler implements CommandHandler {

    private PaymentMntDayWorkerService service = new PaymentMntDayWorkerService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        String payrollDayWorkerIdStr = request.getParameter("payrollDayWorkerId");
        String employeeIdsStr = request.getParameter("employeeIds");

        List<PaymentMntDayWorkerEmployeeDTO> inserted = null;
        if (payrollDayWorkerIdStr != null && employeeIdsStr != null && !employeeIdsStr.isEmpty()) {
            Long payrollDayWorkerId = Long.parseLong(payrollDayWorkerIdStr);
            List<String> empIds = Arrays.asList(employeeIdsStr.split(","));
            inserted = service.insertEmployees(payrollDayWorkerId, empIds);
        }

        StringBuilder json = new StringBuilder("[");
        if (inserted != null) {
            for (int i = 0; i < inserted.size(); i++) {
                PaymentMntDayWorkerEmployeeDTO d = inserted.get(i);
                json.append("{")
                    .append("\"payrollEmployeeId\":").append(d.getPayrollDayWorkerEmployeeId()).append(",")
                    .append("\"employmentType\":\"").append(nvl(d.getEmploymentType())).append("\",")
                    .append("\"employeeName\":\"").append(nvl(d.getEmployeeName())).append("\",")
                    .append("\"department\":\"").append(nvl(d.getDepartment())).append("\",")
                    .append("\"netPayAmount\":").append(d.getNetPayAmount() == null ? 0 : d.getNetPayAmount())
                    .append("}");
                if (i < inserted.size() - 1) json.append(",");
            }
        }
        json.append("]");

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();

        return null;
    }

    private String nvl(String s) { return s == null ? "" : s; }
}
