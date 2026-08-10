<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- 1. 현재 시스템의 연도(yyyy)와 월(MM) 구하기 -->
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate value="${now}" pattern="yyyy" var="currentYear" />
<fmt:formatDate value="${now}" pattern="MM" var="currentMonth" />

<!-- 2. 검색 파라미터가 있으면 그 값을 유지하고, 없으면 현재 연/월을 기본값으로 세팅 -->
<c:set var="selectedYear" value="${not empty param.payYear ? param.payYear : currentYear}" />
<c:set var="selectedMonth" value="${not empty param.payMonth ? param.payMonth : currentMonth}" />

<form id="searchForm" action="${pageContext.request.contextPath}/payroll/input.do" method="GET">
	<input type="hidden" name="incomeType" id="incomeTypeParam" value="${not empty param.incomeType ? param.incomeType : 'GENERAL'}">

	<div style="background: #d9534f; padding: 10px 15px; border-radius: 4px; display: flex; align-items: center; justify-content: space-between; color: white; margin-bottom: 15px; font-size: 13px;">
		<!-- 왼쪽 그룹 -->
		<div style="display: flex; align-items: center; gap: 15px;">
			<div style="display: flex; align-items: center; gap: 5px;">
				<strong>* 귀속연월</strong>&nbsp;
				
				<!-- 연도 Select Box -->
				<select name="payYear" id="payYear" class="form-control input-sm" style="display: inline-block; width: 80px; background: #fff; color: #333; padding: 3px 5px;" onchange="reloadPayrollData()">
					<c:forEach var="year" begin="2005" end="2027">
						<option value="${year}" <c:if test="${selectedYear eq year}">selected</c:if>>${year}년</option>
					</c:forEach>
				</select>&nbsp;
				
				<!-- 월 Select Box -->
				<select name="payMonth" id="payMonth" class="form-control input-sm" style="display: inline-block; width: 65px; background: #fff; color: #333; padding: 3px 5px;" onchange="reloadPayrollData()">
					<c:forEach var="month" begin="1" end="12">
						<fmt:formatNumber value="${month}" pattern="00" var="formattedMonth" />
						<option value="${formattedMonth}" <c:if test="${selectedMonth eq formattedMonth}">selected</c:if>>${formattedMonth}월</option>
					</c:forEach>
				</select>
			</div>
			
			<div style="display: flex; align-items: center; gap: 5px;">
				<strong>* 급여차수</strong>&nbsp;
				<!-- 급여차수 Select Box (1차 ~ 10차 자동 생성) -->
				<select name="paySequence" id="paySequence" class="form-control input-sm" style="display: inline-block; width: 90px; background: #fff; color: #333; padding: 3px 5px;" onchange="reloadPayrollData()">
					<c:forEach var="seq" begin="1" end="10">
						<fmt:formatNumber value="${seq}" pattern="00" var="formattedSeq" />
						<option value="${formattedSeq}" <c:if test="${(empty param.paySequence and formattedSeq eq '01') or (param.paySequence eq formattedSeq)}">selected</c:if>>급여-${formattedSeq}차</option>
					</c:forEach>
				</select>
			</div>
		</div>

		<!-- 중간 그룹 -->
		<div style="display: flex; align-items: center; gap: 15px;">
			<div>
				<strong>* 정산기간</strong>&nbsp;
				<!-- id="calcPeriodStart" 와 id="calcPeriodEnd" 추가 -->
				<input type="text" id="calcPeriodStart" value="${payrollInfo.calcPeriodStart}" readonly class="form-control input-sm" style="display: inline-block; width: 95px; background: #fff; color: #333; padding: 3px 5px; text-align: center;"> ~&nbsp;
				<input type="text" id="calcPeriodEnd" value="${payrollInfo.calcPeriodEnd}" readonly class="form-control input-sm" style="display: inline-block; width: 95px; background: #fff; color: #333; padding: 3px 5px; text-align: center;">
			</div>
			<div>
				<strong>* 급여지급일</strong>&nbsp;
				<!-- id="payDate" 추가 -->
				<input type="text" id="payDate" value="${payrollInfo.payDate}" readonly class="form-control input-sm" style="display: inline-block; width: 95px; background: #fff; color: #333; padding: 3px 5px; text-align: center;">
				<button type="button" class="btn btn-default btn-xs" style="padding: 3px 6px; background: #fff; border: 1px solid #ccc; color: #333; margin-left: 2px;"><i class="fas fa-sync-alt"></i> 수정</button>
			</div>
		</div>

		<!-- 오른쪽 그룹 (계산방법) -->
		<div class="switch-wrap" onclick="toggleCalcMethod()">
			<strong style="font-size: 12px; color: #333;">* 계산방법</strong>
			<span id="calcSwitchBadge" class="switch-btn">on</span>
		</div>
	</div>
</form>

<div style="margin-bottom: 10px; display: flex; gap: 5px;">
	<button type="button" class="btn btn-default" style="background: #fff; border: 1px solid #ccc; padding: 4px 10px; font-size: 12px;"><i class="fas fa-file-import"></i> 지난급여 불러오기</button>
	<button type="button" class="btn btn-primary" onclick="openEmployeeSelectModal()" style="background: #337ab7; color: #fff; border: none; padding: 4px 10px; font-size: 12px;"><i class="fas fa-plus"></i> 신규추가</button>
	<button type="button" class="btn btn-default" style="background: #fff; border: 1px solid #ccc; padding: 4px 10px; font-size: 12px;"><i class="fas fa-trash-alt"></i> 선택삭제</button>
	<button type="button" class="btn btn-danger" style="background: #d9534f; color: #fff; border: none; padding: 4px 10px; font-size: 12px;"><i class="fas fa-trash"></i> 전체삭제</button>
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