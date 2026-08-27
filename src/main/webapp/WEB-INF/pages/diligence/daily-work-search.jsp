<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.DailyWorkRecord"%>
<%
// ページ表示に必要なデータをrequestから取得 / 화면 표시에 필요한 데이터 취득
List<DailyWorkRecord> recordList = (List<DailyWorkRecord>) request.getAttribute("recordList");
String[] workSiteOptions = (String[]) request.getAttribute("workSiteOptions");
String searchMonth = (String) request.getAttribute("searchMonth");
String selectedWorkSiteName = (String) request.getAttribute("selectedWorkSiteName");
String employeeNameKeyword = (String) request.getAttribute("employeeNameKeyword");
String payAmountTotal = (String) request.getAttribute("payAmountTotal");
String netPayTotal = (String) request.getAttribute("netPayTotal");
%>
<%
request.setAttribute("pageTitle", "日雇労働者 勤務照会");
request.setAttribute("pageSection", "勤怠管理");
request.setAttribute("pageDescription", "期間・現場・社員条件で日雇労働者の勤務記録と支給合計を照会します。");
request.setAttribute("activeKey", "daily-work-search");
request.setAttribute("pageCss", "attendance.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>

<!-- 検索条件フォーム / 검색 조건 폼 -->
<form id="searchForm" method="post" action="<%=ctx%>/Diligence/dayWorkerSearchMonth.do">
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
			<label>現場</label>
			<select class="select" name="workSiteName">
				<option value="" <%=selectedWorkSiteName == null || selectedWorkSiteName.isBlank() ? "selected" : ""%>>全件</option>
				<%
				if (workSiteOptions != null) {
					for (String site : workSiteOptions) {
				%>
				<option value="<%=site%>" <%=site.equals(selectedWorkSiteName) ? "selected" : ""%>><%=site%></option>
				<%
					}
				}
				%>
			</select>
		</div>
		<div class="field">
			<label>社員名</label>
			<input type="text" class="input" name="employeeName" placeholder="社員名を入力"
				value="<%=employeeNameKeyword == null ? "" : employeeNameKeyword%>">
		</div>
		<div class="actions">
			<!-- 照会ボタン / 조회 버튼 -->
			<button type="submit" class="btn btn-primary">照会</button>
		</div>
	</section>
</form>

<!-- 照会結果テーブル / 조회 결과 테이블 -->
<section class="card">
	<div class="card-header">
		<h2 class="section-title">日雇労働者 勤務照会 結果</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table">
				<thead>
					<tr>
						<th>氏名</th>
						<th>勤務日</th>
						<th>現場</th>
						<th>日当</th>
						<th>支給率</th>
						<th>支給額</th>
						<th>税金</th>
						<th>実支給額</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (recordList != null && !recordList.isEmpty()) {
						for (DailyWorkRecord record : recordList) {
					%>
					<tr>
						<td><%=record.getEmployeeName()%></td>
						<td><%=record.getWorkDate()%></td>
						<td><%=record.getWorkSiteName()%></td>
						<td><%=record.getDailyWageValue()%></td>
						<td><%=record.getPayRate() == null ? "-" : record.getPayRate().stripTrailingZeros().toPlainString()%></td>
						<td><%=record.getPayAmountValue()%></td>
						<td><%=record.getTotalTaxValue()%></td>
						<td><%=record.getNetPayAmountValue()%></td>
					</tr>
					<%
						}
					} else {
					%>
					<tr>
						<td colspan="8">照会された勤務記録がありません。</td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
		</div>
		<!-- 支給額合計・実支給額合計 / 지급액 합계·실지급액 합계 -->
		<div class="tfoot-summary">
			<span>支給額合計 <%=payAmountTotal%>円</span> <span>実支給額合計 <%=netPayTotal%>円</span>
		</div>
	</div>
</section>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
