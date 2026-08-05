<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setAttribute("pageTitle", "사용자 정보");
request.setAttribute("pageSection", "기본환경");
request.setAttribute("pageDescription", "회사·담당자·급여 지급정보와 로고·직인을 관리합니다.");
request.setAttribute("activeKey", "user-info");
request.setAttribute("pageCss", "environment.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<div class="user-info-layout">
	<div class="user-info-left">
		<section class="source-section">
			<div class="source-section-title">회사정보</div>
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>상호</th>
						<td class="span-1"><input type="text" class="input"
							value="(주)헥사곤아이티"></td>
						<th>대표자명</th>
						<td class="span-1"><input type="text" class="input"
							value="홍길동"></td>
					</tr>
					<tr>
						<th>사업자번호</th>
						<td class="span-1"><input type="text" class="input"
							value="123-45-67890"></td>
						<th>법인등록번호</th>
						<td class="span-1"><input type="text" class="input"></td>
					</tr>
					<tr>
						<th>설립일</th>
						<td class="span-1"><input type="date" class="input"></td>
						<th>홈페이지</th>
						<td class="span-1"><input type="text" class="input"></td>
					</tr>
					<tr>
						<th>사업장 주소</th>
						<td class="span-3"><div class="address-control">
								<input class="input" type="text">
								<button type="button" class="btn btn-sm">우편번호</button>
							</div></td>
					</tr>
					<tr>
						<th>전화번호</th>
						<td class="span-1"><input type="text" class="input"
							value="02-0000-0000"></td>
						<th>팩스번호</th>
						<td class="span-1"><input type="text" class="input"></td>
					</tr>
					<tr>
						<th>업태</th>
						<td class="span-1"><input type="text" class="input"
							value="정보서비스업"></td>
						<th>종목</th>
						<td class="span-1"><input type="text" class="input"
							value="소프트웨어 개발"></td>
					</tr>
				</tbody>
			</table>
		</section>
		<section class="source-section">
			<div class="source-section-title">급여지급정보</div>
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>급여 산정기간</th>
						<td class="span-1"><div class="date-rule">
								당월 <select class="select"><option selected>01일</option>
									<option>05일</option>
									<option>10일</option>
									<option>15일</option>
									<option>25일</option></select> ~ 당월 <select class="select"><option
										selected>말일</option>
									<option>25일</option>
									<option>28일</option></select>
							</div></td>
						<th>급여지급일</th>
						<td class="span-1"><div class="date-rule">
								익월 <select class="select"><option selected>05일</option>
									<option>10일</option>
									<option>25일</option></select>
							</div></td>
					</tr>
					<tr>
						<th>금융기관</th>
						<td class="span-1"><select class="select"><option
									selected>국민은행</option>
								<option>신한은행</option>
								<option>우리은행</option></select></td>
						<th>계좌번호</th>
						<td class="span-1"><input type="text" class="input"></td>
					</tr>
					<tr>
						<th>급여이체뱅킹</th>
						<td class="span-3"><span class="muted">외부 은행 이체 기능은
								프로젝트 범위에서 제외</span></td>
					</tr>
				</tbody>
			</table>
		</section>
		<section class="source-section">
			<div class="source-section-title">회사로고 / 회사도장</div>
			<div class="brand-assets">
				<div class="brand-asset">
					<div class="brand-title">회사로고</div>
					<div class="brand-preview">회사 로고</div>
					<div class="mini-actions">
						<button type="button" class="btn btn-sm">등록</button>
						<button type="button" class="btn btn-sm">삭제</button>
					</div>
				</div>
				<div class="brand-asset">
					<div class="brand-title">회사도장</div>
					<div class="brand-preview seal-preview">직인</div>
					<div class="mini-actions">
						<button type="button" class="btn btn-sm">등록</button>
						<button type="button" class="btn btn-sm">삭제</button>
					</div>
				</div>
			</div>
		</section>
	</div>
	<div class="user-info-right">
		<section class="source-section">
			<div class="source-section-title">담당자정보</div>
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>성명</th>
						<td class="span-3"><input type="text" class="input"
							value="송지수"></td>
					</tr>
					<tr>
						<th>부서</th>
						<td class="span-3"><div class="inline-control">
								<select class="select"><option selected>선택</option>
									<option>경영지원팀</option></select>
								<button type="button" class="btn btn-sm">관리</button>
							</div></td>
					</tr>
					<tr>
						<th>직위</th>
						<td class="span-3"><div class="inline-control">
								<select class="select"><option selected>선택</option>
									<option>관리자</option></select>
								<button type="button" class="btn btn-sm">관리</button>
							</div></td>
					</tr>
					<tr>
						<th>전화번호</th>
						<td class="span-3"><input type="text" class="input"></td>
					</tr>
					<tr>
						<th>휴대전화</th>
						<td class="span-3"><input type="text" class="input"></td>
					</tr>
					<tr>
						<th>이메일</th>
						<td class="span-3"><input type="email" class="input"
							value="hm0814@naver.com"></td>
					</tr>
				</tbody>
			</table>
		</section>
	</div>
</div>
<div class="source-bottom-actions">
	<button type="button" class="btn btn-primary">저장</button>
	<button type="button" class="btn">취소</button>
</div>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
