package payment.paymentMnt.command;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMnt.dto.PaymentMntDeductionDetailDTO;
import payment.paymentMnt.dto.PaymentMntPayDetailDTO;
import payment.paymentMnt.service.PaymentMntService;

public class PaymentMntDetailAjaxHandler implements CommandHandler {

    private PaymentMntService payrollService = new PaymentMntService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 1. 화면(JSP)에서 클릭한 사원의 ID(payrollEmployeeId)를 받아옵니다.
        String empIdStr = request.getParameter("payrollEmployeeId");
        
        if (empIdStr == null || empIdStr.isEmpty()) {
            return null; // ID가 없으면 그냥 종료
        }

        Long payrollEmployeeId = Long.parseLong(empIdStr);

        // 2. Service를 통해 해당 사원의 지급/공제 상세 금액을 오라클 DB에서 조회합니다.
        List<PaymentMntPayDetailDTO> payDetails = payrollService.getPayDetails(payrollEmployeeId);
        List<PaymentMntDeductionDetailDTO> deductionDetails = payrollService.getDeductionDetails(payrollEmployeeId);

        // 3. 조회된 데이터를 자바스크립트가 이해할 수 있는 JSON 문자열 형태로 직접 조립합니다.
        // (외부 라이브러리 충돌을 막기 위해 수동으로 안전하게 조립합니다)
        StringBuilder json = new StringBuilder();
        json.append("{");
        
        // --- 지급항목 JSON 배열 만들기 ---
        json.append("\"payDetails\": [");
        for (int i = 0; i < payDetails.size(); i++) {
            PaymentMntPayDetailDTO p = payDetails.get(i);
            json.append("{")
                .append("\"payItemId\":").append(p.getPayItemId()).append(",")
                .append("\"amount\":").append(p.getAmount())
                .append("}");
            if (i < payDetails.size() - 1) json.append(","); // 마지막이 아니면 콤마 추가
        }
        json.append("],");

        // --- 공제항목 JSON 배열 만들기 ---
        json.append("\"deductionDetails\": [");
        for (int i = 0; i < deductionDetails.size(); i++) {
            PaymentMntDeductionDetailDTO d = deductionDetails.get(i);
            json.append("{")
                .append("\"deductionItemId\":").append(d.getDeductionItemId()).append(",")
                .append("\"amount\":").append(d.getAmount())
                .append("}");
            if (i < deductionDetails.size() - 1) json.append(","); // 마지막이 아니면 콤마 추가
        }
        json.append("]");
        
        json.append("}"); // JSON 완성

        // 4. 완성된 JSON 데이터를 화면(Front-end)으로 쏴줍니다.
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();

        // AJAX 통신이므로 새로운 JSP 페이지로 이동(return)하지 않고 null을 반환합니다.
        return null; 
    }
}