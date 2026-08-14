package payment.paymentRegisterList.service;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import payment.paymentRegisterList.dao.PaymentRegisterListDetailDAO;
import payment.paymentRegisterList.dto.PaymentRegisterListDetailDTO;
import payment.paymentRegisterList.dto.PaymentRegisterListDetailResult;
import payment.paymentRegisterList.dto.PaymentRegisterListItemDTO;

public class PaymentRegisterListDetailService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private PaymentRegisterListDetailDAO dao = new PaymentRegisterListDetailDAO();

    public PaymentRegisterListDetailResult getDetail(String payYear, String payMonth, int paySequence,
            String empType, String department, String incomeType) {

        String payYearMonth = payYear + payMonth;
        PaymentRegisterListDetailResult result = new PaymentRegisterListDetailResult();
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

            List<PaymentRegisterListItemDTO> payItemList = dao.selectPayItemList(conn);
            List<PaymentRegisterListItemDTO> deductionItemList = dao.selectDeductionItemList(conn);
            result.setPayItemList(payItemList);
            result.setDeductionItemList(deductionItemList);
            result.setDepartmentList(dao.selectDepartmentList(conn));

            Long payrollId = dao.selectPayrollId(conn, payYearMonth, paySequence);

            List<PaymentRegisterListDetailDTO> employeeList;
            if (payrollId == null) {
                employeeList = new java.util.ArrayList<>();
            } else {
                employeeList = dao.selectEmployeeList(conn, payrollId, empType, department, incomeType);
                Map<Long, Map<Long, Long>> payDetails = dao.selectPayDetailsByPayroll(conn, payrollId);
                Map<Long, Map<Long, Long>> deductionDetails = dao.selectDeductionDetailsByPayroll(conn, payrollId);
                for (PaymentRegisterListDetailDTO emp : employeeList) {
                    Map<Long, Long> payMap = payDetails.get(emp.getPayrollEmployeeId());
                    if (payMap != null) { emp.setPayAmountByItemId(payMap); }
                    Map<Long, Long> dedMap = deductionDetails.get(emp.getPayrollEmployeeId());
                    if (dedMap != null) { emp.setDeductionAmountByItemId(dedMap); }
                }
            }
            result.setEmployeeList(employeeList);
            result.setTotalRow(buildTotalRow(employeeList, payItemList, deductionItemList));
        } catch (Exception e) {
            throw new RuntimeException("급여대장 상세 조회 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }

        return result;
    }

    private PaymentRegisterListDetailDTO buildTotalRow(List<PaymentRegisterListDetailDTO> employeeList,
            List<PaymentRegisterListItemDTO> payItemList, List<PaymentRegisterListItemDTO> deductionItemList) {
        PaymentRegisterListDetailDTO total = new PaymentRegisterListDetailDTO();
        Map<Long, Long> payTotals = new HashMap<>();
        Map<Long, Long> dedTotals = new HashMap<>();

        for (PaymentRegisterListItemDTO item : payItemList) { payTotals.put(item.getItemId(), 0L); }
        for (PaymentRegisterListItemDTO item : deductionItemList) { dedTotals.put(item.getItemId(), 0L); }

        for (PaymentRegisterListDetailDTO emp : employeeList) {
            for (PaymentRegisterListItemDTO item : payItemList) {
                payTotals.merge(item.getItemId(), emp.getPayAmount(item.getItemId()), Long::sum);
            }
            for (PaymentRegisterListItemDTO item : deductionItemList) {
                dedTotals.merge(item.getItemId(), emp.getDeductionAmount(item.getItemId()), Long::sum);
            }
            total.setTotalPayAmount(total.getTotalPayAmount() + emp.getTotalPayAmount());
            total.setTotalDeductionAmount(total.getTotalDeductionAmount() + emp.getTotalDeductionAmount());
            total.setNetPayAmount(total.getNetPayAmount() + emp.getNetPayAmount());
        }

        total.setPayAmountByItemId(payTotals);
        total.setDeductionAmountByItemId(dedTotals);
        return total;
    }
}
