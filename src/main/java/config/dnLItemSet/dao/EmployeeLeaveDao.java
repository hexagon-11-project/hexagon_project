package config.dnLItemSet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.model.EmployeeLeave;
import config.model.EmployeeLeaveStatus;
import jdbc.JdbcUtil;

// 사원별 휴가(부여일수 등) 데이터를 처리하는 DAO
// 社員別の休暇（付与日数など）データを処理する DAO
public class EmployeeLeaveDao {

	// 특정 휴가항목 기준으로 전체 사원 목록이랑 부여된 일수를 싹 다 가져옴 (부여 안됐으면 null/0 처리)
	// 特定の休暇項目を基準に、全社員リストと付与日数を全て取得（付与されていなければnull/0扱い）
	public List<EmployeeLeave> selectByLeaveTypeId(Connection conn, int leaveTypeId, int companyId)
			throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement("SELECT e.EMPLOYEE_ID, e.EMPLOYMENT_TYPE, e.EMPLOYEE_NO, "
					+ "e.EMPLOYEE_NAME, e.DEPARTMENT, e.POSITION, e.HIRE_DATE, e.RESIGN_DATE, "
					+ "el.EMPLOYEE_LEAVE_ID, el.GRANTED_DAYS " + "FROM EMPLOYEE e "
					+ "LEFT JOIN EMPLOYEE_LEAVE el ON el.EMPLOYEE_ID = e.EMPLOYEE_ID AND el.LEAVE_TYPE_ID = ? "
					+ "WHERE e.COMPANY_ID = ? " + "ORDER BY e.EMPLOYEE_ID");
			pstmt.setInt(1, leaveTypeId);
			pstmt.setInt(2, companyId);
			rs = pstmt.executeQuery();

			List<EmployeeLeave> result = new ArrayList<>();

