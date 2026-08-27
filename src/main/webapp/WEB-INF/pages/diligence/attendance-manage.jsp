<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.LinkedHashSet"%>
<%@ page import="config.model.EmployeeLeave"%>
<%@ page import="config.model.AttendanceType"%>
<%@ page import="config.model.AttendanceRecord"%>
<%@ page import="config.model.EmployeeLeaveStatus"%>
<%@ page import="config.model.LeaveType"%>
<%
// ページ表示に必要なデータをrequestから取得 / 화면 표시에 필요한 데이터 취득
List<EmployeeLeave> employeeList = (List<EmployeeLeave>) request.getAttribute("employeeList");
List<AttendanceType> attendanceTypeList = (List<AttendanceType>) request.getAttribute("attendanceTypeList");
List<LeaveType> leaveOnlyTypeList = (List<LeaveType>) request.getAttribute("leaveOnlyTypeList");
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

// 年度ドロップダウン用リスト / 연도 드롭다운 목록
int currentYear = java.time.LocalDate.now().getYear();
List<Integer> yearOptions = new java.util.ArrayList<>();
for (int y = currentYear - 12; y <= currentYear + 1; y++) {
	yearOptions.add(y);
}
%>
<%
request.setAttribute("pageTitle", "勤怠記録／管理");
request.setAttribute("pageSection", "勤怠管理");
request.setAttribute("pageDescription", "社員を選択して休暇・遅刻・早退・時間外勤務の記録を入力・修正・削除します。");
request.setAttribute("activeKey", "attendance-manage");
request.setAttribute("pageCss", "attendance.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<div class="source-two-pane">
	<!-- 社員リストパネル / 사원 목록 패널 -->
	<section class="source-list-panel">
		<div class="source-list-search">
			<input class="input" type="text" id="employeeSearchInput" placeholder="検索キーワードを入力">
			<button class="btn btn-primary" type="button" id="employeeResetBtn">全件表示</button>
		</div>
		<div class="table-wrap">
			<table class="data-table source-data-table compact-list" id="employeeListTable">
				<thead>
					<tr>
						<th>選択</th>
						<th>区分</th>
						<th>社員番号</th>
						<th>氏名</th>
						<th>部署</th>
						<th>役職</th>
						<th>管理</th>
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
							href="<%=ctx%>/Diligence/diligenceMntSelect.do?employeeId=<%=emp.getEmployeeId()%>">管理</a></td>
					</tr>
					<%
						}
					}
					%>
				</tbody>
			</table>
		</div>
	</section>

	<!-- 勤怠記録入力パネル / 근태기록 입력 패널 -->
	<section class="source-entry-panel">
		<div class="source-editor-head">勤怠記録 入力<%=isEditing ? "（修正中）" : ""%></div>
		<form id="attendanceRecordForm" method="post">
			<!-- 選択中社員ID（hidden） / 선택 중인 사원ID (hidden) -->
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
						<th>入力日付</th>
						<td class="span-3"><input type="date" class="input" name="inputDate"
							value="<%=isEditing && editRecord.getCreatedAt() != null ? editRecord.getCreatedAt().toString()
									: new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())%>"></td>
					</tr>
					<tr>
						<th>勤怠項目</th>
						<td class="span-3"><select class="select" id="attendanceTypeSelect" name="attendanceTypeId">
								<option value="" data-unit="DAY" <%=!isEditing ? "selected" : ""%>>選択してください</option>
								<%
								if (attendanceTypeList != null) {
									for (AttendanceType type : attendanceTypeList) {
								%>
								<option value="<%=type.getAttendanceTypeId()%>" data-unit="<%=type.getUnitCode() == null ? "DAY" : type.getUnitCode()%>"
									<%=isEditing && type.getAttendanceTypeId().equals(editRecord.getAttendanceTypeId()) ? "selected" : ""%>><%=type.getAttendanceName()%></option>
								<%
									}
								}
								if (leaveOnlyTypeList != null) {
									for (LeaveType leaveType : leaveOnlyTypeList) {
								%>
								<option value="leave-<%=leaveType.getLeaveTypeId()%>" data-unit="DAY"><%=leaveType.getLeaveName()%></option>
								<%
									}
								}
								%>
						</select></td>
					</tr>
					<tr>
						<th>期間</th>
						<td class="span-3"><div class="range">
								<input class="input" type="date" name="startDate" id="startDateInput"
									value="<%=isEditing && editRecord.getStartDate() != null ? editRecord.getStartDate().toString() : ""%>"><span>~</span><input
									class="input" type="date" name="endDate" id="endDateInput"
									value="<%=isEditing && editRecord.getEndDate() != null ? editRecord.getEndDate().toString() : ""%>">
							</div></td>
					</tr>
					<tr>
						<th id="countLabelTh">勤怠日数</th>
						<td class="span-3"><div class="inline-control">
								<input class="input number" type="text" name="count" id="countInput"
									placeholder="勤怠日数"
									value="<%=isEditing ? (editRecord.getDayCount() != null ? editRecord.getDayCount().stripTrailingZeros().toPlainString()
											: (editRecord.getHourCount() != null ? editRecord.getHourCount().stripTrailingZeros().toPlainString() : "")) : ""%>">
								<span id="countUnitSuffix">日</span>
								<!-- 休暇日数現況ボタン / 휴가일수 현황 버튼 -->
								<button type="submit" class="btn btn-sm" form="attendanceRecordForm"
									formaction="<%=ctx%>/Diligence/diligenceMntLeaveStatus.do">休暇日数現況</button>
							</div></td>
					</tr>
					<tr>
						<th>金額（手当）</th>
						<td class="span-3"><div class="money-control">
								<input class="input number" type="text" name="amount"
									value="<%=isEditing && editRecord.getAllowanceAmount() != null ? editRecord.getAllowanceAmount().stripTrailingZeros().toPlainString() : ""%>"><span>円</span>
							</div></td>
					</tr>
					<tr>
						<th>摘要</th>
						<td class="span-3"><input type="text" class="input" name="description"
							placeholder="摘要を入力"
							value="<%=isEditing && editRecord.getDescription() != null ? editRecord.getDescription() : ""%>"></td>
					</tr>
				</tbody>
			</table>
			<!-- 保存・クリアボタン / 저장/내용지우기 버튼 -->
			<div class="source-editor-actions">
				<button type="submit" class="btn btn-primary" form="attendanceRecordForm"
					formaction="<%=ctx%>/Diligence/<%=isEditing ? "diligenceMntUpdate" : "diligenceMntInsert"%>.do">保存</button>
				<button type="reset" class="btn" form="attendanceRecordForm">内容をクリア</button>
			</div>
		</form>
	</section>
