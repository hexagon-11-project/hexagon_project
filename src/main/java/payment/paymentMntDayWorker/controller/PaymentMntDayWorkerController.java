package payment.paymentMntDayWorker.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMntDayWorker.dto.PaymentMntDayWorkerDeductionItemDTO;
import payment.paymentMntDayWorker.dto.PaymentMntDayWorkerEmployeeDTO;
import payment.paymentMntDayWorker.service.PaymentMntDayWorkerService;

// 급여입력/관리(일용직) 화면 초기 진입 컨트롤러
// 給与入力・管理（日雇い）画面の初期アクセスコントローラー
public class PaymentMntDayWorkerController implements CommandHandler {

    private PaymentMntDayWorkerService service = new PaymentMntDayWorkerService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        // 1. 귀속연월 파라미터 수집 (없으면 전월을 기본값으로) / 1. 帰属年月パラメータを収集（なければ前月を初期値に）
        String payYear = request.getParameter("payYear");
        String payMonth = request.getParameter("payMonth");
        if (payYear == null || payMonth == null || payYear.isEmpty() || payMonth.isEmpty()) {
            LocalDate prevMonthDate = LocalDate.now().minusMonths(1);
            payYear = String.valueOf(prevMonthDate.getYear());
            payMonth = String.format("%02d", prevMonthDate.getMonthValue());
        }
        String payYearMonth = payYear + payMonth;

        // 2. 급여차수 파라미터 수집 (없으면 1차) / 2. 給与回パラメータを収集（なければ1回）
        String paySeqStr = request.getParameter("paySequence");
        int paySequence = 1;
        if (paySeqStr != null && !paySeqStr.isEmpty()) {
            paySequence = Integer.parseInt(paySeqStr);
        }

        // 3. 헤더(PAYROLL_DAYWORKER) 조회/생성 후 등록된 근로자 목록 조회 / 3. ヘッダー（PAYROLL_DAYWORKER）を照会・生成後、登録された労働者一覧を照会
        Long payrollDayWorkerId = service.getOrCreateHeader(payYearMonth, paySequence);
        List<PaymentMntDayWorkerEmployeeDTO> employeeList = service.getEmployeeList(payrollDayWorkerId);

        // 4. 공제항목 패널을 DB(DEDUCTION_ITEM) 기준으로 동적 렌더링하기 위한 마스터 목록 / 4. 控除項目パネルをDB（DEDUCTION_ITEM）基準で動的レンダリングするためのマスタ一覧
        List<PaymentMntDayWorkerDeductionItemDTO> deductionItemList = service.getDeductionItemList();

        // 5. 하단 [급여 종합정보] - 이 급여차수의 일용직 사원 전체 합계 (사원 추가/삭제/저장될 때마다 최신값) / 5. 下部[給与総合情報] - この給与回の日雇い社員全体の合計（社員追加・削除・保存のたびに最新値）
        Map<String, Object> summaryInfo = service.getSummary(payrollDayWorkerId);

        request.setAttribute("employeeList", employeeList);
        request.setAttribute("payrollDayWorkerId", payrollDayWorkerId);
        request.setAttribute("deductionItemList", deductionItemList);
        request.setAttribute("summaryInfo", summaryInfo);

        return "/WEB-INF/pages/payment/paymentMntDayWorker.jsp";
    }
}
