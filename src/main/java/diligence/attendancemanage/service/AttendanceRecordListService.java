package diligence.attendancemanage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import diligence.attendancemanage.dao.AttendanceRecordDao;
import config.model.EmployeeLeave;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class AttendanceRecordListService {

	private AttendanceRecordDao attendanceRecordDao = new AttendanceRecordDao();

	public List<EmployeeLeave> getEmployeeList(int companyId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return attendanceRecordDao.selectEmployeesByCompanyId(conn, companyId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
