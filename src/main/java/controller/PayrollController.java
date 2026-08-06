package controller;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import connection.ConnectionProvider;
import payroll.dao.PayrollDAO;
import payroll.dto.PayrollEmployeeDTO; // PayrollDTO -> PayrollEmployeeDTO로 수정됨

// 급여 입력 및 관리를 처리하는 커맨드 핸들러 클래스
// 給与入力および管理を処理するコマンドハンドラークラス
public class PayrollController implements CommandHandler {

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

		PayrollDAO dao = new PayrollDAO();
		List<PayrollEmployeeDTO> employeeList = null; // List 타입이 PayrollEmployeeDTO로 수정됨

		// DB 연결 및 데이터 조회 수행
		// DB接続およびデータ照会の実行
		try (Connection conn = ConnectionProvider.getConnection()) {
			// 이 부분의 에러를 없애려면 DAO에도 아래 메서드가 추가되어 있어야 합니다.
			employeeList = dao.getPayrollEmployeeList(conn, payYearMonth, paySequence);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 조회된 사원 목록을 request 객체에 담음
		// 照会された社員リストをrequestオブジェクトに格納
		request.setAttribute("employeeList", employeeList);

		// 이동할 JSP View 페이지의 경로 리턴
		// 移動するJSP Viewページのパスをリターン
		return "/WEB-INF/pages/payroll/input.jsp";
	}
}