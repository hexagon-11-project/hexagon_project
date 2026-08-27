package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.dnLItemSet.dao.AttendanceTypeDao;
import config.model.AttendanceType;
import config.model.LeaveType;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

// 근태 항목 목록 조회 서비스
// 勤怠項目リスト照会サービス
public class AttendanceTypeListService {

	private AttendanceTypeDao attendanceTypeDao = new AttendanceTypeDao();
	private LeaveTypeListService leaveTypeListService = new LeaveTypeListService();

	// 사용 중인 근태 항목 목록만 가져옴
	// 使用中の勤怠項目リストのみ取得
	public List<AttendanceType> getList(int companyId) {

		Connection conn = null;

		try {

			conn = ConnectionProvider.getConnection();
			return attendanceTypeDao.selectByCompanyId(conn, companyId);

		} catch (SQLException e) {

			throw new RuntimeException(e);

		} finally {

			JdbcUtil.close(conn);

		}

	}

	// 근태설정 관리화면용 (사용여부 상관없이 DB에 있는 거 전부 다 긁어옴)
	// 勤怠設定の管理画面用（使用有無に関係なくDBにあるものを全件取得）
	public List<AttendanceType> getAllList(int companyId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return attendanceTypeDao.selectAllByCompanyId(conn, companyId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 근태기록 입력폼 드롭다운용
	// 근태항목 자체는 살아있어도, 거기에 물려있는 휴가가 "사용안함(죽은 상태)"이면 드롭다운에서 빼야 함
	// 勤怠記録入力フォームのドロップダウン用
	// 勤怠項目自体は生きていても、そこに紐づく休暇が「使用しない（死んだ状態）」ならドロップダウンから除外する
	public List<AttendanceType> getListForEntryForm(int companyId) {

		List<AttendanceType> list = getList(companyId);

		java.util.Set<Integer> activeLeaveTypeIds = new java.util.HashSet<>();
		for (LeaveType lt : leaveTypeListService.getList(companyId)) {
			if ("Y".equals(lt.getUseYn())) {
				activeLeaveTypeIds.add(lt.getLeaveTypeId());
			}
		}

		List<AttendanceType> result = new java.util.ArrayList<>();
		for (AttendanceType at : list) {
			// 휴가랑 연결 안 된 순수 근태항목이거나, 연결된 휴가가 현재 살아있는 놈들만 결과에 담음
			// 休暇と紐づいていない純粋な勤怠項目か、紐づく休暇が現在生きているものだけを結果に詰める
			if (at.getLeaveTypeId() == null || activeLeaveTypeIds.contains(at.getLeaveTypeId())) {
				result.add(at);
			}
		}

		return result;
	}

	// 근태기록 입력폼 드롭다운용 2
	// 이미 근태항목이랑 매핑된 휴가는 중복이니까 날리고, 아직 매핑 안 된 순수 휴가항목만 추려서 던져줌
	// 勤怠記録入力フォームのドロップダウン用 2
	// 既に勤怠項目とマッピングされた休暇は重複なので飛ばし、まだマッピングされていない純粋な休暇項目だけを抽出して返す
	public List<LeaveType> getLeaveOnlyOptions(int companyId, List<AttendanceType> attendanceTypeList) {

		java.util.Set<Integer> mappedLeaveTypeIds = new java.util.HashSet<>();
		if (attendanceTypeList != null) {
			for (AttendanceType at : attendanceTypeList) {
				if (at.getLeaveTypeId() != null) {
					mappedLeaveTypeIds.add(at.getLeaveTypeId());
				}
			}
		}

		List<LeaveType> result = new java.util.ArrayList<>();
		for (LeaveType lt : leaveTypeListService.getList(companyId)) {
			// 살아있는 휴가 중에서, 매핑된 리스트에 없는 놈들만 골라냄
			// 生きている休暇の中で、マッピングリストにないものだけを選別する
			if ("Y".equals(lt.getUseYn()) && !mappedLeaveTypeIds.contains(lt.getLeaveTypeId())) {
				result.add(lt);
			}
		}

		return result;
	}

}