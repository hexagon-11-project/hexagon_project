package person.certificatePrintWorking.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.membersinfo.dao.CompanyInfoDao;
import config.model.CompanyInfo;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import person.certificatePrintWorking.dao.CertificatePrintWorkingDao;
import person.model.CertificatePrintWorkingModel;

public class CertificatePrintWorkingInsertService {
	private CertificatePrintWorkingDao dao = new CertificatePrintWorkingDao();

    public boolean insertCertificatePrintWorking(CertificatePrintWorkingModel model) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            
            conn.setAutoCommit(false);
            CompanyInfoDao companyDao = new CompanyInfoDao();
            CompanyInfo companyInfo = companyDao.selectById(conn, 1001); 
            
            if (companyInfo != null && companyInfo.getManagerName() != null) {
                model.setRegId(companyInfo.getManagerName()); 
            } else {
                model.setRegId("system"); // 혹시 데이터가 없을 때를 대비한 기본값
            }							  // もしデータがない場合に備えたデフォルト値
            
            int result = dao.insertCertificatePrintWorking(conn, model);
            
            if (result > 0) {
                conn.commit();
                return true; 
            } else {
                conn.rollback();
                return false; 
            }
            
        } catch (SQLException e) {
            JdbcUtil.rollback(conn); 
            throw new RuntimeException("証明書発行の保存中にエラーが発生： " + e.getMessage(), e);
        } finally {
            JdbcUtil.close(conn); 
        }
    }
}
