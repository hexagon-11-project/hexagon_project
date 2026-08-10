<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setAttribute("pageTitle", "제 증명서 발급");
request.setAttribute("pageSection", "인사관리");
request.setAttribute("pageDescription", "사원을 선택하고 재직·경력·퇴직증명서를 작성하여 인쇄합니다.");
request.setAttribute("activeKey", "certificate-issue");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
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
						<th>선택</th>
						<th>구분</th>
						<th>사원번호</th>
						<th>성명</th>
						<th>부서</th>
						<th>직위</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td><input type="checkbox"></td>
						<td>정규직</td>
						<td>No-140001</td>
						<td>김도석</td>
						<td>콘텐츠팀</td>
						<td>부장</td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>정규직</td>
						<td>No-140002</td>
						<td>송윤석</td>
						<td>기획전략팀</td>
						<td>차장</td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>정규직</td>
						<td>No-140003</td>
						<td>임현규</td>
						<td>콘텐츠팀</td>
						<td>대리</td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>일용직</td>
						<td>No-140007</td>
						<td>박찬우</td>
						<td>연구지원팀</td>
						<td>사원</td>
					</tr>
				</tbody>
			</table>
		</div>
	</section>
	<section class="certificate-workspace">
		<div class="certificate-tabs">
			<button type="button" class="active">재직증명서</button>
			<button type="button">경력증명서</button>
			<button type="button">퇴직증명서</button>
		</div>
		<div class="document-sheet source-certificate">
			<h2 class="document-title">재 직 증 명 서</h2>
			<table class="certificate-table">
				<tbody>
					<tr>
						<th rowspan="2">인적사항</th>
						<th>성명</th>
						<td></td>
						<th>주민등록번호<br>(생년월일)
						</th>
						<td></td>
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
						<td></td>
						<th>직위</th>
						<td></td>
					</tr>
					<tr>
						<th>입사일</th>
						<td></td>
						<th>근속기간</th>
						<td></td>
					</tr>
					<tr>
						<th>발급용도</th>
						<td colspan="4"><select class="select"><option>선택</option></select></td>
					</tr>
					<tr>
						<td colspan="5"><textarea
								class="textarea certificate-message">상기인은 현재 위와 같이 당사에 재직하고 있음을 증명합니다.</textarea></td>
					</tr>
					<tr>
						<td colspan="5" class="certificate-date">2026년 08월 04일</td>
					</tr>
					<tr>
						<th>발급부서</th>
						<td colspan="2"><select class="select"><option>선택</option></select></td>
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
			<button type="button" class="btn btn-primary">인쇄하기</button>
		</div>
	</section>
</div>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
