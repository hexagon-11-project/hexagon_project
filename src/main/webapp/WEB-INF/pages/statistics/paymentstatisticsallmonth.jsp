<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="statistics.model.MonthlyTotalStatistics"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
request.setAttribute("pageTitle", "月別給与総額統計");
request.setAttribute("pageSection", "給与統計");
request.setAttribute("pageDescription", "基準年度における会社全体の月別給与総額と給与支給人数の推移を確認します。");
request.setAttribute("activeKey", "monthly-total");
request.setAttribute("pageCss", "statistics.css?v=3");
request.setAttribute("pageJs", "charts.js?v=combo4");

@SuppressWarnings("unchecked")
List<MonthlyTotalStatistics> monthlyTotalList = (List<MonthlyTotalStatistics>) request.getAttribute("monthlyTotalList");
if (monthlyTotalList == null) {
	monthlyTotalList = new ArrayList<>();
}

StringBuilder labelsJson = new StringBuilder("[");
StringBuilder barValuesJson = new StringBuilder("[");
StringBuilder lineValuesJson = new StringBuilder("[");
for (int i = 0; i < monthlyTotalList.size(); i++) {
	MonthlyTotalStatistics row = monthlyTotalList.get(i);
	if (i > 0) {
		labelsJson.append(',');
		barValuesJson.append(',');
		lineValuesJson.append(',');
	}
	labelsJson.append('"').append(row.getMonth()).append("月\"");
	barValuesJson.append(row.getTotalSalaryAmount() / 1000L);
	lineValuesJson.append(row.getEmployeeCount());
}
labelsJson.append(']');
barValuesJson.append(']');
lineValuesJson.append(']');

long monthlySalaryTotal = 0L;
long monthlyEmployeeSum = 0L;
for (MonthlyTotalStatistics row : monthlyTotalList) {
	monthlySalaryTotal += row.getTotalSalaryAmount();
	monthlyEmployeeSum += row.getEmployeeCount();
}
double monthlyEmployeeAvg = monthlyTotalList.isEmpty() ? 0D : (double) monthlyEmployeeSum / monthlyTotalList.size();
request.setAttribute("monthlyTotalList", monthlyTotalList);
request.setAttribute("monthlySalaryTotal", monthlySalaryTotal);
request.setAttribute("monthlyEmployeeAvg", monthlyEmployeeAvg);

int selectedYear = request.getAttribute("year") == null
		? java.time.LocalDate.now().getYear()
		: (Integer) request.getAttribute("year");
int currentYear = java.time.LocalDate.now().getYear();
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<form action="<%=ctx%>/Statistics/paymentStatisticsAllMonth.do"
	method="get">
	<section class="filter-bar">
		<div class="field">
			<label>基準年</label> <select class="select" name="year">
				<%
				for (int y = currentYear + 1; y >= 2000; y--) {
				%>
				<option value="<%=y%>" <%=y == selectedYear ? "selected" : ""%>><%=y%>年
				</option>
				<%
				}
				%>
			</select>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">照会</button>
		</div>
	</section>
