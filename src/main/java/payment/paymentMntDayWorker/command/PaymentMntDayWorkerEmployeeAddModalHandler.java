package payment.paymentMntDayWorker.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import payment.paymentMntDayWorker.dto.PaymentMntDayWorkerEmployeeDTO;
import payment.paymentMntDayWorker.service.PaymentMntDayWorkerService;

// [신규추가] 버튼 클릭 시 뜨는 일용직 근로자 검색 모달
public class PaymentMntDayWorkerEmployeeAddModalHandler implements CommandHandler {

    private PaymentMntDayWorkerService service = new PaymentMntDayWorkerService();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        String keyword = request.getParameter("empName");

        List<PaymentMntDayWorkerEmployeeDTO> availableEmployeeList = service.getModalEmployeeList(keyword);

        request.setAttribute("availableEmployeeList", availableEmployeeList);
        request.setAttribute("empName", keyword);

        return "/WEB-INF/pages/payment/paymentMntDayWorker_employee_add_modal.jsp";
    }
}
