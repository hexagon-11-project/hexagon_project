<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setAttribute("pageTitle", "사원 퇴직처리");
request.setAttribute("pageSection", "퇴직관리");
request.setAttribute("pageDescription", "재직·퇴직 상태와 퇴직급여 입력 여부를 확인하고 퇴직처리 또는 취소합니다.");
request.setAttribute("activeKey", "retirement-process");
request.setAttribute("pageCss", "retirement.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<section class="filter-bar source-simple-filter">
	<select class="select"><option>성명</option>
		<option>사원번호</option></select><input class="input" type="text"
		placeholder="검색어를 입력하세요">
	<button type="button" class="btn btn-primary">전체보기</button>
	<select class="select"><option>상태별</option>
		<option>재직</option>
		<option>퇴직</option></select>
</section>
<div class="table-wrap">
	<table class="data-table source-data-table">
		<thead>
			<tr>
				<th>순번</th>
				<th>상태</th>
				<th>사원번호</th>
				<th>성명</th>
				<th>부서</th>
				<th>직위</th>
				<th>입사일</th>
				<th>퇴직일</th>
				<th>근속연수</th>
				<th>중간정산</th>
				<th>퇴직정산</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>1</td>
				<td>재직</td>
				<td>No-140001</td>
				<td>김도석</td>
				<td>콘텐츠팀</td>
				<td>부장</td>
				<td>2007-04-04</td>
				<td>-</td>
				<td>8년</td>
				<td>×</td>
				<td>×</td>
			</tr>
			<tr>
				<td>2</td>
				<td>재직</td>
				<td>No-140002</td>
				<td>송윤석</td>
				<td>기획전략팀</td>
				<td>차장</td>
				<td>2009-02-03</td>
				<td>-</td>
				<td>6년</td>
				<td>×</td>
				<td>●</td>
			</tr>
			<tr>
				<td>3</td>
				<td>퇴직</td>
				<td>No-140003</td>
				<td>오동희</td>
				<td>콘텐츠팀</td>
				<td>주임</td>
				<td>2010-03-04</td>
				<td>2014-10-23</td>
				<td>5년</td>
				<td>×</td>
				<td>●</td>
			</tr>
			<tr>
				<td>4</td>
				<td>재직</td>
				<td>No-140004</td>
				<td>임현규</td>
				<td>콘텐츠팀</td>
				<td>대리</td>
				<td>2010-07-07</td>
				<td>-</td>
				<td>5년</td>
				<td>×</td>
				<td>×</td>
			</tr>
		</tbody>
	</table>
</div>
<div class="source-pagination">
	‹ 이전페이지 <strong>1</strong> 다음페이지 ›
</div>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
