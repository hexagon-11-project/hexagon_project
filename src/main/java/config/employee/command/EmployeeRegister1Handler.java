package config.employee.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandHandler;
import config.employee.model.Employee;
import config.employee.service.EmployeeRegister1Service;

// 사원등록 1페이지 - GET: 폼 표시, POST: 저장 처리
// / 社員登録1ページ - GET: フォーム表示、POST: 保存処理
public class EmployeeRegister1Handler implements CommandHandler {

    // JSP 뷰 경로 / JSPビューのパス
    private static final String FORM_VIEW = "/WEB-INF/pages/config/employee-register1.jsp";

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

    // GET - 폼 초기 표시, employeeId 있으면 기존 사원 데이터 로드
    // / GET - フォーム初期表示、employeeIdがあれば既存社員データをロード
    private String processForm(HttpServletRequest request, HttpServletResponse response) {
        String employeeIdParam = request.getParameter("employeeId");

        if (employeeIdParam != null && !employeeIdParam.isBlank()) {
            // 사원현황에서 이름 클릭 - 기존 사원 데이터를 폼에 채워서 표시
            // / 社員一覧から名前クリック - 既存社員データをフォームに表示
            int employeeId = Integer.parseInt(employeeIdParam);
            Employee emp = registerService.getEmployeeById(employeeId);
            if (emp != null) {
                request.setAttribute("loadEmployee", emp);
                request.setAttribute("defaultEmpNo", emp.getEmployeeNo());
                registerService.loadAllSubTableData(employeeId, request);
            } else {
                String nextEmpNo = registerService.generateNextEmpNo();
                request.setAttribute("defaultEmpNo", nextEmpNo);
            }
        } else {
            // 신규 등록 - 새 사원번호 채번
            // / 新規登録 - 社員番号を新規採番
            String nextEmpNo = registerService.generateNextEmpNo();
            request.setAttribute("defaultEmpNo", nextEmpNo);
        }

        return FORM_VIEW;
    }

    // POST - 저장하기 또는 행 추가/삭제 버튼 처리
    // / POST - 保存ボタンまたは行追加/削除ボタンの処理
    private String processSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        String formAction = request.getParameter("formAction");

        // 부양가족/학력/경력 [추가][선택삭제] 버튼 - 저장 안 하고 행 수만 조정해서 폼 다시 표시
        // / 扶養家族/学歴/経歴の[追加][選択削除]ボタン - 保存せず行数のみ調整してフォーム再表示
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
                    // [저장하기]는 formAction 없음 - 아래로 진행
                    // / [保存]はformActionなし - 下に進む
                    break;
            }
        }

        Employee emp = new Employee();

        // 기본 텍스트 필드 매핑 / 基本テキストフィールドのマッピング
        emp.setEmploymentType(request.getParameter("employmentType"));
        emp.setEmployeeName(request.getParameter("employeeName"));
        emp.setEmployeeNameEn(request.getParameter("employeeNameEn"));
        emp.setDepartment(request.getParameter("department"));
        emp.setPosition(request.getParameter("position"));
        emp.setDomForYn(request.getParameter("domForYn")); // 내/외국인 여부 / 日本国籍/外国籍
        emp.setEmail(request.getParameter("email"));
        emp.setSns(request.getParameter("sns"));
        emp.setBankName(request.getParameter("bankName"));
        emp.setBankAccount(request.getParameter("bankAccount"));
        emp.setEmpIncomeType(request.getParameter("empIncomeType"));
        emp.setBaseWageAmount(RowFormUtil.parseIntOrDefault(request.getParameter("baseWageAmount"), 0));
        emp.setNationalPensionBaseAmount(RowFormUtil.parseIntOrDefault(request.getParameter("nationalPensionBaseAmount"), 0));
        emp.setHealthInsuranceBaseAmount(RowFormUtil.parseIntOrDefault(request.getParameter("healthInsuranceBaseAmount"), 0));
        emp.setEmploymentInsuranceAmount(RowFormUtil.parseIntOrDefault(request.getParameter("employmentInsuranceAmount"), 0));
        emp.setPhotoPath(request.getParameter("photoPath"));

        // 날짜 변환 - 빈 문자열이면 skip / 日付変換 - 空文字はスキップ
        String hireDateStr = request.getParameter("hireDate");
        if (hireDateStr != null && !hireDateStr.isEmpty()) {
            emp.setHireDate(java.sql.Date.valueOf(hireDateStr));
        }

        String resignDateStr = request.getParameter("resignDate");
        if (resignDateStr != null && !resignDateStr.isEmpty()) {
            emp.setResignDate(java.sql.Date.valueOf(resignDateStr));
        }

        // 주민번호 앞뒤 합치기 / マイナンバー前後を結合
        String rrnFront = request.getParameter("residentRegNoFront");
        String rrnBack = request.getParameter("residentRegNoBack");
        if (rrnFront != null && !rrnFront.isEmpty() && rrnBack != null) {
            emp.setResidentRegNo(rrnFront + "-" + rrnBack);
        }

        // 전화번호 합치기 / 電話番号を結合
        String phone1 = request.getParameter("phone1");
        String phone2 = request.getParameter("phone2");
        String phone3 = request.getParameter("phone3");
        if (phone1 != null && !phone1.isEmpty() && phone2 != null && phone3 != null) {
            emp.setPhone(phone1 + "-" + phone2 + "-" + phone3);
        }

        // 휴대폰 합치기 / 携帯電話番号を結合
        String mobile1 = request.getParameter("mobile1");
        String mobile2 = request.getParameter("mobile2");
        String mobile3 = request.getParameter("mobile3");
        if (mobile1 != null && !mobile1.isEmpty() && mobile2 != null && mobile3 != null) {
            emp.setMobile(mobile1 + "-" + mobile2 + "-" + mobile3);
        }

        // 기존 사원이면 UPDATE, 신규면 INSERT / 既存社員はUPDATE、新規はINSERT
        String existingEmployeeIdParam = request.getParameter("existingEmployeeId");
        if (existingEmployeeIdParam != null && !existingEmployeeIdParam.isBlank()) {
            emp.setEmployeeId(Integer.parseInt(existingEmployeeIdParam));
            String existingEmployeeNo = request.getParameter("existingEmployeeNo");
            emp.setEmployeeNo((existingEmployeeNo != null && !existingEmployeeNo.isBlank())
                ? existingEmployeeNo
                : request.getParameter("employeeNo"));
            registerService.updateEmployee(emp, request);
        } else {
            registerService.registerEmployee(emp, request);
        }

        // 버튼에 따라 이동 위치 다름 / ボタンによって遷移先が異なる
        if ("saveAndStay".equals(formAction)) {
            // [신규사원등록] - 저장 후 1페이지 빈 폼으로 / [新規社員登録] - 保存後に1ページ初期フォームへ
            response.sendRedirect(request.getContextPath() + "/Config/employeeIns1.do");
        } else {
            // [저장하기] - 저장 후 2페이지로 / [保存] - 保存後に2ページへ
            String encodedEmpNo = java.net.URLEncoder.encode(emp.getEmployeeNo(), "UTF-8");
            response.sendRedirect(request.getContextPath() + "/Config/employeeIns2.do?employeeNo=" + encodedEmpNo);
        }
        return null;
    }
}
