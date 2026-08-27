package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeAppointment;
import jdbc.JdbcUtil;

// 사원 발령(부서이동, 승진 등) 이력 데이터를 처리하는 DAO 
// 社員の辞令・発令（部署異動、昇進など）履歴データを処理する DAO
public class EmployeeAppointmentDao {

    // 발령 내역 한 줄을 DB에 인서트 치는 메서드
    // 発令履歴を1行DBにインサートをかけるメソッド
    public void insert(Connection conn, int employeeId, EmployeeAppointment v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_APPOINTMENT ("
                       + "  APPOINTMENT_ID, EMPLOYEE_ID, APPOINTMENT_TYPE_CODE, APPOINTMENT_DATE, "
                       + "  DEPARTMENT, POSITION, DUTY_TITLE, MEMO, REG_ID, MOD_ID"
                       + ") VALUES (EMP_APPOINTMENT_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getTypeCode());
            pstmt.setDate(3, v.getDate());
            pstmt.setString(4, v.getDepartment());
            pstmt.setString(5, v.getPosition());
            pstmt.setString(6, v.getDutyTitle());
            pstmt.setString(7, v.getMemo());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 발령 이력을 통째로 날림
    // (화면에서 수정할 때 부분 업데이트 안 하고 기존 데이터 싹 지운 다음 새로 엎어치기 하려는 용도)
    // 特定社員の発令履歴を丸ごと飛ばす
    // （画面修正時に部分アップデートせず、既存データを全消しして新しく洗い替えするための用途）
    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_APPOINTMENT WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 전체 발령 내역을 쫙 긁어옴 (화면 표에 뿌려줄 때 씀)
    // 特定社員の全発令履歴をざっとかき集める（画面の表に描画する時に使う）
    public List<EmployeeAppointment> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_APPOINTMENT WHERE EMPLOYEE_ID = ? ORDER BY APPOINTMENT_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmployeeAppointment> list = new ArrayList<>();
            while (rs.next()) {
                EmployeeAppointment v = new EmployeeAppointment();
                v.setTypeCode(rs.getString("APPOINTMENT_TYPE_CODE"));
                v.setDate(rs.getDate("APPOINTMENT_DATE"));
                v.setDepartment(rs.getString("DEPARTMENT"));
                v.setPosition(rs.getString("POSITION"));
                v.setDutyTitle(rs.getString("DUTY_TITLE"));
                v.setMemo(rs.getString("MEMO"));
                list.add(v);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}