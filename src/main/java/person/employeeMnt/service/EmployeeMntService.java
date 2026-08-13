package person.employeeMnt.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import config.employee.model.Employee;
import connection.ConnectionProvider; 
import person.employeeMnt.dao.EmployeeMntDao; 

public class EmployeeMntService {

    // DAO 객체 생성
    private EmployeeMntDao employeeDao = new EmployeeMntDao();

    // 핸들러에서 호출할 리스트 조회 및 생년월일 가공 메서드
    public List<Employee> getEmployeeList() {
        
        try (Connection conn = ConnectionProvider.getConnection()) {
            
            // 1. DAO를 통해 사원 전체 리스트 순수 데이터 가져오기
            List<Employee> list = employeeDao.selectList(conn);
            
            
            // 2. 반복문을 돌면서 주민등록번호를 생년월일로 예쁘게 가공 (비즈니스 로직)
            for (Employee emp : list) {
                if (emp.getResidentRegNo() != null) {
                    String rrn = emp.getResidentRegNo().replace("-", "");
                    
                    if (rrn.length() >= 7) {
                        String yy = rrn.substring(0, 2);
                        String mm = rrn.substring(2, 4);
                        String dd = rrn.substring(4, 6);
                        char century = rrn.charAt(6);
                        
                        // 2000년대생 판별
                        String yearPrefix = (century == '3' || century == '4' || century == '7' || century == '8') ? "20" : "19";
                        
                        // 완성된 생년월일을 모델에 세팅
                        emp.setBirthDate(yearPrefix + yy + "-" + mm + "-" + dd);
                    }
                }
            }
            return list;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("DB 조회 에러", e);
        }
    }

    // 상단 카운트 정보를 DAO에서 한 번에 가져오는 메서드
    public Map<String, Integer> getEmployeeCounts() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            // DAO에 만들어둔 한글 기준 통합 카운트 메서드 호출
            return employeeDao.getAllCounts(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("카운트 조회 에러", e);
        }
    }
    private int size = 30; //  한 페이지에 보여줄 개수 (30개 고정)

	public EmployeePage getEmployeePage(int pageNum) {
		try (Connection conn = connection.ConnectionProvider.getConnection()) {
			
			// 1. 전체 사원 수 구하기 (기존 통합 카운트 메서드의 total 값 활용 또는 별도 count 쿼리)
			int total = employeeDao.getAllCounts(conn).get("total"); 
			
			List<Employee> content = null;
			if (total > 0) {
				// 오라클 ROWNUM에 맞게 범위 계산
				int firstRow = (pageNum - 1) * size + 1;
				int endRow = firstRow + size - 1;
				
				// 2. 30개만 잘라오기
				content = employeeDao.selectListByPaging(conn, firstRow, endRow);
				
				// 3. 주민번호 -> 생년월일 가공 로직 적용
				for (Employee emp : content) {
					if (emp.getResidentRegNo() != null) {
						String rrn = emp.getResidentRegNo().replace("-", "");
						if (rrn.length() >= 7) {
							String yy = rrn.substring(0, 2);
							String mm = rrn.substring(2, 4);
							String dd = rrn.substring(4, 6);
							char century = rrn.charAt(6);
							String yearPrefix = (century == '3' || century == '4' || century == '7' || century == '8') ? "20" : "19";
							emp.setBirthDate(yearPrefix + yy + "-" + mm + "-" + dd);
						}
					}
				}
			}
			// 4. 페이지 계산 상자에 담아서 리턴
			return new EmployeePage(total, pageNum, size, content);
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("페이징 조회 에러", e);
		}
	}
}