<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>급여입력/관리(일용직) | HEXAGON PAY</title>
<%@ include file="../../jspf/head.jspf"%>
<style>
body { min-width: 1200px; }

.switch-wrap { display: flex; align-items: center; gap: 6px; background: #fff; padding: 3px 8px; border-radius: 3px; cursor: pointer; user-select: none; }
.switch-btn { background: #28a745; color: white; padding: 1px 10px; border-radius: 12px; font-weight: bold; font-size: 11px; display: inline-block; }
.switch-btn.off { background: #6c757d; }

.dw-table { width: 100%; border-collapse: collapse; font-size: 12px; }
.dw-table th { background: #f4f6f9; color: #337ab7; border: 1px solid #ddd; padding: 6px; text-align: center; }
.dw-table td { border: 1px solid #ddd; padding: 4px; text-align: center; }
.dw-table input { width: 100%; height: 24px; border: 1px solid #ddd; padding: 2px 4px; text-align: right; box-sizing: border-box; }
.dw-table input.date-input { text-align: center; }
.dw-table input.readonly-input { background: #f2f2f2; color: #666; cursor: default; }
.dw-table input.ded-input::-webkit-outer-spin-button,
.dw-table input.ded-input::-webkit-inner-spin-button,
.dw-table input.readonly-input::-webkit-outer-spin-button,
.dw-table input.readonly-input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }
.dw-table input.ded-input[type=number],
.dw-table input.readonly-input[type=number] { -moz-appearance: textfield; }

.ded-panel-head { display: flex; justify-content: space-between; align-items: center; background: #fcecec; border: 1px solid #f1dada; padding: 5px 10px; font-weight: bold; color: #c0392b; font-size: 12px; }
.ded-mode-btn { font-size: 11px; padding: 3px 8px; border-radius: 4px; border: none; cursor: pointer; background: #ddd; color: #555; margin-left: 4px; }
.ded-mode-btn.active { background: #1b2733; color: #fff; }
</style>
</head>
<body>
	<%@ include file="../../jspf/app-start.jspf"%>
	<%@ include file="../../jspf/sidebar.jspf"%>

	<main class="content-area">
		<div class="page-header" style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
			<img src="https://img.payzon.co.kr/_commonImg/pay_tit_img.gif" width="50" height="45" alt="급여입력/관리(일용직)">
			<div>
				<h2 style="margin: 0; font-size: 20px; font-weight: bold;">급여입력/관리(일용직)</h2>
				<p class="text-muted" style="margin: 3px 0 0 0; font-size: 12px; color: #666;">
					일용직 근로자의 급여 및 상여금 정보를 입력, 저장, 관리하는 메뉴입니다.
					<span style="color: #d9534f; font-weight: bold;">귀속연월, 급여차수를 확인하세요!!</span>
				</p>
			</div>
		</div>

		<jsp:useBean id="now" class="java.util.Date" />
		<fmt:formatDate value="${now}" pattern="yyyy" var="currentYear" />
		<fmt:formatDate value="${now}" pattern="MM" var="currentMonth" />
		<c:set var="prevMonth" value="${currentMonth - 1}" />
		<c:set var="prevYear" value="${currentYear}" />
		<c:if test="${prevMonth == 0}">
			<c:set var="prevMonth" value="12" />
			<c:set var="prevYear" value="${currentYear - 1}" />
		</c:if>
		<fmt:formatNumber value="${prevMonth}" pattern="00" var="formattedPrevMonth" />
		<c:set var="selectedYear" value="${not empty param.payYear ? param.payYear : prevYear}" />
		<c:set var="selectedMonth" value="${not empty param.payMonth ? param.payMonth : formattedPrevMonth}" />

		<form id="searchForm" action="${pageContext.request.contextPath}/Payment/paymentMntDayWorker.do" method="GET">
			<div style="background: #c85a5a; padding: 10px 15px; border-radius: 4px; display: flex; align-items: center; justify-content: space-between; color: white; margin-bottom: 15px; font-size: 13px;">
				<div style="display: flex; align-items: center; gap: 15px;">
					<div style="display: flex; align-items: center; gap: 5px;">
						<strong>* 귀속연월</strong>&nbsp;
						<select name="payYear" id="payYear" class="form-control input-sm" style="display: inline-block; width: 80px; background: #fff; color: #333; padding: 3px 5px;" onchange="reloadPayrollData()">
							<c:forEach var="year" begin="2005" end="2027">
								<option value="${year}" <c:if test="${selectedYear eq year}">selected</c:if>>${year}년</option>
							</c:forEach>
						</select>&nbsp;
						<select name="payMonth" id="payMonth" class="form-control input-sm" style="display: inline-block; width: 65px; background: #fff; color: #333; padding: 3px 5px;" onchange="reloadPayrollData()">
							<c:forEach var="month" begin="1" end="12">
								<fmt:formatNumber value="${month}" pattern="00" var="formattedMonth" />
								<option value="${formattedMonth}" <c:if test="${selectedMonth eq formattedMonth}">selected</c:if>>${formattedMonth}월</option>
							</c:forEach>
						</select>
					</div>
					<div style="display: flex; align-items: center; gap: 5px;">
						<strong>* 급여차수</strong>&nbsp;
						<select name="paySequence" id="paySequence" class="form-control input-sm" style="display: inline-block; width: 90px; background: #fff; color: #333; padding: 3px 5px;" onchange="reloadPayrollData()">
							<c:forEach var="seq" begin="1" end="10">
								<fmt:formatNumber value="${seq}" pattern="00" var="formattedSeq" />
								<option value="${formattedSeq}" <c:if test="${(empty param.paySequence and formattedSeq eq '01') or (param.paySequence eq formattedSeq)}">selected</c:if>>급여-${formattedSeq}차</option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div style="display: flex; align-items: center; gap: 15px;">
					<div>
						<strong>* 정산기간</strong>&nbsp;
						<input type="text" id="calcPeriodStart" readonly class="form-control input-sm" style="display: inline-block; width: 95px; background: #fff; color: #333; padding: 3px 5px; text-align: center;">
						~&nbsp;
						<input type="text" id="calcPeriodEnd" readonly class="form-control input-sm" style="display: inline-block; width: 95px; background: #fff; color: #333; padding: 3px 5px; text-align: center;">
					</div>
					<div>
						<strong>* 급여지급일</strong>&nbsp;
						<input type="text" id="payDate" readonly class="form-control input-sm" style="display: inline-block; width: 95px; background: #fff; color: #333; padding: 3px 5px; text-align: center;">
					</div>
				</div>
			</div>
			<input type="hidden" id="payrollDayWorkerId" value="${payrollDayWorkerId}">
		</form>

		<div style="margin-bottom: 10px; display: flex; gap: 5px;">
			<button type="button" class="btn btn-primary" onclick="openEmployeeSelectModal()" style="background: #337ab7; color: #fff; border: none; padding: 4px 10px; font-size: 12px;">
				<i class="fas fa-plus"></i> 신규추가
			</button>
			<button type="button" class="btn btn-default" onclick="deleteSelectedEmployees()" style="background: #fff; border: 1px solid #ccc; padding: 4px 10px; font-size: 12px;">
				<i class="fas fa-trash-alt"></i> 선택삭제
			</button>
			<button type="button" class="btn btn-danger" onclick="deleteAllEmployees()" style="background: #d9534f; color: #fff; border: none; padding: 4px 10px; font-size: 12px;">
				<i class="fas fa-trash"></i> 전체삭제
			</button>
		</div>

		<!-- 메인 그리드: 좌측 근로자 목록 / 우측 급여상세 -->
		<div style="display: flex; gap: 15px; align-items: flex-start;">

			<!-- 좌측: 근로자 목록 -->
			<div style="flex: 1; background: #fff; border: 1px solid #ddd; padding: 10px;">
				<table class="dw-table">
					<thead>
						<tr>
							<th>구분</th>
							<th>성명</th>
							<th>부서</th>
							<th>실지급액</th>
						</tr>
					</thead>
					<tbody id="employeeTableBody">
						<c:forEach var="emp" items="${employeeList}">
							<tr data-id="${emp.payrollDayWorkerEmployeeId}" onclick="selectEmployeeRow(this, '${emp.payrollDayWorkerEmployeeId}')" style="cursor: pointer;">
								<td>${emp.employmentType}</td>
								<td>${emp.employeeName}</td>
								<td>${emp.department}</td>
								<td style="text-align: right;"><fmt:formatNumber value="${emp.netPayAmount}" pattern="#,###" /></td>
							</tr>
						</c:forEach>
						<c:if test="${empty employeeList}">
							<tr id="emptyRow"><td colspan="4" style="padding: 25px; color: #666;">등록된 근로자가 없습니다.</td></tr>
						</c:if>
					</tbody>
				</table>
			</div>

			<!-- 우측: 급여상세 -->
			<div style="flex: 1.3; background: #fff; border: 1px solid #ddd; padding: 10px; font-size: 12px;">
				<input type="hidden" id="selectedEmpId" value="">

				<div style="display: flex; gap: 10px;">
					<!-- 일자별 지급내역 -->
					<div style="flex: 1.4;">
						<table class="dw-table" style="table-layout: fixed;">
							<thead>
								<tr><th style="width: 26%;">일자</th><th style="width: 14%;">지급율</th><th style="width: 20%;">지급액</th><th style="width: 20%;">소득세</th><th style="width: 20%;">지방소득세</th></tr>
							</thead>
							<tbody id="dailyBody"></tbody>
						</table>
						<div style="display: flex; border: 1px solid #ddd; border-top: none; font-weight: bold; text-align: center;">
							<div style="flex: 1; background: #f4f6f9; padding: 6px;">지급총액 : <span id="payTotalText" style="color: #337ab7;">0</span> 원</div>
						</div>
					</div>

					<!-- 공제항목 -->
					<div style="flex: 1;">
						<div class="ded-panel-head">
							<span>공제항목</span>
							<span>
								<button type="button" class="ded-mode-btn active" id="btnMode4ins" onclick="setDeductionMode('4대보험')">4대보험</button>
								<button type="button" class="ded-mode-btn" id="btnModePeriod" onclick="setDeductionMode('기간단위 소득세')">기간단위 소득세</button>
							</span>
						</div>
						<table class="dw-table">
							<tbody id="dedBody">
								<c:forEach var="item" items="${deductionItemList}">
									<tr>
										<td style="text-align: left; background: #fafafa;">${item.deductionItemName}</td>
										<td><input type="number" class="ded-input" data-item-id="${item.deductionItemId}" value="0" oninput="recalcTotals()" onchange="autoSaveDeduction()"></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<div style="display: flex; justify-content: space-between; background: #fcecec; padding: 6px 10px; font-weight: bold; color: #c0392b;">
							<span>공제총액</span><span id="deductionTotalText">0</span>
						</div>
					</div>
				</div>

				<div style="background: #1b3a5c; color: #fff; text-align: center; padding: 10px; font-weight: bold; font-size: 15px; margin-top: 8px; border-radius: 2px;">
					실지급액 : <span id="netPayText" style="color: #ffd65a;">0</span> 원
				</div>

				<div style="text-align: right; margin-top: 10px; display: flex; justify-content: flex-end; gap: 5px;">
					<button type="button" onclick="saveDetail()" class="btn btn-primary btn-sm" style="background: #337ab7; color: white; border: none; padding: 6px 18px; font-weight: bold;">저장</button>
					<button type="button" onclick="clearForm()" class="btn btn-default btn-sm" style="background: #ccc; color: #333; border: 1px solid #bbb; padding: 6px 15px;">내용 지우기</button>
				</div>
			</div>
		</div>

		<!-- 종합정보 -->
		<div style="margin-top: 25px;">
			<div style="font-weight: bold; margin-bottom: 8px; font-size: 14px;">급여 종합정보</div>
			<div style="display: flex; gap: 10px;">
				<div style="flex: 1; background: #95a5a6; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">월 합계</div>
					<div id="sumCount" style="font-size: 20px; font-weight: bold; margin-top: 5px;">${empty summaryInfo.workerCount ? 0 : summaryInfo.workerCount} 건</div>
				</div>
				<div style="flex: 2; background: #5bc0de; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">＋ 지급 총액</div>
					<div id="sumPay" style="font-size: 20px; font-weight: bold; margin-top: 5px;"><fmt:formatNumber value="${empty summaryInfo.payTotal ? 0 : summaryInfo.payTotal}" pattern="#,###" /> 원</div>
				</div>
				<div style="flex: 2; background: #d9534f; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">－ 공제 총액</div>
					<div id="sumDeduction" style="font-size: 20px; font-weight: bold; margin-top: 5px;"><fmt:formatNumber value="${empty summaryInfo.deductionTotal ? 0 : summaryInfo.deductionTotal}" pattern="#,###" /> 원</div>
				</div>
				<div style="flex: 2; background: #4e5d6c; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">실지급액</div>
					<div id="sumNet" style="font-size: 20px; font-weight: bold; margin-top: 5px;"><fmt:formatNumber value="${empty summaryInfo.netPay ? 0 : summaryInfo.netPay}" pattern="#,###" /> 원</div>
				</div>
			</div>
		</div>
	</main>

	<%@ include file="../../jspf/app-end.jspf"%>

	<script>
	var CTX = "${pageContext.request.contextPath}";
	var rowSeq = 0;

	// ---------------- 정산기간/급여지급일 자동 계산 (paymentMnt와 동일 로직) ----------------
	function updateAutoDates() {
	    var year = parseInt(document.getElementById("payYear").value, 10);
	    var month = parseInt(document.getElementById("payMonth").value, 10);
	    var monthStr = month < 10 ? "0" + month : "" + month;
	    var startDateStr = year + "-" + monthStr + "-01";
	    var lastDay = new Date(year, month, 0).getDate();
	    var endDateStr = year + "-" + monthStr + "-" + lastDay;

	    var nextYear = year, nextMonth = month + 1;
	    if (nextMonth > 12) { nextMonth = 1; nextYear++; }
	    var nextMonthStr = nextMonth < 10 ? "0" + nextMonth : "" + nextMonth;
	    var payDateStr = nextYear + "-" + nextMonthStr + "-05";

	    document.getElementById("calcPeriodStart").value = startDateStr;
	    document.getElementById("calcPeriodEnd").value = endDateStr;
	    document.getElementById("payDate").value = payDateStr;
	}
	window.addEventListener("DOMContentLoaded", function () {
	    updateAutoDates();
	});

	function reloadPayrollData() {
	    document.getElementById("searchForm").submit();
	}

	// ---------------- 근로자 목록 선택 ----------------
	function selectEmployeeRow(rowEl, empId) {
	    fetch(CTX + "/Payment/dayWorkerDetailAjax.do?payrollDayWorkerEmployeeId=" + empId)
	        .then(function (res) { return res.json(); })
	        .then(function (data) {
	            if (!data.dailyList || data.dailyList.length === 0) {
	                alert("해당 월에는 근무일이 없습니다.");
	                return;
	            }

	            document.querySelectorAll('#employeeTableBody tr').forEach(function (r) { r.style.background = ""; r.style.color = ""; });
	            rowEl.style.background = "#2f6fa8";
	            rowEl.style.color = "#fff";
	            document.getElementById("selectedEmpId").value = empId;

	            document.getElementById("dailyBody").innerHTML = "";
	            data.dailyList.forEach(function (d) {
	                addDailyRow(d.workDate, d.rate, d.payAmt, d.incomeTax, d.localTax);
	            });

	            var amounts = data.deductionAmounts || {};
	            document.querySelectorAll('#dedBody .ded-input').forEach(function (input) {
	                input.value = amounts[input.dataset.itemId] || 0;
	            });
	            setDeductionMode(data.deductionMode || '4대보험');

	            recalcTotals();
	        })
	        .catch(function (err) { console.error("상세 조회 실패:", err); });
	}

	// ---------------- 일자별 행 ----------------
	function addDailyRow(workDate, rate, payAmt, incomeTax, localTax) {
	    var id = "row_" + (rowSeq++);
	    var tr = document.createElement("tr");
	    tr.setAttribute("data-rowid", id);
	    tr.innerHTML =
	        '<td><input type="text" class="date-input f-date readonly-input" value="' + (workDate || '') + '" readonly></td>' +
	        '<td><input type="number" step="0.1" class="f-rate readonly-input" value="' + (rate != null ? rate : 1.0) + '" readonly></td>' +
	        '<td><input type="number" class="f-pay readonly-input" value="' + (payAmt || 0) + '" readonly></td>' +
	        '<td><input type="number" class="f-income readonly-input" value="' + (incomeTax || 0) + '" readonly></td>' +
	        '<td><input type="number" class="f-local readonly-input" value="' + (localTax || 0) + '" readonly></td>';
	    document.getElementById("dailyBody").appendChild(tr);
	}

	function setDeductionMode(mode) {
	    document.getElementById("selectedEmpId").dataset.deductionMode = mode;
	    var is4 = mode === '4대보험';
	    document.getElementById("btnMode4ins").classList.toggle("active", is4);
	    document.getElementById("btnModePeriod").classList.toggle("active", !is4);
	}

	// ---------------- 합계 계산 ----------------
	function recalcTotals() {
	    var payTotal = 0;
	    document.querySelectorAll('#dailyBody .f-pay').forEach(function (i) { payTotal += Number(i.value) || 0; });

	    var dedTotal = 0;
	    document.querySelectorAll('#dedBody .ded-input').forEach(function (input) { dedTotal += Number(input.value) || 0; });

	    document.getElementById("payTotalText").innerText = won(payTotal);
	    document.getElementById("deductionTotalText").innerText = won(dedTotal);
	    document.getElementById("netPayText").innerText = won(payTotal - dedTotal);
	}

	function won(n) { return (Number(n) || 0).toLocaleString('ko-KR'); }

	// ---------------- 신규추가 모달 ----------------
	function openEmployeeSelectModal() {
	    var url = CTX + "/Payment/dayWorkerEmployeeAddModal.do";
	    window.open(url, "DayWorkerEmpSelectModal", "width=600,height=600,left=200,top=100,scrollbars=yes");
	}

	// 모달(paymentMntDayWorker_employee_add_modal.jsp)에서 호출
	// 전체 새로고침을 하지 않고 방금 추가한 사원 행만 화면에 붙인다.
	// (새로고침을 하면 [선택삭제]/[전체삭제]로 화면에서만 지워둔 사원들이 DB 기준으로 다시 나타나 버리기 때문)
	function addEmployeesToMain(selectedEmpIds) {
	    if (!selectedEmpIds || selectedEmpIds.length === 0) { alert("선택된 사원이 없습니다."); return; }
	    var payrollDayWorkerId = document.getElementById("payrollDayWorkerId").value;

	    var formData = new URLSearchParams();
	    formData.append("payrollDayWorkerId", payrollDayWorkerId);
	    formData.append("employeeIds", selectedEmpIds.join(","));

	    fetch(CTX + "/Payment/dayWorkerEmployeeInsert.do", {
	        method: "POST",
	        headers: { "Content-Type": "application/x-www-form-urlencoded" },
	        body: formData.toString()
	    }).then(function (res) { return res.json(); })
	      .then(function (list) {
	          if (!list || list.length === 0) { alert("추가된 근로자가 없습니다."); return; }

	          var emptyRow = document.getElementById("emptyRow");
	          if (emptyRow) emptyRow.remove();

	          list.forEach(function (emp) {
	              var empId = String(emp.payrollEmployeeId);
	              if (document.querySelector('#employeeTableBody tr[data-id="' + empId + '"]')) return; // 이미 화면에 있으면 중복 추가 방지

	              var tr = document.createElement("tr");
	              tr.setAttribute("data-id", empId);
	              tr.style.cursor = "pointer";
	              tr.onclick = function () { selectEmployeeRow(tr, empId); };
	              tr.innerHTML =
	                  "<td>" + emp.employmentType + "</td>" +
	                  "<td>" + emp.employeeName + "</td>" +
	                  "<td>" + emp.department + "</td>" +
	                  "<td style='text-align:right;'>" + won(emp.netPayAmount) + "</td>";
	              document.getElementById("employeeTableBody").appendChild(tr);
	          });

	          alert("신규 근로자가 추가되었습니다.");
	      })
	      .catch(function () { alert("서버 통신에 실패했습니다."); });
	}

	// ---------------- 선택삭제 / 전체삭제 (실제 DB 반영) ----------------
	// 주의: 이 화면의 [선택삭제]/[전체삭제]는 DB 데이터(PAYROLL_EMPLOYEE/DAILY_WORK_RECORD/PAYROLL_DEDUCTION_DETAIL)를
	// 절대 지우지 않는다. 오직 이 화면의 사원 목록에서만 안 보이게 할 뿐이며, 페이지를 새로고침하면 다시 보인다.
	function clearDetailPanel() {
	    document.getElementById("selectedEmpId").value = "";
	    document.getElementById("dailyBody").innerHTML = "";
	    document.querySelectorAll('#dedBody .ded-input').forEach(function (input) { input.value = 0; });
	    recalcTotals();
	}

	function deleteSelectedEmployees() {
	    var empId = document.getElementById("selectedEmpId").value;
	    if (!empId) { alert("삭제할 근로자를 선택하세요."); return; }
	    if (!confirm("선택한 근로자를 목록에서 삭제하시겠습니까?")) return;

	    var row = document.querySelector('#employeeTableBody tr[data-id="' + empId + '"]');
	    if (row) row.remove();
	    clearDetailPanel();
	}

	function deleteAllEmployees() {
	    if (!confirm("화면의 모든 근로자를 목록에서 삭제하시겠습니까?")) return;

	    document.getElementById("employeeTableBody").innerHTML =
	        '<tr id="emptyRow"><td colspan="4" style="padding: 25px; color: #666;">등록된 근로자가 없습니다.</td></tr>';
	    clearDetailPanel();
	}

	// ---------------- 저장 ----------------
	function buildDetailFormData(empId) {
	    var formData = new URLSearchParams();
	    formData.append("payrollDayWorkerEmployeeId", empId);

	    document.querySelectorAll('#dailyBody tr').forEach(function (tr) {
	        var workDate = tr.querySelector('.f-date').value;
	        if (!workDate) return; // 일자 없는 빈 행 제외
	        formData.append("workDate", workDate);
	        formData.append("rate", tr.querySelector('.f-rate').value || "1.0");
	        formData.append("payAmt", tr.querySelector('.f-pay').value || "0");
	        formData.append("incomeTax", tr.querySelector('.f-income').value || "0");
	        formData.append("localTax", tr.querySelector('.f-local').value || "0");
	    });

	    formData.append("deductionMode", document.getElementById("selectedEmpId").dataset.deductionMode || "4대보험");
	    document.querySelectorAll('#dedBody .ded-input').forEach(function (input) {
	        formData.append("dedItemId", input.dataset.itemId);
	        formData.append("dedAmount", input.value || "0");
	    });

	    return formData;
	}

	function saveDetail() {
	    var empId = document.getElementById("selectedEmpId").value;
	    if (!empId) { alert("근로자를 먼저 선택하세요."); return; }

	    fetch(CTX + "/Payment/dayWorkerSave.do", {
	        method: "POST",
	        headers: { "Content-Type": "application/x-www-form-urlencoded" },
	        body: buildDetailFormData(empId).toString()
	    }).then(function (res) { return res.text(); })
	      .then(function (result) {
	          if (result === "SUCCESS") { alert("저장되었습니다."); location.reload(); }
	          else { alert("저장 중 문제가 발생했습니다."); }
	      })
	      .catch(function () { alert("서버 통신에 실패했습니다."); });
	}

	// 공제항목 금액을 바꾸고 다른 곳을 클릭(blur)하면 알림/새로고침 없이 바로 DB에 저장하고,
	// 좌측 목록의 실지급액과 하단 [급여 종합정보]를 그 자리에서 갱신한다.
	function autoSaveDeduction() {
	    var empId = document.getElementById("selectedEmpId").value;
	    if (!empId) return;

	    fetch(CTX + "/Payment/dayWorkerSave.do", {
	        method: "POST",
	        headers: { "Content-Type": "application/x-www-form-urlencoded" },
	        body: buildDetailFormData(empId).toString()
	    }).then(function (res) { return res.text(); })
	      .then(function (result) {
	          if (result !== "SUCCESS") { console.error("자동 저장 실패"); return; }
	          var row = document.querySelector('#employeeTableBody tr[data-id="' + empId + '"]');
	          if (row) {
	              var netCell = row.querySelectorAll('td')[3];
	              if (netCell) netCell.innerText = document.getElementById("netPayText").innerText;
	          }
	          refreshSummary();
	      })
	      .catch(function (err) { console.error("자동 저장 통신 실패:", err); });
	}

	function clearForm() {
	    document.querySelectorAll('#employeeTableBody tr').forEach(function (r) { r.style.background = ""; r.style.color = ""; });
	    document.getElementById("selectedEmpId").value = "";
	    document.getElementById("dailyBody").innerHTML = "";
	    document.querySelectorAll('#dedBody .ded-input').forEach(function (input) { input.value = 0; });
	    recalcTotals();
	}

	// ---------------- 종합정보 ----------------
	// 서버(DB)에서 이 급여차수의 일용직 사원 전체 합계를 다시 조회해서 하단 4개 박스를 갱신
	function refreshSummary() {
	    var payrollDayWorkerId = document.getElementById("payrollDayWorkerId").value;
	    if (!payrollDayWorkerId) return;
	    fetch(CTX + "/Payment/dayWorkerSummaryAjax.do?payrollDayWorkerId=" + payrollDayWorkerId)
	        .then(function (res) { return res.json(); })
	        .then(function (data) {
	            document.getElementById("sumCount").innerText = (data.workerCount || 0) + " 건";
	            document.getElementById("sumPay").innerText = won(data.payTotal) + " 원";
	            document.getElementById("sumDeduction").innerText = won(data.deductionTotal) + " 원";
	            document.getElementById("sumNet").innerText = won(data.netPay) + " 원";
	        })
	        .catch(function (err) { console.error("종합정보 조회 실패:", err); });
	}

	</script>
</body>
</html>
