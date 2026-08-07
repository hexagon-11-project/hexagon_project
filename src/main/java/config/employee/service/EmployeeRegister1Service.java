package config.employee.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import config.employee.command.RowFormUtil;
import config.employee.dao.EmployeeCareerDao;
import config.employee.dao.EmployeeDependentDao;
import config.employee.dao.EmployeeEducationDao;
import config.employee.dao.EmployeeInsuranceDao;
import config.employee.dao.EmployeeMilitaryDao;
import config.employee.dao.EmployeeDao;
import config.employee.model.EmployeeCareer;
import config.employee.model.EmployeeDependent;
import config.employee.model.EmployeeEducation;
import config.employee.model.EmployeeInsurance;
import config.employee.model.EmployeeMilitary;
import config.employee.model.Employee;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class EmployeeRegister1Service {

    private static final String EMP_NO_PREFIX = "No-";
    private static final int EMP_NO_START = 260001; // 최초 사원번호

    private EmployeeDao employeeDao = new EmployeeDao();
    private EmployeeDependentDao dependentDao = new EmployeeDependentDao();
    private EmployeeEducationDao educationDao = new EmployeeEducationDao();
    private EmployeeCareerDao careerDao = new EmployeeCareerDao();
    private EmployeeInsuranceDao insuranceDao = new EmployeeInsuranceDao();
    private EmployeeMilitaryDao militaryDao = new EmployeeMilitaryDao();

    // 화면에 보여줄 "다음 사원번호"를 미리 계산한다 (등록 폼 진입 시 GET에서 호출)
    public String generateNextEmpNo() {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return calcNextEmpNo(employeeDao.selectMaxEmpNo(conn));
        } catch (SQLException e) {
            throw new RuntimeException("사원번호 채번 실패", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    /**
     * 사원 기본정보 + 부양가족/학력/경력을 한 트랜잭션으로 저장한다.
     * (하나라도 실패하면 사원 자체도 저장되지 않도록 묶어서 처리)
     */
    public void registerEmployee(Employee emp, HttpServletRequest request) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 폼에서 넘어온 empNo는 화면 표시용일 뿐, 실제로 저장할 번호는
            // 저장 시점에 DB를 다시 조회해서 여기서 최종 확정한다.
            String nextEmpNo = calcNextEmpNo(employeeDao.selectMaxEmpNo(conn));
            emp.setEmployeeNo(nextEmpNo);

            employeeDao.insert(conn, emp); // 이 안에서 emp.setEmployeeId(...)까지 채워짐
            int employeeId = emp.getEmployeeId();

            saveDependents(conn, employeeId, request);
            saveEducations(conn, employeeId, request);
            saveCareers(conn, employeeId, request);
            saveInsurances(conn, employeeId, request);
            saveMilitary(conn, employeeId, request);

            conn.commit(); // 성공 시 한번에 커밋
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException("사원 등록 실패", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    private void saveDependents(Connection conn, int employeeId, HttpServletRequest request) throws SQLException {
        int count = RowFormUtil.parseIntOrDefault(request.getParameter("familyRowCount"), 0);
        for (int i = 1; i <= count; i++) {
            String name = request.getParameter("familyName" + i);
            if (isBlank(name)) continue; // 이름이 없는 빈 줄은 저장하지 않는다

            EmployeeDependent v = new EmployeeDependent();
            v.setDependentName(name);
            v.setRelationCode(request.getParameter("familyRelation" + i));
            v.setBirthDate(toSqlDate(request.getParameter("familyBirthDate" + i)));
            v.setDomForYn(request.getParameter("familyDomForYn" + i));
            v.setDisabledYn(checkboxToYn(request.getParameter("familyDisabled" + i)));
            v.setPersonalDeductionYn(checkboxToYn(request.getParameter("familyDeduction" + i)));
            v.setHealthInsuranceYn(checkboxToYn(request.getParameter("familyHealthIns" + i)));
            v.setCohabitationYn(checkboxToYn(request.getParameter("familyCohab" + i)));
            v.setWageIncomeTaxYn("N"); // 화면에 해당 입력칸이 아직 없어 기본값
            v.setChildUnder20Yn(checkboxToYn(request.getParameter("familyMultiChild" + i)));
            dependentDao.insert(conn, employeeId, v);
        }
    }

    private void saveEducations(Connection conn, int employeeId, HttpServletRequest request) throws SQLException {
        int count = RowFormUtil.parseIntOrDefault(request.getParameter("educationRowCount"), 0);
        for (int i = 1; i <= count; i++) {
            String school = request.getParameter("educationSchool" + i);
            if (isBlank(school)) continue;

            EmployeeEducation v = new EmployeeEducation();
            v.setSchoolName(school);
            v.setMajorName(request.getParameter("educationMajor" + i));
            v.setStartDate(toSqlDate(request.getParameter("educationStart" + i)));
            v.setEndDate(toSqlDate(request.getParameter("educationEnd" + i)));
            v.setGraduationStatus(request.getParameter("educationStatus" + i));
            educationDao.insert(conn, employeeId, v);
        }
    }

    // 4대보험(국민연금/건강보험/고용보험/산재보험)은 표 형태가 아니라 4줄 고정이라
    // familyRowCount 같은 반복 처리 대신 종류별로 하나씩 확인해서 저장한다.
    private void saveInsurances(Connection conn, int employeeId, HttpServletRequest request) throws SQLException {
        saveOneInsurance(conn, employeeId, request, "국민연금", "insuranceNoNP", "acquisitionDateNP", "lossDateNP");
        saveOneInsurance(conn, employeeId, request, "건강보험", "insuranceNoHI", "acquisitionDateHI", "lossDateHI");
        saveOneInsurance(conn, employeeId, request, "고용보험", "insuranceNoEI", "acquisitionDateEI", "lossDateEI");
        saveOneInsurance(conn, employeeId, request, "산재보험", "insuranceNoII", "acquisitionDateII", "lossDateII");
    }

    private void saveOneInsurance(Connection conn, int employeeId, HttpServletRequest request, String typeCode,
            String noParam, String acqParam, String lossParam) throws SQLException {
        String no = request.getParameter(noParam);
        String acq = request.getParameter(acqParam);
        String loss = request.getParameter(lossParam);
        // 기호번호/취득일/상실일 셋 다 비어있으면(=아예 입력 안 한 줄) 저장하지 않는다
        if (isBlank(no) && isBlank(acq) && isBlank(loss)) {
            return;
        }
        EmployeeInsurance v = new EmployeeInsurance();
        v.setInsuranceTypeCode(typeCode);
        v.setInsuranceNo(no);
        v.setAcquisitionDate(toSqlDate(acq));
        v.setLossDate(toSqlDate(loss));
        insuranceDao.insert(conn, employeeId, v);
    }

    // 병역 - 아무것도 입력 안 했으면 저장하지 않는다
    private void saveMilitary(Connection conn, int employeeId, HttpServletRequest request) throws SQLException {
        String status = request.getParameter("militaryStatus");
        String branchCode = request.getParameter("militaryBranchCode"); // 군별
        String start = request.getParameter("militaryStartDate");
        String end = request.getParameter("militaryEndDate");
        String grade = request.getParameter("militaryGrade");           // 계급
        String branch = request.getParameter("militaryBranch");         // 병과
        String specialty = request.getParameter("militarySpecialty");   // 특기
        String exemptReason = request.getParameter("militaryExemptReason");

        if (isBlank(status) && isBlank(branchCode) && isBlank(start) && isBlank(end)
                && isBlank(grade) && isBlank(branch) && isBlank(specialty) && isBlank(exemptReason)) {
            return;
        }

        EmployeeMilitary v = new EmployeeMilitary();
        v.setMilitaryStatusCode(status);
        v.setMilitaryBranchCode(branchCode);
        v.setServiceStartDate(toSqlDate(start));
        v.setServiceEndDate(toSqlDate(end));
        v.setMilitaryGrade(grade);
        v.setMilitaryBranch(branch);
        v.setMilitarySpecialty(specialty);
        v.setMilitaryExemptReason(exemptReason);
        militaryDao.save(conn, employeeId, v);
    }

    private void saveCareers(Connection conn, int employeeId, HttpServletRequest request) throws SQLException {
        int count = RowFormUtil.parseIntOrDefault(request.getParameter("careerRowCount"), 0);
        for (int i = 1; i <= count; i++) {
            String company = request.getParameter("careerCompany" + i);
            if (isBlank(company)) continue;

            EmployeeCareer v = new EmployeeCareer();
            v.setCompanyName(company);
            v.setDepartment(request.getParameter("careerDept" + i));
            v.setPosition(request.getParameter("careerPosition" + i));
            java.sql.Date start = toSqlDate(request.getParameter("careerStart" + i));
            java.sql.Date end = toSqlDate(request.getParameter("careerEnd" + i));
            v.setStartDate(start);
            v.setEndDate(end);
            int[] duty = calcDutyYyMm(start, end);
            v.setDutyYy(duty[0]);
            v.setDutyMm(duty[1]);
            v.setCareerDescription(request.getParameter("careerDuty" + i));
            careerDao.insert(conn, employeeId, v);
        }
    }

    // 근무기간(년/월)을 입사일~퇴사일로 대략 계산 (퇴사일 없으면 0/0)
    private int[] calcDutyYyMm(java.sql.Date start, java.sql.Date end) {
        if (start == null || end == null) {
            return new int[] { 0, 0 };
        }
        java.time.LocalDate s = start.toLocalDate();
        java.time.LocalDate e = end.toLocalDate();
        java.time.Period p = java.time.Period.between(s, e);
        return new int[] { p.getYears(), p.getMonths() };
    }

    private java.sql.Date toSqlDate(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return java.sql.Date.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String checkboxToYn(String value) {
        return "on".equals(value) ? "Y" : "N";
    }

    private String nvl(String value, String defaultValue) {
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    // "No-260001" -> "No-260002" 처럼 다음 번호를 계산. 기존 값이 없으면 최초값부터 시작.
    private String calcNextEmpNo(String maxEmpNo) {
        if (maxEmpNo == null || maxEmpNo.isEmpty()) {
            return EMP_NO_PREFIX + EMP_NO_START;
        }

        try {
            int num = Integer.parseInt(maxEmpNo.substring(EMP_NO_PREFIX.length()));
            return EMP_NO_PREFIX + (num + 1);
        } catch (NumberFormatException e) {
            return EMP_NO_PREFIX + EMP_NO_START;
        }
    }
}
