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

// 사원 등록 1페이지의 비즈니스 로직(채번, 등록, 트랜잭션 관리, 수정, 조회 데이터 세팅)을 총괄하는 Service
// 社員登録1ページ目のビジネスロジック（採番、登録、トランザクション管理、修正、照会データのセット）を総括する Service
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
    // 画面に表示する「次の社員番号」をあらかじめ計算する（登録フォーム進入時のGETで呼び出し）
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
     * 社員基本情報 ＋ 扶養家族／学歴／経歴を1つのトランザクションで保存する。
     * （一つでも失敗すれば社員自体も保存されないようにまとめて処理）
     */
    public void registerEmployee(Employee emp, HttpServletRequest request) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작 (トランザクション開始)

            // 폼에서 넘어온 empNo는 화면 표시용일 뿐, 실제로 저장할 번호는
            // 저장 시점에 DB를 다시 조회해서 여기서 최종 확정한다.
            // フォームから渡されたempNoは画面表示用であり、実際に保存する番号は
            // 保存時点でDBを再照会し、ここで最終確定する。
            String nextEmpNo = calcNextEmpNo(employeeDao.selectMaxEmpNo(conn));
            emp.setEmployeeNo(nextEmpNo);

            employeeDao.insert(conn, emp); // 이 안에서 emp.setEmployeeId(...)까지 채워짐 (この中でemp.setEmployeeId(...)まで埋められる)
            int employeeId = emp.getEmployeeId();

            saveDependents(conn, employeeId, request);
            saveEducations(conn, employeeId, request);
            saveCareers(conn, employeeId, request);
            saveInsurances(conn, employeeId, request);
            saveMilitary(conn, employeeId, request);

            conn.commit(); // 성공 시 한번에 커밋 (成功時に一括コミット)
        } catch (SQLException e) {
            JdbcUtil.rollback(conn); // 예외 발생 시 롤백 (例外発生時にロールバック)
            throw new RuntimeException("사원 등록 실패", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    // 화면에서 넘어온 부양가족 동적 행 개수만큼 반복해서 인서트 치는 메서드
    // 画面から渡された扶養家族の動的行数分、繰り返しインサートをかけるメソッド
    private void saveDependents(Connection conn, int employeeId, HttpServletRequest request) throws SQLException {
        int count = RowFormUtil.parseIntOrDefault(request.getParameter("familyRowCount"), 0);
        for (int i = 1; i <= count; i++) {
            String name = request.getParameter("familyName" + i);
            if (isBlank(name)) continue; // 이름이 없는 빈 줄은 저장하지 않는다 (名前のない空行は保存しない)

            EmployeeDependent v = new EmployeeDependent();
            v.setDependentName(name);
            v.setRelationCode(request.getParameter("familyRelation" + i));
            v.setBirthDate(toSqlDate(request.getParameter("familyBirthDate" + i)));
            v.setDomForYn(request.getParameter("familyDomForYn" + i));
            v.setDisabledYn(checkboxToYn(request.getParameter("familyDisabled" + i)));
            v.setPersonalDeductionYn(checkboxToYn(request.getParameter("familyDeduction" + i)));
            v.setHealthInsuranceYn(checkboxToYn(request.getParameter("familyHealthIns" + i)));
            v.setCohabitationYn(checkboxToYn(request.getParameter("familyCohab" + i)));
            v.setWageIncomeTaxYn("N"); // 화면에 해당 입력칸이 아직 없어 기본값 (画面に対応する入力欄がまだないためデフォルト値)
            v.setChildUnder20Yn(checkboxToYn(request.getParameter("familyMultiChild" + i)));
            dependentDao.insert(conn, employeeId, v);
        }
    }

    // 학력 데이터 동적 행 반복 저장 로직
    // 学歴データの動的行の繰り返し保存ロジック
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
    // 4大保険（国民年金／健康保険／雇用保険／労災保険）はテーブル形式ではなく4行固定のため、
    // familyRowCountのような繰り返し処理の代わりに種類別に1つずつ確認して保存する。
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
        // 記号番号／取得日／喪失日の3つとも空であれば（＝全く入力していない行）保存しない
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
    // 兵役 - 何も入力されていなければ保存しない
    private void saveMilitary(Connection conn, int employeeId, HttpServletRequest request) throws SQLException {
        String status = request.getParameter("militaryStatus");
        String branchCode = request.getParameter("militaryBranchCode"); // 군별 (軍種)
        String start = request.getParameter("militaryStartDate");
        String end = request.getParameter("militaryEndDate");
        String grade = request.getParameter("militaryGrade");            // 계급 (階級)
        String branch = request.getParameter("militaryBranch");         // 병과 (兵科)
        String specialty = request.getParameter("militarySpecialty");   // 특기 (特技)
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
        militaryDao.save(conn, employeeId, v); // 업서트(Upsert) 메서드 호출 (アップサートメソッド呼び出し)
    }

    // 경력 데이터 동적 행 반복 저장 로직 + 근무 기간(년/월) 자동 계산 연동
    // 経歴データの動的行の繰り返し保存ロジック ＋ 勤務期間（年／月）の自動計算連携
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
    // 勤務期間（年／月）を入社日〜退社日で大まかに計算（退社日がなければ0/0）
    private int[] calcDutyYyMm(java.sql.Date start, java.sql.Date end) {
        if (start == null || end == null) {
            return new int[] { 0, 0 };
        }
        java.time.LocalDate s = start.toLocalDate();
        java.time.LocalDate e = end.toLocalDate();
        java.time.Period p = java.time.Period.between(s, e);
        return new int[] { p.getYears(), p.getMonths() };
    }

    // 문자열 날짜를 SQL Date 타입으로 안전하게 변환 (실패 시 null 리턴)
    // 文字列の付箋（日付）をSQL Date型に安全に変換（失敗時はnullを返す）
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

    // 체크박스 값("on" 또는 null)을 "Y" 또는 "N"으로 변환
    // チェックボックスの値（"on"またはnull）を"Y"または"N"に変換
    private String checkboxToYn(String value) {
        return "on".equals(value) ? "Y" : "N";
    }

    private String nvl(String value, String defaultValue) {
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    // 사원현황에서 불러온 기존 사원 수정 - 기본정보 UPDATE + 서브 테이블 delete-insert (아라이가예 방식)
    // 社員状況から呼び出した既存社員の修正 - 基本情報UPDATE ＋ サブテーブルのdelete-insert（洗い替え方式）
    public void updateEmployee(Employee emp, HttpServletRequest request) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            employeeDao.update(conn, emp);
            int employeeId = emp.getEmployeeId();

            // 서브 테이블은 기존 데이터 전부 삭제 후 재삽입
            // サブテーブルは既存データを全削除した後に再挿入
            dependentDao.deleteByEmployeeId(conn, employeeId);
            educationDao.deleteByEmployeeId(conn, employeeId);
            careerDao.deleteByEmployeeId(conn, employeeId);
            insuranceDao.deleteByEmployeeId(conn, employeeId);

            saveDependents(conn, employeeId, request);
            saveEducations(conn, employeeId, request);
            saveCareers(conn, employeeId, request);
            saveInsurances(conn, employeeId, request);
            saveMilitary(conn, employeeId, request);

            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException("사원 정보 수정 실패", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    // 사원현황에서 이름 클릭 시 사원등록1 폼에 기존 데이터를 불러오기 위한 조회
    // 社員状況で名前をクリックした際、社員登録1フォームに既存データを呼び出すための照会
    public Employee getEmployeeById(int employeeId) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return employeeDao.selectEmployeeById(conn, employeeId);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    // 부양가족/학력/경력/4대보험/병역 등 별도 테이블 데이터까지 전부 request에 세팅
    // JSP가 request.getParameter("familyName1") 등으로 읽으므로, 동일한 키로 setAttribute 해줌
    // 扶養家族／学歴／経歴／4大保険／兵役など、別テーブルのデータまで全てrequestにセット
    // JSPがrequest.getParameter("familyName1")などで読み込むため、同一のキーでsetAttributeする
    public void loadAllSubTableData(int employeeId, HttpServletRequest request) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();

            // 부양가족 (扶養家族)
            java.util.List<EmployeeDependent> dependents = dependentDao.selectByEmployeeId(conn, employeeId);
            int familyCount = dependents.size() > 0 ? dependents.size() : 1;
            request.setAttribute("familyRowCount", familyCount);
            for (int i = 0; i < dependents.size(); i++) {
                EmployeeDependent d = dependents.get(i);
                int n = i + 1;
                request.setAttribute("familyRelation" + n, nz(d.getRelationCode()));
                request.setAttribute("familyName" + n, nz(d.getDependentName()));
                request.setAttribute("familyDomForYn" + n, nz(d.getDomForYn()));
                request.setAttribute("familyRrn" + n, "");
                request.setAttribute("familyDisabled" + n, "Y".equals(d.getDisabledYn()) ? "on" : "");
                request.setAttribute("familyDeduction" + n, "Y".equals(d.getPersonalDeductionYn()) ? "on" : "");
                request.setAttribute("familyHealthIns" + n, "Y".equals(d.getHealthInsuranceYn()) ? "on" : "");
                request.setAttribute("familyCohab" + n, "Y".equals(d.getCohabitationYn()) ? "on" : "");
                request.setAttribute("familyMultiChild" + n, "Y".equals(d.getChildUnder20Yn()) ? "on" : "");
            }

            // 학력 (学歴)
            java.util.List<EmployeeEducation> educations = educationDao.selectByEmployeeId(conn, employeeId);
            int eduCount = educations.size() > 0 ? educations.size() : 1;
            request.setAttribute("educationRowCount", eduCount);
            for (int i = 0; i < educations.size(); i++) {
                EmployeeEducation e = educations.get(i);
                int n = i + 1;
                request.setAttribute("educationSchool" + n, nz(e.getSchoolName()));
                request.setAttribute("educationMajor" + n, nz(e.getMajorName()));
                request.setAttribute("educationStart" + n, e.getStartDate() != null ? e.getStartDate().toString() : "");
                request.setAttribute("educationEnd" + n, e.getEndDate() != null ? e.getEndDate().toString() : "");
                request.setAttribute("educationStatus" + n, nz(e.getGraduationStatus()));
            }

            // 경력 (経歴)
            java.util.List<EmployeeCareer> careers = careerDao.selectByEmployeeId(conn, employeeId);
            int carCount = careers.size() > 0 ? careers.size() : 1;
            request.setAttribute("careerRowCount", carCount);
            for (int i = 0; i < careers.size(); i++) {
                EmployeeCareer c = careers.get(i);
                int n = i + 1;
                request.setAttribute("careerCompany" + n, nz(c.getCompanyName()));
                request.setAttribute("careerDept" + n, nz(c.getDepartment()));
                request.setAttribute("careerPosition" + n, nz(c.getPosition()));
                request.setAttribute("careerStart" + n, c.getStartDate() != null ? c.getStartDate().toString() : "");
                request.setAttribute("careerEnd" + n, c.getEndDate() != null ? c.getEndDate().toString() : "");
            }

            // 4대보험 (4大保険)
            java.util.List<EmployeeInsurance> insurances = insuranceDao.selectByEmployeeId(conn, employeeId);
            for (EmployeeInsurance ins : insurances) {
                String code = nz(ins.getInsuranceTypeCode());
                String no = nz(ins.getInsuranceNo());
                String acq = ins.getAcquisitionDate() != null ? ins.getAcquisitionDate().toString() : "";
                String loss = ins.getLossDate() != null ? ins.getLossDate().toString() : "";
                if ("국민연금".equals(code)) {
                    request.setAttribute("insuranceNoNP", no);
                    request.setAttribute("acquisitionDateNP", acq);
                    request.setAttribute("lossDateNP", loss);
                } else if ("건강보험".equals(code)) {
                    request.setAttribute("insuranceNoHI", no);
                    request.setAttribute("acquisitionDateHI", acq);
                    request.setAttribute("lossDateHI", loss);
                } else if ("고용보험".equals(code)) {
                    request.setAttribute("insuranceNoEI", no);
                    request.setAttribute("acquisitionDateEI", acq);
                    request.setAttribute("lossDateEI", loss);
                } else if ("산재보험".equals(code)) {
                    request.setAttribute("insuranceNoII", no);
                    request.setAttribute("acquisitionDateII", acq);
                    request.setAttribute("lossDateII", loss);
                }
            }

            // 병역 (兵役)
            EmployeeMilitary military = militaryDao.selectByEmployeeId(conn, employeeId);
            if (military != null) {
                request.setAttribute("militaryStatus", nz(military.getMilitaryStatusCode()));
                request.setAttribute("militaryBranchCode", nz(military.getMilitaryBranchCode()));
                request.setAttribute("militaryStartDate", military.getServiceStartDate() != null ? military.getServiceStartDate().toString() : "");
                request.setAttribute("militaryEndDate", military.getServiceEndDate() != null ? military.getServiceEndDate().toString() : "");
                request.setAttribute("militaryGrade", nz(military.getMilitaryGrade()));
                request.setAttribute("militaryBranch", nz(military.getMilitaryBranch()));
                request.setAttribute("militarySpecialty", nz(military.getMilitarySpecialty()));
                request.setAttribute("militaryExemptReason", nz(military.getMilitaryExemptReason()));
            }

        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    private String nz(String s) { return s == null ? "" : s; }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    // "No-260001" -> "No-260002" 처럼 다음 번호를 계산. 기존 값이 없으면 최초값부터 시작.
    // "No-260001" -> "No-260002"のように次の番号を計算。既存値がなければ初期値からスタート。
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