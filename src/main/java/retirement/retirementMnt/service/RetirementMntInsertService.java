package retirement.retirementMnt.service;

import java.sql.Connection;
import java.sql.SQLException;

import connection.ConnectionProvider;
import retirement.model.RetirementMntModel;
import retirement.retirementMnt.dao.RetirementMntDao;

public class RetirementMntInsertService {
    
    private RetirementMntDao retirementDao = new RetirementMntDao();

    public int saveRetirementData(RetirementMntModel model) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false); 
            
            int result = retirementDao.RetirementMntInsert(conn, model);
            
            conn.commit(); 
            return result;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {} 
            }
            e.printStackTrace();
            throw new RuntimeException("퇴직급여 저장 중 오류가 발생했습니다.", e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) {}
            }
        }
    }
}