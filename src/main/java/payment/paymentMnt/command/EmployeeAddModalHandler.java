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

    // 필터(부서/직위/상태) 및 검색어 조건에 맞는 사원 목록을 페이징 조회하여 모달에 표시
    // フィルター（部署/職位/状態）および検索語条件に合う社員一覧をページング照会してモーダルに表示
    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 1. 검색어 및 필터 파라미터 받기 / 1. 検索語およびフィルターパラメータを受け取る
        String keyword = request.getParameter("empName");
        String department = request.getParameter("department");
        String position = request.getParameter("position");
        String status = request.getParameter("status"); // 재직 or 퇴직 / 在職 または 退職

        // 2. 현재 페이지 번호 받기 / 2. 現在のページ番号を受け取る
        String pageParam = request.getParameter("page");
        int currentPage = (pageParam != null && !pageParam.isEmpty()) ? Integer.parseInt(pageParam) : 1;

        // 3. 10명씩 자르기 위한 계산 / 3. 10人ずつ区切るための計算
        int limit = 10;
        int offset = (currentPage - 1) * limit;

        try (Connection conn = ConnectionProvider.getConnection()) {
            PaymentMntDAO dao = new PaymentMntDAO();

            // 4. 필터(부서, 직위, 상태)가 적용된 페이징 사원 목록 조회 / 4. フィルター（部署、職位、状態）が適用されたページング社員一覧を照会
            //    일용직/DAILY는 급여입력관리 대상이 아니므로(별도 일용직 급여 화면에서 관리) 사원선택 목록에서 제외 / 日雇い/DAILYは給与入力管理の対象外のため（別途日雇い給与画面で管理）、社員選択一覧から除外
            List<PaymentMntEmployeeDTO> employeeList = dao.getModalEmployeeList(conn, keyword, limit, offset, department, position, status, true);

            // 5. 필터가 적용된 전체 사원 수 계산 / 5. フィルターが適用された全社員数を計算
            int totalCount = dao.getModalEmployeeCount(conn, keyword, department, position, status, true);
            int totalPage = (int) Math.ceil((double) totalCount / limit);
            if (totalPage == 0) totalPage = 1;

            // 6. DB에서 부서, 직위 목록 가져오기 / 6. DBから部署、職位一覧を取得
            List<String> deptList = dao.getDepartmentList(conn);
            List<String> posList = dao.getPositionList(conn);

            // 7. JSP로 데이터 세팅 / 7. JSPへデータを設定
            request.setAttribute("availableEmployeeList", employeeList);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPage", totalPage);

            // 8. 검색어 및 선택된 필터값 유지 (화면 새로고침 시 초기화 방지) / 8. 検索語および選択したフィルター値を維持（画面再読み込み時の初期化防止）
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