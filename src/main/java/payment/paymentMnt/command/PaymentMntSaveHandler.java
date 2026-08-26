package payment.paymentMnt.command;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMnt.service.PaymentMntService;

// 급여입력관리 화면의 지급/공제 금액 저장 처리
// 給与入力管理画面の支給・控除金額の保存処理
public class PaymentMntSaveHandler implements CommandHandler {
    private PaymentMntService service = new PaymentMntService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        String empIdStr = request.getParameter("payrollEmployeeId");

        Long payrollEmployeeId;
        if (empIdStr == null || empIdStr.isEmpty()) {
            // ★ 아직 이 급여차수에 등록 안 된 사원(왼쪽 목록에서 전체 사원 조회로 표시된 사원)을 그대로 저장하려는
            //   경우: employeeId/payrollId로 PAYROLL_EMPLOYEE를 먼저 만들고 그 ID로 저장을 이어간다.
            // ★まだこの給与回に登録されていない社員（左側一覧が全社員照会で表示している社員）をそのまま保存しようと
            //   する場合：employeeId/payrollIdでPAYROLL_EMPLOYEEを先に作成し、そのIDで保存を続ける。
            String employeeId = request.getParameter("employeeId");
            String payrollIdStr = request.getParameter("payrollId");
            if (employeeId == null || employeeId.isEmpty() || payrollIdStr == null || payrollIdStr.isEmpty()) {
                return null;
            }
            Long payrollId = Long.parseLong(payrollIdStr);
            payrollEmployeeId = service.registerEmployeeToPayroll(payrollId, employeeId);
            if (payrollEmployeeId == null) return null;
        } else {
            payrollEmployeeId = Long.parseLong(empIdStr);
        }

        Map<Integer, Long> payItems = new HashMap<>();
        Map<Integer, Long> dedItems = new HashMap<>();
        long totalPay = 0;
        long totalDed = 0;

        // 화면에서 넘어온 모든 입력값을 뒤져서 지급/공제 항목만 골라냅니다. / 画面から渡された全入力値を調べて、支給・控除項目だけを選び出します。
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (paramName.startsWith("payItem_") || paramName.startsWith("dedItem_")) {
                // 혹시 모를 콤마(,) 제거 후 숫자로 변환 / 念のためカンマ(,)を除去してから数値に変換
                String valueStr = request.getParameter(paramName).replace(",", "");
                long value = (valueStr.isEmpty()) ? 0 : Long.parseLong(valueStr);

                if (paramName.startsWith("payItem_")) {
                    int itemId = Integer.parseInt(paramName.replace("payItem_", ""));
                    payItems.put(itemId, value);
                    totalPay += value; // 지급 총액 누적 / 支給総額を積算
                } else if (paramName.startsWith("dedItem_")) {
                    int itemId = Integer.parseInt(paramName.replace("dedItem_", ""));
                    dedItems.put(itemId, value);
                    totalDed += value; // 공제 총액 누적 / 控除総額を積算
                }
            }
        }
        long netPay = totalPay - totalDed; // 실지급액 계산 / 実支給額を計算

        // DB에 몽땅 저장하라고 Service로 넘김 / DBに全部保存するようService へ渡す
        service.savePayrollDetails(payrollEmployeeId, payItems, dedItems, totalPay, totalDed, netPay);

        // 자바스크립트(AJAX) 쪽으로 성공 신호 보내기 / JavaScript（AJAX）側へ成功シグナルを送る
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("SUCCESS");
        out.flush();

        return null; 
    }
}