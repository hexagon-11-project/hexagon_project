<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
request.setAttribute("pageTitle", "제 증명서 발급 대장");
request.setAttribute("pageSection", "인사관리");
request.setAttribute("pageDescription", "증명서 발급 이력을 기간·증명서·사원 기준으로 조회하고 인쇄합니다.");
request.setAttribute("activeKey", "certificate-ledger");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<section class="filter-bar">
	<div class="field ">
		<label>발급기간</label>
		<div class="range">
			<input type="date" name="startDate" class="input"
				value="${startDate}"> <span>~</span> <input type="date"
				name="endDate" class="input" value="${endDate}">
		</div>
	</div>
	<div class="field ">
		<label>증명서</label><select class="select"><option value="전체"
				selected>전체</option>
			<option value="재직증명서">재직증명서</option>
			<option value="경력증명서">경력증명서</option>
			<option value="퇴직증명서">퇴직증명서</option></select>
	</div>
	<div class="field ">
		<label>사원명</label><input type="text" class="input">
	</div>
	<div class="actions">
		<button type="button" class="btn btn-primary">조회</button>
		<button type="button" class="btn ">인쇄</button>
	</div>
</section>

<section class="card ">
	<div class="card-header">
		<h2 class="section-title">증명서 발급 대장</h2>
	</div>
	<div class="card-body">
		
		<!-- 선택 삭제 기능을 위해 테이블 전체를 감싸는 폼 태그 -->
		<!-- action 속성의 경로는 본인의 매핑 주소에 맞게 수정하세요 -->
		<form action="${pageContext.request.contextPath}/Person/certificateRegisterUpdate.do" method="POST" onsubmit="return confirm('선택한 증명서를 정말 취소 처리하시겠습니까?');">
			<div class="table-toolbar">
				<span class="table-count">총 3건</span>
				<div class="actions">
					<!-- 자바스크립트 없이 폼 데이터를 넘기기 위해 type을 submit으로 변경 -->
					<button type="submit" class="btn btn-danger">선택 삭제</button>
					<button type="button" class="btn btn-danger">전체 삭제</button>
				</div>
			</div>
			
			<div class="table-wrap">
				<table class="data-table ">
					<thead>
						<tr>
							<th>선택</th>
							<th>발급번호</th>
							<th>발급일</th>
							<th>성명</th>
							<th>증명서</th>
							<th>용도</th>
							<th>발급자</th>
							<th>상태</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="cert" items="${certList}">
							<tr>
								<!--  서버로 발급번호를 넘기기 위한 name, value 속성 추가 -->
								<td><input type="checkbox" name="issueNo" value="${cert.issueNo}"></td>
								<td>${cert.issueNo}</td>
								<td>${cert.issueDate}</td>
								<td>${cert.employeeName}</td>
								<td>${cert.certificateTypeCode}</td>
								<td>${cert.purpose}</td>
								<td>${cert.regId}</td>
								
								<!-- 상태값(Y/N)에 따라 텍스트와 색상 다르게 출력 -->
								<td>
									<c:choose>
										<c:when test="${cert.certificateYn == 'Y'}">
											<span style="color: green;">발급</span>
										</c:when>
										<c:otherwise>
											<span style="color: red; font-weight: bold;">취소</span>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</form> <!-- 폼 닫기 -->
		
	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>