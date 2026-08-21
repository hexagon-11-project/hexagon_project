<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
request.setAttribute("pageTitle", "4大保険控除内訳");
request.setAttribute("pageSection", "給与管理");
request.setAttribute("pageDescription", "帰属年月・回数別に、従業員の国民年金、健康保険、長期療養保険、雇用保険の控除額を照会します。");
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
			<label>帰属年月</label> <input type="month" class="input"
				name="payYearMonth" value="${payYearMonth}">
		</div>
		<div class="field">
			<label>給与回</label> <select class="select" name="paySequence">
				<option value="1" ${paySequence == 1 ? 'selected' : ''}>給与 01回</option>
				<option value="2" ${paySequence == 2 ? 'selected' : ''}>給与 02回</option>
				<option value="3" ${paySequence == 3 ? 'selected' : ''}>給与 03回</option>
				<option value="4" ${paySequence == 4 ? 'selected' : ''}>給与 04回</option>
				<option value="5" ${paySequence == 5 ? 'selected' : ''}>給与 05回</option>
				<option value="6" ${paySequence == 6 ? 'selected' : ''}>給与 06回</option>
				<option value="7" ${paySequence == 7 ? 'selected' : ''}>給与 07回</option>
				<option value="8" ${paySequence == 8 ? 'selected' : ''}>給与 08回</option>
				<option value="9" ${paySequence == 9 ? 'selected' : ''}>給与 09回</option>
				<option value="10" ${paySequence == 10 ? 'selected' : ''}>給与 10回</option>
			</select>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">照会</button>
		</div>
	</section>
</form>

<c:if test="${not empty errorMessage}">
	<div class="info-note" style="color: #c0392b;">${errorMessage}</div>
</c:if>

<section class="card">
	<div class="card-header">
		<h2 class="section-title">4大保険控除内訳</h2>
	</div>
	<div class="card-body">
		<c:if test="${not empty ledger}">
			<dl class="bank-box">
				<dt>精算期間</dt>
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
				<dt>給与支給日</dt>
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
							<th colspan="5" class="four-insure-emp-head">社員情報</th>
						</tr>
						<tr>
							<th>区分</th>
							<th>名前</th>
							<th>入社日</th>
							<th>部署</th>
							<th>職位</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not searched}">
								<tr>
									<td colspan="5" class="center">帰属年月／回数を選択してから照会してください。</td>
								</tr>
							</c:when>
							<c:when test="${empty employeeList}">
								<tr>
									<td colspan="5" class="center">照会された4大保険の控除内訳はありません。</td>
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
								<td colspan="5" class="center">合計</td>
							</tr>
						</tfoot>
					</c:if>
				</table>
			</div>
			<div class="four-insure-scroll">
				<table class="data-table">
					<thead>
						<tr>
							<th colspan="3" class="four-insure-np-head">国民年金</th>
							<th colspan="3" class="four-insure-hi-head">健康保険</th>
							<th colspan="3" class="four-insure-ltc-head">老人長期療養保険</th>
							<th colspan="3" class="four-insure-ei-head">雇用保険</th>
							<th colspan="3" class="four-insure-grand-head">総計</th>
						</tr>
						<tr>
							<th>事業主</th>
							<th>労働者</th>
							<th>合計</th>
							<th>事業主</th>
							<th>労働者</th>
							<th>合計</th>
							<th>事業主</th>
							<th>労働者</th>
							<th>合計</th>
							<th>事業主</th>
							<th>労働者</th>
							<th>合計</th>
							<th>事業主</th>
							<th>労働者</th>
							<th>合計</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not searched}">
								<tr>
									<td colspan="15" class="center">帰属年月／回数を選択してから照会してください。</td>
								</tr>
							</c:when>
							<c:when test="${empty employeeList}">
								<tr>
									<td colspan="15" class="center">照会された4大保険の控除内訳はありません。</td>
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
			<span>照会人数 ${targetCount}人</span> <span>4大社会保険の合計<fmt:formatNumber
					value="${totalAmount}" pattern="#,###" />円
			</span>
		</div>
	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
