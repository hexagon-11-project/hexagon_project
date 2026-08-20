package retirement.retirementMnt.service;

import java.sql.Connection;
import java.sql.SQLException;

import connection.ConnectionProvider;
import retirement.model.RetirementMntModel;
import retirment.retirementMnt.dao.RetirementMntDao; // DAO 임포트 필수

public class RetirementMntInsertService {
    
    // 이 선언이 반드시 있어야 retirementDao를 인식할 수 있습니다.
    private RetirementMntDao retirementDao = new RetirementMntDao();

    public int saveRetirementData(RetirementMntModel model) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작
            
            int result = retirementDao.RetirementMntInsert(conn, model);
            
            conn.commit(); // 성공 시 커밋
            return result;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {} // 실패 시 롤백
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