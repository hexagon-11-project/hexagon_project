package diligence.searchmonth.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.model.AttendanceRecord;
import jdbc.JdbcUtil;

// 근태 조회 화면에서 조건(월 범위, 근태항목, 정렬조건)에 맞는 근태 기록을 동적 쿼리로 조회하는 DAO
// 勤怠照会画面で条件（月範囲、勤怠項目、並び替え条件）に合う勤怠記録を動的クエリで照会する DAO
public class AttendanceSearchDao {

	// 정렬 파라미터는 사용자 입력값을 그대로 SQL에 이어붙이면 안 되니, 화이트리스트로만 매핑해서 사용
	// 並び替えパラメータはユーザー入力をそのままSQLに連結してはならないため、ホワイトリストでのみマッピングして使用
	public static final String SORT_NAME = "氏名順";
	public static final String SORT_DEPARTMENT = "部署順";
	public static final String SORT_DATE = "日付順";

	// 특정 월 범위 내에 걸쳐 있는 전체 사원의 근태 기록을 조회하는 메서드
	// 特定の月の範囲にわたる全社員の勤怠記録を照会するメソッド
	public List<AttendanceRecord> selectByMonth(Connection conn, int companyId, Date monthStart, Date monthEnd,
			Integer attendanceTypeId, String sortKey) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// 동적 SQL 조립을 위한 StringBuilder 활용
			// 動的SQL組み立てのためのStringBuilder活用
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ar.ATTENDANCE_ID, ar.EMPLOYEE_ID, ar.ATTENDANCE_TYPE_ID, ar.START_DATE, ar.END_DATE, ");
			sql.append("ar.DAY_COUNT, ar.HOUR_COUNT, ar.ALLOWANCE_AMOUNT, ar.DESCRIPTION, ");
			sql.append("e.EMPLOYEE_NAME, e.DEPARTMENT, ");
			sql.append("at.ATTENDANCE_NAME, at.UNIT_CODE ");
			sql.append("FROM ATTENDANCE_RECORD ar ");
			sql.append("JOIN EMPLOYEE e ON e.EMPLOYEE_ID = ar.EMPLOYEE_ID ");
			sql.append("JOIN ATTENDANCE_TYPE at ON at.ATTENDANCE_TYPE_ID = ar.ATTENDANCE_TYPE_ID ");
			
			// 회사 ID 일치 및 해당 월과 기간이 겹치는(Overlap) 근태 기록을 추출하는 핵심 조건식
			// 会社IDの一致、および該当月と期間が重なる（オーバーラップ）勤怠記録を抽出する核心条件式
			sql.append("WHERE e.COMPANY_ID = ? AND ar.START_DATE <= ? AND ar.END_DATE >= ? ");
			
			// 근태 항목이 선택된 경우에만 조건절 추가
			// 勤怠項目が選択された場合のみ条件節を追加
			if (attendanceTypeId != null) {
				sql.append("AND ar.ATTENDANCE_TYPE_ID = ? ");
			}
			
			// 화이트리스트 헬퍼를 통해 안전한 ORDER BY 절 추가
			// ホワイトリストヘルパーを通じて安全なORDER BY句を追加
			sql.append("ORDER BY ").append(resolveOrderBy(sortKey));

			pstmt = conn.prepareStatement(sql.toString());

			// 파라미터 바인딩 (월말/월초 순서가 쿼리 조건식에 맞게 매핑되어 있음에 유의)
			// パラメータバインディング（月末／月初めの順序がクエリ条件式に合わせてマッピングされていることに注意）
			int idx = 1;
			pstmt.setInt(idx++, companyId);
			pstmt.setDate(idx++, monthEnd);   // ar.START_DATE <= monthEnd
			pstmt.setDate(idx++, monthStart); // ar.END_DATE >= monthStart
			if (attendanceTypeId != null) {
				pstmt.setInt(idx++, attendanceTypeId);
			}

			rs = pstmt.executeQuery();

			List<AttendanceRecord> result = new ArrayList<>();

			while (rs.next()) {

				AttendanceRecord item = new AttendanceRecord();

				// 조회된 결과 행을 AttendanceRecord 모델 객체에 안전하게 매핑
				// 照会された結果行をAttendanceRecordモデルオブジェクトに安全にマッピング
				item.setAttendanceId(rs.getInt("ATTENDANCE_ID"));
				item.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
				item.setAttendanceTypeId(rs.getInt("ATTENDANCE_TYPE_ID"));
				item.setStartDate(rs.getDate("START_DATE"));
				item.setEndDate(rs.getDate("END_DATE"));
				item.setDayCount(rs.getBigDecimal("DAY_COUNT"));
				item.setHourCount(rs.getBigDecimal("HOUR_COUNT"));
				item.setAllowanceAmount(rs.getBigDecimal("ALLOWANCE_AMOUNT"));
				item.setDescription(rs.getString("DESCRIPTION"));
				item.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				item.setDepartment(rs.getString("DEPARTMENT"));
				item.setAttendanceName(rs.getString("ATTENDANCE_NAME"));
				item.setUnitCode(rs.getString("UNIT_CODE"));

				result.add(item);
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 정렬 키 값에 따라 안전한 ORDER BY SQL 조각을 리턴하는 화이트리스트 헬퍼 메서드
	// ソートキー値に応じて安全なORDER BY SQL断片をリターンするホワイトリストヘルパーメソッド
	private String resolveOrderBy(String sortKey) {
		if (SORT_DEPARTMENT.equals(sortKey)) {
			return "e.DEPARTMENT, e.EMPLOYEE_NAME";
		}
		if (SORT_DATE.equals(sortKey)) {
			return "ar.START_DATE, e.EMPLOYEE_NAME";
		}
		return "e.EMPLOYEE_NAME, ar.START_DATE"; // 기본값: 성명순 (デフォルト値: 氏名順)
	}
}