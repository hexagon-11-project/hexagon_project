<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setAttribute("pageTitle", "급여항목 구성 통계");
request.setAttribute("pageSection", "급여통계");
request.setAttribute("pageDescription", "귀속연월·사원별 지급항목 금액과 구성비를 원형 그래프와 표로 확인합니다.");
request.setAttribute("activeKey", "item-composition");
request.setAttribute("pageCss", "statistics.css");
request.setAttribute("pageJs", "charts.js");

Integer selectedYear = (Integer) request.getAttribute("year");
Integer selectedMonth = (Integer) request.getAttribute("month");
java.time.YearMonth selectedYearMonth = (selectedYear != null && selectedMonth != null)
		? java.time.YearMonth.of(selectedYear, selectedMonth)
		: java.time.YearMonth.now();
String yearMonthValue = selectedYearMonth.toString();
String employeeNameValue = request.getAttribute("employeeName") == null
		? ""
		: String.valueOf(request.getAttribute("employeeName"));
employeeNameValue = employeeNameValue.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<form action="<%=ctx%>/Statistics/paymentStatisticsPayItems.do" method="get">
	<section class="filter-bar">
		<div class="field">
			<label>귀속연월</label>
			<input type="month" class="input" name="yearMonth" value="<%=yearMonthValue%>">
		</div>
		<div class="field">
			<label>사원</label>
			<input type="text" class="input" id="employeeNameInput" name="employeeName"
				value="<%=employeeNameValue%>" placeholder="사원을 선택하세요" readonly
				style="cursor: pointer;"
				data-employee-popup-url="<%=ctx%>/Statistics/paymentStatisticsPayItemsEmployeePopup.do"
				onclick="openEmployeePopup()">
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">조회</button>
		</div>
	</section>
</form>
<section class="card chart-card">
	<div class="card-header">
		<h2 class="section-title">급여항목 구성</h2>
	</div>
	<div class="card-body">
		<div class="donut-wrap">
			<div>
				<canvas data-chart="donut" data-values="[4200000,0,0]"></canvas>
				<p class="chart-note">지급항목별 금액 비중</p>
			</div>
			<div class="table-wrap">
				<table class="data-table ">
					<thead>
						<tr>
							<th>지급항목</th>
							<th>금액</th>
							<th>구성비</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>기본급</td>
							<td>4,200,000</td>
							<td>100.0%</td>
						</tr>
						<tr>
							<td>식대</td>
							<td>0</td>
							<td>0.0%</td>
						</tr>
						<tr>
							<td>연장근로수당</td>
							<td>0</td>
							<td>0.0%</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</section>
<script>
	var employeePopup = null;

	function openEmployeePopup() {
		var input = document.getElementById('employeeNameInput');
		if (!input) {
			return;
		}
		var popupUrl = input.getAttribute('data-employee-popup-url');
		if (!popupUrl) {
			return;
		}
		var width = 900;
		var height = 560;
		var left = Math.max(0, (window.screen.width - width) / 2);
		var top = Math.max(0, (window.screen.height - height) / 2);
		var features = 'width=' + width + ',height=' + height + ',left=' + left + ',top=' + top
			+ ',scrollbars=yes,resizable=yes';
		if (employeePopup && !employeePopup.closed) {
			employeePopup.focus();
			employeePopup.location.href = popupUrl;
			return;
		}
		employeePopup = window.open(popupUrl, 'employeeSelectPopup', features);
	}

	window.applyEmployee = function (employee) {
		if (!employee) {
			return;
		}
		var input = document.getElementById('employeeNameInput');
		if (input) {
			input.value = employee.employeeName || '';
		}
	};
</script>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>

