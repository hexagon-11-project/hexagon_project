package diligence.searchmonth.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import config.model.AttendanceRecord;
import connection.ConnectionProvider;
import diligence.searchmonth.dao.AttendanceSearchDao;
import jdbc.JdbcUtil;

// 근태 조회 관련 비즈니스 로직 처리 및 DB 커넥션/트랜잭션(조회 전용) 자원을 관리하는 Service 클래스
// 勤怠照会関連のビジネスロジック処理およびDBコネクション／トランザクション（照会専用）リソースを管理する Service クラス
public class AttendanceSearchService {

	private AttendanceSearchDao attendanceSearchDao = new AttendanceSearchDao();

	// 지정된 회사 ID, 월 범위, 근태항목, 정렬 조건을 바탕으로 전체 사원의 근태 기록 목록을 DAO에서 긁어오는 메서드
	// 指定された会社ID、月範囲、勤怠項目、並び替え条件を基に全社員の勤怠記録リストをDAOからかき集めるメソッド
	public List<AttendanceRecord> getListByMonth(int companyId, Date monthStart, Date monthEnd,
			Integer attendanceTypeId, String sortKey) {

		Connection conn = null;

		try {
			// 데이터베이스 커넥션 획득
			// データベースコネクションの獲得
			conn = ConnectionProvider.getConnection();
			
			// DAO의 월별 근태 조회 메서드를 호출하여 결과 리스트 반환
			// DAOの月別勤怠照会メソッドを呼び出して結果リストを返却
			return attendanceSearchDao.selectByMonth(conn, companyId, monthStart, monthEnd, attendanceTypeId, sortKey);
		} catch (SQLException e) {
			// 체크 예외인 SQLException을 런타임 예외로 감싸서 상위 계층(핸들러)으로 전파
			// チェック例外であるSQLExceptionをランタイム例外で包んで上位層（ハンドラー）へ伝播
			throw new RuntimeException(e);
		} finally {
			// 사용이 끝난 DB 커넥션 자원을 안전하게 반납 (메모리 누수 방지)
			// 使用が終了したDBコネクションリソースを安全に返却（メモリリーク防止）
			JdbcUtil.close(conn);
		}
	}
}