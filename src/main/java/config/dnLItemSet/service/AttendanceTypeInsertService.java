package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;

import config.dnLItemSet.dao.AttendanceTypeDao;
import config.model.AttendanceType;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class AttendanceTypeInsertService {

	private AttendanceTypeDao attendanceTypeDao = new AttendanceTypeDao();

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

	// 근태기록/관리 화면에서 휴가항목(LEAVE_TYPE)을 근태항목 대신 직접 선택했을 때 -
	// 이미 연결된 근태항목이 있으면 그걸 재사용하고, 없으면 하나 자동 생성해서 그 id를 반환
	public int resolveAttendanceTypeIdForLeaveType(int companyId, int leaveTypeId, String leaveName,
			String leaveCode) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();

			AttendanceType existing = attendanceTypeDao.selectByLeaveTypeId(conn, companyId, leaveTypeId);
			if (existing != null) {
				return existing.getAttendanceTypeId();
			}

			conn.setAutoCommit(false);

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
