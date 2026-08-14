package config.payitemset.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.model.DeductionItem;
import config.payitemset.dao.DeductionItemDao;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class DeductionItemSetUpdateService {
	
	private DeductionItemDao deductionItemDao = new DeductionItemDao();
	
	public void update(DeductionItem item) {
		
		Connection conn = null;
		
		try {
			
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
			
			deductionItemDao.update(conn, item);
			
			conn.commit();
			
		} catch (SQLException e) {
			
			JdbcUtil.rollback(conn);
			
			throw new RuntimeException(e);
			
		} finally {
			
			JdbcUtil.close(conn);
			
		}
		
	}

}
