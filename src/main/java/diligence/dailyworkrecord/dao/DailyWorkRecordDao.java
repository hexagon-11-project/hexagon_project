package diligence.dailyworkrecord.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.model.DailyWorkRecord;
import config.model.EmployeeLeave;
import jdbc.JdbcUtil;

public class DailyWorkRecordDao {

	// 왼쪽 목록에 뿌릴 일용직 사원 목록만 (EMPLOYMENT_TYPE이 '일용직'/'DAILY' 두 가지로 섞여 있음)
	public List<EmployeeLeave> selectDailyWorkerEmployees(Connection conn, int companyId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement("SELECT EMPLOYEE_ID, EMPLOYMENT_TYPE, EMPLOYEE_NO, EMPLOYEE_NAME, "
					+ "DEPARTMENT, POSITION " + "FROM EMPLOYEE "
					+ "WHERE COMPANY_ID = ? AND EMPLOYMENT_TYPE IN ('일용직', 'DAILY') " + "ORDER BY EMPLOYEE_ID");
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<EmployeeLeave> result = new ArrayList<>();

			while (rs.next()) {
				// 사원 기본정보만 필요해서, 이미 만들어둔 EmployeeLeave 모델을 재사용
				EmployeeLeave row = new EmployeeLeave();
				row.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
				row.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
				row.setEmployeeNo(rs.getString("EMPLOYEE_NO"));
				row.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				row.setDepartment(rs.getString("DEPARTMENT"));
				row.setPosition(rs.getString("POSITION"));
				result.add(row);
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	public void insert(Connection conn, DailyWorkRecord item) throws SQLException {

		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement("INSERT INTO DAILY_WORK_RECORD ("
					+ "DAILY_WORK_RECORD_ID, EMPLOYEE_ID, WORK_SITE_NAME, WORK_DATE, "
					+ "DAILY_WAGE, PAY_RATE, PAY_AMOUNT, INCOME_TAX_AMOUNT, LOCAL_INCOME_TAX_AMOUNT, NET_PAY_AMOUNT, "
					+ "REG_ID, MOD_ID" + ") VALUES ("
					+ "(SELECT NVL(MAX(DAILY_WORK_RECORD_ID), 0) + 1 FROM DAILY_WORK_RECORD), "
					+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			pstmt.setInt(1, item.getEmployeeId());
			pstmt.setString(2, item.getWorkSiteName());
			pstmt.setDate(3, item.getWorkDate());
			pstmt.setBigDecimal(4, item.getDailyWage());
			pstmt.setBigDecimal(5, item.getPayRate());
			pstmt.setBigDecimal(6, item.getPayAmount());
			pstmt.setBigDecimal(7, item.getIncomeTaxAmount());
			pstmt.setBigDecimal(8, item.getLocalIncomeTaxAmount());
			pstmt.setBigDecimal(9, item.getNetPayAmount());
			pstmt.setString(10, "SYSTEM");
			pstmt.setString(11, "SYSTEM");

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	public void update(Connection conn, DailyWorkRecord item) throws SQLException {

		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement("UPDATE DAILY_WORK_RECORD SET "
					+ "WORK_SITE_NAME = ?, WORK_DATE = ?, DAILY_WAGE = ?, PAY_RATE = ?, PAY_AMOUNT = ?, "
					+ "INCOME_TAX_AMOUNT = ?, LOCAL_INCOME_TAX_AMOUNT = ?, NET_PAY_AMOUNT = ?, MOD_ID = ? "
					+ "WHERE DAILY_WORK_RECORD_ID = ?");

			pstmt.setString(1, item.getWorkSiteName());
			pstmt.setDate(2, item.getWorkDate());
			pstmt.setBigDecimal(3, item.getDailyWage());
			pstmt.setBigDecimal(4, item.getPayRate());
			pstmt.setBigDecimal(5, item.getPayAmount());
			pstmt.setBigDecimal(6, item.getIncomeTaxAmount());
			pstmt.setBigDecimal(7, item.getLocalIncomeTaxAmount());
			pstmt.setBigDecimal(8, item.getNetPayAmount());
			pstmt.setString(9, "SYSTEM");
			pstmt.setInt(10, item.getDailyWorkRecordId());

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	public void deleteById(Connection conn, int dailyWorkRecordId) throws SQLException {

		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement("DELETE FROM DAILY_WORK_RECORD WHERE DAILY_WORK_RECORD_ID = ?");
			pstmt.setInt(1, dailyWorkRecordId);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	public List<DailyWorkRecord> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement("SELECT DAILY_WORK_RECORD_ID, EMPLOYEE_ID, WORK_SITE_NAME, WORK_DATE, "
					+ "DAILY_WAGE, PAY_RATE, PAY_AMOUNT, INCOME_TAX_AMOUNT, LOCAL_INCOME_TAX_AMOUNT, NET_PAY_AMOUNT "
					+ "FROM DAILY_WORK_RECORD " + "WHERE EMPLOYEE_ID = ? "
					+ "ORDER BY WORK_DATE DESC, DAILY_WORK_RECORD_ID DESC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();

			List<DailyWorkRecord> result = new ArrayList<>();

			while (rs.next()) {
				result.add(mapRow(rs));
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	public DailyWorkRecord selectById(Connection conn, int dailyWorkRecordId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement("SELECT DAILY_WORK_RECORD_ID, EMPLOYEE_ID, WORK_SITE_NAME, WORK_DATE, "
					+ "DAILY_WAGE, PAY_RATE, PAY_AMOUNT, INCOME_TAX_AMOUNT, LOCAL_INCOME_TAX_AMOUNT, NET_PAY_AMOUNT "
					+ "FROM DAILY_WORK_RECORD " + "WHERE DAILY_WORK_RECORD_ID = ?");
			pstmt.setInt(1, dailyWorkRecordId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return mapRow(rs);
			}

			return null;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	private DailyWorkRecord mapRow(ResultSet rs) throws SQLException {

		DailyWorkRecord item = new DailyWorkRecord();

		item.setDailyWorkRecordId(rs.getInt("DAILY_WORK_RECORD_ID"));
		item.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
		item.setWorkSiteName(rs.getString("WORK_SITE_NAME"));
		item.setWorkDate(rs.getDate("WORK_DATE"));
		item.setDailyWage(rs.getBigDecimal("DAILY_WAGE"));
		item.setPayRate(rs.getBigDecimal("PAY_RATE"));
		item.setPayAmount(rs.getBigDecimal("PAY_AMOUNT"));
		item.setIncomeTaxAmount(rs.getBigDecimal("INCOME_TAX_AMOUNT"));
		item.setLocalIncomeTaxAmount(rs.getBigDecimal("LOCAL_INCOME_TAX_AMOUNT"));
		item.setNetPayAmount(rs.getBigDecimal("NET_PAY_AMOUNT"));

		return item;
	}
}
