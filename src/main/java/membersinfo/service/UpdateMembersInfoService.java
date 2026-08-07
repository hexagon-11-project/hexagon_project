package membersinfo.service;

import java.sql.Connection;
import java.sql.SQLException;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import membersinfo.dao.CompanyInfoDao;
import membersinfo.model.CompanyInfo;

public class UpdateMembersInfoService {
	private CompanyInfoDao companyInfoDao = new CompanyInfoDao();

    public void update(CompanyInfo info) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // DAO의 update 메서드 호출
            companyInfoDao.update(conn, info);

            conn.commit(); // 성공 시 커밋
        } catch (SQLException e) {
            JdbcUtil.rollback(conn); // 실패 시 롤백
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
}

