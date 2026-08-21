package statistics.paymentstatisticsmonth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import statistics.paymentstatisticsmonth.dto.PersonalMonthlyStatistics;

/**
 * 월별 개인급여 통계 Dao.
 * PAYROLL / PAYROLL_EMPLOYEE를 사원 1인 기준으로 선택 연도의 월 단위 집계한다.
 * PAY_YEAR_MONTH 컬럼이 CHAR(6)이므로 연월 비교는 'YYYY01' ~ 'YYYY12' 범위로 처리한다.
 */
public class PaymentStatisticsMonthDao {

    private static final int MONTH_COUNT = 12;

    /**
     * 선택 연도(year)의 1월~12월, 해당 사원(employeeName)의 월별 급여 통계를 조회한다.
     */
    public List<PersonalMonthlyStatistics> selectPersonalMonthlyByYear(Connection conn, String employeeName, int year)
            throws SQLException {

        String sql = "SELECT TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 5, 2)) AS PAY_MONTH, "
                   + "NVL(SUM(pe.TOTAL_PAY_AMOUNT), 0) AS TOTAL_PAY, "
                   + "NVL(SUM(pe.TOTAL_DEDUCTION_AMOUNT), 0) AS TOTAL_DED "
                   + "FROM PAYROLL p "
                   + "JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
                   + "JOIN EMPLOYEE e ON e.EMPLOYEE_ID = pe.EMPLOYEE_ID "
                   + "WHERE e.EMPLOYEE_NAME = ? "
                   + "  AND p.PAY_YEAR_MONTH BETWEEN ? AND ? "
                   + "GROUP BY TO_NUMBER(SUBSTR(p.PAY_YEAR_MONTH, 5, 2)) "
                   + "ORDER BY PAY_MONTH";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeName);
            pstmt.setString(2, year + "01");
            pstmt.setString(3, year + "12");

            Map<Integer, PersonalMonthlyStatistics> monthMap = new HashMap<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int month = rs.getInt("PAY_MONTH");
                    PersonalMonthlyStatistics row = new PersonalMonthlyStatistics();
                    row.setYear(year);
                    row.setMonth(month);
                    row.setTotalPayAmount(rs.getLong("TOTAL_PAY"));
                    row.setTotalDeductionAmount(rs.getLong("TOTAL_DED"));
                    row.setNetPayAmount(row.getTotalPayAmount() - row.getTotalDeductionAmount());
                    monthMap.put(month, row);
                }
            }

            return buildTwelveMonthList(monthMap, year);
        }
    }

    /** 1월~12월 목록을 만들고 데이터 없는 달은 0으로 채운다. */
    private List<PersonalMonthlyStatistics> buildTwelveMonthList(Map<Integer, PersonalMonthlyStatistics> monthMap, int year) {
        List<PersonalMonthlyStatistics> result = new ArrayList<>(MONTH_COUNT);

        for (int month = 1; month <= MONTH_COUNT; month++) {
            result.add(monthMap.getOrDefault(month, emptyRow(year, month)));
        }
        return result;
    }

    private PersonalMonthlyStatistics emptyRow(int year, int month) {
        PersonalMonthlyStatistics row = new PersonalMonthlyStatistics();
        row.setYear(year);
        row.setMonth(month);
        return row;
    }
}
