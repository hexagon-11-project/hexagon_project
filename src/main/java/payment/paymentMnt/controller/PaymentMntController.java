package payment.paymentMnt.controller;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import connection.ConnectionProvider;
import payment.paymentMnt.dao.PaymentMntDAO;
import payment.paymentMnt.dto.PaymentMntEmployeeDTO;

// 급여 입력 및 관리를 처리하는 커맨드 핸들러 클래스
// 給与入力および管理を処理するコマンドハンドラークラス
public class PaymentMntController implements CommandHandler {

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 한글 및 일본어 처리를 위한 인코딩 설정
		// 韓国語および日本語処理のためのエンコーディング設定
		request.setCharacterEncoding("UTF-8");

		// 화면에서 전달받은 검색 조건 (귀속연월, 급여차수) 파라미터 수집
		// 画面から受け取った検索条件パラメータ（帰属年月、給与次数）の収集
		String payYearMonth = request.getParameter("payYearMonth");
		String paySeqStr = request.getParameter("paySequence");

		int paySequence = 1; // 기본값 차수 1 (デフォルト値：次数1)
		if (paySeqStr != null && !paySeqStr.isEmpty()) {
			paySequence = Integer.parseInt(paySeqStr);
		}

		PaymentMntDAO dao = new PaymentMntDAO();
		List<PaymentMntEmployeeDTO> employeeList = null; // List 타입이 PayrollEmployeeDTO로 수정됨

		// DB 연결 및 데이터 조회 수행
		// DB接続およびデータ照会の実行
		try (Connection conn = ConnectionProvider.getConnection()) {
			
			// 1. 먼저 선택된 귀속연월과 차수에 이미 저장된 급여 데이터가 있는지 확인합니다.
			if (payYearMonth != null && !payYearMonth.isEmpty()) {
				employeeList = dao.getPayrollEmployeeList(conn, payYearMonth, paySequence);
			}
			
			// ★ [추가된 부분] 2. 만약 저장된 데이터가 없거나 처음 화면에 들어왔다면?
			// 방금 모달창용으로 완벽하게 고쳐둔 전체 사원 목록을 가져와서 왼쪽에 쫙 띄워줍니다!
			if (employeeList == null || employeeList.isEmpty()) {
				employeeList = dao.getModalEmployeeList(conn, null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 조회된 사원 목록을 request 객체에 담음
		// 照会された社員リストをrequestオブジェクトに格納
		request.setAttribute("employeeList", employeeList);

		// 이동할 JSP View 페이지의 경로 리턴
		// 移動するJSP Viewページのパスをリターン
		return "/WEB-INF/pages/payment/paymentMnt.jsp";
	}
}