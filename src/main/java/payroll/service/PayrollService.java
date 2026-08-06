package payroll.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import jdbc.JdbcUtil; // 문 닫는 유틸
import connection.ConnectionProvider; // 문 여는 유틸 (방금 알려주신 파일)

import payroll.dao.PayrollDAO;
import payroll.dto.PayrollEmployeeDTO;
import payroll.dto.PayrollPayDetailDTO;
import payroll.dto.PayrollDeductionDetailDTO;

public class PayrollService {
    
    private PayrollDAO payrollDao = new PayrollDAO();

    // 1. 좌측 사원 리스트 가져오기
    public List<PayrollEmployeeDTO> getEmployeeList(Long payrollId) {
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
    public List<PayrollPayDetailDTO> getPayDetails(Long payrollEmployeeId) {
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
    public List<PayrollDeductionDetailDTO> getDeductionDetails(Long payrollEmployeeId) {
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
}