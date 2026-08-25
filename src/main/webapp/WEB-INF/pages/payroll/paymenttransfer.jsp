<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
request.setAttribute("pageTitle", "給与振込の申し込み");
request.setAttribute("pageSection", "給与管理");
request.setAttribute("pageDescription", "給与処理における社員ごとの口座と実支給額を確認した後、振込申請の完了履歴を保存します。");
request.setAttribute("activeKey", "transfer-request");
request.setAttribute("pageCss", "payroll.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<form action="<%=ctx%>/Payment/paymenttransfer.do" method="get">
	<input type="hidden" name="search" value="Y">
	<section class="filter-bar">
		<div class="field">
			<label>帰属年</label> <input type="number" class="input" name="payYear"
				value="${payYear}" min="2000" max="2100" required>
		</div>
		<div class="field">
			<label>帰属月</label> <select class="select" name="payMonth">
				<c:forEach var="m" begin="1" end="12">
					<fmt:formatNumber var="mm" value="${m}" pattern="00" />
					<option value="${mm}" ${payMonth == mm ? 'selected' : ''}>${mm}</option>
				</c:forEach>
			</select>
		</div>
		<div class="field">
			<label>給与回</label> <select class="select" name="paySequence">
				<option value="1" ${paySequence == 1 ? 'selected' : ''}>給与
					01回</option>
				<option value="2" ${paySequence == 2 ? 'selected' : ''}>給与
					02回</option>
				<option value="3" ${paySequence == 3 ? 'selected' : ''}>給与
					03回</option>
				<option value="4" ${paySequence == 4 ? 'selected' : ''}>給与
					04回</option>
				<option value="5" ${paySequence == 5 ? 'selected' : ''}>給与
					05回</option>
				<option value="6" ${paySequence == 6 ? 'selected' : ''}>給与
					06回</option>
				<option value="7" ${paySequence == 7 ? 'selected' : ''}>給与
					07回</option>
				<option value="8" ${paySequence == 8 ? 'selected' : ''}>給与
					08回</option>
				<option value="9" ${paySequence == 9 ? 'selected' : ''}>給与
					09回</option>
				<option value="10" ${paySequence == 10 ? 'selected' : ''}>給与
					10回</option>
			</select>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">照会</button>
		</div>
	</section>
</form>

<c:if test="${not empty message}">
	<div class="info-note">${message}</div>
</c:if>
<c:if test="${not empty errorMessage}">
	<div class="info-note" style="color: #c0392b;">${errorMessage}</div>
</c:if>

<%--
  [급여이체 신청 폼]
  - 체크된 체크박스(name=payrollEmployeeId)만 POST로 서버에 전달된다.
  - "급여이체 신청" 버튼 → action=apply → Handler → Service → PAYROLL_TRANSFER_REQUEST 저장
  - 체크 안 한 행은 파라미터로 안 넘어가므로 신청 대상에서 제외된다.
--%>
<form action="<%=ctx%>/Payment/paymenttransfer.do" method="post">
	<input type="hidden" name="action" value="apply"> <input
		type="hidden" name="payYear" value="${payYear}"> <input
		type="hidden" name="payMonth" value="${payMonth}"> <input
		type="hidden" name="paySequence" value="${paySequence}">

	<section class="card">
		<div class="card-header">
			<h2 class="section-title">振替申請対象</h2>
		</div>
		<div class="card-body">
			<div class="table-wrap">
				<table class="data-table">
					<thead>
						<tr>
							<th>選択</th>
							<th>名前</th>
							<th>部署</th>
							<th>職位</th>
							<th>金融機関</th>
							<th>口座番号</th>
							<th>実支給額</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not searched}">
								<tr>
									<td colspan="7" style="text-align: center;">帰属年・月・次数を選択してから照会してください。</td>
								</tr>
							</c:when>
							<c:when test="${empty transferList}">
								<tr>
									<td colspan="7" style="text-align: center;">照会された振込対象がありません。</td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:forEach var="row" items="${transferList}">
									<tr>
										<td>
											<%-- 체크된 행만 서버로 전달. value=PAYROLL_EMPLOYEE_ID --%> <input
											type="checkbox" name="payrollEmployeeId"
											value="${row.payrollEmployeeId}" checked>
										</td>
										<td>${row.employeeName}</td>
										<td>${empty row.department ? '-' : row.department}</td>
										<td>${empty row.position ? '-' : row.position}</td>
										<td>${empty row.bankName ? '-' : row.bankName}</td>
										<td>${empty row.bankAccount ? '-' : row.bankAccount}</td>
										<td><fmt:formatNumber value="${row.netPayAmount}"
												pattern="#,###" /></td>
									</tr>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
			</div>
			<div class="tfoot-summary">
				<span>申込人数 ${targetCount}人</span> <span>振替申請額 <fmt:formatNumber
						value="${totalAmount}" pattern="#,###" />円
				</span>
			</div>
		</div>
	</section>

	<section class="card">
		<div class="card-header">
			<h2 class="section-title">申請情報</h2>
		</div>
		<div class="card-body">
			<dl class="bank-box">
				<dt>出金銀行</dt>
				<dd>国民銀行</dd>
				<dt>出金口座</dt>
				<dd>000-****-0000</dd>
				<dt>申請日</dt>
				<dd>2026-08-04</dd>
				<dt>処理方式</dt>
				<dd>振込申請履歴の保存</dd>
			</dl>
			<div class="info-note">実際の銀行振込は行われず、申請ボタンを押すと「申請完了」状態で保存されます。</div>
			<div class="button-row">
				<%-- 클릭 시 체크된 행 ID만 전송 → PAYROLL_TRANSFER_REQUEST INSERT/UPDATE --%>
				<button type="submit" class="btn btn-primary"
					${empty transferList ? 'disabled' : ''}>給与振込の申し込み</button>
			</div>
		</div>
	</section>
</form>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
