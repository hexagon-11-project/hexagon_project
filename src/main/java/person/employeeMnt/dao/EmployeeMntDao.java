package person.employeeMnt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.employee.model.Employee;
import jdbc.JdbcUtil;

public class EmployeeMntDao {
	public Employee selectById(Connection conn, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			String sql = "SELECT * FROM employee WHERE employee_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, employeeId); // ? 에 들어갈 파라미터 세팅
		
			rs = pstmt.executeQuery();

			Employee emp = null;
			if (rs.next()) {
				emp = new Employee();
				
				// 2. Setter를 이용해 값을 하나씩 세팅
				emp.setEmployeeId(rs.getInt("employee_id"));
				emp.setEmployeeNo(rs.getString("employee_no"));
				emp.setEmploymentType(rs.getString("employment_type"));
				emp.setEmployeeName(rs.getString("employee_name"));
				emp.setDepartment(rs.getString("department"));
				emp.setPosition(rs.getString("position"));
				emp.setResidentRegNo(rs.getString("resident_reg_no"));
				emp.setHireDate(rs.getDate("hire_date")); 
				emp.setMobile(rs.getString("mobile"));
				emp.setEmail(rs.getString("email"));
				emp.setRetirementYn(rs.getString("retirement_yn")); 
			}
			return emp;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
	
	// 사원 전체 리스트를 조회하는 메서드
	public List<Employee> selectList(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT * FROM employee ORDER BY employee_id DESC";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			List<Employee> empList = new ArrayList<>();
			
			while (rs.next()) {
				Employee emp = new Employee();
				
				emp.setEmployeeId(rs.getInt("employee_id"));
				emp.setEmployeeNo(rs.getString("employee_no"));
				emp.setEmploymentType(rs.getString("employment_type"));
				emp.setEmployeeName(rs.getString("employee_name"));
				emp.setDepartment(rs.getString("department"));
				emp.setPosition(rs.getString("position"));
				emp.setResidentRegNo(rs.getString("resident_reg_no"));
				emp.setHireDate(rs.getDate("hire_date"));
				emp.setMobile(rs.getString("mobile"));
				emp.setEmail(rs.getString("email"));
				emp.setRetirementYn(rs.getString("retirement_yn"));
				
				empList.add(emp);
			}
			return empList; 
			
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 한글 기준 고용형태 및 재직상태 카운트를 한 번에 가져오는 통합 쿼리 메서드
	public Map<String, Integer> getAllCounts(Connection conn) throws SQLException {
		String sql = "SELECT " +
				"  SUM(CASE WHEN employment_type = '정규직' THEN 1 ELSE 0 END) AS regular, " +
				"  SUM(CASE WHEN employment_type = '계약직' THEN 1 ELSE 0 END) AS contract, " +
				"  SUM(CASE WHEN employment_type = '임시직' THEN 1 ELSE 0 END) AS temp, " +
				"  SUM(CASE WHEN employment_type = '일용직' THEN 1 ELSE 0 END) AS daily, " +
				"  SUM(CASE WHEN retirement_yn = 'Y' THEN 1 ELSE 0 END) AS retire, " +
				"  SUM(CASE WHEN retirement_yn IS NULL OR retirement_yn != 'Y' THEN 1 ELSE 0 END) AS active, " +
				"  COUNT(*) AS total " +
				"FROM employee";
		
		Map<String, Integer> countMap = new HashMap<>();
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) {
				countMap.put("regular", rs.getInt("regular"));
				countMap.put("contract", rs.getInt("contract"));
				countMap.put("temp", rs.getInt("temp"));
				countMap.put("daily", rs.getInt("daily"));
				countMap.put("retire", rs.getInt("retire"));
				countMap.put("active", rs.getInt("active"));
				countMap.put("total", rs.getInt("total"));
			}
		}
		return countMap;
	}
	// 지정한 범위만큼 사원 리스트를 잘라서 조회하는 페이징 쿼리 
		public List<Employee> selectListByPaging(Connection conn, int firstRow, int endRow) throws SQLException {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			String sql = "SELECT * FROM (" +
					"    SELECT ROWNUM rnum, emp.* FROM (" +
					"        SELECT * FROM employee ORDER BY employee_id DESC" +
					"    ) emp WHERE ROWNUM <= ?" +
					") WHERE rnum >= ?";
			
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, endRow);
				pstmt.setInt(2, firstRow);
				rs = pstmt.executeQuery();

				List<Employee> empList = new java.util.ArrayList<>();
				
				while (rs.next()) {
					Employee emp = new Employee();
					emp.setEmployeeId(rs.getInt("employee_id"));
					emp.setEmploymentType(rs.getString("employment_type"));
					emp.setEmployeeNo(rs.getString("employee_no"));
					emp.setEmployeeName(rs.getString("employee_name"));
					emp.setDepartment(rs.getString("department"));
					emp.setPosition(rs.getString("position"));
					emp.setResidentRegNo(rs.getString("resident_reg_no"));
					emp.setHireDate(rs.getDate("hire_date"));
					emp.setMobile(rs.getString("mobile"));
					emp.setEmail(rs.getString("email"));
					emp.setRetirementYn(rs.getString("retirement_yn"));
					
					empList.add(emp);
				}
				return empList;
			} finally {
				jdbc.JdbcUtil.close(rs);
				jdbc.JdbcUtil.close(pstmt);
			}
		}
		
		// 특정 사번을 받아 DB에서 삭제하는 메서드
		public int deleteEmployeeMnt(Connection conn, int employeeId) throws SQLException {
			String sql = "DELETE FROM employee WHERE employee_id = ?";
			
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setInt(1, employeeId);
				return pstmt.executeUpdate(); // 성공하면 1 반환, 실패하면 0 반환
			}
		}
}