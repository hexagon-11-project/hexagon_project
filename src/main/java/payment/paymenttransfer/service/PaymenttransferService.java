package payment.paymenttransfer.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import payment.model.PaymentTransfer;
import payment.paymenttransfer.dao.PaymenttransferDao;

/**
 * 급여이체 신청 Service.
 * 給与振込申請Service。
 *
 */
public class PaymenttransferService {

	private PaymenttransferDao transferDao = new PaymenttransferDao();

	/**
	 * 귀속연/월/차수로 이체 대상(사원) 목록 조회
	 * 帰属年/月/次数で振込対象(社員)一覧を照会する。
	 *
	 */
	public List<PaymentTransfer> getTransferList(String payYear, String payMonth, int paySequence) {
		String payYearMonth = toPayYearMonth(payYear, payMonth);
		if (payYearMonth == null) {
			return Collections.emptyList();
		}

		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return transferDao.selectByYearMonthSeq(conn, payYearMonth, paySequence);
		} catch (SQLException e) {
			throw new RuntimeException("급여이체 대상 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/**
	 * [급여이체 신청] 체크된 행 기준으로 DB 저장
	 * [給与振込申請] チェックされた行を基準にDBへ保存する。
	 *
	 */
	public int applyTransferRequest(String payYear, String payMonth, int paySequence, String[] selectedIds) {
		// 체크된 행이 하나도 없으면 저장하지 않음
		// チェックされた行が1件もなければ保存しない。
		if (selectedIds == null || selectedIds.length == 0) {
			return 0;
		}

		String payYearMonth = toPayYearMonth(payYear, payMonth);
		if (payYearMonth == null) {
			throw new IllegalArgumentException("귀속연/월이 올바르지 않습니다.");
		}

		int[] payrollEmployeeIds = parseIds(selectedIds);
		if (payrollEmployeeIds.length == 0) {
			return 0;
		}

		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			// 조회 조건에 해당하는 급여작업 PK
			// 照会条件に該当する給与作業PK。
			Integer payrollId = transferDao.selectPayrollId(conn, payYearMonth, paySequence);
			if (payrollId == null) {
				throw new IllegalArgumentException("해당 귀속연월/차수의 급여작업이 없습니다.");
			}

			// 체크된 행이 이 급여작업 소속인지 확인 (조작된 ID 방지)
			// チェックされた行がこの給与作業に属するかを確認する (操作されたIDの防止)。
			int validCount = transferDao.countSelectedInPayroll(conn, payrollId, payrollEmployeeIds);
			if (validCount == 0) {
				conn.rollback();
				return 0;
			}

			// 체크가 유효하면 이체신청 저장 (급여작업 단위 1건)
			// チェックが有効なら振込申請を保存する (給与作業単位で1件)。
			if (transferDao.existsTransferRequest(conn, payrollId)) {
				transferDao.updateTransferRequest(conn, payrollId); // 재신청 → UPDATE / 再申請 → UPDATE。
			} else {
				transferDao.insertTransferRequest(conn, payrollId); // 최초 신청 → INSERT / 初回申請 → INSERT。
			}

			conn.commit();
			return validCount;
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("급여이체 신청 저장 중 DB 오류 발생", e);
		} finally {
			try {
				if (conn != null) {
					conn.setAutoCommit(true);
				}
			} catch (SQLException ignore) {
			}
			JdbcUtil.close(conn);
		}
	}

	/**
	 * 목록의 실지급액 합계를 구한다.
	 * 一覧の実支給額合計を求める。
	 *
	 */
	public long sumNetPayAmount(List<PaymentTransfer> list) {
		long sum = 0L;
		if (list == null) {
			return sum;
		}
		for (PaymentTransfer row : list) {
			sum += row.getNetPayAmount();
		}
		return sum;
	}

	/**
	 * 귀속연 + 귀속월 → PAY_YEAR_MONTH(YYYYMM)
	 * 帰属年 + 帰属月 → PAY_YEAR_MONTH(YYYYMM)。
	 *
	 */
	public String toPayYearMonth(String payYear, String payMonth) {
		if (payYear == null || payYear.trim().isEmpty() || payMonth == null || payMonth.trim().isEmpty()) {
			return null;
		}
		String year = payYear.trim();
		String month = payMonth.trim();
		if (month.length() == 1) {
			month = "0" + month;
		}
		if (year.length() != 4 || month.length() != 2) {
			return null;
		}
		return year + month;
	}

	/**
	 * 체크박스 value 문자열 배열 → int 배열
	 * チェックボックスvalueの文字列配列 → int配列。
	 *
	 */
	private int[] parseIds(String[] selectedIds) {
		int[] ids = new int[selectedIds.length];
		int count = 0;
		for (String raw : selectedIds) {
			if (raw == null || raw.trim().isEmpty()) {
				continue;
			}
			ids[count++] = Integer.parseInt(raw.trim());
		}
		if (count == ids.length) {
			return ids;
		}
		int[] trimmed = new int[count];
		System.arraycopy(ids, 0, trimmed, 0, count);
		return trimmed;
	}
}
