<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
request.setAttribute("pageTitle", "급여이체 신청");
request.setAttribute("pageSection", "급여관리");
request.setAttribute("pageDescription", "급여작업의 사원별 계좌와 실지급액을 확인한 후 이체신청 완료내역을 저장합니다.");
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
			<label>귀속연</label>
			<input type="number" class="input" name="payYear" value="${payYear}" min="2000" max="2100" required>
		</div>
		<div class="field">
			<label>귀속월</label>
			<select class="select" name="payMonth">
				<c:forEach var="m" begin="1" end="12">
					<fmt:formatNumber var="mm" value="${m}" pattern="00"/>
					<option value="${mm}" ${payMonth == mm ? 'selected' : ''}>${mm}</option>
				</c:forEach>
			</select>
		</div>
		<div class="field">
			<label>급여차수</label>
			<select class="select" name="paySequence">
				<option value="1" ${paySequence == 1 ? 'selected' : ''}>1차</option>
				<option value="2" ${paySequence == 2 ? 'selected' : ''}>2차</option>
			</select>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">조회</button>
		</div>
	</section>
</form>

<c:if test="${not empty message}">
	<div class="info-note">${message}</div>
</c:if>
<c:if test="${not empty errorMessage}">
	<div class="info-note" style="color:#c0392b;">${errorMessage}</div>
</c:if>

<%--
  [급여이체 신청 폼]
  - 체크된 체크박스(name=payrollEmployeeId)만 POST로 서버에 전달된다.
  - "급여이체 신청" 버튼 → action=apply → Handler → Service → PAYROLL_TRANSFER_REQUEST 저장
  - 체크 안 한 행은 파라미터로 안 넘어가므로 신청 대상에서 제외된다.
--%>
<form action="<%=ctx%>/Payment/paymenttransfer.do" method="post">
	<input type="hidden" name="action" value="apply">
	<input type="hidden" name="payYear" value="${payYear}">
	<input type="hidden" name="payMonth" value="${payMonth}">
	<input type="hidden" name="paySequence" value="${paySequence}">

	<section class="card">
		<div class="card-header">
			<h2 class="section-title">이체 신청 대상</h2>
		</div>
		<div class="card-body">
			<div class="table-wrap">
				<table class="data-table">
					<thead>
						<tr>
							<th>선택</th>
							<th>성명</th>
							<th>부서</th>
							<th>직위</th>
							<th>금융기관</th>
							<th>계좌번호</th>
							<th>실지급액</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not searched}">
								<tr>
									<td colspan="7" style="text-align:center;">귀속연/월/차수를 선택한 뒤 조회하세요.</td>
								</tr>
							</c:when>
							<c:when test="${empty transferList}">
								<tr>
									<td colspan="7" style="text-align:center;">조회된 이체 대상이 없습니다.</td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:forEach var="row" items="${transferList}">
									<tr>
										<td>
											<%-- 체크된 행만 서버로 전달. value=PAYROLL_EMPLOYEE_ID --%>
											<input type="checkbox" name="payrollEmployeeId"
												value="${row.payrollEmployeeId}" checked>
										</td>
										<td>${row.employeeName}</td>
										<td>${empty row.department ? '-' : row.department}</td>
										<td>${empty row.position ? '-' : row.position}</td>
										<td>${empty row.bankName ? '-' : row.bankName}</td>
										<td>${empty row.bankAccount ? '-' : row.bankAccount}</td>
										<td><fmt:formatNumber value="${row.netPayAmount}" pattern="#,###"/></td>
									</tr>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
			</div>
			<div class="tfoot-summary">
				<span>신청 인원 ${targetCount}명</span>
				<span>이체 신청액 <fmt:formatNumber value="${totalAmount}" pattern="#,###"/>원</span>
			</div>
		</div>
	</section>

	<section class="card">
		<div class="card-header">
			<h2 class="section-title">신청정보</h2>
		</div>
		<div class="card-body">
			<dl class="bank-box">
				<dt>출금은행</dt>
				<dd>국민은행</dd>
				<dt>출금계좌</dt>
				<dd>000-****-0000</dd>
				<dt>신청일</dt>
				<dd>2026-08-04</dd>
				<dt>처리방식</dt>
				<dd>이체신청 내역 저장</dd>
			</dl>
			<div class="info-note">실제 은행 이체는 수행하지 않으며, 신청 버튼을 누르면 신청완료 상태로 저장됩니다.</div>
			<div class="button-row">
				<%-- 클릭 시 체크된 행 ID만 전송 → PAYROLL_TRANSFER_REQUEST INSERT/UPDATE --%>
				<button type="submit" class="btn btn-primary" ${empty transferList ? 'disabled' : ''}>급여이체 신청</button>
			</div>
		</div>
	</section>
</form>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
