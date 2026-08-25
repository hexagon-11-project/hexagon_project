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
 * 年度別給与総額統計Dao。
 *
 */
public class PaymentStatisticsAllDao {

	// 그래프/표에 보여줄 연도 개수
	// グラフ・表に表示する年数。
	private static final int YEAR_SPAN = 10;

	/**
	 * 선택 연도 기준으로 과거 10년간의 연도별 전체급여 통계를 조회한다.
	 * 選択年を基準に過去10年間の年度別給与総額統計を照会する。
	 *
	 */
	public List<AnnualTotalStatistics> selectAnnualTotalByEndYear(Connection conn, int companyId, int endYear)
			throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 10년 구간의 시작 연도
		// 10年区間の開始年。
		int fromYear = endYear - YEAR_SPAN + 1;
		// 증가율 계산용 직전 연도
		// 増加率計算用の直前の年。
		int queryFromYear = fromYear - 1;

		try {
			// CHAR(6) PAY_YEAR_MONTH 대비: 연월 문자열 범위로 필터
			// CHAR(6) PAY_YEAR_MONTH向け: 年月文字列範囲でフィルタする。
			String sql = "SELECT PAY_YEAR, "
					+ "NVL(SUM(MONTH_SALARY), 0) AS TOTAL_SALARY_AMOUNT, "
					+ "NVL(AVG(EMP_CNT), 0) AS AVG_EMPLOYEE_COUNT "
					+ "FROM ( "
					+ "  SELECT TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 1, 4)) AS PAY_YEAR, "
					+ "         p.PAY_YEAR_MONTH, "
					+ "         COUNT(DISTINCT pe.EMPLOYEE_ID) AS EMP_CNT, "
					+ "         NVL(SUM(pe.TOTAL_PAY_AMOUNT), 0) AS MONTH_SALARY "
					+ "  FROM PAYROLL p "
					+ "  JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
					+ "  WHERE p.COMPANY_ID = ? "
					+ "    AND p.PAY_YEAR_MONTH BETWEEN ? AND ? "
					+ "  GROUP BY TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 1, 4)), p.PAY_YEAR_MONTH "
					+ ") "
					+ "GROUP BY PAY_YEAR "
					+ "ORDER BY PAY_YEAR";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, companyId);
			// 조회 시작: 직전 연도 1월
			// 照会開始: 直前の年の1月。
			pstmt.setString(2, queryFromYear + "01");
			// 조회 끝: 선택 연도 12월
			// 照会終了: 選択年の12月。
			pstmt.setString(3, endYear + "12");
			rs = pstmt.executeQuery();

			// 연도 → 집계 행
			// 年度 → 集計行。
			Map<Integer, AnnualTotalStatistics> yearMap = new HashMap<>();
			while (rs.next()) {
				int year = rs.getInt("PAY_YEAR");
				AnnualTotalStatistics row = new AnnualTotalStatistics();
				row.setYear(year);
				row.setTotalSalaryAmount(rs.getLong("TOTAL_SALARY_AMOUNT"));
				row.setAvgEmployeeCount(rs.getDouble("AVG_EMPLOYEE_COUNT"));
				yearMap.put(year, row);
			}

			// 화면용 10년 목록 + 전년 대비 증가율
			// 画面用10年一覧 + 前年比増加率。
			return buildTenYearList(yearMap, fromYear, endYear);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * fromYear~endYear 목록을 만들고 전년 대비 증가율을 채운다.
	 * fromYear〜endYear一覧を作り前年比増加率を入れる。
	 *
	 */
	private List<AnnualTotalStatistics> buildTenYearList(Map<Integer, AnnualTotalStatistics> yearMap, int fromYear,
			int endYear) {
		List<AnnualTotalStatistics> result = new ArrayList<>(YEAR_SPAN);

		for (int year = fromYear; year <= endYear; year++) {
			AnnualTotalStatistics current = yearMap.getOrDefault(year, emptyRow(year));
			// 전년 행
			// 前年行。
			AnnualTotalStatistics previous = yearMap.get(year - 1);

			// 급여 증가율
			// 給与増加率。
			current.setSalaryGrowthRate(calcGrowthRate(
					previous == null ? null : previous.getTotalSalaryAmount(),
					current.getTotalSalaryAmount()));
			// 인원 증가율
			// 人数増加率。
			current.setEmployeeGrowthRate(calcGrowthRate(
					previous == null ? null : previous.getAvgEmployeeCount(),
					current.getAvgEmployeeCount()));

			result.add(current);
		}
		return result;
	}

	/**
	 * 데이터가 없는 연도의 빈 행을 만든다.
	 * データがない年の空行を作る。
	 *
	 */
	private AnnualTotalStatistics emptyRow(int year) {
		AnnualTotalStatistics row = new AnnualTotalStatistics();
		row.setYear(year);
		row.setTotalSalaryAmount(0L);
		row.setAvgEmployeeCount(0D);
		return row;
	}

	/**
	 * 전년 대비 증가율(%). 전년 없거나 0이면 null.
	 * 前年比増加率(%)。前年がないか0ならnull。
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
