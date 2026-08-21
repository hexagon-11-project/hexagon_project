package diligence.dayWorkerSearchMonth.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import config.model.DailyWorkRecord;
import connection.ConnectionProvider;
import diligence.dayWorkerSearchMonth.dao.DayWorkerSearchDao;
import jdbc.JdbcUtil;

public class DayWorkerSearchService {

	private DayWorkerSearchDao dayWorkerSearchDao = new DayWorkerSearchDao();

	public List<DailyWorkRecord> getListByMonth(int companyId, Date monthStart, Date monthEnd, String workSiteName,
			String employeeNameKeyword) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return dayWorkerSearchDao.selectByMonth(conn, companyId, monthStart, monthEnd, workSiteName,
					employeeNameKeyword);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