</div>

<!-- 社員別勤怠記録ダイアログ / 사원별 근태기록 다이얼로그 -->
<div class="dialog-backdrop <%=showRecordDialog ? "open" : ""%>">
	<div class="dialog" style="width: min(980px, 100%); min-height: 560px; display: flex; flex-direction: column;">
		<div class="dialog-header">
			<strong>社員別 勤怠記録</strong>
			<button type="button" class="btn btn-icon" data-dialog-close>×</button>
		</div>
		<div class="dialog-body" style="flex: 1;">
			<%
			if (hasSelectedEmployee) {
			%>
			<div class="table-toolbar compact" style="justify-content: space-between;">
				<div>
					<!-- 選択中社員の基本情報表示 / 선택 사원 기본정보 표시 -->
					· 氏名：<%=selectedEmployee.getEmployeeName()%> (<%=selectedEmployee.getEmployeeNo()%>)
					&nbsp;&nbsp;· 部署：<%=selectedEmployee.getDepartment() == null ? "-" : selectedEmployee.getDepartment()%>
					&nbsp;&nbsp;· 役職：<%=selectedEmployee.getPosition() == null ? "-" : selectedEmployee.getPosition()%>
				</div>
				<div style="display: flex; gap: 6px;">
					<!-- 年度・月フィルター / 연도·월 필터 -->
					<select class="select" id="recordYearFilter" style="width: 100px;">
						<option value="">選択</option>
						<%
						for (Integer y : yearOptions) {
						%>
						<option value="<%=y%>" <%=y == currentYear ? "selected" : ""%>><%=y%>年</option>
						<%
						}
						%>
					</select>
					<select class="select" id="recordMonthFilter" style="width: 100px;">
						<option value="">全期間</option>
						<%
						for (int m = 1; m <= 12; m++) {
						%>
						<option value="<%=String.format("%02d", m)%>"><%=String.format("%02d", m)%>月</option>
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
							<th>番号</th>
							<th>入力日付</th>
							<th>勤怠項目</th>
							<th>勤怠期間</th>
							<th>勤怠日数</th>
							<th>金額</th>
							<th>摘要</th>
							<th>修正／削除</th>
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
									href="<%=ctx%>/Diligence/diligenceMntSelect.do?employeeId=<%=selectedEmployee.getEmployeeId()%>&editId=<%=rec.getAttendanceId()%>">修正</a>
								<form method="post" action="<%=ctx%>/Diligence/diligenceMntDelete.do" style="display: inline;">
									<input type="hidden" name="attendanceId" value="<%=rec.getAttendanceId()%>">
									<input type="hidden" name="employeeId" value="<%=selectedEmployee.getEmployeeId()%>">
									<button type="submit" class="btn btn-sm">削除</button>
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
			<button type="button" class="btn" data-dialog-close>閉じる</button>
		</div>
	</div>
</div>

<!-- 休暇日数現況ダイアログ / 휴가일수 현황 다이얼로그 -->
<div class="dialog-backdrop <%=showLeaveStatusDialog ? "open" : ""%>">
	<div class="dialog" style="width: min(760px, 100%);">
		<div class="dialog-header">
			<strong>休暇日数現況</strong>
			<button type="button" class="btn btn-icon" data-dialog-close>×</button>
		</div>
		<div class="dialog-body">
			<div class="table-wrap">
				<table class="data-table source-data-table">
					<thead>
						<tr>
							<th>区分</th>
							<th>氏名</th>
							<th>役職</th>
							<th>休暇項目</th>
							<th>合計</th>
							<th>使用</th>
							<th>残余</th>
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
							<td colspan="7">付与された休暇項目がありません。</td>
						</tr>
						<%
						}
						%>
					</tbody>
				</table>
			</div>
		</div>
		<div class="dialog-footer">
			<button type="button" class="btn" data-dialog-close>閉じる</button>
		</div>
	</div>
</div>
<script>
// 社員リスト検索フィルター / 사원 목록 검색 필터
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

// 勤怠項目の単位（日／時間）に応じてラベルと自動計算を切り替え / 근태항목 단위에 따라 라벨·자동계산 전환
(function() {
	var typeSelect = document.getElementById('attendanceTypeSelect');
	var countLabelTh = document.getElementById('countLabelTh');
	var countInput = document.getElementById('countInput');
	var countUnitSuffix = document.getElementById('countUnitSuffix');
	var startDateInput = document.getElementById('startDateInput');
	var endDateInput = document.getElementById('endDateInput');
	if (!typeSelect || !countLabelTh) return;

	function isHourUnit() {
		var selected = typeSelect.options[typeSelect.selectedIndex];
		var unit = selected ? selected.getAttribute('data-unit') : 'DAY';
		return unit === 'HOUR';
	}

	function applyUnitLabel() {
		var isHour = isHourUnit();
		var label = isHour ? '勤怠時間' : '勤怠日数';
		countLabelTh.textContent = label;
		if (countInput) countInput.placeholder = label;
		if (countUnitSuffix) countUnitSuffix.textContent = isHour ? '時間' : '日';
	}

	// 開始日～終了日から勤怠日数を自動計算（日単位のみ） / 시작일~종료일로 근태일수 자동계산 (일 단위만)
	function recalcDayCount() {
		if (isHourUnit() || !countInput) return;
		if (!startDateInput || !endDateInput || !startDateInput.value || !endDateInput.value) return;
		var start = new Date(startDateInput.value);
		var end = new Date(endDateInput.value);
		var diffDays = Math.round((end - start) / (1000 * 60 * 60 * 24)) + 1;
		if (diffDays > 0) countInput.value = diffDays;
	}

	typeSelect.addEventListener('change', function() {
		applyUnitLabel();
		recalcDayCount();
	});
	if (startDateInput) startDateInput.addEventListener('change', recalcDayCount);
	if (endDateInput) endDateInput.addEventListener('change', recalcDayCount);
	applyUnitLabel();
})();

// チェックボックス選択時にemployeeId/employeeIdsをフォームに同期 / 체크박스 선택 시 ID를 폼에 동기화
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
			employeeIdsInput.value = checked.map(function(cb) { return cb.value; }).join(',');
		}
		if (lastChanged && lastChanged.checked) {
			employeeIdInput.value = lastChanged.value;
		} else if (checked.length > 0) {
			employeeIdInput.value = checked[checked.length - 1].value;
		} else {
			employeeIdInput.value = '';
		}
	}

	checkboxes.forEach(function(cb) {
		cb.addEventListener('change', function() { sync(this); });
	});
})();

// 年度・月フィルターで勤怠記録を絞り込み / 연도·월 필터로 근태기록 조회
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
// アラートメッセージ表示 / 알림 메시지 표시
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
