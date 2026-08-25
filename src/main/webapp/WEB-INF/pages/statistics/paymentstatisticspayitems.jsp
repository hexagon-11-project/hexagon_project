<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="statistics.model.EmployeeSalaryStatistics"%>
<%@ page import="statistics.model.SalaryItemStatistics"%>
<%!// 도넛 범례/조각 색
	// ドーナツ凡例・切片の色。
	private static final String[] DONUT_PALETTE = {"#3f8fc4", "#e76c64", "#6f7b87", "#50a57a", "#d19a43", "#8d75b8",
			"#58aeb2"};

	/**
	 * null이면 빈 문자열로 바꾼다.
	 * nullなら空文字列に変える。
	 */
	private String nvl(String value) {
		return value == null ? "" : value;
	}

	/**
	 * HTML 특수문자를 이스케이프한다.
	 * HTML特殊文字をエスケープする。
	 */
	private String esc(String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}
		return value.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
	}

	/**
	 * 구성비율을 소수 1자리 % 문자열로 만든다.
	 * 構成比を小数1桁の%文字列にする。
	 */
	private String formatRatio(Double ratio) {
		if (ratio == null) {
			return "0.0%";
		}
		return String.format("%.1f%%", ratio);
	}

	/**
	 * long 값들을 JSON 배열 문자열로 만든다.
	 * long値をJSON配列文字列にする。
	 */
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

	/**
	 * 항목 금액 목록을 JSON 배열로 만든다.
	 * 項目金額一覧をJSON配列にする。
	 */
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

	/**
	 * JSON 문자열 특수문자를 이스케이프한다.
	 * JSON文字列の特殊文字をエスケープする。
	 */
	private String escJson(String value) {
		if (value == null) {
			return "";
		}
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	/**
	 * 문자열 라벨들을 JSON 배열로 만든다.
	 * 文字列ラベルをJSON配列にする。
	 */
	private String toLabelsJson(String... labels) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < labels.length; i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append('"').append(escJson(labels[i])).append('"');
		}
		sb.append(']');
		return sb.toString();
	}

	/**
	 * 항목명 목록을 JSON 배열로 만든다.
	 * 項目名一覧をJSON配列にする。
	 */
	private String toItemLabelsJson(List<SalaryItemStatistics> items) {
		StringBuilder sb = new StringBuilder("[");
		if (items != null) {
			for (int i = 0; i < items.size(); i++) {
				if (i > 0) {
					sb.append(',');
				}
				sb.append('"').append(escJson(nvl(items.get(i).getItemName()))).append('"');
			}
		}
		sb.append(']');
		return sb.toString();
	}

	/**
	 * 도넛 범례 색을 순환한다.
	 * ドーナツ凡例色を循環する。
	 */
	private String paletteColor(int index) {
		return DONUT_PALETTE[index % DONUT_PALETTE.length];
	}%>
<%
request.setAttribute("pageTitle", "給与項目構成統計");
request.setAttribute("pageSection", "給与統計");
request.setAttribute("pageDescription", "帰属年月・社員別の支給項目金額と構成比を、円グラフと表で確認します。");
request.setAttribute("activeKey", "item-composition");
request.setAttribute("pageCss", "statistics.css?v=matrix3");
request.setAttribute("pageJs", "charts.js?v=donutTip1");

Integer selectedYear = (Integer) request.getAttribute("year");
Integer selectedMonth = (Integer) request.getAttribute("month");
// 귀속연월 기본값
// 帰属年月の既定値。
java.time.YearMonth selectedYearMonth = (selectedYear != null && selectedMonth != null)
		? java.time.YearMonth.of(selectedYear, selectedMonth)
		: java.time.YearMonth.now();
String yearMonthValue = selectedYearMonth.toString();
String employeeNameValue = request.getAttribute("employeeName") == null
		? ""
		: String.valueOf(request.getAttribute("employeeName"));
employeeNameValue = esc(employeeNameValue);
String errorMessage = (String) request.getAttribute("errorMessage");
// alert용 메시지 이스케이프
// alert用メッセージのエスケープ。
String errorMessageJs = errorMessage == null
		? ""
		: errorMessage.replace("\\", "\\\\").replace("'", "\\'").replace("\r", "").replace("\n", "\\n");

