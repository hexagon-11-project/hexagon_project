package person.employeeCard.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.employee.model.EmployeeAppointment;
import config.employee.model.EmployeeCareer;
import config.employee.model.EmployeeDependent;
import config.employee.model.EmployeeEducation;
import config.employee.model.EmployeeInsurance;
import config.employee.model.EmployeeLanguage;
import config.employee.model.EmployeeMilitary;
import config.employee.model.EmployeeQualification;
import config.employee.model.EmployeeRewardPunishment;
import config.employee.model.EmployeeTraining;
import jdbc.JdbcUtil;
import person.model.EmployeeCard;

public class EmployeeCardDao {

	public EmployeeCard selectById(Connection conn, int employeeId) throws SQLException {
		EmployeeCard card = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// 1. 인적사항 및 퇴직정보를 메인 테이블(EMPLOYEE)에서 한 번에 조회
			String empSql = "SELECT * FROM EMPLOYEE WHERE EMPLOYEE_ID = ?";
			pstmt = conn.prepareStatement(empSql);
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				card = new EmployeeCard();

				// [1] 기본 인적사항
				card.setEmployeeNo(rs.getString("EMPLOYEE_NO"));
				card.setHireDate(rs.getDate("HIRE_DATE"));
				card.setPhotoPath(rs.getString("PHOTO_PATH"));
				card.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
				card.setEmployeeNameEn(rs.getString("EMPLOYEE_NAME_EN"));
				card.setResidentRegNo(rs.getString("RESIDENT_REG_NO"));
				card.setEmploymentType(rs.getString("EMPLOYMENT_TYPE"));
				card.setEmail(rs.getString("EMAIL"));
				card.setPhone(rs.getString("PHONE"));
				card.setMobile(rs.getString("MOBILE"));

				// [2] 퇴직사항 다이렉트 세팅
				card.setRetireType(rs.getString("RETIREMENT_TYPE_CODE"));
				card.setRetireDate(rs.getDate("RESIGN_DATE"));
				card.setRetireReason(rs.getString("RETIREMENT_REASON"));
				card.setRetirePhone(rs.getString("POST_RETIREMENT_PHONE"));

				JdbcUtil.close(rs);
				JdbcUtil.close(pstmt);

				// [3] 하위 1:N 테이블 리스트 조회 후 세팅
				card.setDependentList(selectDependents(conn, employeeId));
				card.setInsuranceList(selectInsurances(conn, employeeId));
				card.setEducationList(selectEducations(conn, employeeId));
				card.setMilitaryInfo(selectMilitary(conn, employeeId));
				card.setCareerList(selectCareers(conn, employeeId));
				card.setQualificationList(selectQualifications(conn, employeeId));
				card.setLanguageList(selectLanguages(conn, employeeId));
				card.setTrainingList(selectTrainings(conn, employeeId));
				card.setRewardPunishmentList(selectRewardPunishments(conn, employeeId));
				card.setAppointmentList(selectAppointments(conn, employeeId));
			}
			return card;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 1. 가족사항
	private List<EmployeeDependent> selectDependents(Connection conn, int employeeId) throws SQLException {
		List<EmployeeDependent> list = new ArrayList<>();
		String sql = "SELECT * FROM EMPLOYEE_DEPENDENT WHERE EMPLOYEE_ID = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					EmployeeDependent dep = new EmployeeDependent();
					dep.setRelationCode(rs.getString("RELATION_CODE"));
					dep.setDependentName(rs.getString("DEPENDENT_NAME"));
					dep.setBirthDate(rs.getDate("BIRTH_DATE"));
					dep.setCohabitationYn(rs.getString("COHABITATION_YN"));
					list.add(dep);
				}
			}
		}
		return list;
	}

	// 2. 4대보험
	private List<EmployeeInsurance> selectInsurances(Connection conn, int employeeId) throws SQLException {
		List<EmployeeInsurance> list = new ArrayList<>();
		String sql = "SELECT * FROM EMPLOYEE_INSURANCE WHERE EMPLOYEE_ID = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					EmployeeInsurance ins = new EmployeeInsurance();
					ins.setInsuranceTypeCode(rs.getString("INSURANCE_TYPE_CODE"));
					ins.setInsuranceNo(rs.getString("INSURANCE_NO"));
					ins.setAcquisitionDate(rs.getDate("ACQUISITION_DATE"));
					ins.setLossDate(rs.getDate("LOSS_DATE"));
					list.add(ins);
				}
			}
		}
		return list;
	}

	// 3. 학력
	private List<EmployeeEducation> selectEducations(Connection conn, int employeeId) throws SQLException {
		List<EmployeeEducation> list = new ArrayList<>();
		String sql = "SELECT * FROM EMPLOYEE_EDUCATION WHERE EMPLOYEE_ID = ? ORDER BY START_DATE ASC";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					EmployeeEducation edu = new EmployeeEducation();
					edu.setSchoolName(rs.getString("SCHOOL_NAME"));
					edu.setStartDate(rs.getDate("START_DATE"));
					edu.setEndDate(rs.getDate("END_DATE"));
					edu.setMajorName(rs.getString("MAJOR_NAME"));
					edu.setGraduationStatus(rs.getString("GRADUATION_STATUS"));
					list.add(edu);
				}
			}
		}
		return list;
	}

	// 4. 병역
	private EmployeeMilitary selectMilitary(Connection conn, int employeeId) throws SQLException {
		EmployeeMilitary mil = null;
		String sql = "SELECT * FROM EMPLOYEE_MILITARY WHERE EMPLOYEE_ID = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					EmployeeMilitary milObj = new EmployeeMilitary();
					milObj.setMilitaryStatusCode(rs.getString("MILITARY_STATUS_CODE"));
					milObj.setMilitaryExemptReason(rs.getString("MILITARY_EXEMPT_REASON"));
					milObj.setMilitaryBranchCode(rs.getString("MILITARY_BRANCH_CODE"));
					milObj.setMilitaryBranch(rs.getString("MILITARY_BRANCH"));
					milObj.setMilitarySpecialty(rs.getString("MILITARY_SPECIALTY"));
					milObj.setMilitaryGrade(rs.getString("MILITARY_GRADE"));
					milObj.setServiceStartDate(rs.getDate("MILITARY_SERVICE_START_DATE"));
					milObj.setServiceEndDate(rs.getDate("MILITARY_SERVICE_END_DATE"));
					mil = milObj;
				}
			}
		}
		return mil;
	}

	// 5. 경력
	private List<EmployeeCareer> selectCareers(Connection conn, int employeeId) throws SQLException {
		List<EmployeeCareer> list = new ArrayList<>();
		String sql = "SELECT * FROM EMPLOYEE_CAREER WHERE EMPLOYEE_ID = ? ORDER BY START_DATE ASC";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					EmployeeCareer car = new EmployeeCareer();
					car.setCompanyName(rs.getString("COMPANY_NAME"));
					car.setStartDate(rs.getDate("START_DATE"));
					car.setEndDate(rs.getDate("END_DATE"));
					car.setPosition(rs.getString("POSITION"));
					car.setCareerDescription(rs.getString("CAREER_DESCRIPTION"));
					list.add(car);
				}
			}
		}
		return list;
	}

	// 6. 자격/면허
	private List<EmployeeQualification> selectQualifications(Connection conn, int employeeId) throws SQLException {
		List<EmployeeQualification> list = new ArrayList<>();
		String sql = "SELECT * FROM EMPLOYEE_QUALIFICATION WHERE EMPLOYEE_ID = ? ORDER BY ACQUISITION_DATE";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					EmployeeQualification qual = new EmployeeQualification();
					qual.setQualificationName(rs.getString("QUALIFICATION_NAME"));
					qual.setAcquisitionDate(rs.getDate("ACQUISITION_DATE"));
					qual.setIssuingOrganization(rs.getString("ISSUING_ORGANIZATION"));
					qual.setMemo(rs.getString("MEMO"));
					list.add(qual);
				}
			}
		}
		return list;
	}

	// 7. 어학능력
	private List<EmployeeLanguage> selectLanguages(Connection conn, int employeeId) throws SQLException {
		List<EmployeeLanguage> list = new ArrayList<>();
		String sql = "SELECT * FROM EMPLOYEE_LANGUAGE WHERE EMPLOYEE_ID = ? ORDER BY ACQUISITION_DATE";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					EmployeeLanguage lang = new EmployeeLanguage();
					lang.setLanguageName(rs.getString("LANGUAGE_NAME"));
					lang.setTestName(rs.getString("TEST_NAME"));
					lang.setOfficialScore(rs.getString("OFFICIAL_SCORE"));
					lang.setAcquisitionDate(rs.getDate("ACQUISITION_DATE"));
					lang.setReadingLevelCode(rs.getString("READING_LEVEL_CODE"));
					lang.setWritingLevelCode(rs.getString("WRITING_LEVEL_CODE"));
					lang.setSpeakingLevelCode(rs.getString("SPEAKING_LEVEL_CODE"));
					list.add(lang);
				}
			}
		}
		return list;
	}

	// 8. 교육사항
	private List<EmployeeTraining> selectTrainings(Connection conn, int employeeId) throws SQLException {
		List<EmployeeTraining> list = new ArrayList<>();
		String sql = "SELECT * FROM EMPLOYEE_TRAINING WHERE EMPLOYEE_ID = ? ORDER BY TRAINING_START_DATE";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					EmployeeTraining tr = new EmployeeTraining();
					tr.setTrainingTypeCode(rs.getString("TRAINING_TYPE_CODE"));
					tr.setTrainingName(rs.getString("TRAINING_NAME"));
					tr.setStartDate(rs.getDate("TRAINING_START_DATE"));
					tr.setEndDate(rs.getDate("TRAINING_END_DATE"));
					tr.setTrainingInstitution(rs.getString("TRAINING_INSTITUTION"));
					tr.setTrainingCost(rs.getLong("TRAINING_COST"));
					tr.setRefundTrainingCost(rs.getLong("REFUND_TRAINING_COST"));
					list.add(tr);
				}
			}
		}
		return list;
	}

	// 9. 상벌사항
	private List<EmployeeRewardPunishment> selectRewardPunishments(Connection conn, int employeeId) throws SQLException {
		List<EmployeeRewardPunishment> list = new ArrayList<>();
		String sql = "SELECT * FROM EMPLOYEE_REWARD_PUNISHMENT WHERE EMPLOYEE_ID = ? ORDER BY REWARD_PUNISHMENT_DATE";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					EmployeeRewardPunishment rp = new EmployeeRewardPunishment();
					rp.setTypeCode(rs.getString("REWARD_PUNISHMENT_TYPE_CODE"));
					rp.setName(rs.getString("REWARD_PUNISHMENT_NAME"));
					rp.setAuthorityName(rs.getString("AUTHORITY_NAME"));
					rp.setDate(rs.getDate("REWARD_PUNISHMENT_DATE"));
					rp.setContent(rs.getString("REWARD_PUNISHMENT_CONTENT"));
					rp.setMemo(rs.getString("MEMO"));
					list.add(rp);
				}
			}
		}
		return list;
	}

	// 10. 인사발령
	private List<EmployeeAppointment> selectAppointments(Connection conn, int employeeId) throws SQLException {
		List<EmployeeAppointment> list = new ArrayList<>();
		String sql = "SELECT * FROM EMPLOYEE_APPOINTMENT WHERE EMPLOYEE_ID = ? ORDER BY APPOINTMENT_DATE";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					EmployeeAppointment ap = new EmployeeAppointment();
					ap.setTypeCode(rs.getString("APPOINTMENT_TYPE_CODE"));
					ap.setDate(rs.getDate("APPOINTMENT_DATE"));
					ap.setDepartment(rs.getString("DEPARTMENT"));
					ap.setPosition(rs.getString("POSITION"));
					ap.setDutyTitle(rs.getString("DUTY_TITLE"));
					ap.setMemo(rs.getString("MEMO"));
					list.add(ap);
				}
			}
		}
		return list;
	}
}