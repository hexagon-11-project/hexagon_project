package statistics.paymentStatistics.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import statistics.paymentStatistics.dao.PaymentStatisticsDao;
import statistics.paymentStatistics.dto.PersonalAnnualStatistics;

/**
 * 연도별 개인연봉 통계 Service.
 * 선택 연도 기준 과거 10년, 선택 사원 1인의 연도별 통계를 조회한다.
 */
public class PaymentStatisticsService {

    private PaymentStatisticsDao dao = new PaymentStatisticsDao();

    public List<PersonalAnnualStatistics> getPersonalAnnualList(String employeeName, int endYear) {
        if (employeeName == null || employeeName.trim().isEmpty()) {
            return new ArrayList<>();
        }

        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return dao.selectPersonalAnnualByEndYear(conn, employeeName.trim(), endYear);
        } catch (Exception e) {
            throw new RuntimeException("연도별 개인연봉 통계 조회 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
}
