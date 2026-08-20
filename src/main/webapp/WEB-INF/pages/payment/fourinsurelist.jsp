<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
request.setAttribute("pageTitle", "4대보험 공제내역");
request.setAttribute("pageSection", "급여관리");
request.setAttribute("pageDescription", "귀속연월·차수별 사원의 국민연금, 건강보험, 장기요양보험, 고용보험 공제액을 조회합니다.");
request.setAttribute("activeKey", "insurance-deduction");
request.setAttribute("pageCss", "payroll.css?v=4");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<form action="<%=ctx%>/Payment/fourinsureList.do" method="get">
	<input type="hidden" name="search" value="Y">
	<section class="filter-bar">
		<div class="field">
			<label>귀속연월</label> <input type="month" class="input"
				name="payYearMonth" value="${payYearMonth}">
		</div>
		<div class="field">
			<label>급여차수</label> <select class="select" name="paySequence">
				<option value="1" ${paySequence == 1 ? 'selected' : ''}>급여-01차</option>
				<option value="2" ${paySequence == 2 ? 'selected' : ''}>급여-02차</option>
				<option value="3" ${paySequence == 3 ? 'selected' : ''}>급여-03차</option>
				<option value="4" ${paySequence == 4 ? 'selected' : ''}>급여-04차</option>
				<option value="5" ${paySequence == 5 ? 'selected' : ''}>급여-05차</option>
				<option value="6" ${paySequence == 6 ? 'selected' : ''}>급여-06차</option>
				<option value="7" ${paySequence == 7 ? 'selected' : ''}>급여-07차</option>
				<option value="8" ${paySequence == 8 ? 'selected' : ''}>급여-08차</option>
				<option value="9" ${paySequence == 9 ? 'selected' : ''}>급여-09차</option>
				<option value="10" ${paySequence == 10 ? 'selected' : ''}>급여-10차</option>
			</select>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">조회</button>
		</div>
	</section>
</form>

<c:if test="${not empty errorMessage}">
	<div class="info-note" style="color: #c0392b;">${errorMessage}</div>
</c:if>

<section class="card">
	<div class="card-header">
		<h2 class="section-title">4대보험 공제내역</h2>
	</div>
	<div class="card-body">
		<c:if test="${not empty ledger}">
			<dl class="bank-box">
				<dt>정산기간</dt>
				<dd>
					<c:choose>
						<c:when
							test="${empty ledger.settlementStartDate or empty ledger.settlementEndDate}">-</c:when>
						<c:otherwise>
							<fmt:formatDate value="${ledger.settlementStartDate}"
								pattern="yyyy-MM-dd" />
							~
							<fmt:formatDate value="${ledger.settlementEndDate}"
								pattern="yyyy-MM-dd" />
						</c:otherwise>
					</c:choose>
				</dd>
				<dt>급여지급일</dt>
				<dd>
					<c:choose>
						<c:when test="${empty ledger.paymentDate}">-</c:when>
						<c:otherwise>
							<fmt:formatDate value="${ledger.paymentDate}"
								pattern="yyyy-MM-dd" />
						</c:otherwise>
					</c:choose>
				</dd>
			</dl>
		</c:if>
		<div class="four-insure-tables">
			<div class="four-insure-fixed">
				<table class="data-table">
					<thead>
						<tr>
							<th colspan="5" class="four-insure-emp-head">사원정보</th>
						</tr>
						<tr>
							<th>구분</th>
							<th>성명</th>
							<th>입사일</th>
							<th>부서</th>
							<th>직위</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not searched}">
								<tr>
									<td colspan="5" class="center">귀속연월/차수를 선택한 뒤 조회하세요.</td>
								</tr>
							</c:when>
							<c:when test="${empty employeeList}">
								<tr>
									<td colspan="5" class="center">조회된 4대보험 공제내역이 없습니다.</td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:forEach var="row" items="${employeeList}">
									<tr>
										<td class="center">${empty row.employmentType ? '-' : row.employmentType}</td>
										<td class="center">${empty row.employeeName ? '-' : row.employeeName}</td>
										<td class="center"><c:choose>
												<c:when test="${empty row.hireDate}">-</c:when>
												<c:otherwise>
													<fmt:formatDate value="${row.hireDate}"
														pattern="yyyy-MM-dd" />
												</c:otherwise>
											</c:choose></td>
										<td class="center">${empty row.department ? '-' : row.department}</td>
										<td class="center">${empty row.position ? '-' : row.position}</td>
									</tr>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</tbody>
					<c:if test="${searched and not empty employeeList}">
						<tfoot>
							<tr>
								<td colspan="5" class="center">합계</td>
							</tr>
						</tfoot>
					</c:if>
				</table>
			</div>
			<div class="four-insure-scroll">
				<table class="data-table">
					<thead>
						<tr>
							<th colspan="3" class="four-insure-np-head">국민연금</th>
							<th colspan="3" class="four-insure-hi-head">건강보험</th>
							<th colspan="3" class="four-insure-ltc-head">노인장기요양보험</th>
							<th colspan="3" class="four-insure-ei-head">고용보험</th>
							<th colspan="3" class="four-insure-grand-head">총 합계</th>
						</tr>
						<tr>
							<th>사업주</th>
							<th>근로자</th>
							<th>합계</th>
							<th>사업주</th>
							<th>근로자</th>
							<th>합계</th>
							<th>사업주</th>
							<th>근로자</th>
							<th>합계</th>
							<th>사업주</th>
							<th>근로자</th>
							<th>합계</th>
							<th>사업주</th>
							<th>근로자</th>
							<th>합계</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not searched}">
								<tr>
									<td colspan="15" class="center">귀속연월/차수를 선택한 뒤 조회하세요.</td>
								</tr>
							</c:when>
							<c:when test="${empty employeeList}">
								<tr>
									<td colspan="15" class="center">조회된 4대보험 공제내역이 없습니다.</td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:forEach var="row" items="${employeeList}">
									<tr>
										<td class="amount"><fmt:formatNumber
												value="${row.nationalPension}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.nationalPension}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.nationalPension * 2}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.healthInsurance}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.healthInsurance}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.healthInsurance * 2}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.longTermCare}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.longTermCare}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.longTermCare * 2}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.employmentInsurance}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.employmentInsurance}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.employmentInsurance * 2}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.insuranceTotal}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.insuranceTotal}" pattern="#,###" /></td>
										<td class="amount"><fmt:formatNumber
												value="${row.grandTotal}" pattern="#,###" /></td>
									</tr>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</tbody>
					<c:if test="${searched and not empty employeeList}">
						<tfoot>
							<tr>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.nationalPension}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.nationalPension}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.nationalPension * 2}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.healthInsurance}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.healthInsurance}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.healthInsurance * 2}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.longTermCare}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.longTermCare}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.longTermCare * 2}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.employmentInsurance}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.employmentInsurance}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.employmentInsurance * 2}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.insuranceTotal}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.insuranceTotal}" pattern="#,###" /></td>
								<td class="amount"><fmt:formatNumber
										value="${columnTotals.grandTotal}" pattern="#,###" /></td>
							</tr>
						</tfoot>
					</c:if>
				</table>
			</div>
		</div>
		<div class="tfoot-summary">
			<span>조회 인원 ${targetCount}명</span> <span>4대보험 합계 <fmt:formatNumber
					value="${totalAmount}" pattern="#,###" />원
			</span>
		</div>
	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
