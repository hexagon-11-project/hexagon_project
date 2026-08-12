<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setAttribute("pageTitle", "제 증명서 발급 대장");
request.setAttribute("pageSection", "인사관리");
request.setAttribute("pageDescription", "증명서 발급 이력을 기간·증명서·사원 기준으로 조회하고 인쇄합니다.");
request.setAttribute("activeKey", "certificate-ledger");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<section class="filter-bar">
	<div class="field ">
		<label>발급기간</label>
		<div class="range">
			<input type="date" class="input" value="2026-07-01"><span>~</span><input
				type="date" class="input" value="2026-08-04">
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
		<div class="table-toolbar">
			<span class="table-count">총 3건</span>
			<div class="actions">
				<button type="button" class="btn btn-danger">선택 삭제</button>
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
					<tr>
						<td><input type="checkbox"></td>
						<td>2026-0003</td>
						<td>2026-08-03</td>
						<td>김민준</td>
						<td>재직증명서</td>
						<td>은행 제출</td>
						<td>송지수</td>
						<td>발급</td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>2026-0002</td>
						<td>2026-07-28</td>
						<td>박서연</td>
						<td>경력증명서</td>
						<td>관공서 제출</td>
						<td>송지수</td>
						<td>발급</td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>2026-0001</td>
						<td>2026-07-15</td>
						<td>이도윤</td>
						<td>재직증명서</td>
						<td>회사 제출</td>
						<td>송지수</td>
						<td>취소</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
s