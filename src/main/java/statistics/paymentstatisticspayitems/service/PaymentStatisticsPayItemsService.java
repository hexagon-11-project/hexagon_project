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
 * 연도, 월, 사원이름으로 해당 월 지급내역·공제항목을 조회한다.
 */
public class PaymentStatisticsPayItemsService {

	private PaymentStatisticsPayItemsDao paymentStatisticsPayItemsDao = new PaymentStatisticsPayItemsDao();

	/**
	 * 사원 선택 팝업용 목록 조회.
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

	/** 사원 선택 팝업의 부서 필터 목록. */
	public List<String> getDepartmentList(int companyId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return paymentStatisticsPayItemsDao.selectDepartmentList(conn, companyId);
		} catch (SQLException e) {
			throw new RuntimeException("부서 목록 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/**
	 * 표 헤더용 지급항목 목록. 조회 전에는 금액 0으로 둔다.
	 */
	public List<SalaryItemStatistics> getPayItemColumns(int companyId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return paymentStatisticsPayItemsDao.selectPayItemColumns(conn, companyId);
		} catch (SQLException e) {
			throw new RuntimeException("지급항목 목록 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/**
	 * 표 헤더용 공제항목 목록. 조회 전에는 금액 0으로 둔다.
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
	 */
	public void fillItemAmounts(List<SalaryItemStatistics> columns, List<SalaryItemStatistics> actuals, long total) {
		Map<Long, Long> amountById = new HashMap<>();
		if (actuals != null) {
			for (SalaryItemStatistics actual : actuals) {
				if (actual.getItemId() != null) {
					amountById.put(actual.getItemId(), actual.getAmount());
				}
			}
		}
		for (SalaryItemStatistics column : columns) {
			long amount = amountById.getOrDefault(column.getItemId(), 0L);
			column.setAmount(amount);
			column.setCompositionRatio(total == 0L ? 0D : (amount * 100D) / total);
		}
	}

	/**
	 * 연도, 월, 사원이름으로 해당 사원의 월 급여 항목 통계를 조회한다.
	 * 해당 월 급여 데이터가 없으면 EmployeeSalaryNotFoundException을 던진다.
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
