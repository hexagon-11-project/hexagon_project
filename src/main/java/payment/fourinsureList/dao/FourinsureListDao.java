package payment.fourinsureList.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import payment.model.PaymentInsuranceLedger;

/**
 * 4대보험 대장 Dao.
 * 귀속연월·급여차수로 급여 헤더(정산기간, 급여지급일)와
 * 해당 지급 내역이 있는 사원의 4대보험 공제액을 조회한다.
 * 공제 행이 없으면 0원으로 둔다.
 */
public class FourinsureListDao {

	/**
	 * 귀속연월(YYYYMM)·급여차수로 4대보험 대장을 조회한다.
	 * 해당 급여차수가 없으면 null을 반환한다.
	 */
	public PaymentInsuranceLedger selectByYearMonthSeq(Connection conn, String payYearMonth, int paySequence)
			throws SQLException {
		PaymentInsuranceLedger ledger = selectPayrollHeader(conn, payYearMonth, paySequence);
		if (ledger == null) {
			return null;
		}
		ledger.setEmployees(selectEmployees(conn, payYearMonth, paySequence));
		return ledger;
	}

	private PaymentInsuranceLedger selectPayrollHeader(Connection conn, String payYearMonth, int paySequence)
			throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT PAYROLL_ID, COMPANY_ID, PAY_YEAR_MONTH, PAY_SEQUENCE, "
					+ "SETTLEMENT_START_DATE, SETTLEMENT_END_DATE, PAYMENT_DATE "
					+ "FROM PAYROLL "
					+ "WHERE PAY_YEAR_MONTH = ? AND PAY_SEQUENCE = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, payYearMonth);
			pstmt.setInt(2, paySequence);
			rs = pstmt.executeQuery();

			if (!rs.next()) {
				return null;
			}

			PaymentInsuranceLedger ledger = new PaymentInsuranceLedger();
			ledger.setPayrollId(rs.getInt("PAYROLL_ID"));
			ledger.setCompanyId(rs.getInt("COMPANY_ID"));
			ledger.setPayYearMonth(rs.getString("PAY_YEAR_MONTH"));
			ledger.setPaySequence(rs.getInt("PAY_SEQUENCE"));
			ledger.setSettlementStartDate(rs.getDate("SETTLEMENT_START_DATE"));
			ledger.setSettlementEndDate(rs.getDate("SETTLEMENT_END_DATE"));
			ledger.setPaymentDate(rs.getDate("PAYMENT_DATE"));
			return ledger;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	private List<PaymentInsuranceLedger> selectEmployees(Connection conn, String payYearMonth, int paySequence)
			throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT pe.PAYROLL_EMPLOYEE_ID, e.EMPLOYEE_ID, e.EMPLOYMENT_TYPE, e.EMPLOYEE_NAME, "
					+ "e.HIRE_DATE, e.DEPARTMENT, e.POSITION, "
					+ "NVL(SUM(CASE WHEN di.DEDUCTION_ITEM_NAME = ? THEN dd.AMOUNT END), 0) AS NATIONAL_PENSION, "
					+ "NVL(SUM(CASE WHEN di.DEDUCTION_ITEM_NAME = ? THEN dd.AMOUNT END), 0) AS HEALTH_INSURANCE, "
					+ "NVL(SUM(CASE WHEN di.DEDUCTION_ITEM_NAME = ? THEN dd.AMOUNT END), 0) AS LONG_TERM_CARE, "
					+ "NVL(SUM(CASE WHEN di.DEDUCTION_ITEM_NAME = ? THEN dd.AMOUNT END), 0) AS EMPLOYMENT_INSURANCE "
					+ "FROM PAYROLL p "
					+ "JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
					+ "JOIN EMPLOYEE e ON e.EMPLOYEE_ID = pe.EMPLOYEE_ID "
					+ "LEFT JOIN PAYROLL_DEDUCTION_DETAIL dd ON dd.PAYROLL_EMPLOYEE_ID = pe.PAYROLL_EMPLOYEE_ID "
					+ "LEFT JOIN DEDUCTION_ITEM di ON di.DEDUCTION_ITEM_ID = dd.DEDUCTION_ITEM_ID "
					+ "WHERE p.PAY_YEAR_MONTH = ? AND p.PAY_SEQUENCE = ? "
					+ "GROUP BY pe.PAYROLL_EMPLOYEE_ID, e.EMPLOYEE_ID, e.EMPLOYMENT_TYPE, e.EMPLOYEE_NAME, "
					+ "e.HIRE_DATE, e.DEPARTMENT, e.POSITION "
					+ "ORDER BY e.EMPLOYEE_NAME, e.EMPLOYEE_ID";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, PaymentInsuranceLedger.DEDUCTION_NATIONAL_PENSION);
			pstmt.setString(2, PaymentInsuranceLedger.DEDUCTION_HEALTH_INSURANCE);
			pstmt.setString(3, PaymentInsuranceLedger.DEDUCTION_LONG_TERM_CARE);
			pstmt.setString(4, PaymentInsuranceLedger.DEDUCTION_EMPLOYMENT_INSURANCE);
			pstmt.setString(5, payYearMonth);
			pstmt.setInt(6, paySequence);
			rs = pstmt.executeQuery();

			List<PaymentInsuranceLedger> employees = new ArrayList<>();
			while (rs.next()) {
				PaymentInsuranceLedger row = new PaymentInsuranceLedger();
				row.setPayrollEmployeeId(rs.getInt("PAYROLL_EMPLOYEE_ID"));
				row.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
				row.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
				row.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				row.setHireDate(rs.getDate("HIRE_DATE"));
				row.setDepartment(rs.getString("DEPARTMENT"));
				row.setPosition(rs.getString("POSITION"));
				row.setNationalPension(rs.getLong("NATIONAL_PENSION"));
				row.setHealthInsurance(rs.getLong("HEALTH_INSURANCE"));
				row.setLongTermCare(rs.getLong("LONG_TERM_CARE"));
				row.setEmploymentInsurance(rs.getLong("EMPLOYMENT_INSURANCE"));
				employees.add(row);
			}
			return employees;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}
