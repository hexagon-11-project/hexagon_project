package retirment.retirementMnt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import retirement.model.RetirementMntModel;
import retirement.model.RetirementMntModel.MonthlyWage;

public class RetirementMntDao {

    // 1. 퇴직급여 대상 목록 조회 (순수 데이터만 반환)
    public List<RetirementMntModel> getRetirementMntList(Connection conn, String retirementYear, String employeeId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<RetirementMntModel> list = new ArrayList<>();

        try {
            // JSP에서 판별할 수 있도록 NVL을 사용해 순수 'Y' 또는 'N' 값만 넘겨줌
        	String sql = "SELECT e.employee_id, "
                    + "       e.employee_no, "
                    + "       e.employee_name, "
                    + "       TO_CHAR(e.hire_date, 'yyyy-mm-dd') AS hire_date, "
                    + "       TO_CHAR(e.resign_date, 'yyyy-mm-dd') AS resign_date, "
                    + "       NVL(rp.retirement_settlement_yn, 'N') AS retirement_settlement_yn "
                    + "FROM employee e "
                    + "LEFT JOIN ( "
                    + "    SELECT employee_id, retirement_settlement_yn, "
                    + "           ROW_NUMBER() OVER(PARTITION BY employee_id ORDER BY retirement_pay_id DESC) as rn "
                    + "    FROM retirement_pay "
                    + ") rp ON e.employee_id = rp.employee_id AND rp.rn = 1 "
                    + "WHERE e.retirement_yn = 'Y' ";

            if (retirementYear != null && !retirementYear.trim().isEmpty()) {
                sql += "AND TO_CHAR(e.resign_date, 'yyyy') = ? ";
            }
            if (employeeId != null && !employeeId.trim().isEmpty()) {
                sql += "AND e.employee_id = ? ";
            }

            sql += "ORDER BY e.employee_no ASC";

            pstmt = conn.prepareStatement(sql);

            int paramIndex = 1;
            if (retirementYear != null && !retirementYear.trim().isEmpty()) {
                pstmt.setString(paramIndex++, retirementYear);
            }
            if (employeeId != null && !employeeId.trim().isEmpty()) {
                pstmt.setString(paramIndex++, employeeId);
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                RetirementMntModel model = new RetirementMntModel();
                model.setEmployeeId(rs.getString("employee_id"));
                model.setEmployeeNo(rs.getString("employee_no"));
                model.setEmployeeName(rs.getString("employee_name"));
                model.setHireDate(rs.getString("hire_date"));
                model.setResignDate(rs.getString("resign_date"));
                
                
                model.setRetirementSettlementYn(rs.getString("retirement_settlement_yn"));
                
                list.add(model);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }

    // 2. 기준일 바탕으로 최근 3개월 급여 내역 조회
    public List<MonthlyWage> getRecent3MonthsPayroll(Connection conn, String employeeId, String baseDate) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<MonthlyWage> list = new ArrayList<>();

        try {
            String sql = "SELECT e.EMPLOYEE_ID, "
                       + "       e.EMPLOYEE_NAME, "
                       + "       p.PAY_YEAR_MONTH, "
                       + "       p.PAY_SEQUENCE, "
                       + "       pe.NET_PAY_AMOUNT AS payment_amount " 
                       + "FROM PAYROLL p "
                       + "JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_ID = p.PAYROLL_ID "
                       + "JOIN EMPLOYEE e ON e.EMPLOYEE_ID = pe.EMPLOYEE_ID "
                       + "WHERE e.EMPLOYEE_ID = ? "
                       + "AND p.PAY_YEAR_MONTH BETWEEN TO_CHAR(ADD_MONTHS(TO_DATE(?, 'YYYY-MM-DD'), -2), 'YYYYMM') "
                       + "                         AND TO_CHAR(TO_DATE(?, 'YYYY-MM-DD'), 'YYYYMM') "
                       + "ORDER BY p.PAY_YEAR_MONTH ASC, p.PAY_SEQUENCE ASC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employeeId);
            pstmt.setString(2, baseDate);
            pstmt.setString(3, baseDate);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                MonthlyWage wage = new MonthlyWage();
                String yearMonth = rs.getString("PAY_YEAR_MONTH");
                if (yearMonth != null && yearMonth.length() == 6) {
                    yearMonth = yearMonth.substring(0, 4) + "-" + yearMonth.substring(4, 6);
                }
                wage.setWageMonth(yearMonth);
                wage.setPaymentAmount(rs.getLong("payment_amount"));
                list.add(wage);
            }
            return list;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }
 // 3. 퇴직급여 계산 결과 저장 (필수 컬럼 전체 반영)
    public int RetirementMntInsert(Connection conn, RetirementMntModel model) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "INSERT INTO retirement_pay ("
                       + "    retirement_pay_id, company_id, employee_id, "
                       + "    calc_start_date, calc_end_date, service_days, "
                       + "    total_wage_amount, average_daily_wage, retirement_pay_amount, "
                       + "    net_pay_amount, reg_id, mod_id, created_at, updated_at, "
                       + "    interim_settlement_yn, retirement_settlement_yn"
                       + ") VALUES ("
                       + "    retirement_pay_seq.NEXTVAL, 1, ?, "
                       + "    TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD'), ?, "
                       + "    ?, ?, ?, "
                       + "    ?, 'SYSTEM', 'SYSTEM', SYSDATE, SYSDATE, "
                       + "    'N', 'Y'"
                       + ")";

            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, model.getEmployeeId());
            pstmt.setString(2, model.getHireDate());        // calc_start_date (입사일)
            pstmt.setString(3, model.getResignDate());      // calc_end_date (퇴직일)
            pstmt.setInt(4, model.getServiceDays());        // service_days
            pstmt.setLong(5, model.getTotalWageAmount());   // total_wage_amount
            pstmt.setDouble(6, model.getAverageDailyWage());// average_daily_wage
            pstmt.setLong(7, model.getRetirementPayAmount());// retirement_pay_amount
            pstmt.setLong(8, model.getRetirementPayAmount());// net_pay_amount (실지급액은 우선 퇴직금과 동일하게 처리)

            return pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }

    
}