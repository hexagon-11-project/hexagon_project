<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setAttribute("pageTitle", "사원현황/관리");
request.setAttribute("pageSection", "인사관리");
request.setAttribute("pageDescription", "재직·휴직·퇴직 사원을 조회하고 선택한 사원의 관련 화면으로 이동합니다.");
request.setAttribute("activeKey", "employee-list");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<div class="employee-status-strip">
	<button type="button" class="employee-status active">
		<span>재직</span><strong>22</strong>
	</button>
	<button type="button" class="employee-status">
		<span>정규직</span><strong>9</strong>
	</button>
	<button type="button" class="employee-status">
		<span>계약직</span><strong>2</strong>
	</button>
	<button type="button" class="employee-status">
		<span>임시직</span><strong>0</strong>
	</button>
	<button type="button" class="employee-status">
		<span>일용직</span><strong>11</strong>
	</button>
	<button type="button" class="employee-status">
		<span>퇴직</span><strong>5</strong>
	</button>
	<button type="button" class="employee-status dark">
		<span>전체</span><strong>27</strong>
	</button>
</div>
<div class="employee-list-tools">
	<div class="search-strip">
		<select class="select"><option>성명</option>
			<option>사원번호</option>
			<option>부서</option></select><input class="input" type="text"
			placeholder="검색어 입력">
		<button type="button" class="btn btn-primary">전체보기</button>
	</div>
	<div class="search-strip">
		<select class="select"><option>고용형태별</option></select><select
			class="select"><option>상태별</option></select><select class="select"><option>30개
				보기</option></select>
		<button type="button" class="btn">정렬기준 설정하기</button>
	</div>
</div>
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
			<tr>
				<td><input type="checkbox"></td>
				<td>정규직</td>
				<td>No-140019</td>
				<td><strong>박영국</strong></td>
				<td>콘텐츠팀</td>
				<td>주임</td>
				<td>1985-06-11</td>
				<td>2009-07-08</td>
				<td>010-9623-0000</td>
				<td>bok@yesform.com</td>
				<td>재직</td>
			</tr>
			<tr>
				<td><input type="checkbox"></td>
				<td>정규직</td>
				<td>No-140012</td>
				<td><strong>강호준</strong></td>
				<td>개발팀</td>
				<td>주임</td>
				<td>1987-11-06</td>
				<td>2009-09-18</td>
				<td>010-5797-0000</td>
				<td>joo@yesform.com</td>
				<td>재직</td>
			</tr>
			<tr>
				<td><input type="checkbox"></td>
				<td>정규직</td>
				<td>No-140011</td>
				<td><strong>김광민</strong></td>
				<td>개발팀</td>
				<td>주임</td>
				<td>1982-05-16</td>
				<td>2009-07-16</td>
				<td>010-7723-0000</td>
				<td>kim@yesform.com</td>
				<td>퇴직</td>
			</tr>
			<tr>
				<td><input type="checkbox"></td>
				<td>정규직</td>
				<td>No-140003</td>
				<td><strong>오동희</strong></td>
				<td>콘텐츠팀</td>
				<td>주임</td>
				<td>1984-07-12</td>
				<td>2010-03-04</td>
				<td>010-4561-0000</td>
				<td>dong@yesform.com</td>
				<td>재직</td>
			</tr>
			<tr>
				<td><input type="checkbox"></td>
				<td>계약직</td>
				<td>No-140028</td>
				<td><strong>어수정</strong></td>
				<td>연구지원팀</td>
				<td>사원</td>
				<td>1986-10-23</td>
				<td>2014-04-07</td>
				<td>010-6475-0000</td>
				<td>jae@yesform.com</td>
				<td>재직</td>
			</tr>
			<tr>
				<td><input type="checkbox"></td>
				<td>일용직</td>
				<td>No-140007</td>
				<td><strong>박찬우</strong></td>
				<td>연구지원팀</td>
				<td>사원</td>
				<td>1988-12-05</td>
				<td>2012-01-03</td>
				<td>010-4455-0000</td>
				<td>park@yesform.com</td>
				<td>퇴직</td>
			</tr>
		</tbody>
	</table>
</div>
<div class="source-pagination">
	‹ 이전페이지 <strong>1</strong> 다음페이지 ›
</div>
<div class="source-bottom-actions">
	<button type="button" class="btn btn-primary">신규사원등록</button>
	<button type="button" class="btn">선택 삭제</button>
</div>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
