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

	public List<CertificatePrintWorkingModel> getCertificateList(String startDate, String endDate, String certType, String empName) {
		Connection conn = null;
		
		try {
			conn = ConnectionProvider.getConnection();
			
			return certificateRegisterDao.getAllCertificateList(conn, startDate, endDate, certType, empName);
			
		} catch (SQLException e) {
			throw new RuntimeException("証明書一覧の照会中にDBエラーが発生", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}