package retirement.retireProcess.service;

import java.sql.Connection;
import java.sql.SQLException;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import retirement.model.RetirementProcessModel;
import retirement.retireProcess.dao.RetirementProcessReadDao;

public class RetirementProcessUpdateService {
	private RetirementProcessReadDao updateDao = new RetirementProcessReadDao();

    public void processRetirement(RetirementProcessModel model) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            updateDao.updateRetirementProcess(conn, model);

            conn.commit(); // 성공 시 커밋
        } catch (SQLException e) {
            JdbcUtil.rollback(conn); // 실패 시 롤백
            throw new RuntimeException("퇴직 처리 중 오류가 발생했습니다.", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
}
