<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>급여입력/관리 | HEXAGON PAY</title>
<%@ include file="../../jspf/head.jspf"%>
<style>
/* 화면이 좁아져도 좌우 영역이 찌그러지거나 아래로 떨어지지 않도록 고정하는 스타일 */
body {
	min-width: 1200px; /* 화면 전체의 최소 가로폭을 강제로 잡아줍니다 */
}
/* 계산방법 off 시 계산방법 입력 행 숨기기 */
.calc-method-row {
	display: table-row;
}

.calc-method-row.off {
	display: none;
}

/* 스위치 토글 디자인 */
.switch-wrap {
	display: flex;
	align-items: center;
	gap: 6px;
	background: #fff;
	padding: 3px 8px;
	border-radius: 3px;
	cursor: pointer;
	user-select: none;
}

.switch-btn {
	background: #28a745;
	color: white;
	padding: 1px 10px;
	border-radius: 12px;
	font-weight: bold;
	font-size: 11px;
	display: inline-block;
	transition: background 0.2s;
}

.switch-btn.off {
	background: #6c757d;
}
</style>
</head>
<body>
	<%@ include file="../../jspf/app-start.jspf"%>
	<%@ include file="../../jspf/sidebar.jspf"%>

	<main class="content-area">
		<!-- 화면 상단 타이틀 영역 -->
		<div class="page-header"
			style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
			<img src="https://img.payzon.co.kr/_commonImg/pay_tit_img.gif"
				width="50" height="45" alt="급여입력 및 관리">
			<div>
				<h2 style="margin: 0; font-size: 20px; font-weight: bold;">급여입력/관리</h2>
				<p class="text-muted"
					style="margin: 3px 0 0 0; font-size: 12px; color: #666;">
					월별, 사원별 급여 및 상여금 정보를 입력, 저장, 관리하는 메뉴입니다. <span
						style="color: #d9534f; font-weight: bold;">귀속연월, 급여차수를
						확인하세요!!</span>
				</p>
			</div>
		</div>

		<!-- [조각 1] 상단 검색 바 및 기능 버튼 영역 불러오기 -->
		<!-- 1. 현재 시스템의 연도(yyyy)와 월(MM) 구하기 -->
		<jsp:useBean id="now" class="java.util.Date" />
		<fmt:formatDate value="${now}" pattern="yyyy" var="currentYear" />
		<fmt:formatDate value="${now}" pattern="MM" var="currentMonth" />

		<!-- 2. 검색 파라미터가 있으면 그 값을 유지하고, 없으면 현재 연/월을 기본값으로 세팅 -->
		<c:set var="selectedYear"
			value="${not empty param.payYear ? param.payYear : currentYear}" />
		<c:set var="selectedMonth"
			value="${not empty param.payMonth ? param.payMonth : currentMonth}" />

		<form id="searchForm"
			action="${pageContext.request.contextPath}/Payment/paymentMnt.do"
			method="GET">
			<input type="hidden" name="incomeType" id="incomeTypeParam"
				value="${not empty param.incomeType ? param.incomeType : 'GENERAL'}">

			<div
				style="background: #d9534f; padding: 10px 15px; border-radius: 4px; display: flex; align-items: center; justify-content: space-between; color: white; margin-bottom: 15px; font-size: 13px;">
				<!-- 왼쪽 그룹 -->
				<div style="display: flex; align-items: center; gap: 15px;">
					<div style="display: flex; align-items: center; gap: 5px;">
						<strong>* 귀속연월</strong>&nbsp;

						<!-- 연도 Select Box -->
						<select name="payYear" id="payYear" class="form-control input-sm"
							style="display: inline-block; width: 80px; background: #fff; color: #333; padding: 3px 5px;"
							onchange="reloadPayrollData()">
							<c:forEach var="year" begin="2005" end="2027">
								<option value="${year}"
									<c:if test="${selectedYear eq year}">selected</c:if>>${year}년</option>
							</c:forEach>
						</select>&nbsp;

						<!-- 월 Select Box -->
						<select name="payMonth" id="payMonth"
							class="form-control input-sm"
							style="display: inline-block; width: 65px; background: #fff; color: #333; padding: 3px 5px;"
							onchange="reloadPayrollData()">
							<c:forEach var="month" begin="1" end="12">
								<fmt:formatNumber value="${month}" pattern="00"
									var="formattedMonth" />
								<option value="${formattedMonth}"
									<c:if test="${selectedMonth eq formattedMonth}">selected</c:if>>${formattedMonth}월</option>
							</c:forEach>
						</select>
					</div>

					<div style="display: flex; align-items: center; gap: 5px;">
						<strong>* 급여차수</strong>&nbsp;
						<!-- 급여차수 Select Box (1차 ~ 10차 자동 생성) -->
						<select name="paySequence" id="paySequence"
							class="form-control input-sm"
							style="display: inline-block; width: 90px; background: #fff; color: #333; padding: 3px 5px;"
							onchange="reloadPayrollData()">
							<c:forEach var="seq" begin="1" end="10">
								<fmt:formatNumber value="${seq}" pattern="00" var="formattedSeq" />
								<option value="${formattedSeq}"
									<c:if test="${(empty param.paySequence and formattedSeq eq '01') or (param.paySequence eq formattedSeq)}">selected</c:if>>급여-${formattedSeq}차</option>
							</c:forEach>
						</select>
					</div>
				</div>

				<!-- 중간 그룹 -->
				<div style="display: flex; align-items: center; gap: 15px;">
					<div>
						<strong>* 정산기간</strong>&nbsp;
						<!-- id="calcPeriodStart" 와 id="calcPeriodEnd" 추가 -->
						<input type="text" id="calcPeriodStart"
							value="${payrollInfo.calcPeriodStart}" readonly
							class="form-control input-sm"
							style="display: inline-block; width: 95px; background: #fff; color: #333; padding: 3px 5px; text-align: center;">
						~&nbsp; <input type="text" id="calcPeriodEnd"
							value="${payrollInfo.calcPeriodEnd}" readonly
							class="form-control input-sm"
							style="display: inline-block; width: 95px; background: #fff; color: #333; padding: 3px 5px; text-align: center;">
					</div>
					<div>
						<strong>* 급여지급일</strong>&nbsp;
						<!-- id="payDate" 추가 -->
						<input type="text" id="payDate" value="${payrollInfo.payDate}"
							readonly class="form-control input-sm"
							style="display: inline-block; width: 95px; background: #fff; color: #333; padding: 3px 5px; text-align: center;">
						<button type="button" class="btn btn-default btn-xs"
							style="padding: 3px 6px; background: #fff; border: 1px solid #ccc; color: #333; margin-left: 2px;">
							<i class="fas fa-sync-alt"></i> 수정
						</button>
					</div>
				</div>

				<!-- 오른쪽 그룹 (계산방법) -->
				<div class="switch-wrap" onclick="toggleCalcMethod()">
					<strong style="font-size: 12px; color: #333;">* 계산방법</strong> <span
						id="calcSwitchBadge" class="switch-btn">on</span>
				</div>
			</div>
		</form>

		<div style="margin-bottom: 10px; display: flex; gap: 5px;">
			<button type="button" class="btn btn-default"
				style="background: #fff; border: 1px solid #ccc; padding: 4px 10px; font-size: 12px;">
				<i class="fas fa-file-import"></i> 지난급여 불러오기
			</button>
			<button type="button" class="btn btn-primary"
				onclick="openEmployeeSelectModal()"
				style="background: #337ab7; color: #fff; border: none; padding: 4px 10px; font-size: 12px;">
				<i class="fas fa-plus"></i> 신규추가
			</button>
			<!-- [선택삭제] 버튼 -->
