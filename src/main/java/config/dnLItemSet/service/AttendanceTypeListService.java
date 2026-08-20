package config.dnLItemSet.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import config.dnLItemSet.dao.AttendanceTypeDao;
import config.model.AttendanceType;
import config.model.LeaveType;
import connection.ConnectionProvider;
import jdbc.JdbcUtil;

public class AttendanceTypeListService {

	private AttendanceTypeDao attendanceTypeDao = new AttendanceTypeDao();
	private LeaveTypeListService leaveTypeListService = new LeaveTypeListService();

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

	// 휴가/근태설정 관리화면 목록용 - 사용여부 상관없이 전부 조회
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

	// 근태기록/관리 입력폼 드롭다운용 - USE_YN='Y'인 근태항목 중에서도, 연결된 휴가항목이 "사용안함"으로
	// 바뀐 건 같이 빠져야 함 (근태항목 자체는 살아있어도 그 항목이 가리키는 휴가항목이 죽었으면 의미 없음)
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
			if (at.getLeaveTypeId() == null || activeLeaveTypeIds.contains(at.getLeaveTypeId())) {
				result.add(at);
			}
		}

		return result;
	}

	// 근태기록/관리 입력폼 드롭다운용 - 이미 연결된 근태항목이 있는(=근태항목으로 이미 등록된) 휴가항목은 중복이라 빼고,
	// 아직 근태항목으로 안 만들어진, 사용중(Y)인 휴가항목만 반환
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
			if ("Y".equals(lt.getUseYn()) && !mappedLeaveTypeIds.contains(lt.getLeaveTypeId())) {
				result.add(lt);
			}
		}

		return result;
	}

}
