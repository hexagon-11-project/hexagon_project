<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="config.employee.model.Employee"%>
<%!
private String nvl(String value) {
	return value == null ? "" : value;
}

private String esc(String value) {
	if (value == null || value.isEmpty()) {
		return "";
	}
	return value.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
}
%>
<%
String ctx = request.getContextPath();
@SuppressWarnings("unchecked")
List<Employee> employeeList = (List<Employee>) request.getAttribute("employeeList");
@SuppressWarnings("unchecked")
List<String> deptList = (List<String>) request.getAttribute("deptList");
if (deptList == null) {
	deptList = new ArrayList<String>();
}
String empName = nvl((String) request.getAttribute("empName"));
String selectedDept = nvl((String) request.getAttribute("selectedDept"));
String selectedStatus = nvl((String) request.getAttribute("selectedStatus"));
%>
<!doctype html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>급여통계 사원선택 | HEXAGON PAY</title>
<link rel="stylesheet" href="<%=ctx%>/assets/css/base/variables.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/base/reset.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/base/typography.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/components/buttons.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/components/tables.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/pages/source-faithful.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/pages/environment.css">
<style>
body {
	margin: 0;
	padding: 16px;
	background: #fff;
}

.popup-section-head {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 10px;
	padding: 9px 11px;
	border-top: 2px solid #3f8fc4;
	border-bottom: 1px solid var(--line);
}

.popup-section-head .source-section-title {
	padding: 0;
	border: 0;
}

.search-bar {
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 10px;
	padding: 10px 0 12px;
	flex-wrap: wrap;
}

.search-input-group,
.filter-group {
	display: flex;
	align-items: center;
	gap: 6px;
}

.search-input-group input[type="text"],
.filter-group select {
	border: 1px solid #ccc;
	padding: 5px 8px;
	font-size: 12px;
	min-height: 28px;
}

.btn-search,
.btn-view-all {
	background: #f8f9fa;
	border: 1px solid #ccc;
	padding: 5px 10px;
	cursor: pointer;
	font-size: 12px;
	min-height: 28px;
}

.table-scroll {
	max-height: 420px;
	overflow: auto;
}
</style>
</head>
<body>
	<section class="source-config-block">
		<div class="source-config-list">
			<div class="popup-section-head">
				<div class="source-section-title">급여통계 사원선택</div>
			</div>
			<form id="empSearchForm" action="<%=ctx%>/Statistics/paymentStatisticsPayItemsEmployeePopup.do" method="get">
				<div class="search-bar">
					<div class="search-input-group">
						<input type="text" id="empNameInput" name="empName" placeholder="사원검색"
							value="<%=esc(empName)%>">
						<button type="submit" class="btn-search">검색</button>
						<button type="button" class="btn-view-all"
							onclick="location.href='<%=ctx%>/Statistics/paymentStatisticsPayItemsEmployeePopup.do'">전체보기</button>
					</div>
					<div class="filter-group">
						<select name="department" onchange="this.form.submit()">
							<option value="">부서별</option>
							<%
							for (String dept : deptList) {
							%>
							<option value="<%=esc(dept)%>" <%=dept.equals(selectedDept) ? "selected" : ""%>><%=esc(dept)%></option>
							<%
							}
							%>
						</select>
						<select name="status" onchange="this.form.submit()">
							<option value="">상태별</option>
							<option value="재직" <%="재직".equals(selectedStatus) ? "selected" : ""%>>재직</option>
							<option value="퇴직" <%="퇴직".equals(selectedStatus) ? "selected" : ""%>>퇴직</option>
						</select>
					</div>
				</div>
			</form>
			<div class="table-wrap table-scroll">
				<table class="data-table source-data-table">
					<thead>
						<tr>
							<th>구분</th>
							<th>사원번호</th>
							<th>성명</th>
							<th>부서</th>
							<th>직위</th>
							<th>상태</th>
						</tr>
					</thead>
					<tbody>
						<%
						if (employeeList != null && !employeeList.isEmpty()) {
							for (Employee emp : employeeList) {
								String employeeName = nvl(emp.getEmployeeName());
								String statusLabel = "Y".equalsIgnoreCase(emp.getRetirementYn()) ? "퇴직" : "재직";
						%>
						<tr style="cursor: pointer;"
							onclick="selectEmployee(this)"
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
							<td colspan="6" style="padding: 30px; color: #777;">검색된 사원이 없습니다.</td>
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
		function selectEmployee(row) {
			if (!window.opener || window.opener.closed || typeof window.opener.applyEmployee !== 'function') {
				window.close();
				return;
			}
			window.opener.applyEmployee({
				employeeName: row.getAttribute('data-employee-name') || ''
			});
			window.close();
		}
	</script>
</body>
</html>
