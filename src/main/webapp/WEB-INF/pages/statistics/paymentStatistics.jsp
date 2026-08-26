<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="statistics.paymentStatistics.dto.PersonalAnnualStatistics"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
request.setAttribute("pageTitle", "年度別個人年俸統計");
request.setAttribute("pageSection", "給与統計");
request.setAttribute("pageDescription", "帰属年度と社員を検索すると、該当社員の直近10年間の給与状況を確認できます。");
request.setAttribute("activeKey", "annual-personal");
request.setAttribute("pageCss", "statistics.css");
request.setAttribute("pageJs", null);

int currentYear = java.time.LocalDate.now().getYear();
Integer selectedYearAttr = (Integer) request.getAttribute("year");
int selectedYear = selectedYearAttr == null ? currentYear : selectedYearAttr;

String employeeNameValue = request.getAttribute("employeeName") == null
		? "" : String.valueOf(request.getAttribute("employeeName"));
employeeNameValue = employeeNameValue.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");

@SuppressWarnings("unchecked")
List<PersonalAnnualStatistics> annualList = (List<PersonalAnnualStatistics>) request.getAttribute("annualList");
if (annualList == null) {
	annualList = new java.util.ArrayList<PersonalAnnualStatistics>();
}
boolean hasResult = !annualList.isEmpty();

StringBuilder labelsJson = new StringBuilder("[");
StringBuilder deductionValuesJson = new StringBuilder("[");
StringBuilder netValuesJson = new StringBuilder("[");
for (int i = 0; i < annualList.size(); i++) {
	PersonalAnnualStatistics row = annualList.get(i);
	if (i > 0) {
		labelsJson.append(',');
		deductionValuesJson.append(',');
		netValuesJson.append(',');
	}
	labelsJson.append('"').append(row.getYear()).append("年\"");
	deductionValuesJson.append(row.getTotalDeductionAmount() / 1000L);
	netValuesJson.append(row.getNetPayAmount() / 1000L);
}
labelsJson.append(']');
deductionValuesJson.append(']');
netValuesJson.append(']');
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<style>
.chart-print-note {
	border: 1px solid #f0c199;
	background: #fff8f0;
	color: #c0622a;
	font-size: 12px;
	padding: 10px 14px;
	border-radius: 4px;
	margin-top: 12px;
}
</style>

<form id="pstFilterForm" action="<%=ctx%>/Statistics/paymentStatistics.do" method="get">
	<section class="filter-bar">
		<div class="field">
			<label>* 帰属年度を選択してください。</label>
			<select class="select" name="year" onchange="document.getElementById('pstFilterForm').submit()">
<%
for (int y = currentYear + 1; y >= 2005; y--) {
%>
				<option value="<%=y%>" <%=y == selectedYear ? "selected" : ""%>><%=y%>年</option>
<%
}
%>
			</select>
		</div>
		<div class="field">
			<label>対象者を選択してください。</label>
			<input type="text" class="input" id="employeeNameInput" name="employeeName"
				value="<%=employeeNameValue%>" placeholder="社員名を入力してください" readonly
				style="cursor: pointer;" onclick="pstOpenEmployeeModal()">
		</div>
		<div class="actions">
			<button type="button" class="btn btn-primary" onclick="pstOpenEmployeeModal()">🔍</button>
		</div>
	</section>
</form>

<script>
function pstOpenEmployeeModal() {
	var contextPath = "<%=ctx%>";
	var popupUrl = contextPath + "/Statistics/paymentStatisticsEmployeeModal.do";
	window.open(popupUrl, "PstEmployeeSelectModal", "width=820,height=650,left=250,top=100,scrollbars=yes");
}

// 사원선택 팝업에서 호출 (window.opener.pstSetSelectedEmployee(...)) / 社員選択ポップアップから呼び出し（window.opener.pstSetSelectedEmployee(...)）
function pstSetSelectedEmployee(employeeName) {
	document.getElementById("employeeNameInput").value = employeeName;
	document.getElementById("pstFilterForm").submit();
}
</script>

