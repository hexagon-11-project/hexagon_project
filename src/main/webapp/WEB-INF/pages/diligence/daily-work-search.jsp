<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.DailyWorkRecord"%>
<%
List<DailyWorkRecord> recordList = (List<DailyWorkRecord>) request.getAttribute("recordList");
String[] workSiteOptions = (String[]) request.getAttribute("workSiteOptions");
String searchMonth = (String) request.getAttribute("searchMonth");
String selectedWorkSiteName = (String) request.getAttribute("selectedWorkSiteName");
String employeeNameKeyword = (String) request.getAttribute("employeeNameKeyword");
String payAmountTotal = (String) request.getAttribute("payAmountTotal");
String netPayTotal = (String) request.getAttribute("netPayTotal");
%>
<%
request.setAttribute("pageTitle", "일용직 근무 조회");
request.setAttribute("pageSection", "근태관리");
request.setAttribute("pageDescription", "기간·현장·사원 조건으로 일용직 근무기록과 지급 합계를 조회합니다.");
request.setAttribute("activeKey", "daily-work-search");
request.setAttribute("pageCss", "attendance.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>

<form id="searchForm" method="post" action="<%=ctx%>/Diligence/dayWorkerSearchMonth.do">
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
			<label>현장</label>
			<select class="select" name="workSiteName">
				<option value="" <%=selectedWorkSiteName == null || selectedWorkSiteName.isBlank() ? "selected" : ""%>>전체</option>
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
		<div class="field ">
			<label>사원명</label> <input type="text" class="input" name="employeeName"
				value="<%=employeeNameKeyword == null ? "" : employeeNameKeyword%>">
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">조회</button>
		</div>
	</section>
</form>

<section class="card ">
	<div class="card-header">
		<h2 class="section-title">일용직 근무 조회 결과</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table ">
				<thead>
					<tr>
						<th>성명</th>
						<th>근무일</th>
						<th>현장</th>
						<th>일당</th>
						<th>지급률</th>
						<th>지급액</th>
						<th>세금</th>
						<th>실지급액</th>
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
						<td colspan="8">조회된 근무기록이 없습니다.</td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
		</div>
		<div class="tfoot-summary">
			<span>지급액 합계 <%=payAmountTotal%>원</span> <span>실지급액 합계 <%=netPayTotal%>원</span>
		</div>
	</div>
</section>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
