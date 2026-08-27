package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeQualification;
import jdbc.JdbcUtil;

// 사원 자격증(자격/면허 취득 내역, 발급기관 등) 데이터를 처리하는 DAO
// 社員の資格（資格・免許の取得履歴、発行機関など）データを処理する DAO
public class QualificationDao {

    // 자격증 데이터 한 줄을 DB에 밀어 넣는(인서트 치는) 메서드
    // 資格データを1行DBに押し込む（インサートをかける）メソッド
    public void insert(Connection conn, int employeeId, EmployeeQualification v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_QUALIFICATION ("
                       + "  QUALIFICATION_ID, EMPLOYEE_ID, QUALIFICATION_NAME, ACQUISITION_DATE, "
                       + "  ISSUING_ORGANIZATION, CERTIFICATE_NO, MEMO, REG_ID, MOD_ID"
                       + ") VALUES (EMP_QUALIFICATION_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getQualificationName());
            pstmt.setDate(3, v.getAcquisitionDate());
            pstmt.setString(4, v.getIssuingOrganization());
            pstmt.setString(5, v.getCertificateNo());
            pstmt.setString(6, v.getMemo());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 자격증 데이터를 통째로 날림
    // (화면에서 수정 시 부분 업데이트 안 하고, 기존 데이터를 싹 지운 다음 새로 엎어치기 하려는 용도)
    // 特定社員の資格データを丸ごと飛ばす
    // （画面での修正時、部分アップデートを行わず既存データを全消しして新しく洗い替えするための用途）
    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_QUALIFICATION WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 전체 자격증 내역을 쫙 긁어옴 (화면 표에 뿌려줄 때 씀)
    // 特定社員の全資格履歴をざっとかき集める（画面の表に描画する時に使う）
    public List<EmployeeQualification> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_QUALIFICATION WHERE EMPLOYEE_ID = ? ORDER BY QUALIFICATION_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmployeeQualification> list = new ArrayList<>();
            while (rs.next()) {
                EmployeeQualification v = new EmployeeQualification();
                v.setQualificationName(rs.getString("QUALIFICATION_NAME"));
                v.setAcquisitionDate(rs.getDate("ACQUISITION_DATE"));
                v.setIssuingOrganization(rs.getString("ISSUING_ORGANIZATION"));
                v.setCertificateNo(rs.getString("CERTIFICATE_NO"));
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