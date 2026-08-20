package payment.fourinsureList.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import payment.fourinsureList.dao.FourinsureListDao;
import payment.model.PaymentInsuranceLedger;

/**
 * 4대보험 대장 Service.
 * 귀속연·월·차수로 정산기간·급여지급일과 사원별 4대보험 공제액을 조회한다.
 */
public class FourinsureListService {

	private FourinsureListDao fourinsureListDao = new FourinsureListDao();

	/**
	 * 귀속연·월·차수로 4대보험 대장을 조회한다.
	 * 귀속연월이 잘못됐거나 해당 급여차수가 없으면 null을 반환한다.
	 */
	public PaymentInsuranceLedger getInsuranceLedger(String payYear, String payMonth, int paySequence) {
		String payYearMonth = toPayYearMonth(payYear, payMonth);
		if (payYearMonth == null) {
			return null;
		}

		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return fourinsureListDao.selectByYearMonthSeq(conn, payYearMonth, paySequence);
		} catch (SQLException e) {
			throw new RuntimeException("4대보험 대장 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/** 귀속연 + 귀속월 → PAY_YEAR_MONTH(YYYYMM) */
	public String toPayYearMonth(String payYear, String payMonth) {
		if (payYear == null || payYear.trim().isEmpty() || payMonth == null || payMonth.trim().isEmpty()) {
			return null;
		}
		String year = payYear.trim();
		String month = payMonth.trim();
		if (month.length() == 1) {
			month = "0" + month;
		}
		if (year.length() != 4 || month.length() != 2) {
			return null;
		}
		return year + month;
	}

	/** 사원별 4대보험 총합계(사업주+근로자)를 모두 더한 전체 합계. */
	public long sumInsuranceAmount(List<PaymentInsuranceLedger> employeeList) {
		long sum = 0L;
		if (employeeList == null) {
			return sum;
		}
		for (PaymentInsuranceLedger row : employeeList) {
			sum += row.getGrandTotal();
		}
		return sum;
	}

	/** 항목별 전 사원 합계. 사업주/근로자 칸에 넣을 조회 금액만 합산한다. */
	public PaymentInsuranceLedger sumColumnTotals(List<PaymentInsuranceLedger> employeeList) {
		PaymentInsuranceLedger totals = new PaymentInsuranceLedger();
		if (employeeList == null) {
			return totals;
		}
		for (PaymentInsuranceLedger row : employeeList) {
			totals.setNationalPension(totals.getNationalPension() + row.getNationalPension());
			totals.setHealthInsurance(totals.getHealthInsurance() + row.getHealthInsurance());
			totals.setLongTermCare(totals.getLongTermCare() + row.getLongTermCare());
			totals.setEmploymentInsurance(totals.getEmploymentInsurance() + row.getEmploymentInsurance());
		}
		return totals;
	}
}
