package person.employeeMnt.service;

import java.util.List;
import config.employee.model.Employee;

public class EmployeePage {
	private int total;        // 전체 사원 수
	private int currentPage;  // 현재 페이지
	private List<Employee> content; // 30명치 사원 데이터
	private int totalPages;   // 전체 페이지 수
	private int startPage;    // 하단 시작 번호
	private int endPage;      // 하단 끝 번호

	public EmployeePage(int total, int currentPage, int size, List<Employee> content) {
		this.total = total;
		this.currentPage = currentPage;
		this.content = content;
		
		if (total == 0) {
			totalPages = 0; startPage = 0; endPage = 0;
		} else {
			totalPages = total / size;
			if (total % size > 0) totalPages++;
			
			int modVal = currentPage % 5;
			startPage = currentPage / 5 * 5 + 1; 
			if (modVal == 0) startPage -= 5;

			endPage = startPage + 4; 
			if (endPage > totalPages) endPage = totalPages;
		}
	}


	public int getTotal() { return total; }
	public boolean hasNoEmployees() { return total == 0; }
	public boolean hasEmployees() { return total > 0; }
	public int getCurrentPage() { return currentPage; }
	public int getTotalPages() { return totalPages; }
	public List<Employee> getContent() { return content; }
	public int getStartPage() { return startPage; }
	public int getEndPage() { return endPage; }
}