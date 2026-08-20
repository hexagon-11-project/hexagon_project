package payment.paymentRegisterList.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import payment.paymentRegisterList.dto.PaymentRegisterListDetailDTO;
import payment.paymentRegisterList.dto.PaymentRegisterListItemDTO;

// 급여대장 상세화면(사원별 지급/공제 내역) 조회용 DAO
public class PaymentRegisterListDetailDAO {

    // 사원등록 화면은 EMPLOYMENT_TYPE을 한글로만 저장하지만, 일부 기존 데이터는 영문 코드(REGULAR/CONTRACT/DAILY 등)로
    // 들어가 있어(paymentMnt/paymentRegisterList의 '일용직','DAILY' 동시 처리와 동일한 이유) 구분 필터가 한글 값과만
    // 비교하면 그 사원들이 걸러지지 않는다. 알려진 영문 코드가 있는 구분은 함께 매칭해준다.
    private static final Map<String, String[]> EMP_TYPE_ALIASES = new HashMap<>();
    static {
        EMP_TYPE_ALIASES.put("정규직", new String[] { "정규직", "REGULAR" });
        EMP_TYPE_ALIASES.put("계약직", new String[] { "계약직", "CONTRACT" });
        EMP_TYPE_ALIASES.put("일용직", new String[] { "일용직", "DAILY" });
    }

    private List<String> expandEmploymentTypeAliases(String empType) {
        if (empType == null || empType.trim().isEmpty()) { return null; }
        String[] aliases = EMP_TYPE_ALIASES.get(empType);
        return aliases != null ? Arrays.asList(aliases) : Arrays.asList(empType);
    }

