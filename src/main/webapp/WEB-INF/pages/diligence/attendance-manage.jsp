<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.LinkedHashSet"%>
<%@ page import="config.model.EmployeeLeave"%>
<%@ page import="config.model.AttendanceType"%>
<%@ page import="config.model.AttendanceRecord"%>
<%@ page import="config.model.EmployeeLeaveStatus"%>
<%
List<EmployeeLeave> employeeList = (List<EmployeeLeave>) request.getAttribute("employeeList");
List<AttendanceType> attendanceTypeList = (List<AttendanceType>) request.getAttribute("attendanceTypeList");
EmployeeLeave selectedEmployee = (EmployeeLeave) request.getAttribute("selectedEmployee");
List<AttendanceRecord> recordList = (List<AttendanceRecord>) request.getAttribute("recordList");
AttendanceRecord editRecord = (AttendanceRecord) request.getAttribute("editRecord");
List<EmployeeLeaveStatus> leaveStatusList = (List<EmployeeLeaveStatus>) request.getAttribute("leaveStatusList");
String leaveStatusMessage = (String) request.getAttribute("leaveStatusMessage");
String saveMessage = (String) request.getAttribute("saveMessage");
boolean hasSelectedEmployee = selectedEmployee != null;
Boolean showRecordDialogAttr = (Boolean) request.getAttribute("showRecordDialog");
boolean showRecordDialog = hasSelectedEmployee && (showRecordDialogAttr == null || showRecordDialogAttr);
Boolean showLeaveStatusDialogAttr = (Boolean) request.getAttribute("showLeaveStatusDialog");
boolean showLeaveStatusDialog = hasSelectedEmployee && showLeaveStatusDialogAttr != null && showLeaveStatusDialogAttr;
boolean isEditing = editRecord != null;

