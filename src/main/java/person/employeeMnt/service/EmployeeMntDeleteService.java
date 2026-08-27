package person.employeeMnt.service;

import java.sql.Connection;
import java.sql.SQLException;

import connection.ConnectionProvider;
import person.employeeMnt.dao.EmployeeMntDao;

public class EmployeeMntDeleteService {

    private EmployeeMntDao employeeDao = new EmployeeMntDao();

    public void deleteEmployees(String[] empIds) {
        if (empIds == null || empIds.length == 0) return; 

        try (Connection conn = ConnectionProvider.getConnection()) {
            conn.setAutoCommit(false); 
            try {
                for (String idStr : empIds) {
                    try {
                        // 공백 제거 후 숫자로 변환 (오류 방지)
                    	// 空白除去後に数値へ変換 (エラー防止)
                        int empId = Integer.parseInt(idStr.trim());
                        employeeDao.deleteEmployeeMnt(conn, empId);
                    } catch (NumberFormatException e) {
                        
                    }
                }
                conn.commit(); 
                
            } catch (SQLException e) {
                conn.rollback(); 
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("社員選択削除エラー", e);
        }
    }
}