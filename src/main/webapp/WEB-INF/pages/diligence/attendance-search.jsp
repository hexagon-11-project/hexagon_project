<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.AttendanceRecord"%>
<%@ page import="config.model.AttendanceType"%>
<%@ page import="diligence.searchmonth.dao.AttendanceSearchDao"%>
<%
// ページ表示に必要なデータをrequestから取得 / 화면 표시에 필요한 데이터 취득
List<AttendanceRecord> recordList = (List<AttendanceRecord>) request.getAttribute("recordList");
List<AttendanceType> attendanceTypeList = (List<AttendanceType>) request.getAttribute("attendanceTypeList");
String searchMonth = (String) request.getAttribute("searchMonth");
Integer selectedAttendanceTypeId = (Integer) request.getAttribute("selectedAttendanceTypeId");
String sortKey = (String) request.getAttribute("sortKey");
%>
<%
request.setAttribute("pageTitle", "勤怠照会");
request.setAttribute("pageSection", "勤怠管理");
request.setAttribute("pageDescription", "月別条件で社員別の勤怠記録を照会します。");
request.setAttribute("activeKey", "attendance-search");
request.setAttribute("pageCss", "attendance.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>

<!-- 検索条件フォーム / 검색 조건 폼 -->
<form id="searchForm" method="post" action="<%=ctx%>/Diligence/diligenceSearchMonth.do">
	<section class="filter-bar">
		<div class="field">
			<label>照会区分</label>
			<div class="input" style="display: flex; align-items: center;">月別照会</div>
			<input type="hidden" name="searchType" value="월별 조회">
		</div>
		<div class="field">
			<label>照会月</label>
			<input type="month" class="input" name="searchMonth" value="<%=searchMonth%>">
		</div>
		<div class="field">
			<label>勤怠項目</label>
			<select class="select" name="attendanceTypeId">
				<option value="" <%=selectedAttendanceTypeId == null ? "selected" : ""%>>全件</option>
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
		<div class="field">
			<label>並び替え</label>
			<select class="select" name="sortKey">
				<option value="氏名順" <%="氏名順".equals(sortKey) ? "selected" : ""%>>氏名順</option>
				<option value="部署順" <%="部署順".equals(sortKey) ? "selected" : ""%>>部署順</option>
				<option value="日付順" <%="日付順".equals(sortKey) ? "selected" : ""%>>日付順</option>
			</select>
		</div>
		<div class="actions">
			<!-- 照会ボタン / 조회 버튼 -->
			<button type="submit" class="btn btn-primary">照会</button>
		</div>
	</section>
</form>

<!-- 勤怠照会結果テーブル / 근태조회 결과 테이블 -->
<section class="card">
	<div class="card-header">
		<h2 class="section-title">勤怠照会 結果</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table">
				<thead>
					<tr>
						<th>氏名</th>
						<th>部署</th>
						<th>勤怠項目</th>
						<th>日付</th>
						<th>日数／時間</th>
						<th>手当</th>
						<th>摘要</th>
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
						<td colspan="7">照会された勤怠記録がありません。</td>
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
