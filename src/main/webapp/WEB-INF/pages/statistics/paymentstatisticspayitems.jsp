<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="statistics.model.EmployeeSalaryStatistics"%>
<%@ page import="statistics.model.SalaryItemStatistics"%>
<%!
private static final String[] DONUT_PALETTE = {
		"#3f8fc4", "#e76c64", "#6f7b87", "#50a57a", "#d19a43", "#8d75b8", "#58aeb2"
};

private String nvl(String value) {
	return value == null ? "" : value;
}

private String esc(String value) {
	if (value == null || value.isEmpty()) {
		return "";
	}
	return value.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
}

private String formatRatio(Double ratio) {
	if (ratio == null) {
		return "0.0%";
	}
	return String.format("%.1f%%", ratio);
}

private String toValuesJson(long... values) {
	StringBuilder sb = new StringBuilder("[");
	for (int i = 0; i < values.length; i++) {
		if (i > 0) {
			sb.append(',');
		}
		sb.append(values[i]);
	}
	sb.append(']');
	return sb.toString();
}

private String toItemValuesJson(List<SalaryItemStatistics> items) {
	StringBuilder sb = new StringBuilder("[");
	if (items != null) {
		for (int i = 0; i < items.size(); i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(items.get(i).getAmount());
		}
	}
	sb.append(']');
	return sb.toString();
}

private String paletteColor(int index) {
	return DONUT_PALETTE[index % DONUT_PALETTE.length];
}
%>
<%
request.setAttribute("pageTitle", "급여항목 구성 통계");
request.setAttribute("pageSection", "급여통계");
request.setAttribute("pageDescription", "귀속연월·사원별 지급항목 금액과 구성비를 원형 그래프와 표로 확인합니다.");
request.setAttribute("activeKey", "item-composition");
request.setAttribute("pageCss", "statistics.css?v=matrix1");
request.setAttribute("pageJs", "charts.js?v=donut3");

Integer selectedYear = (Integer) request.getAttribute("year");
Integer selectedMonth = (Integer) request.getAttribute("month");
java.time.YearMonth selectedYearMonth = (selectedYear != null && selectedMonth != null)
		? java.time.YearMonth.of(selectedYear, selectedMonth)
		: java.time.YearMonth.now();
String yearMonthValue = selectedYearMonth.toString();
String employeeNameValue = request.getAttribute("employeeName") == null
		? ""
		: String.valueOf(request.getAttribute("employeeName"));
employeeNameValue = esc(employeeNameValue);
String errorMessage = (String) request.getAttribute("errorMessage");
String errorMessageJs = errorMessage == null
		? ""
		: errorMessage.replace("\\", "\\\\").replace("'", "\\'").replace("\r", "").replace("\n", "\\n");

EmployeeSalaryStatistics stats = (EmployeeSalaryStatistics) request.getAttribute("employeeSalaryStatistics");
List<SalaryItemStatistics> payItems = stats != null && stats.getPayItems() != null
		? stats.getPayItems()
		: new ArrayList<SalaryItemStatistics>();
List<SalaryItemStatistics> deductionItems = stats != null && stats.getDeductionItems() != null
		? stats.getDeductionItems()
		: new ArrayList<SalaryItemStatistics>();
DecimalFormat moneyFormat = new DecimalFormat("#,###");
int itemColCount = Math.max(payItems.size(), deductionItems.size());
int payColspan = itemColCount + 2;
int deductionColspan = itemColCount + 3;
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
		<%
		if (errorMessage != null) {
		%>
		<div class="empty-state"><%=esc(errorMessage)%></div>
		<%
		} else {
			long totalPay = stats == null ? 0L : stats.getTotalPayAmount();
			long totalDeduction = stats == null ? 0L : stats.getTotalDeductionAmount();
		%>
		<div class="donut-triple">
			<div class="donut-panel">
				<canvas data-chart="donut"
					data-center="지급/공제"
					data-values="<%=toValuesJson(totalPay, totalDeduction)%>"></canvas>
				<p class="chart-note">지급항목 + 공제항목</p>
				<ul class="donut-legend">
					<li><span class="swatch" style="background:<%=paletteColor(0)%>"></span>
						지급항목 <%=stats == null ? "0.0%" : formatRatio(stats.getPaymentRatio())%></li>
					<li><span class="swatch" style="background:<%=paletteColor(1)%>"></span>
						공제항목 <%=stats == null ? "0.0%" : formatRatio(stats.getDeductionRatio())%></li>
				</ul>
			</div>
			<div class="donut-panel">
				<canvas data-chart="donut"
					data-center="지급항목"
					data-values="<%=toItemValuesJson(payItems)%>"></canvas>
				<p class="chart-note">지급 세부항목</p>
				<ul class="donut-legend">
					<%
					if (payItems.isEmpty()) {
					%>
					<li>조회된 지급항목이 없습니다.</li>
					<%
					} else {
						for (int i = 0; i < payItems.size(); i++) {
							SalaryItemStatistics item = payItems.get(i);
					%>
					<li><span class="swatch" style="background:<%=paletteColor(i)%>"></span>
						<%=esc(nvl(item.getItemName()))%> <%=formatRatio(item.getCompositionRatio())%></li>
					<%
						}
					}
					%>
				</ul>
			</div>
			<div class="donut-panel">
				<canvas data-chart="donut"
					data-center="공제항목"
					data-values="<%=toItemValuesJson(deductionItems)%>"></canvas>
				<p class="chart-note">공제 세부항목</p>
				<ul class="donut-legend">
					<%
					if (deductionItems.isEmpty()) {
					%>
					<li>조회된 공제항목이 없습니다.</li>
					<%
					} else {
						for (int i = 0; i < deductionItems.size(); i++) {
							SalaryItemStatistics item = deductionItems.get(i);
					%>
					<li><span class="swatch" style="background:<%=paletteColor(i)%>"></span>
						<%=esc(nvl(item.getItemName()))%> <%=formatRatio(item.getCompositionRatio())%></li>
					<%
						}
					}
					%>
				</ul>
			</div>
		</div>
		<%
		}
		%>
	</div>
