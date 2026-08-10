package person.employeeCard.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import person.employeeCard.dao.EmployeeCardDao;
import person.model.EmployeeCard;

public class EmployeeCardService {
	private EmployeeCardDao employeeCardDao = new EmployeeCardDao();

	/**
	 * 사원번호(employeeId)를 받아 인사기록카드 데이터를 반환합니다.
	 */
	public EmployeeCard getEmployeeCard(int employeeId) {
		Connection conn = null;
		try {
			// DB 커넥션 획득
			conn = ConnectionProvider.getConnection();
			
			// DAO를 통해 데이터 조회
			EmployeeCard card = employeeCardDao.selectById(conn, employeeId);
			
			// 데이터가 없을 경우 예외 처리 (필요에 따라 커스텀 예외로 변경 가능)
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
	 * 전체 사원 목록 (사원번호, 이름 등 기본 정보) 조회
	 */
	public List<EmployeeCard> selectAllEmployees(Connection conn) throws SQLException {
		List<EmployeeCard> list = new ArrayList<>();
		String sql = "SELECT EMPLOYEE_ID, EMPLOYEE_NO, EMPLOYEE_NAME FROM EMPLOYEE ORDER BY EMPLOYEE_NAME ASC";
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			
			while (rs.next()) {
				EmployeeCard card = new EmployeeCard();
				// 주의: EmployeeCard 모델에 employeeId(int) 변수와 세터가 없다면 추가해주셔야 합니다!
				card.setEmployeeNo(rs.getString("EMPLOYEE_NO"));
				card.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				// 필요하다면 임시로 ID도 담을 수 있게 처리
				list.add(card);
			}
		}
		return list;
	}
	public List<EmployeeCard> getAllEmployeeList() {
		
		return null;
	}

}
