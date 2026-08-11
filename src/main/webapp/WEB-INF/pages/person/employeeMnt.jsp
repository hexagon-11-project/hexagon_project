<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
request.setAttribute("pageTitle", "사원현황/관리");
request.setAttribute("pageSection", "인사관리");
request.setAttribute("pageDescription", "재직·휴직·퇴직 사원을 조회하고 선택한 사원의 관련 화면으로 이동합니다.");
request.setAttribute("activeKey", "employee-list");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<div class="employee-status-strip">
	<button type="button" class="employee-status active">
		<span>재직</span><strong>${countMap.active}</strong>
	</button>
	<button type="button" class="employee-status">
		<span>정규직</span><strong>${countMap.regular}</strong>
	</button>
	<button type="button" class="employee-status">
		<span>계약직</span><strong>${countMap.contract}</strong>
	</button>
	<button type="button" class="employee-status">
		<span>임시직</span><strong>${countMap.temp}</strong>
	</button>
	<button type="button" class="employee-status">
		<span>일용직</span><strong>${countMap.daily}</strong>
	</button>
	<button type="button" class="employee-status">
		<span>퇴직</span><strong>${countMap.retire}</strong>
	</button>
	<button type="button" class="employee-status dark">
		<span>전체</span><strong>${countMap.total}</strong>
	</button>
</div>

<!-- 오타(ass="...") 수정 완료 -->
<div class="employee-list-tools"> 
	<div class="search-strip">
		<select class="select">
			<option>성명</option>
			<option>사원번호</option>
			<option>부서</option>
		</select>
		<input class="input" type="text" placeholder="검색어 입력">
		<button type="button" class="btn btn-primary">전체보기</button>
	</div>
	<div class="search-strip">
		<select class="select"><option>고용형태별</option></select>
		<select class="select"><option>상태별</option></select>
		<select class="select"><option>30개 보기</option></select>
		<button type="button" class="btn">정렬기준 설정하기</button>
	</div>
</div>

<!-- ⭐️ 추가됨: 삭제할 데이터를 서버로 보내기 위해 Table 전체를 form으로 감쌉니다 -->

<form id="deleteForm" action="${pageContext.request.contextPath}/Person/employeeMntDelete.do" method="POST">
	<div class="table-wrap">
		<table class="data-table source-data-table employee-master-table">
			<thead>
				<tr>
					<th>선택</th>
					<th>구분</th>
					<th>사원번호</th>
					<th>성명</th>
					<th>부서</th>
					<th>직위</th>
					<th>생년월일</th>
					<th>입사일</th>
					<th>휴대폰</th>
					<th>이메일</th>
					<th>상태</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="emp" items="${employeePage.content}">
					<tr>
						<!-- ⭐️ 추가됨: 체크박스에 name과 value(사번) 부여 -->
						<td><input type="checkbox" name="empId" value="${emp.employeeId}"></td>
<%-- <td><input type="checkbox" name="empId" value="${emp.employeeNo}"></td> --%>
						<td>${emp.employmentType}</td>
						<td>${emp.employeeNo}</td>
						<td><strong>${emp.employeeName}</strong></td>
						<td>${emp.department}</td>
						<td>${emp.position}</td>
						<td>${emp.birthDate}</td>
						<td>${emp.hireDate}</td>
						<td>${emp.mobile}</td>
						<td>${emp.email}</td>
						<td>${emp.retirementYn == 'Y' ? '퇴직' : '재직'}</td>
					</tr>
				</c:forEach>
				<c:if test="${employeePage.hasNoEmployees()}">
					<tr>
						<td colspan="11">등록된 사원이 없습니다.</td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</div>
</form> <!-- form 끝 -->

<div class="source-pagination">
	<!-- 이전 구간으로 이동 (‹ 이전페이지) -->
	<c:if test="${employeePage.startPage > 5}">
		<a href="employeeMnt.do?page=${employeePage.startPage - 5}">‹ 이전페이지</a>
	</c:if>

	<!-- 페이지 번호 출력 -->
	<c:forEach var="pNo" begin="${employeePage.startPage}" end="${employeePage.endPage}">
		<c:choose>
			<c:when test="${employeePage.currentPage == pNo}">
				<strong>${pNo}</strong>
			</c:when>
			<c:otherwise>
				<a href="employeeMnt.do?page=${pNo}">${pNo}</a>
			</c:otherwise>
		</c:choose>
	</c:forEach>

	<!-- 다음 구간으로 이동 (다음페이지 ›) -->
	<c:if test="${employeePage.endPage < employeePage.totalPages}">
		<a href="employeeMnt.do?page=${employeePage.startPage + 5}">다음페이지 ›</a>
	</c:if>
</div>

<div class="source-bottom-actions">
	<button type="button" class="btn btn-primary"
		onclick="location.href='${pageContext.request.contextPath}/Config/employeeIns1.do'">신규사원등록</button>
	<!-- ⭐️ 추가됨: 버튼 클릭 시 자바스크립트 함수 호출 -->
	<!-- <button type="submit" class="btn">선택 삭제</button> -->
	<button type="submit" form="deleteForm" class="btn">선택 삭제</button>
</div>


<%@ include file="/WEB-INF/jspf/app-end.jspf"%>