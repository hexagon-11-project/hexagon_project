package retirement.retirementMnt.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import connection.ConnectionProvider;
import retirement.model.RetirementMntModel;
import retirement.model.RetirementMntModel.MonthlyWage;
import retirment.retirementMnt.dao.RetirementMntDao;

public class RetirementMntService {
	private RetirementMntDao retirementDao = new RetirementMntDao();

    // 1. 퇴직급여 대상 목록 조회
    public List<RetirementMntModel> getRetirementMntList(String retirementYear, String employeeId) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return retirementDao.getRetirementMntList(conn, retirementYear, employeeId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("퇴직급여 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 2. 기준일 바탕으로 최근 3개월 급여 내역 조회
    public List<MonthlyWage> getRecent3MonthsPayroll(String employeeId, String baseDate) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return retirementDao.getRecent3MonthsPayroll(conn, employeeId, baseDate);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("최근 3개월 급여 내역 조회 중 오류가 발생했습니다.", e);
        }
    }
}
