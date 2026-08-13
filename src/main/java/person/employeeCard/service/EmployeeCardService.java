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

	/**
	 사원번호(employeeId)를 받아 인사기록카드 데이터를 반환합니다.
	 */
	public EmployeeCard getEmployeeCard(int employeeId) {
		Connection conn = null;
		try {
			// DB 커넥션 획득
			conn = ConnectionProvider.getConnection();
			
			// DAO를 통해 데이터 조회
			EmployeeCard card = employeeCardDao.selectById(conn, employeeId);
			
			// 데이터가 없을 경우 예외 처리
			if (card == null) {
				throw new RuntimeException("해당 사원의 인사기록을 찾을 수 없습니다. 사원번호: " + employeeId);
			}
			
			return card;
			
		} catch (SQLException e) {
			throw new RuntimeException("인사기록카드 조회 중 DB 오류 발생", e);
		} finally {
			// 커넥션 자원 반납
			JdbcUtil.close(conn);
		}
	}

	/**
	 * Handler에서 직접 호출하는 메서드 (전체 사원 목록)
	 */
	public List<EmployeeCard> getAllEmployeeList() {
		Connection conn = null;
		try {
			// 커넥션을 맺고 DAO 호출
			conn = ConnectionProvider.getConnection();
			return employeeCardDao.selectAllEmployees(conn); 
		} catch (SQLException e) {
			throw new RuntimeException("전체 사원 목록 조회 중 DB 오류 발생", e);
		} finally {
			// 커넥션 자원 반납
			JdbcUtil.close(conn);
		}
	}
}