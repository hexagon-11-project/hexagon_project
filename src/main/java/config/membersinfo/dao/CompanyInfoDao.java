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
			
			// 별칭 없이 테이블명 직접 명시
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
						rs.getString("tel_no"), rs.getString("fax_no"),
						rs.getString("business_type"), rs.getString("business_item"), rs.getInt("pay_day"),
						rs.getInt("pay_period_start_day"), rs.getInt("pay_period_end_day"), rs.getString("bank_name"),
						rs.getString("account_holder"), rs.getString("bank_account"), rs.getString("logo_path"),
						rs.getString("seal_path"), rs.getString("created_at"), rs.getString("updated_at"),
						rs.getString("employee_name"),rs.getString("phone"),rs.getString("mobile"),rs.getString("email"));

				
				
				 			}
			return info;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 2. 수정 메서드 (companyinfo 업데이트 + employee 업데이트 모두 포함)
	public int update(Connection conn, CompanyInfo info) throws SQLException {

		// 1번 쿼리: 회사 기본 정보 업데이트 (ceo_title 제거 완료)
		String sql1 = "UPDATE company_info SET " + 
		        "company_name=?, business_no=?, ceo_name=?, corp_no=?, "
		        + "est_date=?, web_site=?,  tel_no=?, fax_no=?, "
		        + "business_type=?, business_item=?, pay_day=?, pay_period_start_day=?, pay_period_end_day=?, "
		        + "bank_name=?, account_holder=?, bank_account=?, logo_path=?, seal_path=?, updated_at=sysdate "
		        + "WHERE company_id=?";

		// 2번 쿼리: 담당자(employee) 정보 업데이트
		String sql2 = "UPDATE employee SET emp_name=?, tel_no=?, mobile_no=?, email=? "
		        + "WHERE emp_id = (SELECT manager_id FROM company_info WHERE company_id = ?)";

		// try-with-resources로 PreparedStatement 2개를 동시에 열고 닫음
		try (PreparedStatement pstmt1 = conn.prepareStatement(sql1);
		        PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {

			pstmt1.setString(1, info.getCompanyName());
		    pstmt1.setString(2, info.getBusinessNo());
		    // pstmt1.setString(3, info.getCeoTitle()); <-- 이 부분이 삭제되었습니다.
		    pstmt1.setString(3, info.getCeoName());        // 번호가 4에서 3으로 당겨짐
		    pstmt1.setString(4, info.getCorpNo());         // 번호가 5에서 4로 당겨짐
		    // pstmt1.setDate(5, toDate(info.getEstDate()));
		    pstmt1.setDate(5, (java.sql.Date) info.getEstDate());
		    pstmt1.setString(6, info.getWebSite());
		    // pstmt1.setString(7, info.getAddress()); <-- 이 부분이 삭제되었습니다.
		    pstmt1.setString(7, info.getTelNo());          // 번호가 8에서 7로 당겨짐
		    pstmt1.setString(8, info.getFaxNo());          // 번호가 9에서 8로 당겨짐
		    pstmt1.setString(9, info.getBusinessType());   // 번호가 10에서 9로 당겨짐
		    pstmt1.setString(10, info.getBusinessItem());  // 번호가 11에서 10으로 당겨짐
		    pstmt1.setInt(11, info.getPayDay());           // 번호가 12에서 11로 당겨짐
		    pstmt1.setInt(12, info.getPayPeriodStartDay());// 번호가 13에서 12로 당겨짐
		    pstmt1.setInt(13, info.getPayPeriodEndDay());  // 번호가 14에서 13으로 당겨짐
		    pstmt1.setString(14, info.getBankName());      // 번호가 15에서 14로 당겨짐
		    pstmt1.setString(15, info.getAccountHolder()); // 번호가 16에서 15로 당겨짐
		    pstmt1.setString(16, info.getBankAccount());   // 번호가 17에서 16으로 당겨짐
		    pstmt1.setString(17, info.getLogoPath());      // 번호가 18에서 17로 당겨짐
		    pstmt1.setString(18, info.getSealPath());      // 번호가 19에서 18로 당겨짐
		    pstmt1.setInt(19, info.getCompanyId());        // 번호가 20에서 19로 당겨짐
		    int result1 = pstmt1.executeUpdate();

		    return result1;
		
		}
	}
	
	/*
	 * private Date toDate(String date) { return Date.valueOf(LocalDate.parse(date,
	 * DateTimeFormatter.ofPattern("yyyyMMdd"))); }
	 */
	private Date toDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return null; // 값이 없으면 그냥 null로 저장 (예외 던지지 않음)
        }
        String trimmed = date.trim();
        try {
            if (trimmed.contains("-")) {
                // <input type="date"> 등 브라우저 달력 위젯이 보내는 형식: yyyy-MM-dd
                return Date.valueOf(LocalDate.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE));
            } else {
                // 순수 텍스트로 8자리 입력받는 형식: yyyyMMdd
                return Date.valueOf(LocalDate.parse(trimmed, DateTimeFormatter.ofPattern("yyyyMMdd")));
            }
        } catch (Exception e) {
            // 형식이 안 맞는 값이 들어와도 저장 자체가 실패하지 않도록 null 처리
            return null;
        }
    }
}