EmployeeSalaryStatistics stats = (EmployeeSalaryStatistics) request.getAttribute("employeeSalaryStatistics");
@SuppressWarnings("unchecked")
List<SalaryItemStatistics> payItems = (List<SalaryItemStatistics>) request.getAttribute("payItemColumns");
if (payItems == null) {
	payItems = new ArrayList<SalaryItemStatistics>();
}
@SuppressWarnings("unchecked")
List<SalaryItemStatistics> deductionItems = (List<SalaryItemStatistics>) request.getAttribute("deductionItemColumns");
if (deductionItems == null) {
	deductionItems = new ArrayList<SalaryItemStatistics>();
}
DecimalFormat moneyFormat = new DecimalFormat("#,###");
// 지급/공제 열 개수
// 支給/控除列数。
int itemColCount = Math.max(payItems.size(), deductionItems.size());
boolean hasResult = stats != null;
long totalPayAmount = hasResult ? stats.getTotalPayAmount() : 0L;
long totalDeductionAmount = hasResult ? stats.getTotalDeductionAmount() : 0L;
long netPayAmount = hasResult ? stats.getNetPayAmount() : 0L;
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<%-- 급여항목 구성 통계 화면 --%>
<%-- 給与項目構成統計画面。 --%>
<%-- 검색폼 --%>
<%-- 検索フォーム。 --%>
<form action="<%=ctx%>/Statistics/paymentStatisticsPayItems.do"
	method="get">
	<section class="filter-bar">
		<div class="field">
			<label>帰属年月</label> <input type="month" class="input"
				name="yearMonth" value="<%=yearMonthValue%>">
		</div>
		<div class="field">
			<label>社員</label> <input type="text" class="input"
				id="employeeNameInput" name="employeeName"
				value="<%=employeeNameValue%>" placeholder="社員を選択してください" readonly
				style="cursor: pointer;"
				data-employee-popup-url="<%=ctx%>/Statistics/paymentStatisticsPayItemsEmployeePopup.do"
				onclick="openEmployeePopup()">
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">照会</button>
		</div>
	</section>
