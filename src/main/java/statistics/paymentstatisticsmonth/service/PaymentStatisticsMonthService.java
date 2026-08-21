package statistics.paymentstatisticsmonth.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import statistics.paymentstatisticsmonth.dao.PaymentStatisticsMonthDao;
import statistics.paymentstatisticsmonth.dto.PersonalMonthlyStatistics;

/**
 * 월별 개인급여 통계 Service.
 * 선택 연도, 선택 사원 1인의 1월~12월 통계를 조회한다.
 */
public class PaymentStatisticsMonthService {

    private PaymentStatisticsMonthDao dao = new PaymentStatisticsMonthDao();

    public List<PersonalMonthlyStatistics> getPersonalMonthlyList(String employeeName, int year) {
        if (employeeName == null || employeeName.trim().isEmpty()) {
            return new ArrayList<>();
        }

        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return dao.selectPersonalMonthlyByYear(conn, employeeName.trim(), year);
        } catch (Exception e) {
            throw new RuntimeException("월별 개인급여 통계 조회 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
}
