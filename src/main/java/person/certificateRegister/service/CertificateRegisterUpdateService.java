package person.certificateRegister.service;

import java.sql.Connection;
import java.sql.SQLException;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import person.certificateRegister.dao.CertificateRegisterDao;

public class CertificateRegisterUpdateService {
    
    private CertificateRegisterDao updateDao = new CertificateRegisterDao();

    public boolean softDeleteCertificates(String[] issueNos) {
        // 넘어온 데이터가 없으면 진행하지 않음
        if (issueNos == null || issueNos.length == 0) {
            return false;
        }
        
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작
            
            // DAO 호출
            int count = updateDao.updateCertificateStatusToN(conn, issueNos);
            
            // 업데이트된 건수가 있으면 커밋
            if (count > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            JdbcUtil.rollback(conn); // 예외 발생 시 롤백
            throw new RuntimeException("삭제중 DB 에러 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
}