<%
if (!hasResult) {
%>
<section class="card">
	<div class="card-body">
		<p class="empty-state"><%=employeeNameValue.isEmpty() ? "社員名を入力して検索してください。" : "照会された給与内訳がありません。"%></p>
	</div>
</section>
<%
} else {
%>
<section class="card chart-card">
	<div class="card-body">
		<script>
			window.paymentStatisticsChartData = {
				labels: <%=labelsJson.toString()%>,
				deduction: { name: "控除金額（千円）", values: <%=deductionValuesJson.toString()%> },
				net: { name: "実支給額（千円）", values: <%=netValuesJson.toString()%> }
			};
		</script>
		<div class="chart-canvas-wrap">
			<canvas id="paymentStatisticsCanvas" data-chart="stacked-bar"
				data-source="paymentStatisticsChartData"></canvas>
			<div id="paymentStatisticsTooltip" class="chart-tooltip" hidden></div>
		</div>
		<script>
		(function () {
			var hitAreas = [];
			var tipBound = false;
			var activeIndex = -1;

			function fmt(n) { return Math.round(n).toLocaleString('ko-KR'); }

			function ensureTooltip() {
				var tip = document.getElementById('paymentStatisticsTooltip');
				var canvas = document.getElementById('paymentStatisticsCanvas');
				if (!tip || !canvas || tipBound) return tip;

				canvas.addEventListener('mousemove', function (e) {
					var rect = canvas.getBoundingClientRect();
					var x = e.clientX - rect.left;
					var y = e.clientY - rect.top;
					var found = -1;
					for (var i = 0; i < hitAreas.length; i++) {
						var h = hitAreas[i];
						if (x >= h.x && x <= h.x + h.w && y >= h.y && y <= h.y + h.h) {
							found = i;
							break;
						}
					}
					if (found < 0) {
						activeIndex = -1;
						tip.hidden = true;
						canvas.style.cursor = 'default';
						return;
					}
					canvas.style.cursor = 'pointer';
					var item = hitAreas[found];
					if (activeIndex !== found) {
						activeIndex = found;
						tip.innerHTML =
							'<div class="chart-tooltip-title">' + item.label + '</div>' +
							'<div class="chart-tooltip-row"><span class="dot" style="background:#8ea9db"></span>実支給額（千円）：<strong>' + fmt(item.net) + '</strong></div>' +
							'<div class="chart-tooltip-row"><span class="dot" style="background:#f4b183"></span>控除金額（千円）：<strong>' + fmt(item.deduction) + '</strong></div>';
					}
					tip.hidden = false;
					var tipW = tip.offsetWidth || 160;
					var tipH = tip.offsetHeight || 70;
					var left = e.clientX - rect.left + 14;
					var top = e.clientY - rect.top - tipH - 10;
					if (left + tipW > rect.width) left = e.clientX - rect.left - tipW - 14;
					if (top < 0) top = e.clientY - rect.top + 16;
					tip.style.left = left + 'px';
					tip.style.top = top + 'px';
				});

				canvas.addEventListener('mouseleave', function () {
					activeIndex = -1;
					tip.hidden = true;
					canvas.style.cursor = 'default';
				});

				tipBound = true;
				return tip;
			}

			function drawStackedBar() {
				var canvas = document.getElementById('paymentStatisticsCanvas');
				var data = window.paymentStatisticsChartData;
				if (!canvas || !data) return;
				var ctx = canvas.getContext('2d');
				if (!ctx) return;
				ensureTooltip();

				var labels = data.labels || [];
				var deductionValues = (data.deduction && data.deduction.values) || [];
				var netValues = (data.net && data.net.values) || [];
				var deductionName = (data.deduction && data.deduction.name) || '控除金額（千円）';
				var netName = (data.net && data.net.name) || '実支給額（千円）';
				var DEDUCTION_COLOR = '#f4b183';
				var NET_COLOR = '#8ea9db';

				var ratio = window.devicePixelRatio || 1;
				var cssW = Math.max(canvas.clientWidth || (canvas.parentElement && canvas.parentElement.clientWidth) || 0, 320);
				var cssH = 420;
				canvas.width = cssW * ratio;
				canvas.height = cssH * ratio;
				canvas.style.width = '100%';
				canvas.style.height = cssH + 'px';
				ctx.setTransform(ratio, 0, 0, ratio, 0, 0);

				var p = { l: 72, r: 30, t: 28, b: 72 };
				var plotW = cssW - p.l - p.r;
				var plotH = cssH - p.t - p.b;
				var n = Math.max(labels.length, 1);
				var slot = plotW / n;
				var barW = Math.min(56, slot * 0.6);

				function niceMax(value, steps) {
					if (!isFinite(value) || value <= 0) return steps;
					var raw = value * 1.12;
					var magnitude = Math.pow(10, Math.floor(Math.log(raw) / Math.LN10));
					var normalized = raw / magnitude;
					var nice = normalized <= 1 ? 1 : normalized <= 2 ? 2 : normalized <= 5 ? 5 : 10;
					var step = (nice * magnitude) / steps;
					return Math.ceil(raw / step) * step;
				}

				var totals = [];
				for (var i = 0; i < labels.length; i++) {
					totals.push((deductionValues[i] || 0) + (netValues[i] || 0));
				}
				var maxTotal = niceMax(totals.length ? Math.max.apply(null, totals) : 0, 7);
				hitAreas = [];

				ctx.clearRect(0, 0, cssW, cssH);
				ctx.fillStyle = '#fff';
				ctx.fillRect(0, 0, cssW, cssH);

				ctx.strokeStyle = '#b8c0c8';
				ctx.beginPath();
				ctx.moveTo(p.l, p.t);
				ctx.lineTo(p.l, p.t + plotH);
				ctx.lineTo(p.l + plotW, p.t + plotH);
				ctx.stroke();

				ctx.fillStyle = '#5f6b77';
				ctx.font = '11px sans-serif';
				ctx.textAlign = 'right';
				ctx.textBaseline = 'middle';
				for (var g = 0; g <= 7; g++) {
					var gy = p.t + plotH - plotH * (g / 7);
					ctx.fillText(fmt(maxTotal * g / 7), p.l - 8, gy);
				}

				ctx.save();
				ctx.translate(16, p.t + plotH / 2);
				ctx.rotate(-Math.PI / 2);
				ctx.textAlign = 'center';
				ctx.fillStyle = '#44505c';
				ctx.font = '12px sans-serif';
				ctx.fillText('控除金額 実支給額（千円）', 0, 0);
				ctx.restore();

				for (var b = 0; b < labels.length; b++) {
					var dedV = deductionValues[b] || 0;
					var netV = netValues[b] || 0;
					var cx = p.l + slot * b + slot / 2;
					var barX = cx - barW / 2;

					var dedH = plotH * (dedV / (maxTotal || 1));
					var netH = plotH * (netV / (maxTotal || 1));
					var dedY = p.t + plotH - dedH;
					var netY = dedY - netH;

					hitAreas.push({ x: p.l + slot * b, y: p.t, w: slot, h: plotH, label: labels[b] || '', deduction: dedV, net: netV });

					ctx.fillStyle = DEDUCTION_COLOR;
					ctx.fillRect(barX, dedY, barW, dedH);
					ctx.fillStyle = NET_COLOR;
					ctx.fillRect(barX, netY, barW, netH);

					ctx.font = '11px sans-serif';
					ctx.textAlign = 'center';
					ctx.textBaseline = 'middle';
					if (dedH > 14) {
						ctx.fillStyle = '#5a3a1a';
						ctx.fillText(fmt(dedV), cx, dedY + dedH / 2);
					}
					if (netH > 14) {
						ctx.fillStyle = '#1f2d50';
						ctx.fillText(fmt(netV), cx, netY + netH / 2);
					}
				}

				ctx.fillStyle = '#5f6b77';
				ctx.font = '11px sans-serif';
				ctx.textAlign = 'center';
				ctx.textBaseline = 'top';
				for (var t = 0; t < labels.length; t++) {
					ctx.fillText(labels[t], p.l + slot * t + slot / 2, p.t + plotH + 10);
				}

				ctx.font = '12px sans-serif';
				var items = [{ c: DEDUCTION_COLOR, t: deductionName }, { c: NET_COLOR, t: netName }];
				var legendW = 0;
				for (var x = 0; x < items.length; x++) legendW += 18 + ctx.measureText(items[x].t).width + 24;
				var lx0 = (cssW - legendW) / 2;
				var legendY = cssH - 22;
				for (var yi = 0; yi < items.length; yi++) {
					ctx.fillStyle = items[yi].c;
					ctx.fillRect(lx0, legendY - 7, 12, 12);
					ctx.fillStyle = '#44505c';
					ctx.textAlign = 'left';
					ctx.textBaseline = 'middle';
					ctx.fillText(items[yi].t, lx0 + 18, legendY);
					lx0 += 18 + ctx.measureText(items[yi].t).width + 24;
				}
			}

			if (document.readyState === 'loading') {
				document.addEventListener('DOMContentLoaded', drawStackedBar);
			} else {
				drawStackedBar();
			}
			window.addEventListener('resize', drawStackedBar);
		})();
		</script>
	</div>
</section>

<section class="card">
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table stats-matrix">
				<thead>
					<tr>
						<th class="col-label">区分</th>
						<c:forEach var="row" items="${annualList}">
							<th>${row.year}年</th>
						</c:forEach>
					</tr>
				</thead>
				<tbody>
					<tr>
						<th class="col-label">年俸額（千円）</th>
						<c:forEach var="row" items="${annualList}">
							<td><fmt:formatNumber value="${row.totalPayAmount / 1000}" pattern="#,###"/></td>
						</c:forEach>
					</tr>
					<tr>
						<th class="col-label sub-label">└ 増加率</th>
						<c:forEach var="row" items="${annualList}">
							<td>
								<c:choose>
									<c:when test="${empty row.salaryGrowthRate}"></c:when>
									<c:when test="${row.salaryGrowthRate gt 0}">
										<span class="rate-up"><fmt:formatNumber value="${row.salaryGrowthRate}" pattern="0.0"/>%</span>
									</c:when>
									<c:when test="${row.salaryGrowthRate lt 0}">
										<span class="rate-down"><fmt:formatNumber value="${row.salaryGrowthRate}" pattern="0.0"/>%</span>
									</c:when>
									<c:otherwise>
										<span class="rate-zero">0.0%</span>
									</c:otherwise>
								</c:choose>
							</td>
						</c:forEach>
					</tr>
					<tr>
						<th class="col-label">控除金額（千円）</th>
						<c:forEach var="row" items="${annualList}">
							<td><fmt:formatNumber value="${row.totalDeductionAmount / 1000}" pattern="#,###"/></td>
						</c:forEach>
					</tr>
					<tr>
						<th class="col-label">実支給額（千円）</th>
						<c:forEach var="row" items="${annualList}">
							<td><fmt:formatNumber value="${row.netPayAmount / 1000}" pattern="#,###"/></td>
						</c:forEach>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="chart-print-note">
			※ 統計チャートは別途の印刷画面を提供しておりません。Windowsが提供するスクリーンショット機能をご利用の上、印刷（Ctrl + P）してください。
		</div>
	</div>
</section>
<%
}
%>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
