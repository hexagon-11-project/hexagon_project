package config.employee.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.employee.model.Employee;
import config.employee.service.EmployeeRegister1Service;

public class EmployeeRegister1Handler implements CommandHandler {

    // JSP 뷰 파일 경로 설정 (WEB-INF/view 디렉터리 기준)
    private static final String FORM_VIEW = "/WEB-INF/pages/environment/employee-register1.jsp";

    private EmployeeRegister1Service registerService = new EmployeeRegister1Service();

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

    // 1. GET 요청: 사원 등록 폼 화면을 보여줄 때 처리
    private String processForm(HttpServletRequest request, HttpServletResponse response) {
        String nextEmpNo = registerService.generateNextEmpNo();
        request.setAttribute("defaultEmpNo", nextEmpNo);
        return FORM_VIEW;
    }

    // 2. POST 요청: 사용자가 [저장하기] 등을 눌러 데이터를 서버로 전송했을 때 처리
    private String processSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 한글 인코딩 설정
        request.setCharacterEncoding("UTF-8");

        String formAction = request.getParameter("formAction");

        // ===== 부양가족 / 학력 / 경력 표의 [추가] [선택삭제] 처리 =====
        // "저장하기"가 아니라 이 버튼들 중 하나가 눌린 거라면, 실제 사원 저장은 하지 않고
        // 표 내용만 갱신해서 같은 폼을 다시 보여준다.
        if (formAction != null) {
            switch (formAction) {
                case "addFamilyRow":
                    RowFormUtil.addRow(request, "familyRowCount", 2);
                    request.setAttribute("defaultEmpNo", request.getParameter("employeeNo"));
                    return FORM_VIEW;
                case "deleteFamilyRows":
                    request.setAttribute("defaultEmpNo", request.getParameter("employeeNo"));
                    RowFormUtil.forwardWithDeletedRows(request, response, FORM_VIEW,
                            "family", "familyRowCount", "familyDel", 2);
                    return null;
                case "addEducationRow":
                    RowFormUtil.addRow(request, "educationRowCount", 1);
                    request.setAttribute("defaultEmpNo", request.getParameter("employeeNo"));
                    return FORM_VIEW;
                case "deleteEducationRows":
                    request.setAttribute("defaultEmpNo", request.getParameter("employeeNo"));
                    RowFormUtil.forwardWithDeletedRows(request, response, FORM_VIEW,
                            "education", "educationRowCount", "educationDel", 1);
                    return null;
                case "addCareerRow":
                    RowFormUtil.addRow(request, "careerRowCount", 1);
                    request.setAttribute("defaultEmpNo", request.getParameter("employeeNo"));
                    return FORM_VIEW;
                case "deleteCareerRows":
                    request.setAttribute("defaultEmpNo", request.getParameter("employeeNo"));
                    RowFormUtil.forwardWithDeletedRows(request, response, FORM_VIEW,
                            "career", "careerRowCount", "careerDel", 1);
                    return null;
                default:
                    // "저장하기"는 formAction 파라미터가 없으므로 여기로 안 들어옴 - 아래로 계속 진행
                    break;
            }
        }

        Employee emp = new Employee();

        // 1. 기본 텍스트 정보 매핑 (JSP의 name 속성 -> VO의 Setter)
        emp.setEmploymentType(request.getParameter("employmentType"));
        emp.setEmployeeName(request.getParameter("employeeName"));
        emp.setEmployeeNameEn(request.getParameter("employeeNameEn"));
        emp.setDepartment(request.getParameter("department"));
        emp.setPosition(request.getParameter("position"));
        emp.setDomForYn(request.getParameter("domForYn")); // 내/외국인 여부 (Y/N)
        emp.setEmail(request.getParameter("email"));
        emp.setSns(request.getParameter("sns"));
        emp.setBankName(request.getParameter("bankName"));
        emp.setBankAccount(request.getParameter("bankAccount"));
        emp.setEmpIncomeType(request.getParameter("empIncomeType"));
        emp.setBaseWageAmount(RowFormUtil.parseIntOrDefault(request.getParameter("baseWageAmount"), 0));
        emp.setNationalPensionBaseAmount(RowFormUtil.parseIntOrDefault(request.getParameter("nationalPensionBaseAmount"), 0));
        emp.setHealthInsuranceBaseAmount(RowFormUtil.parseIntOrDefault(request.getParameter("healthInsuranceBaseAmount"), 0));
        emp.setEmploymentInsuranceAmount(RowFormUtil.parseIntOrDefault(request.getParameter("employmentInsuranceAmount"), 0));

        // 2. 날짜 데이터 변환 (빈 문자열이 넘어오면 에러가 나므로 예외 방지 처리)
        String hireDateStr = request.getParameter("hireDate");
        if (hireDateStr != null && !hireDateStr.isEmpty()) {
            emp.setHireDate(java.sql.Date.valueOf(hireDateStr));
        }

        String resignDateStr = request.getParameter("resignDate");
        if (resignDateStr != null && !resignDateStr.isEmpty()) {
            emp.setResignDate(java.sql.Date.valueOf(resignDateStr));
        }

        // 3. 쪼개진 데이터 합치기 (주민번호)
        String rrnFront = request.getParameter("residentRegNoFront");
        String rrnBack = request.getParameter("residentRegNoBack");
        if (rrnFront != null && !rrnFront.isEmpty() && rrnBack != null) {
            emp.setResidentRegNo(rrnFront + "-" + rrnBack);
        }

        // 4. 쪼개진 데이터 합치기 (일반 전화번호)
        String phone1 = request.getParameter("phone1");
        String phone2 = request.getParameter("phone2");
        String phone3 = request.getParameter("phone3");
        if (phone1 != null && !phone1.isEmpty() && phone2 != null && phone3 != null) {
            emp.setPhone(phone1 + "-" + phone2 + "-" + phone3);
        }

        // 5. 쪼개진 데이터 합치기 (휴대폰 번호)
        String mobile1 = request.getParameter("mobile1");
        String mobile2 = request.getParameter("mobile2");
        String mobile3 = request.getParameter("mobile3");
        if (mobile1 != null && !mobile1.isEmpty() && mobile2 != null && mobile3 != null) {
            emp.setMobile(mobile1 + "-" + mobile2 + "-" + mobile3);
        }

        // 6. 서비스 호출하여 DB 저장 (사원 기본정보 + 부양가족/학력/경력 한 번에)
        registerService.registerEmployee(emp, request);

        // 7. 어느 버튼으로 눌렀는지에 따라 이동할 곳이 다름
        if ("saveAndStay".equals(formAction)) {
            // [신규사원등록] 버튼: DB엔 저장하되, 2페이지로 넘어가지 않고 1페이지에 그대로 머무른다.
            // (다음 사람을 새로 등록할 수 있게 사원번호만 새로 채번해서 빈 폼으로 되돌아감)
            response.sendRedirect(request.getContextPath() + "/Config/employeeIns1.do");
        } else {
            // [저장하기] 버튼: 2페이지로 이어서 넘어감
            String encodedEmpNo = java.net.URLEncoder.encode(emp.getEmployeeNo(), "UTF-8");
            response.sendRedirect(request.getContextPath() + "/Config/employeeIns2.do?employeeNo=" + encodedEmpNo);
        }
        return null;
    }
}
