package payment.paymentMnt.command;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMnt.dto.PaymentMntDeductionDetailDTO;
import payment.paymentMnt.dto.PaymentMntEffectiveDetail;
import payment.paymentMnt.dto.PaymentMntPayDetailDTO;
import payment.paymentMnt.service.PaymentMntService;

// 사원 클릭 시 우측 상세(지급/공제) 데이터를 JSON으로 반환하는 AJAX 처리
// 社員クリック時に右側詳細（支給・控除）データをJSONで返すAJAX処理
public class PaymentMntDetailAjaxHandler implements CommandHandler {

    private PaymentMntService payrollService = new PaymentMntService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 1. 화면(JSP)에서 클릭한 사원의 ID(payrollEmployeeId)를 받아옵니다. / 1. 画面（JSP）でクリックした社員のID（payrollEmployeeId）を受け取ります。
        String empIdStr = request.getParameter("payrollEmployeeId");

        if (empIdStr == null || empIdStr.isEmpty()) {
            return null; // ID가 없으면 그냥 종료 / IDがなければそのまま終了
        }

        Long payrollEmployeeId = Long.parseLong(empIdStr);

        // 2. Service를 통해 해당 사원의 "최종" 지급/공제 상세를 조회합니다. / 2. Serviceを通じて該当社員の「最終」支給・控除詳細を照会します。
        //    (저장된 값 + 기본급/일용급여/공제 기본값 보정까지 반영 - 좌측 목록 총액과 항상 일치하는 동일 로직) / （保存された値＋基本給・日雇い給与・控除初期値補正まで反映 - 左側一覧の総額と常に一致する同一ロジック）
        PaymentMntEffectiveDetail detail = payrollService.getEffectiveDetail(payrollEmployeeId);
        List<PaymentMntPayDetailDTO> payDetails = detail.getPayDetails();
        List<PaymentMntDeductionDetailDTO> deductionDetails = detail.getDeductionDetails();

        // 3. 조회된 데이터를 자바스크립트가 이해할 수 있는 JSON 문자열 형태로 직접 조립합니다. / 3. 照会されたデータをJavaScriptが理解できるJSON文字列の形に直接組み立てます。
        // (외부 라이브러리 충돌을 막기 위해 수동으로 안전하게 조립합니다) / （外部ライブラリの衝突を防ぐため、手動で安全に組み立てます）
        StringBuilder json = new StringBuilder();
        json.append("{");

        // --- 지급항목 JSON 배열 만들기 --- / --- 支給項目JSON配列を作る ---
        json.append("\"payDetails\": [");
        for (int i = 0; i < payDetails.size(); i++) {
            PaymentMntPayDetailDTO p = payDetails.get(i);
            json.append("{")
                .append("\"payItemId\":").append(p.getPayItemId()).append(",")
                .append("\"amount\":").append(p.getAmount())
                .append("}");
            if (i < payDetails.size() - 1) json.append(","); // 마지막이 아니면 콤마 추가 / 最後でなければカンマを追加
        }
        json.append("],");

        // --- 공제항목 JSON 배열 만들기 --- / --- 控除項目JSON配列を作る ---
        json.append("\"deductionDetails\": [");
        for (int i = 0; i < deductionDetails.size(); i++) {
            PaymentMntDeductionDetailDTO d = deductionDetails.get(i);
            json.append("{")
                .append("\"deductionItemId\":").append(d.getDeductionItemId()).append(",")
                .append("\"amount\":").append(d.getAmount())
                .append("}");
            if (i < deductionDetails.size() - 1) json.append(","); // 마지막이 아니면 콤마 추가 / 最後でなければカンマを追加
        }
        json.append("]");

        json.append("}"); // JSON 완성 / JSON完成

        // 4. 완성된 JSON 데이터를 화면(Front-end)으로 쏴줍니다. / 4. 完成したJSONデータを画面（フロントエンド）へ送ります。
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();

        // AJAX 통신이므로 새로운 JSP 페이지로 이동(return)하지 않고 null을 반환합니다. / AJAX通信のため、新しいJSPページへ移動（return）せずnullを返します。
        return null;
    }
}
