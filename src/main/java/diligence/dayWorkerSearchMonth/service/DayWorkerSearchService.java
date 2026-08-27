package diligence.dayWorkerSearchMonth.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import config.model.DailyWorkRecord;
import connection.ConnectionProvider;
import diligence.dayWorkerSearchMonth.dao.DayWorkerSearchDao;
import jdbc.JdbcUtil;

// 일용직 월별 근무 조회 비즈니스 로직과 DB 커넥션을 관리하는 Service 클래스
// 日雇い月別勤務照会のビジネスロジックおよびDBコネクションを管理する Service クラス
public class DayWorkerSearchService {

	private DayWorkerSearchDao dayWorkerSearchDao = new DayWorkerSearchDao();

	// 지정된 연월 범위와 검색 조건(현장, 사원명)에 해당하는 일용직 근무 기록 목록을 DAO에서 긁어오는 메서드
	// 指定された年月範囲および検索条件（現場、社員名）に該当する日雇い勤務記録リストをDAOからかき集めるメソッド
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