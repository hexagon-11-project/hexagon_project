package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeDependent;
import jdbc.JdbcUtil;

// 사원 부양가족(가족수당, 연말정산 등) 정보를 처리하는 DAO
// 社員の扶養家族（家族手当、年末調整など）情報を処理する DAO
public class DependentDao {

    // 부양가족 데이터를 한 줄씩 인서트 쳐주는 메서드
    // 扶養家族データを1行ずつインサートするメソッド
    public void insert(Connection conn, int employeeId, EmployeeDependent v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_DEPENDENT ("
                       + "  DEPENDENT_ID, EMPLOYEE_ID, DEPENDENT_NAME, RELATION_CODE, DOM_FOR_YN, BIRTH_DATE, "
                       + "  DISABLED_YN, PERSONAL_DEDUCTION_YN, HEALTH_INSURANCE_YN, COHABITATION_YN, "
                       + "  WAGE_INCOME_TAX_YN, CHILD_UNDER_20_YN, REG_ID, MOD_ID"
                       + ") VALUES (EMP_DEPENDENT_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getDependentName());
            pstmt.setString(3, v.getRelationCode());
            pstmt.setString(4, v.getDomForYn());
            pstmt.setDate(5, v.getBirthDate());
            pstmt.setString(6, v.getDisabledYn());
            pstmt.setString(7, v.getPersonalDeductionYn());
            pstmt.setString(8, v.getHealthInsuranceYn());
            pstmt.setString(9, v.getCohabitationYn());
            pstmt.setString(10, v.getWageIncomeTaxYn());
            pstmt.setString(11, v.getChildUnder20Yn());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 사원 한 명의 부양가족을 통째로 날림 
    // (화면에서 수정 버튼 눌렀을 때 부분 업데이트 안 하고, 싹 지운 다음 다시 엎어치기(재입력) 하려는 용도)
    // 社員1名分の扶養家族データを丸ごと飛ばす
    // （保存のたびに全削除して新しく入れ直す「洗い替え」方式で処理するため）
    public void deleteByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_DEPENDENT WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 특정 사원의 부양가족 목록을 쫙 긁어옴 (화면 표에 뿌려주거나 공제 계산할 때 씀)
    // 特定社員の扶養家族リストをざっと取得する（画面の表に描画したり、控除計算する時に使う）
    public List<EmployeeDependent> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_DEPENDENT WHERE EMPLOYEE_ID = ? ORDER BY DEPENDENT_ID");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            List<EmployeeDependent> list = new ArrayList<>();
            while (rs.next()) {
                EmployeeDependent v = new EmployeeDependent();
                v.setDependentName(rs.getString("DEPENDENT_NAME"));
                v.setRelationCode(rs.getString("RELATION_CODE"));
                v.setDomForYn(rs.getString("DOM_FOR_YN"));
                v.setBirthDate(rs.getDate("BIRTH_DATE"));
                v.setDisabledYn(rs.getString("DISABLED_YN"));
                v.setPersonalDeductionYn(rs.getString("PERSONAL_DEDUCTION_YN"));
                v.setHealthInsuranceYn(rs.getString("HEALTH_INSURANCE_YN"));
                v.setCohabitationYn(rs.getString("COHABITATION_YN"));
                v.setWageIncomeTaxYn(rs.getString("WAGE_INCOME_TAX_YN"));
                v.setChildUnder20Yn(rs.getString("CHILD_UNDER_20_YN"));
                list.add(v);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}