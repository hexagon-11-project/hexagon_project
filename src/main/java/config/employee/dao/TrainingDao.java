package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeTraining;
import jdbc.JdbcUtil;

// 사원 교육/훈련(사내 교육, 외부 연수, 교육비 등) 데이터를 처리하는 DAO
// 社員の教育・訓練（社内研修、外部研修、教育費など）データを処理する DAO
public class TrainingDao {

    // 교육/훈련 데이터 한 줄을 DB에 밀어 넣는(인서트 치는) 메서드
    // 教育データを1行DBに押し込む（インサートをかける）メソッド
    public void insert(Connection conn, int employeeId, EmployeeTraining v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_TRAINING ("
                       + "  TRAINING_ID, EMPLOYEE_ID, TRAINING_TYPE_CODE, TRAINING_NAME, "
                       + "  TRAINING_START_DATE, TRAINING_END_DATE, TRAINING_INSTITUTION, "
                       + "  TRAINING_COST, REFUND_TRAINING_COST, REG_ID, MOD_ID"
                       + ") VALUES (EMP_TRAINING_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getTrainingTypeCode());
            pstmt.setString(3, v.getTrainingName());
            pstmt.setDate(4, v.getStartDate());
            pstmt.setDate(5, v.getEndDate());
            pstmt.setString(6, v.getTrainingInstitution());
            pstmt.setLong(7, v.getTrainingCost());
            pstmt.setLong(8, v.getRefundTrainingCost());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 교육 이력을 통째로 날림
    // (화면에서 수정 시 부분 업데이트 안 하고, 기존 데이터를 싹 지운 다음 새로 엎어치기 하려는 용도)
    // 特定社員の教育履歴を丸ごと飛ばす
    // （画面での修正時、部分アップデートを行わず既存データを全消しして新しく洗い替えするための用途）
    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_TRAINING WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 전체 교육/연수 내역을 쫙 긁어옴 (화면 표에 뿌려줄 때 씀)
    // 特定社員の全教育・研修履歴をざっとかき集める（画面の表に描画する時に使う）
    public List<EmployeeTraining> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_TRAINING WHERE EMPLOYEE_ID = ? ORDER BY TRAINING_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmployeeTraining> list = new ArrayList<>();
            while (rs.next()) {
                EmployeeTraining v = new EmployeeTraining();
                v.setTrainingTypeCode(rs.getString("TRAINING_TYPE_CODE"));
                v.setTrainingName(rs.getString("TRAINING_NAME"));
                v.setStartDate(rs.getDate("TRAINING_START_DATE"));
                v.setEndDate(rs.getDate("TRAINING_END_DATE"));
                v.setTrainingInstitution(rs.getString("TRAINING_INSTITUTION"));
                v.setTrainingCost(rs.getLong("TRAINING_COST"));
                v.setRefundTrainingCost(rs.getLong("REFUND_TRAINING_COST"));
                list.add(v);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}