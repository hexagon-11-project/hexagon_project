package person.certificateRegister.dao; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import person.model.CertificatePrintWorkingModel;

public class CertificateRegisterDao {

	public List<CertificatePrintWorkingModel> getAllCertificateList(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<CertificatePrintWorkingModel> list = new ArrayList<>();

		try {
			String sql = "SELECT certificate_issue.issue_no, "
					   + "       TO_CHAR(certificate_issue.issue_date, 'yyyy-mm-dd') AS issue_date, "
					   + "       employee.employee_name, "
					   + "       certificate_issue.certificate_type_code, "
					   + "       certificate_issue.purpose, "
					   + "       certificate_issue.reg_id, "
					   + "       certificate_issue.certificate_yn "
					   + "FROM certificate_issue "
					   + "JOIN employee ON certificate_issue.employee_id = employee.employee_id "
					   + "ORDER BY certificate_issue.issue_date DESC, certificate_issue.issue_no DESC";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				CertificatePrintWorkingModel model = new CertificatePrintWorkingModel();

				model.setIssueNo(rs.getString("issue_no"));
				model.setCertificateTypeCode(rs.getString("certificate_type_code"));
				model.setPurpose(rs.getString("purpose"));
				model.setRegId(rs.getString("reg_id"));
				model.setCertificateYn(rs.getString("certificate_yn"));

				// JSP 출력을 위해 필요한 변수들 
				 model.setIssueDate(rs.getString("issue_date"));
				 model.setEmployeeName(rs.getString("employee_name"));

				list.add(model);
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
	
	public int updateCertificateStatusToN(Connection conn, String[] issueNos) throws SQLException {
        PreparedStatement pstmt = null;
        int resultCount = 0;
        
        try {
            // 상태를 'N'으로 변경하는 쿼리
            String sql = "UPDATE certificate_issue SET certificate_yn = 'N' WHERE issue_no = ?";
            pstmt = conn.prepareStatement(sql);
            
            // 배열로 넘어온 발급번호를 하나씩 꺼내서 즉시 업데이트 실행
            for (String issueNo : issueNos) {
                pstmt.setString(1, issueNo);
                
                // 오라클 버그를 피하기 위해 즉시 실행(executeUpdate) 사용
                int result = pstmt.executeUpdate();
                
                // 정상적으로 1건이 업데이트 되었다면 카운트 증가
                if (result > 0) {
                    resultCount++;
                }
            }
            
            return resultCount;
        } finally {
            JdbcUtil.close(pstmt);
        }
    }
}