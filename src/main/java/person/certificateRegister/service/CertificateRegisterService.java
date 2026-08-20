package person.certificateRegister.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import connection.ConnectionProvider; 
import jdbc.JdbcUtil;
import person.certificateRegister.dao.CertificateRegisterDao;
import person.model.CertificatePrintWorkingModel;

public class CertificateRegisterService {
	
	private CertificateRegisterDao certificateRegisterDao = new CertificateRegisterDao();

	// 핸들러에서 호출하는 메서드 (파라미터 없음)
	public List<CertificatePrintWorkingModel> getCertificateList(String startDate, String endDate, String certType, String empName) {
		Connection conn = null;
		
		try {
			// 1. 커넥션 풀에서 Connection 획득
			conn = ConnectionProvider.getConnection();
			
			// 2. DAO에 Connection을 넘겨서 쿼리 실행 결과 받아오기
			return certificateRegisterDao.getAllCertificateList(conn, startDate, endDate, certType, empName);
			
		} catch (SQLException e) {
			// 예외 발생 시 Handler에서 처리할 수 있도록 RuntimeException으로 던짐
			throw new RuntimeException("증명서 목록 조회 중 DB 에러 발생", e);
		} finally {
			// 3. 커넥션 반납
			JdbcUtil.close(conn);
		}
	}
}