// 팝업 년도 드롭다운에 넣을 목록 - 이 사원 기록에 실제로 있는 연도 + 올해
int currentYear = java.time.LocalDate.now().getYear();
List<Integer> yearOptions = new java.util.ArrayList<>();
for (int y = currentYear - 12; y <= currentYear + 1; y++) {
	yearOptions.add(y);
}
%>
<%
request.setAttribute("pageTitle", "근태기록/관리");
request.setAttribute("pageSection", "근태관리");
request.setAttribute("pageDescription", "사원을 선택해 휴가, 지각·조퇴, 연장근무 기록을 입력하고 수정·삭제합니다.");
request.setAttribute("activeKey", "attendance-manage");
request.setAttribute("pageCss", "attendance.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<div class="source-two-pane">
	<section class="source-list-panel">
		<div class="source-list-search">
			<input class="input" type="text" id="employeeSearchInput" placeholder="검색어 입력">
			<button class="btn btn-primary" type="button" id="employeeResetBtn">전체보기</button>
		</div>
		<div class="table-wrap">
			<table class="data-table source-data-table compact-list" id="employeeListTable">
				<thead>
					<tr>
						<th>선택</th>
						<th>구분</th>
						<th>사원번호</th>
						<th>성명</th>
						<th>부서</th>
						<th>직위</th>
						<th>관리</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (employeeList != null) {
						for (EmployeeLeave emp : employeeList) {
					%>
					<tr>
						<td><input type="checkbox" class="employeeSelectCheckbox" value="<%=emp.getEmployeeId()%>"></td>
						<td><%=emp.getEmploymentType() == null ? "-" : emp.getEmploymentType()%></td>
						<td><%=emp.getEmployeeNo()%></td>
						<td><%=emp.getEmployeeName()%></td>
						<td><%=emp.getDepartment() == null ? "-" : emp.getDepartment()%></td>
						<td><%=emp.getPosition() == null ? "-" : emp.getPosition()%></td>
						<td><a class="btn btn-sm"
							href="<%=ctx%>/Diligence/diligenceMntSelect.do?employeeId=<%=emp.getEmployeeId()%>">관리</a></td>
					</tr>
					<%
						}
					}
					%>
				</tbody>
			</table>
		</div>
	</section>
	<section class="source-entry-panel">
		<div class="source-editor-head">근태기록 입력<%=isEditing ? " (수정 중)" : ""%></div>
		<form id="attendanceRecordForm" method="post">
			<input type="hidden" id="entryEmployeeId" name="employeeId" value="<%=hasSelectedEmployee ? selectedEmployee.getEmployeeId() : ""%>">
			<input type="hidden" id="leaveStatusEmployeeIds" name="employeeIds" value="">
			<%
			if (isEditing) {
			%>
			<input type="hidden" name="attendanceId" value="<%=editRecord.getAttendanceId()%>">
			<%
			}
			%>
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>입력일자</th>
						<td class="span-3"><input type="date" class="input" name="inputDate"
							value="<%=isEditing && editRecord.getCreatedAt() != null ? editRecord.getCreatedAt().toString()
									: new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())%>"></td>
					</tr>
					<tr>
						<th>근태항목</th>
						<td class="span-3"><select class="select" id="attendanceTypeSelect" name="attendanceTypeId">
								<option value="" data-unit="DAY" <%=!isEditing ? "selected" : ""%>>선택하세요</option>
								<%
								if (attendanceTypeList != null) {
									for (AttendanceType type : attendanceTypeList) {
								%>
								<option value="<%=type.getAttendanceTypeId()%>" data-unit="<%=type.getUnitCode() == null ? "DAY" : type.getUnitCode()%>"
									<%=isEditing && type.getAttendanceTypeId().equals(editRecord.getAttendanceTypeId()) ? "selected" : ""%>><%=type.getAttendanceName()%></option>
								<%
									}
								}
								%>
						</select></td>
					</tr>
					<tr>
						<th>기간</th>
						<td class="span-3"><div class="range">
								<input class="input" type="date" name="startDate"
									value="<%=isEditing && editRecord.getStartDate() != null ? editRecord.getStartDate().toString() : ""%>"><span>~</span><input
									class="input" type="date" name="endDate"
									value="<%=isEditing && editRecord.getEndDate() != null ? editRecord.getEndDate().toString() : ""%>">
							</div></td>
					</tr>
					<tr>
						<th id="countLabelTh">근태일수</th>
						<td class="span-3"><div class="inline-control">
								<input class="input number" type="text" name="count" id="countInput"
									placeholder="근태일수"
									value="<%=isEditing ? (editRecord.getDayCount() != null ? editRecord.getDayCount().stripTrailingZeros().toPlainString()
											: (editRecord.getHourCount() != null ? editRecord.getHourCount().stripTrailingZeros().toPlainString() : "")) : ""%>">
								<span id="countUnitSuffix">일</span>
								<button type="submit" class="btn btn-sm" form="attendanceRecordForm"
									formaction="<%=ctx%>/Diligence/diligenceMntLeaveStatus.do">휴가일수 현황</button>
							</div></td>
					</tr>
					<tr>
						<th>금액(수당)</th>
						<td class="span-3"><div class="money-control">
								<input class="input number" type="text" name="amount"
									value="<%=isEditing && editRecord.getAllowanceAmount() != null ? editRecord.getAllowanceAmount().stripTrailingZeros().toPlainString() : ""%>"><span>원</span>
							</div></td>
					</tr>
					<tr>
						<th>적요</th>
						<td class="span-3"><input type="text" class="input" name="description"
							value="<%=isEditing && editRecord.getDescription() != null ? editRecord.getDescription() : ""%>"></td>
					</tr>
				</tbody>
			</table>
			<div class="source-editor-actions">
				<button type="submit" class="btn btn-primary" form="attendanceRecordForm"
					formaction="<%=ctx%>/Diligence/<%=isEditing ? "diligenceMntUpdate" : "diligenceMntInsert"%>.do">저장</button>
				<button type="reset" class="btn" form="attendanceRecordForm">내용 지우기</button>
			</div>
		</form>
	</section>
</div>

