package payment.paymenttransferlist.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

import connection.ConnectionProvider;
import jdbc.JdbcUtil;
import payment.model.PaymentTransferRequest;
import payment.paymenttransferlist.dao.PaymenttransferlistDao;

/**
 * 급여이체 신청 조회 Service.
 * 給与振込申請照会Service。
 *
 */
public class PaymenttransferlistService {

	// ISO 날짜 형식
	// ISO日付形式。
	private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

	private PaymenttransferlistDao transferListDao = new PaymenttransferlistDao();

	/**
	 * 신청기간 안의 이체신청 결과 목록 조회.
	 * 申請期間内の振込申請結果一覧を照会する。
	 *
	 */
	public List<PaymentTransferRequest> getTransferRequestList(String startDate, String endDate) {
		Date start = toSqlDate(startDate);
		Date end = toSqlDate(endDate);
		if (start == null || end == null) {
			return Collections.emptyList();
		}
		if (start.after(end)) {
			return Collections.emptyList();
		}

		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return transferListDao.selectListByRequestPeriod(conn, start, end);
		} catch (SQLException e) {
			throw new RuntimeException("급여이체 신청 조회 중 DB 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	/**
	 * 목록의 이체금액 합계를 구한다.
	 * 一覧の振込金額合計を求める。
	 *
	 */
	public long sumTransferAmount(List<PaymentTransferRequest> list) {
		long sum = 0L;
		if (list == null) {
			return sum;
		}
		for (PaymentTransferRequest row : list) {
			sum += row.getTransferAmount();
		}
		return sum;
	}

	/**
	 * yyyy-MM-dd 문자열을 SQL Date로 변환한다.
	 * yyyy-MM-dd文字列をSQL Dateに変換する。
	 *
	 */
	private Date toSqlDate(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			LocalDate localDate = LocalDate.parse(value.trim(), ISO_DATE);
			return Date.valueOf(localDate);
		} catch (DateTimeParseException e) {
			return null;
		}
	}
}
