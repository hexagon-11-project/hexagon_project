package retirement.retirementMnt.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import connection.ConnectionProvider;
import retirement.model.RetirementMntModel;
import retirement.model.RetirementMntModel.MonthlyWage;
import retirement.retirementMnt.dao.RetirementMntDao;

public class RetirementMntService {
	private RetirementMntDao retirementDao = new RetirementMntDao();

    //  퇴직급여 대상 목록 조회
	// 退職給与対象リストを照会
    public List<RetirementMntModel> getRetirementMntList(String retirementYear, String employeeId) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return retirementDao.getRetirementMntList(conn, retirementYear, employeeId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("退職給与リストの照会中にエラーが発生しました", e);
        }
    }

    //  기준일 바탕으로 최근 3개월 급여 내역 조회
    // 基準日を基に直近3ヶ月の給与履歴を照会
    public List<MonthlyWage> getRecent3MonthsPayroll(String employeeId, String baseDate) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return retirementDao.getRecent3MonthsPayroll(conn, employeeId, baseDate);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("直近3ヶ月の給与履歴の照会中にエラーが発生しました。", e);
        }
    }
}