<div class="dialog-backdrop <%=showRecordDialog ? "open" : ""%>">
	<div class="dialog" style="width: min(980px, 100%); min-height: 560px; display: flex; flex-direction: column;">
		<div class="dialog-header">
			<strong>사원별 근태기록</strong>
			<button type="button" class="btn btn-icon" data-dialog-close>×</button>
		</div>
		<div class="dialog-body" style="flex: 1;">
			<%
			if (hasSelectedEmployee) {
			%>
			<div class="table-toolbar compact" style="justify-content: space-between;">
				<div>
					· 성명 : <%=selectedEmployee.getEmployeeName()%> (<%=selectedEmployee.getEmployeeNo()%>)
					&nbsp;&nbsp;· 부서 : <%=selectedEmployee.getDepartment() == null ? "-" : selectedEmployee.getDepartment()%>
					&nbsp;&nbsp;· 직위 : <%=selectedEmployee.getPosition() == null ? "-" : selectedEmployee.getPosition()%>
				</div>
				<div style="display: flex; gap: 6px;">
					<select class="select" id="recordYearFilter" style="width: 100px;">
						<option value="">선택</option>
						<%
						for (Integer y : yearOptions) {
						%>
						<option value="<%=y%>" <%=y == currentYear ? "selected" : ""%>><%=y%>년</option>
						<%
						}
						%>
					</select>
					<select class="select" id="recordMonthFilter" style="width: 100px;">
						<option value="">전체</option>
						<%
						for (int m = 1; m <= 12; m++) {
						%>
						<option value="<%=String.format("%02d", m)%>"><%=String.format("%02d", m)%>월</option>
						<%
						}
						%>
					</select>
				</div>
			</div>
			<div class="table-wrap" style="max-height: 420px; overflow-y: auto;">
				<table class="data-table source-data-table" id="recordListTable">
					<thead>
						<tr>
							<th>번호</th>
							<th>입력일자</th>
							<th>근태항목</th>
							<th>근태기간</th>
							<th>근태일수</th>
							<th>금액</th>
							<th>적요</th>
							<th>수정/삭제</th>
						</tr>
					</thead>
					<tbody>
						<%
						if (recordList != null) {
							int no = recordList.size();
							for (AttendanceRecord rec : recordList) {
								String periodLabel = rec.getStartDate() == null ? "-"
										: (rec.getStartDate().equals(rec.getEndDate()) ? rec.getStartDate().toString()
												: rec.getStartDate() + " ~ " + rec.getEndDate());
								String countLabel = rec.getDayCount() != null ? rec.getDayCount().stripTrailingZeros().toPlainString()
										: (rec.getHourCount() != null ? rec.getHourCount().stripTrailingZeros().toPlainString() : "-");
								String dataYear = rec.getStartDate() == null ? "" : String.valueOf(rec.getStartDate().toLocalDate().getYear());
								String dataMonth = rec.getStartDate() == null ? "" : String.format("%02d", rec.getStartDate().toLocalDate().getMonthValue());
						%>
						<tr data-year="<%=dataYear%>" data-month="<%=dataMonth%>">
							<td><%=no--%></td>
							<td><%=rec.getCreatedAt() == null ? "-" : rec.getCreatedAt().toString()%></td>
							<td><%=rec.getAttendanceName()%></td>
							<td><%=periodLabel%></td>
							<td><%=countLabel%></td>
							<td><%=rec.getAllowanceAmountValue()%></td>
							<td><%=rec.getDescription() == null ? "" : rec.getDescription()%></td>
							<td>
								<a class="btn btn-sm"
									href="<%=ctx%>/Diligence/diligenceMntSelect.do?employeeId=<%=selectedEmployee.getEmployeeId()%>&editId=<%=rec.getAttendanceId()%>">수정</a>
								<form method="post" action="<%=ctx%>/Diligence/diligenceMntDelete.do" style="display: inline;">
									<input type="hidden" name="attendanceId" value="<%=rec.getAttendanceId()%>">
									<input type="hidden" name="employeeId" value="<%=selectedEmployee.getEmployeeId()%>">
									<button type="submit" class="btn btn-sm">삭제</button>
								</form>
							</td>
						</tr>
						<%
							}
						}
						%>
					</tbody>
				</table>
			</div>
			<%
			}
			%>
		</div>
		<div class="dialog-footer">
			<button type="button" class="btn" data-dialog-close>닫기</button>
		</div>
	</div>
</div>

