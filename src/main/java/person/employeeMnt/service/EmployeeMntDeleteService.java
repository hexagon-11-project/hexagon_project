package person.employeeMnt.service;

import java.sql.Connection;
import java.sql.SQLException;

import connection.ConnectionProvider;
import person.employeeMnt.dao.EmployeeMntDao;

public class EmployeeMntDeleteService {

    // 기존 DAO는 그대로 사용
    private EmployeeMntDao employeeDao = new EmployeeMntDao();

    public void deleteEmployees(String[] empIds) {
        if (empIds == null || empIds.length == 0) return; 

        try (Connection conn = ConnectionProvider.getConnection()) {
            conn.setAutoCommit(false); 
            try {
                for (String idStr : empIds) {
                    try {
                        // 공백 제거 후 숫자로 변환 (오류 방지)
                        int empId = Integer.parseInt(idStr.trim());
                        employeeDao.deleteEmployeeMnt(conn, empId);
                    } catch (NumberFormatException e) {
                        System.out.println("🚨 숫자 변환 실패 (무시됨) : " + idStr);
                    }
                }
                conn.commit(); 
                
            } catch (SQLException e) {
                conn.rollback(); 
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("사원 선택 삭제 에러", e);
        }
    }
}