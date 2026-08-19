package retirement.retireProcess.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import retirement.model.RetirementProcessModel;

public class RetirementProcessReadDao {

    public List<RetirementProcessModel> getRetirementList(Connection conn, String searchName, String status) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<RetirementProcessModel> list = new ArrayList<>();

        try {
            String sql = "SELECT e.retirement_yn, "
                       + "       e.employee_no, "
                       + "       e.employee_name, "
                       + "       e.department, "
                       + "       e.position, "
                       + "       TO_CHAR(e.hire_date, 'yyyy-mm-dd') AS hire_date, "
                       + "       TO_CHAR(e.resign_date, 'yyyy-mm-dd') AS resign_date, "
                       + "       NVL(rp.interim_settlement_yn, 'N') AS interim_settlement_yn, "
                       + "       NVL(rp.retirement_settlement_yn, 'N') AS retirement_settlement_yn "
                       + "FROM employee e "
                       + "LEFT JOIN retirement_pay rp ON e.employee_id = rp.employee_id "
                       + "WHERE 1=1 ";

            // 검색어 조건 추가
            if (searchName != null && !searchName.trim().isEmpty()) {
                sql += "AND e.employee_name LIKE ? ";
            }
            
            // 상태별 검색 조건 추가 (전체가 아닐 경우)
            if (status != null && !status.equals("전체보기") && !status.trim().isEmpty()) {
                sql += "AND e.retirement_yn = ? ";
            }

            sql += "ORDER BY e.employee_no ASC";

            pstmt = conn.prepareStatement(sql);

            // 파라미터 세팅
            int paramIndex = 1;
            if (searchName != null && !searchName.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + searchName.trim() + "%");
            }
            
            if (status != null && !status.equals("전체보기") && !status.trim().isEmpty()) {
                pstmt.setString(paramIndex++, status);
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                RetirementProcessModel model = new RetirementProcessModel();

                model.setRetirementYn(rs.getString("retirement_yn"));
                model.setEmployeeNo(rs.getString("employee_no"));
                model.setEmployeeName(rs.getString("employee_name"));
                model.setDepartment(rs.getString("department"));
                model.setPosition(rs.getString("position"));
                model.setHireDate(rs.getString("hire_date"));
                model.setResignDate(rs.getString("resign_date"));
                model.setInterimSettlementYn(rs.getString("interim_settlement_yn"));
                model.setRetirementSettlementYn(rs.getString("retirement_settlement_yn"));

                list.add(model);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
       

            public int updateRetirementProcess(Connection conn, RetirementProcessModel model) throws SQLException {
                PreparedStatement pstmt = null;
                int result = 0;

                try {
                    // 사원이 이미 존재하므로 UPDATE 쿼리 사용
                    String sql = "UPDATE employee SET "
                               + "retirement_yn = 'Y', "                     // 상태를 퇴직으로 변경
                               + "retirement_type_code = ?, "                // 퇴직구분
                               + "resign_date = TO_DATE(?, 'yyyy-mm-dd'), "  // 퇴직일자
                               + "retirement_reason = ?, "                   // 퇴직사유
                               + "post_retirement_phone = ? "                // 퇴직 후 연락처
                               + "WHERE employee_no = ?";                    // 사원번호 조건

                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, model.getRetirementTypeCode());
                    pstmt.setString(2, model.getResignDate());
                    pstmt.setString(3, model.getRetirementReason());
                    pstmt.setString(4, model.getPostRetirementPhone());
                    pstmt.setString(5, model.getEmployeeNo());

                    result = pstmt.executeUpdate();
                    return result;
                } finally {
                    JdbcUtil.close(pstmt);
                }
            }
    
}