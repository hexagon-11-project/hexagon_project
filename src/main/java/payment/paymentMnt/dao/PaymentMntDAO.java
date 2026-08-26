package payment.paymentMnt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import payment.paymentMnt.dto.PaymentMntDailyWorkSummary;
import payment.paymentMnt.dto.PaymentMntDeductionDetailDTO;
import payment.paymentMnt.dto.PaymentMntEffectiveDetail;
import payment.paymentMnt.dto.PaymentMntEmployeeDTO;
import payment.paymentMnt.dto.PaymentMntPayDetailDTO;
import payment.paymentMnt.dto.PaymentMntPayItemDTO;
import payment.paymentMnt.dto.PaymentMntDeductionItemDTO;
import payment.paymentMnt.dto.PaymentMntSummaryDTO;

public class PaymentMntDAO {

    public List<PaymentMntEmployeeDTO> getPayrollEmployeeList(Connection conn, String payYearMonth, int paySequence) throws SQLException {
        List<PaymentMntEmployeeDTO> list = new ArrayList<>();
        // 일용직/DAILY 사원은 급여입력관리 화면 대상이 아니므로(별도 일용직 급여 화면에서 관리) 항상 제외한다. / 日雇い/DAILY社員は給与入力管理画面の対象外のため（別途日雇い給与画面で管理）常に除外する。
        String sql = "SELECT p.payroll_employee_id, p.payroll_id, p.employee_id, e.employee_name, e.employment_type, e.department,"
                   + "p.total_pay_amount, p.total_deduction_amount, p.net_pay_amount "
                   + "FROM PAYROLL_EMPLOYEE p JOIN EMPLOYEE e ON p.employee_id = e.employee_id "
                   + "JOIN PAYROLL pr ON p.payroll_id = pr.payroll_id "
                   + "WHERE pr.pay_year_month = ? AND pr.pay_sequence = ? "
                   + "  AND e.employment_type NOT IN ('일용직','DAILY') "
                   + "ORDER BY e.employee_name";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payYearMonth);
            pstmt.setInt(2, paySequence);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntEmployeeDTO dto = new PaymentMntEmployeeDTO();
                    dto.setPayrollEmployeeId(rs.getLong("payroll_employee_id"));
                    dto.setPayrollId(rs.getLong("payroll_id"));
                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setEmploymentType(rs.getString("employment_type"));
                    dto.setDepartment(rs.getString("department"));
                    
