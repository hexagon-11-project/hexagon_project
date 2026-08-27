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
            conn.setAutoCommit(false); 

            updateDao.updateRetirementProcess(conn, model);

            conn.commit(); 
        } catch (SQLException e) {
            JdbcUtil.rollback(conn); 
            throw new RuntimeException("退職処理中にエラーが発生しました。", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
}
