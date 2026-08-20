<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<% request.setAttribute("activeKey", "pay-slip"); %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>급여명세서 | HEXAGON PAY</title>
<%@ include file="../../jspf/head.jspf"%>
<style>
body { min-width: 1200px; background: #fff; }
.content-area { background: #fff; }

.psl-filter-bar { background: #d9534f; padding: 10px 15px; border-radius: 4px; display: flex; align-items: center; gap: 18px; margin-bottom: 12px; color: #fff; font-size: 13px; flex-wrap: wrap; }
.psl-filter-bar select { display: inline-block; padding: 3px 5px; font-size: 12px; border: none; border-radius: 3px; background: #fff; color: #333; }
.psl-filter-bar .psl-static { display: inline-block; background: #fff; color: #333; padding: 3px 10px; border-radius: 3px; min-width: 90px; text-align: center; }

.psl-toolbar { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.psl-toolbar .psl-search { display: flex; }
.psl-toolbar .psl-search input { width: 180px; padding: 5px 8px; border: 1px solid #ccc; border-radius: 3px 0 0 3px; font-size: 12px; }
.psl-toolbar .psl-search button { border: 1px solid #ccc; border-left: none; background: #f4f6f9; border-radius: 0 3px 3px 0; padding: 0 10px; cursor: pointer; }
.psl-btn { border: 1px solid #ccc; background: #fff; padding: 5px 12px; font-size: 12px; border-radius: 3px; cursor: pointer; }

.psl-layout { display: flex; gap: 15px; align-items: flex-start; }
.psl-list { flex: 1; background: #fff; border: 1px solid #ddd; }
.psl-list table { width: 100%; border-collapse: collapse; font-size: 12px; }
.psl-list th { background: #f4f6f9; color: #337ab7; border: 1px solid #ddd; padding: 8px 6px; text-align: center; }
.psl-list td { border: 1px solid #ddd; padding: 7px 6px; text-align: center; }
.psl-list tbody tr { cursor: pointer; }
.psl-list tbody tr:hover { background: #f8fafc; }
.psl-list tbody tr.selected { background: #e8f1fb; }
.psl-list .psl-amt { text-align: right; color: #337ab7; font-weight: bold; }

.psl-preview { flex: 1.15; background: #f4f6f9; border: 1px solid #ddd; padding: 15px; }
.psl-sheet { background: #fff; border: 1px solid #999; padding: 20px; }
.psl-sheet-head { display: flex; align-items: center; justify-content: center; position: relative; margin-bottom: 15px; }
.psl-logo { position: absolute; left: 0; top: 0; display: flex; align-items: center; gap: 4px; }
.psl-logo-mark { background: #d9534f; color: #fff; font-weight: bold; font-size: 13px; padding: 8px 10px; border-radius: 3px; }
.psl-logo-close { border: none; background: none; color: #999; cursor: pointer; font-size: 12px; }
.psl-logo-placeholder { display: none; border: 1px dashed #ccc; color: #999; font-size: 11px; padding: 8px 10px; border-radius: 3px; cursor: pointer; }
.psl-logo-placeholder:hover { background: #f4f6f9; }
.psl-sheet-title { font-size: 20px; font-weight: bold; }

.psl-info-table, .psl-detail-table, .psl-net-table { width: 100%; border-collapse: collapse; font-size: 13px; margin-bottom: 10px; }
.psl-info-table th, .psl-info-table td { border: 1px solid #ccc; padding: 8px; }
.psl-info-table th { background: #f4f6f9; width: 12%; text-align: center; }
.psl-info-table td { width: 38%; }

.psl-section-header { background: #f4f6f9; border: 1px solid #ccc; border-bottom: none; padding: 6px 10px; font-weight: bold; font-size: 12px; }
.psl-detail-table { margin-top: 0; }
.psl-detail-table th, .psl-detail-table td { border: 1px solid #ccc; padding: 6px 8px; font-size: 12px; }
.psl-detail-table th { background: #f4f6f9; text-align: center; }
.psl-detail-table .psl-blank-row td { height: 22px; text-align: center; color: #999; }
.psl-detail-table td.psl-amount { text-align: right; }
.psl-detail-table .psl-sum-row td { background: #fcf8e3; font-weight: bold; text-align: center; }

.psl-net-table td { border: 1px solid #ccc; padding: 10px; }
.psl-net-table .psl-net-label { width: 12%; background: #fcf8e3; font-weight: bold; text-align: center; }
.psl-net-table .psl-net-value { background: #fcf8e3; font-weight: bold; text-align: center; font-size: 13px; }

.psl-thanks { text-align: center; color: #337ab7; font-size: 13px; margin: 15px 0; }
.psl-sign { display: flex; align-items: center; justify-content: center; gap: 20px; }
.psl-sign label { font-size: 12px; display: flex; align-items: center; gap: 4px; cursor: pointer; }
.psl-sign-name { font-weight: bold; text-align: center; font-size: 13px; line-height: 1.5; }
.psl-seal { position: relative; display: inline-block; }
.psl-seal-mark { width: 54px; height: 54px; border: 2px solid #d9534f; border-radius: 4px; color: #d9534f; font-weight: bold; font-size: 10px; display: flex; align-items: center; justify-content: center; text-align: center; line-height: 1.2; }
.psl-seal-close { position: absolute; top: -6px; right: -6px; background: #fff; border: 1px solid #ccc; border-radius: 50%; width: 16px; height: 16px; font-size: 10px; line-height: 1; cursor: pointer; padding: 0; }
.psl-seal-placeholder { display: none; width: 54px; height: 54px; border: 1px dashed #ccc; border-radius: 4px; color: #999; font-size: 10px; align-items: center; justify-content: center; text-align: center; line-height: 1.2; cursor: pointer; }
.psl-seal-placeholder:hover { background: #f4f6f9; }
</style>
</head>
<body>
	<%@ include file="../../jspf/app-start.jspf"%>
	<%@ include file="../../jspf/sidebar.jspf"%>

	<main class="content-area">
		<div class="page-header" style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
			<img src="https://img.payzon.co.kr/_commonImg/pay_tit_img.gif" width="50" height="45" alt="급여명세서">
			<div>
				<h2 style="margin: 0; font-size: 20px; font-weight: bold;">급여명세서</h2>
				<p class="text-muted" style="margin: 3px 0 0 0; font-size: 12px; color: #666;">
					사원을 선택하면 해당사원의 급여명세서가 자동으로 작성됩니다.
				</p>
			</div>
		</div>

		<form id="pslFilterForm" action="${pageContext.request.contextPath}/Payment/paymentPayslip.do" method="GET">
			<div class="psl-filter-bar">
				<div>
					<strong>* 귀속연월</strong>&nbsp;
					<select name="payYear" onchange="document.getElementById('pslFilterForm').submit()">
						<c:forEach var="year" begin="2020" end="2030">
							<option value="${year}" <c:if test="${payYear eq year}">selected</c:if>>${year} 년</option>
						</c:forEach>
					</select>
					<select name="payMonth" onchange="document.getElementById('pslFilterForm').submit()">
						<c:forEach var="month" begin="1" end="12">
							<fmt:formatNumber value="${month}" pattern="00" var="formattedMonth" />
							<option value="${formattedMonth}" <c:if test="${payMonth eq formattedMonth}">selected</c:if>>${formattedMonth} 월</option>
						</c:forEach>
					</select>
				</div>
				<div>
					<strong>* 급여차수</strong>&nbsp;
					<select name="paySequence" onchange="document.getElementById('pslFilterForm').submit()">
						<c:forEach var="seq" begin="1" end="10">
							<fmt:formatNumber value="${seq}" pattern="00" var="formattedSeq" />
							<option value="${seq}" <c:if test="${paySequence eq seq}">selected</c:if>>급여-${formattedSeq}</option>
						</c:forEach>
					</select>
				</div>
				<div>
					<strong>* 정산기간</strong>&nbsp;
					<span class="psl-static">${result.settlementStartDate}</span> ~
					<span class="psl-static">${result.settlementEndDate}</span>
				</div>
				<div>
					<strong>* 급여지급일</strong>&nbsp;
					<span class="psl-static">${result.paymentDate}</span>
				</div>
			</div>
		</form>

		<div class="psl-toolbar">
			<div class="psl-search">
				<input type="text" id="pslSearchInput" placeholder="검색어 입력" onkeydown="if(event.key==='Enter'){pslSearch();}">
				<button type="button" onclick="pslSearch()">🔍</button>
			</div>
			<button type="button" class="psl-btn" onclick="pslShowAll()">전체보기</button>
		</div>

		<div class="psl-layout">
			<!-- 좌측: 사원 목록 -->
			<div class="psl-list">
				<table>
					<thead>
						<tr>
							<th style="width: 34px;"><input type="checkbox" id="pslCheckAll" onclick="pslToggleAll(this)"></th>
							<th>구분</th>
							<th>성명</th>
							<th>실지급액</th>
						</tr>
					</thead>
					<tbody id="pslEmployeeBody">
						<c:forEach var="emp" items="${result.employeeList}">
							<tr data-id="${emp.payrollEmployeeId}" data-name="${emp.employeeName}" onclick="pslSelectRow(this)">
								<td onclick="event.stopPropagation();"><input type="checkbox"></td>
								<td>${emp.employmentType}</td><td>${emp.employeeName}</td>
								<td class="psl-amt"><fmt:formatNumber value="${emp.netPayAmount}" pattern="#,###" /></td>
							</tr>
						</c:forEach>
						<c:if test="${empty result.employeeList}">
							<tr>
								<td colspan="4" style="padding: 25px; color: #666;">등록된 급여 데이터가 없습니다.</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</div>

			<!-- 우측: 급여명세서 미리보기 -->
			<div class="psl-preview">
				<div class="psl-sheet">
					<div class="psl-sheet-head">
						<div class="psl-logo" id="pslLogoBlock">
							<span class="psl-logo-mark" id="pslLogoMark">HEXAGON</span>
							<button type="button" class="psl-logo-close" id="pslLogoCloseBtn" onclick="pslToggleLogo(false)">✕</button>
							<span class="psl-logo-placeholder" id="pslLogoPlaceholder" onclick="pslToggleLogo(true)">로고 표시</span>
						</div>
						<div class="psl-sheet-title">${payYear}년 ${payMonth}월 급여명세서</div>
					</div>

					<table class="psl-info-table">
						<tr>
							<th>성명</th><td id="pslInfoName">&nbsp;</td>
							<th>생년월일</th><td id="pslInfoResident">&nbsp;</td>
						</tr>
						<tr>
							<th>부서</th><td id="pslInfoDept">&nbsp;</td>
							<th>직급</th><td id="pslInfoPosition">&nbsp;</td>
						</tr>
						<tr>
							<th>입사일</th><td id="pslInfoHireDate">&nbsp;</td>
							<th>급여지급일</th><td>${result.paymentDate}</td>
						</tr>
					</table>

					<div class="psl-section-header">지급 항목</div>
					<table class="psl-detail-table">
						<thead>
							<tr><th>항목명</th><th>금액</th><th>산출식 또는 산출방법</th></tr>
						</thead>
						<tbody id="pslPayItemsBody">
							<tr class="psl-blank-row"><td colspan="3">사원을 선택해주세요.</td></tr>
						</tbody>
						<tfoot>
							<tr class="psl-sum-row"><td colspan="2">합계</td><td id="pslPaySum">&nbsp;</td></tr>
						</tfoot>
					</table>

					<div class="psl-section-header">공제 항목</div>
					<table class="psl-detail-table">
						<thead>
							<tr><th>항목명</th><th>금액</th><th>산출식 또는 산출방법</th></tr>
						</thead>
						<tbody id="pslDedItemsBody">
							<tr class="psl-blank-row"><td colspan="3">사원을 선택해주세요.</td></tr>
						</tbody>
						<tfoot>
							<tr class="psl-sum-row"><td colspan="2">합계</td><td id="pslDedSum">&nbsp;</td></tr>
						</tfoot>
					</table>

					<table class="psl-net-table">
						<tr>
							<td class="psl-net-label">실수령액</td>
							<td class="psl-net-value" id="pslNetValue">&nbsp;</td>
						</tr>
					</table>

					<p class="psl-thanks">귀하의 노고에 감사드리며, 수고 많으셨습니다.</p>

					<div class="psl-sign">
						<label>
							<input type="checkbox" id="pslShowSign" checked onchange="pslToggleSign(this)"> 대표자 표기
						</label>
						<div class="psl-sign-name">HEXAGON PAY<br><span id="pslSignTitle">대표이사</span></div>
						<div class="psl-seal" id="pslSealBlock">
							<div class="psl-seal-mark" id="pslSealMark">직인</div>
							<button type="button" class="psl-seal-close" id="pslSealCloseBtn" onclick="pslToggleSeal(false)">✕</button>
							<div class="psl-seal-placeholder" id="pslSealPlaceholder" onclick="pslToggleSeal(true)">직인 표시</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</main>

	<%@ include file="../../jspf/app-end.jspf"%>

	<script>
	// 컨트롤러가 조회한 사원별 지급/공제 내역 (payrollEmployeeId를 key로 하는 JSON)
	var pslEmployeeDetail = <%= request.getAttribute("employeeDetailJson") %>;

	function pslRenderItems(tbodyId, items) {
	    var tbody = document.getElementById(tbodyId);
	    tbody.innerHTML = "";
	    if (!items || items.length === 0) {
	        var emptyRow = document.createElement("tr");
	        emptyRow.className = "psl-blank-row";
	        emptyRow.innerHTML = "<td colspan=\"3\">내역이 없습니다.</td>";
	        tbody.appendChild(emptyRow);
	        return;
	    }
	    items.forEach(function (item) {
	        var tr = document.createElement("tr");
	        var nameTd = document.createElement("td");
	        nameTd.textContent = item.name;
	        var amtTd = document.createElement("td");
	        amtTd.className = "psl-amount";
	        amtTd.textContent = Number(item.amount || 0).toLocaleString("ko-KR");
	        var calcTd = document.createElement("td");
	        calcTd.textContent = item.calc || "";
	        tr.appendChild(nameTd);
	        tr.appendChild(amtTd);
	        tr.appendChild(calcTd);
	        tbody.appendChild(tr);
	    });
	}

	function pslSelectRow(tr) {
	    document.querySelectorAll("#pslEmployeeBody tr").forEach(function (r) { r.classList.remove("selected"); });
	    tr.classList.add("selected");

	    var data = pslEmployeeDetail[tr.getAttribute("data-id")];
	    if (!data) { return; }

	    document.getElementById("pslInfoName").textContent = data.name || "";
	    document.getElementById("pslInfoResident").textContent = data.residentRegNo || "";
	    document.getElementById("pslInfoDept").textContent = data.department || "";
	    document.getElementById("pslInfoPosition").textContent = data.position || "";
	    document.getElementById("pslInfoHireDate").textContent = data.hireDate || "";

	    pslRenderItems("pslPayItemsBody", data.payItems);
	    document.getElementById("pslPaySum").textContent = Number(data.totalPay || 0).toLocaleString("ko-KR");

	    pslRenderItems("pslDedItemsBody", data.dedItems);
	    document.getElementById("pslDedSum").textContent = Number(data.totalDed || 0).toLocaleString("ko-KR");

	    document.getElementById("pslNetValue").textContent = Number(data.netPay || 0).toLocaleString("ko-KR");
	}

	function pslToggleAll(checkbox) {
	    document.querySelectorAll("#pslEmployeeBody input[type=checkbox]").forEach(function (cb) { cb.checked = checkbox.checked; });
	}

	function pslShowAll() {
	    document.getElementById("pslSearchInput").value = "";
	    document.querySelectorAll("#pslEmployeeBody tr").forEach(function (r) { r.style.display = ""; });
	}

	function pslSearch() {
	    var keyword = document.getElementById("pslSearchInput").value.trim();
	    document.querySelectorAll("#pslEmployeeBody tr").forEach(function (r) {
	        var name = r.getAttribute("data-name") || "";
	        r.style.display = (!keyword || name.indexOf(keyword) > -1) ? "" : "none";
	    });
	}

	function pslToggleSign(checkbox) {
	    document.getElementById("pslSignTitle").style.display = checkbox.checked ? "" : "none";
	}

	function pslToggleLogo(show) {
	    document.getElementById("pslLogoMark").style.display = show ? "" : "none";
	    document.getElementById("pslLogoCloseBtn").style.display = show ? "" : "none";
	    document.getElementById("pslLogoPlaceholder").style.display = show ? "none" : "inline-block";
	}

	function pslToggleSeal(show) {
	    document.getElementById("pslSealMark").style.display = show ? "flex" : "none";
	    document.getElementById("pslSealCloseBtn").style.display = show ? "" : "none";
	    document.getElementById("pslSealPlaceholder").style.display = show ? "none" : "flex";
	}
	</script>
</body>
</html>
