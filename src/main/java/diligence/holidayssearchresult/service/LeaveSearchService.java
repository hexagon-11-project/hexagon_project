package diligence.holidayssearchresult.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.model.AttendanceRecord;
import config.model.EmployeeLeaveStatus;
import connection.ConnectionProvider;
import diligence.holidayssearchresult.dao.LeaveSearchDao;
import jdbc.JdbcUtil;

public class LeaveSearchService {

	private LeaveSearchDao leaveSearchDao = new LeaveSearchDao();

	public List<EmployeeLeaveStatus> getStatusByLeaveType(int companyId, int leaveTypeId, int year, String sortKey) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return leaveSearchDao.selectStatusByLeaveType(conn, companyId, leaveTypeId, year, sortKey);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<AttendanceRecord> getUsageDetail(int employeeId, int leaveTypeId, int year) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return leaveSearchDao.selectUsageDetail(conn, employeeId, leaveTypeId, year);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
