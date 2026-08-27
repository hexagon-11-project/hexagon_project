package diligence.dayWorkerSearchMonth.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.model.DailyWorkRecord;
import jdbc.JdbcUtil;

// 일용직 근무 조회 화면에서 조건(월 범위, 현장명, 사원명)에 맞는 근무 기록을 동적 쿼리로 조회하는 DAO
// 日雇い勤務照会画面で条件（月範囲、現場名、社員名）に合う勤務記録を動的クエリで照会する DAO
public class DayWorkerSearchDao {

	// 조회월 범위 및 선택적 검색 조건(현장명, 사원명 키워드)을 반영해 일용직 근무 기록을 조회하는 메서드
	// 照会月の範囲および選択的検索条件（現場名、社員名キーワード）を反映して日雇い勤務記録を照会するメソッド
	public List<DailyWorkRecord> selectByMonth(Connection conn, int companyId, Date monthStart, Date monthEnd,
			String workSiteName, String employeeNameKeyword) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// 동적 SQL 조립을 위한 StringBuilder 활용
			// 動的SQL組み立てのためのStringBuilder活用
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT e.EMPLOYEE_NAME, dwr.WORK_DATE, dwr.WORK_SITE_NAME, dwr.DAILY_WAGE, dwr.PAY_RATE, ");
			sql.append("dwr.PAY_AMOUNT, dwr.INCOME_TAX_AMOUNT, dwr.LOCAL_INCOME_TAX_AMOUNT, dwr.NET_PAY_AMOUNT ");
			sql.append("FROM DAILY_WORK_RECORD dwr ");
			sql.append("JOIN EMPLOYEE e ON e.EMPLOYEE_ID = dwr.EMPLOYEE_ID ");
			sql.append("WHERE e.COMPANY_ID = ? AND dwr.WORK_DATE BETWEEN ? AND ? ");
			
			// 현장명이 입력된 경우에만 조건절 추가
			// 現場名が入力された場合のみ条件節を追加
			if (workSiteName != null && !workSiteName.isBlank()) {
				sql.append("AND dwr.WORK_SITE_NAME = ? ");
			}
			
			// 사원명 검색어가 입력된 경우에만 부분 일치(LIKE) 조건절 추가
			// 社員名検索ワードが入力された場合のみ部分一致（LIKE）条件節を追加
			if (employeeNameKeyword != null && !employeeNameKeyword.isBlank()) {
				sql.append("AND e.EMPLOYEE_NAME LIKE ('%' || ? || '%') ");
			}
			sql.append("ORDER BY e.EMPLOYEE_NAME, dwr.WORK_DATE");

			pstmt = conn.prepareStatement(sql.toString());

			// 동적으로 추가된 조건 순서에 맞춰 인덱스(idx)를 증가시키며 파라미터 바인딩
			// 動的に追加された条件の順序に合わせてインデックス（idx）をインクリメントしながらパラメータバインディング
			int idx = 1;
			pstmt.setInt(idx++, companyId);
			pstmt.setDate(idx++, monthStart);
			pstmt.setDate(idx++, monthEnd);
			if (workSiteName != null && !workSiteName.isBlank()) {
				pstmt.setString(idx++, workSiteName);
			}
			if (employeeNameKeyword != null && !employeeNameKeyword.isBlank()) {
				pstmt.setString(idx++, employeeNameKeyword);
			}

			rs = pstmt.executeQuery();

			List<DailyWorkRecord> result = new ArrayList<>();

			while (rs.next()) {

				DailyWorkRecord item = new DailyWorkRecord();

				// 조인된 사원 이름 및 일용직 근무 관련 각종 금액 데이터 매핑
				// JOINされた社員名および日雇い勤務に関する各種金額データをマッピング
				item.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				item.setWorkDate(rs.getDate("WORK_DATE"));
				item.setWorkSiteName(rs.getString("WORK_SITE_NAME"));
				item.setDailyWage(rs.getBigDecimal("DAILY_WAGE"));
				item.setPayRate(rs.getBigDecimal("PAY_RATE"));
				item.setPayAmount(rs.getBigDecimal("PAY_AMOUNT"));
				item.setIncomeTaxAmount(rs.getBigDecimal("INCOME_TAX_AMOUNT"));
				item.setLocalIncomeTaxAmount(rs.getBigDecimal("LOCAL_INCOME_TAX_AMOUNT"));
				item.setNetPayAmount(rs.getBigDecimal("NET_PAY_AMOUNT"));

				result.add(item);
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}