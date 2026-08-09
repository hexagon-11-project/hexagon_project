package payment.paymentMnt.command;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import connection.ConnectionProvider;
import payment.paymentMnt.dao.PaymentMntDAO;
import payment.paymentMnt.dto.PaymentMntEmployeeDTO;

public class EmployeeAddModalHandler implements CommandHandler {

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // 1. 검색어 및 필터 파라미터 받기
        String keyword = request.getParameter("empName");
        String department = request.getParameter("department");
        String position = request.getParameter("position");
        String status = request.getParameter("status"); // 재직 or 퇴직
        
        // 2. 현재 페이지 번호 받기
        String pageParam = request.getParameter("page");
        int currentPage = (pageParam != null && !pageParam.isEmpty()) ? Integer.parseInt(pageParam) : 1;
        
        // 3. 10명씩 자르기 위한 계산
        int limit = 10;
        int offset = (currentPage - 1) * limit;

        try (Connection conn = ConnectionProvider.getConnection()) {
            PaymentMntDAO dao = new PaymentMntDAO();
            
            // 4. 필터(부서, 직위, 상태)가 적용된 페이징 사원 목록 조회
            List<PaymentMntEmployeeDTO> employeeList = dao.getModalEmployeeList(conn, keyword, limit, offset, department, position, status);
            
            // 5. 필터가 적용된 전체 사원 수 계산
            int totalCount = dao.getModalEmployeeCount(conn, keyword, department, position, status);
            int totalPage = (int) Math.ceil((double) totalCount / limit);
            if (totalPage == 0) totalPage = 1;
            
            // 6. DB에서 부서, 직위 목록 가져오기
            List<String> deptList = dao.getDepartmentList(conn);
            List<String> posList = dao.getPositionList(conn);
            
            // 7. JSP로 데이터 세팅
            request.setAttribute("availableEmployeeList", employeeList);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPage", totalPage);
            
            // 8. 검색어 및 선택된 필터값 유지 (화면 새로고침 시 초기화 방지)
            request.setAttribute("empName", keyword);
            request.setAttribute("selectedDept", department);
            request.setAttribute("selectedPos", position);
            request.setAttribute("selectedStatus", status);
            
            request.setAttribute("deptList", deptList);
            request.setAttribute("posList", posList);
        }
        
        return "/WEB-INF/pages/payment/paymentMnt_employee_add_modal.jsp";
    }
}