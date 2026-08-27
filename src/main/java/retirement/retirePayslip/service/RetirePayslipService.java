package retirement.retirePayslip.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.employee.model.Employee;
import config.model.CompanyInfo;
import connection.ConnectionProvider;
import retirement.model.RetirementMntModel;
import retirement.retirePayslip.dao.RetirePayslipDao;

public class RetirePayslipService {

    private RetirePayslipDao retirePayslipDao = new RetirePayslipDao(); 

    //  명세서 데이터 채우기
    //明細書データを埋める
    public void getRetirementStatement(String employeeId, 
                                       RetirementMntModel statement, CompanyInfo company) {
     
        try (Connection conn = ConnectionProvider.getConnection()) { 
            retirePayslipDao.selectRetirementStatement(conn,  employeeId, statement, company);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("明細書DB照会エラー", e);
        }
    }

    //  사원 목록 조회
    // 社員リスト照会
    public List<Employee> getSettledEmployeeList() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return retirePayslipDao.selectSettledEmployeeList(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("精算完了社員リスト照会エラー", e);
        }
    }
}