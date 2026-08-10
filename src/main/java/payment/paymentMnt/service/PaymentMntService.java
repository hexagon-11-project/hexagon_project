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
import payment.paymentMnt.dto.PaymentMntEmployeeDTO;
import payment.paymentMnt.dto.PaymentMntPayDetailDTO;
import payment.paymentMnt.dto.PaymentMntPayItemDTO;

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

	public void insertEmployees(Long payrollId, List<String> empIds) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			PaymentMntDAO dao = new PaymentMntDAO();

			if (empIds != null && !empIds.isEmpty()) {
				dao.insertPayrollEmployees(conn, payrollId, empIds);
			}

			conn.commit();
		} catch (Exception e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
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
			conn.setAutoCommit(false); // 도중에 뻑나면 롤백하기 위해 수동 커밋 모드
			try {
				PaymentMntDAO dao = new PaymentMntDAO();

// 1. 지급항목 DB 업데이트
				for (Map.Entry<Integer, Long> entry : payItems.entrySet()) {
					dao.upsertPayDetail(conn, empId, entry.getKey(), entry.getValue());
				}

// 2. 공제항목 DB 업데이트
				for (Map.Entry<Integer, Long> entry : dedItems.entrySet()) {
					dao.upsertDeductionDetail(conn, empId, entry.getKey(), entry.getValue());
				}

// 3. 대상 사원의 총 지급/공제/실지급액 업데이트
				dao.updateEmployeeTotals(conn, empId, totalPay, totalDed, netPay);

				conn.commit(); // 모두 성공하면 도장 쾅!
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
                
                // ★ 1. 이번 달(예: 8월) 급여를 담을 '서류철(PAYROLL)'이 DB에 있는지 먼저 확인하고, 없으면 생성!
                dao.ensurePayrollExists(conn, currYearMonth, currSeq);
                
                // 2. 현재 선택된 달의 기존 데이터(상세내역, 마스터) 싹 지우기
                dao.deletePayrollEmployeesByPeriod(conn, currYearMonth, currSeq);
                
                // 3. 선택한 이전 달 데이터를 복사하고, 넣은 사람 수를 반환받기
                count = dao.copyPreviousEmployeesCount(conn, prevYearMonth, prevSeq, currYearMonth, currSeq);
                
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
        return count; // 성공하면 3건 등 결과 리턴
    }

}