<div class="dialog-backdrop <%=showLeaveStatusDialog ? "open" : ""%>">
	<div class="dialog" style="width: min(760px, 100%);">
		<div class="dialog-header">
			<strong>휴가일수 현황</strong>
			<button type="button" class="btn btn-icon" data-dialog-close>×</button>
		</div>
		<div class="dialog-body">
			<div class="table-wrap">
				<table class="data-table source-data-table">
					<thead>
						<tr>
							<th>구분</th>
							<th>성명</th>
							<th>직위</th>
							<th>휴가항목</th>
							<th>전체</th>
							<th>사용</th>
							<th>잔여</th>
						</tr>
					</thead>
					<tbody>
						<%
						if (leaveStatusList != null && !leaveStatusList.isEmpty()) {
							for (EmployeeLeaveStatus status : leaveStatusList) {
						%>
						<tr>
							<td><%=status.getEmploymentType() == null ? "-" : status.getEmploymentType()%></td>
							<td><%=status.getEmployeeName()%></td>
							<td><%=status.getPosition() == null ? "-" : status.getPosition()%></td>
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
							<td colspan="7">부여받은 휴가항목이 없습니다.</td>
						</tr>
						<%
						}
						%>
					</tbody>
				</table>
			</div>
		</div>
		<div class="dialog-footer">
			<button type="button" class="btn" data-dialog-close>닫기</button>
		</div>
	</div>
</div>
<script>
(function() {
	var searchInput = document.getElementById('employeeSearchInput');
	var resetBtn = document.getElementById('employeeResetBtn');
	var table = document.getElementById('employeeListTable');
	if (!table) return;

	function applyFilter() {
		var keyword = (searchInput.value || '').trim().toLowerCase();
		table.querySelectorAll('tbody tr').forEach(function(row) {
			row.hidden = keyword !== '' && !row.textContent.toLowerCase().includes(keyword);
		});
	}

	searchInput.addEventListener('input', applyFilter);
	resetBtn.addEventListener('click', function() {
		searchInput.value = '';
		applyFilter();
	});
})();

(function() {
	var typeSelect = document.getElementById('attendanceTypeSelect');
	var countLabelTh = document.getElementById('countLabelTh');
	var countInput = document.getElementById('countInput');
	var countUnitSuffix = document.getElementById('countUnitSuffix');
	if (!typeSelect || !countLabelTh) return;

	function applyUnitLabel() {
		var selected = typeSelect.options[typeSelect.selectedIndex];
		var unit = selected ? selected.getAttribute('data-unit') : 'DAY';
		var isHour = unit === 'HOUR';
		var label = isHour ? '근태시간' : '근태일수';

		countLabelTh.textContent = label;
		if (countInput) countInput.placeholder = label;
		if (countUnitSuffix) countUnitSuffix.textContent = isHour ? '시간' : '일';
	}

	typeSelect.addEventListener('change', applyUnitLabel);
	applyUnitLabel();
})();

(function() {
	var checkboxes = document.querySelectorAll('.employeeSelectCheckbox');
	var employeeIdInput = document.getElementById('entryEmployeeId');
	var employeeIdsInput = document.getElementById('leaveStatusEmployeeIds');
	if (!checkboxes.length || !employeeIdInput) return;

	function sync(lastChanged) {
		var checked = Array.prototype.filter.call(checkboxes, function(cb) {
			return cb.checked;
		});

		if (employeeIdsInput) {
			employeeIdsInput.value = checked.map(function(cb) {
				return cb.value;
			}).join(',');
		}

		if (lastChanged && lastChanged.checked) {
			employeeIdInput.value = lastChanged.value; // 근태기록 입력폼은 방금 체크한 사원 기준
		} else if (checked.length > 0) {
			employeeIdInput.value = checked[checked.length - 1].value;
		} else {
			employeeIdInput.value = '';
		}
	}

	checkboxes.forEach(function(cb) {
		cb.addEventListener('change', function() {
			sync(this);
		});
	});
})();

(function() {
	var yearFilter = document.getElementById('recordYearFilter');
	var monthFilter = document.getElementById('recordMonthFilter');
	var table = document.getElementById('recordListTable');
	if (!table || !yearFilter) return;

	function applyRecordFilter() {
		var year = yearFilter.value;
		var month = monthFilter.value;
		table.querySelectorAll('tbody tr').forEach(function(row) {
			var matchesYear = year === '' || row.dataset.year === year;
			var matchesMonth = month === '' || row.dataset.month === month;
			row.hidden = !(matchesYear && matchesMonth);
		});
	}

	yearFilter.addEventListener('change', applyRecordFilter);
	monthFilter.addEventListener('change', applyRecordFilter);
	applyRecordFilter();
})();
</script>
<%
if (leaveStatusMessage != null) {
%>
<script>alert('<%=leaveStatusMessage.replace("'", "\\'")%>');</script>
<%
}
if (saveMessage != null) {
%>
<script>alert('<%=saveMessage.replace("'", "\\'")%>');</script>
<%
}
%>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
