package diligence.dailyworkrecord.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import config.model.DailyWorkRecord;
import config.model.EmployeeLeave;
import jdbc.JdbcUtil;

// 일용직 사원 목록 조회 및 일용직 근무 기록(단가, 지급액, 세금 등)을 DB에 처리하고 급여 시스템과 연동하는 DAO
// 日雇い社員リストの照会、および日雇い勤務記録（単価、支給額、税金など）をDBで処理し給与システムと連携する DAO
public class DailyWorkRecordDao {

	// 왼쪽 목록에 뿌릴 일용직 사원 목록만 (EMPLOYMENT_TYPE이 '일용직'/'DAILY' 두 가지로 섞여 있음)
	// 左側のリストに描画する日雇い社員リストのみ（EMPLOYMENT_TYPEが「日雇い」／「DAILY」の2種類で混在しているため）
	public List<EmployeeLeave> selectDailyWorkerEmployees(Connection conn, int companyId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement("SELECT EMPLOYEE_ID, EMPLOYMENT_TYPE, EMPLOYEE_NO, EMPLOYEE_NAME, "
					+ "DEPARTMENT, POSITION " + "FROM EMPLOYEE "
					+ "WHERE COMPANY_ID = ? AND EMPLOYMENT_TYPE IN ('일용직', 'DAILY') " + "ORDER BY EMPLOYEE_ID");
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			List<EmployeeLeave> result = new ArrayList<>();

			while (rs.next()) {
				// 사원 기본정보만 필요해서, 이미 만들어둔 EmployeeLeave 모델을 재사용
				// 社員基本情報のみが必要なため、すでに作成済みのEmployeeLeaveモデルを再利用
				EmployeeLeave row = new EmployeeLeave();
				row.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
				row.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
				row.setEmployeeNo(rs.getString("EMPLOYEE_NO"));
				row.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				row.setDepartment(rs.getString("DEPARTMENT"));
				row.setPosition(rs.getString("POSITION"));
				result.add(row);
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 새로운 일용직 근무 기록을 인서트 치기 전, 급여 귀속 ID를 먼저 확보한 뒤 저장하는 메서드
	// 新しい日雇い勤務記録をインサートする前に、給与帰属IDを先に確保してから保存するメソッド
	public void insert(Connection conn, DailyWorkRecord item) throws SQLException {

		// 근무일자를 바탕으로 해당 월의 급여 마스터 및 사원별 급여 귀속 ID를 자동 해결(조회 또는 생성)
		// 勤務日を基に該当月の給与マスターおよび社員別給与帰属IDを自動解決（照会または生成）
		Long payrollEmployeeId = resolvePayrollEmployeeId(conn, item.getEmployeeId(), item.getWorkDate());

		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement("INSERT INTO DAILY_WORK_RECORD ("
					+ "DAILY_WORK_RECORD_ID, EMPLOYEE_ID, PAYROLL_EMPLOYEE_ID, WORK_SITE_NAME, WORK_DATE, "
					+ "DAILY_WAGE, PAY_RATE, PAY_AMOUNT, INCOME_TAX_AMOUNT, LOCAL_INCOME_TAX_AMOUNT, NET_PAY_AMOUNT, "
					+ "REG_ID, MOD_ID" + ") VALUES ("
					+ "DAILY_WORK_RECORD_SEQ.NEXTVAL, "
					+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			pstmt.setInt(1, item.getEmployeeId());
			pstmt.setLong(2, payrollEmployeeId);
			pstmt.setString(3, item.getWorkSiteName());
			pstmt.setDate(4, item.getWorkDate());
			pstmt.setBigDecimal(5, item.getDailyWage());
			pstmt.setBigDecimal(6, item.getPayRate());
			pstmt.setBigDecimal(7, item.getPayAmount());
			pstmt.setBigDecimal(8, item.getIncomeTaxAmount());
			pstmt.setBigDecimal(9, item.getLocalIncomeTaxAmount());
			pstmt.setBigDecimal(10, item.getNetPayAmount());
			pstmt.setString(11, "SYSTEM");
			pstmt.setString(12, "SYSTEM");

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 기존 일용직 근무 기록을 수정(UPDATE)하는 메서드
	// 既存の日雇い勤務記録を修正（UPDATE）するメソッド
	public void update(Connection conn, DailyWorkRecord item) throws SQLException {

		// 근무일자를 다른 달로 옮겨 수정할 수도 있으므로, 매번 현재 근무일자 기준으로 다시 계산해서 갱신한다.
		// 勤務日を別の月に変更して修正する場合もあるため、毎回現在の勤務日を基準に再計算して更新する。
		Long payrollEmployeeId = resolvePayrollEmployeeId(conn, item.getEmployeeId(), item.getWorkDate());

		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement("UPDATE DAILY_WORK_RECORD SET "
					+ "PAYROLL_EMPLOYEE_ID = ?, WORK_SITE_NAME = ?, WORK_DATE = ?, DAILY_WAGE = ?, PAY_RATE = ?, PAY_AMOUNT = ?, "
					+ "INCOME_TAX_AMOUNT = ?, LOCAL_INCOME_TAX_AMOUNT = ?, NET_PAY_AMOUNT = ?, MOD_ID = ? "
					+ "WHERE DAILY_WORK_RECORD_ID = ?");

			pstmt.setLong(1, payrollEmployeeId);
			pstmt.setString(2, item.getWorkSiteName());
			pstmt.setDate(3, item.getWorkDate());
			pstmt.setBigDecimal(4, item.getDailyWage());
			pstmt.setBigDecimal(5, item.getPayRate());
			pstmt.setBigDecimal(6, item.getPayAmount());
			pstmt.setBigDecimal(7, item.getIncomeTaxAmount());
			pstmt.setBigDecimal(8, item.getLocalIncomeTaxAmount());
			pstmt.setBigDecimal(9, item.getNetPayAmount());
			pstmt.setString(10, "SYSTEM");
			pstmt.setInt(11, item.getDailyWorkRecordId());

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	/** 근무일자가 속한 귀속연월 + 급여-01차의 PAYROLL_EMPLOYEE_ID를 반환한다.
	 *  (payment.paymentMntDayWorker 급여입력관리 화면이 이 ID로 근무기록을 조회하므로, 근태관리에서
	 *  근무기록을 저장하는 시점에 미리 연결해둬야 그 화면에 바로 반영된다) 
	 * 勤務日が属する帰属年月 ＋ 給与-第1次のPAYROLL_EMPLOYEE_IDを返却する。
	 * （payment.paymentMntDayWorker 給与入力管理画面がこのIDで勤務記録を照会するため、勤怠管理で
	 * 勤務記録を保存する時点で事前に紐づけておく必要がある） */
	private Long resolvePayrollEmployeeId(Connection conn, int employeeId, java.sql.Date workDate) throws SQLException {
		LocalDate date = workDate.toLocalDate();
		String payYearMonth = String.format("%04d%02d", date.getYear(), date.getMonthValue());
		int paySequence = 1;

		Long payrollId = ensurePayrollExists(conn, payYearMonth, paySequence);
		return ensurePayrollEmployeeExists(conn, payrollId, employeeId);
	}

	// 해당 귀속연월/차수의 급여 마스터가 없으면 자동 생성하고, 있으면 ID를 리턴하는 내부 헬퍼
	// 該当帰属年月・次数の給与マスターがなければ自動生成し、あればIDをリターンする内部ヘルパー
	private Long ensurePayrollExists(Connection conn, String payYearMonth, int paySequence) throws SQLException {
		String selectSql = "SELECT PAYROLL_ID FROM PAYROLL WHERE PAY_YEAR_MONTH = ? AND PAY_SEQUENCE = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
			pstmt.setString(1, payYearMonth);
			pstmt.setInt(2, paySequence);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getLong("PAYROLL_ID");
				}
			}
		}

		String insertSql = "INSERT INTO PAYROLL (PAYROLL_ID, COMPANY_ID, PAY_YEAR_MONTH, PAY_SEQUENCE, "
				+ "SETTLEMENT_START_DATE, SETTLEMENT_END_DATE, PAYMENT_DATE, REG_ID, MOD_ID) "
				+ "VALUES (PAYROLL_SEQ.NEXTVAL, 1001, ?, ?, SYSDATE, SYSDATE, SYSDATE, 'SYSTEM', 'SYSTEM')";
		try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
			pstmt.setString(1, payYearMonth);
			pstmt.setInt(2, paySequence);
			pstmt.executeUpdate();
		}

		try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
			pstmt.setString(1, payYearMonth);
			pstmt.setInt(2, paySequence);
			try (ResultSet rs = pstmt.executeQuery()) {
				rs.next();
				return rs.getLong("PAYROLL_ID");
			}
		}
	}

