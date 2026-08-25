package payment.paymentpayitempart.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import payment.model.PaymentItemLedger;
import payment.paymentpayitempart.dao.PaymentpayitempartDao;

/**
 * 항목별 대장 조회 Service.
 * 項目別台帳の照会Service。
 */
public class PaymentpayitempartService {

	private PaymentpayitempartDao paymentpayitempartDao = new PaymentpayitempartDao();

	/**
	 * 셀렉트 박스용 지급항목+공제항목 통합 목록을 조회한다.
	 * セレクトボックス用の支給項目+控除項目の統合一覧を照会する。
	 */
	public List<PaymentItemLedger> getItemList(int companyId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return paymentpayitempartDao.selectItemList(conn, companyId);
		} catch (SQLException e) {
			throw new RuntimeException("지급/공제 항목 목록 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/**
	 * 시작·종료 연월과 선택한 항목으로 기간 안 사원 내역을 조회한다.
	 * 開始・終了年月と選択した項目で期間内の社員明細を照会する。
	 */
	public List<PaymentItemLedger> getEmployeeItemLedger(int companyId,
			int startYear, int startMonth, int endYear, int endMonth, String itemSelectValue) {
		PaymentItemLedger selectedItem = parseSelectValue(itemSelectValue);
		if (selectedItem == null) {
			return Collections.emptyList();
		}

		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return paymentpayitempartDao.selectByPeriodAndItem(conn, companyId,
					startYear, startMonth, endYear, endMonth,
					selectedItem.getItemType(), selectedItem.getItemId());
		} catch (SQLException e) {
			throw new RuntimeException("항목별 대장 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/**
	 * 사원별 기간 합계를 모두 더한다.
	 * 社員別の期間合計をすべて加算する。
	 */
	public long sumTotalAmount(List<PaymentItemLedger> employeeList) {
		long sum = 0L;
		if (employeeList == null) {
			return sum;
		}
		for (PaymentItemLedger row : employeeList) {
			sum += row.getTotalAmount();
		}
		return sum;
	}

	/**
	 * 셀렉트 박스 value를 항목 구분·아이디로 나눈다.
	 * セレクトボックスのvalueを項目区分・IDに分ける。
	 */
	public PaymentItemLedger parseSelectValue(String itemSelectValue) {
		if (itemSelectValue == null) {
			return null;
		}
		String value = itemSelectValue.trim();
		int separator = value.indexOf(':');
		if (separator <= 0 || separator == value.length() - 1) {
			return null;
		}

		String itemType = value.substring(0, separator);
		if (!PaymentItemLedger.TYPE_PAY.equals(itemType)
				&& !PaymentItemLedger.TYPE_DEDUCTION.equals(itemType)) {
			return null;
		}

		try {
			Long itemId = Long.valueOf(value.substring(separator + 1));
			return new PaymentItemLedger(itemType, itemId, null);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
