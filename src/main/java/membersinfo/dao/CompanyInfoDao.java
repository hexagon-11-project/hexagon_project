package membersinfo.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jdbc.JdbcUtil;
import membersinfo.model.CompanyInfo;

public class CompanyInfoDao {
	public CompanyInfo selectById(Connection conn, int companyId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			System.out.println("--- DB 연결 정보 확인 ---");
			System.out.println("실제 접속한 DB URL: " + conn.getMetaData().getURL());
			System.out.println("실제 접속한 DB User: " + conn.getMetaData().getUserName());
			System.out.println("조회하려는 companyId: " + companyId);
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
				System.out.println("데이터 조회 성공! 회사명: " + rs.getString("company_name"));
				info = new CompanyInfo(rs.getInt("company_id"), rs.getString("company_name"),
						rs.getString("business_no"), rs.getString("ceo_title"), rs.getString("ceo_name"),
						rs.getString("corp_no"), rs.getString("est_date"), rs.getString("web_site"),
						rs.getString("address"), rs.getString("tel_no"), rs.getString("fax_no"),
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
		        + "est_date=?, web_site=?, address=?, tel_no=?, fax_no=?, "
		        + "business_type=?, business_item=?, pay_day=?, pay_period_start_day=?, pay_period_end_day=?, "
		        + "bank_name=?, account_holder=?, bank_account=?, logo_path=?, seal_path=?, updated_at=sysdate "
		        + "WHERE company_id=?";

		// 2번 쿼리: 담당자(employee) 정보 업데이트
		String sql2 = "UPDATE employee SET emp_name=?, tel_no=?, mobile_no=?, email=? "
		        + "WHERE emp_id = (SELECT manager_id FROM company_info WHERE company_id = ?)";

		// try-with-resources로 PreparedStatement 2개를 동시에 열고 닫음
		try (PreparedStatement pstmt1 = conn.prepareStatement(sql1);
		        PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {

		    // --- 첫 번째 테이블 (company_info) 값 세팅 ---
		    pstmt1.setString(1, info.getCompanyName());
		    pstmt1.setString(2, info.getBusinessNo());
		    // pstmt1.setString(3, info.getCeoTitle()); <-- 이 부분이 삭제되었습니다.
		    pstmt1.setString(3, info.getCeoName());        // 번호가 4에서 3으로 당겨짐
		    pstmt1.setString(4, info.getCorpNo());         // 번호가 5에서 4로 당겨짐
		    pstmt1.setDate(5, toDate(info.getEstDate()));
		    pstmt1.setString(6, info.getWebSite());
		    pstmt1.setString(7, info.getAddress());
		    pstmt1.setString(8, info.getTelNo());
		    pstmt1.setString(9, info.getFaxNo());
		    pstmt1.setString(10, info.getBusinessType());
		    pstmt1.setString(11, info.getBusinessItem());
		    pstmt1.setInt(12, info.getPayDay());
		    pstmt1.setInt(13, info.getPayPeriodStartDay());
		    pstmt1.setInt(14, info.getPayPeriodEndDay());
		    pstmt1.setString(15, info.getBankName());
		    pstmt1.setString(16, info.getAccountHolder());
		    pstmt1.setString(17, info.getBankAccount());
		    pstmt1.setString(18, info.getLogoPath());
		    pstmt1.setString(19, info.getSealPath());
		    pstmt1.setInt(20, info.getCompanyId());      // 마지막 company_id 번호도 21에서 20으로 당겨짐

		    int result1 = pstmt1.executeUpdate();

		    return result1;
		
		}
	}
	
	private Date toDate(String date) {
        return Date.valueOf(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")));
    }
}
