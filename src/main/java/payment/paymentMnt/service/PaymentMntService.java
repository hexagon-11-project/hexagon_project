package payment.paymentMnt.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import payment.paymentMnt.dao.PaymentMntDAO;
import payment.paymentMnt.dto.PaymentMntDeductionDetailDTO;
import payment.paymentMnt.dto.PaymentMntDeductionItemDTO;
import payment.paymentMnt.dto.PaymentMntEffectiveDetail;
import payment.paymentMnt.dto.PaymentMntEmployeeDTO;
import payment.paymentMnt.dto.PaymentMntPayDetailDTO;
import payment.paymentMnt.dto.PaymentMntPayItemDTO;
import payment.paymentMnt.dto.PaymentMntSummaryDTO;

// 급여입력관리 화면의 비즈니스 로직을 처리하는 서비스 클래스
// 給与入力管理画面のビジネスロジックを処理するサービスクラス
public class PaymentMntService {

	private PaymentMntDAO payrollDao = new PaymentMntDAO();

	public List<PaymentMntEmployeeDTO> getEmployeeList(Long payrollId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return payrollDao.selectEmployeeList(conn, payrollId);
		} catch (SQLException e) {
			throw new RuntimeException("급여 대상자 리스트 조회 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<PaymentMntPayDetailDTO> getPayDetails(Long payrollEmployeeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return payrollDao.selectPayDetails(conn, payrollEmployeeId);
		} catch (SQLException e) {
			throw new RuntimeException("지급 상세 내역 조회 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<PaymentMntDeductionDetailDTO> getDeductionDetails(Long payrollEmployeeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return payrollDao.selectDeductionDetails(conn, payrollEmployeeId);
		} catch (SQLException e) {
			throw new RuntimeException("공제 상세 내역 조회 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/** 사원별급여(payrollEmployeeId) 하나의 "최종" 지급/공제 상세 (저장된 값 + 기본급/일용급여/공제 기본값 보정 반영).
	 *  좌측 사원목록 총액과 우측 상세패널이 항상 같은 숫자가 나오도록, 목록/상세 양쪽에서 이 메서드만 사용한다.
	 *  社員別給与（payrollEmployeeId）1件の「最終」支給・控除詳細（保存値＋基本給・日雇い給与・控除初期値補正を反映）。
	 *  左側社員一覧の総額と右側詳細パネルが常に同じ数字になるよう、一覧・詳細の両方でこのメソッドのみ使用する。 */
	public PaymentMntEffectiveDetail getEffectiveDetail(Long payrollEmployeeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return payrollDao.computeEffectiveDetail(conn, payrollEmployeeId);
		} catch (SQLException e) {
			throw new RuntimeException("급여 상세 조회 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/** 신규추가 후, 방금 등록한 사원들의 화면 표시용 정보를 반환 (전체 새로고침 없이 해당 행만 추가하기 위함)
	 *  新規追加後、今登録した社員の画面表示用情報を返す（全体再読み込みなしで該当行だけ追加するため） */
	public List<PaymentMntEmployeeDTO> insertEmployees(Long payrollId, List<String> empIds) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			PaymentMntDAO dao = new PaymentMntDAO();

			List<PaymentMntEmployeeDTO> inserted = null;
			if (empIds != null && !empIds.isEmpty()) {
				dao.insertPayrollEmployees(conn, payrollId, empIds);
				inserted = dao.selectEmployeesByEmployeeIds(conn, payrollId, empIds);
			}

			conn.commit();
			return inserted;
		} catch (Exception e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/** 아직 이 급여차수(payrollId)에 등록되지 않은 사원(employeeId)을 PAYROLL_EMPLOYEE에 새로 등록하고,
	 *  방금 생성된(또는 이미 존재하던) payrollEmployeeId를 반환한다. insertPayrollEmployees가 NOT EXISTS로
	 *  멱등하게 처리하므로 이미 등록된 사원이 다시 들어와도 안전하다.
	 *  まだこの給与回（payrollId）に登録されていない社員（employeeId）をPAYROLL_EMPLOYEEに新規登録し、
	 *  今作成された（またはすでに存在していた）payrollEmployeeIdを返す。insertPayrollEmployeesがNOT EXISTSで
	 *  冪等に処理するため、すでに登録済みの社員が再度渡されても安全。 */
	public Long registerEmployeeToPayroll(Long payrollId, String employeeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			PaymentMntDAO dao = new PaymentMntDAO();
			List<String> empIds = java.util.Collections.singletonList(employeeId);
			dao.insertPayrollEmployees(conn, payrollId, empIds);
			List<PaymentMntEmployeeDTO> list = dao.selectEmployeesByEmployeeIds(conn, payrollId, empIds);

			conn.commit();
			return (list == null || list.isEmpty()) ? null : list.get(0).getPayrollEmployeeId();
		} catch (Exception e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("사원 급여 대상자 등록 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<PaymentMntPayItemDTO> getPayItemList() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return payrollDao.selectPayItemList(conn);
		} catch (SQLException e) {
			throw new RuntimeException("지급항목 마스터 조회 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<PaymentMntDeductionItemDTO> getDeductionItemList() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return payrollDao.selectDeductionItemList(conn);
		} catch (SQLException e) {
			throw new RuntimeException("공제항목 마스터 조회 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public void savePayrollDetails(Long empId, Map<Integer, Long> payItems, Map<Integer, Long> dedItems, long totalPay,
			long totalDed, long netPay) throws Exception {
		try (Connection conn = connection.ConnectionProvider.getConnection()) {
			conn.setAutoCommit(false); // 도중에 뻑나면 롤백하기 위해 수동 커밋 모드 / 途中で失敗した場合にロールバックするため手動コミットモード
			try {
				PaymentMntDAO dao = new PaymentMntDAO();

// 1. 지급항목 DB 업데이트 / 1. 支給項目DB更新
				for (Map.Entry<Integer, Long> entry : payItems.entrySet()) {
					dao.upsertPayDetail(conn, empId, entry.getKey(), entry.getValue());
				}

// 2. 공제항목 DB 업데이트 / 2. 控除項目DB更新
				for (Map.Entry<Integer, Long> entry : dedItems.entrySet()) {
					dao.upsertDeductionDetail(conn, empId, entry.getKey(), entry.getValue());
				}

// 3. 대상 사원의 총 지급/공제/실지급액 업데이트 / 3. 対象社員の総支給・控除・実支給額を更新
				dao.updateEmployeeTotals(conn, empId, totalPay, totalDed, netPay);

				conn.commit(); // 모두 성공하면 도장 쾅! / すべて成功したら確定！
			} catch (Exception e) {
				conn.rollback();
				throw e;
			}
		}
	}
	
	public int loadPreviousPayrollDataWithDelete(String prevYearMonth, int prevSeq, String currYearMonth, int currSeq) throws Exception {
        int count = 0;
        try (Connection conn = connection.ConnectionProvider.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PaymentMntDAO dao = new PaymentMntDAO();
                
                // ★ 1. 이번 달(예: 8월) 급여를 담을 '서류철(PAYROLL)'이 DB에 있는지 먼저 확인하고, 없으면 생성! / ★1. 今月（例：8月）の給与を入れる「フォルダ（PAYROLL）」がDBにあるか先に確認し、なければ生成！
                dao.ensurePayrollExists(conn, currYearMonth, currSeq);

                // 2. 현재 선택된 달의 기존 데이터(상세내역, 마스터) 싹 지우기 / 2. 現在選択された月の既存データ（詳細内訳、マスタ）をすべて削除
                dao.deletePayrollEmployeesByPeriod(conn, currYearMonth, currSeq);

                // 3. 선택한 이전 달 데이터를 복사하고, 넣은 사람 수를 반환받기 / 3. 選択した前月データをコピーし、追加した人数を受け取る
                count = dao.copyPreviousEmployeesCount(conn, prevYearMonth, prevSeq, currYearMonth, currSeq);

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
        return count; // 성공하면 3건 등 결과 리턴 / 成功すれば3件などの結果を返す
    }

	// ★ 하단 [급여 종합정보] 조회 (월 합계 / 지급총액 / 공제총액 / 실지급액) / ★下部[給与総合情報]照会（月合計／支給総額／控除総額／実支給額）
	public PaymentMntSummaryDTO getPayrollSummary(String payYearMonth, int paySequence) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return payrollDao.selectPayrollSummary(conn, payYearMonth, paySequence);
		} catch (SQLException e) {
			throw new RuntimeException("급여 종합정보 조회 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

}