package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.dnLItemSet.dao.AttendanceTypeDao;
import config.model.AttendanceType;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class AttendanceTypeListService {

	private AttendanceTypeDao attendanceTypeDao = new AttendanceTypeDao();

	public List<AttendanceType> getList(int companyId) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			return attendanceTypeDao.selectByCompanyId(conn, companyId);

		} catch (SQLException e) {

			throw new RuntimeException(e);

		} finally {

			JdbcUtil.close(conn);

		}

	}

}
