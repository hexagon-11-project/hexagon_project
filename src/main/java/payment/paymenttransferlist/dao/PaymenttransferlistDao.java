package payment.paymenttransferlist.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import payment.model.PaymentTransferRequest;

/**
 * 급여이체 신청 조회 Dao.
 * 給与振込申請照会Dao。
 *
 */
public class PaymenttransferlistDao {

	/**
	 * 신청기간(REQUEST_DATE) 안의 이체신청 결과 조회.
	 * 申請期間(REQUEST_DATE)内の振込申請結果を照会する。
	 *
	 */
	public List<PaymentTransferRequest> selectListByRequestPeriod(Connection conn, Date startDate, Date endDate)
			throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// UNIQUE 신청 1건 → PAYROLL → PAYROLL_EMPLOYEE → EMPLOYEE 로 사원 행을 펼친다
			// UNIQUE申請1件 → PAYROLL → PAYROLL_EMPLOYEE → EMPLOYEE で社員行を展開する。
			String sql = "SELECT e.BANK_NAME, e.BANK_ACCOUNT, e.EMPLOYEE_NAME, pe.NET_PAY_AMOUNT, "
					+ "c.BANK_NAME AS COMPANY_BANK_NAME, c.ACCOUNT_HOLDER AS COMPANY_ACCOUNT_HOLDER, "
					+ "c.BANK_ACCOUNT AS COMPANY_BANK_ACCOUNT "
					+ "FROM PAYROLL_TRANSFER_REQUEST ptr "
					+ "JOIN PAYROLL p ON p.PAYROLL_ID = ptr.PAYROLL_ID "
					+ "JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
					+ "JOIN EMPLOYEE e ON e.EMPLOYEE_ID = pe.EMPLOYEE_ID "
					+ "JOIN COMPANY_INFO c ON c.COMPANY_ID = p.COMPANY_ID "
					+ "WHERE ptr.REQUEST_YN = 'Y' "
					+ "AND TRUNC(ptr.REQUEST_DATE) BETWEEN ? AND ? "
					+ "ORDER BY ptr.REQUEST_DATE DESC, e.EMPLOYEE_NAME";

			pstmt = conn.prepareStatement(sql);
			pstmt.setDate(1, startDate);
			pstmt.setDate(2, endDate);
			rs = pstmt.executeQuery();
			return mapList(rs);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * ResultSet을 신청 결과 행 목록으로 변환한다.
	 * ResultSetを申請結果行の一覧に変換する。
	 *
	 */
	private List<PaymentTransferRequest> mapList(ResultSet rs) throws SQLException {
		List<PaymentTransferRequest> list = new ArrayList<>();
		while (rs.next()) {
			PaymentTransferRequest row = new PaymentTransferRequest();
			row.setBankName(rs.getString("BANK_NAME"));
			row.setBankAccount(rs.getString("BANK_ACCOUNT"));
			row.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
			row.setTransferAmount(rs.getLong("NET_PAY_AMOUNT"));
			row.setCompanyBankName(rs.getString("COMPANY_BANK_NAME"));
			row.setCompanyAccountHolder(rs.getString("COMPANY_ACCOUNT_HOLDER"));
			row.setCompanyBankAccount(rs.getString("COMPANY_BANK_ACCOUNT"));
			list.add(row);
		}
		return list;
	}
}
