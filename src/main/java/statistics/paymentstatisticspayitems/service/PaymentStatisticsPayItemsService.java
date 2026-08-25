package statistics.paymentstatisticspayitems.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.employee.model.Employee;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import statistics.model.EmployeeSalaryStatistics;
import statistics.model.SalaryItemStatistics;
import statistics.paymentstatisticspayitems.dao.PaymentStatisticsPayItemsDao;

/**
 * 사원별 급여 항목 통계 Service.
 * 社員別給与項目統計Service。
 *
 */
public class PaymentStatisticsPayItemsService {

	private PaymentStatisticsPayItemsDao paymentStatisticsPayItemsDao = new PaymentStatisticsPayItemsDao();

	/**
	 * 사원 선택 팝업용 목록 조회.
	 * 社員選択ポップアップ用一覧照会。
	 *
	 */
	public List<Employee> getEmployeeList(int companyId, String employeeName, String department, String status) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return paymentStatisticsPayItemsDao.selectEmployeeList(conn, companyId, employeeName, department, status);
		} catch (SQLException e) {
			throw new RuntimeException("사원 목록 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/**
	 * 사원 선택 팝업의 부서 필터 목록.
	 * 社員選択ポップアップの部署フィルタ一覧。
	 *
	 */
	public List<String> getDepartmentList() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return paymentStatisticsPayItemsDao.selectDepartmentList(conn);
		} catch (SQLException e) {
			throw new RuntimeException("부서 목록 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/**
	 * 사원 선택 팝업의 상태 필터 목록.
	 * 社員選択ポップアップの状態フィルタ一覧。
	 *
	 */
	public List<String> getStatusList() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return paymentStatisticsPayItemsDao.selectStatusList(conn);
		} catch (SQLException e) {
			throw new RuntimeException("상태 목록 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/**
	 * 표 헤더용 지급항목 목록. 조회 전에는 금액 0으로 둔다.
	 * 表ヘッダ用支給項目一覧。照会前は金額を0にする。
	 *
	 */
	public List<SalaryItemStatistics> getPayItemColumns(int companyId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			// 마스터 열 0 채우기
			// マスタ列を0で埋める。
			return paymentStatisticsPayItemsDao.selectPayItemColumns(conn, companyId);
		} catch (SQLException e) {
			throw new RuntimeException("지급항목 목록 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/**
	 * 표 헤더용 공제항목 목록. 조회 전에는 금액 0으로 둔다.
	 * 表ヘッダ用控除項目一覧。照会前は金額を0にする。
	 *
	 */
	public List<SalaryItemStatistics> getDeductionItemColumns(int companyId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return paymentStatisticsPayItemsDao.selectDeductionItemColumns(conn, companyId);
		} catch (SQLException e) {
			throw new RuntimeException("공제항목 목록 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/**
	 * 마스터 항목 목록에 실제 조회 금액을 채운다.
	 * マスタ項目一覧に実際の照会金額を入れる。
	 *
	 */
	public void fillItemAmounts(List<SalaryItemStatistics> columns, List<SalaryItemStatistics> actuals, long total) {
		// itemId → 실제 금액
		// itemId → 実際の金額。
		Map<Long, Long> amountById = new HashMap<>();
		if (actuals != null) {
			for (SalaryItemStatistics actual : actuals) {
				if (actual.getItemId() != null) {
					amountById.put(actual.getItemId(), actual.getAmount());
				}
			}
		}
		for (SalaryItemStatistics column : columns) {
			// 마스터 열 0 채우기
			// マスタ列を0で埋める。
			long amount = amountById.getOrDefault(column.getItemId(), 0L);
			column.setAmount(amount);
			column.setCompositionRatio(total == 0L ? 0D : (amount * 100D) / total);
		}
	}

	/**
	 * 연도, 월, 사원이름으로 해당 사원의 월 급여 항목 통계를 조회한다.
	 * 年、月、社員名で該当社員の月給与項目統計を照会する。
	 *
	 */
	public EmployeeSalaryStatistics getEmployeeSalaryStatistics(int companyId, int year, int month,
			String employeeName) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			EmployeeSalaryStatistics result = paymentStatisticsPayItemsDao
					.selectByYearMonthAndName(conn, companyId, year, month, employeeName);
			if (result == null) {
				throw new EmployeeSalaryNotFoundException(year, month, employeeName);
			}
			return result;
		} catch (SQLException e) {
			throw new RuntimeException("사원별 급여 항목 통계 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
