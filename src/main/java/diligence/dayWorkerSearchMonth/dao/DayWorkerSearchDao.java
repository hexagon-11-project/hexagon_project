package diligence.dayWorkerSearchMonth.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.model.DailyWorkRecord;
import jdbc.JdbcUtil;

public class DayWorkerSearchDao {

	public List<DailyWorkRecord> selectByMonth(Connection conn, int companyId, Date monthStart, Date monthEnd,
			String workSiteName, String employeeNameKeyword) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT e.EMPLOYEE_NAME, dwr.WORK_DATE, dwr.WORK_SITE_NAME, dwr.DAILY_WAGE, dwr.PAY_RATE, ");
			sql.append("dwr.PAY_AMOUNT, dwr.INCOME_TAX_AMOUNT, dwr.LOCAL_INCOME_TAX_AMOUNT, dwr.NET_PAY_AMOUNT ");
			sql.append("FROM DAILY_WORK_RECORD dwr ");
			sql.append("JOIN EMPLOYEE e ON e.EMPLOYEE_ID = dwr.EMPLOYEE_ID ");
			sql.append("WHERE e.COMPANY_ID = ? AND dwr.WORK_DATE BETWEEN ? AND ? ");
			if (workSiteName != null && !workSiteName.isBlank()) {
				sql.append("AND dwr.WORK_SITE_NAME = ? ");
			}
			if (employeeNameKeyword != null && !employeeNameKeyword.isBlank()) {
				sql.append("AND e.EMPLOYEE_NAME LIKE ('%' || ? || '%') ");
			}
			sql.append("ORDER BY e.EMPLOYEE_NAME, dwr.WORK_DATE");

			pstmt = conn.prepareStatement(sql.toString());

			int idx = 1;
			pstmt.setInt(idx++, companyId);
			pstmt.setDate(idx++, monthStart);
			pstmt.setDate(idx++, monthEnd);
			if (workSiteName != null && !workSiteName.isBlank()) {
				pstmt.setString(idx++, workSiteName);
			}
			if (employeeNameKeyword != null && !employeeNameKeyword.isBlank()) {
				pstmt.setString(idx++, employeeNameKeyword);
			}

			rs = pstmt.executeQuery();

			List<DailyWorkRecord> result = new ArrayList<>();

			while (rs.next()) {

				DailyWorkRecord item = new DailyWorkRecord();

				item.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				item.setWorkDate(rs.getDate("WORK_DATE"));
				item.setWorkSiteName(rs.getString("WORK_SITE_NAME"));
				item.setDailyWage(rs.getBigDecimal("DAILY_WAGE"));
				item.setPayRate(rs.getBigDecimal("PAY_RATE"));
				item.setPayAmount(rs.getBigDecimal("PAY_AMOUNT"));
				item.setIncomeTaxAmount(rs.getBigDecimal("INCOME_TAX_AMOUNT"));
				item.setLocalIncomeTaxAmount(rs.getBigDecimal("LOCAL_INCOME_TAX_AMOUNT"));
				item.setNetPayAmount(rs.getBigDecimal("NET_PAY_AMOUNT"));

				result.add(item);
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}
