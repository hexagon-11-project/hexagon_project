package payment.paymenttransfer.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import payment.model.PaymentTransfer;

public class PaymenttransferDao {

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
	 * (체크된 행만 신청 대상으로 인정)
	 */
	public int countSelectedInPayroll(Connection conn, int payrollId, int[] payrollEmployeeIds) throws SQLException {
		if (payrollEmployeeIds == null || payrollEmployeeIds.length == 0) {
			return 0;
		}
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

	/** 이미 같은 PAYROLL_ID로 이체신청이 있는지 확인 (UK_PTR_1) */
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
	 * PAYROLL_TRANSFER_REQUEST에 급여작업(PAYROLL_ID) 단위로 1건 저장.
	 * ※ 사원(체크 행)마다 INSERT하지 않음.
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
	 * REQUEST_YN='Y', REQUEST_DATE=SYSDATE 로 갱신.
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
