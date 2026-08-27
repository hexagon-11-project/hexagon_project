<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.EmployeeLeave"%>
<%@ page import="config.model.DailyWorkRecord"%>
<%
// ページ表示に必要なデータをrequestから取得 / 화면 표시에 필요한 데이터 취득
List<EmployeeLeave> employeeList = (List<EmployeeLeave>) request.getAttribute("employeeList");
EmployeeLeave selectedEmployee = (EmployeeLeave) request.getAttribute("selectedEmployee");
List<DailyWorkRecord> recordList = (List<DailyWorkRecord>) request.getAttribute("recordList");
DailyWorkRecord editRecord = (DailyWorkRecord) request.getAttribute("editRecord");
String saveMessage = (String) request.getAttribute("saveMessage");
boolean hasSelectedEmployee = selectedEmployee != null;
Boolean showRecordDialogAttr = (Boolean) request.getAttribute("showRecordDialog");
boolean showRecordDialog = hasSelectedEmployee && (showRecordDialogAttr == null || showRecordDialogAttr);
boolean isEditing = editRecord != null;

// 現場・プロジェクト固定リスト（別管理画面なし） / 현장/프로젝트 고정 목록
String[] workSiteOptions = { "現場1", "現場2", "研究所", "開発プロジェクト", "第1工場" };
%>
<%
request.setAttribute("pageTitle", "日雇労働者 勤務記録／管理");
request.setAttribute("pageSection", "勤怠管理");
request.setAttribute("pageDescription", "日雇労働者の日付別現場・日当・支給率・税金・実支給額を記録します。");
request.setAttribute("activeKey", "daily-work-manage");
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
			<button class="btn btn-primary" type="button" id="employeeSearchResetBtn">全件表示</button>
		</div>
		<div class="table-wrap">
			<table class="data-table source-data-table compact-list" id="employeeTable">
				<thead>
					<tr>
						<th>選択</th>
						<th>区分</th>
						<th>社員番号</th>
						<th>氏名</th>
						<th>部署</th>
						<th>勤務記録</th>
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
						<td><a class="btn btn-sm"
							href="<%=ctx%>/Diligence/dayWorkerMntSelect.do?employeeId=<%=emp.getEmployeeId()%>">管理</a></td>
					</tr>
					<%
						}
					}
					%>
				</tbody>
			</table>
		</div>
	</section>

	<!-- 勤務記録入力パネル / 근무기록 입력 패널 -->
	<section class="source-entry-panel">
		<div class="source-editor-head">日雇労働者 勤務記録<%=isEditing ? "（修正中）" : ""%></div>
		<form id="dailyWorkForm" method="post">
			<input type="hidden" id="entryEmployeeId" name="employeeId" value="<%=hasSelectedEmployee ? selectedEmployee.getEmployeeId() : ""%>">
			<input type="hidden" id="leaveStatusEmployeeIds" name="employeeIds" value="">
			<%
			if (isEditing) {
			%>
			<input type="hidden" name="dailyWorkRecordId" value="<%=editRecord.getDailyWorkRecordId()%>">
			<%
			}
			%>
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>勤務日付</th>
						<td class="span-3"><input type="date" class="input" name="workDate"
							value="<%=isEditing && editRecord.getWorkDate() != null ? editRecord.getWorkDate().toString() : java.time.LocalDate.now().toString()%>"></td>
					</tr>
					<tr>
						<th>現場／プロジェクト</th>
						<td class="span-3"><select class="select" name="workSiteName">
								<option value="" <%=!isEditing ? "selected" : ""%>>選択してください</option>
								<%
								for (String site : workSiteOptions) {
									boolean matchesEditing = isEditing && site.equals(editRecord.getWorkSiteName());
								%>
								<option value="<%=site%>" <%=matchesEditing ? "selected" : ""%>><%=site%></option>
								<%
								}
								%>
						</select></td>
					</tr>
					<tr>
						<th>日当</th>
						<td class="span-3"><div class="money-control">
								<input class="input number" type="text" id="dailyWageInput" name="dailyWage" style="text-align: right;"
									value="<%=isEditing && editRecord.getDailyWage() != null ? editRecord.getDailyWage().stripTrailingZeros().toPlainString() : ""%>">
							</div></td>
					</tr>
					<tr>
						<th>支給率</th>
						<td class="span-3"><div class="money-control">
								<input class="input number" type="text" id="payRateInput" name="payRate" style="text-align: right;"
									value="<%=isEditing && editRecord.getPayRate() != null ? editRecord.getPayRate().stripTrailingZeros().toPlainString() : "1.0"%>"><span></span>
							</div></td>
					</tr>
					<tr>
						<th>所得税</th>
						<td class="span-3"><div class="money-control auto-value" style="justify-content: flex-end;">
								<strong id="incomeTaxPreview"><%=isEditing ? editRecord.getIncomeTaxAmountValue() : "0"%></strong>
							</div></td>
					</tr>
					<tr>
						<th>地方所得税</th>
						<td class="span-3"><div class="money-control auto-value" style="justify-content: flex-end;">
								<strong id="localTaxPreview"><%=isEditing ? editRecord.getLocalIncomeTaxAmountValue() : "0"%></strong>
							</div></td>
					</tr>
					<tr>
						<th>実支給額</th>
						<td class="span-3"><div class="money-control auto-value" style="justify-content: flex-end;">
								<strong id="netPayPreview"><%=isEditing ? editRecord.getNetPayAmountValue() : "0"%></strong>
							</div></td>
					</tr>
				</tbody>
			</table>
			<!-- 保存・クリアボタン / 저장/내용지우기 버튼 -->
			<div class="source-editor-actions">
				<button type="submit" class="btn btn-primary"
					formaction="<%=ctx%>/Diligence/<%=isEditing ? "dayWorkerMntUpdate" : "dayWorkerMntInsert"%>.do">保存</button>
				<button type="reset" class="btn">内容をクリア</button>
			</div>
		</form>
	</section>