</form>
<section class="card chart-card">
	<div class="card-header">
		<h2 class="section-title">給与項目の構成</h2>
	</div>
	<div class="card-body">
		<%-- 차트 --%>
		<%-- チャート。 --%>
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
				<div class="chart-canvas-wrap">
					<canvas data-chart="donut" data-center="支給／控除"
						data-labels='<%=toLabelsJson("지급항목", "공제항목")%>'
						data-values="<%=toValuesJson(totalPay, totalDeduction)%>"></canvas>
					<div class="chart-tooltip" hidden></div>
				</div>
				<p class="chart-note">支給項目 + 控除項目</p>
				<ul class="donut-legend">
					<li><span class="swatch"
						style="background:<%=paletteColor(0)%>"></span> 支給項目 <%=stats == null ? "0.0%" : formatRatio(stats.getPaymentRatio())%></li>
					<li><span class="swatch"
						style="background:<%=paletteColor(1)%>"></span> 控除項目 <%=stats == null ? "0.0%" : formatRatio(stats.getDeductionRatio())%></li>
				</ul>
			</div>
			<div class="donut-panel">
				<div class="chart-canvas-wrap">
					<canvas data-chart="donut" data-center="支給項目"
						data-labels='<%=toItemLabelsJson(payItems)%>'
						data-values="<%=toItemValuesJson(payItems)%>"></canvas>
					<div class="chart-tooltip" hidden></div>
				</div>
				<p class="chart-note">支払詳細項目</p>
				<ul class="donut-legend">
					<%
					if (payItems.isEmpty()) {
					%>
					<li>該当する支給項目はありません。</li>
					<%
					} else {
					for (int i = 0; i < payItems.size(); i++) {
						SalaryItemStatistics item = payItems.get(i);
					%>
					<li><span class="swatch"
						style="background:<%=paletteColor(i)%>"></span> <%=esc(nvl(item.getItemName()))%>
						<%=formatRatio(item.getCompositionRatio())%></li>
					<%
					}
					}
					%>
				</ul>
			</div>
			<div class="donut-panel">
				<div class="chart-canvas-wrap">
					<canvas data-chart="donut" data-center="控除項目"
						data-labels='<%=toItemLabelsJson(deductionItems)%>'
						data-values="<%=toItemValuesJson(deductionItems)%>"></canvas>
					<div class="chart-tooltip" hidden></div>
				</div>
				<p class="chart-note">控除の詳細</p>
				<ul class="donut-legend">
					<%
					if (deductionItems.isEmpty()) {
					%>
					<li>該当する控除項目はありません。</li>
					<%
					} else {
					for (int i = 0; i < deductionItems.size(); i++) {
						SalaryItemStatistics item = deductionItems.get(i);
					%>
					<li><span class="swatch"
						style="background:<%=paletteColor(i)%>"></span> <%=esc(nvl(item.getItemName()))%>
						<%=formatRatio(item.getCompositionRatio())%></li>
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
		<h2 class="section-title">支給・控除内訳</h2>
	</div>
	<div class="card-body">
		<%-- 테이블 --%>
		<%-- テーブル。 --%>
		<div class="table-wrap">
			<table class="data-table stats-matrix item-composition-matrix">
				<colgroup>
					<col class="col-label-w">
					<%
					for (int i = 0; i < itemColCount; i++) {
					%>
					<col class="col-item-w">
					<%
					}
					%>
					<col class="col-total-w">
					<col class="col-net-w">
				</colgroup>
				<thead>
					<tr>
						<th class="col-label">支給項目</th>
						<%
						for (int i = 0; i < itemColCount; i++) {
							SalaryItemStatistics item = i < payItems.size() ? payItems.get(i) : null;
						%>
						<th><%=item == null ? "" : esc(nvl(item.getItemName()))%></th>
						<%
						}
						%>
						<th>合計</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<th class="col-label sub-label">ㄴ 金額(円)</th>
						<%
						for (int i = 0; i < itemColCount; i++) {
							SalaryItemStatistics item = i < payItems.size() ? payItems.get(i) : null;
						%>
						<td><%=item == null ? "" : moneyFormat.format(item.getAmount())%></td>
						<%
						}
						%>
						<td><%=moneyFormat.format(totalPayAmount)%></td>
						<td></td>
					</tr>
					<tr>
						<th class="col-label sub-label">ㄴ 構成比率</th>
						<%
						for (int i = 0; i < itemColCount; i++) {
							SalaryItemStatistics item = i < payItems.size() ? payItems.get(i) : null;
						%>
						<td><%=item == null ? "" : formatRatio(item.getCompositionRatio())%></td>
						<%
						}
						%>
						<td><%=hasResult ? "100.0%" : "0.0%"%></td>
						<td></td>
					</tr>
				</tbody>
			</table>
			<table
				class="data-table stats-matrix item-composition-matrix deduction-matrix">
				<colgroup>
					<col class="col-label-w">
					<%
					for (int i = 0; i < itemColCount; i++) {
					%>
					<col class="col-item-w">
					<%
					}
					%>
					<col class="col-total-w">
					<col class="col-net-w">
				</colgroup>
				<thead>
					<tr>
						<th class="col-label">控除項目</th>
						<%
						for (int i = 0; i < itemColCount; i++) {
							SalaryItemStatistics item = i < deductionItems.size() ? deductionItems.get(i) : null;
						%>
						<th><%=item == null ? "" : esc(nvl(item.getItemName()))%></th>
						<%
						}
						%>
						<th>合計</th>
						<th class="net-pay-head">実支給額</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<th class="col-label sub-label">ㄴ 金額(円)</th>
						<%
						for (int i = 0; i < itemColCount; i++) {
							SalaryItemStatistics item = i < deductionItems.size() ? deductionItems.get(i) : null;
						%>
						<td><%=item == null ? "" : moneyFormat.format(item.getAmount())%></td>
						<%
						}
						%>
						<td><%=moneyFormat.format(totalDeductionAmount)%></td>
						<td class="net-pay-value" rowspan="2"><%=moneyFormat.format(netPayAmount)%></td>
					</tr>
					<tr>
						<th class="col-label sub-label">ㄴ 構成比率</th>
						<%
						for (int i = 0; i < itemColCount; i++) {
							SalaryItemStatistics item = i < deductionItems.size() ? deductionItems.get(i) : null;
						%>
						<td><%=item == null ? "" : formatRatio(item.getCompositionRatio())%></td>
						<%
						}
						%>
						<td><%=hasResult ? "100.0%" : "0.0%"%></td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</section>
<script>
	// 팝업
	// ポップアップ。
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
<%-- 급여 없음 안내 --%>
<%-- 給与なし案内。 --%>
<%
if (errorMessage != null) {
%>
<script>alert('<%=errorMessageJs%>
	');
</script>
<%
}
%>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
