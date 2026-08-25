package diligence.dailyworkrecord.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.model.DailyWorkRecord;
import config.model.EmployeeLeave;
import connection.ConnectionProvider;
import diligence.dailyworkrecord.dao.DailyWorkRecordDao;
import jdbc.JdbcUtil;

public class DailyWorkRecordService {

	// 일용근로소득 원천징수 기준 (근로소득공제 15만원, 세율 6%, 세액공제 55% => 실효세율 2.7%)
	private static final BigDecimal DAILY_INCOME_DEDUCTION = BigDecimal.valueOf(150_000);
	private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.06);
	private static final BigDecimal TAX_CREDIT_RATE = BigDecimal.valueOf(0.45); // (1 - 55% 세액공제)
	private static final BigDecimal LOCAL_TAX_RATE = BigDecimal.valueOf(0.1);

	private DailyWorkRecordDao dailyWorkRecordDao = new DailyWorkRecordDao();

	public List<EmployeeLeave> getDailyWorkerEmployees(int companyId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return dailyWorkRecordDao.selectDailyWorkerEmployees(conn, companyId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<DailyWorkRecord> getListByEmployeeId(int employeeId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return dailyWorkRecordDao.selectByEmployeeId(conn, employeeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public DailyWorkRecord getById(int dailyWorkRecordId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return dailyWorkRecordDao.selectById(conn, dailyWorkRecordId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public void insert(DailyWorkRecord item) {

		calculateTax(item);

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			dailyWorkRecordDao.insert(conn, item);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public void update(DailyWorkRecord item) {

		calculateTax(item);

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			dailyWorkRecordDao.update(conn, item);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public void delete(int dailyWorkRecordId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			dailyWorkRecordDao.deleteById(conn, dailyWorkRecordId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 일당·지급율로 지급액/소득세/지방소득세/실지급액을 계산해서 item에 채워넣음
	// (화면의 "자동계산" 표시는 미리보기일 뿐이고, 실제 저장값은 항상 서버에서 다시 계산 - 클라이언트 값을 신뢰하지 않음)
	private void calculateTax(DailyWorkRecord item) {

		BigDecimal dailyWage = item.getDailyWage() == null ? BigDecimal.ZERO : item.getDailyWage();
		BigDecimal payRate = item.getPayRate() == null ? BigDecimal.ONE : item.getPayRate();

		BigDecimal payAmount = dailyWage.multiply(payRate).setScale(0, RoundingMode.HALF_UP);

		BigDecimal taxableBase = payAmount.subtract(DAILY_INCOME_DEDUCTION);
		if (taxableBase.compareTo(BigDecimal.ZERO) < 0) {
			taxableBase = BigDecimal.ZERO;
		}

		// 10원 미만 절사
		BigDecimal incomeTax = taxableBase.multiply(TAX_RATE).multiply(TAX_CREDIT_RATE).setScale(-1,
				RoundingMode.DOWN);
		BigDecimal localTax = incomeTax.multiply(LOCAL_TAX_RATE).setScale(-1, RoundingMode.DOWN);
		BigDecimal netPay = payAmount.subtract(incomeTax).subtract(localTax);

		item.setPayAmount(payAmount);
		item.setIncomeTaxAmount(incomeTax);
		item.setLocalIncomeTaxAmount(localTax);
		item.setNetPayAmount(netPay);
	}
}
