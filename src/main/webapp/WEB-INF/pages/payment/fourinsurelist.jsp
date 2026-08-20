<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setAttribute("pageTitle", "4대보험 공제내역");
request.setAttribute("pageSection", "급여관리");
request.setAttribute("pageDescription", "귀속연월·차수별 사원의 국민연금, 건강보험, 장기요양보험, 고용보험 공제액을 조회합니다.");
request.setAttribute("activeKey", "insurance-deduction");
request.setAttribute("pageCss", "payroll.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<section class="filter-bar">
	<div class="field ">
		<label>귀속연월</label><input type="month" class="input" value="2026-08">
	</div>
	<div class="field ">
		<label>급여차수</label><select class="select"><option value="1차"
				selected>1차</option>
			<option value="2차">2차</option></select>
	</div>
	<div class="field ">
		<label>부서</label><select class="select"><option value="전체"
				selected>전체</option>
			<option value="기획전략팀">기획전략팀</option>
			<option value="콘텐츠팀">콘텐츠팀</option>
			<option value="개발팀">개발팀</option></select>
	</div>
	<div class="actions">
		<button type="button" class="btn btn-primary">조회</button>
		<button type="button" class="btn ">인쇄</button>
	</div>
</section>
<section class="card ">
	<div class="card-header">
		<h2 class="section-title">4대보험 공제내역</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table ">
				<thead>
					<tr>
						<th>사번</th>
						<th>성명</th>
						<th>부서</th>
						<th>국민연금</th>
						<th>건강보험</th>
						<th>장기요양보험</th>
						<th>고용보험</th>
						<th>공제합계</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>No-140031</td>
						<td>김민준</td>
						<td>기획전략팀</td>
						<td>189,000</td>
						<td>148,000</td>
						<td>19,200</td>
						<td>37,800</td>
						<td>394,000</td>
					</tr>
					<tr>
						<td>No-140032</td>
						<td>박서연</td>
						<td>콘텐츠팀</td>
						<td>144,000</td>
						<td>112,800</td>
						<td>14,600</td>
						<td>28,800</td>
						<td>300,200</td>
					</tr>
					<tr>
						<td>No-140033</td>
						<td>이도윤</td>
						<td>개발팀</td>
						<td>108,000</td>
						<td>84,600</td>
						<td>10,900</td>
						<td>21,600</td>
						<td>225,100</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="tfoot-summary">
			<span>4대보험 합계 919,300원</span>
		</div>
	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
