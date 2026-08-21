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

    // 1. 명세서 데이터 채우기
    public void getRetirementStatement(String employeeId, 
                                       RetirementMntModel statement, CompanyInfo company) {
        // 괄호() 제대로 닫고 메서드 호출은 중괄호{} 안으로 이동
        try (Connection conn = ConnectionProvider.getConnection()) { 
            retirePayslipDao.selectRetirementStatement(conn,  employeeId, statement, company);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("명세서 DB 조회 에러", e);
        }
    }

    // 2. 콤보박스용 사원 목록 조회
    public List<Employee> getSettledEmployeeList() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return retirePayslipDao.selectSettledEmployeeList(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("정산 완료 사원 목록 조회 에러", e);
        }
    }
}