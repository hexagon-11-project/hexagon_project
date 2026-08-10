<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div style="flex: 1.2; background: #fff; border: 1px solid #ddd; padding: 10px;">
	<table class="table table-bordered table-hover" style="width: 100%; border-collapse: collapse; font-size: 12px; margin-bottom: 0;">
		<thead style="background: #f4f6f9;">
			<tr>
				<th style="border: 1px solid #ddd; padding: 6px; text-align: center; color: #337ab7;">구분</th>
				<th style="border: 1px solid #ddd; padding: 6px; text-align: center; color: #337ab7;">성명</th>
				<th style="border: 1px solid #ddd; padding: 6px; text-align: center; color: #337ab7;">부서</th>
				<th style="border: 1px solid #ddd; padding: 6px; text-align: center;">지급총액</th>
				<th style="border: 1px solid #ddd; padding: 6px; text-align: center;">공제총액</th>
				<th style="border: 1px solid #ddd; padding: 6px; text-align: center;">실지급액</th>
			</tr>
		</thead>
		<tbody id="employeeTableBody">
			<c:forEach var="emp" items="${employeeList}">
				<tr onclick="selectEmployeeRow(this, '${emp.payrollEmployeeId}')" style="cursor: pointer;">
					<td style="border: 1px solid #ddd; padding: 6px; text-align: center;">${emp.employmentType}</td>
					<td style="border: 1px solid #ddd; padding: 6px; text-align: center;">${emp.employeeName}</td>
					<td style="border: 1px solid #ddd; padding: 6px; text-align: center;">${emp.department}</td>
					<td style="border: 1px solid #ddd; padding: 6px; text-align: right; color: #337ab7; font-weight: bold;"><fmt:formatNumber value="${emp.totalPayAmount}" pattern="#,###"/></td>
					<td style="border: 1px solid #ddd; padding: 6px; text-align: right; color: #d9534f; font-weight: bold;"><fmt:formatNumber value="${emp.totalDeductionAmount}" pattern="#,###"/></td>
					<td style="border: 1px solid #ddd; padding: 6px; text-align: right;"><fmt:formatNumber value="${emp.netPayAmount}" pattern="#,###"/></td>
				</tr>
			</c:forEach>
			<c:if test="${empty employeeList}">
				<tr>
					<td colspan="6" style="border: 1px solid #ddd; padding: 25px; text-align: center; color: #666;">등록된 사원 데이터가 없습니다.</td>
				</tr>
			</c:if>
		</tbody>
	</table>
</div>