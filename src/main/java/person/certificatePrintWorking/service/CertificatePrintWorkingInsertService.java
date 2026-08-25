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
                model.setRegId(companyInfo.getManagerName()); // '김민수'가 쏙 들어갑니다!
            } else {
                model.setRegId("시스템"); // 혹시 데이터가 없을 때를 대비한 기본값
            }
            
            int result = dao.insertCertificatePrintWorking(conn, model);
            
            if (result > 0) {
                conn.commit();
                return true; // 저장 성공
            } else {
                conn.rollback();
                return false; // 저장 실패 (INSERT 된 행이 없음)
            }
            
        } catch (SQLException e) {
            JdbcUtil.rollback(conn); // 예외 발생 시 롤백
            throw new RuntimeException("증명서 발급 저장 중 오류 발생: " + e.getMessage(), e);
        } finally {
            JdbcUtil.close(conn); 
        }
    }
}
