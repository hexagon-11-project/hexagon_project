<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
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
%>
<!doctype html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>사원 선택 | HEXAGON PAY</title>
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
</style>
</head>
<body>
	<section class="source-config-block">
		<div class="source-config-list">
			<div class="popup-section-head">
				<div class="source-section-title">사원 선택</div>
			</div>
			<div class="table-wrap">
				<table class="data-table source-data-table">
					<thead>
						<tr>
							<th>사원 구분</th>
							<th>사원번호</th>
							<th>이름</th>
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
							<td colspan="6" style="padding: 30px; color: #777;">등록된 사원이 없습니다.</td>
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
