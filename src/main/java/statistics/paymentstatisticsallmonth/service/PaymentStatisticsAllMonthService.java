package statistics.paymentstatisticsallmonth.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import statistics.model.MonthlyTotalStatistics;
import statistics.paymentstatisticsallmonth.dao.PaymentStatisticsAllMonthDao;

/**
 * 월별 전체급여 통계 Service.
 * 선택 연도의 1월~12월 집계를 조회한다.
 */
public class PaymentStatisticsAllMonthService {

	private PaymentStatisticsAllMonthDao paymentStatisticsAllMonthDao = new PaymentStatisticsAllMonthDao();

	/**
	 * 선택 연도의 1월~12월 통계 목록 조회.
	 */
	public List<MonthlyTotalStatistics> getMonthlyTotalList(int companyId, int year) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return paymentStatisticsAllMonthDao.selectMonthlyTotalByYear(conn, companyId, year);
		} catch (SQLException e) {
			throw new RuntimeException("월별 전체급여 통계 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
