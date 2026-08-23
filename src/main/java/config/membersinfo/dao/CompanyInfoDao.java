package config.membersinfo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import config.model.CompanyInfo;

import java.sql.Date;

import jdbc.JdbcUtil;

public class CompanyInfoDao {
	public CompanyInfo selectById(Connection conn, int companyId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// 회사 정보 조회
			String sql = "SELECT company_info.*, " +

					"employee.employee_name, employee.phone, employee.mobile, employee.email " +

					"FROM company_info " +

					"LEFT JOIN employee ON company_info.COMPANY_ID = employee.COMPANY_ID " +

					"WHERE company_info.company_id = 1001 AND employee.mng_yn = 'Y'";

			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			CompanyInfo info = null;
			if (rs.next()) {

				info = new CompanyInfo(rs.getInt("company_id"), rs.getString("company_name"),
						rs.getString("business_no"), rs.getString("ceo_title"), rs.getString("ceo_name"),
						rs.getString("corp_no"), rs.getDate("est_date"), rs.getString("web_site"),
						rs.getString("tel_no"), rs.getString("fax_no"), rs.getString("business_type"),
						rs.getString("business_item"), rs.getInt("pay_day"), rs.getInt("pay_period_start_day"),
						rs.getInt("pay_period_end_day"), rs.getString("bank_name"), rs.getString("account_holder"),
						rs.getString("bank_account"), rs.getString("logo_path"), rs.getString("seal_path"),
						rs.getString("created_at"), rs.getString("updated_at"), rs.getString("employee_name"),
						rs.getString("phone"), rs.getString("mobile"), rs.getString("email"));

			}
			return info;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 회사 정보와 회사의 메인 담당자 정보 동시 업데이트
	public int update(Connection conn, CompanyInfo info) throws SQLException {

		String sql1 = "UPDATE company_info SET " + "company_name=?, business_no=?, ceo_name=?, corp_no=?, "
				+ "est_date=?, web_site=?,  tel_no=?, fax_no=?, "
				+ "business_type=?, business_item=?, pay_day=?, pay_period_start_day=?, pay_period_end_day=?, "
				+ "bank_name=?, account_holder=?, bank_account=?, logo_path=?, seal_path=?, updated_at=sysdate "
				+ "WHERE company_id=?";

		String sql2 = "UPDATE employee SET emp_name=?, tel_no=?, mobile_no=?, email=? "
				+ "WHERE emp_id = (SELECT manager_id FROM company_info WHERE company_id = ?)";

		try (PreparedStatement pstmt1 = conn.prepareStatement(sql1);
				PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {

			pstmt1.setString(1, info.getCompanyName());
			pstmt1.setString(2, info.getBusinessNo());
			pstmt1.setString(3, info.getCeoName()); 
			pstmt1.setString(4, info.getCorpNo()); 
			pstmt1.setDate(5, (java.sql.Date) info.getEstDate());
			pstmt1.setString(6, info.getWebSite());
			pstmt1.setString(7, info.getTelNo()); 
			pstmt1.setString(8, info.getFaxNo()); 
			pstmt1.setString(9, info.getBusinessType()); 
			pstmt1.setString(10, info.getBusinessItem()); 
			pstmt1.setInt(11, info.getPayDay()); 
			pstmt1.setInt(12, info.getPayPeriodStartDay());
			pstmt1.setInt(13, info.getPayPeriodEndDay()); 
			pstmt1.setString(14, info.getBankName()); 
			pstmt1.setString(15, info.getAccountHolder()); 
			pstmt1.setString(16, info.getBankAccount()); 
			pstmt1.setString(17, info.getLogoPath()); 
			pstmt1.setString(18, info.getSealPath()); 
			pstmt1.setInt(19, info.getCompanyId()); 
			int result1 = pstmt1.executeUpdate();

			return result1;

		}
	}
//날짜 문자열을 데이터베이스 저장용 날짜 객체로 변환
	private Date toDate(String date) {
		if (date == null || date.trim().isEmpty()) {
			return null; 
		}
		String trimmed = date.trim();
		try {
			if (trimmed.contains("-")) {
				// 브라우저 달력 위젯이 보내는 형식: yyyy-MM-dd
				return Date.valueOf(LocalDate.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE));
			} else {
				// 순수 텍스트로 8자리 입력받는 형식: yyyyMMdd
				return Date.valueOf(LocalDate.parse(trimmed, DateTimeFormatter.ofPattern("yyyyMMdd")));
			}
		} catch (Exception e) {
			return null;
		}
	}
}
