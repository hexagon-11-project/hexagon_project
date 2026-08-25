package payment.fourinsureList.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import payment.fourinsureList.dao.FourinsureListDao;
import payment.model.PaymentInsuranceLedger;

/**
 * 4대보험 대장 조회 Service.
 * 社会保険(4大保険)台帳の照会Service。
 */
public class FourinsureListService {

	private FourinsureListDao fourinsureListDao = new FourinsureListDao();

	/**
	 * 귀속연·월·차수로 4대보험 대장을 조회한다.
	 * 帰属年・月・次数で社会保険(4大保険)台帳を照会する。
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

	/**
	 * 귀속연과 귀속월을 PAY_YEAR_MONTH(YYYYMM)로 합친다.
	 * 帰属年と帰属月をPAY_YEAR_MONTH(YYYYMM)に結合する。
	 */
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

	/**
	 * 사원별 4대보험 총합계(사업주+근로자)를 모두 더한다.
	 * 社員別の社会保険(4大保険)総計(事業主+労働者)をすべて加算する。
	 */
	public long sumInsuranceAmount(List<PaymentInsuranceLedger> employeeList) {
		long sum = 0L;
		if (employeeList == null) {
			return sum;
		}
		for (PaymentInsuranceLedger row : employeeList) {
			// 행 총계 = (국민+건강+장기요양+고용) × 2
			// 行総計 = (国民年金+健康保険+介護保険+雇用保険) × 2。
			sum += row.getGrandTotal();
		}
		return sum;
	}

	/**
	 * 항목별 전 사원 합계를 만든다.
	 * 項目別の全社員合計を作成する。
	 */
	public PaymentInsuranceLedger sumColumnTotals(List<PaymentInsuranceLedger> employeeList) {
		PaymentInsuranceLedger totals = new PaymentInsuranceLedger();
		if (employeeList == null) {
			return totals;
		}
		for (PaymentInsuranceLedger row : employeeList) {
			// 한 쪽(조회 금액)만 누적한다
			// 一方(照会金額)だけを累積する。
			totals.setNationalPension(totals.getNationalPension() + row.getNationalPension());
			totals.setHealthInsurance(totals.getHealthInsurance() + row.getHealthInsurance());
			totals.setLongTermCare(totals.getLongTermCare() + row.getLongTermCare());
			totals.setEmploymentInsurance(totals.getEmploymentInsurance() + row.getEmploymentInsurance());
		}
		return totals;
	}
}
