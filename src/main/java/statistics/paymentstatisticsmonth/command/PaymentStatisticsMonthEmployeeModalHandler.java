package statistics.paymentstatisticsmonth.command;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import connection.ConnectionProvider;
import payment.paymentMnt.dao.PaymentMntDAO;
import payment.paymentMnt.dto.PaymentMntEmployeeDTO;

// 월별 개인급여 통계 화면의 "대상자 선택" 팝업 - 급여지급 사원선택(paymentMnt) 모달과 동일한 사원 검색/페이징 로직을 재사용한다.
public class PaymentStatisticsMonthEmployeeModalHandler implements CommandHandler {

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String keyword = request.getParameter("empName");
        String department = request.getParameter("department");
        String status = request.getParameter("status"); // 재직 or 퇴직

        String pageParam = request.getParameter("page");
        int currentPage = (pageParam != null && !pageParam.isEmpty()) ? Integer.parseInt(pageParam) : 1;

        int limit = 10;
        int offset = (currentPage - 1) * limit;

        try (Connection conn = ConnectionProvider.getConnection()) {
            PaymentMntDAO dao = new PaymentMntDAO();

            List<PaymentMntEmployeeDTO> employeeList = dao.getModalEmployeeList(conn, keyword, limit, offset, department, null, status);
            int totalCount = dao.getModalEmployeeCount(conn, keyword, department, null, status);
            int totalPage = (int) Math.ceil((double) totalCount / limit);
            if (totalPage == 0) { totalPage = 1; }

            List<String> deptList = dao.getDepartmentList(conn);

            request.setAttribute("availableEmployeeList", employeeList);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPage", totalPage);

            request.setAttribute("empName", keyword);
            request.setAttribute("selectedDept", department);
            request.setAttribute("selectedStatus", status);

            request.setAttribute("deptList", deptList);
        }

        return "/WEB-INF/pages/statistics/paymentStatisticsMonth_employee_select_modal.jsp";
    }
}