<button type="button" class="btn btn-default" onclick="deleteSelectedEmployees()"
    style="background: #fff; border: 1px solid #ccc; padding: 4px 10px; font-size: 12px; cursor: pointer;">
    <i class="fas fa-trash-alt"></i> 선택삭제
</button>

<!-- [전체삭제] 버튼 -->
<button type="button" class="btn btn-danger" onclick="deleteAllEmployees()"
    style="background: #d9534f; color: #fff; border: none; padding: 4px 10px; font-size: 12px; cursor: pointer;">
    <i class="fas fa-trash"></i> 전체삭제
</button>
		</div>

		<!-- 귀속연월에 맞춰 정산기간 및 급여지급일 자동 계산 스크립트 -->
		<script>
	function updateAutoDates() {
		var yearSel = document.getElementById("payYear");
		var monthSel = document.getElementById("payMonth");
		
		if (!yearSel || !monthSel) return;

		var year = parseInt(yearSel.value, 10);
		var month = parseInt(monthSel.value, 10);

		// 월을 무조건 2자리(01, 02..)로 맞춤
		var monthStr = month < 10 ? "0" + month : "" + month;

		// 1. 정산기간 시작일 (항상 1일)
		var startDateStr = year + "-" + monthStr + "-01";

		// 2. 정산기간 종료일 (해당 월의 마지막 날짜 계산)
		// Date 객체에서 일을 0으로 주면 이전 달의 마지막 날을 반환함. 
		// 따라서 넘어온 month를 그대로 넣으면 해당 month의 마지막 날짜가 나옴.
		var lastDay = new Date(year, month, 0).getDate();
		var endDateStr = year + "-" + monthStr + "-" + lastDay;

		// 3. 급여지급일 (다음 달 5일 계산)
		var nextYear = year;
		var nextMonth = month + 1;
		if (nextMonth > 12) {
			nextMonth = 1;
			nextYear++;
		}
		var nextMonthStr = nextMonth < 10 ? "0" + nextMonth : "" + nextMonth;
		var payDateStr = nextYear + "-" + nextMonthStr + "-05";

		// 계산된 값들을 화면의 input 태그에 자동 세팅
		document.getElementById("calcPeriodStart").value = startDateStr;
		document.getElementById("calcPeriodEnd").value = endDateStr;
		document.getElementById("payDate").value = payDateStr;
	}

	// 화면이 처음 로드될 때 바로 날짜 계산 함수 실행
	window.addEventListener("DOMContentLoaded", updateAutoDates);
