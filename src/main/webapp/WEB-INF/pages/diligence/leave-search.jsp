<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.AttendanceRecord"%>
<%@ page import="config.model.EmployeeLeaveStatus"%>
<%@ page import="config.model.LeaveType"%>
<%@ page import="diligence.holidayssearchresult.dao.LeaveSearchDao"%>
<%
List<LeaveType> leaveTypeList = (List<LeaveType>) request.getAttribute("leaveTypeList");
List<EmployeeLeaveStatus> statusList = (List<EmployeeLeaveStatus>) request.getAttribute("statusList");
List<AttendanceRecord> usageList = (List<AttendanceRecord>) request.getAttribute("usageList");
Integer selectedLeaveTypeId = (Integer) request.getAttribute("selectedLeaveTypeId");
Integer year = (Integer) request.getAttribute("year");
String sortKey = (String) request.getAttribute("sortKey");
Integer selectedEmployeeId = (Integer) request.getAttribute("selectedEmployeeId");
EmployeeLeaveStatus selectedStatus = (EmployeeLeaveStatus) request.getAttribute("selectedStatus");
%>
<%
request.setAttribute("pageTitle", "휴가조회");
request.setAttribute("pageSection", "근태관리");
request.setAttribute("pageDescription", "휴가항목별 부여·사용·잔여일수와 선택 사원의 사용내역을 조회합니다.");
request.setAttribute("activeKey", "leave-search");
request.setAttribute("pageCss", "attendance.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>

<form id="searchForm" method="post"
	action="<%=ctx%>/Diligence/holidaysSearchResult.do">
	<input type="hidden" id="selectedEmployeeIdInput"
		name="selectedEmployeeId" value="">
	<section class="filter-bar">
		<div class="field ">
			<label>휴가항목</label> <select class="select" name="leaveTypeId">
				<%
				if (leaveTypeList != null) {
					for (LeaveType type : leaveTypeList) {
				%>
				<option value="<%=type.getLeaveTypeId()%>"
					<%=type.getLeaveTypeId().equals(selectedLeaveTypeId) ? "selected" : ""%>><%=type.getLeaveName()%></option>
				<%
				}
				}
				%>
			</select>
		</div>
		<div class="field ">
			<label>기준연도</label> <select class="select" name="year">
				<%
				int currentYear = java.time.Year.now().getValue();
				for (int y = currentYear; y >= currentYear - 4; y--) {
				%>
				<option value="<%=y%>"
					<%=year != null && year == y ? "selected" : ""%>><%=y%></option>
				<%
				}
				%>
			</select>
		</div>
		<div class="field ">
			<label>정렬</label> <select class="select" name="sortKey">
				<option value="<%=LeaveSearchDao.SORT_NAME%>"
					<%=LeaveSearchDao.SORT_NAME.equals(sortKey) ? "selected" : ""%>><%=LeaveSearchDao.SORT_NAME%></option>
				<option value="<%=LeaveSearchDao.SORT_DEPARTMENT%>"
					<%=LeaveSearchDao.SORT_DEPARTMENT.equals(sortKey) ? "selected" : ""%>><%=LeaveSearchDao.SORT_DEPARTMENT%></option>
				<option value="<%=LeaveSearchDao.SORT_REMAINING%>"
					<%=LeaveSearchDao.SORT_REMAINING.equals(sortKey) ? "selected" : ""%>><%=LeaveSearchDao.SORT_REMAINING%></option>
			</select>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">조회</button>
		</div>
	</section>
</form>

<section class="card ">
	<div class="card-header">
		<h2 class="section-title">휴가 현황</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table list-table" id="leaveTable">
				<thead>
					<tr>
						<th>성명</th>
						<th>부서</th>
						<th>휴가항목</th>
						<th>부여일수</th>
						<th>사용일수</th>
						<th>잔여일수</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (statusList != null && !statusList.isEmpty()) {
						for (EmployeeLeaveStatus status : statusList) {
							boolean isSelectedRow = selectedEmployeeId != null && selectedEmployeeId.equals(status.getEmployeeId());
					%>
					<tr class="clickable-row <%=isSelectedRow ? "selected" : ""%>"
						onclick="document.getElementById('selectedEmployeeIdInput').value='<%=status.getEmployeeId()%>'; document.getElementById('searchForm').submit();">
						<td><%=status.getEmployeeName()%></td>
						<td><%=status.getDepartment() == null ? "-" : status.getDepartment()%></td>
						<td><%=status.getLeaveName()%></td>
						<td><%=status.getTotalDaysValue()%></td>
						<td><%=status.getUsedDaysValue()%></td>
						<td><%=status.getRemainingDaysValue()%></td>
					</tr>
					<%
					}
					} else {
					%>
					<tr>
						<td colspan="6">조회된 휴가 현황이 없습니다.</td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
		</div>
	</div>
</section>

<section class="card ">
	<div class="card-header">
		<h2 class="section-title">
			선택 사원 휴가 사용내역<%=selectedStatus != null ? " - " + selectedStatus.getEmployeeName() : ""%></h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table ">
				<thead>
					<tr>
						<th>사용일</th>
						<th>휴가항목</th>
						<th>사용일수</th>
						<th>메모</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (selectedStatus == null) {
					%>
					<tr>
						<td colspan="4">위 목록에서 사원을 선택하면 사용내역이 표시됩니다.</td>
					</tr>
					<%
					} else if (usageList != null && !usageList.isEmpty()) {
					for (AttendanceRecord record : usageList) {
					%>
					<tr>
						<td><%=record.getStartDate()%></td>
						<td><%=record.getAttendanceName()%></td>
						<td><%=record.getDayCount() == null ? "-" : record.getDayCount().stripTrailingZeros().toPlainString()%></td>
						<td><%=record.getDescription() == null ? "-" : record.getDescription()%></td>
					</tr>
					<%
					}
					} else {
					%>
					<tr>
						<td colspan="4">사용내역이 없습니다.</td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
		</div>
	</div>
</section>

<style>
.clickable-row {
	cursor: pointer;
}

.clickable-row:hover {
	background: var(- -row-hover, #f5f7fa);
}

.clickable-row.selected {
	background: var(- -row-selected, #eef3ff);
}
</style>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
