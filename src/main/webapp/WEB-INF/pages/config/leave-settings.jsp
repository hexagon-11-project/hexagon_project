<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.LeaveType"%>
<%@ page import="config.model.AttendanceType"%>
<%@ page import="config.model.EmployeeLeave"%>
<%
List<LeaveType> leaveTypeList = (List<LeaveType>) request.getAttribute("leaveTypeList");
List<AttendanceType> attendanceTypeList = (List<AttendanceType>) request.getAttribute("attendanceTypeList");
LeaveType selectedLeaveType = (LeaveType) request.getAttribute("selectedLeaveType");
AttendanceType selectedAttendanceType = (AttendanceType) request.getAttribute("selectedAttendanceType");
LeaveType manageLeaveType = (LeaveType) request.getAttribute("manageLeaveType");
List<config.model.EmployeeLeave> employeeLeaveList = (List<config.model.EmployeeLeave>) request.getAttribute("employeeLeaveList");
String selectedWorkTimeType = (String) request.getAttribute("selectedWorkTimeType");
boolean hasSelectedLeaveType = selectedLeaveType != null;
boolean hasSelectedAttendanceType = selectedAttendanceType != null;
boolean showEmployeeLeaveDialog = manageLeaveType != null;
int currentYear = java.time.LocalDate.now().getYear();
String defaultStartDate = currentYear + "-01-01";
String defaultEndDate = currentYear + "-12-31";
%>
<%
request.setAttribute("pageTitle", "휴가/근태 설정");
request.setAttribute("pageSection", "기본환경");
request.setAttribute("pageDescription", "휴가유형과 근태항목, 단위, 사용 여부 및 휴가 차감 연결을 설정합니다.");
request.setAttribute("activeKey", "leave-settings");
request.setAttribute("pageCss", "environment.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>

<section class="source-config-block">
	<div class="source-config-list">
		<div class="source-section-title">휴가항목 설정</div>
		<div class="table-wrap">
			<table class="data-table source-data-table">
				<thead>
					<tr>
						<th>휴가항목</th>
						<th>적용기간</th>
						<th>사원별 휴가일수</th>
						<th>사용여부</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (leaveTypeList != null) {
						for (LeaveType item : leaveTypeList) {
					%>
					<tr style="cursor: pointer;"
						onclick="location.href='<%=ctx%>/Config/leavetypeselect.do?leaveTypeId=<%=item.getLeaveTypeId()%>'">
						<td><%=item.getLeaveName()%></td>
						<td><%=item.getPeriodLabel()%></td>
						<td><a class="btn btn-sm"
							href="<%=ctx%>/Config/employeeleavemanage.do?leaveTypeId=<%=item.getLeaveTypeId()%>">관리</a></td>
						<td><%=item.getUseLabel()%></td>
					</tr>
					<%
						}
					}
					%>
				</tbody>
			</table>
		</div>
	</div>
	<div class="source-config-editor">
		<div class="source-editor-head">휴가항목</div>
		<form id="leaveTypeForm" method="post">
			<input type="hidden" name="leaveTypeId"
				value="<%=hasSelectedLeaveType ? selectedLeaveType.getLeaveTypeId() : ""%>">
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>휴가항목</th>
						<td class="span-3"><input type="text" class="input" name="leaveName"
						required
							placeholder="휴가항목을 입력하세요."
							value="<%=hasSelectedLeaveType && selectedLeaveType.getLeaveName() != null ? selectedLeaveType.getLeaveName() : ""%>">
						</td>
					</tr>
					<tr>
						<th>적용기간</th>
						<td class="span-3">
							<div class="range">
								<input class="input" type="date" name="startDate" required
									value="<%=hasSelectedLeaveType && selectedLeaveType.getEffectiveStartDate() != null ? selectedLeaveType.getEffectiveStartDate().toString() : defaultStartDate%>">
								<span>~</span>
								<input class="input" type="date" name="endDate" required
									value="<%=hasSelectedLeaveType && selectedLeaveType.getEffectiveEndDate() != null ? selectedLeaveType.getEffectiveEndDate().toString() : defaultEndDate%>">
							</div>
						</td>
					</tr>
					<tr>
						<th>사용여부</th>
						<td class="span-3">
							<div class="check-list">
								<label> <input type="radio" name="useYn" value="Y"
									<%=!hasSelectedLeaveType || !"N".equalsIgnoreCase(selectedLeaveType.getUseYn()) ? "checked" : ""%>>
									사용
								</label> <label> <input type="radio" name="useYn" value="N"
									<%=hasSelectedLeaveType && "N".equalsIgnoreCase(selectedLeaveType.getUseYn()) ? "checked" : ""%>>
									사용안함
								</label>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			<div class="source-editor-actions">
				<button type="submit" class="btn btn-primary"
					formaction="<%=ctx%>/Config/leavetypeinsert.do">추가</button>
				<button type="submit" class="btn btn-blue"
					formaction="<%=ctx%>/Config/leavetypeupdate.do"
					<%=hasSelectedLeaveType ? "" : "disabled"%>>수정</button>
				<button type="submit" class="btn"
					formaction="<%=ctx%>/Config/leavetypedelete.do"
					<%=hasSelectedLeaveType ? "" : "disabled"%>>삭제</button>
				<button type="button" class="btn"
					onclick="location.href='<%=ctx%>/Config/leavesettingslist.do'">내용 지우기</button>
			</div>
		</form>
	</div>
</section>

<section class="source-config-block">
	<div class="source-config-list">
		<div class="source-section-title">근태항목 설정</div>
		<div class="table-wrap">
			<table class="data-table source-data-table">
				<thead>
					<tr>
						<th>근태항목</th>
						<th>단위</th>
						<th>그룹관리</th>
						<th>휴가공제</th>
						<th>사용여부</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (attendanceTypeList != null) {
						for (AttendanceType item : attendanceTypeList) {
					%>
					<tr style="cursor: pointer;"
						onclick="location.href='<%=ctx%>/Config/attendancetypeselect.do?attendanceTypeId=<%=item.getAttendanceTypeId()%>'">
						<td><%=item.getAttendanceName()%></td>
						<td><%=item.getUnitLabel()%></td>
						<td><%=item.getAttendanceGroupCode() == null ? "-" : item.getAttendanceGroupCode()%></td>
						<td><%=item.getLeaveTypeDeductionLabel()%></td>
						<td><%=item.getUseLabel()%></td>
					</tr>
					<%
						}
					}
					%>
				</tbody>
			</table>
		</div>
	</div>
	<div class="source-config-editor">
		<div class="source-editor-head">근태항목</div>
		<form id="attendanceTypeForm" method="post">
			<input type="hidden" name="attendanceTypeId"
				value="<%=hasSelectedAttendanceType ? selectedAttendanceType.getAttendanceTypeId() : ""%>">
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>근태항목</th>
						<td class="span-3"><input type="text" class="input"
							name="attendanceName" placeholder="근태항목을 입력하세요."
							value="<%=hasSelectedAttendanceType && selectedAttendanceType.getAttendanceName() != null ? selectedAttendanceType.getAttendanceName() : ""%>">
						</td>
					</tr>
					<tr>
						<th>단위</th>
						<td class="span-3"><select class="select" name="unitCode">
								<option value=""
									<%=!hasSelectedAttendanceType || selectedAttendanceType.getUnitCode() == null ? "selected" : ""%>>선택해주세요</option>
								<option value="DAY"
									<%=hasSelectedAttendanceType && "DAY".equalsIgnoreCase(selectedAttendanceType.getUnitCode()) ? "selected" : ""%>>일</option>
								<option value="HOUR"
									<%=hasSelectedAttendanceType && "HOUR".equalsIgnoreCase(selectedAttendanceType.getUnitCode()) ? "selected" : ""%>>시간</option>
						</select></td>
					</tr>
					<tr>
						<th>근태그룹</th>
						<td class="span-3">
							<div class="inline-control">
								<select class="select" name="attendanceGroupCode">
									<option value=""
										<%=!hasSelectedAttendanceType || selectedAttendanceType.getAttendanceGroupCode() == null ? "selected" : ""%>>선택해주세요</option>
									<option value="휴가"
										<%=hasSelectedAttendanceType && "휴가".equals(selectedAttendanceType.getAttendanceGroupCode()) ? "selected" : ""%>>휴가</option>
									<option value="지각/조퇴"
										<%=hasSelectedAttendanceType && "지각/조퇴".equals(selectedAttendanceType.getAttendanceGroupCode()) ? "selected" : ""%>>지각/조퇴</option>
									<option value="연장근무"
										<%=hasSelectedAttendanceType && "연장근무".equals(selectedAttendanceType.getAttendanceGroupCode()) ? "selected" : ""%>>연장근무</option>
									<option value="기타"
										<%=hasSelectedAttendanceType && "기타".equals(selectedAttendanceType.getAttendanceGroupCode()) ? "selected" : ""%>>기타</option>
								</select>
							</div>
						</td>
					</tr>
					<tr>
						<th>휴가공제</th>
						<td class="span-3"><select class="select" name="leaveTypeId">
								<option value=""
									<%=!hasSelectedAttendanceType || selectedAttendanceType.getLeaveTypeId() == null ? "selected" : ""%>>미연결</option>
								<%
								if (leaveTypeList != null) {
									for (LeaveType lt : leaveTypeList) {
								%>
								<option value="<%=lt.getLeaveTypeId()%>"
									<%=hasSelectedAttendanceType && selectedAttendanceType.getLeaveTypeId() != null
											&& selectedAttendanceType.getLeaveTypeId().equals(lt.getLeaveTypeId()) ? "selected" : ""%>><%=lt.getLeaveName()%></option>
								<%
									}
								}
								%>
						</select></td>
					</tr>
					<tr>
						<th>사용여부</th>
						<td class="span-3">
							<div class="check-list">
								<label> <input type="radio" name="useYn" value="Y"
									<%=!hasSelectedAttendanceType || !"N".equalsIgnoreCase(selectedAttendanceType.getUseYn()) ? "checked" : ""%>>
									사용
								</label> <label> <input type="radio" name="useYn" value="N"
									<%=hasSelectedAttendanceType && "N".equalsIgnoreCase(selectedAttendanceType.getUseYn()) ? "checked" : ""%>>
									사용안함
								</label>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			<div class="source-editor-actions">
				<button type="submit" class="btn btn-primary"
					formaction="<%=ctx%>/Config/attendancetypeinsert.do">추가</button>
				<button type="submit" class="btn btn-blue"
					formaction="<%=ctx%>/Config/attendancetypeupdate.do"
					<%=hasSelectedAttendanceType ? "" : "disabled"%>>수정</button>
				<button type="submit" class="btn"
					formaction="<%=ctx%>/Config/attendancetypedelete.do"
					<%=hasSelectedAttendanceType ? "" : "disabled"%>>삭제</button>
				<button type="button" class="btn"
					onclick="location.href='<%=ctx%>/Config/leavesettingslist.do'">내용 지우기</button>
			</div>
		</form>
	</div>
</section>

<div class="dialog-backdrop <%=showEmployeeLeaveDialog ? "open" : ""%>">
	<div class="dialog" style="width: min(920px, 100%);">
		<div class="dialog-header">
			<strong>휴가일수 설정</strong>
			<button type="button" class="btn btn-icon" data-dialog-close>×</button>
		</div>
		<div class="dialog-body">
			<%
			if (showEmployeeLeaveDialog) {
			%>
			<div class="table-toolbar compact" style="justify-content: space-between;">
				<div style="display: flex; gap: 6px; align-items: center;">
					<input type="text" class="input" id="employeeLeaveSearchInput"
						placeholder="사원명/사원번호 검색">
					<button type="button" class="btn btn-icon" id="employeeLeaveSearchBtn">🔍</button>
					<button type="button" class="btn btn-sm" id="employeeLeaveResetBtn">전체보기</button>
				</div>
				<div style="display: flex; gap: 6px; align-items: center;">
					<select class="select" id="employeeLeaveStatusFilter">
						<option value="">상태별</option>
						<option value="재직">재직</option>
						<option value="퇴직">퇴직</option>
					</select>
				</div>
			</div>
			<form id="employeeLeaveForm" method="post">
				<input type="hidden" name="leaveTypeId" value="<%=manageLeaveType.getLeaveTypeId()%>">
				<div class="table-wrap" style="max-height: 420px; overflow-y: auto;">
					<table class="data-table source-data-table" id="employeeLeaveTable">
						<thead>
							<tr>
								<th><input type="checkbox" data-check-all></th>
								<th>구분</th>
								<th>사원번호</th>
								<th>성명</th>
								<th>부서</th>
								<th>직위</th>
								<th>입사일</th>
								<th>휴가일수</th>
							</tr>
						</thead>
						<tbody>
							<%
							if (employeeLeaveList != null) {
								for (EmployeeLeave row : employeeLeaveList) {
							%>
							<tr data-status="<%=row.getEmploymentStatus() == null ? "" : row.getEmploymentStatus()%>">
								<td><input type="checkbox" name="checkedEmployeeId"
									value="<%=row.getEmployeeId()%>"></td>
								<td><%=row.getEmploymentType() == null ? "-" : row.getEmploymentType()%></td>
								<td><%=row.getEmployeeNo()%></td>
								<td><%=row.getEmployeeName()%></td>
								<td><%=row.getDepartment() == null ? "-" : row.getDepartment()%></td>
								<td><%=row.getPosition() == null ? "-" : row.getPosition()%></td>
								<td><%=row.getHireDate() == null ? "-" : row.getHireDate().toString()%></td>
								<td><input class="input" type="number" step="0.5" min="0"
									style="width: 80px;"
									name="grantedDays_<%=row.getEmployeeId()%>"
									value="<%=row.getGrantedDaysValue()%>"> 일</td>
							</tr>
							<%
								}
							}
							%>
						</tbody>
					</table>
				</div>
			</form>
			<%
			}
			%>
		</div>
		<div class="dialog-footer">
			<button type="submit" form="employeeLeaveForm" class="btn"
				formaction="<%=ctx%>/Config/employeeleavedelete.do">휴가일수 삭제</button>
			<button type="submit" form="employeeLeaveForm" class="btn"
				formaction="<%=ctx%>/Config/employeeleaveautocalc.do">휴가일수 자동계산</button>
			<button type="submit" form="employeeLeaveForm" class="btn btn-primary"
				formaction="<%=ctx%>/Config/employeeleavesave.do">휴가일수 저장</button>
			<button type="button" class="btn" data-dialog-close>닫기</button>
		</div>
	</div>
</div>
<script>
(function() {
	var searchInput = document.getElementById('employeeLeaveSearchInput');
	var searchBtn = document.getElementById('employeeLeaveSearchBtn');
	var resetBtn = document.getElementById('employeeLeaveResetBtn');
	var statusFilter = document.getElementById('employeeLeaveStatusFilter');
	var table = document.getElementById('employeeLeaveTable');
	if (!table) return;

	function applyFilter() {
		var keyword = (searchInput.value || '').trim().toLowerCase();
		var status = statusFilter.value;
		table.querySelectorAll('tbody tr').forEach(function(row) {
			var matchesKeyword = keyword === '' || row.textContent.toLowerCase().includes(keyword);
			var matchesStatus = status === '' || row.dataset.status === status;
			row.hidden = !(matchesKeyword && matchesStatus);
		});
	}

	searchBtn.addEventListener('click', applyFilter);
	searchInput.addEventListener('keydown', function(e) {
		if (e.key === 'Enter') {
			e.preventDefault();
			applyFilter();
		}
	});
	statusFilter.addEventListener('change', applyFilter);
	resetBtn.addEventListener('click', function() {
		searchInput.value = '';
		statusFilter.value = '';
		applyFilter();
	});
})();
</script>
<%
if (Boolean.TRUE.equals(request.getAttribute("employeeLeaveJustSaved"))) {
%>
<script>alert('저장되었습니다.');</script>
<%
}
%>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
