<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setAttribute("pageTitle", "급여항목 설정");
request.setAttribute("pageSection", "기본환경");
request.setAttribute("pageDescription", "급여 계산에 사용할 지급항목과 공제항목을 설정합니다.");
request.setAttribute("activeKey", "pay-item-settings");
request.setAttribute("pageCss", "environment.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<section class="source-config-block">
	<div class="source-config-list">
		<div class="source-section-title">지급항목 설정</div>
		<div class="table-wrap">
			<table class="data-table source-data-table">
				<thead>
					<tr>
						<th>지급항목</th>
						<th>과세여부</th>
						<th>비과세한도액</th>
						<th>절사단위</th>
						<th>근태연결/일괄지급</th>
						<th>사용여부</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>기본급</td>
						<td>전체과세</td>
						<td></td>
						<td>없음</td>
						<td></td>
						<td>사용</td>
					</tr>
					<tr>
						<td>식대</td>
						<td>비과세</td>
						<td>100,000</td>
						<td>없음</td>
						<td></td>
						<td>사용</td>
					</tr>
					<tr>
						<td>보육수당</td>
						<td>전체과세</td>
						<td></td>
						<td>없음</td>
						<td></td>
						<td>사용</td>
					</tr>
					<tr>
						<td>차량유지비</td>
						<td>비과세</td>
						<td>200,000</td>
						<td>없음</td>
						<td></td>
						<td>사용</td>
					</tr>
					<tr>
						<td>시간외수당</td>
						<td>전체과세</td>
						<td></td>
						<td>없음</td>
						<td>시간외근무</td>
						<td>사용</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	<div class="source-config-editor">
		<div class="source-editor-head">지급항목</div>
		<table class="source-form-table">
			<tbody>
				<tr>
					<th>지급항목</th>
					<td class="span-3"><input type="text" class="input"
						placeholder="지급 항목을 입력하세요."></td>
				</tr>
				<tr>
					<th>과세여부</th>
					<td class="span-3"><div class="check-list">
							<label><input type="radio" name="taxable" checked>
								전체과세</label><label><input type="radio" name="taxable">
								비과세</label>
						</div></td>
				</tr>
				<tr>
					<th>비과세명</th>
					<td class="span-3"><input type="text" class="input"></td>
				</tr>
				<tr>
					<th>비과세 한도액</th>
					<td class="span-3"><div class="money-control">
							<input type="text" class="input number"><span>원</span>
						</div></td>
				</tr>
				<tr>
					<th>절사단위</th>
					<td class="span-3"><select class="select"><option
								selected>없음</option>
							<option>1원 단위</option>
							<option>10원 단위</option>
							<option>100원 단위</option></select></td>
				</tr>
				<tr>
					<th>근태연결/일괄지급</th>
					<td class="span-3"><select class="select"><option
								selected>선택해주세요</option>
							<option>시간외근무</option>
							<option>일괄지급</option></select></td>
				</tr>
				<tr>
					<th>사용여부</th>
					<td class="span-3"><div class="check-list">
							<label><input type="radio" name="pay-use" checked>
								사용</label><label><input type="radio" name="pay-use">
								사용안함</label>
						</div></td>
				</tr>
			</tbody>
		</table>
		<div class="source-editor-actions">
			<button type="button" class="btn btn-primary">추가</button>
			<button type="button" class="btn btn-blue">수정</button>
			<button type="button" class="btn">삭제</button>
			<button type="button" class="btn">내용 지우기</button>
		</div>
	</div>
</section>
<section class="source-config-block">
	<div class="source-config-list">
		<div class="source-section-title">공제항목 설정</div>
		<div class="table-wrap">
			<table class="data-table source-data-table">
				<thead>
					<tr>
						<th>공제항목</th>
						<th>절사단위</th>
						<th>사용여부</th>
						<th>비고</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>국민연금</td>
						<td>10원 단위</td>
						<td>사용</td>
						<td>기본항목</td>
					</tr>
					<tr>
						<td>건강보험</td>
						<td>10원 단위</td>
						<td>사용</td>
						<td>기본항목</td>
					</tr>
					<tr>
						<td>장기요양보험</td>
						<td>10원 단위</td>
						<td>사용</td>
						<td>기본항목</td>
					</tr>
					<tr>
						<td>고용보험</td>
						<td>10원 단위</td>
						<td>사용</td>
						<td>기본항목</td>
					</tr>
					<tr>
						<td>소득세</td>
						<td>10원 단위</td>
						<td>사용</td>
						<td>기본항목</td>
					</tr>
					<tr>
						<td>지방소득세</td>
						<td>10원 단위</td>
						<td>사용</td>
						<td>기본항목</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	<div class="source-config-editor">
		<div class="source-editor-head">공제항목</div>
		<table class="source-form-table">
			<tbody>
				<tr>
					<th>공제항목</th>
					<td class="span-3"><input type="text" class="input"
						placeholder="공제 항목을 입력하세요."></td>
				</tr>
				<tr>
					<th>절사단위</th>
					<td class="span-3"><select class="select"><option
								selected>없음</option>
							<option>1원 단위</option>
							<option>10원 단위</option>
							<option>100원 단위</option></select></td>
				</tr>
				<tr>
					<th>비고</th>
					<td class="span-3"><input type="text" class="input"></td>
				</tr>
				<tr>
					<th>사용여부</th>
					<td class="span-3"><div class="check-list">
							<label><input type="radio" name="ded-use" checked>
								사용</label><label><input type="radio" name="ded-use">
								사용안함</label>
						</div></td>
				</tr>
			</tbody>
		</table>
		<div class="source-editor-actions">
			<button type="button" class="btn btn-primary">추가</button>
			<button type="button" class="btn btn-blue">수정</button>
			<button type="button" class="btn">삭제</button>
			<button type="button" class="btn">내용 지우기</button>
		</div>
	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
