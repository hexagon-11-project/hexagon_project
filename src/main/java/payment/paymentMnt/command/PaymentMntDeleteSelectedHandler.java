package payment.paymentMnt.command;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMnt.service.PaymentMntService;

// [선택삭제] - 체크된 사원(payrollEmployeeIds, 콤마구분)의 급여 데이터를 DB에서 실제 삭제
// [選択削除] - チェックされた社員（payrollEmployeeIds、カンマ区切り）の給与データをDBから実際に削除
public class PaymentMntDeleteSelectedHandler implements CommandHandler {

    private PaymentMntService service = new PaymentMntService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        String idsStr = request.getParameter("payrollEmployeeIds");

        List<Long> ids = new ArrayList<>();
        if (idsStr != null && !idsStr.isEmpty()) {
            for (String s : idsStr.split(",")) {
                if (!s.trim().isEmpty()) ids.add(Long.parseLong(s.trim()));
            }
        }
        service.deleteEmployees(ids);

        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("SUCCESS");
        out.flush();
        return null;
    }
}
