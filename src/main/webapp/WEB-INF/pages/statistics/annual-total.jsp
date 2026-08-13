<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setAttribute("pageTitle", "연도별 전체급여 통계");
request.setAttribute("pageSection", "급여통계");
request.setAttribute("pageDescription", "최근 10년 회사 전체 급여총액과 급여인원 추이를 연도별로 확인합니다.");
request.setAttribute("activeKey", "annual-total");
request.setAttribute("pageCss", "statistics.css");
request.setAttribute("pageJs", "charts.js");
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<section class="card chart-card">
	<div class="card-header">
		<h2 class="section-title">최근 10년 전체급여 추이</h2>
	</div>
	<div class="card-body">
		<canvas data-chart="line"
			data-labels='["2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026"]'
			data-series='[{"name": "급여총액", "values": [520, 560, 600, 650, 680, 720, 760, 810, 850, 900]}, {"name": "인원", "values": [15, 16, 17, 18, 19, 20, 21, 23, 24, 25]}]'></canvas>
		<div class="legend">
			<span>급여총액</span><span>인원</span>
		</div>
	</div>
</section>
<section class="card ">
	<div class="card-header">
		<h2 class="section-title">연도별 전체급여 내역</h2>
	</div>
	<div class="card-body">
		<div class="table-wrap">
			<table class="data-table ">
				<thead>
					<tr>
						<th>연도</th>
						<th>급여총액</th>
						<th>공제총액</th>
						<th>실지급액</th>
						<th>급여인원</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>2026</td>
						<td>900,000,000</td>
						<td>89,100,000</td>
						<td>810,900,000</td>
						<td>25명</td>
					</tr>
					<tr>
						<td>2025</td>
						<td>850,000,000</td>
						<td>83,500,000</td>
						<td>766,500,000</td>
						<td>24명</td>
					</tr>
					<tr>
						<td>2024</td>
						<td>810,000,000</td>
						<td>79,400,000</td>
						<td>730,600,000</td>
						<td>23명</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
