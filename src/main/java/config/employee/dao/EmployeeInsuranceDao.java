package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeInsurance;
import jdbc.JdbcUtil;

// 사원 4대보험(국민연금, 건강보험 등 가입/상실) 데이터를 처리하는 DAO
// 社員の4大保険（国民年金、健康保険などの加入・喪失）データを処理する DAO
public class EmployeeInsuranceDao {

    // 보험 이력 한 줄을 DB에 밀어 넣는(인서트 치는) 메서드
    // (기존 주석 내용처럼 여기만 시퀀스 이름이 튀니까 나중에 헷갈리지 않게 주의!)
    // 保険履歴を1行DBに押し込む（インサートをかける）メソッド
    // （既存のコメント通り、ここだけシーケンス名が他と違うので後で混乱しないように注意！）
    // 참고: 실제 DB에 만들어져 있는 시퀀스 이름이 다른 테이블들과 패턴이 달라서(SEQ_EMPLOYEE_INSURANCE) 그대로 맞춤
    public void insert(Connection conn, int employeeId, EmployeeInsurance v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_INSURANCE ("
                       + "  EMPLOYEE_INSURANCE_ID, EMPLOYEE_ID, INSURANCE_TYPE_CODE, INSURANCE_NO, "
                       + "  ACQUISITION_DATE, LOSS_DATE, REG_ID, MOD_ID"
                       + ") VALUES (SEQ_EMPLOYEE_INSURANCE.NEXTVAL, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getInsuranceTypeCode());
            pstmt.setString(3, v.getInsuranceNo());
            pstmt.setDate(4, v.getAcquisitionDate());
            pstmt.setDate(5, v.getLossDate());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 보험 이력을 통째로 날림
    // (이것도 역시 화면에서 정보 수정 시, 기존 데이터를 싹 지우고 새로 엎어치기(재입력) 하려는 용도)
    // 特定社員の保険履歴を丸ごと飛ばす
    // （これもやはり画面での情報修正時、既存データを全消しして新しく洗い替え（再入力）するための用途）
    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_INSURANCE WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 전체 보험 취득/상실 내역을 쫙 긁어옴 (화면 표에 뿌려줄 때 씀)
    // 特定社員の全保険（取得・喪失）履歴をざっとかき集める（画面の表に描画する時に使う）
    public List<EmployeeInsurance> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_INSURANCE WHERE EMPLOYEE_ID = ? ORDER BY EMPLOYEE_INSURANCE_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmployeeInsurance> list = new ArrayList<>();
            while (rs.next()) {
                EmployeeInsurance v = new EmployeeInsurance();
                v.setInsuranceTypeCode(rs.getString("INSURANCE_TYPE_CODE"));
                v.setInsuranceNo(rs.getString("INSURANCE_NO"));
                v.setAcquisitionDate(rs.getDate("ACQUISITION_DATE"));
                v.setLossDate(rs.getDate("LOSS_DATE"));
                list.add(v);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}