package config.dnLItemSet.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.dnLItemSet.dao.EmployeeLeaveDao;
import config.model.EmployeeLeave;
import config.model.EmployeeLeaveStatus;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

// 사원별 휴가(연차 등) 부여/삭제 등 현황을 관리하는 서비스
// 社員別の休暇（有休など）の付与・削除など、状況を管理するサービス
public class EmployeeLeaveManageService {

	private EmployeeLeaveDao employeeLeaveDao = new EmployeeLeaveDao();

	// 특정 휴가 항목(예: 연차) 기준으로 사원들 목록이랑 각자 부여받은 일수 쫙 뽑아옴
	// 特定の休暇項目（例：有休）を基準に、社員リストとそれぞれ付与された日数をざっと抽出する
	public List<EmployeeLeave> getList(int leaveTypeId, int companyId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return employeeLeaveDao.selectByLeaveTypeId(conn, leaveTypeId, companyId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// [휴가일수 현황] 팝업용 - 근태 화면에서 사원 클릭했을 때 띄워줄 부여/사용 내역
	// [休暇日数現況] ポップアップ用 - 勤怠画面で社員をクリックした時に表示する付与・使用履歴
	public List<EmployeeLeaveStatus> getStatusByEmployeeId(int employeeId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return employeeLeaveDao.selectStatusByEmployeeId(conn, employeeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 체크한 사원들 휴가 일수 일괄 저장 처리 (트랜잭션 태움)
	// 이미 부여된 내역이 있으면 업데이트 치고, 없으면 새로 인서트 쳐줌 (Upsert 느낌)
	// チェックした社員の休暇日数を一括保存処理（トランザクションに乗せる）
	// 既に付与履歴があればアップデートをかけ、なければ新規でインサートする（Upsertのイメージ）
	public void saveGrantedDays(int leaveTypeId, int[] employeeIds, BigDecimal[] grantedDaysList) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			for (int i = 0; i < employeeIds.length; i++) {

				int employeeId = employeeIds[i];
				BigDecimal grantedDays = grantedDaysList[i];

				// 데이터 있는지 먼저 찔러봄
				// データがあるか先にチェックする
				Integer employeeLeaveId = employeeLeaveDao.selectEmployeeLeaveId(conn, employeeId, leaveTypeId);

				if (employeeLeaveId == null) {
					employeeLeaveDao.insert(conn, employeeId, leaveTypeId, grantedDays);
				} else {
					employeeLeaveDao.update(conn, employeeLeaveId, grantedDays);
				}
			}

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 체크한 사원들의 휴가 부여 기록 자체를 완전 날려버림 (0일로 바꾸는 게 아니라 DB에서 아예 지움)
	// チェックした社員の休暇付与履歴自体を完全に飛ばす（0日に変更するのではなくDBから完全に削除）
	public void deleteGrantedDays(int leaveTypeId, int[] employeeIds) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			for (int employeeId : employeeIds) {
				employeeLeaveDao.deleteByEmployeeAndLeaveType(conn, employeeId, leaveTypeId);
			}

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}