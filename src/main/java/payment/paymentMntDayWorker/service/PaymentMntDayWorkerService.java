package payment.paymentMntDayWorker.service;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import payment.paymentMntDayWorker.dao.PaymentMntDayWorkerDAO;
import payment.paymentMntDayWorker.dto.PaymentMntDayWorkerDeductionItemDTO;
import payment.paymentMntDayWorker.dto.PaymentMntDayWorkerEmployeeDTO;
import payment.paymentMntDayWorker.model.PaymentMntDayWorkerDailyVO;
import payment.paymentMntDayWorker.model.PaymentMntDayWorkerDeductionVO;

public class PaymentMntDayWorkerService {

    private PaymentMntDayWorkerDAO dao = new PaymentMntDayWorkerDAO();

    /** 귀속연월+차수 헤더 조회/생성 후 PAYROLL_DAYWORKER_ID 반환 */
    public Long getOrCreateHeader(String payYearMonth, int paySequence) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return dao.ensurePayrollDayWorkerExists(conn, payYearMonth, paySequence);
        } catch (Exception e) {
            throw new RuntimeException("급여차수 헤더 조회/생성 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    /** 공제항목 마스터 목록 (화면 공제항목 패널을 DB 기준으로 동적 렌더링) */
    public List<PaymentMntDayWorkerDeductionItemDTO> getDeductionItemList() {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return dao.selectDeductionItemList(conn);
        } catch (Exception e) {
            throw new RuntimeException("공제항목 마스터 조회 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    public void updatePeriod(Long payrollDayWorkerId, String periodStart, String periodEnd, String payDate) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            dao.updatePeriod(conn, payrollDayWorkerId, periodStart, periodEnd, payDate);
        } catch (Exception e) {
            throw new RuntimeException("정산기간/급여지급일 수정 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    public List<PaymentMntDayWorkerEmployeeDTO> getEmployeeList(Long payrollDayWorkerId) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return dao.getPayrollDayWorkerEmployeeList(conn, payrollDayWorkerId);
        } catch (Exception e) {
            throw new RuntimeException("근로자 목록 조회 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    public List<PaymentMntDayWorkerEmployeeDTO> getModalEmployeeList(String keyword) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return dao.getModalEmployeeList(conn, keyword);
        } catch (Exception e) {
            throw new RuntimeException("근로자 검색 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    public void insertEmployees(Long payrollDayWorkerId, List<String> empIds) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            dao.insertDayWorkerEmployees(conn, payrollDayWorkerId, empIds);
            conn.commit();
        } catch (Exception e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException("근로자 신규추가 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    public void deleteEmployees(List<Long> payrollDayWorkerEmployeeIds) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            dao.deleteDayWorkerEmployees(conn, payrollDayWorkerEmployeeIds);
            conn.commit();
        } catch (Exception e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException("선택삭제 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    public void deleteAllEmployees(Long payrollDayWorkerId) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            dao.deleteAllDayWorkerEmployees(conn, payrollDayWorkerId);
            conn.commit();
        } catch (Exception e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException("전체삭제 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    /** 근로자 클릭 시 우측 상세(일자별 내역 + 공제항목) 조회 */
    public List<PaymentMntDayWorkerDailyVO> getDailyList(Long payrollDayWorkerEmployeeId) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return dao.selectDailyList(conn, payrollDayWorkerEmployeeId);
        } catch (Exception e) {
            throw new RuntimeException("일자별 내역 조회 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    public PaymentMntDayWorkerDeductionVO getDeduction(Long payrollDayWorkerEmployeeId) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return dao.selectDeduction(conn, payrollDayWorkerEmployeeId);
        } catch (Exception e) {
            throw new RuntimeException("공제항목 조회 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    /** 저장: 일자별 내역 + 공제항목을 한번에 저장, 합계도 갱신 */
    public void saveDetail(Long payrollDayWorkerEmployeeId, List<PaymentMntDayWorkerDailyVO> dailyList,
                            PaymentMntDayWorkerDeductionVO deduction) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            dao.deleteDailyByEmployee(conn, payrollDayWorkerEmployeeId);
            long totalPay = 0;
            if (dailyList != null) {
                for (PaymentMntDayWorkerDailyVO d : dailyList) {
                    d.setPayrollDayWorkerEmployeeId(payrollDayWorkerEmployeeId);
                    totalPay += d.getPayAmt() == null ? 0L : d.getPayAmt();
                }
                dao.insertDailyList(conn, payrollDayWorkerEmployeeId, dailyList);
            }

            deduction.setPayrollDayWorkerEmployeeId(payrollDayWorkerEmployeeId);
            dao.upsertDeduction(conn, deduction);
            long totalDed = deduction.getTotal();

            dao.updateEmployeeTotals(conn, payrollDayWorkerEmployeeId, totalPay, totalDed, totalPay - totalDed);

            conn.commit();
        } catch (Exception e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException("급여상세 저장 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    /** 지난급여 불러오기: 현재 차수 데이터 삭제 후 이전 차수 데이터 복사 */
    public int loadPreviousPayrollData(String prevYearMonth, int prevSeq, String currYearMonth, int currSeq) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            Long currId = dao.ensurePayrollDayWorkerExists(conn, currYearMonth, currSeq);
            dao.deleteAllDayWorkerEmployees(conn, currId);
            int count = dao.copyPreviousEmployees(conn, prevYearMonth, prevSeq, currId);

            conn.commit();
            return count;
        } catch (Exception e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException("지난급여 불러오기 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    public Map<String, Object> getSummary(Long payrollDayWorkerId) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return dao.selectSummary(conn, payrollDayWorkerId);
        } catch (Exception e) {
            throw new RuntimeException("급여 종합정보 조회 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
}
