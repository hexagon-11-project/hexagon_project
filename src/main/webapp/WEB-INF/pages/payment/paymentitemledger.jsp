<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="payment.model.PaymentItemLedger"%>
<%!private String dash(String value) {
		if (value == null || value.trim().length() == 0) {
			return "-";
		}
		return value;
	}

	private long amountOfMonth(PaymentItemLedger emp, String monthLabel) {
		long sum = 0L;
		if (emp == null || monthLabel == null || monthLabel.length() < 7) {
			return 0L;
		}
		List details = emp.getDetails();
		if (details == null) {
			return 0L;
		}
		int year;
		int month;
		try {
			year = Integer.parseInt(monthLabel.substring(0, 4));
			month = Integer.parseInt(monthLabel.substring(5, 7));
		} catch (Exception e) {
			return 0L;
		}
		for (int i = 0; i < details.size(); i++) {
			PaymentItemLedger detail = (PaymentItemLedger) details.get(i);
			if (detail.getYear() == year && detail.getMonth() == month) {
				sum += detail.getAmount();
			}
		}
		return sum;
	}%>
<%
request.setAttribute("pageTitle", "項目別台帳");
request.setAttribute("pageSection", "給与管理");
request.setAttribute("pageDescription", "照会期間と支給・控除項目を選択し、社員ごとの項目金額と合計を確認します。");
request.setAttribute("activeKey", "item-ledger");
request.setAttribute("pageCss", "payroll.css?v=2");
request.setAttribute("pageJs", null);

List ledgerItemList = (List) request.getAttribute("itemList");
String selectedPayItemKey = (String) request.getAttribute("payItemKey");
if (selectedPayItemKey == null) {
	selectedPayItemKey = "";
}
boolean selectPlaceholder = selectedPayItemKey.isEmpty();

List monthColumns = (List) request.getAttribute("monthColumns");
if (monthColumns == null) {
	monthColumns = new ArrayList();
}
List employeeRows = (List) request.getAttribute("employeeList");
if (employeeRows == null) {
	employeeRows = new ArrayList();
}
boolean searched = Boolean.TRUE.equals(request.getAttribute("searched"));
String selectedItemName = (String) request.getAttribute("selectedItemName");
if (selectedItemName == null) {
	selectedItemName = "";
}
int tableColCount = 5 + monthColumns.size();
DecimalFormat amountFormat = new DecimalFormat("#,###");
long[] monthTotals = new long[monthColumns.size()];
long grandTotal = 0L;
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>
<form action="<%=ctx%>/Payment/paymentPayItemPart.do" method="get"
	autocomplete="off" onsubmit="return checkLedgerPeriod(this);">
	<input type="hidden" name="search" value="Y">
	<section class="filter-bar">
		<div class="field">
			<label>照会期間</label>
			<div class="range">
				<input type="month" class="input" name="startYearMonth"
					value="${startYearMonth}"> <span>~</span> <input
					type="month" class="input" name="endYearMonth"
					value="${endYearMonth}">
			</div>
		</div>
		<div class="field">
			<label>項目</label> <select class="select" name="payItemKey"
				autocomplete="off">
				<option value=""
					<%=selectPlaceholder ? "selected=\"selected\"" : ""%>>給与項目の選択</option>
				<%
				if (ledgerItemList != null) {
					for (int i = 0; i < ledgerItemList.size(); i++) {
						PaymentItemLedger payItem = (PaymentItemLedger) ledgerItemList.get(i);
						String optionValue = payItem.getSelectValue();
						String optionName = payItem.getItemName() == null ? "" : payItem.getItemName();
						boolean optionSelected = !selectPlaceholder && selectedPayItemKey.equals(optionValue);
				%>
				<option value="<%=optionValue%>"
					<%=optionSelected ? "selected=\"selected\"" : ""%>><%=optionName%></option>
				<%
				}
				}
				%>
			</select>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">照会</button>
		</div>
	</section>
</form>
<section class="card">
	<div class="card-header">
		<h2 class="section-title">
			<%
			if (!selectedItemName.isEmpty()) {
			%>
			<%=selectedItemName%>
			項目別台帳
			<%
			} else {
			%>
			項目別台帳
			<%
			}
			%>
		</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table item-ledger-table">
				<thead>
					<tr>
						<th>区分</th>
						<th>名前</th>
						<th>部署</th>
						<th>職位</th>
						<%
						for (int i = 0; i < monthColumns.size(); i++) {
							String monthLabel = String.valueOf(monthColumns.get(i));
						%>
						<th class="month-col"><%=monthLabel%></th>
						<%
						}
						%>
						<th class="row-total">合計</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (!searched) {
					%>
					<tr>
						<td colspan="<%=tableColCount%>" style="text-align: center;">照会期間と項目を選択してから照会してください。</td>
					</tr>
					<%
					} else if (employeeRows.isEmpty()) {
					%>
					<tr>
						<td colspan="<%=tableColCount%>" style="text-align: center;">照会された履歴はありません。</td>
					</tr>
					<%
					} else {
					for (int r = 0; r < employeeRows.size(); r++) {
						PaymentItemLedger emp = (PaymentItemLedger) employeeRows.get(r);
						grandTotal += emp.getTotalAmount();
						String employmentType = dash(emp.getEmploymentType());
						String employeeName = dash(emp.getEmployeeName());
						String department = dash(emp.getDepartment());
						String position = dash(emp.getPosition());
					%>
					<tr>
						<td class="center"><%=employmentType%></td>
						<td class="center"><%=employeeName%></td>
						<td class="center"><%=department%></td>
						<td class="center"><%=position%></td>
						<%
						for (int c = 0; c < monthColumns.size(); c++) {
							String monthLabel = String.valueOf(monthColumns.get(c));
							long monthAmount = amountOfMonth(emp, monthLabel);
							monthTotals[c] += monthAmount;
						%>
						<td class="amount"><%=amountFormat.format(monthAmount)%></td>
						<%
						}
						%>
						<td class="row-total"><%=amountFormat.format(emp.getTotalAmount())%></td>
					</tr>
					<%
					}
					}
					%>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="4">合計</td>
						<%
						for (int c = 0; c < monthColumns.size(); c++) {
						%>
						<td class="amount"><%=amountFormat.format(monthTotals[c])%></td>
						<%
						}
						%>
						<td class="amount"><%=amountFormat.format(grandTotal)%></td>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>
</section>
<script>
function checkLedgerPeriod(form) {
	var startValue = form.startYearMonth.value;
	var endValue = form.endYearMonth.value;
	if (!startValue || !endValue) {
		return true;
	}
	var startParts = startValue.split('-');
	var endParts = endValue.split('-');
	var startYear = parseInt(startParts[0], 10);
	var startMonth = parseInt(startParts[1], 10);
	var endYear = parseInt(endParts[0], 10);
	var endMonth = parseInt(endParts[1], 10);
	var monthCount = (endYear - startYear) * 12 + (endMonth - startMonth) + 1;
	if (monthCount > 12) {
		alert('照会期間は12ヶ月を超えることはできません。');
		return false;
	}
	return true;
}
</script>
<%
String errorMessage = (String) request.getAttribute("errorMessage");
if (errorMessage != null) {
	String errorMessageJs = errorMessage.replace("\\", "\\\\").replace("'", "\\'").replace("\r", "").replace("\n",
	"\\n");
%>
<script>alert('<%=errorMessageJs%>
	');
</script>
<%
}
%>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
