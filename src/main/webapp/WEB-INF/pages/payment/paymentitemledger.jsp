<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="payment.model.PaymentItemLedger"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
request.setAttribute("pageTitle", "항목별 대장");
request.setAttribute("pageSection", "급여관리");
request.setAttribute("pageDescription", "조회기간과 지급·공제항목을 선택해 사원별 항목 금액과 합계를 확인합니다.");
request.setAttribute("activeKey", "item-ledger");
request.setAttribute("pageCss", "payroll.css");
request.setAttribute("pageJs", null);

@SuppressWarnings("unchecked")
List<PaymentItemLedger> ledgerItemList = (List<PaymentItemLedger>) request.getAttribute("itemList");
String selectedPayItemKey = (String) request.getAttribute("payItemKey");
if (selectedPayItemKey == null) {
	selectedPayItemKey = "";
}
boolean selectPlaceholder = selectedPayItemKey.isEmpty();
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<form action="<%=ctx%>/Payment/paymentPayItemPart.do" method="get" autocomplete="off"
	onsubmit="return checkLedgerPeriod(this);">
	<input type="hidden" name="search" value="Y">
	<section class="filter-bar">
		<div class="field">
			<label>조회기간</label>
			<div class="range">
				<input type="month" class="input" name="startYearMonth" value="${startYearMonth}">
				<span>~</span>
				<input type="month" class="input" name="endYearMonth" value="${endYearMonth}">
			</div>
		</div>
		<div class="field">
			<label>항목</label>
			<select class="select" name="payItemKey" autocomplete="off">
				<option value="" <%=selectPlaceholder ? "selected=\"selected\"" : ""%>>급여항목 선택</option>
<%
if (ledgerItemList != null) {
	for (PaymentItemLedger payItem : ledgerItemList) {
		String optionValue = payItem.getSelectValue();
		String optionName = payItem.getItemName() == null ? "" : payItem.getItemName();
		boolean optionSelected = !selectPlaceholder && selectedPayItemKey.equals(optionValue);
%>
				<option value="<%=optionValue%>" <%=optionSelected ? "selected=\"selected\"" : ""%>><%=optionName%></option>
<%
	}
}
%>
			</select>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">조회</button>
			<button type="button" class="btn">인쇄</button>
		</div>
	</section>
</form>
<section class="card">
	<div class="card-header">
		<h2 class="section-title">
			<c:choose>
				<c:when test="${not empty selectedItemName}">${selectedItemName} 항목별 대장</c:when>
				<c:otherwise>항목별 대장</c:otherwise>
			</c:choose>
		</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table">
				<thead>
					<tr>
						<th>구분</th>
						<th>성명</th>
						<th>부서</th>
						<th>직위</th>
						<th>귀속연월</th>
						<th>금액</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not searched}">
							<tr>
								<td colspan="6" style="text-align:center;">조회기간과 항목을 선택한 뒤 조회하세요.</td>
							</tr>
						</c:when>
						<c:when test="${empty employeeList}">
							<tr>
								<td colspan="6" style="text-align:center;">조회된 내역이 없습니다.</td>
							</tr>
						</c:when>
						<c:otherwise>
							<c:forEach var="emp" items="${employeeList}">
								<c:forEach var="detail" items="${emp.details}">
									<tr>
										<td>${empty emp.employmentType ? '-' : emp.employmentType}</td>
										<td>${empty emp.employeeName ? '-' : emp.employeeName}</td>
										<td>${empty emp.department ? '-' : emp.department}</td>
										<td>${empty emp.position ? '-' : emp.position}</td>
										<td>${detail.year}-<fmt:formatNumber value="${detail.month}" pattern="00"/></td>
										<td><fmt:formatNumber value="${detail.amount}" pattern="#,###"/></td>
									</tr>
								</c:forEach>
								<tr>
									<td colspan="5" style="text-align:right;">${emp.employeeName} 합계</td>
									<td><fmt:formatNumber value="${emp.totalAmount}" pattern="#,###"/></td>
								</tr>
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
		<div class="tfoot-summary">
			<span>조회 인원 ${targetCount}명</span>
			<span>항목 합계 <fmt:formatNumber value="${totalAmount}" pattern="#,###"/>원</span>
		</div>
	</div>
</section>
<script>
function checkLedgerPeriod(form) {
	var startValue = form.startYearMonth.value;
	var endValue = form.endYearMonth.value;
	if (!startValue || !endValue) {
		return true;
	}
	var startParts = startValue.split('-');
	var endParts = endValue.split('-');
	var startYear = parseInt(startParts[0], 10);
	var startMonth = parseInt(startParts[1], 10);
	var endYear = parseInt(endParts[0], 10);
	var endMonth = parseInt(endParts[1], 10);
	var monthCount = (endYear - startYear) * 12 + (endMonth - startMonth) + 1;
	if (monthCount > 12) {
		alert('조회기간은 12개월을 초과할 수 없습니다.');
		return false;
	}
	return true;
}
</script>
<%
String errorMessage = (String) request.getAttribute("errorMessage");
if (errorMessage != null) {
	String errorMessageJs = errorMessage.replace("\\", "\\\\").replace("'", "\\'").replace("\r", "").replace("\n", "\\n");
%>
<script>alert('<%=errorMessageJs%>');</script>
<%
}
%>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
