<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setAttribute("pageTitle", "항목별 대장");
request.setAttribute("pageSection", "급여관리");
request.setAttribute("pageDescription", "조회기간과 지급·공제항목을 선택해 사원별 항목 금액과 합계를 확인합니다.");
request.setAttribute("activeKey", "item-ledger");
request.setAttribute("pageCss", "payroll.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<section class="filter-bar">
	<div class="field ">
		<label>조회기간</label>
		<div class="range">
			<input type="month" class="input" value="2026-01"><span>~</span><input
				type="month" class="input" value="2026-08">
		</div>
	</div>
	<div class="field ">
		<label>항목</label><select class="select"><option value="기본급"
				selected>기본급</option>
			<option value="식대">식대</option>
			<option value="연장근로수당">연장근로수당</option>
			<option value="국민연금">국민연금</option>
			<option value="건강보험">건강보험</option></select>
	</div>
	<div class="actions">
		<button type="button" class="btn btn-primary">조회</button>
		<button type="button" class="btn ">인쇄</button>
	</div>
</section>
<section class="card ">
	<div class="card-header">
		<h2 class="section-title">기본급 항목별 대장</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table ">
				<thead>
					<tr>
						<th>귀속연월</th>
						<th>사번</th>
						<th>성명</th>
						<th>부서</th>
						<th>항목명</th>
						<th>금액</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>2026-08</td>
						<td>No-140031</td>
						<td>김민준</td>
						<td>기획전략팀</td>
						<td>기본급</td>
						<td>4,200,000</td>
					</tr>
					<tr>
						<td>2026-08</td>
						<td>No-140032</td>
						<td>박서연</td>
						<td>콘텐츠팀</td>
						<td>기본급</td>
						<td>3,200,000</td>
					</tr>
					<tr>
						<td>2026-08</td>
						<td>No-140033</td>
						<td>이도윤</td>
						<td>개발팀</td>
						<td>기본급</td>
						<td>2,400,000</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="tfoot-summary">
			<span>항목 합계 9,800,000원</span>
		</div>
	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
