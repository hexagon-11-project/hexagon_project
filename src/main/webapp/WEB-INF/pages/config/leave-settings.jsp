<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.LeaveType"%>
<%@ page import="config.model.AttendanceType"%>
<%@ page import="config.model.EmployeeLeave"%>
<%
// ページ表示に必要なデータをrequestから取得 / 화면 표시에 필요한 데이터를 request에서 취득
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
request.setAttribute("pageTitle", "休暇・勤怠設定");
request.setAttribute("pageSection", "基本環境");
request.setAttribute("pageDescription", "休暇種別と勤怠項目、単位、使用有無および休暇控除連携を設定します。");
request.setAttribute("activeKey", "leave-settings");
request.setAttribute("pageCss", "environment.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>

<!-- 休暇項目設定セクション / 휴가항목 설정 섹션 -->
<section class="source-config-block">
	<div class="source-config-list">
		<div class="source-section-title">休暇項目設定</div>
		<div class="table-wrap">
			<table class="data-table source-data-table">
				<thead>
					<tr>
						<th>休暇項目</th>
						<th>適用期間</th>
						<th>社員別休暇日数</th>
						<th>使用有無</th>
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
							href="<%=ctx%>/Config/employeeleavemanage.do?leaveTypeId=<%=item.getLeaveTypeId()%>">管理</a></td>
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
	<!-- 休暇項目編集フォーム / 휴가항목 편집 폼 -->
	<div class="source-config-editor">
		<div class="source-editor-head">休暇項目</div>
		<form id="leaveTypeForm" method="post">
			<input type="hidden" name="leaveTypeId"
				value="<%=hasSelectedLeaveType ? selectedLeaveType.getLeaveTypeId() : ""%>">
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>休暇項目</th>
						<td class="span-3"><input type="text" class="input" name="leaveName"
						required
							placeholder="休暇項目を入力してください"
							value="<%=hasSelectedLeaveType && selectedLeaveType.getLeaveName() != null ? selectedLeaveType.getLeaveName() : ""%>">
						</td>
					</tr>
					<tr>
						<th>適用期間</th>
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
						<th>使用有無</th>
						<td class="span-3">
							<div class="check-list">
								<label> <input type="radio" name="useYn" value="Y"
									<%=!hasSelectedLeaveType || !"N".equalsIgnoreCase(selectedLeaveType.getUseYn()) ? "checked" : ""%>>
									使用
								</label> <label> <input type="radio" name="useYn" value="N"
									<%=hasSelectedLeaveType && "N".equalsIgnoreCase(selectedLeaveType.getUseYn()) ? "checked" : ""%>>
									使用しない
								</label>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			<!-- 追加・修正・削除・クリアボタン / 추가/수정/삭제/내용지우기 버튼 -->
			<div class="source-editor-actions">
				<button type="submit" class="btn btn-primary"
					formaction="<%=ctx%>/Config/leavetypeinsert.do">追加</button>
				<button type="submit" class="btn btn-blue"
					formaction="<%=ctx%>/Config/leavetypeupdate.do"
					<%=hasSelectedLeaveType ? "" : "disabled"%>>修正</button>
				<button type="submit" class="btn"
					formaction="<%=ctx%>/Config/leavetypedelete.do"
					<%=hasSelectedLeaveType ? "" : "disabled"%>>削除</button>
				<button type="button" class="btn"
					onclick="location.href='<%=ctx%>/Config/leavesettingslist.do'">内容をクリア</button>
			</div>
		</form>
	</div>
</section>

<!-- 勤怠項目設定セクション / 근태항목 설정 섹션 -->
<section class="source-config-block">
	<div class="source-config-list">
		<div class="source-section-title">勤怠項目設定</div>
		<div class="table-wrap">
			<table class="data-table source-data-table">
				<thead>
					<tr>
						<th>勤怠項目</th>
						<th>単位</th>
						<th>グループ管理</th>
						<th>休暇控除</th>
						<th>使用有無</th>
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
	<!-- 勤怠項目編集フォーム / 근태항목 편집 폼 -->
	<div class="source-config-editor">
		<div class="source-editor-head">勤怠項目</div>
		<form id="attendanceTypeForm" method="post">
			<input type="hidden" name="attendanceTypeId"
				value="<%=hasSelectedAttendanceType ? selectedAttendanceType.getAttendanceTypeId() : ""%>">
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>勤怠項目</th>
						<td class="span-3"><input type="text" class="input"
							name="attendanceName" placeholder="勤怠項目を入力してください"
							value="<%=hasSelectedAttendanceType && selectedAttendanceType.getAttendanceName() != null ? selectedAttendanceType.getAttendanceName() : ""%>">
						</td>
					</tr>
					<tr>
						<th>単位</th>
						<td class="span-3"><select class="select" name="unitCode">
								<option value=""
									<%=!hasSelectedAttendanceType || selectedAttendanceType.getUnitCode() == null ? "selected" : ""%>>選択してください</option>
								<option value="DAY"
									<%=hasSelectedAttendanceType && "DAY".equalsIgnoreCase(selectedAttendanceType.getUnitCode()) ? "selected" : ""%>>日</option>
								<option value="HOUR"
									<%=hasSelectedAttendanceType && "HOUR".equalsIgnoreCase(selectedAttendanceType.getUnitCode()) ? "selected" : ""%>>時間</option>
						</select></td>
					</tr>
					<tr>
						<th>勤怠グループ</th>
						<td class="span-3">
							<div class="inline-control">
								<select class="select" name="attendanceGroupCode">
									<option value=""
										<%=!hasSelectedAttendanceType || selectedAttendanceType.getAttendanceGroupCode() == null ? "selected" : ""%>>選択してください</option>
									<option value="휴가"
										<%=hasSelectedAttendanceType && "휴가".equals(selectedAttendanceType.getAttendanceGroupCode()) ? "selected" : ""%>>休暇</option>
									<option value="지각/조퇴"
										<%=hasSelectedAttendanceType && "지각/조퇴".equals(selectedAttendanceType.getAttendanceGroupCode()) ? "selected" : ""%>>遅刻/早退</option>
									<option value="연장근무"
										<%=hasSelectedAttendanceType && "연장근무".equals(selectedAttendanceType.getAttendanceGroupCode()) ? "selected" : ""%>>時間外勤務</option>
									<option value="기타"
										<%=hasSelectedAttendanceType && "기타".equals(selectedAttendanceType.getAttendanceGroupCode()) ? "selected" : ""%>>その他</option>
								</select>
							</div>
						</td>
					</tr>
					<tr>
						<th>休暇控除</th>
						<td class="span-3"><select class="select" name="leaveTypeId">
								<option value=""
									<%=!hasSelectedAttendanceType || selectedAttendanceType.getLeaveTypeId() == null ? "selected" : ""%>>未連結</option>
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
						<th>使用有無</th>
						<td class="span-3">
							<div class="check-list">
								<label> <input type="radio" name="useYn" value="Y"
									<%=!hasSelectedAttendanceType || !"N".equalsIgnoreCase(selectedAttendanceType.getUseYn()) ? "checked" : ""%>>
									使用
								</label> <label> <input type="radio" name="useYn" value="N"
									<%=hasSelectedAttendanceType && "N".equalsIgnoreCase(selectedAttendanceType.getUseYn()) ? "checked" : ""%>>
									使用しない
								</label>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			<div class="source-editor-actions">
				<button type="submit" class="btn btn-primary"
					formaction="<%=ctx%>/Config/attendancetypeinsert.do">追加</button>
				<button type="submit" class="btn btn-blue"
					formaction="<%=ctx%>/Config/attendancetypeupdate.do"
					<%=hasSelectedAttendanceType ? "" : "disabled"%>>修正</button>
				<button type="submit" class="btn"
					formaction="<%=ctx%>/Config/attendancetypedelete.do"
					<%=hasSelectedAttendanceType ? "" : "disabled"%>>削除</button>
				<button type="button" class="btn"
					onclick="location.href='<%=ctx%>/Config/leavesettingslist.do'">内容をクリア</button>
			</div>
		</form>
	</div>
</section>

<!-- 休暇日数設定ダイアログ / 휴가일수 설정 다이얼로그 -->
<div class="dialog-backdrop <%=showEmployeeLeaveDialog ? "open" : ""%>">
	<div class="dialog" style="width: min(920px, 100%);">
		<div class="dialog-header">
			<strong>休暇日数設定</strong>
			<button type="button" class="btn btn-icon" data-dialog-close>×</button>
		</div>
		<div class="dialog-body">
			<%
			if (showEmployeeLeaveDialog) {
			%>
			<div class="table-toolbar compact" style="justify-content: space-between;">
				<div style="display: flex; gap: 6px; align-items: center;">
					<!-- 社員名・社員番号での検索 / 사원명/사원번호 검색 -->
					<input type="text" class="input" id="employeeLeaveSearchInput"
						placeholder="社員名／社員番号 検索">
					<button type="button" class="btn btn-icon" id="employeeLeaveSearchBtn">🔍</button>
					<button type="button" class="btn btn-sm" id="employeeLeaveResetBtn">全件表示</button>
				</div>
				<div style="display: flex; gap: 6px; align-items: center;">
					<select class="select" id="employeeLeaveStatusFilter">
						<option value="">状態別</option>
						<option value="재직">在職</option>
						<option value="퇴직">退職</option>
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
								<th>区分</th>
								<th>社員番号</th>
								<th>氏名</th>
								<th>部署</th>
								<th>役職</th>
								<th>入社日</th>
								<th>休暇日数</th>
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
									value="<%=row.getGrantedDaysValue()%>"> 日</td>
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
		<!-- ダイアログフッターボタン / 다이얼로그 하단 버튼 -->
		<div class="dialog-footer">
			<button type="submit" form="employeeLeaveForm" class="btn"
				formaction="<%=ctx%>/Config/employeeleavedelete.do">休暇日数削除</button>
			<button type="submit" form="employeeLeaveForm" class="btn"
				formaction="<%=ctx%>/Config/employeeleaveautocalc.do">休暇日数自動計算</button>
			<button type="submit" form="employeeLeaveForm" class="btn btn-primary"
				formaction="<%=ctx%>/Config/employeeleavesave.do">休暇日数保存</button>
			<button type="button" class="btn" data-dialog-close>閉じる</button>
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

	// キーワード・状態でフィルタリング / 키워드·상태로 필터링
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
// 保存完了アラート / 저장 완료 알림
if (Boolean.TRUE.equals(request.getAttribute("employeeLeaveJustSaved"))) {
%>
<script>alert('保存しました。');</script>
<%
}
%>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
