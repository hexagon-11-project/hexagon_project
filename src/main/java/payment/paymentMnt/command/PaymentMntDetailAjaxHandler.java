package payment.paymentMnt.command;

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
        String empIdStr = request.getParameter("payrollEmployeeId");
        if(empIdStr == null || empIdStr.isEmpty()) {
            return null; // 값이 없으면 그냥 종료
        }
        
        Long payrollEmployeeId = Long.parseLong(empIdStr);

        // 지급항목과 공제항목 리스트 가져오기
        List<PaymentMntPayDetailDTO> payList = payrollService.getPayDetails(payrollEmployeeId);
        List<PaymentMntDeductionDetailDTO> dedList = payrollService.getDeductionDetails(payrollEmployeeId);

        // 라이브러리 추가 설정 없이 바로 쓸 수 있도록 순수 자바 문자열로 JSON 형태 만들기
        StringBuilder json = new StringBuilder();
        json.append("{ \"pays\": [");
        for (int i = 0; i < payList.size(); i++) {
            json.append("{\"name\":\"").append(payList.get(i).getItemName())
                .append("\", \"amount\":").append(payList.get(i).getAmount()).append("}");
            if (i < payList.size() - 1) json.append(",");
        }
        json.append("], \"deductions\": [");
        for (int i = 0; i < dedList.size(); i++) {
            json.append("{\"name\":\"").append(dedList.get(i).getItemName())
                .append("\", \"amount\":").append(dedList.get(i).getAmount()).append("}");
            if (i < dedList.size() - 1) json.append(",");
        }
        json.append("] }");

        // 브라우저에 JSON 데이터를 응답으로 쏴줌
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json.toString());
        
        // AJAX 요청이므로 특정 JSP 화면으로 이동하지 않고 마무리
        return null; 
    }
}