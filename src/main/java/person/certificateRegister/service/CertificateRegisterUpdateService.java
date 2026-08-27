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
    	// 渡されたデータがない場合は進めない
        if (issueNos == null || issueNos.length == 0) {
            return false;
        }
        
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false); 
            
          
            int count = updateDao.updateCertificateStatusToN(conn, issueNos);
            
            // 업데이트된 건수가 있으면 커밋
            // 更新された件数があればコミット
            if (count > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            JdbcUtil.rollback(conn); 
            throw new RuntimeException("削除中にDBエラーが発生", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
}