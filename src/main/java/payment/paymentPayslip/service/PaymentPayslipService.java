package payment.paymentPayslip.service;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import payment.paymentPayslip.dao.PaymentPayslipDAO;
import payment.paymentPayslip.dto.PaymentPayslipDetailDTO;
import payment.paymentPayslip.dto.PaymentPayslipItemDTO;
import payment.paymentPayslip.dto.PaymentPayslipResult;

public class PaymentPayslipService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private PaymentPayslipDAO dao = new PaymentPayslipDAO();

    public PaymentPayslipResult getPayslipData(String payYear, String payMonth, int paySequence) {
        String payYearMonth = payYear + payMonth;
        PaymentPayslipResult result = new PaymentPayslipResult();
        result.setPayYearMonth(payYearMonth);
        result.setPaySequence(paySequence);

        int year = Integer.parseInt(payYear);
        int month = Integer.parseInt(payMonth);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        LocalDate payDate = start.plusMonths(1).withDayOfMonth(5);
        result.setSettlementStartDate(start.format(DATE_FMT));
        result.setSettlementEndDate(end.format(DATE_FMT));
        result.setPaymentDate(payDate.format(DATE_FMT));

        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();

            Long payrollId = dao.selectPayrollId(conn, payYearMonth, paySequence);

            List<PaymentPayslipDetailDTO> employeeList;
            if (payrollId == null) {
                employeeList = new ArrayList<>();
            } else {
                employeeList = dao.selectEmployeeList(conn, payrollId);
                Map<Long, List<PaymentPayslipItemDTO>> payItems = dao.selectPayItemsByPayroll(conn, payrollId);
                Map<Long, List<PaymentPayslipItemDTO>> deductionItems = dao.selectDeductionItemsByPayroll(conn, payrollId);
                // 일용직은 지급내역이 PAYROLL_PAY_DETAIL이 아니라 DAILY_WORK_RECORD에 저장되고,
                // 원천징수 세액(소득세/지방소득세)도 PAYROLL_DEDUCTION_DETAIL이 아니라 그 안에 함께 저장된다.
                Map<Long, PaymentPayslipItemDTO> dailyPay = dao.selectDailyPayByPayroll(conn, payrollId);
                Map<Long, List<PaymentPayslipItemDTO>> dailyTax = dao.selectDailyTaxDeductionsByPayroll(conn, payrollId);
                for (PaymentPayslipDetailDTO emp : employeeList) {
                    List<PaymentPayslipItemDTO> payList = payItems.get(emp.getPayrollEmployeeId());
                    if (payList != null) { emp.setPayItems(payList); }
                    List<PaymentPayslipItemDTO> dedList = deductionItems.get(emp.getPayrollEmployeeId());
                    if (dedList != null) { emp.setDeductionItems(new ArrayList<>(dedList)); }

                    if (isDailyWorker(emp.getEmploymentType())) {
                        PaymentPayslipItemDTO dailyPayItem = dailyPay.get(emp.getPayrollEmployeeId());
                        if (dailyPayItem != null) {
                            List<PaymentPayslipItemDTO> combinedPay = new ArrayList<>(emp.getPayItems());
                            combinedPay.add(dailyPayItem);
                            emp.setPayItems(combinedPay);
                        }
                        List<PaymentPayslipItemDTO> taxList = dailyTax.get(emp.getPayrollEmployeeId());
                        if (taxList != null) {
                            List<PaymentPayslipItemDTO> combinedDed = new ArrayList<>(emp.getDeductionItems());
                            combinedDed.addAll(taxList);
                            emp.setDeductionItems(combinedDed);
                        }
                    }
                }
            }
            result.setEmployeeList(employeeList);
        } catch (Exception e) {
            throw new RuntimeException("급여명세서 조회 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }

        return result;
    }

    private boolean isDailyWorker(String employmentType) {
        return "일용직".equals(employmentType) || "DAILY".equals(employmentType);
    }

    /** 사원별 상세를 JS에서 바로 꺼내 쓸 수 있도록 payrollEmployeeId를 key로 하는 JSON 문자열로 조립한다. */
    public String buildEmployeeDetailJson(List<PaymentPayslipDetailDTO> employeeList) {
        StringBuilder json = new StringBuilder("{");
        for (int i = 0; i < employeeList.size(); i++) {
            PaymentPayslipDetailDTO emp = employeeList.get(i);
            if (i > 0) { json.append(","); }
            json.append("\"").append(emp.getPayrollEmployeeId()).append("\":{");
            json.append("\"name\":").append(jsonString(emp.getEmployeeName())).append(",");
            json.append("\"residentRegNo\":").append(jsonString(emp.getResidentRegNo())).append(",");
            json.append("\"department\":").append(jsonString(emp.getDepartment())).append(",");
            json.append("\"position\":").append(jsonString(emp.getPosition())).append(",");
            json.append("\"hireDate\":").append(jsonString(emp.getHireDate())).append(",");
            json.append("\"payItems\":").append(jsonItemArray(emp.getPayItems())).append(",");
            json.append("\"totalPay\":").append(emp.getTotalPayAmount()).append(",");
            json.append("\"dedItems\":").append(jsonItemArray(emp.getDeductionItems())).append(",");
            json.append("\"totalDed\":").append(emp.getTotalDeductionAmount()).append(",");
            json.append("\"netPay\":").append(emp.getNetPayAmount());
            json.append("}");
        }
        json.append("}");
        return json.toString();
    }

    private String jsonItemArray(List<PaymentPayslipItemDTO> items) {
        StringBuilder sb = new StringBuilder("[");
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                PaymentPayslipItemDTO item = items.get(i);
                if (i > 0) { sb.append(","); }
                sb.append("{")
                  .append("\"name\":").append(jsonString(item.getItemName())).append(",")
                  .append("\"amount\":").append(item.getAmount()).append(",")
                  .append("\"calc\":").append(jsonString(item.getCalculationMethod()))
                  .append("}");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String jsonString(String value) {
        if (value == null) { return "null"; }
        String escaped = value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "").replace("\n", "\\n");
        return "\"" + escaped + "\"";
    }
}
