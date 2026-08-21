package statistics.paymentStatistics.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import statistics.paymentStatistics.dto.PersonalAnnualStatistics;

/**
 * 연도별 개인연봉 통계 Dao.
 * PAYROLL / PAYROLL_EMPLOYEE를 사원 1인 기준으로 연도 단위 집계한다.
 * PAY_YEAR_MONTH 컬럼이 CHAR(6)이므로 연도 비교는 'YYYY01' ~ 'YYYY12' 범위로 처리한다.
 */
public class PaymentStatisticsDao {

    private static final int YEAR_SPAN = 10;

    /**
     * 선택 연도(endYear) 기준 과거 10년간, 해당 사원(employeeName)의 연도별 급여 통계를 조회한다.
     * 예: endYear=2026 → 2017~2026. 증가율 계산을 위해 직전 연도(fromYear - 1) 데이터도 함께 조회한다.
     */
    public List<PersonalAnnualStatistics> selectPersonalAnnualByEndYear(Connection conn, String employeeName, int endYear)
            throws SQLException {
        int fromYear = endYear - YEAR_SPAN + 1;
        int queryFromYear = fromYear - 1;

        String sql = "SELECT PAY_YEAR, "
                   + "NVL(SUM(MONTH_PAY), 0) AS TOTAL_PAY, "
                   + "NVL(SUM(MONTH_DED), 0) AS TOTAL_DED "
                   + "FROM ( "
                   + "  SELECT TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 1, 4)) AS PAY_YEAR, "
                   + "         p.PAY_YEAR_MONTH, "
                   + "         NVL(SUM(pe.TOTAL_PAY_AMOUNT), 0) AS MONTH_PAY, "
                   + "         NVL(SUM(pe.TOTAL_DEDUCTION_AMOUNT), 0) AS MONTH_DED "
                   + "  FROM PAYROLL p "
                   + "  JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
                   + "  JOIN EMPLOYEE e ON e.EMPLOYEE_ID = pe.EMPLOYEE_ID "
                   + "  WHERE e.EMPLOYEE_NAME = ? "
                   + "    AND p.PAY_YEAR_MONTH BETWEEN ? AND ? "
                   + "  GROUP BY TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 1, 4)), p.PAY_YEAR_MONTH "
                   + ") "
                   + "GROUP BY PAY_YEAR "
                   + "ORDER BY PAY_YEAR";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeName);
            pstmt.setString(2, queryFromYear + "01");
            pstmt.setString(3, endYear + "12");

            Map<Integer, PersonalAnnualStatistics> yearMap = new HashMap<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int year = rs.getInt("PAY_YEAR");
                    PersonalAnnualStatistics row = new PersonalAnnualStatistics();
                    row.setYear(year);
                    row.setTotalPayAmount(rs.getLong("TOTAL_PAY"));
                    row.setTotalDeductionAmount(rs.getLong("TOTAL_DED"));
                    row.setNetPayAmount(row.getTotalPayAmount() - row.getTotalDeductionAmount());
                    yearMap.put(year, row);
                }
            }

            return buildTenYearList(yearMap, fromYear, endYear);
        }
    }

    /** fromYear~endYear 목록을 만들고 전년 대비 증가율을 채운다. */
    private List<PersonalAnnualStatistics> buildTenYearList(Map<Integer, PersonalAnnualStatistics> yearMap, int fromYear, int endYear) {
        List<PersonalAnnualStatistics> result = new ArrayList<>(YEAR_SPAN);

        for (int year = fromYear; year <= endYear; year++) {
            PersonalAnnualStatistics current = yearMap.getOrDefault(year, emptyRow(year));
            PersonalAnnualStatistics previous = yearMap.get(year - 1);

            current.setSalaryGrowthRate(calcGrowthRate(
                    previous == null ? null : previous.getTotalPayAmount(),
                    current.getTotalPayAmount()));

            result.add(current);
        }
        return result;
    }

    private PersonalAnnualStatistics emptyRow(int year) {
        PersonalAnnualStatistics row = new PersonalAnnualStatistics();
        row.setYear(year);
        return row;
    }

    /** 전년 대비 증가율(%). 전년 없거나 0이면 null. */
    private Double calcGrowthRate(Long previous, long current) {
        if (previous == null || previous == 0L) {
            return null;
        }
        return ((current - previous) / (double) previous) * 100D;
    }
}
