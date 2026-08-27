package config.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.employee.model.EmployeeMilitary;
import jdbc.JdbcUtil;

// 사원 병역(군필 여부, 복무 기간, 군별 등) 정보를 처리하는 DAO
// 社員の兵役（軍歴、服務期間、軍種など）情報を処理する DAO
// EMPLOYEE_MILITARY는 사원 한 명당 한 줄뿐인 테이블이라(PK가 EMPLOYEE_ID 그 자체),
// 시퀀스로 채번하는 다른 테이블들과 다르게 insert/update/select 모두 employeeId로 직접 다룬다.
public class EmployeeMilitaryDao {

    // 병역 데이터를 신규로 DB에 밀어 넣는 메서드
    // 兵役データを新規でDBに押し込むメソッド
    public void insert(Connection conn, int employeeId, EmployeeMilitary v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO EMPLOYEE_MILITARY ("
                       + "  EMPLOYEE_ID, MILITARY_STATUS_CODE, MILITARY_BRANCH_CODE, "
                       + "  MILITARY_SERVICE_START_DATE, MILITARY_SERVICE_END_DATE, "
                       + "  MILITARY_SPECIALTY, MILITARY_EXEMPT_REASON, MILITARY_GRADE, MILITARY_BRANCH, "
                       + "  REG_ID, MOD_ID"
                       + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'SYSTEM', 'SYSTEM')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, v.getMilitaryStatusCode());
            pstmt.setString(3, v.getMilitaryBranchCode());
            pstmt.setDate(4, v.getServiceStartDate());
            pstmt.setDate(5, v.getServiceEndDate());
            pstmt.setString(6, v.getMilitarySpecialty());
            pstmt.setString(7, v.getMilitaryExemptReason());
            pstmt.setString(8, v.getMilitaryGrade());
            pstmt.setString(9, v.getMilitaryBranch());
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 이미 등록된 병역기록이 있으면 새로 넣지 않고 덮어쓴다 (1:1 관계라 여러 줄이 되면 안 됨)
    // 既に登録された兵役記録があれば、新しく入れずに上書きする（1:1関係なので複数行になるとNG）
    public int update(Connection conn, int employeeId, EmployeeMilitary v) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "UPDATE EMPLOYEE_MILITARY SET "
                       + "MILITARY_STATUS_CODE=?, MILITARY_BRANCH_CODE=?, "
                       + "MILITARY_SERVICE_START_DATE=?, MILITARY_SERVICE_END_DATE=?, "
                       + "MILITARY_SPECIALTY=?, MILITARY_EXEMPT_REASON=?, MILITARY_GRADE=?, MILITARY_BRANCH=?, "
                       + "MOD_ID='SYSTEM', UPDATED_AT=SYSDATE "
                       + "WHERE EMPLOYEE_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, v.getMilitaryStatusCode());
            pstmt.setString(2, v.getMilitaryBranchCode());
            pstmt.setDate(3, v.getServiceStartDate());
            pstmt.setDate(4, v.getServiceEndDate());
            pstmt.setString(5, v.getMilitarySpecialty());
            pstmt.setString(6, v.getMilitaryExemptReason());
            pstmt.setString(7, v.getMilitaryGrade());
            pstmt.setString(8, v.getMilitaryBranch());
            pstmt.setInt(9, employeeId);
            return pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    // 등록이면 insert, 이미 있으면 update - 호출하는 쪽에서 매번 이거 하나만 부르면 됨
    // 데이터 유무를 확인해서 알아서 분기 태우는 이른바 Upsert(업서트) 꿀메서드. 서비스단에서는 고민 없이 이것만 호출함.
    // 登録ならinsert、既にあればupdate - 呼び出す側は毎回これ一つを呼べばOK
    // データの有無を確認して勝手に分岐させる、いわゆるUpsert（アップサート）便利メソッド。サービス側では何も考えずこれだけ呼ぶ。
    public void save(Connection conn, int employeeId, EmployeeMilitary v) throws SQLException {
        if (exists(conn, employeeId)) {
            update(conn, employeeId, v);
        } else {
            insert(conn, employeeId, v);
        }
    }

    // DB에 해당 사원의 병역 데이터가 이미 있는지 찔러보는(카운트 세는) 메서드
    // DBに該当社員の兵役データが既にあるか探ってみる（カウントを数える）メソッド
    public boolean exists(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM EMPLOYEE_MILITARY WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }

    // 사원 ID로 병역 정보 단건 조회 (화면 그릴 때 데이터 가져오는 용도)
    // 社員IDで兵役情報を単件照会（画面を描画する時にデータを引っ張ってくる用途）
    public EmployeeMilitary selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM EMPLOYEE_MILITARY WHERE EMPLOYEE_ID = ?");
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                EmployeeMilitary v = new EmployeeMilitary();
                v.setMilitaryStatusCode(rs.getString("MILITARY_STATUS_CODE"));
                v.setMilitaryBranchCode(rs.getString("MILITARY_BRANCH_CODE"));
                v.setServiceStartDate(rs.getDate("MILITARY_SERVICE_START_DATE"));
                v.setServiceEndDate(rs.getDate("MILITARY_SERVICE_END_DATE"));
                v.setMilitarySpecialty(rs.getString("MILITARY_SPECIALTY"));
                v.setMilitaryExemptReason(rs.getString("MILITARY_EXEMPT_REASON"));
                v.setMilitaryGrade(rs.getString("MILITARY_GRADE"));
                v.setMilitaryBranch(rs.getString("MILITARY_BRANCH"));
                return v;
            }
            return null;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
}