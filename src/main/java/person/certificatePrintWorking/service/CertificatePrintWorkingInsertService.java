package person.certificatePrintWorking.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.membersinfo.dao.CompanyInfoDao;
import config.model.CompanyInfo;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import person.certificatePrintWorking.Dao.CertificatePrintWorkingDao;
import person.model.CertificatePrintWorkingModel;

public class CertificatePrintWorkingInsertService {
	private CertificatePrintWorkingDao dao = new CertificatePrintWorkingDao();

    public boolean insertCertificatePrintWorking(CertificatePrintWorkingModel model) {
        Connection conn = null;
        try {
            // 1. DB 커넥션 가져오기
            conn = ConnectionProvider.getConnection();
            
            // 2. 트랜잭션 시작 (자동 커밋 방지)
            conn.setAutoCommit(false);
         // ==========================================================
            // [추가] CompanyInfoDao를 이용해 담당자(mng_yn='Y') 이름 조회
            // ==========================================================
            CompanyInfoDao companyDao = new CompanyInfoDao();
            // 회사 ID 1001번의 정보를 조회해 옵니다.
            CompanyInfo companyInfo = companyDao.selectById(conn, 1001); 
            
            // 담당자 이름이 정상적으로 조회되었다면, Model의 발급자(RegId)로 세팅합니다.
            if (companyInfo != null && companyInfo.getManagerName() != null) {
                model.setReg_Id(companyInfo.getManagerName()); // '김민수'가 쏙 들어갑니다!
            } else {
                model.setReg_Id("시스템"); // 혹시 데이터가 없을 때를 대비한 기본값
            }
            // ==========================================================
            
            // 3. DAO 호출하여 INSERT 실행
            int result = dao.insertCertificatePrintWorking(conn, model);
            
            // 4. 결과에 따른 커밋/롤백 처리
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
            JdbcUtil.close(conn); // 커넥션 반환
        }
    }
}
