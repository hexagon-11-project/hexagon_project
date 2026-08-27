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
										 // // ? に入るパラメータをセット
		
			rs = pstmt.executeQuery();

			Employee emp = null;
			if (rs.next()) {
				emp = new Employee();
				
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
	// 社員の全リストを照会するメソッド
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

	// 고용형태 및 재직상태 카운트를 한 번에 가져오는 통합 쿼리 메서드
	// 雇用形態および在職ステータスのカウントを一度に取得する統合クエリメソッド
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
	// 指定した範囲だけ社員リストを切り取って照会するページングクエリ
	public List<Employee> selectListByPaging(Connection conn, int firstRow, int endRow, String searchType, String keyword) throws SQLException {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String where = searchCondition(searchType, keyword);
			String sql = "SELECT * FROM (" +
					"    SELECT ROWNUM rnum, emp.* FROM (" +
					"        SELECT * FROM employee " + where + " ORDER BY employee_id DESC" +
					"    ) emp WHERE ROWNUM <= ?" +
					") WHERE rnum >= ?";
			
			try {
				pstmt = conn.prepareStatement(sql);
	            int paramIndex = 1;
	            
	            // 검색어가 있을 경우 ? 위치에 값 바인딩
	            // 検索キーワードがある場合、? の位置に値をバインディング
	            if (!where.isEmpty()) {
	                pstmt.setString(paramIndex++, "%" + keyword.trim() + "%");
	            }
	            
	            pstmt.setInt(paramIndex++, endRow);
	            pstmt.setInt(paramIndex++, firstRow);
	            
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
		// 特定の社員番号を受け取り、DBから削除するメソッド
		public int deleteEmployeeMnt(Connection conn, int employeeId) throws SQLException {
			String sql = "DELETE FROM employee WHERE employee_id = ?";
			
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setInt(1, employeeId);
				return pstmt.executeUpdate(); 
			}
		}
		// 공통 검색 조건 메소드
		// 共通の検索条件メソッド
		private String searchCondition(String searchType, String keyword) {
	        if (keyword == null || keyword.trim().isEmpty()) {
	            return "";
	        }
	        if ("name".equals(searchType)) {
	            return " WHERE employee_name LIKE ? ";
	        } else if ("empNo".equals(searchType)) {
	            return " WHERE employee_no LIKE ? "; 
	        } else if ("dept".equals(searchType)) {
	            return " WHERE department LIKE ? ";
	        }
	        return "";
	    }
		// 검색된 데이터의 총 개수 카운트 메서드 
		// 検索されたデータの総件数をカウントするメソッド
		public int getSearchCount(Connection conn, String searchType, String keyword) throws SQLException {
	        String where = searchCondition(searchType, keyword); 
	        String sql = "SELECT COUNT(*) FROM employee" + where;
	        
	        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            if (!where.isEmpty()) {
	                pstmt.setString(1, "%" + keyword.trim() + "%");
	            }
	            try (ResultSet rs = pstmt.executeQuery()) {
	                if (rs.next()) {
	                    return rs.getInt(1);
	                }
	            }
	        }
	        return 0;
	    }
}