<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
request.setAttribute("pageTitle", "給与振込結果照会");
request.setAttribute("pageSection", "給与管理");
request.setAttribute("pageDescription", "申請期間を条件として、保存された給与振込申請完了の履歴を照会します。");
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
			<label>申請期間</label>
			<div class="range">
				<input type="date" class="input" name="startDate"
					value="${startDate}"> <span>~</span> <input type="date"
					class="input" name="endDate" value="${endDate}">
			</div>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">照会</button>
		</div>
	</section>
</form>

<section class="card">
	<div class="card-header">
		<h2 class="section-title">給与振込の申請結果</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table">
				<thead>
					<tr>
						<th>出金銀行</th>
						<th>出金口座</th>
						<th>振込先銀行</th>
						<th>振込先口座</th>
						<th>口座名義人</th>
						<th>振込金額</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not searched}">
							<tr>
								<td colspan="6" style="text-align: center;">申請期間を選択してから照会してください。</td>
							</tr>
						</c:when>
						<c:when test="${empty transferRequestList}">
							<tr>
								<td colspan="6" style="text-align: center;">照会された振替申請結果はありません。</td>
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
									<td><fmt:formatNumber value="${row.transferAmount}"
											pattern="#,###" /></td>
								</tr>
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
		<div class="tfoot-summary">
			<span>照会人数 ${targetCount}人</span> <span>振込金額合計 <fmt:formatNumber
					value="${totalAmount}" pattern="#,###" />円
			</span>
		</div>
	</div>
</section>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
