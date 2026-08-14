package payment.paymentMnt.controller;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import connection.ConnectionProvider;
import payment.paymentMnt.dao.PaymentMntDAO;
import payment.paymentMnt.dto.PaymentMntEffectiveDetail;
import payment.paymentMnt.dto.PaymentMntEmployeeDTO;
import payment.paymentMnt.dto.PaymentMntPayItemDTO;        // 추가
import payment.paymentMnt.dto.PaymentMntDeductionItemDTO;  // 추가
import payment.paymentMnt.dto.PaymentMntSummaryDTO;        // 추가 (급여 종합정보)
import payment.paymentMnt.service.PaymentMntService;       // 추가

// 급여 입력 및 관리를 처리하는 커맨드 핸들러 클래스
public class PaymentMntController implements CommandHandler {

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 한글 및 일본어 처리를 위한 인코딩 설정
		request.setCharacterEncoding("UTF-8");

		// 1. 화면에서 전달받은 검색 조건 (귀속연월) 파라미터 수집
        String payYear = request.getParameter("payYear");
        String payMonth = request.getParameter("payMonth");

        // 파라미터가 없다면 (메뉴에 처음 진입했을 때) 기본값을 '전월'로 세팅
        if (payYear == null || payMonth == null) {
            java.time.LocalDate prevMonthDate = java.time.LocalDate.now().minusMonths(1);
            
            payYear = String.valueOf(prevMonthDate.getYear());
            payMonth = String.format("%02d", prevMonthDate.getMonthValue());
        }

        String payYearMonth = payYear + payMonth;

		// 2. 화면에서 전달받은 급여차수 파라미터 수집 (★이 부분은 그대로 둡니다!)
		String paySeqStr = request.getParameter("paySequence");

		int paySequence = 1; // 기본값 차수 1
		if (paySeqStr != null && !paySeqStr.isEmpty()) {
		    paySequence = Integer.parseInt(paySeqStr);
		}

		// ... 이후 dao 호출 로직 ...

		PaymentMntDAO dao = new PaymentMntDAO();
		PaymentMntService payrollService = new PaymentMntService(); // 마스터 항목 조회를 위해 서비스 추가
		
		List<PaymentMntEmployeeDTO> employeeList = null; 

		// DB 연결 및 데이터 조회 수행
		Long payrollId = null; // ★ 추가 : 현재 귀속연월+차수의 정확한 PAYROLL_ID
		try (Connection conn = ConnectionProvider.getConnection()) {

			// ★ 추가 : 해당 연월+차수의 PAYROLL이 없으면 먼저 생성해두고, 정확한 PAYROLL_ID를 가져온다
			//   (신규 사원 추가 시 화면에서 이 값을 그대로 서버에 보내야 엉뚱한 회차에 등록되는 버그가 안 생김)
			dao.ensurePayrollExists(conn, payYearMonth, paySequence);
			payrollId = dao.selectPayrollId(conn, payYearMonth, paySequence);

			// 1. 먼저 선택된 귀속연월과 차수에 이미 저장된 급여 데이터가 있는지 확인합니다.
			if (payYearMonth != null && !payYearMonth.isEmpty()) {
				employeeList = dao.getPayrollEmployeeList(conn, payYearMonth, paySequence);
			}

			// 2. 저장된 급여 데이터가 있으면, 목록의 지급총액/공제총액/실지급액을 우측 상세패널과 동일한 로직
			//    (기본급/일용급여/공제 기본값 보정)으로 다시 계산해서 덮어쓴다.
			//    (PAYROLL_EMPLOYEE에 저장된 값이 상세내역과 어긋나 있어도 좌측 목록과 우측 패널이 항상 일치하도록)
			if (employeeList != null) {
				for (PaymentMntEmployeeDTO emp : employeeList) {
					PaymentMntEffectiveDetail effective = dao.computeEffectiveDetail(conn, emp.getPayrollEmployeeId());
					emp.setTotalPayAmount(effective.getTotalPayAmount());
					emp.setTotalDeductionAmount(effective.getTotalDeductionAmount());
					emp.setNetPayAmount(effective.getNetPayAmount());
				}
			}

			// 3. 만약 저장된 데이터가 없거나 처음 화면에 들어왔다면 전체 사원 목록 띄우기
			//    (아직 급여차수에 등록되지 않은 사원 목록이라 payrollEmployeeId가 없으므로 위 보정 대상이 아님)
			if (employeeList == null || employeeList.isEmpty()) {
				employeeList = dao.getModalEmployeeList(conn, null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// ★ [추가된 부분] 3. DB에 등록된 전체 지급/공제 항목 마스터 조회 (Service 내부에서 커넥션 맺고 끊음)
		List<PaymentMntPayItemDTO> payItemList = payrollService.getPayItemList();
		List<PaymentMntDeductionItemDTO> deductionItemList = payrollService.getDeductionItemList();

		// ★ [추가된 부분] 4. 하단 [급여 종합정보] 집계 조회
		// - 선택된 귀속연월(payYearMonth) + 급여차수(paySequence) 기준으로
		//   PAYROLL_EMPLOYEE 금액을 실시간 집계하므로, 사원 급여를 저장/수정한 뒤
		//   화면이 다시 로딩될 때마다(=저장 후 location.reload()) 자동으로 최신값이 반영됨
		PaymentMntSummaryDTO summaryInfo = payrollService.getPayrollSummary(payYearMonth, paySequence);

		// 조회된 사원 목록과 마스터 항목들을 request 객체에 담음
		request.setAttribute("employeeList", employeeList);
		request.setAttribute("payItemList", payItemList);
		request.setAttribute("deductionItemList", deductionItemList);
		request.setAttribute("summaryInfo", summaryInfo);
		request.setAttribute("payrollId", payrollId); // ★ 추가 : 화면 hidden input에 심어서 JS가 정확히 읽도록 함

		// 이동할 JSP View 페이지의 경로 리턴
		return "/WEB-INF/pages/payment/paymentMnt.jsp";
	}
}