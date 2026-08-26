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
import payment.paymentMnt.dto.PaymentMntPayItemDTO;        // 추가 / 追加
import payment.paymentMnt.dto.PaymentMntDeductionItemDTO;  // 추가 / 追加
import payment.paymentMnt.dto.PaymentMntSummaryDTO;        // 추가 (급여 종합정보) / 追加（給与総合情報）
import payment.paymentMnt.service.PaymentMntService;       // 추가 / 追加

// 급여 입력 및 관리를 처리하는 커맨드 핸들러 클래스
// 給与入力および管理を処理するコマンドハンドラークラス
public class PaymentMntController implements CommandHandler {

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 한글 및 일본어 처리를 위한 인코딩 설정 / 韓国語および日本語処理のためのエンコーディング設定
		request.setCharacterEncoding("UTF-8");

		// 1. 화면에서 전달받은 검색 조건 (귀속연월) 파라미터 수집 / 1. 画面から渡された検索条件（帰属年月）パラメータを収集
        String payYear = request.getParameter("payYear");
        String payMonth = request.getParameter("payMonth");

        // 파라미터가 없다면 (메뉴에 처음 진입했을 때) 기본값을 '전월'로 세팅 / パラメータがなければ（メニューに初回アクセスした時）初期値を「前月」に設定
        if (payYear == null || payMonth == null) {
            java.time.LocalDate prevMonthDate = java.time.LocalDate.now().minusMonths(1);

            payYear = String.valueOf(prevMonthDate.getYear());
            payMonth = String.format("%02d", prevMonthDate.getMonthValue());
        }

        String payYearMonth = payYear + payMonth;

		// 2. 화면에서 전달받은 급여차수 파라미터 수집 (★이 부분은 그대로 둡니다!) / 2. 画面から渡された給与回パラメータを収集（★この部分はそのままにします！）
		String paySeqStr = request.getParameter("paySequence");

		int paySequence = 1; // 기본값 차수 1 / 初期値は1回
		if (paySeqStr != null && !paySeqStr.isEmpty()) {
		    paySequence = Integer.parseInt(paySeqStr);
		}

		// ... 이후 dao 호출 로직 ... / ... 以降dao呼び出しロジック ...

		PaymentMntDAO dao = new PaymentMntDAO();
		PaymentMntService payrollService = new PaymentMntService(); // 마스터 항목 조회를 위해 서비스 추가 / マスタ項目照会のためService追加

		List<PaymentMntEmployeeDTO> employeeList = null;