</section>
<section class="card">
	<div class="card-header">
		<h2 class="section-title">지급·공제 내역</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table stats-matrix item-composition-matrix">
				<thead>
					<tr>
						<th class="col-label">지급항목</th>
						<%
						for (int i = 0; i < itemColCount; i++) {
							SalaryItemStatistics item = i < payItems.size() ? payItems.get(i) : null;
						%>
						<th><%=item == null ? "" : esc(nvl(item.getItemName()))%></th>
						<%
						}
						%>
						<th>합계</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (errorMessage != null) {
					%>
					<tr>
						<td colspan="<%=payColspan%>" class="center"><%=esc(errorMessage)%></td>
					</tr>
					<%
					} else if (stats == null) {
					%>
					<tr>
						<td colspan="<%=Math.max(payColspan, 2)%>" class="center">사원과 귀속연월을 선택해 조회하세요.</td>
					</tr>
					<%
					} else {
					%>
					<tr>
						<th class="col-label sub-label">ㄴ 금액(원)</th>
						<%
						for (int i = 0; i < itemColCount; i++) {
							SalaryItemStatistics item = i < payItems.size() ? payItems.get(i) : null;
						%>
						<td><%=item == null ? "" : moneyFormat.format(item.getAmount())%></td>
						<%
						}
						%>
						<td><%=moneyFormat.format(stats.getTotalPayAmount())%></td>
					</tr>
					<tr>
						<th class="col-label sub-label">ㄴ 구성비율</th>
						<%
						for (int i = 0; i < itemColCount; i++) {
							SalaryItemStatistics item = i < payItems.size() ? payItems.get(i) : null;
						%>
						<td><%=item == null ? "" : formatRatio(item.getCompositionRatio())%></td>
						<%
						}
						%>
						<td>100.0%</td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
			<table class="data-table stats-matrix item-composition-matrix deduction-matrix">
				<thead>
					<tr>
						<th class="col-label">공제항목</th>
						<%
						for (int i = 0; i < itemColCount; i++) {
							SalaryItemStatistics item = i < deductionItems.size() ? deductionItems.get(i) : null;
						%>
						<th><%=item == null ? "" : esc(nvl(item.getItemName()))%></th>
						<%
						}
						%>
						<th>합계</th>
						<th class="net-pay-head">실지급액</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (errorMessage != null) {
					%>
					<tr>
						<td colspan="<%=deductionColspan%>" class="center"><%=esc(errorMessage)%></td>
					</tr>
					<%
					} else if (stats == null) {
					%>
					<tr>
						<td colspan="<%=Math.max(deductionColspan, 3)%>" class="center">사원과 귀속연월을 선택해 조회하세요.</td>
					</tr>
					<%
					} else {
					%>
					<tr>
						<th class="col-label sub-label">ㄴ 금액(원)</th>
						<%
						for (int i = 0; i < itemColCount; i++) {
							SalaryItemStatistics item = i < deductionItems.size() ? deductionItems.get(i) : null;
						%>
						<td><%=item == null ? "" : moneyFormat.format(item.getAmount())%></td>
						<%
						}
						%>
						<td><%=moneyFormat.format(stats.getTotalDeductionAmount())%></td>
						<td class="net-pay-value" rowspan="2"><%=moneyFormat.format(stats.getNetPayAmount())%></td>
					</tr>
					<tr>
						<th class="col-label sub-label">ㄴ 구성비율</th>
						<%
						for (int i = 0; i < itemColCount; i++) {
							SalaryItemStatistics item = i < deductionItems.size() ? deductionItems.get(i) : null;
						%>
						<td><%=item == null ? "" : formatRatio(item.getCompositionRatio())%></td>
						<%
						}
						%>
						<td>100.0%</td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
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
<%
if (errorMessage != null) {
%>
<script>alert('<%=errorMessageJs%>');</script>
<%
}
%>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
