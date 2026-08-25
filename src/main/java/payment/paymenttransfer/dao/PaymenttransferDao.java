package payment.paymenttransfer.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import payment.model.PaymentTransfer;

/**
 * 급여이체 신청 Dao.
 * 給与振込申請Dao。
 *
 */
public class PaymenttransferDao {

	/**
	 * 귀속연월/차수로 이체 대상 사원 목록을 조회한다.
	 * 帰属年月/次数で振込対象の社員一覧を照会する。
	 *
	 */
	public List<PaymentTransfer> selectByYearMonthSeq(Connection conn, String payYearMonth, int paySequence)
			throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT p.PAYROLL_ID, pe.PAYROLL_EMPLOYEE_ID, e.EMPLOYEE_ID, "
					+ "e.EMPLOYEE_NAME, e.DEPARTMENT, e.POSITION, "
					+ "e.BANK_NAME, e.BANK_ACCOUNT, pe.NET_PAY_AMOUNT "
					+ "FROM PAYROLL p "
					+ "JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
					+ "JOIN EMPLOYEE e ON e.EMPLOYEE_ID = pe.EMPLOYEE_ID "
					+ "WHERE p.PAY_YEAR_MONTH = ? AND p.PAY_SEQUENCE = ? "
					+ "ORDER BY e.EMPLOYEE_NAME";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, payYearMonth);
			pstmt.setInt(2, paySequence);
			rs = pstmt.executeQuery();

			List<PaymentTransfer> list = new ArrayList<>();
			while (rs.next()) {
				PaymentTransfer row = new PaymentTransfer();
				row.setPayrollId(rs.getInt("PAYROLL_ID"));
				row.setPayrollEmployeeId(rs.getInt("PAYROLL_EMPLOYEE_ID"));
				row.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
				row.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				row.setDepartment(rs.getString("DEPARTMENT"));
				row.setPosition(rs.getString("POSITION"));
				row.setBankName(rs.getString("BANK_NAME"));
				row.setBankAccount(rs.getString("BANK_ACCOUNT"));
				row.setNetPayAmount(rs.getLong("NET_PAY_AMOUNT"));
				list.add(row);
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * 귀속연월/차수로 급여작업 PK를 조회한다.
	 * 帰属年月/次数で給与作業PKを照会する。
	 *
	 */
	public Integer selectPayrollId(Connection conn, String payYearMonth, int paySequence) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT PAYROLL_ID FROM PAYROLL WHERE PAY_YEAR_MONTH = ? AND PAY_SEQUENCE = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, payYearMonth);
			pstmt.setInt(2, paySequence);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("PAYROLL_ID");
			}
			return null;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * 화면에서 체크된 payrollEmployeeId들이
	 * 해당 급여작업(PAYROLL_ID)에 실제로 속하는지 검증하고 건수를 반환한다.
	 * 画面でチェックされたpayrollEmployeeIdが
	 * 当該給与作業(PAYROLL_ID)に実際に属するかを検証し、件数を返す。
	 *
	 */
	public int countSelectedInPayroll(Connection conn, int payrollId, int[] payrollEmployeeIds) throws SQLException {
		if (payrollEmployeeIds == null || payrollEmployeeIds.length == 0) {
			return 0;
		}
		// IN (?, ?, ...) 를 선택 건수만큼 동적으로 붙인다
		// IN (?, ?, ...) を選択件数だけ動的に付ける。
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM PAYROLL_EMPLOYEE ");
		sql.append("WHERE PAYROLL_ID = ? AND PAYROLL_EMPLOYEE_ID IN (");
		for (int i = 0; i < payrollEmployeeIds.length; i++) {
			if (i > 0) {
				sql.append(",");
			}
			sql.append("?");
		}
		sql.append(")");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql.toString());
			// 1번은 급여작업 PK, 2번부터가 체크된 PAYROLL_EMPLOYEE_ID 이다
			// 1番は給与作業PK、2番からがチェックされたPAYROLL_EMPLOYEE_IDである。
			pstmt.setInt(1, payrollId);
			for (int i = 0; i < payrollEmployeeIds.length; i++) {
				pstmt.setInt(i + 2, payrollEmployeeIds[i]);
			}
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * 이미 같은 PAYROLL_ID로 이체신청이 있는지 확인 (UK_PTR_1)
	 * 既に同じPAYROLL_IDで振込申請があるかを確認する (UK_PTR_1)。
	 *
	 */
	public boolean existsTransferRequest(Connection conn, int payrollId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT COUNT(*) FROM PAYROLL_TRANSFER_REQUEST WHERE PAYROLL_ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, payrollId);
			rs = pstmt.executeQuery();
			return rs.next() && rs.getInt(1) > 0;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * [INSERT] 체크된 행이 1건 이상일 때 호출.
	 * [INSERT] チェックされた行が1件以上あるときに呼び出す。
	 *
	 */
	public int insertTransferRequest(Connection conn, int payrollId) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			String sql = "INSERT INTO PAYROLL_TRANSFER_REQUEST ("
					+ "TRANSFER_REQUEST_ID, PAYROLL_ID, REQUEST_YN, REQUEST_DATE, "
					+ "REG_ID, MOD_ID, CREATED_AT, UPDATED_AT"
					+ ") VALUES ("
					+ "PAYROLL_TRANSFER_REQ_SEQ.NEXTVAL, ?, 'Y', SYSDATE, "
					+ "'SYSTEM', 'SYSTEM', SYSDATE, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, payrollId);
			return pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * [UPDATE] 같은 PAYROLL_ID 신청이 이미 있으면 재신청 처리.
	 * [UPDATE] 同じPAYROLL_IDの申請が既にあれば再申請処理をする。
	 *
	 */
	public int updateTransferRequest(Connection conn, int payrollId) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			String sql = "UPDATE PAYROLL_TRANSFER_REQUEST SET "
					+ "REQUEST_YN = 'Y', REQUEST_DATE = SYSDATE, "
					+ "MOD_ID = 'SYSTEM', UPDATED_AT = SYSDATE "
					+ "WHERE PAYROLL_ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, payrollId);
			return pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}
