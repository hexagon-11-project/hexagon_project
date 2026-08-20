package payment.paymentRegisterList.service;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import payment.paymentRegisterList.dao.PaymentRegisterListDAO;
import payment.paymentRegisterList.dto.PaymentRegisterListDTO;

public class PaymentRegisterListService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private PaymentRegisterListDAO dao = new PaymentRegisterListDAO();

    /** 귀속연도의 1~12월 급여-01차 목록. 아직 급여가 등록되지 않은 달도 0건으로 항상 채워서 반환하고,
     *  정산기간/지급일은 PAYROLL에 저장된 값과 무관하게 귀속연월 기준(그 달 1일~말일, 다음달 5일)으로 항상 계산한다. */
    public List<PaymentRegisterListDTO> getPayrollList(String payYear) {
        Map<String, PaymentRegisterListDTO> dbMap = new HashMap<>();
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            for (PaymentRegisterListDTO dto : dao.selectPayrollSummaryByYear(conn, payYear)) {
                dbMap.put(dto.getPayYearMonth(), dto);
            }
        } catch (Exception e) {
            throw new RuntimeException("급여대장 목록 조회 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }

        int year = Integer.parseInt(payYear);
        List<PaymentRegisterListDTO> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            String monthStr = String.format("%02d", month);
            String payYearMonth = payYear + monthStr;

            PaymentRegisterListDTO dto = dbMap.get(payYearMonth);
            if (dto == null) {
                dto = new PaymentRegisterListDTO();
                dto.setPayYearMonth(payYearMonth);
                dto.setPaySequence(1);
            }

            LocalDate start = LocalDate.of(year, month, 1);
            LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
            LocalDate payDate = start.plusMonths(1).withDayOfMonth(5);
            dto.setSettlementStartDate(start.format(DATE_FMT));
            dto.setSettlementEndDate(end.format(DATE_FMT));
            dto.setPaymentDate(payDate.format(DATE_FMT));

            result.add(dto);
        }
        return result;
    }

    public void deletePayroll(Long payrollId) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            dao.deletePayroll(conn, payrollId);
            conn.commit();
        } catch (Exception e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException("급여차수 삭제 중 오류 발생", e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
}
