<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- ★ JSTL 사용을 위한 태그 라이브러리 추가 -->
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

<!-- ★ 퇴직증명서 예외 처리 알림창 (JS 없이 HTML로 화면에 노출) -->
<c:if test="${not empty alertMessage}">
	<div
		style="color: #d9534f; background-color: #f2dede; border: 1px solid #ebccd1; padding: 15px; margin-bottom: 20px; border-radius: 4px; font-weight: bold;">
		※ ${alertMessage}</div>
</c:if>

<div class="certificate-source-layout">
	<section class="source-list-panel">
		<div class="source-list-search">
			<input class="input" type="text" placeholder="검색어 입력">
			<button class="btn btn-primary" type="button">전체보기</button>
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
						<!-- ★ 상태 컬럼 추가 -->
					</tr>
				</thead>
				<tbody>
					<!-- ★ 서버에서 넘겨받은 사원 리스트 반복 출력 -->
					<c:forEach var="emp" items="${empList}">
						<!-- 선택된 사원의 행(tr) 배경색을 살짝 다르게 표시 (선택사항) -->
						<tr
							<c:if test="${emp.employeeNo == selectedEmpNo}">style="background-color: #f0f8ff;"</c:if>>
							<!-- <td><input type="checkbox"...></td> 삭제됨 -->
							<td>${emp.employmentType}</td>
							<td>${emp.employeeNo}</td>s
							<!-- ★ a태그의 href 경로를 반드시 본인의 프로젝트 환경에 맞게 수정해야 합니다! -->
							<td><a
								href="${pageContext.request.contextPath}/Person/certificatePrintWorking.do?employeeNo=${emp.employeeNo}&certType=${selectedCertType}"
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
			<!-- ★ button 태그를 a 태그로 변경하여 자바스크립트 없이 탭 이동 처리 -->
			<!-- 현재 탭이 선택된 탭과 일치하면 active 클래스를 부여하여 디자인 유지 -->
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

		<div class="document-sheet source-certificate">
			<!-- ★ 선택된 증명서 종류에 따라 타이틀 변경 -->
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
						<!-- ★ 이름 바인딩 -->
						<th>주민등록번호<br>(생년월일)
						</th>
						<td>${empDetail.residentRegNo}</td>
						<!-- ★ 주민번호(마스킹) 바인딩 -->
					</tr>
					<tr>
						<th>주소</th>
						<td colspan="3"></td>
						<!-- 주소는 Employee 모델에 없으므로 공란 처리 -->
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
						<!-- ★ 부서 바인딩 -->
						<th>직위</th>
						<td>${empDetail.position}</td>
						<!-- ★ 직위 바인딩 -->
					</tr>
					<tr>
						<th>입사일</th>
						<td>${empDetail.hireDate}</td>
						<!-- ★ 입사일 바인딩 -->
						<th>근속기간</th>
						<td>${workPeriod}</td>
						<!-- ★ 서비스에서 계산된 근속기간 바인딩 -->
					</tr>
					<tr>
						<th>발급용도</th>
						<td colspan="4"><select class="select">
								<option>선택</option>
								<option value="은행제출용">금융기관 제출용</option>
								<option value="관공서제출용">관공서 제출용</option>
								<option value="회사제출용">타 회사 제출용</option>
						</select></td>
					</tr>
					<tr>
						<!-- ★ 증명서 종류에 따라 Service에서 결정된 문구 바인딩 -->
						<td colspan="5"><textarea
								class="textarea certificate-message">${certText}</textarea></td>
					</tr>
					<tr>
						<!-- ★ Handler에서 넘어온 오늘 날짜 바인딩 -->
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
			<div class="signature-area">
				<p>
					<strong>(주)헥사곤아이티 대표이사</strong>
				</p>
				<div class="seal-box">직인</div>
			</div>
		</div>
		<div class="source-bottom-actions">
			<!-- 윈도우 기본 인쇄 기능을 호출하는 간단한 인라인 JS 스크립트만 추가했습니다. -->
			<button type="button" class="btn btn-primary"
				onclick="window.print();">인쇄하기</button>
		</div>
	</section>
</div>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>