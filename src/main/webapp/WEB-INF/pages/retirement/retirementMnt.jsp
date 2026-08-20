<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
request.setAttribute("pageTitle", "퇴직급여 입력/관리");
request.setAttribute("pageSection", "퇴직관리");
request.setAttribute("pageDescription", "퇴직자의 최근 3개월 급여를 불러와 평균임금과 퇴직급여를 계산·저장합니다.");
request.setAttribute("activeKey", "retirement-pay");
request.setAttribute("pageCss", "retirement.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<!-- 1. 상단 검색 영역 (Filter Bar) -->
<section class="filter-bar">
	<form action="" method="get" id="searchForm" style="display: contents;">
		<div class="field ">
			<label>퇴직연도</label> <select class="select" name="retirementYear"
				onchange="this.form.submit()">
				<option value="">전체</option>
				<c:set var="currentYear"
					value="<%=java.time.Year.now().getValue()%>" />
				<c:forEach var="i" begin="0" end="4">
					<c:set var="y" value="${currentYear - i}" />
					<option value="${y}"
						<c:if test="${y eq retirementYear}">selected</c:if>>${y}</option>
				</c:forEach>
			</select>
		</div>
		<div class="field ">
			<label>사원</label> <select class="select" name="employeeId"
				onchange="this.form.submit()">
				<option value="">전체보기</option>
				<c:forEach var="emp" items="${retiredEmpList}">
					<option value="${emp.employeeId}"
						<c:if test="${emp.employeeId eq employeeId}">selected</c:if>>
						${emp.employeeName} (${emp.employeeNo})</option>
				</c:forEach>
			</select>
		</div>
		<div class="actions">
			<button type="button" class="btn " onclick="location.href='?'">초기화</button>
		</div>
	</form>
</section>

<div class="page-grid two">
	<!-- 2. 좌측: 퇴직급여 대상 목록 -->
	<section class="card ">
		<div class="card-header">
			<h2 class="section-title">퇴직급여 대상 목록</h2>
		</div>
		<div class="card-body">
			<div class="table-wrap">
				<table class="data-table list-table">
					<thead>
						<tr>
							<th>성명</th>
							<th>입사일</th>
							<th>퇴직일</th>
							<th>상태</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="pay" items="${payList}">
							<tr style="cursor: pointer;"
								onclick="selectEmployee('${pay.employeeId}', '${pay.hireDate}', '${pay.resignDate}')">
								<td>${pay.employeeName}</td>
								<td>${pay.hireDate}</td>
								<td>${pay.resignDate}</td>
								<td><c:choose>
										<c:when test="${pay.retirementSettlementYn eq 'Y'}">
                                            확정
                                        </c:when>
										<c:otherwise>
                                            작성 전
                                        </c:otherwise>
									</c:choose></td>
							</tr>
						</c:forEach>
						<c:if test="${empty payList}">
							<tr>
								<td colspan="4" style="text-align: center;">조회된 대상자가 없습니다.</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</div>
			<!-- 선택 삭제, 전체 삭제 버튼 영역 완전 제거 됨 -->
		</div>
	</section>

	<!-- 3. 우측: 퇴직급여 계산 -->
	<section class="card ">
		<div class="card-header">
			<h2 class="section-title">퇴직급여 계산</h2>
		</div>
		<div class="card-body">
			<form action="/retirement/insert.do" method="post" id="calcForm">

				<input type="hidden" name="employeeId" id="selectedEmployeeId"
					value=""> <input type="hidden" name="serviceDays"
					id="serviceDays" value="0"> <input type="hidden"
					name="totalWageAmount" id="totalWageAmount" value="0"> <input
					type="hidden" name="averageDailyWage" id="averageDailyWage"
					value="0"> <input type="hidden" name="retirementPayAmount"
					id="retirementPayAmount" value="0">

				<div class="form-grid cols-2">
					<div class="field ">
						<label>입사일</label> <input type="date" class="input"
							id="calcHireDate" readonly>
					</div>
					<div class="field ">
						<label>퇴직일</label> <input type="date" class="input"
							id="calcResignDate" name="resignDate" readonly>
					</div>
				</div>

				<div class="table-wrap">
					<table class="data-table ">
						<thead>
							<tr>
								<th>최근 3개월</th>
								<th>지급총액</th>
								<th>일수</th>
							</tr>
						</thead>
						<tbody id="wageTableBody">
							<tr>
								<td colspan="3" style="text-align: center; color: #999;">좌측
									목록에서 사원을 선택해 주세요.</td>
							</tr>
						</tbody>
					</table>
				</div>

				<div class="calc-box">
					<div class="calc-line">
						<span>3개월 임금총액</span><strong id="displayTotalWage">0원</strong>
					</div>
					<div class="calc-line">
						<span>1일 평균임금</span><strong id="displayAvgWage">0원</strong>
					</div>
					<div class="calc-line total">
						<span>퇴직급여</span><strong id="displayRetirementPay">0원</strong>
					</div>
				</div>

				<div class="button-row right">
					<button type="button" class="btn " onclick="fetchRecent3Months()">최근
						3개월 급여 불러오기</button>
					<button type="submit" class="btn btn-primary"
						onclick="return validateForm()">저장</button>
				</div>
			</form>
		</div>
	</section>
</div>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>

<script>
	function selectEmployee(empId, hireDate, resignDate) {
		document.getElementById('selectedEmployeeId').value = empId;
		document.getElementById('calcHireDate').value = hireDate;
		document.getElementById('calcResignDate').value = resignDate;

		document.getElementById('wageTableBody').innerHTML = '<tr><td colspan="3" style="text-align:center; color:#999;">[최근 3개월 급여 불러오기]를 클릭하세요.</td></tr>';
		resetCalcValues();
	}

	function resetCalcValues() {
		document.getElementById('serviceDays').value = "0";
		document.getElementById('totalWageAmount').value = "0";
		document.getElementById('averageDailyWage').value = "0";
		document.getElementById('retirementPayAmount').value = "0";

		document.getElementById('displayTotalWage').innerText = "0원";
		document.getElementById('displayAvgWage').innerText = "0원";
		document.getElementById('displayRetirementPay').innerText = "0원";
	}

	function fetchRecent3Months() {
		var empId = document.getElementById('selectedEmployeeId').value;
		var resignDate = document.getElementById('calcResignDate').value;

		if (!empId) {
			alert("먼저 좌측 목록에서 사원을 선택해주세요.");
			return;
		}

		var xhr = new XMLHttpRequest();
		xhr
				.open(
						"POST",
						"${pageContext.request.contextPath}/Retirement/retirementMntPay.do",
						true);
		xhr.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded");
		xhr.onreadystatechange = function() {
			if (xhr.readyState === 4 && xhr.status === 200) {
				var responseText = xhr.responseText.trim();

				if (responseText === "") {
					alert("최근 3개월 급여 내역이 존재하지 않습니다.");
					return;
				}

				var tbody = document.getElementById('wageTableBody');
				tbody.innerHTML = "";

				var totalWage = 0;
				var totalDays = 0; // 고정 90일 대신 실제 일수가 누적될 변수

				var months = responseText.split("|");

				for (var i = 0; i < months.length; i++) {
					var data = months[i].split(",");
					var wageMonth = data[0];
					var payAmount = parseInt(data[1]);

					totalWage += payAmount;

					// [추가] 해당 월의 실제 마지막 일자 계산 
					var parts = wageMonth.split("-");
					var year = parseInt(parts[0]);
					var month = parseInt(parts[1]);
					var daysInMonth = new Date(year, month, 0).getDate();

					totalDays += daysInMonth; // 3개월간의 총 일수 합산

					// 표에 행 추가 (실제 월별 일수 출력)
					var tr = document.createElement('tr');
					tr.innerHTML = "<td>" + wageMonth + "</td>" + "<td>"
							+ payAmount.toLocaleString() + "</td>" + "<td>"
							+ daysInMonth + "</td>";
					tbody.appendChild(tr);
				}

				// [3개월간의 실제 총 일수(totalDays)로 나누어 1일 평균임금 계산
				var avgWage = Math.floor(totalWage / totalDays);

				var hireDateVal = document.getElementById('calcHireDate').value;
				var resignDateVal = document.getElementById('calcResignDate').value;

				var hireDateObj = new Date(hireDateVal);
				var resignDateObj = new Date(resignDateVal);

				// 밀리초(ms) 차이를 구한 뒤 일(day) 수로 환산 (+1은 퇴직일 당일 포함)
				var diffTime = resignDateObj.getTime() - hireDateObj.getTime();
				var serviceDays = Math.floor(diffTime / (1000 * 60 * 60 * 24)) + 1;

				if (isNaN(serviceDays) || serviceDays < 0) {
					serviceDays = 0;
				}

				// 법정 퇴직금 공식: 1일 평균임금 × 30일 × (근속일수 / 365)
				var retirementPay = Math.floor(avgWage * 30
						* (serviceDays / 365));

				// 화면 출력 갱신
				document.getElementById('displayTotalWage').innerText = totalWage
						.toLocaleString()
						+ "원";
				document.getElementById('displayAvgWage').innerText = avgWage
						.toLocaleString()
						+ "원";
				document.getElementById('displayRetirementPay').innerText = retirementPay
						.toLocaleString()
						+ "원";

				// Form Submit(저장) 용도 Hidden 값 세팅
				document.getElementById('serviceDays').value = serviceDays;
				document.getElementById('totalWageAmount').value = totalWage;
				document.getElementById('averageDailyWage').value = avgWage;
				document.getElementById('retirementPayAmount').value = retirementPay;
			}
		};

		xhr.send("employeeId=" + empId + "&resignDate=" + resignDate);
	}

	function validateForm() {
		if (!document.getElementById('selectedEmployeeId').value) {
			alert("사원을 선택하고 급여를 불러온 뒤 저장해주세요.");
			return false;
		}
		if (document.getElementById('totalWageAmount').value === "0") {
			alert("급여 불러오기를 완료해야 저장할 수 있습니다.");
			return false;
		}
		return confirm("해당 퇴직급여 내역을 저장하시겠습니까?");
	}
</script>