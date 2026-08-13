package statistics.paymentstatisticsall.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdbc.JdbcUtil;
import statistics.model.AnnualTotalStatistics;

/**
 * 연도별 전체급여 통계 Dao.
 * PAYROLL / PAYROLL_EMPLOYEE를 연도 단위로 집계한다.
 */
public class PaymentStatisticsAllDao {

	private static final int YEAR_SPAN = 10;

	/**
	 * 선택 연도 기준으로 과거 10년간의 연도별 전체급여 통계를 조회한다.
	 * 예: endYear=2026 → 2017~2026
	 * 증가율 계산을 위해 직전 연도(fromYear - 1) 데이터도 함께 조회한다.
	 *
	 * @param conn      DB 연결
	 * @param companyId 회사 아이디
	 * @param endYear   선택 연도(구간의 마지막 연도)
	 * @return 과거 10년 목록 (오름차순, 데이터 없는 연도는 0으로 채움)
	 */
	public List<AnnualTotalStatistics> selectAnnualTotalByEndYear(Connection conn, int companyId, int endYear)
			throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		int fromYear = endYear - YEAR_SPAN + 1;
		int queryFromYear = fromYear - 1;

		try {
			String sql = "SELECT PAY_YEAR, "
					+ "NVL(SUM(MONTH_SALARY), 0) AS TOTAL_SALARY_AMOUNT, "
					+ "NVL(AVG(EMP_CNT), 0) AS AVG_EMPLOYEE_COUNT "
					+ "FROM ( "
					+ "  SELECT SUBSTR(p.PAY_YEAR_MONTH, 1, 4) AS PAY_YEAR, "
					+ "         p.PAY_YEAR_MONTH, "
					+ "         COUNT(DISTINCT pe.EMPLOYEE_ID) AS EMP_CNT, "
					+ "         NVL(SUM(pe.TOTAL_PAY_AMOUNT), 0) AS MONTH_SALARY "
					+ "  FROM PAYROLL p "
					+ "  JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
					+ "  WHERE p.COMPANY_ID = ? "
					+ "    AND SUBSTR(p.PAY_YEAR_MONTH, 1, 4) BETWEEN ? AND ? "
					+ "  GROUP BY SUBSTR(p.PAY_YEAR_MONTH, 1, 4), p.PAY_YEAR_MONTH "
					+ ") "
					+ "GROUP BY PAY_YEAR "
					+ "ORDER BY PAY_YEAR";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, companyId);
			pstmt.setString(2, String.valueOf(queryFromYear));
			pstmt.setString(3, String.valueOf(endYear));
			rs = pstmt.executeQuery();

			Map<Integer, AnnualTotalStatistics> yearMap = new HashMap<>();
			while (rs.next()) {
				int year = Integer.parseInt(rs.getString("PAY_YEAR"));
				AnnualTotalStatistics row = new AnnualTotalStatistics();
				row.setYear(year);
				row.setTotalSalaryAmount(rs.getLong("TOTAL_SALARY_AMOUNT"));
				row.setAvgEmployeeCount(rs.getDouble("AVG_EMPLOYEE_COUNT"));
				yearMap.put(year, row);
			}

			return buildTenYearList(yearMap, fromYear, endYear);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/** fromYear~endYear 목록을 만들고 전년 대비 증가율을 채운다. */
	private List<AnnualTotalStatistics> buildTenYearList(Map<Integer, AnnualTotalStatistics> yearMap, int fromYear,
			int endYear) {
		List<AnnualTotalStatistics> result = new ArrayList<>(YEAR_SPAN);

		for (int year = fromYear; year <= endYear; year++) {
			AnnualTotalStatistics current = yearMap.getOrDefault(year, emptyRow(year));
			AnnualTotalStatistics previous = yearMap.get(year - 1);

			current.setSalaryGrowthRate(calcGrowthRate(
					previous == null ? null : previous.getTotalSalaryAmount(),
					current.getTotalSalaryAmount()));
			current.setEmployeeGrowthRate(calcGrowthRate(
					previous == null ? null : previous.getAvgEmployeeCount(),
					current.getAvgEmployeeCount()));

			result.add(current);
		}
		return result;
	}

	private AnnualTotalStatistics emptyRow(int year) {
		AnnualTotalStatistics row = new AnnualTotalStatistics();
		row.setYear(year);
		row.setTotalSalaryAmount(0L);
		row.setAvgEmployeeCount(0D);
		return row;
	}

	/** 전년 대비 증가율(%). 전년 없거나 0이면 null. */
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
