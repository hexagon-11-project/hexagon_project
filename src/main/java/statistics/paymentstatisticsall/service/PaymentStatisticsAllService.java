package statistics.paymentstatisticsall.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import statistics.model.AnnualTotalStatistics;
import statistics.paymentstatisticsall.dao.PaymentStatisticsAllDao;

/**
 * 연도별 전체급여 통계 Service.
 * 선택 연도 기준 과거 10년 집계를 조회한다.
 */
public class PaymentStatisticsAllService {

	private PaymentStatisticsAllDao paymentStatisticsAllDao = new PaymentStatisticsAllDao();

	/**
	 * 선택 연도(endYear) 기준 과거 10년 통계 목록 조회.
	 * 예: endYear=2026 → 2017~2026
	 */
	public List<AnnualTotalStatistics> getAnnualTotalList(int companyId, int endYear) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return paymentStatisticsAllDao.selectAnnualTotalByEndYear(conn, companyId, endYear);
		} catch (SQLException e) {
			throw new RuntimeException("연도별 전체급여 통계 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
