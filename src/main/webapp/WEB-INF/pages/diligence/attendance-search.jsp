<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.AttendanceRecord"%>
<%@ page import="config.model.AttendanceType"%>
<%@ page import="diligence.searchmonth.dao.AttendanceSearchDao"%>
<%
List<AttendanceRecord> recordList = (List<AttendanceRecord>) request.getAttribute("recordList");
List<AttendanceType> attendanceTypeList = (List<AttendanceType>) request.getAttribute("attendanceTypeList");
String searchMonth = (String) request.getAttribute("searchMonth");
Integer selectedAttendanceTypeId = (Integer) request.getAttribute("selectedAttendanceTypeId");
String sortKey = (String) request.getAttribute("sortKey");
%>
<%
request.setAttribute("pageTitle", "근태조회");
request.setAttribute("pageSection", "근태관리");
request.setAttribute("pageDescription", "월별 조건으로 사원별 근태기록을 조회합니다.");
request.setAttribute("activeKey", "attendance-search");
request.setAttribute("pageCss", "attendance.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>

<form id="searchForm" method="post" action="<%=ctx%>/Diligence/diligenceSearchMonth.do">
	<section class="filter-bar">
		<div class="field ">
			<label>조회구분</label>
			<div class="input" style="display: flex; align-items: center;">월별 조회</div>
			<input type="hidden" name="searchType" value="월별 조회">
		</div>
		<div class="field ">
			<label>조회월</label> <input type="month" class="input"
				name="searchMonth" value="<%=searchMonth%>">
		</div>
		<div class="field ">
			<label>근태항목</label>
			<select class="select" name="attendanceTypeId">
				<option value="" <%=selectedAttendanceTypeId == null ? "selected" : ""%>>전체</option>
				<%
				if (attendanceTypeList != null) {
					for (AttendanceType type : attendanceTypeList) {
				%>
				<option value="<%=type.getAttendanceTypeId()%>"
					<%=type.getAttendanceTypeId().equals(selectedAttendanceTypeId) ? "selected" : ""%>><%=type.getAttendanceName()%></option>
				<%
					}
				}
				%>
			</select>
		</div>
		<div class="field ">
			<label>정렬</label>
			<select class="select" name="sortKey">
				<option value="<%=AttendanceSearchDao.SORT_NAME%>"
					<%=AttendanceSearchDao.SORT_NAME.equals(sortKey) ? "selected" : ""%>><%=AttendanceSearchDao.SORT_NAME%></option>
				<option value="<%=AttendanceSearchDao.SORT_DEPARTMENT%>"
					<%=AttendanceSearchDao.SORT_DEPARTMENT.equals(sortKey) ? "selected" : ""%>><%=AttendanceSearchDao.SORT_DEPARTMENT%></option>
				<option value="<%=AttendanceSearchDao.SORT_DATE%>"
					<%=AttendanceSearchDao.SORT_DATE.equals(sortKey) ? "selected" : ""%>><%=AttendanceSearchDao.SORT_DATE%></option>
			</select>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">조회</button>
		</div>
	</section>
</form>

<section class="card ">
	<div class="card-header">
		<h2 class="section-title">근태 조회 결과</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table ">
				<thead>
					<tr>
						<th>성명</th>
						<th>부서</th>
						<th>근태항목</th>
						<th>일자</th>
						<th>일수/시간</th>
						<th>수당</th>
						<th>메모</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (recordList != null && !recordList.isEmpty()) {
						for (AttendanceRecord record : recordList) {
					%>
					<tr>
						<td><%=record.getEmployeeName()%></td>
						<td><%=record.getDepartment() == null ? "-" : record.getDepartment()%></td>
						<td><%=record.getAttendanceName()%></td>
						<td><%=record.getStartDate()%><%=record.getEndDate() != null && !record.getEndDate().equals(record.getStartDate()) ? " ~ " + record.getEndDate() : ""%></td>
						<td><%=record.getCountDisplayValue()%></td>
						<td><%=record.getAllowanceAmountValue()%></td>
						<td><%=record.getDescription() == null ? "-" : record.getDescription()%></td>
					</tr>
					<%
						}
					} else {
					%>
					<tr>
						<td colspan="7">조회된 근태기록이 없습니다.</td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
		</div>
	</div>
</section>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
