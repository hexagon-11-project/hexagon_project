package payment.paymentMntDayWorker.controller;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMntDayWorker.dto.PaymentMntDayWorkerDeductionItemDTO;
import payment.paymentMntDayWorker.dto.PaymentMntDayWorkerEmployeeDTO;
import payment.paymentMntDayWorker.service.PaymentMntDayWorkerService;

// 급여입력/관리(일용직) 화면 초기 진입 컨트롤러
public class PaymentMntDayWorkerController implements CommandHandler {

    private PaymentMntDayWorkerService service = new PaymentMntDayWorkerService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        // 1. 귀속연월 파라미터 수집 (없으면 전월을 기본값으로)
        String payYear = request.getParameter("payYear");
        String payMonth = request.getParameter("payMonth");
        if (payYear == null || payMonth == null || payYear.isEmpty() || payMonth.isEmpty()) {
            LocalDate prevMonthDate = LocalDate.now().minusMonths(1);
            payYear = String.valueOf(prevMonthDate.getYear());
            payMonth = String.format("%02d", prevMonthDate.getMonthValue());
        }
        String payYearMonth = payYear + payMonth;

        // 2. 급여차수 파라미터 수집 (없으면 1차)
        String paySeqStr = request.getParameter("paySequence");
        int paySequence = 1;
        if (paySeqStr != null && !paySeqStr.isEmpty()) {
            paySequence = Integer.parseInt(paySeqStr);
        }

        // 3. 헤더(PAYROLL_DAYWORKER) 조회/생성 후 등록된 근로자 목록 조회
        Long payrollDayWorkerId = service.getOrCreateHeader(payYearMonth, paySequence);
        List<PaymentMntDayWorkerEmployeeDTO> employeeList = service.getEmployeeList(payrollDayWorkerId);

        // 4. 공제항목 패널을 DB(DEDUCTION_ITEM) 기준으로 동적 렌더링하기 위한 마스터 목록
        List<PaymentMntDayWorkerDeductionItemDTO> deductionItemList = service.getDeductionItemList();

        request.setAttribute("employeeList", employeeList);
        request.setAttribute("payrollDayWorkerId", payrollDayWorkerId);
        request.setAttribute("deductionItemList", deductionItemList);

        return "/WEB-INF/pages/payment/paymentMntDayWorker.jsp";
    }
}
