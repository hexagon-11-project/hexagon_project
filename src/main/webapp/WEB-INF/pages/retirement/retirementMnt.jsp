<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
request.setAttribute("pageTitle", "退職給与入力/管理");
request.setAttribute("pageSection", "退職管理");
request.setAttribute("pageDescription", "退職者の直近3ヶ月の給与を読み込み、平均賃金と退職給与を計算・保存します。");
request.setAttribute("activeKey", "retirement-pay");
request.setAttribute("pageCss", "retirement.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<!-- 상단 검색 영역 (Filter Bar) -->
<!-- 上段検索領域 (Filter Bar) -->
<section class="filter-bar">
	<form action="" method="get" id="searchForm" style="display: contents;">
		<div class="field ">
			<label>退職年度</label> <select class="select" name="retirementYear"
				onchange="this.form.submit()">
				<option value="">全体</option>
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
			<label>社員</label> <select class="select" name="employeeId"
				onchange="this.form.submit()">
				<option value="">全件表示</option>
				<c:forEach var="emp" items="${retiredEmpList}">
					<option value="${emp.employeeId}"
						<c:if test="${emp.employeeId eq employeeId}">selected</c:if>>
						${emp.employeeName} (${emp.employeeNo})</option>
				</c:forEach>
			</select>
		</div>
		<div class="actions">
			<button type="button" class="btn " onclick="location.href='?'">初期化</button>
		</div>
	</form>
</section>

<div class="page-grid two">
	<!--  퇴직급여 대상 목록 -->
	<!-- 退職給与対象リスト -->
	<section class="card ">
		<div class="card-header">
			<h2 class="section-title">退職給与対象リスト</h2>
		</div>
		<div class="card-body">
			<div class="table-wrap">
				<table class="data-table list-table">
					<thead>
						<tr>
							<th>氏名</th>
							<th>入社日</th>
							<th>退職日</th>
							<th>ステータス</th>
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
                                            確定
                                        </c:when>
										<c:otherwise>
                                            作成前
                                        </c:otherwise>
									</c:choose></td>
							</tr>
						</c:forEach>
						<c:if test="${empty payList}">
							<tr>
								<td colspan="4" style="text-align: center;">照会された対象者がいません。</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</div>
		</div>
	</section>

	<!-- 퇴직급여 계산 -->
	<!-- 退職給与計算 -->
	<section class="card ">
		<div class="card-header">
			<h2 class="section-title">退職給与計算</h2>
		</div>
		<div class="card-body">
			<form action="${pageContext.request.contextPath}/Retire/retirementMntInsert.do" method="post" id="calcForm">

				<input type="hidden" name="employeeId" id="selectedEmployeeId" value=""> 
				<input type="hidden" name="serviceDays" id="serviceDays" value="0"> 
				<input type="hidden" name="totalWageAmount" id="totalWageAmount" value="0"> 
				<input type="hidden" name="averageDailyWage" id="averageDailyWage" value="0"> 
				<input type="hidden" name="retirementPayAmount" id="retirementPayAmount" value="0">

				<div class="form-grid cols-2">
					<div class="field ">
						<label>入社日</label> 
						<!-- [수정] name="hireDate" 추가 -->
						<!-- [修正] name="hireDate" 追加 -->
						<input type="date" class="input" id="calcHireDate" name="hireDate" readonly>
					</div>
					<div class="field ">
						<label>退職日</label> 
						<input type="date" class="input" id="calcResignDate" name="resignDate" readonly>
					</div>
				</div>

				<div class="table-wrap">
					<table class="data-table ">
						<thead>
							<tr>
								<th>直近3ヶ月</th>
								<th>支給総額</th>
								<th>日数</th>
							</tr>
						</thead>
						<tbody id="wageTableBody">
							<tr>
								<td colspan="3" style="text-align: center; color: #999;">左側のリストから社員を選択してください。</td>
							</tr>
						</tbody>
					</table>
				</div>

				<div class="calc-box">
					<div class="calc-line">
						<span>3ヶ月賃金総額</span><strong id="displayTotalWage">0ウォン</strong>
					</div>
					<div class="calc-line">
						<span>1日平均賃金</span><strong id="displayAvgWage">0ウォン</strong>
					</div>
					<div class="calc-line total">
						<span>退職給与</span><strong id="displayRetirementPay">0ウォン</strong>
					</div>
				</div>

				<div class="button-row right">
					<button type="button" class="btn " onclick="fetchRecent3Months()">直近3ヶ月の給与を読み込む</button>
					<button type="submit" class="btn btn-primary"
						onclick="return validateForm()">保存</button>
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

		document.getElementById('wageTableBody').innerHTML = '<tr><td colspan="3" style="text-align:center; color:#999;">[直近3ヶ月の給与を読み込む]をクリックしてください。</td></tr>';
		resetCalcValues();
	}

	function resetCalcValues() {
		document.getElementById('serviceDays').value = "0";
		document.getElementById('totalWageAmount').value = "0";
		document.getElementById('averageDailyWage').value = "0";
		document.getElementById('retirementPayAmount').value = "0";

		document.getElementById('displayTotalWage').innerText = "0ウォン";
		document.getElementById('displayAvgWage').innerText = "0ウォン";
		document.getElementById('displayRetirementPay').innerText = "0ウォン";
	}

	function fetchRecent3Months() {
		var empId = document.getElementById('selectedEmployeeId').value;
		var resignDate = document.getElementById('calcResignDate').value;

		if (!empId) {
			alert("先に左側のリストから社員を選択してください。");
			return;
		}

		var xhr = new XMLHttpRequest();
		xhr.open(
			"POST",
			"${pageContext.request.contextPath}/Retire/retirementMntPay.do",
			true
		);
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		xhr.onreadystatechange = function() {
			if (xhr.readyState === 4 && xhr.status === 200) {
				var responseText = xhr.responseText.trim();

				if (responseText === "") {
					alert("直近3ヶ月の給与履歴が存在しません。");
					return;
				}

				var tbody = document.getElementById('wageTableBody');
				tbody.innerHTML = "";

				var totalWage = 0;
				var totalDays = 0; 

				var months = responseText.split("|");

				for (var i = 0; i < months.length; i++) {
					var data = months[i].split(",");
					var wageMonth = data[0];
					var payAmount = parseInt(data[1]);

					totalWage += payAmount;

					var parts = wageMonth.split("-");
					var year = parseInt(parts[0]);
					var month = parseInt(parts[1]);
					var daysInMonth = new Date(year, month, 0).getDate();

					totalDays += daysInMonth; 

					var tr = document.createElement('tr');
					tr.innerHTML = "<td>" + wageMonth + "</td>" + "<td>"
							+ payAmount.toLocaleString() + "</td>" + "<td>"
							+ daysInMonth + "</td>";
					tbody.appendChild(tr);
				}

				var avgWage = Math.floor(totalWage / totalDays);

				var hireDateVal = document.getElementById('calcHireDate').value;
				var resignDateVal = document.getElementById('calcResignDate').value;

				var hireDateObj = new Date(hireDateVal);
				var resignDateObj = new Date(resignDateVal);

				var diffTime = resignDateObj.getTime() - hireDateObj.getTime();
				var serviceDays = Math.floor(diffTime / (1000 * 60 * 60 * 24)) + 1;

				if (isNaN(serviceDays) || serviceDays < 0) {
					serviceDays = 0;
				}

				var retirementPay = Math.floor(avgWage * 30 * (serviceDays / 365));

				document.getElementById('displayTotalWage').innerText = totalWage.toLocaleString() + "ウォン";
				document.getElementById('displayAvgWage').innerText = avgWage.toLocaleString() + "ウォン";
				document.getElementById('displayRetirementPay').innerText = retirementPay.toLocaleString() + "ウォン";

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
			alert("社員を選択し、給与を読み込んでから保存してください。");
			return false;
		}
		if (document.getElementById('totalWageAmount').value === "0") {
			alert("給与の読み込みを完了しないと保存できません。");
			return false;
		}
		return confirm("該当の退職給与履歴を保存しますか？");
	}
	window.onload = function() {
	    var urlParams = new URLSearchParams(window.location.search);
	    
	    // 저장 성공 시 알림창 띄우기
	    // 保存成功時にアラートウィンドウを表示
	    if (urlParams.get('save') === 'success') {
	        alert("保存されました。");
	        
	        var cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
	        window.history.replaceState({path: cleanUrl}, '', cleanUrl);
	    }
	    
	    // error 값이 'dup'인지 확인 (중복 저장 방지)
	    // error値が'dup'か確認 (重複保存防止)
	    if (urlParams.get('error') === 'dup') {
	        alert("すでに保存されている履歴です。");
	        
	        var cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
	        window.history.replaceState({path: cleanUrl}, '', cleanUrl);
	    }
	};
</script>