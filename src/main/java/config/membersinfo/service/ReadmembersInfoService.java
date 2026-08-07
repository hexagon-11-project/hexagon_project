package config.membersinfo.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.membersinfo.dao.CompanyInfoDao;
import config.model.CompanyInfo;
import connection.ConnectionProvider;

public class ReadmembersInfoService {

	private CompanyInfoDao companyInfoDao = new CompanyInfoDao();

	public CompanyInfo getCompanyInfo(int companyId) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			CompanyInfo companyInfo = companyInfoDao.selectById(conn, companyId);
//			System.out.println("2");
			if (companyInfo == null) {
//				System.out.println("1");
				throw new CompanyNotFoundException();
			}
			return companyInfo;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
