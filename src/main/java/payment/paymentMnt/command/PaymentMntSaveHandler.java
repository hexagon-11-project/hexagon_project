package payment.paymentMnt.command;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMnt.service.PaymentMntService;

public class PaymentMntSaveHandler implements CommandHandler {
    private PaymentMntService service = new PaymentMntService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        String empIdStr = request.getParameter("payrollEmployeeId");
        if (empIdStr == null || empIdStr.isEmpty()) return null;

        Long payrollEmployeeId = Long.parseLong(empIdStr);

        Map<Integer, Long> payItems = new HashMap<>();
        Map<Integer, Long> dedItems = new HashMap<>();
        long totalPay = 0;
        long totalDed = 0;

        // 화면에서 넘어온 모든 입력값을 뒤져서 지급/공제 항목만 골라냅니다.
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (paramName.startsWith("payItem_") || paramName.startsWith("dedItem_")) {
                // 혹시 모를 콤마(,) 제거 후 숫자로 변환
                String valueStr = request.getParameter(paramName).replace(",", ""); 
                long value = (valueStr.isEmpty()) ? 0 : Long.parseLong(valueStr);

                if (paramName.startsWith("payItem_")) {
                    int itemId = Integer.parseInt(paramName.replace("payItem_", ""));
                    payItems.put(itemId, value);
                    totalPay += value; // 지급 총액 누적
                } else if (paramName.startsWith("dedItem_")) {
                    int itemId = Integer.parseInt(paramName.replace("dedItem_", ""));
                    dedItems.put(itemId, value);
                    totalDed += value; // 공제 총액 누적
                }
            }
        }
        long netPay = totalPay - totalDed; // 실지급액 계산

        // DB에 몽땅 저장하라고 Service로 넘김
        service.savePayrollDetails(payrollEmployeeId, payItems, dedItems, totalPay, totalDed, netPay);

        // 자바스크립트(AJAX) 쪽으로 성공 신호 보내기
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("SUCCESS");
        out.flush();

        return null; 
    }
}