	// 해당 급여 마스터에 사원별 급여 정보가 없으면 자동 생성하고, 있으면 ID를 리턴하는 내부 헬퍼
	// 該当給与マスターに社員別給与情報がなければ自動生成し、あればIDをリターンする内部ヘルパー
	private Long ensurePayrollEmployeeExists(Connection conn, Long payrollId, int employeeId) throws SQLException {
		String selectSql = "SELECT PAYROLL_EMPLOYEE_ID FROM PAYROLL_EMPLOYEE WHERE PAYROLL_ID = ? AND EMPLOYEE_ID = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
			pstmt.setLong(1, payrollId);
			pstmt.setInt(2, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getLong("PAYROLL_EMPLOYEE_ID");
				}
			}
		}

		String insertSql = "INSERT INTO PAYROLL_EMPLOYEE "
				+ "(PAYROLL_EMPLOYEE_ID, PAYROLL_ID, EMPLOYEE_ID, EMPLOYMENT_TYPE, INCOME_TYPE, "
				+ " TOTAL_PAY_AMOUNT, TOTAL_DEDUCTION_AMOUNT, NET_PAY_AMOUNT, REG_ID, MOD_ID) "
				+ "SELECT PAYROLL_EMPLOYEE_SEQ.NEXTVAL, ?, e.EMPLOYEE_ID, e.EMPLOYMENT_TYPE, '일반', 0, 0, 0, 'SYSTEM', 'SYSTEM' "
				+ "FROM EMPLOYEE e WHERE e.EMPLOYEE_ID = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
			pstmt.setLong(1, payrollId);
			pstmt.setInt(2, employeeId);
			pstmt.executeUpdate();
		}

		try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
			pstmt.setLong(1, payrollId);
			pstmt.setInt(2, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				rs.next();
				return rs.getLong("PAYROLL_EMPLOYEE_ID");
			}
		}
	}

	// 특정 일용직 근무 기록 건을 PK로 삭제하는 메서드
	// 特定の日雇い勤務記録案件をPKで削除するメソッド
	public void deleteById(Connection conn, int dailyWorkRecordId) throws SQLException {

		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement("DELETE FROM DAILY_WORK_RECORD WHERE DAILY_WORK_RECORD_ID = ?");
			pstmt.setInt(1, dailyWorkRecordId);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 특정 사원의 일용직 근무 기록 목록을 최근 근무일순으로 조회
	// 該当社員の日雇い勤務記録リストを最近の勤務日順に照会
	public List<DailyWorkRecord> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement("SELECT DAILY_WORK_RECORD_ID, EMPLOYEE_ID, WORK_SITE_NAME, WORK_DATE, "
					+ "DAILY_WAGE, PAY_RATE, PAY_AMOUNT, INCOME_TAX_AMOUNT, LOCAL_INCOME_TAX_AMOUNT, NET_PAY_AMOUNT "
					+ "FROM DAILY_WORK_RECORD " + "WHERE EMPLOYEE_ID = ? "
					+ "ORDER BY WORK_DATE DESC, DAILY_WORK_RECORD_ID DESC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();

			List<DailyWorkRecord> result = new ArrayList<>();

			while (rs.next()) {
				result.add(mapRow(rs));
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 특정 일용직 근무 기록 단건 조회
	// 特定の日雇い勤務記録単件照会
	public DailyWorkRecord selectById(Connection conn, int dailyWorkRecordId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement("SELECT DAILY_WORK_RECORD_ID, EMPLOYEE_ID, WORK_SITE_NAME, WORK_DATE, "
					+ "DAILY_WAGE, PAY_RATE, PAY_AMOUNT, INCOME_TAX_AMOUNT, LOCAL_INCOME_TAX_AMOUNT, NET_PAY_AMOUNT "
					+ "FROM DAILY_WORK_RECORD " + "WHERE DAILY_WORK_RECORD_ID = ?");
			pstmt.setInt(1, dailyWorkRecordId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return mapRow(rs);
			}

			return null;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// ResultSet 결과를 DailyWorkRecord 모델 객체에 매핑(포장)해 주는 공통 헬퍼 메서드
	// ResultSetの結果をDailyWorkRecordモデルオブジェクトにマッピング（パッケージング）する共通ヘルパーメソッド
	private DailyWorkRecord mapRow(ResultSet rs) throws SQLException {

		DailyWorkRecord item = new DailyWorkRecord();

		item.setDailyWorkRecordId(rs.getInt("DAILY_WORK_RECORD_ID"));
		item.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
		item.setWorkSiteName(rs.getString("WORK_SITE_NAME"));
		item.setWorkDate(rs.getDate("WORK_DATE"));
		item.setDailyWage(rs.getBigDecimal("DAILY_WAGE"));
		item.setPayRate(rs.getBigDecimal("PAY_RATE"));
		item.setPayAmount(rs.getBigDecimal("PAY_AMOUNT"));
		item.setIncomeTaxAmount(rs.getBigDecimal("INCOME_TAX_AMOUNT"));
		item.setLocalIncomeTaxAmount(rs.getBigDecimal("LOCAL_INCOME_TAX_AMOUNT"));
		item.setNetPayAmount(rs.getBigDecimal("NET_PAY_AMOUNT"));

		return item;
	}
}