</script>

		<!-- 메인 컨텐츠 그리드 레이아웃 (좌/우 분할) -->
		<div style="display: flex; gap: 15px; align-items: flex-start;">

			<!-- [조각 2] 좌측: 대상 사원 목록 그리드 불러오기 -->
			<div
				style="flex: 1.2; background: #fff; border: 1px solid #ddd; padding: 10px;">
				<table class="table table-bordered table-hover"
					style="width: 100%; border-collapse: collapse; font-size: 12px; margin-bottom: 0;">
					<thead style="background: #f4f6f9;">
						<tr>
							<th
								style="border: 1px solid #ddd; padding: 6px; text-align: center; color: #337ab7;">구분</th>
							<th
								style="border: 1px solid #ddd; padding: 6px; text-align: center; color: #337ab7;">성명</th>
							<th
								style="border: 1px solid #ddd; padding: 6px; text-align: center; color: #337ab7;">부서</th>
							<th
								style="border: 1px solid #ddd; padding: 6px; text-align: center;">지급총액</th>
							<th
								style="border: 1px solid #ddd; padding: 6px; text-align: center;">공제총액</th>
							<th
								style="border: 1px solid #ddd; padding: 6px; text-align: center;">실지급액</th>
						</tr>
					</thead>
					<tbody id="employeeTableBody">
						<c:forEach var="emp" items="${employeeList}">
							<tr onclick="selectEmployeeRow(this, '${emp.payrollEmployeeId}')"
								style="cursor: pointer;">
								<td
									style="border: 1px solid #ddd; padding: 6px; text-align: center;">${emp.employmentType}</td>
								<td
									style="border: 1px solid #ddd; padding: 6px; text-align: center;">${emp.employeeName}</td>
								<td
									style="border: 1px solid #ddd; padding: 6px; text-align: center;">${emp.department}</td>
								<td
									style="border: 1px solid #ddd; padding: 6px; text-align: right; color: #337ab7; font-weight: bold;"><fmt:formatNumber
										value="${emp.totalPayAmount}" pattern="#,###" /></td>
								<td
									style="border: 1px solid #ddd; padding: 6px; text-align: right; color: #d9534f; font-weight: bold;"><fmt:formatNumber
										value="${emp.totalDeductionAmount}" pattern="#,###" /></td>
								<td
									style="border: 1px solid #ddd; padding: 6px; text-align: right;"><fmt:formatNumber
										value="${emp.netPayAmount}" pattern="#,###" /></td>
							</tr>
						</c:forEach>
						<c:if test="${empty employeeList}">
							<tr>
								<td colspan="6"
									style="border: 1px solid #ddd; padding: 25px; text-align: center; color: #666;">등록된
									사원 데이터가 없습니다.</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</div>

			<!-- [조각 3] 우측: 소득 탭 및 상세 금액 입력 폼 불러오기 -->
			<div
				style="flex: 1.3; background: #fff; border: 1px solid #ddd; padding: 10px; font-size: 12px;">

				<!-- 탭 버튼 영역 (디자인 수정 적용) -->
				<div
					style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 10px; border-bottom: 2px solid #222;">
					<div style="display: flex; gap: 2px;">
						<!-- 일반소득 탭 -->
						<button type="button" onclick="switchIncomeTab('GENERAL')"
							style="background: ${param.incomeType == 'BUSINESS' ? '#999999' : '#009688'}; 
				color: white; 
				border: none; 
				border-radius: 5px 5px 0 0; 
				padding: 8px 25px; 
				font-weight: bold; 
				font-size: 13px; 
				cursor: pointer;">일반소득</button>

						<!-- 사업소득/기타소득 탭 -->
						<!--  <button type="button" onclick="switchIncomeTab('BUSINESS')"
				style="background: ${param.incomeType == 'BUSINESS' ? '#009688' : '#999999'}; 
				color: white; 
				border: none; 
				border-radius: 5px 5px 0 0; 
				padding: 8px 25px; 
				font-weight: bold; 
				font-size: 13px; 
				cursor: pointer;">사업소득/기타소득</button> -->
					</div>

					<!-- 우측 기능 버튼 -->
					<div style="display: flex; gap: 5px; margin-bottom: 5px;">

						<button type="button" class="btn btn-dark btn-xs"
							onclick="openTipModal()"
							style="background: #333; color: #fff; border: none; padding: 3px 8px;">
							<i class="fas fa-question-circle"></i> Tip
						</button>
					</div>
				</div>

				<!-- 폼 시작 -->
				<form action="${pageContext.request.contextPath}/payroll/save.do"
					method="POST" id="payrollDetailForm">
					<input type="hidden" name="payrollEmployeeId"
						id="selectedPayrollEmployeeId"
						value="${selectedEmployee.payrollEmployeeId}">

					<div style="display: flex; gap: 10px;">
						<!-- 지급항목 테이블 -->
						<div style="flex: 1;">
							<div
								style="display: flex; justify-content: space-between; align-items: center; background: #f4f6f9; padding: 4px 8px; border: 1px solid #ddd; font-weight: bold;">
								<span>지급항목 <span
									style="background: #009688; color: white; font-size: 9px; padding: 1px 4px; border-radius: 2px;">M</span></span>
								<!--<button type="button" class="btn btn-xs btn-dark"
						onclick="executeAutoCalculation()"
						style="background: #222; color: #fff; font-size: 10px; padding: 1px 6px; border: none;">±
						자동계산</button>-->
							</div>
							<table class="table table-bordered table-condensed"
								style="width: 100%; border-collapse: collapse; margin-bottom: 0;">
								<tr>
									<td style="padding: 4px; background: #fafafa; width: 40%;">기본급</td>
									<!-- [수정] 클래스에 pay-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="baseWage" id="input_baseWage" value="0"
										oninput="calculateRealPay()"
										class="form-control input-sm pay-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">식비<span
										style="color: red;">[비]</span></td>
									<!-- [수정] 클래스에 pay-input 추가 (계산에는 포함되어야 함) -->
									<td style="padding: 4px;"><input type="text"
										name="mealAllowance" id="input_mealAllowance" value="200000"
										class="form-control input-sm pay-input" readonly
										style="width: 100%; height: 24px; padding: 2px; text-align: right; background-color: #e9ecef; cursor: not-allowed;">
									</td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background-color: #e9ecef; cursor: not-allowed;">
									</td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">보육수당</td>
									<!-- [수정] 클래스에 pay-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="childAllowance" id="input_childAllowance" value="0"
										oninput="calculateRealPay()"
										class="form-control input-sm pay-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">직책수당</td>
									<!-- [수정] 클래스에 pay-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="positionAllowance" id="input_positionAllowance"
										value="0" oninput="calculateRealPay()"
										class="form-control input-sm pay-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">차량유지비<span
										style="color: red;">[비]</span></td>
									<!-- [수정] 클래스에 pay-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="carAllowance" id="input_carAllowance" value="0"
										oninput="calculateRealPay()"
										class="form-control input-sm pay-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">근속수당</td>
									<!-- [수정] 클래스에 pay-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="tenureAllowance" id="input_tenureAllowance" value="0"
										oninput="calculateRealPay()"
										class="form-control input-sm pay-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">당직수당</td>
									<!-- [수정] 클래스에 pay-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="nightAllowance" id="input_nightAllowance" value="0"
										oninput="calculateRealPay()"
										class="form-control input-sm pay-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">상여금</td>
									<!-- [수정] 클래스에 pay-input 추가 -->
									<td style="padding: 4px;"><input type="text" name="bonus"
										id="input_bonus" value="0" oninput="calculateRealPay()"
										class="form-control input-sm pay-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">휴일수당</td>
									<!-- [수정] 클래스에 pay-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="holidayAllowance" id="input_holidayAllowance" value="0"
										oninput="calculateRealPay()"
										class="form-control input-sm pay-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>
							</table>
						</div>

						<!-- 공제항목 테이블 -->
						<div style="flex: 1;">
							<div
								style="background: #f4f6f9; padding: 4px 8px; border: 1px solid #ddd; font-weight: bold; display: flex; justify-content: space-between; align-items: center;">
								<span>공제항목 <span
									style="background: #d9534f; color: white; font-size: 9px; padding: 1px 4px; border-radius: 2px;">M</span></span>
							</div>
							<table class="table table-bordered table-condensed"
								style="width: 100%; border-collapse: collapse; margin-bottom: 0;">
								<tr>
									<td style="padding: 4px; background: #fafafa; width: 40%;">국민연금</td>
									<!-- [수정] 클래스에 deduction-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="nationalPension" id="input_nationalPension" value="0"
										oninput="calculateRealPay()"
										class="form-control input-sm deduction-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">건강보험</td>
									<!-- [수정] 클래스에 deduction-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="healthInsurance" id="input_healthInsurance" value="0"
										oninput="calculateRealPay()"
										class="form-control input-sm deduction-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">장기요양보험</td>
									<!-- [수정] 클래스에 deduction-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="longTermCare" id="input_longTermCare" value="0"
										oninput="calculateRealPay()"
										class="form-control input-sm deduction-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">고용보험</td>
									<!-- [수정] 클래스에 deduction-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="employmentInsurance" id="input_employmentInsurance"
										value="0" oninput="calculateRealPay()"
										class="form-control input-sm deduction-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">소득세</td>
									<!-- [수정] 클래스에 deduction-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="incomeTax" id="input_incomeTax" value="0"
										oninput="calculateRealPay()"
										class="form-control input-sm deduction-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">지방소득세</td>
									<!-- [수정] 클래스에 deduction-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="localIncomeTax" id="input_localIncomeTax" value="0"
										oninput="calculateRealPay()"
										class="form-control input-sm deduction-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td style="padding: 4px; background: #fafafa;">상조회비</td>
									<!-- [수정] 클래스에 deduction-input 추가 -->
									<td style="padding: 4px;"><input type="text"
										name="mutualAid" id="input_mutualAid" value="0"
										oninput="calculateRealPay()"
										class="form-control input-sm deduction-input"
										style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
								</tr>
								<tr class="calc-method-row">
									<td
										style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
									<td style="padding: 4px;"><input type="text" readonly
										class="form-control input-sm"
										style="width: 100%; height: 22px; background: #f9f9f9;"></td>
								</tr>

								<tr>
									<td colspan="2"
										style="background: #fff; border: none; height: 48px;"></td>
								</tr>
							</table>
						</div>
					</div>

					<!-- 합계 및 버튼 영역 -->
					<div
						style="display: flex; border: 1px solid #ddd; margin-top: 10px; font-weight: bold; text-align: center;">
						<div
							style="flex: 1; background: #f4f6f9; padding: 6px; border-right: 1px solid #ddd; font-size: 13px;">
							<!-- [수정] JS에서 값을 바꿀 수 있도록 id="totalPayResult" 로 변경 -->
							지급총액 : <span id="totalPayResult" style="color: #337ab7;"><fmt:formatNumber
									value="${selectedEmployee.totalPayAmount != null ? selectedEmployee.totalPayAmount : 0}"
									pattern="#,###" /></span> 원
						</div>
						<div
							style="flex: 1; background: #f4f6f9; padding: 6px; font-size: 13px;">
							<!-- [수정] JS에서 값을 바꿀 수 있도록 id="totalDeductionResult" 로 변경 -->
							공제총액 : <span id="totalDeductionResult" style="color: #d9534f;"><fmt:formatNumber
									value="${selectedEmployee.totalDeductionAmount != null ? selectedEmployee.totalDeductionAmount : 0}"
									pattern="#,###" /></span> 원
						</div>
					</div>
					<div
						style="background: #204d74; color: white; text-align: center; padding: 10px; font-weight: bold; font-size: 15px; margin-top: 5px; border-radius: 2px;">
						<!-- [수정] JS에서 값을 바꿀 수 있도록 실지급액 숫자를 감싸는 span 태그 추가 -->
						실지급액 : <span id="netPayResult"> <fmt:formatNumber
								value="${selectedEmployee.netPayAmount != null ? selectedEmployee.netPayAmount : 0}"
								pattern="#,###" />
						</span> 원
					</div>

					<div
						style="text-align: right; margin-top: 10px; display: flex; justify-content: flex-end; gap: 5px;">
						<button type="submit" class="btn btn-primary btn-sm"
							style="background: #337ab7; color: white; border: none; padding: 6px 18px; font-weight: bold;">저장</button>
						<button type="button" class="btn btn-default btn-sm"
							onclick="clearPayrollForm()"
							style="background: #ccc; color: #333; border: 1px solid #bbb; padding: 6px 15px;">내용
							지우기</button>
					</div>
				</form>
			</div>

		</div>

		<!-- 4. 하단 급여 종합정보 집계 영역 -->
		<div style="margin-top: 25px;">
			<div style="font-weight: bold; margin-bottom: 8px; font-size: 14px;">급여
				종합정보</div>
			<div style="display: flex; gap: 10px;">
				<div
					style="flex: 1; background: #95a5a6; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">월 합계</div>
					<div style="font-size: 20px; font-weight: bold; margin-top: 5px;">${summaryInfo.totalCount}
						건</div>
				</div>
				<div
					style="flex: 2; background: #5bc0de; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">지급 총액</div>
					<div style="font-size: 20px; font-weight: bold; margin-top: 5px;">
						<fmt:formatNumber value="${summaryInfo.totalGiveAmount}"
							pattern="#,###" />
						원
					</div>
				</div>
				<div
					style="flex: 2; background: #d9534f; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">공제 총액</div>
					<div style="font-size: 20px; font-weight: bold; margin-top: 5px;">
						<fmt:formatNumber value="${summaryInfo.totalDeduAmount}"
							pattern="#,###" />
						원
					</div>
				</div>
				<div
					style="flex: 2; background: #4e5d6c; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">실지급액</div>
					<div style="font-size: 20px; font-weight: bold; margin-top: 5px;">
						<fmt:formatNumber value="${summaryInfo.totalRealAmount}"
							pattern="#,###" />
						원
					</div>
				</div>
			</div>
		</div>
	</main>
	<!-- ================= [Tip 모달창 시작] ================= -->
	<div id="tipModalOverlay"
		style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); z-index: 9999; align-items: center; justify-content: center;">
		<div
			style="background: white; width: 550px; max-height: 80vh; border-radius: 5px; overflow: hidden; display: flex; flex-direction: column; box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5); font-family: 'Malgun Gothic', sans-serif;">

			<!-- 모달 헤더 -->
			<div
				style="background: #333; color: white; padding: 10px 15px; display: flex; justify-content: space-between; align-items: center;">
				<span style="font-weight: bold; font-size: 14px;">Payzon Tip</span>
				<button type="button" onclick="closeTipModal()"
					style="background: transparent; border: none; color: white; font-size: 20px; cursor: pointer; line-height: 1;">×</button>
			</div>

			<!-- 모달 본문 (스크롤 영역) -->
			<div style="padding: 20px; overflow-y: auto; flex: 1;">
				<h3
					style="color: #e82c6d; margin-top: 0; margin-bottom: 20px; font-weight: bold; font-size: 18px;">급여입력/관리</h3>

				<!-- 설명 항목 반복 템플릿 스타일 -->
				<style>
