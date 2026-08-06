package command;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import connection.ConnectionProvider; // DB 통신을 위한 ConnectionProvider 임포트
import payroll.dao.PayrollDAO;
import payroll.dto.PayrollEmployeeDTO; 

public class EmployeeAddModalHandler implements CommandHandler {

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // 1. 돋보기 검색을 눌렀을 때 넘어오는 검색어 파라미터 받기
        String keyword = request.getParameter("empName");
        
        // 2. DB 연결 및 DAO 호출
        try (Connection conn = ConnectionProvider.getConnection()) {
            PayrollDAO dao = new PayrollDAO();
            
            // ★ 에러 났던 부분 수정! 방금 새로 만든 메서드를 호출합니다.
            List<PayrollEmployeeDTO> employeeList = dao.getModalEmployeeList(conn, keyword); 
            
            // 3. JSP에서 쓸 수 있게 바구니(request)에 담기 (이름은 JSP와 동일하게!)
            request.setAttribute("availableEmployeeList", employeeList);
        }
        
        // 4. 팝업창 JSP 띄우기
        return "/WEB-INF/pages/payroll/employee_add_modal.jsp";
    }
}