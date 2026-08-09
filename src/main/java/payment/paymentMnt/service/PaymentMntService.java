package payment.paymentMnt.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import jdbc.JdbcUtil; // 문 닫는 유틸
import payment.paymentMnt.dao.PaymentMntDAO;
import payment.paymentMnt.dto.PaymentMntDeductionDetailDTO;
import payment.paymentMnt.dto.PaymentMntEmployeeDTO;
import payment.paymentMnt.dto.PaymentMntPayDetailDTO;
import connection.ConnectionProvider; // 문 여는 유틸 (방금 알려주신 파일)

public class PaymentMntService {
    
    private PaymentMntDAO payrollDao = new PaymentMntDAO();

    // 1. 좌측 사원 리스트 가져오기
    public List<PaymentMntEmployeeDTO> getEmployeeList(Long payrollId) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection(); // 커넥션 풀에서 연결 가져오기
            return payrollDao.selectEmployeeList(conn, payrollId);
        } catch (SQLException e) {
            throw new RuntimeException("급여 대상자 리스트 조회 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn); // 안전하게 연결 반환
        }
    }

    // 2. 우측 지급항목 가져오기
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

    // 3. 우측 공제항목 가져오기
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
 // 4. 사원 추가하기 (모달창에서 선택된 사원들을 급여 대상자로 등록)
    public void insertEmployees(Long payrollId, List<String> empIds) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작
            
            // DAO를 호출하여 DB에 사원 INSERT 수행
            payrollDao.insertPayrollEmployees(conn, payrollId, empIds);
            
            conn.commit(); // 커밋
        } catch (SQLException e) {
            JdbcUtil.rollback(conn); // 오류 시 롤백
            throw new RuntimeException("사원 추가 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
}