                    // 금액 3종 세팅 / 金額3種を設定
                    dto.setTotalPayAmount(rs.getLong("total_pay_amount"));
                    dto.setTotalDeductionAmount(rs.getLong("total_deduction_amount"));
                    dto.setNetPayAmount(rs.getLong("net_pay_amount"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    public List<PaymentMntEmployeeDTO> selectEmployeeList(Connection conn, Long payrollId) throws SQLException {
        List<PaymentMntEmployeeDTO> list = new ArrayList<>();
        // 일용직/DAILY 사원은 급여입력관리 화면 대상이 아니므로(별도 일용직 급여 화면에서 관리) 항상 제외한다. / 日雇い/DAILY社員は給与入力管理画面の対象外のため（別途日雇い給与画面で管理）常に除外する。
        String sql = "SELECT p.payroll_employee_id, p.payroll_id, p.employee_id, e.employee_name, e.employment_type, "
                   + "e.department, "
                   + "p.total_pay_amount, p.total_deduction_amount, p.net_pay_amount "
                   + "FROM PAYROLL_EMPLOYEE p JOIN EMPLOYEE e ON p.employee_id = e.employee_id "
                   + "WHERE p.payroll_id = ? "
                   + "  AND e.employment_type NOT IN ('일용직','DAILY') "
                   + "ORDER BY e.employee_name";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntEmployeeDTO dto = new PaymentMntEmployeeDTO();
                    dto.setPayrollEmployeeId(rs.getLong("payroll_employee_id"));
                    dto.setPayrollId(rs.getLong("payroll_id"));
                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setEmploymentType(rs.getString("employment_type"));
                    dto.setDepartment(rs.getString("department"));

                    // 금액 3종 세팅 / 金額3種を設定
                    dto.setTotalPayAmount(rs.getLong("total_pay_amount"));
                    dto.setTotalDeductionAmount(rs.getLong("total_deduction_amount"));
                    dto.setNetPayAmount(rs.getLong("net_pay_amount"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    /** 방금 신규추가(또는 지정)한 사원들만 조회 - 전체 새로고침 없이 해당 행만 화면에 바로 붙이기 위함
     *  今登録（または指定）した社員だけを照会 - 全体再読み込みなしで該当行だけ画面に追加するため */
    public List<PaymentMntEmployeeDTO> selectEmployeesByEmployeeIds(Connection conn, Long payrollId, List<String> empIds) throws SQLException {
        List<PaymentMntEmployeeDTO> list = new ArrayList<>();
        if (empIds == null || empIds.isEmpty()) return list;

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < empIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String sql = "SELECT p.payroll_employee_id, p.payroll_id, p.employee_id, e.employee_name, e.employment_type, "
                   + "e.department, "
                   + "p.total_pay_amount, p.total_deduction_amount, p.net_pay_amount "
                   + "FROM PAYROLL_EMPLOYEE p JOIN EMPLOYEE e ON p.employee_id = e.employee_id "
                   + "WHERE p.payroll_id = ? AND p.employee_id IN (" + placeholders + ") "
                   + "ORDER BY e.employee_name";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int idx = 1;
            pstmt.setLong(idx++, payrollId);
            for (String empId : empIds) {
                pstmt.setString(idx++, empId.trim());
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntEmployeeDTO dto = new PaymentMntEmployeeDTO();
                    dto.setPayrollEmployeeId(rs.getLong("payroll_employee_id"));
                    dto.setPayrollId(rs.getLong("payroll_id"));
                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setEmploymentType(rs.getString("employment_type"));
                    dto.setDepartment(rs.getString("department"));
                    dto.setTotalPayAmount(rs.getLong("total_pay_amount"));
                    dto.setTotalDeductionAmount(rs.getLong("total_deduction_amount"));
                    dto.setNetPayAmount(rs.getLong("net_pay_amount"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    public List<PaymentMntPayDetailDTO> selectPayDetails(Connection conn, Long payrollEmployeeId) throws SQLException {
        List<PaymentMntPayDetailDTO> list = new ArrayList<>();
        String sql = "SELECT d.payroll_pay_detail_id, d.payroll_employee_id, d.pay_item_id, i.pay_item_name as item_name, d.amount "
                   + "FROM PAYROLL_PAY_DETAIL d JOIN PAY_ITEM i ON d.pay_item_id = i.pay_item_id "
                   + "WHERE d.payroll_employee_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollEmployeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntPayDetailDTO dto = new PaymentMntPayDetailDTO();
                    dto.setPayrollPayDetailId(rs.getLong("payroll_pay_detail_id"));
                    dto.setPayrollEmployeeId(rs.getLong("payroll_employee_id"));
                    dto.setPayItemId(rs.getLong("pay_item_id"));
                    dto.setItemName(rs.getString("item_name"));
                    dto.setAmount(rs.getLong("amount"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    public List<PaymentMntDeductionDetailDTO> selectDeductionDetails(Connection conn, Long payrollEmployeeId) throws SQLException {
        List<PaymentMntDeductionDetailDTO> list = new ArrayList<>();
        String sql = "SELECT PAYROLL_DEDUCTION_DETAIL_ID, PAYROLL_EMPLOYEE_ID, DEDUCTION_ITEM_ID, AMOUNT "
                   + "FROM PAYROLL_DEDUCTION_DETAIL "
                   + "WHERE PAYROLL_EMPLOYEE_ID = ?";
                   
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollEmployeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntDeductionDetailDTO dto = new PaymentMntDeductionDetailDTO();
                    dto.setPayrollDeductionDetailId(rs.getLong("PAYROLL_DEDUCTION_DETAIL_ID"));
                    dto.setPayrollEmployeeId(rs.getLong("PAYROLL_EMPLOYEE_ID"));
                    dto.setDeductionItemId(rs.getLong("DEDUCTION_ITEM_ID"));
                    dto.setAmount(rs.getLong("AMOUNT"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    /** 이 사원별급여(payrollEmployeeId)에 저장된 공제내역이 없을 때, 같은 사원의 다른 급여차수 중
     *  공제내역이 저장돼 있는 가장 최근 것을 대신 보여주기 위한 조회.
     *  (급여차수마다 공제항목을 매번 새로 입력할 필요 없이, 최근에 입력해둔 값을 기본값으로 채워주기 위함)
     *  この社員別給与（payrollEmployeeId）に保存された控除内訳がない場合、同じ社員の他の給与回のうち
     *  控除内訳が保存されている最新のものを代わりに表示するための照会。
     *  （給与回ごとに控除項目を毎回新規入力する必要がないよう、直近に入力した値を初期値として埋めるため） */
    public List<PaymentMntDeductionDetailDTO> selectDefaultDeductionDetails(Connection conn, Long payrollEmployeeId) throws SQLException {
        List<PaymentMntDeductionDetailDTO> list = new ArrayList<>();
        String sql = "SELECT PAYROLL_DEDUCTION_DETAIL_ID, PAYROLL_EMPLOYEE_ID, DEDUCTION_ITEM_ID, AMOUNT "
                   + "FROM PAYROLL_DEDUCTION_DETAIL "
                   + "WHERE PAYROLL_EMPLOYEE_ID = ( "
                   + "  SELECT PAYROLL_EMPLOYEE_ID FROM ( "
                   + "    SELECT pe2.PAYROLL_EMPLOYEE_ID "
                   + "    FROM PAYROLL_EMPLOYEE pe2 JOIN PAYROLL p2 ON pe2.PAYROLL_ID = p2.PAYROLL_ID "
                   + "    WHERE pe2.EMPLOYEE_ID = (SELECT pe1.EMPLOYEE_ID FROM PAYROLL_EMPLOYEE pe1 WHERE pe1.PAYROLL_EMPLOYEE_ID = ?) "
                   + "      AND EXISTS (SELECT 1 FROM PAYROLL_DEDUCTION_DETAIL dd WHERE dd.PAYROLL_EMPLOYEE_ID = pe2.PAYROLL_EMPLOYEE_ID) "
                   + "    ORDER BY p2.PAY_YEAR_MONTH DESC, p2.PAY_SEQUENCE DESC "
                   + "  ) WHERE ROWNUM = 1 "
                   + ")";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollEmployeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntDeductionDetailDTO dto = new PaymentMntDeductionDetailDTO();
                    dto.setPayrollDeductionDetailId(rs.getLong("PAYROLL_DEDUCTION_DETAIL_ID"));
                    dto.setPayrollEmployeeId(payrollEmployeeId);
                    dto.setDeductionItemId(rs.getLong("DEDUCTION_ITEM_ID"));
                    dto.setAmount(rs.getLong("AMOUNT"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    /** 일용직(일용직/DAILY)이면 DAILY_WORK_RECORD(일자별 근무기록) 합계(없으면 0, recordCount=0)를 반환하고,
     *  일용직이 아니면 null을 반환. 급여입력/관리 일반 화면에서 사원 클릭 시 지급/공제 상세를 자동으로 채우기 위함.
     *  日雇い（일용직/DAILY）であればDAILY_WORK_RECORD（日別勤務記録）の合計（なければ0、recordCount=0）を返し、
     *  日雇いでなければnullを返す。給与入力・管理の一般画面で社員クリック時に支給・控除詳細を自動で埋めるため。 */
    public PaymentMntDailyWorkSummary selectDailyWorkSummary(Connection conn, Long payrollEmployeeId) throws SQLException {
        String sql = "SELECT e.EMPLOYMENT_TYPE, "
                + "  NVL(SUM(d.PAY_AMOUNT), 0) AS SUM_PAY, "
                + "  NVL(SUM(d.INCOME_TAX_AMOUNT), 0) AS SUM_INCOME_TAX, "
                + "  NVL(SUM(d.LOCAL_INCOME_TAX_AMOUNT), 0) AS SUM_LOCAL_TAX, "
                + "  COUNT(d.DAILY_WORK_RECORD_ID) AS REC_CNT "
                + "FROM PAYROLL_EMPLOYEE pe JOIN EMPLOYEE e ON pe.EMPLOYEE_ID = e.EMPLOYEE_ID "
                + "LEFT JOIN DAILY_WORK_RECORD d ON d.PAYROLL_EMPLOYEE_ID = pe.PAYROLL_EMPLOYEE_ID "
                + "WHERE pe.PAYROLL_EMPLOYEE_ID = ? "
                + "GROUP BY e.EMPLOYMENT_TYPE";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollEmployeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;

                String employmentType = rs.getString("EMPLOYMENT_TYPE");
                boolean isDaily = "일용직".equals(employmentType) || "DAILY".equals(employmentType);
                if (!isDaily) return null;

                PaymentMntDailyWorkSummary summary = new PaymentMntDailyWorkSummary();
                summary.sumPay = rs.getLong("SUM_PAY");
                summary.sumIncomeTax = rs.getLong("SUM_INCOME_TAX");
                summary.sumLocalTax = rs.getLong("SUM_LOCAL_TAX");
                summary.recordCount = rs.getLong("REC_CNT");
                return summary;
            }
        }
    }

    /** 사원의 EMPLOYEE.BASE_WAGE_AMOUNT(기본급) 조회. 기본급은 급여차수마다 새로 입력할 값이 아니라
     *  사원 마스터에 고정된 값이므로, 이 급여차수에 저장된 상세가 없을 때 기본값으로 채우기 위함.
     *  社員のEMPLOYEE.BASE_WAGE_AMOUNT（基本給）を照会。基本給は給与回ごとに新規入力する値ではなく
     *  社員マスタに固定された値のため、この給与回に保存された詳細がない時に初期値として埋めるため。 */
    public Long selectEmployeeBaseWageAmount(Connection conn, Long payrollEmployeeId) throws SQLException {
        String sql = "SELECT e.BASE_WAGE_AMOUNT FROM PAYROLL_EMPLOYEE pe "
                + "JOIN EMPLOYEE e ON pe.EMPLOYEE_ID = e.EMPLOYEE_ID "
                + "WHERE pe.PAYROLL_EMPLOYEE_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, payrollEmployeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long amount = rs.getLong(1);
                    return rs.wasNull() ? null : amount;
                }
            }
        }
        return null;
    }

    /** 공제항목 마스터에서 이름으로 DEDUCTION_ITEM_ID 조회 (없으면 null)
     *  控除項目マスタから名前でDEDUCTION_ITEM_IDを照会（なければnull） */
    public Long selectDeductionItemIdByName(Connection conn, String deductionItemName) throws SQLException {
        String sql = "SELECT DEDUCTION_ITEM_ID FROM DEDUCTION_ITEM WHERE DEDUCTION_ITEM_NAME = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, deductionItemName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    return rs.wasNull() ? null : id;
                }
            }
        }
        return null;
    }

    /** 사원별급여(payrollEmployeeId) 하나의 "최종" 지급/공제 상세를 계산한다 - 저장된 값에 기본값 보정
     *  (일용직은 근무기록 합계/기본급 대체로 '일용급여', 그 외는 '기본급', 공제내역은 최근 저장분 이월)까지 반영.
     *  급여입력/관리 화면의 좌측 사원목록 총액과 우측 상세패널 총액이 항상 일치하도록, 두 곳 모두 이 메서드 하나만 쓴다.
     *  ★ 여기서 보정(대체/기본값 채움)된 항목은 화면에 "저장된 것처럼" 보이므로, 실제로도 즉시 DB에 반영해둔다.
     *  그렇지 않으면 급여대장/지난급여 불러오기처럼 PAYROLL_PAY_DETAIL을 직접 조회하는 화면에서는 0으로 보이는
     *  화면-DB 불일치가 생긴다.
     *  社員別給与（payrollEmployeeId）1件の「最終」支給・控除詳細を計算する - 保存された値に初期値補正
     *  （日雇いは勤務記録合計・基本給代替で「日雇い給与」、それ以外は「基本給」、控除内訳は直近保存分を繰越）まで反映。
     *  給与入力・管理画面の左側社員一覧総額と右側詳細パネル総額が常に一致するよう、両方でこのメソッドだけを使う。
     *  ★ ここで補正（代替・初期値埋め）された項目は画面上「保存されたように」見えるため、実際にも即座にDBへ反映しておく。
     *  そうしないと給与台帳・前回給与読み込みのようにPAYROLL_PAY_DETAILを直接照会する画面では0に見える
     *  画面-DB不一致が発生する。 */
    public PaymentMntEffectiveDetail computeEffectiveDetail(Connection conn, Long payrollEmployeeId) throws SQLException {
        return computeEffectiveDetail(conn, payrollEmployeeId, selectPayItemBulkDefaults(conn));
    }

    /** computeEffectiveDetail과 동일하지만, 지급항목 마스터 기본값(PAY_ITEM 전체 조회 결과)을 호출부에서
     *  미리 조회해 넘겨받는다. 사원 여러 명을 반복 처리하는 화면(급여입력 목록, 지난급여 불러오기)에서
     *  사원마다 이 마스터 조회를 반복하지 않도록 루프 밖에서 한 번만 조회해 재사용하기 위함.
     *  computeEffectiveDetailと同一だが、支給項目マスタ初期値（PAY_ITEM全件照会結果）を呼び出し側で
     *  あらかじめ照会して受け取る。複数の社員を繰り返し処理する画面（給与入力一覧、前回給与読み込み）で
     *  社員ごとにこのマスタ照会を繰り返さないよう、ループの外で一度だけ照会して再利用するため。 */
    public PaymentMntEffectiveDetail computeEffectiveDetail(Connection conn, Long payrollEmployeeId, Map<Long, Long> bulkDefaults) throws SQLException {
        List<PaymentMntPayDetailDTO> payDetails = selectPayDetails(conn, payrollEmployeeId);
        List<PaymentMntDeductionDetailDTO> deductionDetails = selectDeductionDetails(conn, payrollEmployeeId);
        boolean deductionCarriedForward = (deductionDetails == null || deductionDetails.isEmpty());
        if (deductionCarriedForward) {
            deductionDetails = selectDefaultDeductionDetails(conn, payrollEmployeeId);
        }

        Set<Long> touchedPayItemIds = new HashSet<>();
        Set<Long> touchedDeductionItemIds = new HashSet<>();

        // ★ 버그 수정: 이번 급여차수에 공제상세가 하나도 저장 안 되어있어 직전 저장분을 이월해서 화면에만
        //   보여주는 경우(selectDefaultDeductionDetails), 이 항목들이 "touched"로 표시되지 않아 화면엔
        //   보이는데 PAYROLL_DEDUCTION_DETAIL에는 끝내 저장되지 않는 문제가 있었다. 이월해서 보여준
        //   이상 실제로도 이번 차수 상세로 반영해야 화면(합계)과 DB(상세행)가 항상 일치한다.
        // ★ バグ修正：今回の給与回に控除詳細が一つも保存されておらず、直前の保存分を繰り越して画面にだけ
        //   表示する場合（selectDefaultDeductionDetails）、これらの項目が「touched」として扱われず画面には
        //   見えるがPAYROLL_DEDUCTION_DETAILには結局保存されない問題があった。繰り越して表示した
        //   以上、実際にも今回の回の詳細として反映しないと画面（合計）とDB（詳細行）が常に一致しない。
        if (deductionCarriedForward) {
            for (PaymentMntDeductionDetailDTO d : deductionDetails) {
                touchedDeductionItemIds.add(d.getDeductionItemId());
            }
        }

        PaymentMntDailyWorkSummary dailySummary = selectDailyWorkSummary(conn, payrollEmployeeId);
        if (dailySummary != null) {
            Long dailyPayItemId = selectDailyPayItemId(conn);
            if (dailyPayItemId != null) {
                if (dailySummary.getRecordCount() > 0) {
                    upsertPayDetailInList(payDetails, dailyPayItemId, dailySummary.getSumPay());
                    touchedPayItemIds.add(dailyPayItemId);

                    Long incomeTaxItemId = selectDeductionItemIdByName(conn, "소득세");
                    if (incomeTaxItemId != null) {
                        upsertDeductionDetailInList(deductionDetails, incomeTaxItemId, dailySummary.getSumIncomeTax());
                        touchedDeductionItemIds.add(incomeTaxItemId);
                    }
                    Long localTaxItemId = selectDeductionItemIdByName(conn, "지방소득세");
                    if (localTaxItemId != null) {
                        upsertDeductionDetailInList(deductionDetails, localTaxItemId, dailySummary.getSumLocalTax());
                        touchedDeductionItemIds.add(localTaxItemId);
                    }
                } else if (!hasPayItem(payDetails, dailyPayItemId)) {
                    Long baseWageAmount = selectEmployeeBaseWageAmount(conn, payrollEmployeeId);
                    if (baseWageAmount != null && baseWageAmount > 0) {
                        upsertPayDetailInList(payDetails, dailyPayItemId, baseWageAmount);
                        touchedPayItemIds.add(dailyPayItemId);
                    }
                }
            }
        } else {
            Long baseWagePayItemId = selectBaseWagePayItemId(conn);
            if (baseWagePayItemId != null && !hasPayItem(payDetails, baseWagePayItemId)) {
                Long baseWageAmount = selectEmployeeBaseWageAmount(conn, payrollEmployeeId);
                if (baseWageAmount != null && baseWageAmount > 0) {
                    upsertPayDetailInList(payDetails, baseWagePayItemId, baseWageAmount);
                    touchedPayItemIds.add(baseWagePayItemId);
                }
            }
        }

        // 식대처럼 지급항목 마스터(PAY_ITEM.BULK_PAY_AMOUNT)에 회사 공통 기본값이 있는 항목은, 화면에도
        // 항상 그 기본값이 표시되므로(입력화면 렌더링 시 data-default), 저장된 상세가 없으면 여기서도 채워서
        // 좌측 목록 합계와 우측 화면에 보이는 합계가 항상 일치하도록 한다.
        // 食事代のように支給項目マスタ（PAY_ITEM.BULK_PAY_AMOUNT）に会社共通の初期値がある項目は、画面にも
        // 常にその初期値が表示されるため（入力画面レンダリング時data-default）、保存された詳細がなければここでも埋めて
        // 左側一覧の合計と右側画面に見える合計が常に一致するようにする。
        for (Map.Entry<Long, Long> entry : bulkDefaults.entrySet()) {
            if (!hasPayItem(payDetails, entry.getKey())) {
                upsertPayDetailInList(payDetails, entry.getKey(), entry.getValue());
                touchedPayItemIds.add(entry.getKey());
            }
        }

        long totalPay = 0;
        for (PaymentMntPayDetailDTO p : payDetails) totalPay += (p.getAmount() == null ? 0 : p.getAmount());
        long totalDeduction = 0;
        for (PaymentMntDeductionDetailDTO d : deductionDetails) totalDeduction += (d.getAmount() == null ? 0 : d.getAmount());

        // ★ 화면 표시값과 DB를 일치시키기 위해, 위에서 보정/대체된 항목만 실제로 저장한다
        //   (사용자가 직접 저장한 값은 손대지 않고, 화면에만 보이던 기본값/근무기록 합계만 반영).
        // ★ 画面表示値とDBを一致させるため、上で補正・代替された項目のみ実際に保存する
        //   （ユーザーが直接保存した値には手を加えず、画面にだけ見えていた初期値・勤務記録合計のみ反映）。
        if (!touchedPayItemIds.isEmpty() || !touchedDeductionItemIds.isEmpty()) {
            for (PaymentMntPayDetailDTO p : payDetails) {
                if (touchedPayItemIds.contains(p.getPayItemId())) {
                    upsertPayDetail(conn, payrollEmployeeId, p.getPayItemId().intValue(), p.getAmount());
                }
            }
            for (PaymentMntDeductionDetailDTO d : deductionDetails) {
                if (touchedDeductionItemIds.contains(d.getDeductionItemId())) {
                    upsertDeductionDetail(conn, payrollEmployeeId, d.getDeductionItemId().intValue(), d.getAmount());
                }
            }
            updateEmployeeTotals(conn, payrollEmployeeId, totalPay, totalDeduction, totalPay - totalDeduction);
        }

        PaymentMntEffectiveDetail result = new PaymentMntEffectiveDetail();
        result.setPayDetails(payDetails);
        result.setDeductionDetails(deductionDetails);
        result.setTotalPayAmount(totalPay);
        result.setTotalDeductionAmount(totalDeduction);
        result.setNetPayAmount(totalPay - totalDeduction);
        return result;
    }

    /** 지급항목 마스터 중 회사 공통 기본값(BULK_PAY_AMOUNT)이 설정된 항목들 (PAY_ITEM_ID -> 기본값)
     *  支給項目マスタのうち、会社共通初期値（BULK_PAY_AMOUNT）が設定された項目（PAY_ITEM_ID -> 初期値） */
    public Map<Long, Long> selectPayItemBulkDefaults(Connection conn) throws SQLException {
        Map<Long, Long> map = new HashMap<>();
        String sql = "SELECT PAY_ITEM_ID, BULK_PAY_AMOUNT FROM PAY_ITEM WHERE USE_YN = 'Y' AND BULK_PAY_AMOUNT > 0";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getLong("PAY_ITEM_ID"), rs.getLong("BULK_PAY_AMOUNT"));
            }
        }
        return map;
    }

    private boolean hasPayItem(List<PaymentMntPayDetailDTO> payDetails, Long payItemId) {
        for (PaymentMntPayDetailDTO p : payDetails) {
            if (payItemId.equals(p.getPayItemId())) return true;
        }
        return false;
    }

    private void upsertPayDetailInList(List<PaymentMntPayDetailDTO> payDetails, Long payItemId, long amount) {
        for (PaymentMntPayDetailDTO p : payDetails) {
            if (payItemId.equals(p.getPayItemId())) {
                p.setAmount(amount);
                return;
            }
        }
        PaymentMntPayDetailDTO added = new PaymentMntPayDetailDTO();
        added.setPayItemId(payItemId);
        added.setAmount(amount);
        payDetails.add(added);
    }

    private void upsertDeductionDetailInList(List<PaymentMntDeductionDetailDTO> deductionDetails, Long deductionItemId, long amount) {
        for (PaymentMntDeductionDetailDTO d : deductionDetails) {
            if (deductionItemId.equals(d.getDeductionItemId())) {
                d.setAmount(amount);
                return;
            }
        }
        PaymentMntDeductionDetailDTO added = new PaymentMntDeductionDetailDTO();
        added.setDeductionItemId(deductionItemId);
        added.setAmount(amount);
        deductionDetails.add(added);
    }

    public List<PaymentMntEmployeeDTO> getModalEmployeeList(Connection conn, String keyword) throws SQLException {
        List<PaymentMntEmployeeDTO> list = new ArrayList<>();
        // 일용직/DAILY 사원은 급여입력관리 화면 대상이 아니므로(별도 일용직 급여 화면에서 관리) 항상 제외한다. / 日雇い/DAILY社員は給与入力管理画面の対象外のため（別途日雇い給与画面で管理）常に除外する。
        String sql = "SELECT employee_id, employee_name, employment_type, DEPARTMENT, POSITION, BASE_WAGE_AMOUNT FROM EMPLOYEE "
                   + "WHERE employment_type NOT IN ('일용직','DAILY') ";
        if (keyword != null && !keyword.trim().isEmpty()) { sql += "AND employee_name LIKE ? "; }
        sql += "ORDER BY employee_name";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (keyword != null && !keyword.trim().isEmpty()) { pstmt.setString(1, "%" + keyword.trim() + "%"); }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntEmployeeDTO dto = new PaymentMntEmployeeDTO();
                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setEmploymentType(rs.getString("employment_type"));
                    dto.setDepartment(rs.getString("DEPARTMENT"));
                    dto.setPosition(rs.getString("POSITION"));
                    dto.setBaseWageAmount(rs.getLong("BASE_WAGE_AMOUNT"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    public List<PaymentMntEmployeeDTO> getModalEmployeeList(Connection conn, String keyword, int limit, int offset, String department, String position, String status) throws SQLException {
        return getModalEmployeeList(conn, keyword, limit, offset, department, position, status, false);
    }

    /** excludeDayWorkers=true면 일용직/DAILY 사원은 목록에서 제외한다 (급여입력관리는 일용직을 별도 화면에서 관리하므로 사원추가 대상에서 뺀다).
     *  excludeDayWorkers=trueなら日雇い/DAILY社員は一覧から除外する（給与入力管理は日雇いを別画面で管理するため社員追加対象から外す）。 */
    public List<PaymentMntEmployeeDTO> getModalEmployeeList(Connection conn, String keyword, int limit, int offset, String department, String position, String status, boolean excludeDayWorkers) throws SQLException {
        List<PaymentMntEmployeeDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT * FROM ( ");
        sql.append("  SELECT ROWNUM AS RNUM, A.* FROM ( ");
        sql.append("    SELECT employee_id, employee_name, employment_type, DEPARTMENT, POSITION, BASE_WAGE_AMOUNT, RESIGN_DATE ");
        sql.append("    FROM EMPLOYEE WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) { sql.append(" AND employee_name LIKE ? "); }
        if (department != null && !department.trim().isEmpty()) { sql.append(" AND DEPARTMENT = ? "); }
        if (position != null && !position.trim().isEmpty()) { sql.append(" AND POSITION = ? "); }

        if ("재직".equals(status)) { sql.append(" AND RESIGN_DATE IS NULL "); }
        else if ("퇴직".equals(status)) { sql.append(" AND RESIGN_DATE IS NOT NULL "); }

        if (excludeDayWorkers) { sql.append(" AND employment_type NOT IN ('일용직','DAILY') "); }

        sql.append("    ORDER BY employee_name ");
        sql.append("  ) A WHERE ROWNUM <= ? ");
        sql.append(") WHERE RNUM > ? ");
                    
        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (keyword != null && !keyword.trim().isEmpty()) { pstmt.setString(paramIndex++, "%" + keyword.trim() + "%"); }
            if (department != null && !department.trim().isEmpty()) { pstmt.setString(paramIndex++, department); }
            if (position != null && !position.trim().isEmpty()) { pstmt.setString(paramIndex++, position); }
            
            pstmt.setInt(paramIndex++, offset + limit); 
            pstmt.setInt(paramIndex++, offset);         
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentMntEmployeeDTO dto = new PaymentMntEmployeeDTO();
                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setEmployeeName(rs.getString("employee_name"));
                    dto.setEmploymentType(rs.getString("employment_type"));
                    dto.setDepartment(rs.getString("DEPARTMENT"));
                    dto.setPosition(rs.getString("POSITION"));
                    dto.setBaseWageAmount(rs.getLong("BASE_WAGE_AMOUNT"));
                    
                    String empStatus = (rs.getString("RESIGN_DATE") == null) ? "재직" : "퇴직";
                    dto.setStatus(empStatus); 
                    list.add(dto);
                }
            }
        }
        return list;
    }

    public int getModalEmployeeCount(Connection conn, String keyword, String department, String position, String status) throws SQLException {
        return getModalEmployeeCount(conn, keyword, department, position, status, false);
    }

    /** excludeDayWorkers=true면 일용직/DAILY 사원은 카운트에서 제외한다 (getModalEmployeeList와 동일 조건).
     *  excludeDayWorkers=trueなら日雇い/DAILY社員はカウントから除外する（getModalEmployeeListと同一条件）。 */
    public int getModalEmployeeCount(Connection conn, String keyword, String department, String position, String status, boolean excludeDayWorkers) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM EMPLOYEE WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) { sql.append(" AND employee_name LIKE ? "); }
        if (department != null && !department.trim().isEmpty()) { sql.append(" AND DEPARTMENT = ? "); }
        if (position != null && !position.trim().isEmpty()) { sql.append(" AND POSITION = ? "); }

        if ("재직".equals(status)) { sql.append(" AND RESIGN_DATE IS NULL "); }
        else if ("퇴직".equals(status)) { sql.append(" AND RESIGN_DATE IS NOT NULL "); }

        if (excludeDayWorkers) { sql.append(" AND employment_type NOT IN ('일용직','DAILY') "); }

        int count = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (keyword != null && !keyword.trim().isEmpty()) { pstmt.setString(paramIndex++, "%" + keyword.trim() + "%"); }
            if (department != null && !department.trim().isEmpty()) { pstmt.setString(paramIndex++, department); }
            if (position != null && !position.trim().isEmpty()) { pstmt.setString(paramIndex++, position); }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { count = rs.getInt(1); }
            }
        }
        return count;
    }

    public void insertPayrollEmployees(Connection conn, Long payrollId, List<String> empIds) throws SQLException {
        // 1. PAYROLL_EMPLOYEE 테이블에 사원 먼저 등록 / 1. PAYROLL_EMPLOYEEテーブルに社員を先に登録
        String insertEmpSql = "INSERT INTO PAYROLL_EMPLOYEE ("
                   + "    payroll_employee_id, payroll_id, employee_id, "
                   + "    employment_type, income_type, "
                   + "    total_pay_amount, total_deduction_amount, net_pay_amount, reg_id, mod_id"
                   + ") "
                   + "SELECT "
                   + "    PAYROLL_EMPLOYEE_SEQ.NEXTVAL, "
                   + "    ?, "
                   + "    e.employee_id, "
                   + "    e.employment_type, "
                   + "    '일반', "        
                   + "    NVL(e.base_wage_amount, 0), "  
                   + "    0, "            
                   + "    NVL(e.base_wage_amount, 0), "  
                   + "    'admin', "      
                   + "    'admin' "        
                   + "FROM EMPLOYEE e "
                   + "WHERE e.employee_id = ? "
                   + "  AND NOT EXISTS ("
                   + "      SELECT 1 FROM PAYROLL_EMPLOYEE p "
                   + "      WHERE p.payroll_id = ? AND p.employee_id = e.employee_id"
                   + "  )";

     // 2. PAYROLL_PAY_DETAIL 테이블에 지급항목 등록 (고용형태에 따라 분기) / 2. PAYROLL_PAY_DETAILテーブルに支給項目を登録（雇用形態により分岐）
     //    - DAILY(일용직) : '일용급여' 항목에 등록 / DAILY（日雇い）：「日雇い給与」項目に登録
     //    - 그 외(REGULAR/CONTRACT 등) : '기본급' 항목에 등록 / それ以外（REGULAR/CONTRACTなど）：「基本給」項目に登録
        // '일용급여'/'기본급' 항목 ID는 미리 한 번만 조회해서 바인드 파라미터로 넘긴다. / 「日雇い給与」/「基本給」項目IDは事前に一度だけ照会してバインドパラメータで渡す。
        // (CASE 안에 스칼라 서브쿼리를 직접 넣으면, JOIN + correlated NOT EXISTS와 결합될 때
        //  Oracle이 타입을 잘못 추론해 ORA-00932(inconsistent datatypes)가 발생한다)
        // （CASE内にスカラーサブクエリを直接入れると、JOIN + correlated NOT EXISTSと組み合わさる際
        //  OracleがORA-00932(inconsistent datatypes)を起こすことがある）
        Long dailyPayItemId = selectDailyPayItemId(conn);
        Long baseWagePayItemId = selectBaseWagePayItemId(conn);

        String insertPayDetailSql = "INSERT INTO PAYROLL_PAY_DETAIL ("
                   + "    payroll_pay_detail_id, payroll_employee_id, pay_item_id, amount, reg_id, mod_id"
                   + ") "
                   + "SELECT "
                   + "    PAYROLL_PAY_DETAIL_SEQ.NEXTVAL, "
                   + "    p.payroll_employee_id, "
                   + "    CASE WHEN e.employment_type IN ('일용직','DAILY') THEN ? ELSE ? END, " // ★ 일용직은 일용급여 항목, 그 외는 기본급 항목 - 실데이터의 EMPLOYMENT_TYPE이 '일용직'/'DAILY' 두 가지로 섞여 있어 둘 다 포함 / ★日雇いは日雇い給与項目、それ以外は基本給項目 - 実データのEMPLOYMENT_TYPEが「일용직」/「DAILY」の2種類混在のため両方含む
                   + "    e.base_wage_amount, "
                   + "    'admin', 'admin' "
                   + "FROM PAYROLL_EMPLOYEE p JOIN EMPLOYEE e ON p.employee_id = e.employee_id "
                   + "WHERE p.payroll_id = ? AND p.employee_id = ? "
                   + "  AND NOT EXISTS ("
                   + "      SELECT 1 FROM PAYROLL_PAY_DETAIL d "
                   + "      WHERE d.payroll_employee_id = p.payroll_employee_id "
                   + "        AND d.pay_item_id = CASE WHEN e.employment_type IN ('일용직','DAILY') THEN ? ELSE ? END"
                   + "  )";

        try (PreparedStatement pstmtEmp = conn.prepareStatement(insertEmpSql);
             PreparedStatement pstmtDetail = conn.prepareStatement(insertPayDetailSql)) {

            for (String empId : empIds) {
                if (empId == null || empId.trim().isEmpty()) continue;

                pstmtEmp.setLong(1, payrollId);
                pstmtEmp.setString(2, empId.trim());
                pstmtEmp.setLong(3, payrollId);
                pstmtEmp.executeUpdate();

                setDailyPayItemIdParam(pstmtDetail, 1, dailyPayItemId);
                setDailyPayItemIdParam(pstmtDetail, 2, baseWagePayItemId);
                pstmtDetail.setLong(3, payrollId);
                pstmtDetail.setString(4, empId.trim());
                setDailyPayItemIdParam(pstmtDetail, 5, dailyPayItemId);
                setDailyPayItemIdParam(pstmtDetail, 6, baseWagePayItemId);
                pstmtDetail.executeUpdate();
            }
        }
    }

    /** '일용급여' 지급항목의 PAY_ITEM_ID (없으면 null)
     *  「日雇い給与」支給項目のPAY_ITEM_ID（なければnull） */
    public Long selectDailyPayItemId(Connection conn) throws SQLException {
        String sql = "SELECT PAY_ITEM_ID FROM PAY_ITEM WHERE PAY_ITEM_NAME = '일용급여'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                long id = rs.getLong(1);
                return rs.wasNull() ? null : id;
            }
        }
        return null;
    }

    /** '기본급' 지급항목의 PAY_ITEM_ID (없으면 null). PAY_ITEM 마스터에서 이름으로 조회하며,
     *  ID를 하드코딩하지 않는 이유는 환경마다 실제 채번된 ID가 다를 수 있기 때문이다.
     *  「基本給」支給項目のPAY_ITEM_ID（なければnull）。PAY_ITEMマスタから名前で照会し、
     *  IDをハードコーディングしない理由は、環境ごとに実際に採番されたIDが異なる場合があるため。 */
    public Long selectBaseWagePayItemId(Connection conn) throws SQLException {
        String sql = "SELECT PAY_ITEM_ID FROM PAY_ITEM WHERE PAY_ITEM_NAME = '기본급'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                long id = rs.getLong(1);
                return rs.wasNull() ? null : id;
            }
        }
        return null;
    }

    private void setDailyPayItemIdParam(PreparedStatement pstmt, int idx, Long dailyPayItemId) throws SQLException {
        if (dailyPayItemId == null) {
            pstmt.setNull(idx, Types.NUMERIC);
        } else {
            pstmt.setLong(idx, dailyPayItemId);
        }
    }

    public List<String> getDepartmentList(Connection conn) throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT DEPARTMENT FROM EMPLOYEE WHERE DEPARTMENT IS NOT NULL ORDER BY DEPARTMENT";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) { list.add(rs.getString("DEPARTMENT")); }
        }
        return list;
    }

    public List<String> getPositionList(Connection conn) throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT POSITION FROM EMPLOYEE WHERE POSITION IS NOT NULL ORDER BY POSITION";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) { list.add(rs.getString("POSITION")); }
        }
        return list;
    }

    // 지급 항목 마스터 조회 / 支給項目マスタ照会
    public List<PaymentMntPayItemDTO> selectPayItemList(Connection conn) throws SQLException {
        List<PaymentMntPayItemDTO> list = new ArrayList<>();
        // ★ 쿼리에 BULK_PAY_AMOUNT 추가 / ★クエリにBULK_PAY_AMOUNTを追加
        String sql = "SELECT PAY_ITEM_ID, PAY_ITEM_NAME, CALCULATION_METHOD, BULK_PAY_AMOUNT FROM PAY_ITEM WHERE USE_YN = 'Y' ORDER BY DISPLAY_ORDER";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                PaymentMntPayItemDTO dto = new PaymentMntPayItemDTO();
                dto.setPayItemId(rs.getInt("PAY_ITEM_ID"));
                dto.setPayItemName(rs.getString("PAY_ITEM_NAME"));
                dto.setCalculationMethod(rs.getString("CALCULATION_METHOD"));
                // ★ 식대 20만 원 가져오기 / ★食事代20万ウォンを取得
                dto.setBulkPayAmount(rs.getLong("BULK_PAY_AMOUNT")); 
                list.add(dto);
            }
        }
        return list;
    }

    // 공제 항목 마스터 조회 / 控除項目マスタ照会
    public List<PaymentMntDeductionItemDTO> selectDeductionItemList(Connection conn) throws SQLException {
        List<PaymentMntDeductionItemDTO> list = new ArrayList<>();
        // ★ 수정: ITEM_NAME을 DEDUCTION_ITEM_NAME으로 변경 / ★修正：ITEM_NAMEをDEDUCTION_ITEM_NAMEに変更
        String sql = "SELECT DEDUCTION_ITEM_ID, DEDUCTION_ITEM_NAME, CALCULATION_METHOD FROM DEDUCTION_ITEM WHERE USE_YN = 'Y' ORDER BY DISPLAY_ORDER";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                PaymentMntDeductionItemDTO dto = new PaymentMntDeductionItemDTO();
                dto.setDeductionItemId(rs.getInt("DEDUCTION_ITEM_ID"));
                
                // ★ 수정: 값을 꺼내올 때도 DEDUCTION_ITEM_NAME으로 변경 / ★修正：値を取り出す時もDEDUCTION_ITEM_NAMEに変更
                dto.setDeductionItemName(rs.getString("DEDUCTION_ITEM_NAME")); 
                dto.setCalculationMethod(rs.getString("CALCULATION_METHOD"));
                list.add(dto);
            }
        }
        return list;
    }
    
    public void upsertPayDetail(Connection conn, Long empId, Integer itemId, Long amount) throws SQLException {
        String updateSql = "UPDATE PAYROLL_PAY_DETAIL SET AMOUNT = ?, MOD_ID = 'admin' WHERE PAYROLL_EMPLOYEE_ID = ? AND PAY_ITEM_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setLong(1, amount);
            pstmt.setLong(2, empId);
            pstmt.setInt(3, itemId);
            int count = pstmt.executeUpdate();

            if (count == 0) { // 수정된 게 없다면 (기존 데이터가 없다는 뜻이므로 INSERT) / 更新がなければ（既存データがないという意味なのでINSERT）
                String insertSql = "INSERT INTO PAYROLL_PAY_DETAIL (PAYROLL_PAY_DETAIL_ID, PAYROLL_EMPLOYEE_ID, PAY_ITEM_ID, AMOUNT, REG_ID, MOD_ID) "
                                 + "VALUES (PAYROLL_PAY_DETAIL_SEQ.NEXTVAL, ?, ?, ?, 'admin', 'admin')";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setLong(1, empId);
                    insertStmt.setInt(2, itemId);
                    insertStmt.setLong(3, amount);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    // 2. 공제항목 갱신 (있으면 수정, 없으면 새로 추가) / 2. 控除項目更新（あれば修正、なければ新規追加）
    public void upsertDeductionDetail(Connection conn, Long empId, Integer itemId, Long amount) throws SQLException {
        String updateSql = "UPDATE PAYROLL_DEDUCTION_DETAIL SET AMOUNT = ?, MOD_ID = 'admin' WHERE PAYROLL_EMPLOYEE_ID = ? AND DEDUCTION_ITEM_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setLong(1, amount);
            pstmt.setLong(2, empId);
            pstmt.setInt(3, itemId);
            int count = pstmt.executeUpdate();

            if (count == 0) {
                String insertSql = "INSERT INTO PAYROLL_DEDUCTION_DETAIL (PAYROLL_DEDUCTION_DETAIL_ID, PAYROLL_EMPLOYEE_ID, DEDUCTION_ITEM_ID, AMOUNT, REG_ID, MOD_ID) "
                                 + "VALUES (PAYROLL_DEDUCT_DETAIL_SEQ.NEXTVAL, ?, ?, ?, 'admin', 'admin')";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setLong(1, empId);
                    insertStmt.setInt(2, itemId);
                    insertStmt.setLong(3, amount);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    // 3. 사원 마스터(PAYROLL_EMPLOYEE) 총 금액 갱신 / 3. 社員マスタ（PAYROLL_EMPLOYEE）の総金額を更新
    public void updateEmployeeTotals(Connection conn, Long empId, long totalPay, long totalDed, long netPay) throws SQLException {
        String sql = "UPDATE PAYROLL_EMPLOYEE SET TOTAL_PAY_AMOUNT = ?, TOTAL_DEDUCTION_AMOUNT = ?, NET_PAY_AMOUNT = ? WHERE PAYROLL_EMPLOYEE_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, totalPay);
            pstmt.setLong(2, totalDed);
            pstmt.setLong(3, netPay);
            pstmt.setLong(4, empId);
            pstmt.executeUpdate();
        }
    }
    
 // 1. 기존 데이터 깔끔하게 지우기 (외래키 오류 방지를 위해 하위 테이블부터 삭제) / 1. 既存データをきれいに削除（外部キーエラー防止のため子テーブルから削除）
    public void deletePayrollEmployeesByPeriod(Connection conn, String currYearMonth, int currSeq) throws SQLException {
        // 공제 상세내역 삭제 / 控除詳細内訳を削除
        String delDed = "DELETE FROM PAYROLL_DEDUCTION_DETAIL WHERE PAYROLL_EMPLOYEE_ID IN (SELECT PAYROLL_EMPLOYEE_ID FROM PAYROLL_EMPLOYEE p JOIN PAYROLL pr ON p.PAYROLL_ID = pr.PAYROLL_ID WHERE pr.PAY_YEAR_MONTH = ? AND pr.PAY_SEQUENCE = ?)";
        try(PreparedStatement pstmt = conn.prepareStatement(delDed)) {
            pstmt.setString(1, currYearMonth);
            pstmt.setInt(2, currSeq);
            pstmt.executeUpdate();
        }
        
        // 지급 상세내역 삭제 / 支給詳細内訳を削除
        String delPay = "DELETE FROM PAYROLL_PAY_DETAIL WHERE PAYROLL_EMPLOYEE_ID IN (SELECT PAYROLL_EMPLOYEE_ID FROM PAYROLL_EMPLOYEE p JOIN PAYROLL pr ON p.PAYROLL_ID = pr.PAYROLL_ID WHERE pr.PAY_YEAR_MONTH = ? AND pr.PAY_SEQUENCE = ?)";
        try(PreparedStatement pstmt = conn.prepareStatement(delPay)) {
            pstmt.setString(1, currYearMonth);
            pstmt.setInt(2, currSeq);
            pstmt.executeUpdate();
        }
        
        // 급여 대상자 목록 삭제 / 給与対象者一覧を削除
        String delEmp = "DELETE FROM PAYROLL_EMPLOYEE WHERE PAYROLL_ID = (SELECT PAYROLL_ID FROM PAYROLL WHERE PAY_YEAR_MONTH = ? AND PAY_SEQUENCE = ?)";
        try(PreparedStatement pstmt = conn.prepareStatement(delEmp)) {
            pstmt.setString(1, currYearMonth);
            pstmt.setInt(2, currSeq);
            pstmt.executeUpdate();
        }
    }

    // 2. 이전 급여차수의 사원을 복사. PAYROLL_EMPLOYEE 총액만 복사하면 지급/공제 항목별 상세(기본급/식대/일용급여,
    //    국민연금/소득세 등)가 빠져서 급여대장 등에서 항목별 금액이 0으로 보이므로, 사원별로 새 PAYROLL_EMPLOYEE 행을
    //    만든 뒤 PAYROLL_PAY_DETAIL/PAYROLL_DEDUCTION_DETAIL 상세행까지 함께 복사한다.
    // 2. 前回の給与回の社員をコピー。PAYROLL_EMPLOYEE総額だけコピーすると支給・控除項目別詳細（基本給・食事代・日雇い給与、
    //    国民年金・所得税など）が抜けて給与台帳などで項目別金額が0に見えるため、社員ごとに新しいPAYROLL_EMPLOYEE行を
    //    作った後、PAYROLL_PAY_DETAIL/PAYROLL_DEDUCTION_DETAIL詳細行まで一緒にコピーする。
    public int copyPreviousEmployeesCount(Connection conn, String prevYearMonth, int prevSeq, String currYearMonth, int currSeq) throws SQLException {
        Long currPayrollId = selectPayrollId(conn, currYearMonth, currSeq);
        if (currPayrollId == null) { return 0; }

        List<Object[]> prevEmployees = new ArrayList<>(); // [PAYROLL_EMPLOYEE_ID, EMPLOYEE_ID, EMPLOYMENT_TYPE, INCOME_TYPE]
        String selectSql = "SELECT prev_e.PAYROLL_EMPLOYEE_ID, prev_e.EMPLOYEE_ID, prev_e.EMPLOYMENT_TYPE, prev_e.INCOME_TYPE "
                          + "FROM PAYROLL_EMPLOYEE prev_e JOIN PAYROLL prev_p ON prev_e.PAYROLL_ID = prev_p.PAYROLL_ID "
                          + "WHERE prev_p.PAY_YEAR_MONTH = ? AND prev_p.PAY_SEQUENCE = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            pstmt.setString(1, prevYearMonth);
            pstmt.setInt(2, prevSeq);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    prevEmployees.add(new Object[] {
                        rs.getLong("PAYROLL_EMPLOYEE_ID"), rs.getString("EMPLOYEE_ID"),
                        rs.getString("EMPLOYMENT_TYPE"), rs.getString("INCOME_TYPE")
                    });
                }
            }
        }

        // ★ 최적화: 사원마다 반복 조회하던 지급항목 기본값(마스터 전체 조회)을 루프 밖에서 한 번만 조회 / ★最適化：社員ごとに繰り返し照会していた支給項目初期値（マスタ全件照会）をループの外で一度だけ照会
        Map<Long, Long> bulkDefaults = selectPayItemBulkDefaults(conn);
        // ★ 최적화: INSERT할 때마다 "SELECT MAX(id)+1"로 채번하던 것을 테이블당 1회만 조회하고, / ★最適化：INSERTのたびに「SELECT MAX(id)+1」で採番していたものをテーブルごとに1回だけ照会し、
        //   이후 같은 배치 안에서는 메모리 카운터로 증가시킨다 (사원 수 × 항목 수만큼 반복되던 MAX 재조회 제거) / 以降同じバッチ内ではメモリカウンタで増加させる（社員数×項目数分繰り返されていたMAX再照会を除去）
        IdAllocator ids = new IdAllocator();

        int count = 0;
        for (Object[] row : prevEmployees) {
            Long prevPayrollEmployeeId = (Long) row[0];
            String employeeId = (String) row[1];
            String employmentType = (String) row[2];
            String incomeType = (String) row[3];

            // 일용직/DAILY 사원은 급여입력관리 화면 대상이 아니므로(별도 일용직 급여 화면에서 관리) 복사하지 않는다. / 日雇い/DAILY社員は給与入力管理画面の対象外のため（別途日雇い給与画面で管理）コピーしない。
            if ("일용직".equals(employmentType) || "DAILY".equals(employmentType)) {
                continue;
            }

            // ★ 저장된 상세만 그대로 읽지 않고, 급여입력/관리 화면에 실제로 보이는 "최종" 금액(기본급/식대
            //   기본값 보정, 근무기록 합계 대체 포함)을 기준으로 복사한다. 이전 달 사원이 화면에는 금액이
            //   보였지만 상세행이 실제로 저장된 적은 없는 경우(예: 신규 사원목록 표시 - 저장 미클릭)까지
            //   포함해서 선택한 귀속연월 데이터와 완전히 동일하게 불러오기 위함이다.
            // ★ 保存された詳細だけをそのまま読まず、給与入力・管理画面に実際に表示される「最終」金額（基本給・食事代
            //   初期値補正、勤務記録合計代替を含む）を基準にコピーする。前月の社員が画面には金額が
            //   見えていたが詳細行が実際に保存されたことはない場合（例：新規社員一覧表示 - 保存未クリック）まで
            //   含めて、選択した帰属年月データと完全に同一に読み込むためである。
            PaymentMntEffectiveDetail prevEffective = computeEffectiveDetail(conn, prevPayrollEmployeeId, bulkDefaults);

            Long newPayrollEmployeeId = insertPayrollEmployeeCopy(conn, ids, currPayrollId, employeeId, employmentType, incomeType,
                    prevEffective.getTotalPayAmount(), prevEffective.getTotalDeductionAmount(), prevEffective.getNetPayAmount());

            // ★ 방금 새로 만든 PAYROLL_EMPLOYEE 행이라 기존 상세행이 있을 리 없으므로, upsert(UPDATE 시도 후
            //   INSERT)가 아니라 바로 INSERT만 한다 (사원마다 항목 수만큼 반복되던 헛UPDATE 왕복 제거)
            // ★ 今作ったばかりのPAYROLL_EMPLOYEE行のため既存の詳細行があるはずがなく、upsert（UPDATE試行後
            //   INSERT）ではなく直接INSERTのみ行う（社員ごとに項目数分繰り返されていた無駄なUPDATE往復を除去）
            for (PaymentMntPayDetailDTO detail : prevEffective.getPayDetails()) {
                insertPayDetailFast(conn, ids, newPayrollEmployeeId, detail.getPayItemId().intValue(), detail.getAmount());
            }
            for (PaymentMntDeductionDetailDTO detail : prevEffective.getDeductionDetails()) {
                insertDeductionDetailFast(conn, ids, newPayrollEmployeeId, detail.getDeductionItemId().intValue(), detail.getAmount());
            }

            count++;
        }
        return count;
    }

    /** "지난급여 불러오기"처럼 짧은 시간에 여러 테이블에 수십~수백 건을 INSERT해야 하는 배치 작업 전용 채번기.
     *  테이블별 MAX(id)를 최초 1회만 조회하고, 이후로는 메모리에서 증가시켜서 매 행마다 MAX를 다시 읽는
     *  비용을 없앤다. 그래도 동시 요청(예: 버튼 연속 클릭)으로 PK가 겹칠 수 있으니, 충돌 시 호출부에서
     *  bump()로 캐시된 다음 번호를 갱신하고 재시도한다.
     *  「前回給与の読み込み」のように短時間に複数のテーブルへ数十～数百件をINSERTする必要があるバッチ作業専用の採番機。
     *  テーブルごとのMAX(id)を最初の1回だけ照会し、以降はメモリ上で増加させて毎行MAXを再度読む
     *  コストをなくす。それでも同時リクエスト（例：ボタン連続クリック）でPKが重複する可能性があるため、衝突時は呼び出し側で
     *  bump()でキャッシュされた次番号を更新して再試行する。 */
    private static class IdAllocator {
        private final Map<String, Long> nextIds = new HashMap<>();

        long next(Connection conn, String table, String pkColumn) throws SQLException {
            Long current = nextIds.get(table);
            if (current == null) {
                String sql = "SELECT NVL(MAX(" + pkColumn + "), 0) FROM " + table;
                try (PreparedStatement pstmt = conn.prepareStatement(sql);
                     ResultSet rs = pstmt.executeQuery()) {
                    rs.next();
                    current = rs.getLong(1);
                }
            }
            long next = current + 1;
            nextIds.put(table, next);
            return next;
        }

        void bump(String table, long collidedValue) {
            nextIds.put(table, collidedValue + 1);
        }
    }

    private Long insertPayrollEmployeeCopy(Connection conn, IdAllocator ids, Long payrollId, String employeeId, String employmentType,
            String incomeType, long totalPay, long totalDed, long netPay) throws SQLException {
        String sql = "INSERT INTO PAYROLL_EMPLOYEE ("
                   + "    PAYROLL_EMPLOYEE_ID, PAYROLL_ID, EMPLOYEE_ID, EMPLOYMENT_TYPE, INCOME_TYPE, "
                   + "    TOTAL_PAY_AMOUNT, TOTAL_DEDUCTION_AMOUNT, NET_PAY_AMOUNT, REG_ID, MOD_ID"
                   + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'admin', 'admin')";

        final int maxAttempts = 5;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            long newId = ids.next(conn, "PAYROLL_EMPLOYEE", "PAYROLL_EMPLOYEE_ID");
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, newId);
                pstmt.setLong(2, payrollId);
                pstmt.setString(3, employeeId);
                pstmt.setString(4, employmentType);
                pstmt.setString(5, incomeType);
                pstmt.setLong(6, totalPay);
                pstmt.setLong(7, totalDed);
                pstmt.setLong(8, netPay);
                pstmt.executeUpdate();
                return newId;
            } catch (SQLIntegrityConstraintViolationException dup) {
                if (attempt == maxAttempts) { throw dup; }
                ids.bump("PAYROLL_EMPLOYEE", newId);
            }
        }
        throw new SQLException("PAYROLL_EMPLOYEE_ID 채번에 반복적으로 실패했습니다.");
    }

    private void insertPayDetailFast(Connection conn, IdAllocator ids, Long payrollEmployeeId, Integer itemId, Long amount) throws SQLException {
        String sql = "INSERT INTO PAYROLL_PAY_DETAIL (PAYROLL_PAY_DETAIL_ID, PAYROLL_EMPLOYEE_ID, PAY_ITEM_ID, AMOUNT, REG_ID, MOD_ID) "
                   + "VALUES (?, ?, ?, ?, 'admin', 'admin')";
        final int maxAttempts = 5;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            long newId = ids.next(conn, "PAYROLL_PAY_DETAIL", "PAYROLL_PAY_DETAIL_ID");
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, newId);
                pstmt.setLong(2, payrollEmployeeId);
                pstmt.setInt(3, itemId);
                pstmt.setLong(4, amount);
                pstmt.executeUpdate();
                return;
            } catch (SQLIntegrityConstraintViolationException dup) {
                if (attempt == maxAttempts) { throw dup; }
                ids.bump("PAYROLL_PAY_DETAIL", newId);
            }
        }
    }

    private void insertDeductionDetailFast(Connection conn, IdAllocator ids, Long payrollEmployeeId, Integer itemId, Long amount) throws SQLException {
        String sql = "INSERT INTO PAYROLL_DEDUCTION_DETAIL (PAYROLL_DEDUCTION_DETAIL_ID, PAYROLL_EMPLOYEE_ID, DEDUCTION_ITEM_ID, AMOUNT, REG_ID, MOD_ID) "
                   + "VALUES (?, ?, ?, ?, 'admin', 'admin')";
        final int maxAttempts = 5;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            long newId = ids.next(conn, "PAYROLL_DEDUCTION_DETAIL", "PAYROLL_DEDUCTION_DETAIL_ID");
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, newId);
                pstmt.setLong(2, payrollEmployeeId);
                pstmt.setInt(3, itemId);
                pstmt.setLong(4, amount);
                pstmt.executeUpdate();
                return;
            } catch (SQLIntegrityConstraintViolationException dup) {
                if (attempt == maxAttempts) { throw dup; }
                ids.bump("PAYROLL_DEDUCTION_DETAIL", newId);
            }
        }
    }
    
 // ★ 급여 마스터(PAYROLL) 테이블에 해당 연월 폴더가 없으면 새로 생성해주는 메서드 / ★給与マスタ（PAYROLL）テーブルに該当年月のフォルダがなければ新規生成するメソッド
    public void ensurePayrollExists(Connection conn, String yearMonth, int seq) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM PAYROLL WHERE PAY_YEAR_MONTH = ? AND PAY_SEQUENCE = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, yearMonth);
            pstmt.setInt(2, seq);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // ★ 수정된 부분: SYSDATE를 추가하여 날짜 빈칸(NULL) 에러 방지 / ★修正部分：SYSDATEを追加して日付欄（NULL）エラーを防止
                    String insertSql = "INSERT INTO PAYROLL (PAYROLL_ID, COMPANY_ID, PAY_YEAR_MONTH, PAY_SEQUENCE, SETTLEMENT_START_DATE, SETTLEMENT_END_DATE, PAYMENT_DATE, REG_ID, MOD_ID) "
                                     + "VALUES (PAYROLL_SEQ.NEXTVAL, 1001, ?, ?, SYSDATE, SYSDATE, SYSDATE, 'admin', 'admin')";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, yearMonth);
                        insertStmt.setInt(2, seq);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }
    
 // ★ 귀속연월 + 급여차수에 해당하는 정확한 PAYROLL_ID 조회 / ★帰属年月＋給与回に該当する正確なPAYROLL_IDを照会
    // (신규 사원 추가 시, 화면에서 JS가 엉뚱한 payrollId를 보내는 버그 방지용) / （新規社員追加時、画面からJSが誤ったpayrollIdを送るバグの防止用）
    public Long selectPayrollId(Connection conn, String payYearMonth, int paySequence) throws SQLException {
        String sql = "SELECT PAYROLL_ID FROM PAYROLL WHERE PAY_YEAR_MONTH = ? AND PAY_SEQUENCE = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payYearMonth);
            pstmt.setInt(2, paySequence);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("PAYROLL_ID");
                }
            }
        }
        return null;
    }
    

 // ★ 하단 [급여 종합정보] 집계 조회 / ★下部[給与総合情報]集計照会
    // - 월 합계 : 재직중(RESIGN_DATE IS NULL)인 사원 수 / - 月合計：在職中（RESIGN_DATE IS NULL）の社員数
    // - 지급/공제/실지급 총액 : 선택한 귀속연월(payYearMonth) + 급여차수(paySequence)에 등록된 / - 支給・控除・実支給総額：選択した帰属年月（payYearMonth）＋給与回（paySequence）に登録された
    //   PAYROLL_EMPLOYEE 전체 사원의 금액을 합산 (사원 급여정보가 저장/수정될 때마다 / PAYROLL_EMPLOYEE全社員の金額を合算（社員給与情報が保存・修正されるたび
    //   PAYROLL_EMPLOYEE.TOTAL_PAY_AMOUNT / TOTAL_DEDUCTION_AMOUNT 가 같이 갱신되므로, / PAYROLL_EMPLOYEE.TOTAL_PAY_AMOUNT / TOTAL_DEDUCTION_AMOUNTも一緒に更新されるため、
    //   화면을 다시 조회할 때마다 최신값이 자동 반영됨) / 画面を再照会するたびに最新値が自動反映される）
    public PaymentMntSummaryDTO selectPayrollSummary(Connection conn, String payYearMonth, int paySequence) throws SQLException {
        PaymentMntSummaryDTO summary = new PaymentMntSummaryDTO();

        // 1. 월 합계 - 이 급여차수에 실제로 화면 목록에 뜨는 사원 수 / 1. 月合計 - この給与回に実際に画面一覧に表示される社員数
        //    (일용직/DAILY는 급여입력관리 화면 대상이 아니므로 항상 제외 - selectEmployeeList와 동일한 조건으로 카운트) / （日雇い/DAILYは給与入力管理画面の対象外のため常に除外 - selectEmployeeListと同一条件でカウント）
        String countSql = "SELECT COUNT(*) AS TOTAL_COUNT "
                   + "FROM PAYROLL_EMPLOYEE pe "
                   + "JOIN PAYROLL pr ON pe.PAYROLL_ID = pr.PAYROLL_ID "
                   + "JOIN EMPLOYEE e ON pe.EMPLOYEE_ID = e.EMPLOYEE_ID "
                   + "WHERE pr.PAY_YEAR_MONTH = ? AND pr.PAY_SEQUENCE = ? "
                   + "  AND e.EMPLOYMENT_TYPE NOT IN ('일용직','DAILY')";
        try (PreparedStatement pstmt = conn.prepareStatement(countSql)) {
            pstmt.setString(1, payYearMonth);
            pstmt.setInt(2, paySequence);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    summary.setTotalCount(rs.getInt("TOTAL_COUNT"));
                }
            }
        }

        // 2. 지급/공제/실지급 총액 - 선택한 귀속연월+급여차수 기준, 화면 목록과 동일한 대상으로 집계 / 2. 支給・控除・実支給総額 - 選択した帰属年月＋給与回基準、画面一覧と同一対象で集計
        String sql = "SELECT "
                   + "    NVL(SUM(pe.TOTAL_PAY_AMOUNT), 0) AS TOTAL_GIVE_AMOUNT, "
                   + "    NVL(SUM(pe.TOTAL_DEDUCTION_AMOUNT), 0) AS TOTAL_DEDU_AMOUNT "
                   + "FROM PAYROLL_EMPLOYEE pe "
                   + "JOIN PAYROLL pr ON pe.PAYROLL_ID = pr.PAYROLL_ID "
                   + "JOIN EMPLOYEE e ON pe.EMPLOYEE_ID = e.EMPLOYEE_ID "
                   + "WHERE pr.PAY_YEAR_MONTH = ? AND pr.PAY_SEQUENCE = ? "
                   + "  AND e.EMPLOYMENT_TYPE NOT IN ('일용직','DAILY')";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payYearMonth);
            pstmt.setInt(2, paySequence);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long totalGive = rs.getLong("TOTAL_GIVE_AMOUNT");
                    long totalDedu = rs.getLong("TOTAL_DEDU_AMOUNT");

                    summary.setTotalGiveAmount(totalGive);
                    summary.setTotalDeduAmount(totalDedu);
                    summary.setTotalRealAmount(totalGive - totalDedu); // 실지급액 = 지급총액 - 공제총액 / 実支給額＝支給総額－控除総額
                }
            }
        }

        return summary;
    }
}