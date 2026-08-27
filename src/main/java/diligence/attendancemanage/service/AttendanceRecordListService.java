package diligence.attendancemanage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import diligence.attendancemanage.dao.AttendanceRecordDao;
import config.model.EmployeeLeave;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

// 근태 관리 화면에서 사용할 사원 목록 조회 비즈니스 로직을 처리하는 Service
// 勤怠管理画面で使用する社員リスト照会ビジネスロジックを処理する Service
public class AttendanceRecordListService {

	private AttendanceRecordDao attendanceRecordDao = new AttendanceRecordDao();

	// 회사 ID를 기준으로 해당 회사의 전체 사원 목록을 긁어오는 메서드
	// 会社IDを基準に該当会社の全社員リストをかき集めるメソッド
	public List<EmployeeLeave> getEmployeeList(int companyId) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			return attendanceRecordDao.selectEmployeesByCompanyId(conn, companyId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}