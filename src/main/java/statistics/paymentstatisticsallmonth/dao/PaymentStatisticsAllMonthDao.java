package statistics.paymentstatisticsallmonth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdbc.JdbcUtil;
import statistics.model.MonthlyTotalStatistics;

/**
 * 월별 전체급여 통계 Dao.
 * PAYROLL / PAYROLL_EMPLOYEE를 월 단위로 집계한다.
 *
 * PAY_YEAR_MONTH 컬럼이 CHAR(6) 이므로 연월 비교는
 * 'YYYY01' ~ 'YYYY12' 범위로 처리한다.
 */
public class PaymentStatisticsAllMonthDao {

	private static final int MONTH_COUNT = 12;

	/**
	 * 선택 연도의 1월~12월 전체급여 통계를 조회한다.
	 * 1월 증가율 계산을 위해 직전 연도 12월 데이터도 함께 조회한다.
	 */
	public List<MonthlyTotalStatistics> selectMonthlyTotalByYear(Connection conn, int companyId, int year)
			throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		int prevYear = year - 1;

		try {
			String sql = "SELECT TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 1, 4)) AS PAY_YEAR, "
					+ "TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 5, 2)) AS PAY_MONTH, "
					+ "COUNT(DISTINCT pe.EMPLOYEE_ID) AS EMP_CNT, "
					+ "NVL(SUM(pe.TOTAL_PAY_AMOUNT), 0) AS TOTAL_SALARY_AMOUNT "
					+ "FROM PAYROLL p "
					+ "JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
					+ "WHERE p.COMPANY_ID = ? "
					+ "  AND p.PAY_YEAR_MONTH BETWEEN ? AND ? "
					+ "GROUP BY TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 1, 4)), "
					+ "         TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 5, 2)) "
					+ "ORDER BY PAY_YEAR, PAY_MONTH";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, companyId);
			pstmt.setString(2, prevYear + "12");
			pstmt.setString(3, year + "12");
			rs = pstmt.executeQuery();

			Map<Integer, MonthlyTotalStatistics> monthMap = new HashMap<>();
			while (rs.next()) {
				int payYear = rs.getInt("PAY_YEAR");
				int payMonth = rs.getInt("PAY_MONTH");
				MonthlyTotalStatistics row = new MonthlyTotalStatistics();
				row.setYear(payYear);
				row.setMonth(payMonth);
				row.setTotalSalaryAmount(rs.getLong("TOTAL_SALARY_AMOUNT"));
				row.setEmployeeCount(rs.getInt("EMP_CNT"));
				monthMap.put(toYearMonthKey(payYear, payMonth), row);
			}

			return buildTwelveMonthList(monthMap, year);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/** 선택 연도 1~12월 목록을 만들고 전월 대비 증가율을 채운다. */
	private List<MonthlyTotalStatistics> buildTwelveMonthList(Map<Integer, MonthlyTotalStatistics> monthMap, int year) {
		List<MonthlyTotalStatistics> result = new ArrayList<>(MONTH_COUNT);

		for (int month = 1; month <= MONTH_COUNT; month++) {
			MonthlyTotalStatistics current = monthMap.getOrDefault(toYearMonthKey(year, month), emptyRow(year, month));
			MonthlyTotalStatistics previous = monthMap.get(toYearMonthKey(previousYear(year, month), previousMonth(month)));

			current.setSalaryGrowthRate(calcGrowthRate(
					previous == null ? null : previous.getTotalSalaryAmount(),
					current.getTotalSalaryAmount()));
			current.setEmployeeGrowthRate(calcGrowthRate(
					previous == null ? null : previous.getEmployeeCount(),
					current.getEmployeeCount()));

			result.add(current);
		}
		return result;
	}

	private MonthlyTotalStatistics emptyRow(int year, int month) {
		MonthlyTotalStatistics row = new MonthlyTotalStatistics();
		row.setYear(year);
		row.setMonth(month);
		row.setTotalSalaryAmount(0L);
		row.setEmployeeCount(0);
		return row;
	}

	private int toYearMonthKey(int year, int month) {
		return year * 100 + month;
	}

	private int previousYear(int year, int month) {
		return month == 1 ? year - 1 : year;
	}

	private int previousMonth(int month) {
		return month == 1 ? 12 : month - 1;
	}

	/** 전월 대비 증가율(%). 전월 없거나 0이면 null. */
	private Double calcGrowthRate(Number previous, Number current) {
		if (previous == null) {
			return null;
		}
		double prev = previous.doubleValue();
		if (prev == 0D) {
			return null;
		}
		return ((current.doubleValue() - prev) / prev) * 100D;
	}
}
