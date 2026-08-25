<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.EmployeeLeave"%>
<%@ page import="config.model.DailyWorkRecord"%>
<%
List<EmployeeLeave> employeeList = (List<EmployeeLeave>) request.getAttribute("employeeList");
EmployeeLeave selectedEmployee = (EmployeeLeave) request.getAttribute("selectedEmployee");
List<DailyWorkRecord> recordList = (List<DailyWorkRecord>) request.getAttribute("recordList");
DailyWorkRecord editRecord = (DailyWorkRecord) request.getAttribute("editRecord");
String saveMessage = (String) request.getAttribute("saveMessage");
boolean hasSelectedEmployee = selectedEmployee != null;
Boolean showRecordDialogAttr = (Boolean) request.getAttribute("showRecordDialog");
boolean showRecordDialog = hasSelectedEmployee && (showRecordDialogAttr == null || showRecordDialogAttr);
boolean isEditing = editRecord != null;

// 현장/프로젝트 목록 - 별도 관리 화면 없이 고정 목록
String[] workSiteOptions = { "현장1", "현장2", "연구소", "개발프로젝트", "제1공장" };
%>
<%
request.setAttribute("pageTitle", "일용직 근무기록/관리");
request.setAttribute("pageSection", "근태관리");
request.setAttribute("pageDescription", "일용직 사원의 일자별 현장, 일당, 지급률, 세금과 실지급액을 기록합니다.");
request.setAttribute("activeKey", "daily-work-manage");
request.setAttribute("pageCss", "attendance.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>

<div class="source-two-pane">
	<section class="source-list-panel">
		<div class="source-list-search">
			<input class="input" type="text" id="employeeSearchInput" placeholder="검색어 입력">
			<button class="btn btn-primary" type="button" id="employeeSearchResetBtn">전체보기</button>
		</div>
		<div class="table-wrap">
			<table class="data-table source-data-table compact-list" id="employeeTable">
				<thead>
					<tr>
						<th>선택</th>
						<th>구분</th>
						<th>사원번호</th>
						<th>성명</th>
						<th>부서</th>
						<th>근무기록</th>
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
							href="<%=ctx%>/Diligence/dayWorkerMntSelect.do?employeeId=<%=emp.getEmployeeId()%>">관리</a></td>
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
		<div class="source-editor-head">일용직 근무기록<%=isEditing ? " (수정 중)" : ""%></div>
		<form id="dailyWorkForm" method="post">
			<input type="hidden" id="entryEmployeeId" name="employeeId" value="<%=hasSelectedEmployee ? selectedEmployee.getEmployeeId() : ""%>">
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
						<th>근무일자</th>
						<td class="span-3"><input type="date" class="input" name="workDate"
							value="<%=isEditing && editRecord.getWorkDate() != null ? editRecord.getWorkDate().toString() : java.time.LocalDate.now().toString()%>"></td>
					</tr>
					<tr>
						<th>현장/프로젝트</th>
						<td class="span-3"><select class="select" name="workSiteName">
								<option value="" <%=!isEditing ? "selected" : ""%>>선택하세요</option>
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
						<th>일당</th>
						<td class="span-3"><div class="money-control">
								<input class="input number" type="text" id="dailyWageInput" name="dailyWage" style="text-align: right;"
									value="<%=isEditing && editRecord.getDailyWage() != null ? editRecord.getDailyWage().stripTrailingZeros().toPlainString() : ""%>">
							</div></td>
					</tr>
					<tr>
						<th>지급율</th>
						<td class="span-3"><div class="money-control">
								<input class="input number" type="text" id="payRateInput" name="payRate" style="text-align: right;"
									value="<%=isEditing && editRecord.getPayRate() != null ? editRecord.getPayRate().stripTrailingZeros().toPlainString() : "1.0"%>"><span></span>
							</div></td>
					</tr>
					<tr>
						<th>소득세</th>
						<td class="span-3"><div class="money-control auto-value" style="justify-content: flex-end;">
								<strong id="incomeTaxPreview"><%=isEditing ? editRecord.getIncomeTaxAmountValue() : "0"%></strong>
							</div></td>
					</tr>
					<tr>
						<th>지방소득세</th>
						<td class="span-3"><div class="money-control auto-value" style="justify-content: flex-end;">
								<strong id="localTaxPreview"><%=isEditing ? editRecord.getLocalIncomeTaxAmountValue() : "0"%></strong>
							</div></td>
					</tr>
					<tr>
						<th>실지급액</th>
						<td class="span-3"><div class="money-control auto-value" style="justify-content: flex-end;">
								<strong id="netPayPreview"><%=isEditing ? editRecord.getNetPayAmountValue() : "0"%></strong>
							</div></td>
					</tr>
				</tbody>
			</table>
			<div class="source-editor-actions">
				<button type="submit" class="btn btn-primary"
					formaction="<%=ctx%>/Diligence/<%=isEditing ? "dayWorkerMntUpdate" : "dayWorkerMntInsert"%>.do">저장</button>
				<button type="reset" class="btn">내용 지우기</button>
			</div>
		</form>
	</section>
</div>

<div class="dialog-backdrop <%=showRecordDialog ? "open" : ""%>">
	<div class="dialog" style="width: min(900px, 100%);">
		<div class="dialog-header">
			<strong>일용직 근무기록<%=hasSelectedEmployee ? " - " + selectedEmployee.getEmployeeName() : ""%></strong>
			<button type="button" class="btn btn-icon" data-dialog-close>×</button>
		</div>
		<div class="dialog-body">
			<div class="table-wrap">
				<table class="data-table">
					<thead>
						<tr>
							<th>번호</th>
							<th>근무일자</th>
							<th>현장/프로젝트</th>
							<th>일당</th>
							<th>지급율</th>
							<th>소득세</th>
							<th>지방소득세</th>
							<th>실지급액</th>
							<th>수정/삭제</th>
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
									<button type="submit" class="btn btn-sm">수정</button>
								</form>
								<form method="post" style="display: inline;"
									action="<%=ctx%>/Diligence/dayWorkerMntDelete.do"
									onsubmit="return confirm('삭제하시겠습니까?');">
									<input type="hidden" name="employeeId" value="<%=selectedEmployee.getEmployeeId()%>">
									<input type="hidden" name="dailyWorkRecordId" value="<%=rec.getDailyWorkRecordId()%>">
									<button type="submit" class="btn btn-sm">삭제</button>
								</form>
							</td>
						</tr>
						<%
							}
						} else {
						%>
						<tr>
							<td colspan="9">등록된 근무기록이 없습니다.</td>
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
	var backdrops = document.querySelectorAll('.dialog-backdrop');
	backdrops.forEach(function(backdrop) {
		backdrop.addEventListener('click', function(e) {
			if (e.target === backdrop || e.target.closest('[data-dialog-close]')) {
				backdrop.classList.remove('open');
			}
		});
	});
})();

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

(function() {
	var checkboxes = document.querySelectorAll('.employeeSelectCheckbox');
	var employeeIdInput = document.getElementById('entryEmployeeId');
	if (!checkboxes.length || !employeeIdInput) return;

	checkboxes.forEach(function(cb) {
		cb.addEventListener('change', function() {
			if (this.checked) {
				employeeIdInput.value = this.value; // 입력폼 대상은 마지막으로 체크한 사원 기준
			} else if (employeeIdInput.value === this.value) {
				employeeIdInput.value = '';
			}
		});
	});
})();

(function() {
	// 일당 * 지급율 기준으로 소득세/지방소득세/실지급액 미리보기 (실제 저장값은 항상 서버에서 다시 계산됨)
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
