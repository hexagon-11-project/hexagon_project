<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<% request.setAttribute("activeKey", "employee-pay-history"); %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>사원별 급여내역 | HEXAGON PAY</title>
<%@ include file="../../jspf/head.jspf"%>
<style>
body { min-width: 1200px; background: #fff; }
.content-area { background: #fff; }

.ppl-filter-bar { background: #eef0f2; border: 1px solid #ddd; border-radius: 4px; padding: 12px 15px; display: flex; align-items: center; gap: 12px; margin-bottom: 15px; font-size: 13px; flex-wrap: wrap; }
.ppl-filter-bar select { padding: 5px 6px; border: 1px solid #ccc; border-radius: 3px; font-size: 12px; background: #fff; }
.ppl-filter-bar input[type=text] { padding: 5px 8px; border: 1px solid #ccc; border-radius: 3px; font-size: 12px; width: 160px; }
.ppl-icon-btn { border: 1px solid #ccc; background: #fff; border-radius: 3px; padding: 5px 9px; cursor: pointer; font-size: 12px; }
.ppl-query-btn { background: #337ab7; color: #fff; border: none; padding: 6px 16px; border-radius: 3px; font-size: 12px; font-weight: bold; cursor: pointer; }

.ppl-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.ppl-table th, .ppl-table td { border: 1px solid #ddd; padding: 9px 6px; text-align: center; }
.ppl-table thead tr.ppl-group-row th { background: #eaf2fb; color: #204d74; font-weight: bold; }
.ppl-table thead tr.ppl-group-row th.ppl-group-insurance { background: #fdf1ec; color: #a94442; }
.ppl-table thead tr.ppl-col-row th { background: #f4f6f9; }
.ppl-table thead tr.ppl-col-row th.ppl-month-col { color: #337ab7; }
.ppl-table tbody td.ppl-num { text-align: right; padding-right: 12px; }
.ppl-table tfoot td { background: #fcf8e3; font-weight: bold; }

.ppl-pagination { display: flex; justify-content: center; align-items: center; gap: 15px; margin-top: 15px; font-size: 13px; }
.ppl-pagination a { color: #337ab7; text-decoration: none; }
.ppl-pagination .ppl-page-num { display: inline-block; min-width: 22px; text-align: center; padding: 2px 6px; border: 1px solid #337ab7; border-radius: 3px; color: #337ab7; font-weight: bold; }

.ppl-back-wrap { text-align: center; margin-top: 20px; }
.ppl-back-btn { display: inline-block; background: #d9d9d9; color: #333; font-size: 14px; font-weight: bold;
	padding: 10px 30px; border-radius: 20px; text-decoration: none; border: none; cursor: pointer; }
.ppl-back-btn:hover { background: #c9c9c9; }
</style>
</head>
<body>
	<%@ include file="../../jspf/app-start.jspf"%>
	<%@ include file="../../jspf/sidebar.jspf"%>

	<main class="content-area">
		<div class="page-header" style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
			<img src="https://img.payzon.co.kr/_commonImg/pay_tit_img.gif" width="50" height="45" alt="사원별 급여내역">
			<div>
				<h2 style="margin: 0; font-size: 20px; font-weight: bold;">사원별 급여내역</h2>
				<p class="text-muted" style="margin: 3px 0 0 0; font-size: 12px; color: #666;">
					사원의 급여내역을 조회하여 한 눈에 확인할 수 있도록 제공되는 메뉴입니다. 조회기간 및 사원을 확인하세요!
				</p>
			</div>
		</div>

		<form id="pplFilterForm" action="${pageContext.request.contextPath}/Payment/paymentPayList.do" method="GET" onsubmit="return pplCheckPeriod();">
			<div class="ppl-filter-bar">
				<strong>* 기간선택</strong>
				<select name="startYear">
					<c:forEach var="year" begin="2005" end="2031">
						<option value="${year}" <c:if test="${startYear eq year}">selected</c:if>>${year} 년</option>
					</c:forEach>
				</select>
				<select name="startMonth">
					<c:forEach var="month" begin="1" end="12">
						<fmt:formatNumber value="${month}" pattern="00" var="pplStartMonthFmt" />
						<option value="${pplStartMonthFmt}" <c:if test="${startMonth eq pplStartMonthFmt}">selected</c:if>>${pplStartMonthFmt} 월</option>
					</c:forEach>
				</select>
				<span>~</span>
				<select name="endYear">
					<c:forEach var="year" begin="2005" end="2031">
						<option value="${year}" <c:if test="${endYear eq year}">selected</c:if>>${year} 년</option>
					</c:forEach>
				</select>
				<select name="endMonth">
					<c:forEach var="month" begin="1" end="12">
						<fmt:formatNumber value="${month}" pattern="00" var="pplEndMonthFmt" />
						<option value="${pplEndMonthFmt}" <c:if test="${endMonth eq pplEndMonthFmt}">selected</c:if>>${pplEndMonthFmt} 월</option>
					</c:forEach>
				</select>

				<strong>* 사원선택</strong>
				<input type="text" id="pplEmployeeName" name="employeeName" placeholder="사원명 입력" value="${employeeName}" readonly style="cursor: pointer; background: #fff;" onclick="pplOpenEmployeeModal()">
				<button type="button" class="ppl-icon-btn" onclick="pplOpenEmployeeModal()">🔍</button>

				<button type="submit" class="ppl-query-btn">📋 급여내역 조회</button>
			</div>
		</form>

		<table class="ppl-table">
			<thead>
				<tr class="ppl-group-row">
					<th colspan="5">월별 급여내역</th>
					<th colspan="6" class="ppl-group-insurance">4대보험 및 갑근세 내역</th>
				</tr>
				<tr class="ppl-col-row">
					<th class="ppl-month-col">급여월(차수)</th>
					<th>보수월액</th>
					<th>지급합계</th>
					<th>공제합계</th>
					<th>실지급액</th>
					<th>국민연금</th>
					<th>건강보험</th>
					<th>노인장기요양보험</th>
					<th>고용보험</th>
					<th>소득세</th>
					<th>주민세</th>
				</tr>
			</thead>
			<tbody id="pplBody">
				<c:forEach var="row" items="${result.rows}">
					<fmt:formatNumber value="${row.paySequence}" pattern="00" var="pplSeqPadded" />
					<tr>
						<td>${fn:substring(row.payYearMonth, 0, 4)}.${fn:substring(row.payYearMonth, 4, 6)}(${pplSeqPadded})</td>
						<td class="ppl-num"><fmt:formatNumber value="${row.totalPayAmount}" pattern="#,###" /></td>
						<td class="ppl-num"><fmt:formatNumber value="${row.totalPayAmount}" pattern="#,###" /></td>
						<td class="ppl-num"><fmt:formatNumber value="${row.totalDeductionAmount}" pattern="#,###" /></td>
						<td class="ppl-num"><fmt:formatNumber value="${row.netPayAmount}" pattern="#,###" /></td>
						<td class="ppl-num"><fmt:formatNumber value="${row.nationalPension}" pattern="#,###" /></td>
						<td class="ppl-num"><fmt:formatNumber value="${row.healthInsurance}" pattern="#,###" /></td>
						<td class="ppl-num"><fmt:formatNumber value="${row.longTermCare}" pattern="#,###" /></td>
						<td class="ppl-num"><fmt:formatNumber value="${row.employmentInsurance}" pattern="#,###" /></td>
						<td class="ppl-num"><fmt:formatNumber value="${row.incomeTax}" pattern="#,###" /></td>
						<td class="ppl-num"><fmt:formatNumber value="${row.localIncomeTax}" pattern="#,###" /></td>
					</tr>
				</c:forEach>
				<c:if test="${empty result.rows}">
					<tr>
						<td colspan="11" style="padding: 25px; color: #666;">
							<c:choose>
								<c:when test="${empty employeeName}">사원을 선택하고 조회해주세요.</c:when>
								<c:otherwise>조회된 급여내역이 없습니다.</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td>합계</td>
					<td class="ppl-num"><fmt:formatNumber value="${result.totals.totalPayAmount}" pattern="#,###" /></td>
					<td class="ppl-num"><fmt:formatNumber value="${result.totals.totalPayAmount}" pattern="#,###" /></td>
					<td class="ppl-num"><fmt:formatNumber value="${result.totals.totalDeductionAmount}" pattern="#,###" /></td>
					<td class="ppl-num"><fmt:formatNumber value="${result.totals.netPayAmount}" pattern="#,###" /></td>
					<td class="ppl-num"><fmt:formatNumber value="${result.totals.nationalPension}" pattern="#,###" /></td>
					<td class="ppl-num"><fmt:formatNumber value="${result.totals.healthInsurance}" pattern="#,###" /></td>
					<td class="ppl-num"><fmt:formatNumber value="${result.totals.longTermCare}" pattern="#,###" /></td>
					<td class="ppl-num"><fmt:formatNumber value="${result.totals.employmentInsurance}" pattern="#,###" /></td>
					<td class="ppl-num"><fmt:formatNumber value="${result.totals.incomeTax}" pattern="#,###" /></td>
					<td class="ppl-num"><fmt:formatNumber value="${result.totals.localIncomeTax}" pattern="#,###" /></td>
				</tr>
			</tfoot>
		</table>

		<div class="ppl-pagination">
			<a href="javascript:void(0);">‹ 이전페이지</a>
			<span class="ppl-page-num">1</span>
			<a href="javascript:void(0);">다음페이지 ›</a>
		</div>

		<div class="ppl-back-wrap">
			<a class="ppl-back-btn" href="${pageContext.request.contextPath}/Payment/paymentRegisterList.do">급여대장 목록</a>
		</div>
	</main>

	<%@ include file="../../jspf/app-end.jspf"%>

	<script>
	function pplOpenEmployeeModal() {
	    var contextPath = "${pageContext.request.contextPath}";
	    var popupUrl = contextPath + "/Payment/paymentPayListEmployeeModal.do";
	    window.open(popupUrl, "PplEmployeeSelectModal", "width=820,height=650,left=250,top=100,scrollbars=yes");
	}

	// 사원선택 팝업에서 호출 (window.opener.pplSetSelectedEmployee(...))
	function pplSetSelectedEmployee(employeeName) {
	    document.getElementById("pplEmployeeName").value = employeeName;
	    // form.submit()으로 직접 제출하면 onsubmit 검증(pplCheckPeriod)이 건너뛰어지므로 여기서도 먼저 확인한다
	    if (pplCheckPeriod()) {
	        document.getElementById("pplFilterForm").submit();
	    }
	}

	function pplCheckPeriod() {
	    var form = document.getElementById("pplFilterForm");
	    var startYear = parseInt(form.startYear.value, 10);
	    var startMonth = parseInt(form.startMonth.value, 10);
	    var endYear = parseInt(form.endYear.value, 10);
	    var endMonth = parseInt(form.endMonth.value, 10);

	    var monthCount = (endYear - startYear) * 12 + (endMonth - startMonth) + 1;
	    if (monthCount > 12) {
	        alert("검색 기간은 최대 12개월[1년] 입니다.");
	        return false;
	    }
	    return true;
	}
	</script>
</body>
</html>