.tip-item {
	margin-bottom: 18px;
}

.tip-title {
	font-weight: bold;
	font-size: 13px;
	margin-bottom: 6px;
	display: flex;
	align-items: center;
	gap: 6px;
}

.tip-num {
	background: #fb6a7e;
	color: white;
	padding: 2px 6px;
	border-radius: 4px;
	font-size: 11px;
}

.tip-desc {
	font-size: 12px;
	color: #555;
	line-height: 1.6;
	padding-left: 25px;
	word-break: keep-all;
}
</style>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">1</span> 귀속연월
					</div>
					<div class="tip-desc">급여를 계산의 대상이 되는 연도와 월을 선택합니다.</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">2</span> 급여차수
					</div>
					<div class="tip-desc">기본값으로 [급여-1차]로 표기되며, 한 달에 급여를 분할하여 지급할
						경우 최대 급여-5차까지 나누어 급여계산 및 급여테이블을 생성하실 수 있습니다.</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">3</span> 정산기간
					</div>
					<div class="tip-desc">[사용자 정보] 메뉴에서 설정한 급여 산정기간에 따라 정산기간이 자동
						표시되며, 정산기간의 변동이 있을 경우 사용자가 직접 수정 입력할 수 있습니다.</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">4</span> 급여지급일
					</div>
					<div class="tip-desc">선택한 귀속연월의 급여가 실제로 지급되는 급여지급일을 선택합니다.</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">5</span> [지난급여 불러오기]
					</div>
					<div class="tip-desc">이전 월의 급여정보를 선택한 귀속연월로 동일하게 불러오기 합니다.
						급여정보가 동일하거나 변동이 크지 않을 경우 이전 월의 급여정보를 불러오기 하여 바로 적용할 수 있으며, 변동사항만
						수정 입력할 수 있습니다.</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">6</span> [신규추가]
					</div>
					<div class="tip-desc">[신규추가] 버튼을 클릭하시면 사원 리스트 레이어가 표시됩니다. 여기서
						급여를 계산하고자 하는 대상 사원들을 체크하신 후 하단의 [사원선택] 버튼을 클릭하시면 레이어가 닫힌 후 급여정보
						리스트에 추가됩니다.</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">7</span> [전체삭제]
					</div>
					<div class="tip-desc">선택한 귀속연월의 급여정보 리스트를 모두 삭제합니다. 리스트에서 삭제된
						급여정보는 복구할 수 없으며, [신규추가] 또는 [지난급여 불러오기] 등으로 새로 급여정보를 등록할 수 있습니다.</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">8</span> [삭제]
					</div>
					<div class="tip-desc">선택한 귀속연월의 급여정보 리스트에서 삭제하고자 하는 급여정보만
						선택하여 삭제합니다. 리스트에서 삭제된 급여정보는 복구할 수 없으며, [신규추가]를 통하여 새로 급여정보를 등록할 수
						있습니다.</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">9</span> 급여정보 리스트
					</div>
					<div class="tip-desc">급여정보 리스트에서 급여정보를 입력할 대상을 선택합니다. 우측
						급여입력란에 선택한 사원의 급여를 입력하여 급여정보를 저장할 수 있습니다. 이미 급여정보가 저장된 사원을 선택하였을
						경우 우측에 저장된 급여정보가 표시되어 확인 및 수정 입력하실 수 있습니다.</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">10</span> 일반소득 탭
					</div>
					<div class="tip-desc">일반소득 탭은 사원등록 시에 갑근세 설정에서 [근로소득자
						갑근세(근로소득간이세액표)]로 설정한 근로자들이 해당되며, 일반소득 탭에서는 근로소득간이세액표에 근거하여 갑근세
						공제액을 계산합니다.</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">11</span> [자동계산]
					</div>
					<div class="tip-desc">지급항목에 해당하는 금액을 모두 입력하신 후 [자동계산] 버튼을
						클릭하시면 4대보험 및 갑근세를 자동으로 계산하여 금액을 표시합니다.</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">12</span> [저장]
					</div>
					<div class="tip-desc">지급액 및 공제액을 입력하고 실지급액이 정상적으로 계산되었을 경우
						[저장] 버튼을 클릭하시면 왼쪽에 급여정보 리스트에 입력한 급여정보가 저장되면서 리스트가 갱신됩니다. 이미 급여정보가
						등록된 사원의 경우 새로 입력한 급여정보로 수정됩니다.</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">13</span> [내용지우기]
					</div>
					<div class="tip-desc">입력한 지급액 및 공제액을 모두 지우고 새로 작성할 수 있습니다.</div>
				</div>
			</div>
		</div>
	</div>
	<!-- ================= [Tip 모달창 끝] ================= -->


	<!-- 스크립트 기능 정의 -->
	<script>
		function reloadPayrollData() {
			document.getElementById("searchForm").submit();
		}

		// ★ [핵심 수정] 좌측 사원 클릭 시 AJAX로 우측 데이터 갱신하기
		function selectEmployeeRow(rowElement, payrollEmployeeId) {
			// 1. 선택된 행 색상 변경 (기존 로직 유지)
			var rows = document.querySelectorAll("#employeeTableBody tr");
			if(rows.length > 0) {
				rows.forEach(function(r) { r.style.background = ""; });
			}
			rowElement.style.background = "#eef4fb";
			
			var selectedIdInput = document.getElementById("selectedPayrollEmployeeId");
			if(selectedIdInput) selectedIdInput.value = payrollEmployeeId;

			// 2. AJAX 비동기 통신으로 상세 데이터 요청 (화면 새로고침 없음)
			var contextPath = "${pageContext.request.contextPath}";
			var url = contextPath + "/payroll/detailAjax.do?payrollEmployeeId=" + payrollEmployeeId;

			fetch(url)
				.then(response => response.json())
				.then(data => {
					console.log("서버에서 성공적으로 가져온 데이터:", data);
					calculateRealPay();
				})
				.catch(error => {
					console.error("데이터를 가져오는 중 오류 발생:", error);
				});
		}

		function toggleCalcMethod() {
			var badge = document.getElementById("calcSwitchBadge");
			var rows = document.querySelectorAll(".calc-method-row");
			if (badge.innerText === "on") {
				badge.innerText = "off";
				badge.classList.add("off");
				rows.forEach(function(r) { r.classList.add("off"); });
			} else {
				badge.innerText = "on";
				badge.classList.remove("off");
				rows.forEach(function(r) { r.classList.remove("off"); });
			}
		}

		function calculateRealPay() {
	        let totalPay = 0;
	        let totalDeduction = 0;

	        const payInputs = document.querySelectorAll('.pay-input');
	        payInputs.forEach(function(input) {
	            totalPay += parseInt(input.value) || 0;
	        });

	        const deductionInputs = document.querySelectorAll('.deduction-input');
	        deductionInputs.forEach(function(input) {
	            totalDeduction += parseInt(input.value) || 0;
	        });

	        const netPay = totalPay - totalDeduction;

	        const payResultElem = document.getElementById('totalPayResult');
	        const deduResultElem = document.getElementById('totalDeductionResult');
	        const netResultElem = document.getElementById('netPayResult');

	        if (payResultElem) payResultElem.innerText = totalPay.toLocaleString();
	        if (deduResultElem) deduResultElem.innerText = totalDeduction.toLocaleString();
	        if (netResultElem) netResultElem.innerText = netPay.toLocaleString();
	    }

	    function clearPayrollForm() {
	        const form = document.getElementById("payrollDetailForm");
	        if (form) {
	            form.reset();
	            calculateRealPay();
	        }
	    }

	    function openEmployeeSelectModal() {
	        var contextPath = "${pageContext.request.contextPath}";
	        var popupUrl = contextPath + "/Payment/employeeAddModal.do";
	        window.open(popupUrl, "EmpSelectModal", "width=700,height=600,left=200,top=100,scrollbars=yes");
	    }

	    function addEmployeesToMain(selectedEmpIds) {
	        console.log("추가된 사원 ID 목록:", selectedEmpIds);
	        alert("선택된 사원이 목록에 추가되었습니다.");
	    }
		
	    function openTipModal() {
	        document.getElementById('tipModalOverlay').style.display = 'flex';
	    }

	    function closeTipModal() {
	        document.getElementById('tipModalOverlay').style.display = 'none';
	    }
	    
	 // 금액 입력칸 관련 자동화 스크립트
	    document.querySelectorAll('.pay-input, .deduction-input').forEach(function(input) {
	        
	        // 1. 문자 입력 방지 (숫자만 남기기)
	        input.addEventListener('input', function() {
	            this.value = this.value.replace(/[^0-9]/g, '');
	        });

	        // 2. [추가] 클릭(포커스) 시 기본값 '0' 전체 블록 지정하기
	        input.addEventListener('focus', function() {
	            this.select(); // 입력칸 안의 텍스트를 전체 선택합니다!
	        });
	        
	    });
	</script>
	<script>
    // 1. [선택삭제] : 마우스로 클릭해서 배경색이 바뀐 행을 삭제하는 함수
    function deleteSelectedEmployees() {
        // 선택된 ID가 들어있는 히든 인풋 확인
        var selectedIdInput = document.getElementById("selectedPayrollEmployeeId");
        
        // 배경색이 바뀌어 있는(#eef4fb 계열) 행 찾기
        var targetRow = document.querySelector("#employeeTableBody tr[style*='background']");
        
        // 만약 선택된 것이 없다면
        if (!targetRow && (!selectedIdInput || !selectedIdInput.value)) {
            alert("삭제할 사원을 선택해주세요.");
            return;
        }

        // 1번 사진 같은 확인창 띄우기
        if (confirm("선택된 사원을 삭제 하시겠습니까?")) {
            // 화면에서 해당 행 삭제
            if (targetRow) {
                targetRow.remove();
            } else {
                // 만약 스타일로 못 찾았을 경우 #employeeTableBody 안의 모든 행을 돌며 처리
                var rows = document.querySelectorAll("#employeeTableBody tr");
                // 여기서는 안전하게 배경색이 들어간 행을 다시 탐색
                var coloredRow = document.querySelector("#employeeTableBody tr[style*='background']");
                if (coloredRow) coloredRow.remove();
            }

            // 히든 인풋 값 초기화
            if (selectedIdInput) selectedIdInput.value = "";
            
            alert("선택된 사원이 삭제되었습니다.");
            
            // (필요시 여기서 우측 급여 입력폼 데이터도 초기화하거나 비우는 로직 추가 가능)
        }
    }

    // 2. [전체삭제] : 2단계 경고창을 거쳐 전체 사원 목록을 싹 지우는 함수
    function deleteAllEmployees() {
        var rows = document.querySelectorAll("#employeeTableBody tr");
        
        if (rows.length === 0) {
            alert("삭제할 사원 정보가 없습니다.");
            return;
        }

        // 3번째 사진 같은 1차 경고창
        if (confirm("■■ 주의!! ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■\n■ 삭제된 급여입력 정보는 복구할 수 없습니다. ■\n■ 삭제 하시겠습니까? ■\n■■■메이션■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■")) {
            
            // 2번째 사진 같은 2차 최종 경고창
            if (confirm("■ [전체] 급여입력 정보를 삭제 하시겠습니까?")) {
                
                // 테이블의 모든 사원 행 삭제
                rows.forEach(function(r) {
                    r.remove();
                });

                // 선택된 ID 히든 인풋 초기화
                var selectedIdInput = document.getElementById("selectedPayrollEmployeeId");
                if (selectedIdInput) selectedIdInput.value = "";

                alert("전체 사원 및 급여 정보가 삭제되었습니다.");
            }
        }
    }
</script>
	

	<%@ include file="../../jspf/app-end.jspf"%>
</body>
</html>