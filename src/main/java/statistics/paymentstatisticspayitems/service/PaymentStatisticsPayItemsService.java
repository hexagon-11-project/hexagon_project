package statistics.paymentstatisticspayitems.service;

import java.sql.Connection;
import java.sql.SQLException;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import statistics.model.EmployeeSalaryStatistics;
import statistics.paymentstatisticspayitems.dao.PaymentStatisticsPayItemsDao;

/**
 * 사원별 급여 항목 통계 Service.
 * 연도, 월, 사원이름으로 해당 월 지급내역·공제항목을 조회한다.
 */
public class PaymentStatisticsPayItemsService {

	private PaymentStatisticsPayItemsDao paymentStatisticsPayItemsDao = new PaymentStatisticsPayItemsDao();

	/**
	 * 연도, 월, 사원이름으로 해당 사원의 월 급여 항목 통계를 조회한다.
	 * 해당 월 급여 데이터가 없으면 null을 반환한다.
	 */
	public EmployeeSalaryStatistics getEmployeeSalaryStatistics(int companyId, int year, int month,
			String employeeName) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return paymentStatisticsPayItemsDao.selectByYearMonthAndName(conn, companyId, year, month, employeeName);
		} catch (SQLException e) {
			throw new RuntimeException("사원별 급여 항목 통계 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
