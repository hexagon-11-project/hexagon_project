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
 * 신청기간(REQUEST_DATE) 기준으로 PAYROLL_TRANSFER_REQUEST 내역을 조회한다.
 */
public class PaymenttransferlistService {

	private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

	private PaymenttransferlistDao transferListDao = new PaymenttransferlistDao();

	/**
	 * 신청기간 안의 이체신청 결과 목록 조회.
	 * @param startDate yyyy-MM-dd
	 * @param endDate yyyy-MM-dd
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
