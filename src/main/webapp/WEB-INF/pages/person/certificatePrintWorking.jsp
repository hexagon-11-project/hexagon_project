<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!--  JSTL 사용을 위한 태그 라이브러리 추가 -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
request.setAttribute("pageTitle", "제 증명서 발급");
request.setAttribute("pageSection", "인사관리");
request.setAttribute("pageDescription", "사원을 선택하고 재직·경력·퇴직증명서를 작성하여 인쇄합니다.");
request.setAttribute("activeKey", "certificate-issue");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<!--  퇴직증명서 예외 처리 알림창 (JS 없이 HTML로 화면에 노출) -->
<c:if test="${not empty alertMessage}">
	<div
		style="color: #d9534f; background-color: #f2dede; border: 1px solid #ebccd1; padding: 15px; margin-bottom: 20px; border-radius: 4px; font-weight: bold;">
		※ ${alertMessage}</div>
</c:if>

<div class="certificate-source-layout">
	<section class="source-list-panel">
		<div class="source-list-search">
			<!-- 검색 폼: GET 방식으로 검색어(searchName) 전송 -->
			<form
				action="${pageContext.request.contextPath}/Person/certificatePrintWorking.do"
				method="GET" style="display: flex; gap: 5px; width: 100%;">
				<!-- 선택된 사원 정보와 증명서 타입 유지를 위한 hidden 필드 -->
				<input type="hidden" name="employeeNo" value="${selectedEmpNo}">
				<input type="hidden" name="certType" value="${selectedCertType}">

				<input class="input" type="text" name="searchName"
					placeholder="이름 입력" value="${param.searchName}">
				<button class="btn btn-primary" type="submit">검색</button>
				<button class="btn btn-primary" type="button"
					onclick="location.href='${pageContext.request.contextPath}/Person/certificatePrintWorking.do'"
					style="background-color: #6c757d; border-color: #6c757d;">전체보기</button>
			</form>
		</div>
		<div class="table-wrap">
			<table class="data-table source-data-table compact-list">
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
					<c:forEach var="emp" items="${empList}">
						<tr
							<c:if test="${emp.employeeNo == selectedEmpNo}">style="background-color: #f0f8ff;"</c:if>>
							<td>${emp.employmentType}</td>
							<td>${emp.employeeNo}</td>
							<td><a
								href="${pageContext.request.contextPath}/Person/certificatePrintWorking.do?employeeNo=${emp.employeeNo}&certType=${selectedCertType}&searchName=${param.searchName}"
								style="color: #0056b3; text-decoration: underline; font-weight: bold;">
									${emp.employeeName} </a></td>
							<td>${emp.department}</td>
							<td>${emp.position}</td>
							<td><c:choose>
									<c:when test="${emp.retirementYn == 'Y'}">
										<span style="color: gray;">퇴직</span>
									</c:when>
									<c:otherwise>
										<span style="color: blue;">재직</span>
									</c:otherwise>
								</c:choose></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</section>

	<section class="certificate-workspace">
		<div class="certificate-tabs">
			<a
				href="${pageContext.request.contextPath}/Person/certificatePrintWorking.do?employeeNo=${selectedEmpNo}&certType=재직증명서"
				class="btn ${selectedCertType == '재직증명서' ? 'active' : ''}"
				style="text-decoration: none;">재직증명서</a> <a
				href="${pageContext.request.contextPath}/Person/certificatePrintWorking.do?employeeNo=${selectedEmpNo}&certType=경력증명서"
				class="btn ${selectedCertType == '경력증명서' ? 'active' : ''}"
				style="text-decoration: none;">경력증명서</a> <a
				href="${pageContext.request.contextPath}/Person/certificatePrintWorking.do?employeeNo=${selectedEmpNo}&certType=퇴직증명서"
				class="btn ${selectedCertType == '퇴직증명서' ? 'active' : ''}"
				style="text-decoration: none;">퇴직증명서</a>
		</div>

		<form
			action="${pageContext.request.contextPath}/Person/certificatePrintWorkingInsert.do"
			method="POST">

			<input type="hidden" name="employeeNo" value="${selectedEmpNo}">
			<input type="hidden" name="certificateTypeCode"
				value="${selectedCertType}">
			<input type="hidden" name="issueNo" value="ISSUE-${today}">

			<div class="document-sheet source-certificate">
				<h2 class="document-title">
					<c:choose>
						<c:when test="${selectedCertType == '경력증명서'}">경 력 증 명 서</c:when>
						<c:when test="${selectedCertType == '퇴직증명서'}">퇴 직 증 명 서</c:when>
						<c:otherwise>재 직 증 명 서</c:otherwise>
					</c:choose>
				</h2>

				<table class="certificate-table">
					<tbody>
						<tr>
							<th rowspan="2">인적사항</th>
							<th>성명</th>
							<td>${empDetail.employeeName}</td>
							<th>주민등록번호<br>(생년월일)
							</th>
							<td>${empDetail.residentRegNo}</td>
						</tr>
						<tr>
							<th>주소</th>
							<td colspan="3"></td>
						</tr>
						<tr>
							<th rowspan="3">재직사항</th>
							<th>회사명</th>
							<td>(주)헥사곤아이티</td>
							<th>사업자번호</th>
							<td>123-45-67890</td>
						</tr>
						<tr>
							<th>부서</th>
							<td>${empDetail.department}</td>
							<th>직위</th>
							<td>${empDetail.position}</td>
						</tr>
						<tr>
							<th>입사일</th>
							<td>${empDetail.hireDate}</td>
							<th>근속기간</th>
							<td>${workPeriod}</td>
						</tr>

						<tr>
							<th>발급용도</th>
							<td colspan="4"><select class="select" name="purpose"
								required>
									<option value="">선택</option>
									<option value="금융기관 제출용">금융기관 제출용</option>
									<option value="관공서 제출용">관공서 제출용</option>
									<option value="타 회사 제출용">타 회사 제출용</option>
							</select></td>
						</tr>

						<tr>
							<th>제출처</th>
							<td colspan="4"><input type="text" class="input"
								name="submissionTarget" placeholder="제출처를 입력하세요 (예: XX은행)"
								style="width: 100%;"></td>
						</tr>

						<tr>
							<td colspan="5"><textarea
									class="textarea certificate-message">${certText}</textarea></td>
						</tr>
						<tr>
							<td colspan="5" class="certificate-date">${today}</td>
						</tr>
						<tr>
							<th>발급부서</th>
							<td colspan="2"><select class="select">
									<option>선택</option>
									<option value="인사팀">인사팀</option>
									<option value="경영지원팀">경영지원팀</option>
							</select></td>
							<th>연락처</th>
							<td>02-0000-0000</td>
						</tr>
					</tbody>
				</table>
				<div class="signature-area"
					style="display: flex; align-items: center; justify-content: flex-end; gap: 10px;">
					<p style="margin: 0;">
						<strong>(주)헥사곤아이티 대표이사</strong>
					</p>
					<div class="seal-box">
						<img
							src="${pageContext.request.contextPath}/assets/images/Seal.png"
							alt="직인" style="width: 60px; height: 60px;">
					</div>
				</div>

				<div class="source-bottom-actions">
					<button type="submit" class="btn btn-primary">저장하기</button>
				</div>
		</form>
		<!--  form 태그 닫기 -->
	</section>
</div>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>