package config.employee.command;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.employee.dao.EmployeeDao;
import config.employee.model.Employee;
import config.employee.service.EmployeeRegister2Service;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class EmployeeRegister2Handler implements CommandHandler {

    // 사원 등록 2페이지 JSP 파일 경로로 지정
    private static final String FORM_VIEW = "/WEB-INF/pages/environment/employee-register2.jsp";

    private EmployeeRegister2Service registerService = new EmployeeRegister2Service();

    @Override
    public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getMethod().equalsIgnoreCase("GET")) {
            return processForm(request, response);
        } else if (request.getMethod().equalsIgnoreCase("POST")) {
            return processSubmit(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }

    // 1. GET 요청: 사원 등록 2페이지 화면을 보여줄 때 처리
    private String processForm(HttpServletRequest request, HttpServletResponse response) {
        String employeeNo = request.getParameter("employeeNo");
        loadEmpInfo(request, employeeNo);
        return FORM_VIEW;
    }

    // 1페이지에서 방금 등록한 사원번호로 DB에서 정보를 다시 조회해서 request에 실어준다.
    // (자격면허/교육훈련/상벌/발령 표의 [추가][선택삭제] 처리 후 다시 forward할 때도
    //  이걸 안 해주면 화면 위쪽 "기본정보"란이 통째로 비어버린다 - ${empInfo.xxx} 바인딩이라)
    private void loadEmpInfo(HttpServletRequest request, String employeeNo) {
        if (employeeNo == null || employeeNo.isEmpty()) {
            return;
        }
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            EmployeeDao dao = new EmployeeDao();
            Employee empInfo = dao.selectEmployee(conn, employeeNo);
            request.setAttribute("empInfo", empInfo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.close(conn);
        }
    }

    // 2. POST 요청: 2페이지의 자격면허, 교육훈련, 상벌, 발령, 신원보증, 퇴직 등의 데이터를 전송했을 때 처리
    private String processSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 한글 인코딩 설정
        request.setCharacterEncoding("UTF-8");

        String employeeNo = request.getParameter("employeeNo");
        String formAction = request.getParameter("formAction");

        // ===== 자격면허 / 교육훈련 / 상벌 / 발령 표의 [추가] [선택삭제] 처리 =====
        if (formAction != null) {
            switch (formAction) {
                case "addLicenseRow":
                    RowFormUtil.addRow(request, "licenseRowCount", 1);
                    loadEmpInfo(request, employeeNo);
                    return FORM_VIEW;
                case "deleteLicenseRows":
                    loadEmpInfo(request, employeeNo);
                    RowFormUtil.forwardWithDeletedRows(request, response, FORM_VIEW,
                            "license", "licenseRowCount", "licenseDel", 1);
                    return null;
                case "addTrainingRow":
                    RowFormUtil.addRow(request, "trainingRowCount", 1);
                    loadEmpInfo(request, employeeNo);
                    return FORM_VIEW;
                case "deleteTrainingRows":
                    loadEmpInfo(request, employeeNo);
                    RowFormUtil.forwardWithDeletedRows(request, response, FORM_VIEW,
                            "training", "trainingRowCount", "trainingDel", 1);
                    return null;
                case "addRewardRow":
                    RowFormUtil.addRow(request, "rewardRowCount", 1);
                    loadEmpInfo(request, employeeNo);
                    return FORM_VIEW;
                case "deleteRewardRows":
                    loadEmpInfo(request, employeeNo);
                    RowFormUtil.forwardWithDeletedRows(request, response, FORM_VIEW,
                            "reward", "rewardRowCount", "rewardDel", 1);
                    return null;
                case "addAppointmentRow":
                    RowFormUtil.addRow(request, "appointmentRowCount", 1);
                    loadEmpInfo(request, employeeNo);
                    return FORM_VIEW;
                case "deleteAppointmentRows":
                    loadEmpInfo(request, employeeNo);
                    RowFormUtil.forwardWithDeletedRows(request, response, FORM_VIEW,
                            "appointment", "appointmentRowCount", "apptDel", 1);
                    return null;
                default:
                    break;
            }
        }

        // ===== 실제 저장 (자격면허 / 교육훈련 / 상벌 / 발령) =====
        Employee empInfo = fetchEmpInfo(employeeNo);
        if (empInfo == null) {
            // 사원번호가 없거나 잘못된 경우 - 1페이지부터 다시 진행하도록 안내
            request.setAttribute("errorMsg", "사원 정보를 찾을 수 없습니다. 1페이지부터 다시 진행해주세요.");
            return "/WEB-INF/pages/environment/employee-register1.jsp";
        }
        registerService.saveAdditionalInfo(empInfo.getEmployeeId(), request);

        // 방금 저장한 내용(특히 퇴직 정보)이 화면에도 바로 반영되도록, 저장 후 최신 상태로 다시 조회한다.
        empInfo = fetchEmpInfo(employeeNo);

        // 목록으로 이동하지 않고 이 페이지에 계속 머무른다.
        // (입력해뒀던 줄 수도 유지 - 안 하면 저장 직후 표가 1줄로 줄어들어 보인다)
        request.setAttribute("empInfo", empInfo);
        request.setAttribute("licenseRowCount", RowFormUtil.parseIntOrDefault(request.getParameter("licenseRowCount"), 1));
        request.setAttribute("trainingRowCount", RowFormUtil.parseIntOrDefault(request.getParameter("trainingRowCount"), 1));
        request.setAttribute("rewardRowCount", RowFormUtil.parseIntOrDefault(request.getParameter("rewardRowCount"), 1));
        request.setAttribute("appointmentRowCount", RowFormUtil.parseIntOrDefault(request.getParameter("appointmentRowCount"), 1));
        request.setAttribute("justSaved", true); // 저장 완료 알림창을 띄우라는 신호
        return FORM_VIEW;
    }

    private Employee fetchEmpInfo(String employeeNo) {
        if (employeeNo == null || employeeNo.isEmpty()) {
            return null;
        }
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return new EmployeeDao().selectEmployee(conn, employeeNo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            JdbcUtil.close(conn);
        }
    }
}
