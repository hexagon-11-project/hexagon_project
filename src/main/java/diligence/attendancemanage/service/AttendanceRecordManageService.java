package diligence.attendancemanage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.model.AttendanceRecord;
import connection.ConnectionProvider;
import diligence.attendancemanage.dao.AttendanceRecordDao;
import jdbc.JdbcUtil;

// 사원별 근태 기록의 조회, 등록, 수정, 삭제 및 트랜잭션을 총괄하는 Service 클래스
// 社員別の勤怠記録の照会、登録、修正、削除、およびトランザクションを総括する Service クラス
public class AttendanceRecordManageService {

	private AttendanceRecordDao attendanceRecordDao = new AttendanceRecordDao();

	// 특정 사원의 전체 근태 기록 목록을 조회해 오는 메서드
	// 該当社員の全勤怠記録リストを照会するメソッド
	public List<AttendanceRecord> getListByEmployeeId(int employeeId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return attendanceRecordDao.selectByEmployeeId(conn, employeeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 새로운 근태 기록을 트랜잭션 단위로 안전하게 등록하는 메서드
	// 新しい勤怠記録をトランザクション単位で安全に登録するメソッド
	public void insert(AttendanceRecord item) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false); // 트랜잭션 시작 (トランザクション開始)

			attendanceRecordDao.insert(conn, item);

			conn.commit(); // 성공 시 커밋 (成功時コミット)
		} catch (SQLException e) {
			JdbcUtil.rollback(conn); // 예외 발생 시 롤백 (例外発生時ロールバック)
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 특정 근태 기록의 ID(PK)로 단건 상세 정보를 조회하는 메서드
	// 特定の勤怠記録のID（PK）で単件の詳細情報を照会するメソッド
	public AttendanceRecord getById(int attendanceId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return attendanceRecordDao.selectById(conn, attendanceId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 기존 근태 기록 내용을 수정하고 트랜잭션을 처리하는 메서드
	// 既存の勤怠記録内容を修正し、トランザクションを処理するメソッド
	public void update(AttendanceRecord item) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			attendanceRecordDao.update(conn, item);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 특정 근태 기록을 삭제하고 트랜잭션을 처리하는 메서드
	// 特定の勤怠記録を削除し、トランザクションを処理するメソッド
	public void delete(int attendanceId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			attendanceRecordDao.delete(conn, attendanceId);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}