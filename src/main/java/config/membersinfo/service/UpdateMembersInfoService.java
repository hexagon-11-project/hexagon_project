package config.membersinfo.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.membersinfo.dao.CompanyInfoDao;
import config.model.CompanyInfo;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class UpdateMembersInfoService {
	private CompanyInfoDao companyInfoDao = new CompanyInfoDao();

    public void update(CompanyInfo info) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false); 

          
            companyInfoDao.update(conn, info);

            conn.commit(); 
        } catch (SQLException e) {
            JdbcUtil.rollback(conn); 
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
}

