package person.employeeCard.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import person.employeeCard.dao.EmployeeCardDao;
import person.model.EmployeeCard;

public class EmployeeCardService {
	private EmployeeCardDao employeeCardDao = new EmployeeCardDao();

	
	 //사원번호(employeeId)를 받아 인사기록카드 데이터를 반환합니다.
	 // 社員番号(employeeId)を受け取り、人事記録カードデータを返します。
	
	public EmployeeCard getEmployeeCard(int employeeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			
			EmployeeCard card = employeeCardDao.selectById(conn, employeeId);
			
			if (card == null) {
				throw new RuntimeException("該当社員の人事記録が見つかりません。社員番号： " + employeeId);
			}
			
			return card;
			
		} catch (SQLException e) {
			throw new RuntimeException("人事記録カードの照会中にDBエラーが発生", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	
	 // Handler에서 직접 호출하는 메서드 (전체 사원 목록)
	 // Handlerから直接呼び出すメソッド (全社員リスト)
	
	public List<EmployeeCard> getAllEmployeeList() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return employeeCardDao.selectAllEmployees(conn); 
		} catch (SQLException e) {
			throw new RuntimeException("全社員リストの照会中にDBエラーが発生", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}