package payment.paymentMnt.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMnt.dto.PaymentMntDeductionDetailDTO;
import payment.paymentMnt.dto.PaymentMntDeductionItemDTO; // 추가
import payment.paymentMnt.dto.PaymentMntEmployeeDTO;
import payment.paymentMnt.dto.PaymentMntPayDetailDTO;
import payment.paymentMnt.dto.PaymentMntPayItemDTO; // 추가
import payment.paymentMnt.service.PaymentMntService;

// 급여입력관리 화면(사원목록 + 우측 상세) 조회 처리
// 給与入力管理画面（社員一覧＋右側詳細）の照会処理
public class PaymentMntManageHandler implements CommandHandler {

	private PaymentMntService payrollService = new PaymentMntService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    String payrollIdStr = request.getParameter("payrollId");
	    Long payrollId = (payrollIdStr != null && !payrollIdStr.isEmpty()) ? Long.parseLong(payrollIdStr) : 1L;

	    // 1. 사원 리스트 먼저 조회 / 1. 社員リストを先に照会
	    List<PaymentMntEmployeeDTO> empList = payrollService.getEmployeeList(payrollId);

	    // 2. 선택된 사원 ID 파라미터 받기 / 2. 選択された社員IDパラメータを受け取る
	    String payrollEmpIdStr = request.getParameter("payrollEmployeeId");
	    Long payrollEmployeeId = null;

	    if (payrollEmpIdStr != null && !payrollEmpIdStr.isEmpty()) {
	        payrollEmployeeId = Long.parseLong(payrollEmpIdStr);
	    } else if (empList != null && !empList.isEmpty()) {
	        // 파라미터가 없는데 사원 목록이 존재한다면, 첫 번째 사원의 ID를 기본값으로 지정 / パラメータがないが社員一覧が存在する場合、最初の社員のIDを初期値として指定
	        payrollEmployeeId = empList.get(0).getPayrollEmployeeId();
	    }

	    // 3. 우측 상세 내역 조회 (사원 ID가 있을 때만 조회) / 3. 右側詳細内訳を照会（社員IDがある時のみ照会）
	    List<PaymentMntPayDetailDTO> payDetails = null;
	    List<PaymentMntDeductionDetailDTO> deductionDetails = null;

	    if (payrollEmployeeId != null) {
	        payDetails = payrollService.getPayDetails(payrollEmployeeId);
	        deductionDetails = payrollService.getDeductionDetails(payrollEmployeeId);
	    }

	    // ★ [추가된 부분] 4. DB에 등록된 전체 지급/공제 항목 마스터 조회 (하드코딩 제거 및 동적 출력용) / ★[追加部分] 4. DBに登録された全支給・控除項目マスタを照会（ハードコーディング除去および動的出力用）
	    List<PaymentMntPayItemDTO> payItemList = payrollService.getPayItemList();
	    List<PaymentMntDeductionItemDTO> deductionItemList = payrollService.getDeductionItemList();

	    // 5. JSP로 데이터 전달 / 5. JSPへデータを渡す
	    request.setAttribute("employeeList", empList);
	    request.setAttribute("payDetails", payDetails);
	    request.setAttribute("deductionDetails", deductionDetails);

	    // ★ [추가된 부분] 마스터 항목 리스트도 JSP로 전달 / ★[追加部分] マスタ項目リストもJSPへ渡す
	    request.setAttribute("payItemList", payItemList);
	    request.setAttribute("deductionItemList", deductionItemList);

	    return "/WEB-INF/pages/payment/paymentMnt.jsp";
	}
}