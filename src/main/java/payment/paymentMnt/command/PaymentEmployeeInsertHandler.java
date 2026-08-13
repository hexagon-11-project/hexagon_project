package payment.paymentMnt.command;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMnt.dto.PaymentMntEmployeeDTO;
import payment.paymentMnt.service.PaymentMntService;

// 전체 새로고침 없이 방금 등록한 사원 행만 화면에 붙일 수 있도록, 등록된 사원들의 표시정보를 JSON으로 반환
public class PaymentEmployeeInsertHandler implements CommandHandler {

    private PaymentMntService paymentMntService = new PaymentMntService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 1. 파라미터 받기
        String payrollIdStr = request.getParameter("payrollId");
        String employeeIdsStr = request.getParameter("employeeIds");

        List<PaymentMntEmployeeDTO> inserted = null;
        if (payrollIdStr != null && employeeIdsStr != null && !employeeIdsStr.isEmpty()) {
            Long payrollId = Long.parseLong(payrollIdStr);
            List<String> empIds = Arrays.asList(employeeIdsStr.split(","));

            // 2. 서비스 호출하여 DB에 INSERT 실행하고, 방금 추가된 사원들의 표시정보를 돌려받음
            inserted = paymentMntService.insertEmployees(payrollId, empIds);
        }

        StringBuilder json = new StringBuilder("[");
        if (inserted != null) {
            for (int i = 0; i < inserted.size(); i++) {
                PaymentMntEmployeeDTO d = inserted.get(i);
                json.append("{")
                    .append("\"payrollEmployeeId\":").append(d.getPayrollEmployeeId()).append(",")
                    .append("\"employmentType\":\"").append(nvl(d.getEmploymentType())).append("\",")
                    .append("\"employeeName\":\"").append(nvl(d.getEmployeeName())).append("\",")
                    .append("\"department\":\"").append(nvl(d.getDepartment())).append("\",")
                    .append("\"totalPayAmount\":").append(d.getTotalPayAmount() == null ? 0 : d.getTotalPayAmount()).append(",")
                    .append("\"totalDeductionAmount\":").append(d.getTotalDeductionAmount() == null ? 0 : d.getTotalDeductionAmount()).append(",")
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