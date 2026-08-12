<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
request.setAttribute("pageTitle", "급여이체 결과조회");
request.setAttribute("pageSection", "급여관리");
request.setAttribute("pageDescription", "신청기간 조건으로 저장된 급여이체 신청완료 내역을 조회합니다.");
request.setAttribute("activeKey", "transfer-result");
request.setAttribute("pageCss", "payroll.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<form action="<%=ctx%>/Payment/paymenttransferlist.do" method="get">
	<input type="hidden" name="search" value="Y">
	<section class="filter-bar">
		<div class="field">
			<label>신청기간</label>
			<div class="range">
				<input type="date" class="input" name="startDate" value="${startDate}">
				<span>~</span>
				<input type="date" class="input" name="endDate" value="${endDate}">
			</div>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">조회</button>
		</div>
	</section>
</form>

<section class="card">
	<div class="card-header">
		<h2 class="section-title">급여이체 신청 결과</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table">
				<thead>
					<tr>
						<th>출금은행</th>
						<th>출금계좌</th>
						<th>입금은행</th>
						<th>입금계좌</th>
						<th>예금주</th>
						<th>이체금액</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not searched}">
							<tr>
								<td colspan="6" style="text-align:center;">신청기간을 선택한 뒤 조회하세요.</td>
							</tr>
						</c:when>
						<c:when test="${empty transferRequestList}">
							<tr>
								<td colspan="6" style="text-align:center;">조회된 이체신청 결과가 없습니다.</td>
							</tr>
						</c:when>
						<c:otherwise>
							<c:forEach var="row" items="${transferRequestList}">
								<tr>
									<td>${empty row.companyBankName ? '-' : row.companyBankName}</td>
									<td>${empty row.companyBankAccount ? '-' : row.companyBankAccount}</td>
									<td>${empty row.bankName ? '-' : row.bankName}</td>
									<td>${empty row.bankAccount ? '-' : row.bankAccount}</td>
									<td>${empty row.employeeName ? '-' : row.employeeName}</td>
									<td><fmt:formatNumber value="${row.transferAmount}" pattern="#,###"/></td>
								</tr>
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
		<div class="tfoot-summary">
			<span>조회 인원 ${targetCount}명</span>
			<span>이체금액 합계 <fmt:formatNumber value="${totalAmount}" pattern="#,###"/>원</span>
		</div>
	</div>
</section>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
