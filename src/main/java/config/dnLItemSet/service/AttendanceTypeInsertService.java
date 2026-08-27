package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.dnLItemSet.dao.AttendanceTypeDao;
import config.model.AttendanceType;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

// 근태 항목 등록 서비스
// 勤怠項目の登録サービス
public class AttendanceTypeInsertService {

	private AttendanceTypeDao attendanceTypeDao = new AttendanceTypeDao();

	// 근태 항목 새로 등록 (DB 트랜잭션 태워서 처리)
	// 勤怠項目を新しく登録（DBトランザクションをかけて処理）
	public void insert(AttendanceType item) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			attendanceTypeDao.insert(conn, item);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 화면에서 휴가 항목을 바로 선택했을 때 쓰는 로직
	// 이미 이 휴가에 물려있는 근태 항목이 있으면 그거 그냥 쓰고, 없으면 시스템이 새로 하나 만들어서 ID 던져줌
	// 画面で休暇項目を直接選択した時に使うロジック
	// すでにこの休暇に紐づいている勤怠項目があればそのまま使い、なければシステムが新しく作ってIDを返す
	public int resolveAttendanceTypeIdForLeaveType(int companyId, int leaveTypeId, String leaveName,
			String leaveCode) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();

			// 이미 연결된 거 있는지 찔러봄
			// 既に紐づいているものがあるかチェック
			AttendanceType existing = attendanceTypeDao.selectByLeaveTypeId(conn, companyId, leaveTypeId);
			if (existing != null) {
				return existing.getAttendanceTypeId();
			}

			conn.setAutoCommit(false);

			// 없으면 디폴트값(단위: DAY, 그룹: 휴가) 넣고 새로 하나 만들어줌
			// なければデフォルト値（単位: DAY, グループ: 休暇）を入れて新しく作成
			AttendanceType item = new AttendanceType();
			item.setCompanyId(companyId);
			item.setAttendanceCode(leaveCode != null ? leaveCode : ("LV" + leaveTypeId));
			item.setAttendanceName(leaveName);
			item.setUnitCode("DAY");
			item.setAttendanceGroupCode("휴가");
			item.setLeaveTypeId(leaveTypeId);
			item.setUseYn("Y");

			attendanceTypeDao.insert(conn, item);
			conn.commit();

			// 방금 만든 거 다시 조회해서 ID 가져옴
			// 今作ったものを再照会してIDを取得
			AttendanceType created = attendanceTypeDao.selectByLeaveTypeId(conn, companyId, leaveTypeId);
			return created.getAttendanceTypeId();

		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}