		// DB 연결 및 데이터 조회 수행 / DB接続およびデータ照会を実行
		Long payrollId = null; // ★ 추가 : 현재 귀속연월+차수의 정확한 PAYROLL_ID / ★追加：現在の帰属年月＋回の正確なPAYROLL_ID
		try (Connection conn = ConnectionProvider.getConnection()) {

			// ★ 추가 : 해당 연월+차수의 PAYROLL이 없으면 먼저 생성해두고, 정확한 PAYROLL_ID를 가져온다 / ★追加：該当年月＋回のPAYROLLがなければ先に生成しておき、正確なPAYROLL_IDを取得する
			//   (신규 사원 추가 시 화면에서 이 값을 그대로 서버에 보내야 엉뚱한 회차에 등록되는 버그가 안 생김) / （新規社員追加時、画面からこの値をそのままサーバーへ送らないと、誤った回に登録されるバグが発生する）
			dao.ensurePayrollExists(conn, payYearMonth, paySequence);
			payrollId = dao.selectPayrollId(conn, payYearMonth, paySequence);

			// 1. 먼저 선택된 귀속연월과 차수에 이미 등록(저장)된 사원 목록을 확인합니다. / 1. まず選択された帰属年月と回にすでに登録（保存）済みの社員一覧を確認します。
			List<PaymentMntEmployeeDTO> registeredList = new java.util.ArrayList<>();
			if (payYearMonth != null && !payYearMonth.isEmpty()) {
				registeredList = dao.getPayrollEmployeeList(conn, payYearMonth, paySequence);
			}

			// 2. 등록된 사원이 있으면, 지급총액/공제총액/실지급액을 우측 상세패널과 동일한 로직 / 2. 登録済みの社員がいれば、支給総額・控除総額・実支給額を右側詳細パネルと同一ロジックで
			//    (기본급/일용급여/공제 기본값 보정)으로 다시 계산해서 덮어쓴다. / （基本給・日雇い給与・控除初期値補正）で再計算して上書きする。
			//    (PAYROLL_EMPLOYEE에 저장된 값이 상세내역과 어긋나 있어도 좌측 목록과 우측 패널이 항상 일치하도록) / （PAYROLL_EMPLOYEEに保存された値が詳細内訳とずれていても、左側一覧と右側パネルが常に一致するように）
			java.util.Map<String, PaymentMntEmployeeDTO> registeredByEmployeeId = new java.util.HashMap<>();
			if (!registeredList.isEmpty()) {
				// 사원마다 반복 조회하던 지급항목 기본값(마스터 전체 조회)을 루프 밖에서 한 번만 조회해 재사용 / 社員ごとに繰り返し照会していた支給項目初期値（マスタ全件照会）をループの外で一度だけ照会して再利用
				java.util.Map<Long, Long> bulkDefaults = dao.selectPayItemBulkDefaults(conn);
				for (PaymentMntEmployeeDTO emp : registeredList) {
					PaymentMntEffectiveDetail effective = dao.computeEffectiveDetail(conn, emp.getPayrollEmployeeId(), bulkDefaults);
					emp.setTotalPayAmount(effective.getTotalPayAmount());
					emp.setTotalDeductionAmount(effective.getTotalDeductionAmount());
					emp.setNetPayAmount(effective.getNetPayAmount());
					registeredByEmployeeId.put(emp.getEmployeeId(), emp);
				}
			}

			// 3. 등록 여부와 상관없이 항상 전체 사원 목록을 보여준다. / 3. 登録有無に関わらず常に全社員一覧を表示する。
			//    (일부 사원만 등록된 상태라도 나머지 미등록 사원들이 화면에서 사라지면 안 되므로, / （一部の社員だけ登録された状態でも、残りの未登録社員が画面から消えてはいけないため、
			//     전체 목록을 기준으로 삼고 이미 등록된 사원만 위에서 계산한 실제 저장값으로 덮어쓴다) / 全社員一覧を基準にし、すでに登録済みの社員だけ上で計算した実際の保存値で上書きする）
			employeeList = dao.getModalEmployeeList(conn, null);
			for (PaymentMntEmployeeDTO emp : employeeList) {
				PaymentMntEmployeeDTO registered = registeredByEmployeeId.get(emp.getEmployeeId());
				if (registered != null) {
					emp.setPayrollEmployeeId(registered.getPayrollEmployeeId());
					emp.setTotalPayAmount(registered.getTotalPayAmount());
					emp.setTotalDeductionAmount(registered.getTotalDeductionAmount());
					emp.setNetPayAmount(registered.getNetPayAmount());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// ★ [추가된 부분] 3. DB에 등록된 전체 지급/공제 항목 마스터 조회 (Service 내부에서 커넥션 맺고 끊음) / ★[追加部分] 3. DBに登録された全支給・控除項目マスタを照会（Service内部でコネクションを接続・切断）
		List<PaymentMntPayItemDTO> payItemList = payrollService.getPayItemList();
		List<PaymentMntDeductionItemDTO> deductionItemList = payrollService.getDeductionItemList();

		// ★ [추가된 부분] 4. 하단 [급여 종합정보] 집계 조회 / ★[追加部分] 4. 下部[給与総合情報]集計照会
		// - 선택된 귀속연월(payYearMonth) + 급여차수(paySequence) 기준으로 / - 選択された帰属年月（payYearMonth）＋給与回（paySequence）基準で
		//   PAYROLL_EMPLOYEE 금액을 실시간 집계하므로, 사원 급여를 저장/수정한 뒤 / PAYROLL_EMPLOYEE金額をリアルタイム集計するため、社員給与を保存・修正した後、
		//   화면이 다시 로딩될 때마다(=저장 후 location.reload()) 자동으로 최신값이 반영됨 / 画面が再読み込みされるたび（＝保存後location.reload()）自動的に最新値が反映される
		PaymentMntSummaryDTO summaryInfo = payrollService.getPayrollSummary(payYearMonth, paySequence);

		// 조회된 사원 목록과 마스터 항목들을 request 객체에 담음 / 照会された社員一覧とマスタ項目をrequestオブジェクトに格納
		request.setAttribute("employeeList", employeeList);
		request.setAttribute("payItemList", payItemList);
		request.setAttribute("deductionItemList", deductionItemList);
		request.setAttribute("summaryInfo", summaryInfo);
		request.setAttribute("payrollId", payrollId); // ★ 추가 : 화면 hidden input에 심어서 JS가 정확히 읽도록 함 / ★追加：画面のhidden inputに埋め込み、JSが正確に読めるようにする

		// 이동할 JSP View 페이지의 경로 리턴 / 遷移するJSP Viewページのパスを返す
		return "/WEB-INF/pages/payment/paymentMnt.jsp";
	}
}