    private String placeholders(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) { sb.append(","); }
            sb.append("?");
        }
        return sb.toString();
    }

    /** 귀속연월+급여차수에 해당하는 PAYROLL_ID 조회 (등록된 급여가 없으면 null) */
    public Long selectPayrollId(Connection conn, String payYearMonth, int paySequence) throws SQLException {
        String sql = "SELECT PAYROLL_ID FROM PAYROLL WHERE PAY_YEAR_MONTH = ? AND PAY_SEQUENCE = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payYearMonth);
            pstmt.setInt(2, paySequence);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getLong("PAYROLL_ID") : null;
            }
        }
    }

    public List<PaymentRegisterListItemDTO> selectPayItemList(Connection conn) throws SQLException {
        return selectItemList(conn, "SELECT PAY_ITEM_ID AS ITEM_ID, PAY_ITEM_NAME AS ITEM_NAME FROM PAY_ITEM WHERE USE_YN = 'Y' ORDER BY DISPLAY_ORDER");
    }

    public List<PaymentRegisterListItemDTO> selectDeductionItemList(Connection conn) throws SQLException {
        return selectItemList(conn, "SELECT DEDUCTION_ITEM_ID AS ITEM_ID, DEDUCTION_ITEM_NAME AS ITEM_NAME FROM DEDUCTION_ITEM WHERE USE_YN = 'Y' ORDER BY DISPLAY_ORDER");
    }

    private List<PaymentRegisterListItemDTO> selectItemList(Connection conn, String sql) throws SQLException {
        List<PaymentRegisterListItemDTO> list = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                PaymentRegisterListItemDTO dto = new PaymentRegisterListItemDTO();
                dto.setItemId(rs.getLong("ITEM_ID"));
                dto.setItemName(rs.getString("ITEM_NAME"));
                list.add(dto);
            }
        }
        return list;
    }

    public List<String> selectDepartmentList(Connection conn) throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT DEPARTMENT FROM EMPLOYEE WHERE DEPARTMENT IS NOT NULL ORDER BY DEPARTMENT";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) { list.add(rs.getString("DEPARTMENT")); }
        }
        return list;
    }

    /** 급여차수에 속한 사원 목록 (구분/부서/소득유형 필터 적용). 지급/공제 상세는 별도 조회 후 Service에서 합쳐준다. */
    public List<PaymentRegisterListDetailDTO> selectEmployeeList(Connection conn, Long payrollId,
            String empType, String department, String incomeType) throws SQLException {
        List<PaymentRegisterListDetailDTO> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT pe.PAYROLL_EMPLOYEE_ID, e.EMPLOYMENT_TYPE, e.EMPLOYEE_NAME, e.HIRE_DATE, e.DEPARTMENT, e.POSITION, "
              + "pe.TOTAL_PAY_AMOUNT, pe.TOTAL_DEDUCTION_AMOUNT, pe.NET_PAY_AMOUNT "
              + "FROM PAYROLL_EMPLOYEE pe JOIN EMPLOYEE e ON e.EMPLOYEE_ID = pe.EMPLOYEE_ID "
              + "WHERE pe.PAYROLL_ID = ? "
              + "  AND (e.EMPLOYMENT_TYPE NOT IN ('일용직','DAILY') "
              + "       OR EXISTS (SELECT 1 FROM DAILY_WORK_RECORD d WHERE d.PAYROLL_EMPLOYEE_ID = pe.PAYROLL_EMPLOYEE_ID)) ");

        List<String> empTypeAliases = expandEmploymentTypeAliases(empType);
        if (empTypeAliases != null) {
            sql.append(" AND e.EMPLOYMENT_TYPE IN (").append(placeholders(empTypeAliases.size())).append(") ");
        }
        if (department != null && !department.trim().isEmpty()) {
            sql.append(" AND e.DEPARTMENT = ? ");
        }
        if ("일용근로자".equals(incomeType)) {
            sql.append(" AND e.EMPLOYMENT_TYPE IN ('일용직','DAILY') ");
        } else if (incomeType != null && !incomeType.trim().isEmpty()) {
            sql.append(" AND e.EMP_INCOME_TYPE LIKE ? ");
        }
        sql.append(" ORDER BY e.EMPLOYEE_NAME");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            pstmt.setLong(idx++, payrollId);
            if (empTypeAliases != null) {
                for (String alias : empTypeAliases) { pstmt.setString(idx++, alias); }
            }
            if (department != null && !department.trim().isEmpty()) { pstmt.setString(idx++, department); }
            if (incomeType != null && !incomeType.trim().isEmpty() && !"일용근로자".equals(incomeType)) {
                pstmt.setString(idx++, incomeType + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentRegisterListDetailDTO dto = new PaymentRegisterListDetailDTO();
                    dto.setPayrollEmployeeId(rs.getLong("PAYROLL_EMPLOYEE_ID"));
                    dto.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
                    dto.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
                    dto.setHireDate(rs.getString("HIRE_DATE"));
                    dto.setDepartment(rs.getString("DEPARTMENT"));
                    dto.setPosition(rs.getString("POSITION"));
                    dto.setTotalPayAmount(rs.getLong("TOTAL_PAY_AMOUNT"));
                    dto.setTotalDeductionAmount(rs.getLong("TOTAL_DEDUCTION_AMOUNT"));
                    dto.setNetPayAmount(rs.getLong("NET_PAY_AMOUNT"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    /** payrollId에 속한 모든 사원의 지급상세를 한 번에 조회: key = PAYROLL_EMPLOYEE_ID, value = (PAY_ITEM_ID -> AMOUNT) */
    public Map<Long, Map<Long, Long>> selectPayDetailsByPayroll(Connection conn, Long payrollId) throws SQLException {
        Map<Long, Map<Long, Long>> result = new HashMap<>();
        String sql = "SELECT d.PAYROLL_EMPLOYEE_ID, d.PAY_ITEM_ID, d.AMOUNT "
                   + "FROM PAYROLL_PAY_DETAIL d JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_EMPLOYEE_ID = d.PAYROLL_EMPLOYEE_ID "
                   + "WHERE pe.PAYROLL_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long payrollEmployeeId = rs.getLong("PAYROLL_EMPLOYEE_ID");
                    result.computeIfAbsent(payrollEmployeeId, k -> new HashMap<>())
                          .put(rs.getLong("PAY_ITEM_ID"), rs.getLong("AMOUNT"));
                }
            }
        }
        return result;
    }

    /** payrollId에 속한 모든 사원의 공제상세를 한 번에 조회: key = PAYROLL_EMPLOYEE_ID, value = (DEDUCTION_ITEM_ID -> AMOUNT) */
    public Map<Long, Map<Long, Long>> selectDeductionDetailsByPayroll(Connection conn, Long payrollId) throws SQLException {
        Map<Long, Map<Long, Long>> result = new HashMap<>();
        String sql = "SELECT d.PAYROLL_EMPLOYEE_ID, d.DEDUCTION_ITEM_ID, d.AMOUNT "
                   + "FROM PAYROLL_DEDUCTION_DETAIL d JOIN PAYROLL_EMPLOYEE pe ON pe.PAYROLL_EMPLOYEE_ID = d.PAYROLL_EMPLOYEE_ID "
                   + "WHERE pe.PAYROLL_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long payrollEmployeeId = rs.getLong("PAYROLL_EMPLOYEE_ID");
                    result.computeIfAbsent(payrollEmployeeId, k -> new HashMap<>())
                          .put(rs.getLong("DEDUCTION_ITEM_ID"), rs.getLong("AMOUNT"));
                }
            }
        }
        return result;
    }
}
