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
 * 月別給与総額統計Dao。
 *
 */
public class PaymentStatisticsAllMonthDao {

	// 한 해의 월 개수
	// 1年の月数。
	private static final int MONTH_COUNT = 12;

	/**
	 * 선택 연도의 1월~12월 전체급여 통계를 조회한다.
	 * 選択年の1月〜12月給与総額統計を照会する。
	 *
	 */
	public List<MonthlyTotalStatistics> selectMonthlyTotalByYear(Connection conn, int companyId, int year)
			throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 1월의 전월이 속한 해
		// 1月の前月が属する年。
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
			// 조회 시작: 전년 12월
			// 照会開始: 前年12月。
			pstmt.setString(2, prevYear + "12");
			// 조회 끝: 선택 연도 12월
			// 照会終了: 選択年の12月。
			pstmt.setString(3, year + "12");
			rs = pstmt.executeQuery();

			// 연월 키 → 그 달 집계
			// 年月キー → その月の集計。
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

	/**
	 * 선택 연도 1~12월 목록을 만들고 전월 대비 증가율을 채운다.
	 * 選択年1〜12月一覧を作り前月比増加率を入れる。
	 *
	 */
	private List<MonthlyTotalStatistics> buildTwelveMonthList(Map<Integer, MonthlyTotalStatistics> monthMap, int year) {
		List<MonthlyTotalStatistics> result = new ArrayList<>(MONTH_COUNT);

		for (int month = 1; month <= MONTH_COUNT; month++) {
			MonthlyTotalStatistics current = monthMap.getOrDefault(toYearMonthKey(year, month), emptyRow(year, month));
			// 전월 행
			// 前月行。
			MonthlyTotalStatistics previous = monthMap.get(toYearMonthKey(previousYear(year, month), previousMonth(month)));

			// 급여 증가율
			// 給与増加率。
			current.setSalaryGrowthRate(calcGrowthRate(
					previous == null ? null : previous.getTotalSalaryAmount(),
					current.getTotalSalaryAmount()));
			// 인원 증가율
			// 人数増加率。
			current.setEmployeeGrowthRate(calcGrowthRate(
					previous == null ? null : previous.getEmployeeCount(),
					current.getEmployeeCount()));

			result.add(current);
		}
		return result;
	}

	/**
	 * 데이터가 없는 월의 빈 행을 만든다.
	 * データがない月の空行を作る。
	 *
	 */
	private MonthlyTotalStatistics emptyRow(int year, int month) {
		MonthlyTotalStatistics row = new MonthlyTotalStatistics();
		row.setYear(year);
		row.setMonth(month);
		row.setTotalSalaryAmount(0L);
		row.setEmployeeCount(0);
		return row;
	}

	/**
	 * 연월을 맵 키로 만든다.
	 * 年月をマップキーにする。
	 *
	 */
	private int toYearMonthKey(int year, int month) {
		return year * 100 + month;
	}

	/**
	 * 전월이 속한 연도를 구한다.
	 * 前月が属する年を求める。
	 *
	 */
	private int previousYear(int year, int month) {
		return month == 1 ? year - 1 : year;
	}

	/**
	 * 전월 번호를 구한다.
	 * 前月番号を求める。
	 *
	 */
	private int previousMonth(int month) {
		return month == 1 ? 12 : month - 1;
	}

	/**
	 * 전월 대비 증가율(%). 전월 없거나 0이면 null.
	 * 前月比増加率(%)。前月がないか0ならnull。
	 *
	 */
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