			while (rs.next()) {

				EmployeeLeave item = new EmployeeLeave();

				item.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
				item.setLeaveTypeId(leaveTypeId);
				item.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
				item.setEmployeeNo(rs.getString("EMPLOYEE_NO"));
				item.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				item.setDepartment(rs.getString("DEPARTMENT"));
				item.setPosition(rs.getString("POSITION"));
				item.setHireDate(rs.getDate("HIRE_DATE"));
				java.sql.Date resignDate = rs.getDate("RESIGN_DATE");
				item.setEmploymentStatus(resignDate == null ? "재직" : "퇴직");

				int employeeLeaveId = rs.getInt("EMPLOYEE_LEAVE_ID");
				if (!rs.wasNull()) {
					item.setEmployeeLeaveId(employeeLeaveId);
				}
				item.setGrantedDays(rs.getBigDecimal("GRANTED_DAYS"));

				result.add(item);
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 특정 사원의 특정 휴가항목 부여일수만 가져옴 (근태기록 화면의 휴가일수 현황 버튼용)
	// 特定社員の特定休暇項目の付与日数のみ取得（勤怠記録画面の休暇日数現況ボタン用）
	public java.math.BigDecimal selectGrantedDays(Connection conn, int employeeId, int leaveTypeId)
			throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(
					"SELECT GRANTED_DAYS FROM EMPLOYEE_LEAVE WHERE EMPLOYEE_ID = ? AND LEAVE_TYPE_ID = ?");
			pstmt.setInt(1, employeeId);
			pstmt.setInt(2, leaveTypeId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getBigDecimal("GRANTED_DAYS");
			}

			return null;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// [휴가일수 현황] 팝업용 - 이 사원이 부여받은 휴가별로 총 부여일수랑 사용일수(근태기록 합계)를 같이 가져옴
	// [休暇日数現況] ポップアップ用 - この社員が付与された休暇ごとに、総付与日数と使用日数（勤怠記録の合計）を合わせて取得
	public List<EmployeeLeaveStatus> selectStatusByEmployeeId(Connection conn, int employeeId)
			throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement("SELECT e.EMPLOYMENT_TYPE, e.EMPLOYEE_NAME, e.POSITION, lt.LEAVE_NAME, "
					+ "el.GRANTED_DAYS, "
					+ "NVL((SELECT SUM(ar.DAY_COUNT) FROM ATTENDANCE_RECORD ar "
					+ "JOIN ATTENDANCE_TYPE at ON at.ATTENDANCE_TYPE_ID = ar.ATTENDANCE_TYPE_ID "
					+ "WHERE ar.EMPLOYEE_ID = el.EMPLOYEE_ID AND at.LEAVE_TYPE_ID = el.LEAVE_TYPE_ID), 0) AS USED_DAYS "
					+ "FROM EMPLOYEE_LEAVE el " + "JOIN EMPLOYEE e ON e.EMPLOYEE_ID = el.EMPLOYEE_ID "
					+ "JOIN LEAVE_TYPE lt ON lt.LEAVE_TYPE_ID = el.LEAVE_TYPE_ID " + "WHERE el.EMPLOYEE_ID = ? "
					+ "ORDER BY lt.DISPLAY_ORDER, lt.LEAVE_TYPE_ID");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();

			List<EmployeeLeaveStatus> result = new ArrayList<>();

			while (rs.next()) {

				EmployeeLeaveStatus item = new EmployeeLeaveStatus();

				item.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
				item.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				item.setPosition(rs.getString("POSITION"));
				item.setLeaveName(rs.getString("LEAVE_NAME"));
				item.setTotalDays(rs.getBigDecimal("GRANTED_DAYS"));
				item.setUsedDays(rs.getBigDecimal("USED_DAYS"));

				result.add(item);
			}

			return result;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 특정 사원과 휴가항목에 매핑된 ID 조회 (데이터 이미 있는지 체크할 때 씀)
	// 特定社員と休暇項目にマッピングされたIDを取得（データが既に存在するかチェックする時に使用）
	public Integer selectEmployeeLeaveId(Connection conn, int employeeId, int leaveTypeId) throws SQLException {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement(
					"SELECT EMPLOYEE_LEAVE_ID FROM EMPLOYEE_LEAVE WHERE EMPLOYEE_ID = ? AND LEAVE_TYPE_ID = ?");
			pstmt.setInt(1, employeeId);
			pstmt.setInt(2, leaveTypeId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("EMPLOYEE_LEAVE_ID");
			}

			return null;

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 사원한테 휴가일수 신규 부여 (등록)
	// 社員に休暇日数を新規付与（登録）
	public void insert(Connection conn, int employeeId, int leaveTypeId, java.math.BigDecimal grantedDays)
			throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("INSERT INTO EMPLOYEE_LEAVE ("
					+ "EMPLOYEE_LEAVE_ID, EMPLOYEE_ID, LEAVE_TYPE_ID, GRANTED_DAYS, "
					+ "REG_ID, MOD_ID, CREATED_AT, UPDATED_AT" + ") VALUES ("
					+ "EMP_LEAVE_SEQ.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE, SYSDATE" + ")");

			pstmt.setInt(1, employeeId);
			pstmt.setInt(2, leaveTypeId);
			pstmt.setBigDecimal(3, grantedDays);
			pstmt.setString(4, "SYSTEM");
			pstmt.setString(5, "SYSTEM");

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 이미 부여된 휴가일수 수정
	// 既に付与された休暇日数を修正
	public void update(Connection conn, int employeeLeaveId, java.math.BigDecimal grantedDays) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement(
					"UPDATE EMPLOYEE_LEAVE SET GRANTED_DAYS = ?, MOD_ID = ?, UPDATED_AT = SYSDATE "
							+ "WHERE EMPLOYEE_LEAVE_ID = ?");

			pstmt.setBigDecimal(1, grantedDays);
			pstmt.setString(2, "SYSTEM");
			pstmt.setInt(3, employeeLeaveId);

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 특정 사원의 특정 휴가 부여 내역 삭제
	// 特定社員の特定休暇の付与履歴を削除
	public void deleteByEmployeeAndLeaveType(Connection conn, int employeeId, int leaveTypeId) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_LEAVE WHERE EMPLOYEE_ID = ? AND LEAVE_TYPE_ID = ?");
			pstmt.setInt(1, employeeId);
			pstmt.setInt(2, leaveTypeId);
			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}