package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.dnLItemSet.dao.AttendanceTypeDao;
import config.model.AttendanceType;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class AttendanceTypeSelectService {

	private AttendanceTypeDao attendanceTypeDao = new AttendanceTypeDao();

	public AttendanceType getById(int attendanceTypeId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return attendanceTypeDao.selectById(conn, attendanceTypeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
