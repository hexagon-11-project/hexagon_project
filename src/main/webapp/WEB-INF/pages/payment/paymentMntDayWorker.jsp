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
.dw-table input.tax-input { background: #fff8de; }
.dw-table input.readonly-input { background: #f2f2f2; color: #666; cursor: default; }
.dw-table input.ded-input::-webkit-outer-spin-button,
.dw-table input.ded-input::-webkit-inner-spin-button,
.dw-table input.tax-input::-webkit-outer-spin-button,
.dw-table input.tax-input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }
.dw-table input.ded-input[type=number],
.dw-table input.tax-input[type=number] { -moz-appearance: textfield; }

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
			<button type="button" class="btn btn-default" onclick="openLoadPrevModal()" style="background: #fff; border: 1px solid #ccc; padding: 4px 10px; font-size: 12px;">
				<i class="fas fa-file-import"></i> 지난급여 불러오기
			</button>
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
										<td><input type="number" class="ded-input" data-item-id="${item.deductionItemId}" value="0" oninput="recalcTotals()"></td>
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
					<div id="sumCount" style="font-size: 20px; font-weight: bold; margin-top: 5px;">0 건</div>
				</div>
				<div style="flex: 2; background: #5bc0de; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">＋ 지급 총액</div>
					<div id="sumPay" style="font-size: 20px; font-weight: bold; margin-top: 5px;">0 원</div>
				</div>
				<div style="flex: 2; background: #d9534f; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">－ 공제 총액</div>
					<div id="sumDeduction" style="font-size: 20px; font-weight: bold; margin-top: 5px;">0 원</div>
				</div>
				<div style="flex: 2; background: #4e5d6c; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">실지급액</div>
					<div id="sumNet" style="font-size: 20px; font-weight: bold; margin-top: 5px;">0 원</div>
				</div>
			</div>
		</div>
	</main>

	<!-- [지난급여 불러오기] 모달 -->
	<div id="loadPrevModalOverlay" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 9999; align-items: center; justify-content: center;">
		<div style="background: white; width: 320px; border-radius: 5px; box-shadow: 0 5px 15px rgba(0,0,0,0.5); padding: 25px; text-align: center; font-family: 'Malgun Gothic', sans-serif;">
			<h4 style="margin-top: 0; margin-bottom: 20px; font-weight: bold; text-align: left; font-size: 16px;">급여연월 선택</h4>
			<select id="prevPayrollSelect" class="form-control" style="width: 100%; margin-bottom: 20px; height: 35px;">
				<option value="">귀속연월 차수 선택</option>
			</select>
			<button type="button" class="btn btn-primary" onclick="executeLoadPrev()" style="width: 100%; background: #337ab7; border: none; padding: 10px; font-weight: bold;">급여정보 불러오기</button>
			<button type="button" class="btn btn-default" onclick="closeLoadPrevModal()" style="width: 100%; margin-top: 8px; padding: 10px;">취소</button>
		</div>
	</div>

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
	    loadSummary();
	});

	function reloadPayrollData() {
	    document.getElementById("searchForm").submit();
	}

	// ---------------- 근로자 목록 선택 ----------------
	function selectEmployeeRow(rowEl, empId) {
	    if (!confirm("변경된 근무기록 데이터가 있습니다.\n\n변경된 데이터로 수정 하시겠습니까?")) return;
	    alert("근무기록이 변경됨에 따라 근무기록 확인 후,\n\n저장버튼을 클릭하시어 저장해주세요.");

	    document.querySelectorAll('#employeeTableBody tr').forEach(function (r) { r.style.background = ""; r.style.color = ""; });
	    rowEl.style.background = "#2f6fa8";
	    rowEl.style.color = "#fff";
	    document.getElementById("selectedEmpId").value = empId;

	    fetch(CTX + "/Payment/dayWorkerDetailAjax.do?payrollDayWorkerEmployeeId=" + empId)
	        .then(function (res) { return res.json(); })
	        .then(function (data) {
	            document.getElementById("dailyBody").innerHTML = "";
	            if (data.dailyList) {
	                data.dailyList.forEach(function (d) {
	                    addDailyRow(d.workDate, d.rate, d.payAmt, d.incomeTax, d.localTax);
	                });
	            }

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
	        '<td><input type="number" class="tax-input f-income" value="' + (incomeTax || 0) + '"></td>' +
	        '<td><input type="number" class="tax-input f-local" value="' + (localTax || 0) + '"></td>';
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
	    }).then(function (res) {
	        if (res.ok) { alert("신규 근로자가 추가되었습니다."); location.reload(); }
	        else { alert("추가 중 오류가 발생했습니다."); }
	    }).catch(function () { alert("서버 통신에 실패했습니다."); });
	}

	// ---------------- 선택삭제 / 전체삭제 (실제 DB 반영) ----------------
	function deleteSelectedEmployees() {
	    var empId = document.getElementById("selectedEmpId").value;
	    if (!empId) { alert("삭제할 근로자를 선택하세요."); return; }
	    if (!confirm("선택한 근로자를 삭제하시겠습니까? 삭제된 정보는 복구할 수 없습니다.")) return;

	    var formData = new URLSearchParams();
	    formData.append("payrollDayWorkerEmployeeIds", empId);

	    fetch(CTX + "/Payment/dayWorkerDeleteSelected.do", {
	        method: "POST",
	        headers: { "Content-Type": "application/x-www-form-urlencoded" },
	        body: formData.toString()
	    }).then(function (res) { return res.text(); })
	      .then(function (result) {
	          if (result === "SUCCESS") { alert("삭제되었습니다."); location.reload(); }
	          else { alert("삭제 중 오류가 발생했습니다."); }
	      });
	}

	function deleteAllEmployees() {
	    var payrollDayWorkerId = document.getElementById("payrollDayWorkerId").value;
	    if (!confirm("■ 주의! 삭제된 급여입력 정보는 복구할 수 없습니다. 전체 삭제하시겠습니까?")) return;
	    if (!confirm("[전체] 급여입력 정보를 삭제 하시겠습니까?")) return;

	    var formData = new URLSearchParams();
	    formData.append("payrollDayWorkerId", payrollDayWorkerId);

	    fetch(CTX + "/Payment/dayWorkerDeleteAll.do", {
	        method: "POST",
	        headers: { "Content-Type": "application/x-www-form-urlencoded" },
	        body: formData.toString()
	    }).then(function (res) { return res.text(); })
	      .then(function (result) {
	          if (result === "SUCCESS") { alert("전체 삭제되었습니다."); location.reload(); }
	          else { alert("삭제 중 오류가 발생했습니다."); }
	      });
	}

	// ---------------- 저장 ----------------
	function saveDetail() {
	    var empId = document.getElementById("selectedEmpId").value;
	    if (!empId) { alert("근로자를 먼저 선택하세요."); return; }

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

	    fetch(CTX + "/Payment/dayWorkerSave.do", {
	        method: "POST",
	        headers: { "Content-Type": "application/x-www-form-urlencoded" },
	        body: formData.toString()
	    }).then(function (res) { return res.text(); })
	      .then(function (result) {
	          if (result === "SUCCESS") { alert("저장되었습니다."); location.reload(); }
	          else { alert("저장 중 문제가 발생했습니다."); }
	      })
	      .catch(function () { alert("서버 통신에 실패했습니다."); });
	}

	function clearForm() {
	    document.querySelectorAll('#dailyBody tr').forEach(function (tr) {
	        tr.querySelector('.f-income').value = 0;
	        tr.querySelector('.f-local').value = 0;
	    });
	    document.querySelectorAll('#dedBody .ded-input').forEach(function (input) { input.value = 0; });
	    recalcTotals();
	}

	// ---------------- 종합정보 ----------------
	function loadSummary() {
	    var payrollDayWorkerId = document.getElementById("payrollDayWorkerId").value;
	    if (!payrollDayWorkerId) return;
	    // 종합정보는 서버에서 JSTL로 최초 렌더링하지 않으므로, 목록 합계를 화면에서 즉시 집계
	    var payTotal = 0, dedTotal = 0, netTotal = 0, count = 0;
	    document.querySelectorAll('#employeeTableBody tr[data-id]').forEach(function (tr) {
	        count++;
	        var netCell = tr.querySelectorAll('td')[3];
	        netTotal += Number((netCell ? netCell.innerText.replace(/,/g, '') : 0)) || 0;
	    });
	    document.getElementById("sumCount").innerText = count + " 건";
	    document.getElementById("sumNet").innerText = won(netTotal) + " 원";
	}

	// ---------------- 지난급여 불러오기 ----------------
	function openLoadPrevModal() {
	    var select = document.getElementById("prevPayrollSelect");
	    select.innerHTML = '<option value="">귀속연월 차수 선택</option>';
	    var today = new Date();
	    var year = today.getFullYear(), month = today.getMonth() + 1;
	    for (var i = 0; i < 12; i++) {
	        var m = month - i, y = year;
	        if (m <= 0) { m += 12; y -= 1; }
	        var mStr = m < 10 ? "0" + m : "" + m;
	        var val = y + "" + mStr + "-1";
	        var text = y + "년 " + mStr + "월 01차";
	        select.options.add(new Option(text, val));
	    }
	    document.getElementById('loadPrevModalOverlay').style.display = 'flex';
	}

	function closeLoadPrevModal() {
	    document.getElementById('loadPrevModalOverlay').style.display = 'none';
	}

	function executeLoadPrev() {
	    var selectedVal = document.getElementById("prevPayrollSelect").value;
	    if (!selectedVal) { alert("불러올 귀속연월 차수를 선택해주세요."); return; }
	    if (!confirm("기등록된 급여테이블은 삭제되며,\n\n불러오기 한 급여테이블로 교체됩니다.\n\n불러오기 하시겠습니까?")) return;

	    var currYear = document.getElementById("payYear").value;
	    var currMonth = document.getElementById("payMonth").value;
	    var currSeq = document.getElementById("paySequence").value;
	    var prevYearMonth = selectedVal.split("-")[0];
	    var prevSeq = selectedVal.split("-")[1];

	    var formData = new URLSearchParams();
	    formData.append("currYear", currYear);
	    formData.append("currMonth", currMonth);
	    formData.append("currSeq", currSeq || "1");
	    formData.append("prevYearMonth", prevYearMonth);
	    formData.append("prevSeq", prevSeq);

	    fetch(CTX + "/Payment/dayWorkerLoadPrevAjax.do", {
	        method: "POST",
	        headers: { "Content-Type": "application/x-www-form-urlencoded" },
	        body: formData.toString()
	    }).then(function (res) { return res.json(); })
	      .then(function (data) {
	          if (data.status === "SUCCESS") { alert("[불러오기] " + data.count + "건"); location.reload(); }
	          else { alert("불러오기 중 오류가 발생했습니다."); }
	      })
	      .catch(function () { alert("서버 통신에 실패했습니다."); });
	}

	</script>
</body>
</html>
