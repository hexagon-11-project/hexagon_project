package diligence.attendancemanage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.model.AttendanceRecord;
import connection.ConnectionProvider;
import diligence.attendancemanage.dao.AttendanceRecordDao;
import jdbc.JdbcUtil;

public class AttendanceRecordManageService {

	private AttendanceRecordDao attendanceRecordDao = new AttendanceRecordDao();

	public List<AttendanceRecord> getListByEmployeeId(int employeeId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return attendanceRecordDao.selectByEmployeeId(conn, employeeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public void insert(AttendanceRecord item) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			attendanceRecordDao.insert(conn, item);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public AttendanceRecord getById(int attendanceId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return attendanceRecordDao.selectById(conn, attendanceId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public void update(AttendanceRecord item) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			attendanceRecordDao.update(conn, item);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public void delete(int attendanceId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			attendanceRecordDao.delete(conn, attendanceId);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
