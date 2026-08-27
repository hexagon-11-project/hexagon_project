<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.AttendanceRecord"%>
<%@ page import="config.model.EmployeeLeaveStatus"%>
<%@ page import="config.model.LeaveType"%>
<%@ page import="diligence.holidayssearchresult.dao.LeaveSearchDao"%>
<%
// ページ表示に必要なデータをrequestから取得 / 화면 표시에 필요한 데이터 취득
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
request.setAttribute("pageTitle", "休暇照会");
request.setAttribute("pageSection", "勤怠管理");
request.setAttribute("pageDescription", "休暇項目別の付与・使用・残余日数と選択社員の使用内訳を照会します。");
request.setAttribute("activeKey", "leave-search");
request.setAttribute("pageCss", "attendance.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>

<!-- 検索条件フォーム / 검색 조건 폼 -->
<form id="searchForm" method="post"
	action="<%=ctx%>/Diligence/holidaysSearchResult.do">
	<input type="hidden" id="selectedEmployeeIdInput"
		name="selectedEmployeeId" value="">
	<section class="filter-bar">
		<div class="field">
			<label>休暇項目</label>
			<select class="select" name="leaveTypeId">
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
		<div class="field">
			<label>基準年度</label>
			<select class="select" name="year">
				<%
				int currentYear = java.time.Year.now().getValue();
				for (int y = currentYear; y >= currentYear - 4; y--) {
				%>
				<option value="<%=y%>"
					<%=year != null && year == y ? "selected" : ""%>><%=y%>年</option>
				<%
				}
				%>
			</select>
		</div>
		<div class="field">
			<!-- 並び替えは直接ハードコード（DAO定数が日本語未対応のため） / 정렬 드롭다운 하드코딩 -->
			<label>並び替え</label>
			<select class="select" name="sortKey">
				<option value="氏名順" <%="氏名順".equals(sortKey) ? "selected" : ""%>>氏名順</option>
				<option value="部署順" <%="部署順".equals(sortKey) ? "selected" : ""%>>部署順</option>
				<option value="残余順" <%="残余順".equals(sortKey) ? "selected" : ""%>>残余順</option>
			</select>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">照会</button>
		</div>
	</section>
</form>

<!-- 休暇現況テーブル / 휴가 현황 테이블 -->
<section class="card">
	<div class="card-header">
		<h2 class="section-title">休暇現況</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table list-table" id="leaveTable">
				<thead>
					<tr>
						<th>氏名</th>
						<th>部署</th>
						<th>休暇項目</th>
						<th>付与日数</th>
						<th>使用日数</th>
						<th>残余日数</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (statusList != null && !statusList.isEmpty()) {
						for (EmployeeLeaveStatus status : statusList) {
							boolean isSelectedRow = selectedEmployeeId != null && selectedEmployeeId.equals(status.getEmployeeId());
					%>
					<!-- 行クリックで該当社員の使用内訳を表示 / 행 클릭 시 해당 사원 사용내역 표시 -->
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
						<td colspan="6">照会された休暇現況がありません。</td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
		</div>
	</div>
</section>

<!-- 選択社員の休暇使用内訳テーブル / 선택 사원 휴가 사용내역 테이블 -->
<section class="card">
	<div class="card-header">
		<h2 class="section-title">
			選択社員 休暇使用内訳<%=selectedStatus != null ? " - " + selectedStatus.getEmployeeName() : ""%></h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table">
				<thead>
					<tr>
						<th>使用日</th>
						<th>休暇項目</th>
						<th>使用日数</th>
						<th>摘要</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (selectedStatus == null) {
					%>
					<tr>
						<td colspan="4">上の一覧から社員を選択すると使用内訳が表示されます。</td>
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
						<td colspan="4">使用内訳がありません。</td>
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
.clickable-row { cursor: pointer; }
.clickable-row:hover { background: var(--row-hover, #f5f7fa); }
.clickable-row.selected { background: var(--row-selected, #eef3ff); }
</style>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
