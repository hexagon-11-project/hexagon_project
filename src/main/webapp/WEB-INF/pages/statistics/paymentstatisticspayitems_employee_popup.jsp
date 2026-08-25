<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.LinkedHashSet"%>
<%@ page import="config.employee.model.Employee"%>
<%!private String nvl(String value) {
		return value == null ? "" : value;
	}

	private String esc(String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}
		return value.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
	}%>
<%
String ctx = request.getContextPath();
@SuppressWarnings("unchecked")
List<Employee> employeeList = (List<Employee>) request.getAttribute("employeeList");
@SuppressWarnings("unchecked")
List<String> deptList = (List<String>) request.getAttribute("deptList");
String empName = nvl((String) request.getAttribute("empName"));
String selectedDept = nvl((String) request.getAttribute("selectedDept"));
String selectedStatus = nvl((String) request.getAttribute("selectedStatus"));

LinkedHashSet<String> departments = new LinkedHashSet<String>();
if (deptList != null) {
	for (String dept : deptList) {
		if (dept != null && !dept.trim().isEmpty()) {
	departments.add(dept.trim());
		}
	}
}
%>
<!doctype html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>給与統計 社員選択 | HEXAGON PAY</title>
<style>
body {
	font-family: 'Malgun Gothic', sans-serif;
	font-size: 12px;
	color: #333;
	margin: 0;
	padding: 15px;
	background: #fff;
}

.modal-header {
	font-size: 18px;
	font-weight: bold;
	margin-bottom: 15px;
	border-bottom: 2px solid #337ab7;
	padding-bottom: 10px;
	color: #555;
}

.search-bar {
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 10px;
	margin-bottom: 10px;
	flex-wrap: wrap;
}

.search-input-group, .filter-group {
	display: flex;
	align-items: center;
	gap: 5px;
}

select, input[type="text"] {
	border: 1px solid #ccc;
	padding: 4px 8px;
	font-size: 12px;
	min-height: 28px;
	background: #fff;
	color: #333;
}

.filter-group select {
	min-width: 140px;
}

.btn-search, .btn-view-all {
	background: #f8f9fa;
	border: 1px solid #ccc;
	padding: 4px 10px;
	cursor: pointer;
	min-height: 28px;
}

.data-table {
	width: 100%;
	border-collapse: collapse;
	text-align: center;
}

.data-table th, .data-table td {
	border: 1px solid #ddd;
	padding: 8px;
}

.data-table th {
	background: #f9f9f9;
	color: #337ab7;
	font-weight: bold;
}

.table-scroll {
	max-height: 420px;
	overflow: auto;
}
</style>
</head>
<body>
	<div class="modal-header">給与統計 社員選択</div>
	<form id="empSearchForm"
		action="<%=ctx%>/Statistics/paymentStatisticsPayItemsEmployeePopup.do"
		method="get">
		<div class="search-bar">
			<div class="search-input-group">
				<input type="text" id="empNameInput" name="empName"
					placeholder="社員検索" value="<%=esc(empName)%>">
				<button type="submit" class="btn-search">検索</button>
				<button type="button" class="btn-view-all"
					onclick="location.href='<%=ctx%>/Statistics/paymentStatisticsPayItemsEmployeePopup.do'">すべて見る</button>
			</div>
			<div class="filter-group">
				<select name="department" onchange="this.form.submit()">
					<option value="">部署別</option>
					<%
					for (String dept : departments) {
					%>
					<option value="<%=esc(dept)%>"
						<%=dept.equals(selectedDept) ? "selected" : ""%>><%=esc(dept)%></option>
					<%
					}
					%>
				</select> <select name="status" onchange="this.form.submit()">
					<option value="">状態別</option>
					<option value="재직"
						<%="재직".equals(selectedStatus) ? "selected" : ""%>>在職</option>
					<option value="퇴직"
						<%="퇴직".equals(selectedStatus) ? "selected" : ""%>>退職</option>
				</select>
			</div>
		</div>
	</form>
	<div class="table-scroll">
		<table class="data-table">
			<thead>
				<tr>
					<th>区分</th>
					<th>社員番号</th>
					<th>名前</th>
					<th>部署</th>
					<th>職位</th>
					<th>状態</th>
				</tr>
			</thead>
			<tbody>
				<%
				if (employeeList != null && !employeeList.isEmpty()) {
					for (Employee emp : employeeList) {
						String employeeName = nvl(emp.getEmployeeName());
						String statusLabel = "Y".equalsIgnoreCase(emp.getRetirementYn()) ? "퇴직" : "재직";
				%>
				<tr style="cursor: pointer;" onclick="selectEmployee(this)"
					data-employee-name="<%=esc(employeeName)%>">
					<td><%=esc(nvl(emp.getEmploymentType()))%></td>
					<td><%=esc(nvl(emp.getEmployeeNo()))%></td>
					<td><%=esc(employeeName)%></td>
					<td><%=esc(nvl(emp.getDepartment()))%></td>
					<td><%=esc(nvl(emp.getPosition()))%></td>
					<td><%=statusLabel%></td>
				</tr>
				<%
				}
				} else {
				%>
				<tr>
					<td colspan="6" style="padding: 30px; color: #777;">検索された社員はいません。</td>
				</tr>
				<%
				}
				%>
			</tbody>
		</table>
	</div>
	<script>
		function selectEmployee(row) {
			if (!window.opener || window.opener.closed
					|| typeof window.opener.applyEmployee !== 'function') {
				window.close();
				return;
			}
			window.opener.applyEmployee({
				employeeName : row.getAttribute('data-employee-name') || ''
			});
			window.close();
		}
	</script>
</body>
</html>