</div>

<!-- 日雇労働者 勤務記録ダイアログ / 일용직 근무기록 다이얼로그 -->
<div class="dialog-backdrop <%=showRecordDialog ? "open" : ""%>">
	<div class="dialog" style="width: min(900px, 100%);">
		<div class="dialog-header">
			<strong>日雇労働者 勤務記録<%=hasSelectedEmployee ? " - " + selectedEmployee.getEmployeeName() : ""%></strong>
			<button type="button" class="btn btn-icon" data-dialog-close>×</button>
		</div>
		<div class="dialog-body">
			<div class="table-wrap">
				<table class="data-table">
					<thead>
						<tr>
							<th>番号</th>
							<th>勤務日付</th>
							<th>現場／プロジェクト</th>
							<th>日当</th>
							<th>支給率</th>
							<th>所得税</th>
							<th>地方所得税</th>
							<th>実支給額</th>
							<th>修正／削除</th>
						</tr>
					</thead>
					<tbody>
						<%
						if (recordList != null && !recordList.isEmpty()) {
							int no = recordList.size();
							for (DailyWorkRecord rec : recordList) {
						%>
						<tr>
							<td><%=no--%></td>
							<td><%=rec.getWorkDate()%></td>
							<td><%=rec.getWorkSiteName()%></td>
							<td><%=rec.getDailyWageValue()%></td>
							<td><%=rec.getPayRate() == null ? "-" : rec.getPayRate().stripTrailingZeros().toPlainString()%></td>
							<td><%=rec.getIncomeTaxAmountValue()%></td>
							<td><%=rec.getLocalIncomeTaxAmountValue()%></td>
							<td><%=rec.getNetPayAmountValue()%></td>
							<td>
								<form method="post" style="display: inline;"
									action="<%=ctx%>/Diligence/dayWorkerMntSelect.do">
									<input type="hidden" name="employeeId" value="<%=selectedEmployee.getEmployeeId()%>">
									<input type="hidden" name="editId" value="<%=rec.getDailyWorkRecordId()%>">
									<button type="submit" class="btn btn-sm">修正</button>
								</form>
								<form method="post" style="display: inline;"
									action="<%=ctx%>/Diligence/dayWorkerMntDelete.do"
									onsubmit="return confirm('削除しますか？');">
									<input type="hidden" name="employeeId" value="<%=selectedEmployee.getEmployeeId()%>">
									<input type="hidden" name="dailyWorkRecordId" value="<%=rec.getDailyWorkRecordId()%>">
									<button type="submit" class="btn btn-sm">削除</button>
								</form>
							</td>
						</tr>
						<%
							}
						} else {
						%>
						<tr>
							<td colspan="9">登録された勤務記録がありません。</td>
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
// ダイアログ外クリックで閉じる / 모달 외부 클릭 시 닫기
(function() {
	var backdrops = document.querySelectorAll('.dialog-backdrop');
	backdrops.forEach(function(backdrop) {
		backdrop.addEventListener('click', function(e) {
			if (e.target === backdrop || e.target.closest('[data-dialog-close]')) {
				backdrop.classList.remove('open');
			}
		});
	});
})();

// 社員リスト検索フィルター / 사원 목록 검색 필터
(function() {
	var searchInput = document.getElementById('employeeSearchInput');
	var resetBtn = document.getElementById('employeeSearchResetBtn');
	var rows = document.querySelectorAll('#employeeTable tbody tr');
	if (!searchInput) return;

	function applyFilter() {
		var keyword = searchInput.value.trim().toLowerCase();
		rows.forEach(function(row) {
			var text = row.textContent.toLowerCase();
			row.style.display = (keyword === '' || text.indexOf(keyword) !== -1) ? '' : 'none';
		});
	}

	searchInput.addEventListener('input', applyFilter);
	resetBtn.addEventListener('click', function() {
		searchInput.value = '';
		applyFilter();
	});
})();

// チェックボックス選択時にemployeeId/employeeIdsをフォームに同期 / 체크박스 선택 시 ID를 폼에 동기화
(function() {
	var checkboxes = document.querySelectorAll('.employeeSelectCheckbox');
	var employeeIdInput = document.getElementById('entryEmployeeId');
	var employeeIdsInput = document.getElementById('leaveStatusEmployeeIds');
	if (!checkboxes.length || !employeeIdInput) return;

	checkboxes.forEach(function(cb) {
		cb.addEventListener('change', function() {
			var checked = Array.prototype.filter.call(checkboxes, function(c) { return c.checked; });
			if (employeeIdsInput) {
				employeeIdsInput.value = checked.map(function(c) { return c.value; }).join(',');
			}
			if (this.checked) {
				employeeIdInput.value = this.value;
			} else if (employeeIdInput.value === this.value) {
				employeeIdInput.value = checked.length > 0 ? checked[checked.length - 1].value : '';
			}
		});
	});
})();

// 日当・支給率から所得税・地方所得税・実支給額をプレビュー（実際の保存値はサーバーで再計算） / 일당·지급율로 세금 미리보기
(function() {
	var dailyWageInput = document.getElementById('dailyWageInput');
	var payRateInput = document.getElementById('payRateInput');
	var incomeTaxPreview = document.getElementById('incomeTaxPreview');
	var localTaxPreview = document.getElementById('localTaxPreview');
	var netPayPreview = document.getElementById('netPayPreview');
	if (!dailyWageInput || !payRateInput) return;

	var DEDUCTION = 150000;
	var TAX_RATE = 0.06;
	var TAX_CREDIT_RATE = 0.45;

	function floorTen(value) {
		return Math.floor(value / 10) * 10;
	}

	function recalcPreview() {
		var wage = parseFloat(dailyWageInput.value) || 0;
		var rate = parseFloat(payRateInput.value);
		if (isNaN(rate)) rate = 1;

		var payAmount = Math.round(wage * rate);
		var taxableBase = Math.max(0, payAmount - DEDUCTION);
		var incomeTax = floorTen(taxableBase * TAX_RATE * TAX_CREDIT_RATE);
		var localTax = floorTen(incomeTax * 0.1);
		var netPay = payAmount - incomeTax - localTax;

		incomeTaxPreview.textContent = incomeTax.toLocaleString();
		localTaxPreview.textContent = localTax.toLocaleString();
		netPayPreview.textContent = netPay.toLocaleString();
	}

	dailyWageInput.addEventListener('input', recalcPreview);
	payRateInput.addEventListener('input', recalcPreview);
})();
</script>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
