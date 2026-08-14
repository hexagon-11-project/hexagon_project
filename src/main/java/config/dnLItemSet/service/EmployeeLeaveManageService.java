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

public class EmployeeLeaveManageService {

	private EmployeeLeaveDao employeeLeaveDao = new EmployeeLeaveDao();

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

	// [휴가일수 현황] 팝업용 - 근태기록/관리 화면에서 사원을 선택했을 때 호출
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

	// 체크된 사원들의 휴가일수를 한 번에 저장 - 이미 부과기록이 있으면 수정, 없으면 새로 추가
	public void saveGrantedDays(int leaveTypeId, int[] employeeIds, BigDecimal[] grantedDaysList) {

		Connection conn = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			for (int i = 0; i < employeeIds.length; i++) {

				int employeeId = employeeIds[i];
				BigDecimal grantedDays = grantedDaysList[i];

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

	// 체크된 사원들의 휴가부과 기록을 통째로 삭제 (0일로 만드는 게 아니라 기록 자체를 지움)
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
