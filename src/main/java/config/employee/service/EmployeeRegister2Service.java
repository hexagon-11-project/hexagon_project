package config.employee.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import config.employee.command.RowFormUtil;
import config.employee.dao.EmployeeAppointmentDao;
import config.employee.dao.EmployeeDao;
import config.employee.dao.EmployeeQualificationDao;
import config.employee.dao.EmployeeRewardPunishmentDao;
import config.employee.dao.EmployeeTrainingDao;
import config.employee.model.EmployeeAppointment;
import config.employee.model.EmployeeQualification;
import config.employee.model.EmployeeRewardPunishment;
import config.employee.model.EmployeeTraining;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class EmployeeRegister2Service {

    private EmployeeDao employeeDao = new EmployeeDao();
    private EmployeeQualificationDao qualificationDao = new EmployeeQualificationDao();
    private EmployeeTrainingDao trainingDao = new EmployeeTrainingDao();
    private EmployeeRewardPunishmentDao rewardPunishmentDao = new EmployeeRewardPunishmentDao();
    private EmployeeAppointmentDao appointmentDao = new EmployeeAppointmentDao();

    /**
     * 자격면허/교육훈련/상벌/발령을 한 트랜잭션으로 저장한다.
     * 이 페이지는 "이어서 계속 추가로 등록"하는 화면이라, 저장할 때마다
     * 해당 사원의 기존 데이터를 싹 지우고 화면에 있는 내용으로 다시 채운다
     * (그래야 화면에서 지운 줄이 DB에도 반영됨).
     */
    public void saveAdditionalInfo(int employeeId, HttpServletRequest request) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            qualificationDao.deleteByEmployeeId(conn, employeeId);
            saveQualifications(conn, employeeId, request);

            trainingDao.deleteByEmployeeId(conn, employeeId);
            saveTrainings(conn, employeeId, request);

            rewardPunishmentDao.deleteByEmployeeId(conn, employeeId);
            saveRewardPunishments(conn, employeeId, request);

            appointmentDao.deleteByEmployeeId(conn, employeeId);
            saveAppointments(conn, employeeId, request);

            saveRetirementInfo(conn, employeeId, request);

            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException("사원 추가 정보 등록 실패", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    private void saveQualifications(Connection conn, int employeeId, HttpServletRequest request) throws SQLException {
        int count = RowFormUtil.parseIntOrDefault(request.getParameter("licenseRowCount"), 0);
        for (int i = 1; i <= count; i++) {
            String name = request.getParameter("licenseName" + i);
            if (isBlank(name)) continue;

            EmployeeQualification v = new EmployeeQualification();
            v.setQualificationName(name);
            v.setAcquisitionDate(toSqlDate(request.getParameter("licenseDate" + i)));
            v.setIssuingOrganization(request.getParameter("licenseOrg" + i));
            v.setCertificateNo(request.getParameter("licenseGrade" + i)); // 화면상 "등급" 칸을 증번호로 매핑
            v.setMemo(request.getParameter("licenseMemo" + i));
            qualificationDao.insert(conn, employeeId, v);
        }
    }

    private void saveTrainings(Connection conn, int employeeId, HttpServletRequest request) throws SQLException {
        int count = RowFormUtil.parseIntOrDefault(request.getParameter("trainingRowCount"), 0);
        for (int i = 1; i <= count; i++) {
            String name = request.getParameter("trainingName" + i);
            if (isBlank(name)) continue;

            EmployeeTraining v = new EmployeeTraining();
            v.setTrainingName(name);
            v.setTrainingTypeCode(request.getParameter("trainingType" + i));
            v.setStartDate(toSqlDate(request.getParameter("trainingStart" + i)));
            v.setEndDate(toSqlDate(request.getParameter("trainingEnd" + i)));
            v.setTrainingInstitution(request.getParameter("trainingOrg" + i));
            v.setTrainingCost(toLong(request.getParameter("trainingCost" + i)));
            v.setRefundTrainingCost(toLong(request.getParameter("trainingRefund" + i)));
            trainingDao.insert(conn, employeeId, v);
        }
    }

    private void saveRewardPunishments(Connection conn, int employeeId, HttpServletRequest request) throws SQLException {
        int count = RowFormUtil.parseIntOrDefault(request.getParameter("rewardRowCount"), 0);
        for (int i = 1; i <= count; i++) {
            String name = request.getParameter("rewardName" + i);
            if (isBlank(name)) continue;

            EmployeeRewardPunishment v = new EmployeeRewardPunishment();
            v.setName(name);
            v.setTypeCode(request.getParameter("rewardType" + i));
            v.setDate(toSqlDate(request.getParameter("rewardDate" + i)));
            v.setContent(request.getParameter("rewardContent" + i));
            v.setMemo(request.getParameter("rewardMemo" + i));
            rewardPunishmentDao.insert(conn, employeeId, v);
        }
    }

    private void saveAppointments(Connection conn, int employeeId, HttpServletRequest request) throws SQLException {
        int count = RowFormUtil.parseIntOrDefault(request.getParameter("appointmentRowCount"), 0);
        for (int i = 1; i <= count; i++) {
            String date = request.getParameter("apptDate" + i);
            String type = request.getParameter("apptType" + i);
            if (isBlank(date)) continue; // APPOINTMENT_DATE는 DB에서 NOT NULL이라 필수

            EmployeeAppointment v = new EmployeeAppointment();
            v.setTypeCode(type);
            v.setDate(toSqlDate(date));
            v.setDepartment(request.getParameter("apptDept" + i));
            v.setPosition(request.getParameter("apptPosition" + i));
            v.setDutyTitle(request.getParameter("apptDuty" + i));
            v.setMemo(request.getParameter("apptMemo" + i));
            appointmentDao.insert(conn, employeeId, v);
        }
    }

    // 퇴직 섹션은 여러 줄이 아니라 사원 한 명당 값 하나씩이라, EMPLOYEE 테이블을 직접 UPDATE한다.
    private void saveRetirementInfo(Connection conn, int employeeId, HttpServletRequest request) throws SQLException {
        String type = request.getParameter("retireType");
        String date = request.getParameter("retireDate");
        String reason = request.getParameter("retireReason");
        String phone = request.getParameter("retirePhone");

        // 아무것도 입력 안 했으면(전부 빈값) 건드리지 않는다 - 괜히 기존 값을 null로 덮어쓰지 않기 위함
        if (isBlank(type) && isBlank(date) && isBlank(reason) && isBlank(phone)) {
            return;
        }
        employeeDao.updateRetirementInfo(conn, employeeId, type, toSqlDate(date), reason, phone);
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

    private long toLong(String value) {
        if (value == null || value.isEmpty()) {
            return 0L;
        }
        try {
            return Long.parseLong(value.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
