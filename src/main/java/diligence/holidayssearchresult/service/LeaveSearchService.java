package diligence.holidayssearchresult.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.model.AttendanceRecord;
import config.model.EmployeeLeaveStatus;
import connection.ConnectionProvider;
import diligence.holidayssearchresult.dao.LeaveSearchDao;
import jdbc.JdbcUtil;

// 휴가 조회 관련 비즈니스 로직(휴가 현황 목록 조회, 상세 사용 이력 조회)과 DB 커넥션을 관리하는 Service 클래스
// 休暇照会関連のビジネスロジック（休暇現状リスト照会、詳細使用履歴照会）およびDBコネクションを管理する Service クラス
public class LeaveSearchService {

	private LeaveSearchDao leaveSearchDao = new LeaveSearchDao();

	// 회사 ID, 휴가항목 ID, 기준 연도, 정렬 조건을 받아 전체 사원의 휴가 현황 목록을 DAO에서 가져오는 메서드
	// 会社ID、休暇項目ID、基準年度、並び替え条件を受けて全社員の休暇現状リストをDAOから持ってくるメソッド
	public List<EmployeeLeaveStatus> getStatusByLeaveType(int companyId, int leaveTypeId, int year, String sortKey) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return leaveSearchDao.selectStatusByLeaveType(conn, companyId, leaveTypeId, year, sortKey);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 특정 사원이 특정 연도/휴가항목으로 실제로 사용한 상세 근태 기록 내역을 DAO에서 가져오는 메서드
	// 特定の社員が特定の年度／休暇項目で実際に使用した詳細勤怠記録履歴をDAOから持ってくるメソッド
	public List<AttendanceRecord> getUsageDetail(int employeeId, int leaveTypeId, int year) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return leaveSearchDao.selectUsageDetail(conn, employeeId, leaveTypeId, year);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}