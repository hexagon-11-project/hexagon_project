package diligence.dailyworkrecord.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.model.DailyWorkRecord;
import config.model.EmployeeLeave;
import connection.ConnectionProvider;
import diligence.dailyworkrecord.dao.DailyWorkRecordDao;
import jdbc.JdbcUtil;

// 일용직 근무 기록 비즈니스 로직(목록 조회, 단건 조회, 등록/수정/삭제, 세금 자동 계산)을 총괄하는 Service 클래스
// 日雇い勤務記録のビジネスロジック（リスト照会、単件照会、登録／修正／削除、税金自動計算）を総括する Service クラス
public class DailyWorkRecordService {

	// 일용근로소득 원천징수 기준 (근로소득공제 15만원, 세율 6%, 세액공제 55% => 실효세율 2.7%)
	// 日雇労働所得源泉徴収基準（勤労所得控除15万ウォン、税率6%、税額控除55% => 実効税率2.7%）
	private static final BigDecimal DAILY_INCOME_DEDUCTION = BigDecimal.valueOf(150_000);
	private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.06);
	private static final BigDecimal TAX_CREDIT_RATE = BigDecimal.valueOf(0.45); // (1 - 55% 세액공제)
	private static final BigDecimal LOCAL_TAX_RATE = BigDecimal.valueOf(0.1);

	private DailyWorkRecordDao dailyWorkRecordDao = new DailyWorkRecordDao();

	// 회사 ID를 기준으로 일용직(DAILY) 근무가 가능한 사원 목록을 조회해 오는 메서드
	// 会社IDを基準に日雇い（DAILY）勤務が可能な社員リストを照会するメソッド
	public List<EmployeeLeave> getDailyWorkerEmployees(int companyId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return dailyWorkRecordDao.selectDailyWorkerEmployees(conn, companyId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 특정 사원의 일용직 근무 기록 전체 목록을 조회하는 메서드
	// 該当社員の日雇い勤務記録の全リストを照会するメソッド
	public List<DailyWorkRecord> getListByEmployeeId(int employeeId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return dailyWorkRecordDao.selectByEmployeeId(conn, employeeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 특정 일용직 근무 기록의 ID(PK)로 단건 상세 정보를 조회하는 메서드
	// 特定の日雇い勤務記録のID（PK）で単件の詳細情報を照会するメソッド
	public DailyWorkRecord getById(int dailyWorkRecordId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return dailyWorkRecordDao.selectById(conn, dailyWorkRecordId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 일용직 근무 기록을 등록하기 전 세금을 자동 계산한 뒤 DB에 저장하는 메서드
	// 日雇い勤務記録を登録する前に税金を自動計算した後、DBに保存するメソッド
	public void insert(DailyWorkRecord item) {

		calculateTax(item); // 세금 및 지급액 자동 계산 (税金および支給額の自動計算)

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			dailyWorkRecordDao.insert(conn, item);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 일용직 근무 기록을 수정하기 전 세금을 다시 계산한 뒤 DB에 갱신하는 메서드
	// 日雇い勤務記録を修正する前に税金を再計算した後、DBに更新するメソッド
	public void update(DailyWorkRecord item) {

		calculateTax(item); // 변경된 일당/지급율 기준으로 재계산 (変更された日給／支給率基準で再計算)

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			dailyWorkRecordDao.update(conn, item);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 특정 일용직 근무 기록을 삭제하는 메서드
	// 特定の日雇い勤務記録を削除するメソッド
	public void delete(int dailyWorkRecordId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			dailyWorkRecordDao.deleteById(conn, dailyWorkRecordId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 일당·지급율로 지급액/소득세/지방소득세/실지급액을 계산해서 item에 채워넣음
	// (화면의 "자동계산" 표시는 미리보기일 뿐이고, 실제 저장값은 항상 서버에서 다시 계산 - 클라이언트 값을 신뢰하지 않음)
	// 日給・支給率で支給額／所得税／地方所得税／実支給額を計算してitemに埋め込む
	// （画面の「自動計算」表示はプレビューにすぎず、実際の保存値は常にサーバーで再計算 - クライアントの値を信頼しない）
	private void calculateTax(DailyWorkRecord item) {

		BigDecimal dailyWage = item.getDailyWage() == null ? BigDecimal.ZERO : item.getDailyWage();
		BigDecimal payRate = item.getPayRate() == null ? BigDecimal.ONE : item.getPayRate();

		// 지급액 = 일당 * 지급율 (반올림 처리)
		// 支給額 ＝ 日給 × 支給率（四捨五入処理）
		BigDecimal payAmount = dailyWage.multiply(payRate).setScale(0, RoundingMode.HALF_UP);

		// 과세표준 = 지급액 - 일용근로소득 공제(15만원) (0보다 작으면 0으로 보정)
		// 課税標準 ＝ 支給額 － 日雇労働所得控除（15万ウォン）（0より小さければ0に補正）
		BigDecimal taxableBase = payAmount.subtract(DAILY_INCOME_DEDUCTION);
		if (taxableBase.compareTo(BigDecimal.ZERO) < 0) {
			taxableBase = BigDecimal.ZERO;
		}

		// 10원 미만 절사 (소득세 및 지방소득세 계산)
		// 10ウォン未満切り捨て（所得税および地方所得税の計算）
		BigDecimal incomeTax = taxableBase.multiply(TAX_RATE).multiply(TAX_CREDIT_RATE).setScale(-1,
				RoundingMode.DOWN);
		BigDecimal localTax = incomeTax.multiply(LOCAL_TAX_RATE).setScale(-1, RoundingMode.DOWN);
		BigDecimal netPay = payAmount.subtract(incomeTax).subtract(localTax);

		item.setPayAmount(payAmount);
		item.setIncomeTaxAmount(incomeTax);
		item.setLocalIncomeTaxAmount(localTax);
		item.setNetPayAmount(netPay);
	}
}