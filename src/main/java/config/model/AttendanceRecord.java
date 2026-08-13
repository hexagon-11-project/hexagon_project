package config.model;

import java.math.BigDecimal;
import java.sql.Date;

public class AttendanceRecord {

	private Integer attendanceId;
	private Integer employeeId;
	private Integer attendanceTypeId;
	private Date startDate;
	private Date endDate;
	private String startTime;
	private String endTime;
	private BigDecimal dayCount;
	private BigDecimal hourCount;
	private String description;
	private Date createdAt; // 입력일자 (등록된 시각)

	// 화면 표시용 (join 결과)
	private String attendanceName;
	private String unitCode;
	private Integer leaveTypeId;

	public Integer getAttendanceId() {
		return attendanceId;
	}

	public void setAttendanceId(Integer attendanceId) {
		this.attendanceId = attendanceId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getAttendanceTypeId() {
		return attendanceTypeId;
	}

	public void setAttendanceTypeId(Integer attendanceTypeId) {
		this.attendanceTypeId = attendanceTypeId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public BigDecimal getDayCount() {
		return dayCount;
	}

	public void setDayCount(BigDecimal dayCount) {
		this.dayCount = dayCount;
	}

	public BigDecimal getHourCount() {
		return hourCount;
	}

	public void setHourCount(BigDecimal hourCount) {
		this.hourCount = hourCount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getAttendanceName() {
		return attendanceName;
	}

	public void setAttendanceName(String attendanceName) {
		this.attendanceName = attendanceName;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public Integer getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(Integer leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}
}