</form>
<section class="card chart-card">
	<div class="card-header">
		<h2 class="section-title">毎月の全給与の推移</h2>
	</div>
	<div class="card-body">
		<script>
			window.paymentStatisticsAllMonthChartData = {
				labels: <%=labelsJson.toString()%>,
				bar: { name: "給与総額 (千円)", values: <%=barValuesJson.toString()%> },
				line: { name: "人数 (人)", values: <%=lineValuesJson.toString()%> }
			};
		</script>
		<div class="chart-canvas-wrap">
			<canvas id="paymentStatisticsAllMonthCanvas"
				data-source="paymentStatisticsAllMonthChartData"></canvas>
			<div id="paymentStatisticsAllMonthTooltip" class="chart-tooltip"
				hidden></div>
		</div>
		<script>
		(function () {
			var hitAreas = [];
			var tipBound = false;
			var activeIndex = -1;

			function fmt(n) { return Math.round(n).toLocaleString('ko-KR'); }

			function ensureTooltip() {
				var tip = document.getElementById('paymentStatisticsAllMonthTooltip');
				var canvas = document.getElementById('paymentStatisticsAllMonthCanvas');
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
							'<div class="chart-tooltip-row"><span class="dot bar"></span>' + item.barName +
							' : <strong>' + fmt(item.barValue) + '</strong></div>' +
							'<div class="chart-tooltip-row"><span class="dot line"></span>' + item.lineName +
							' : <strong>' + item.lineValue + '</strong></div>';
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

			function drawCombo() {
				var canvas = document.getElementById('paymentStatisticsAllMonthCanvas');
				var data = window.paymentStatisticsAllMonthChartData;
				if (!canvas || !data) return;
				var ctx = canvas.getContext('2d');
				if (!ctx) return;
				ensureTooltip();

				var labels = data.labels || [];
				var barValues = (data.bar && data.bar.values) || [];
				var lineValues = (data.line && data.line.values) || [];
				var barName = (data.bar && data.bar.name) || '給与総額 (千円)';
				var lineName = (data.line && data.line.name) || '人数 (人)';
				var COMBO_BAR = '#A9D08E';
				var COMBO_LINE = '#ED7D31';
				var COMBO_LINE_LABEL = '#5B9BD5';

				var ratio = window.devicePixelRatio || 1;
				var cssW = Math.max(canvas.clientWidth || (canvas.parentElement && canvas.parentElement.clientWidth) || 0, 320);
				var cssH = 400;
				canvas.width = cssW * ratio;
				canvas.height = cssH * ratio;
				canvas.style.width = '100%';
				canvas.style.height = cssH + 'px';
				ctx.setTransform(ratio, 0, 0, ratio, 0, 0);

				var p = { l: 72, r: 56, t: 28, b: 72 };
				var plotW = cssW - p.l - p.r;
				var plotH = cssH - p.t - p.b;
				var n = Math.max(labels.length, 1);
				var slot = plotW / n;
				var barW = Math.min(42, slot * 0.55);

				function niceMax(value, steps) {
					if (!isFinite(value) || value <= 0) return steps;
					var raw = value * 1.12;
					var magnitude = Math.pow(10, Math.floor(Math.log(raw) / Math.LN10));
					var normalized = raw / magnitude;
					var nice = normalized <= 1 ? 1 : normalized <= 2 ? 2 : normalized <= 5 ? 5 : 10;
					var step = (nice * magnitude) / steps;
					return Math.ceil(raw / step) * step;
				}

				var barMax = niceMax(barValues.length ? Math.max.apply(null, barValues) : 0, 7);
				var lineMax = niceMax(lineValues.length ? Math.max.apply(null, lineValues) : 0, 2);
				hitAreas = [];

				ctx.clearRect(0, 0, cssW, cssH);
				ctx.fillStyle = '#fff';
				ctx.fillRect(0, 0, cssW, cssH);

				ctx.strokeStyle = '#b8c0c8';
				ctx.beginPath();
				ctx.moveTo(p.l, p.t);
				ctx.lineTo(p.l, p.t + plotH);
				ctx.lineTo(p.l + plotW, p.t + plotH);
				ctx.lineTo(p.l + plotW, p.t);
				ctx.stroke();

				ctx.fillStyle = '#5f6b77';
				ctx.font = '11px sans-serif';
				ctx.textAlign = 'right';
				ctx.textBaseline = 'middle';
				for (var i = 0; i <= 7; i++) {
					var y = p.t + plotH - plotH * (i / 7);
					ctx.fillText(fmt(barMax * i / 7), p.l - 8, y);
				}
				ctx.textAlign = 'left';
				for (var j = 0; j <= 2; j++) {
					var y2 = p.t + plotH - plotH * (j / 2);
					ctx.fillText(String(Math.round(lineMax * j / 2 * 10) / 10), p.l + plotW + 8, y2);
				}

				ctx.save();
				ctx.translate(16, p.t + plotH / 2);
				ctx.rotate(-Math.PI / 2);
				ctx.textAlign = 'center';
				ctx.fillStyle = '#44505c';
				ctx.font = '12px sans-serif';
				ctx.fillText(barName, 0, 0);
				ctx.restore();

				ctx.save();
				ctx.translate(cssW - 14, p.t + plotH / 2);
				ctx.rotate(Math.PI / 2);
				ctx.textAlign = 'center';
				ctx.fillText(lineName, 0, 0);
				ctx.restore();

				for (var b = 0; b < barValues.length; b++) {
					var v = barValues[b];
					var cx = p.l + slot * b + slot / 2;
					var bh = Math.max(plotH * (v / (barMax || 1)), 0);
					var barX = cx - barW / 2;
					var barY = p.t + plotH - bh;
					hitAreas.push({
						x: p.l + slot * b,
						y: p.t,
						w: slot,
						h: plotH,
						label: labels[b] || '',
						barName: barName,
						lineName: lineName,
						barValue: v,
						lineValue: lineValues[b] || 0
					});
					ctx.fillStyle = COMBO_BAR;
					ctx.fillRect(barX, barY, barW, Math.max(bh, 0));
					if (v > 0) {
						ctx.fillStyle = '#2f3a45';
						ctx.font = '11px sans-serif';
						ctx.textAlign = 'center';
						ctx.textBaseline = 'bottom';
						ctx.fillText(fmt(v), cx, barY - 4);
					}
				}

				ctx.strokeStyle = COMBO_LINE;
				ctx.lineWidth = 2;
				ctx.beginPath();
				for (var l = 0; l < lineValues.length; l++) {
					var lx = p.l + slot * l + slot / 2;
					var ly = p.t + plotH - plotH * (lineValues[l] / (lineMax || 1));
					if (l === 0) ctx.moveTo(lx, ly); else ctx.lineTo(lx, ly);
				}
				ctx.stroke();

				for (var m = 0; m < lineValues.length; m++) {
					var mx = p.l + slot * m + slot / 2;
					var my = p.t + plotH - plotH * (lineValues[m] / (lineMax || 1));
					ctx.beginPath();
					ctx.arc(mx, my, 4.5, 0, Math.PI * 2);
					ctx.fillStyle = COMBO_LINE;
					ctx.fill();
					ctx.fillStyle = COMBO_LINE_LABEL;
					ctx.font = '12px sans-serif';
					ctx.textAlign = 'center';
					ctx.textBaseline = 'bottom';
					ctx.fillText(String(lineValues[m]), mx, my - 8);
				}

				ctx.fillStyle = '#5f6b77';
				ctx.font = '11px sans-serif';
				ctx.textAlign = 'center';
				ctx.textBaseline = 'top';
				for (var t = 0; t < labels.length; t++) {
					ctx.fillText(labels[t], p.l + slot * t + slot / 2, p.t + plotH + 10);
				}

				ctx.font = '12px sans-serif';
				var items = [{ c: COMBO_BAR, t: barName }, { c: COMBO_LINE, t: lineName }];
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
				document.addEventListener('DOMContentLoaded', drawCombo);
			} else {
				drawCombo();
			}
			window.addEventListener('resize', drawCombo);
		})();
		</script>
	</div>
</section>
<section class="card">
	<div class="card-header">
		<h2 class="section-title">毎月の全給与履歴</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table stats-matrix">
				<thead>
					<tr>
						<th class="col-label">区分</th>
						<c:forEach var="row" items="${monthlyTotalList}">
							<th>${row.month}月</th>
						</c:forEach>
						<th>合計</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${empty monthlyTotalList}">
							<tr>
								<td colspan="14" style="text-align: center;">検索されたデータがありません。</td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<th class="col-label">給与総額 (千円)</th>
								<c:forEach var="row" items="${monthlyTotalList}">
									<td><fmt:formatNumber
											value="${row.totalSalaryAmount / 1000}" pattern="#,###" /></td>
								</c:forEach>
								<td><fmt:formatNumber value="${monthlySalaryTotal / 1000}"
										pattern="#,###" /></td>
							</tr>
							<tr>
								<th class="col-label sub-label">└ 増加率</th>
								<c:forEach var="row" items="${monthlyTotalList}">
									<td><c:choose>
											<c:when test="${empty row.salaryGrowthRate}"></c:when>
											<c:when test="${row.salaryGrowthRate gt 0}">
												<span class="rate-up"><fmt:formatNumber
														value="${row.salaryGrowthRate}" pattern="0.0" />%</span>
											</c:when>
											<c:when test="${row.salaryGrowthRate lt 0}">
												<span class="rate-down"><fmt:formatNumber
														value="${row.salaryGrowthRate}" pattern="0.0" />%</span>
											</c:when>
											<c:otherwise>
												<span class="rate-zero">0.0%</span>
											</c:otherwise>
										</c:choose></td>
								</c:forEach>
								<td></td>
							</tr>
							<tr>
								<th class="col-label">人数 (人)</th>
								<c:forEach var="row" items="${monthlyTotalList}">
									<td><fmt:formatNumber value="${row.employeeCount}"
											pattern="0.0" /></td>
								</c:forEach>
								<td><fmt:formatNumber value="${monthlyEmployeeAvg}"
										pattern="0.0" /></td>
							</tr>
							<tr>
								<th class="col-label sub-label">└ 増加率</th>
								<c:forEach var="row" items="${monthlyTotalList}">
									<td><c:choose>
											<c:when test="${empty row.employeeGrowthRate}"></c:when>
											<c:when test="${row.employeeGrowthRate gt 0}">
												<span class="rate-up"><fmt:formatNumber
														value="${row.employeeGrowthRate}" pattern="0.0" />%</span>
											</c:when>
											<c:when test="${row.employeeGrowthRate lt 0}">
												<span class="rate-down"><fmt:formatNumber
														value="${row.employeeGrowthRate}" pattern="0.0" />%</span>
											</c:when>
											<c:otherwise>
												<span class="rate-zero">0.0%</span>
											</c:otherwise>
										</c:choose></td>
								</c:forEach>
								<td></td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
