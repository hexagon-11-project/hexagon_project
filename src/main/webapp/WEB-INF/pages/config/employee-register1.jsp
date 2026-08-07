<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setAttribute("pageTitle", "사원 등록1");
request.setAttribute("pageSection", "기본환경");
request.setAttribute("pageDescription", "사원의 기본정보, 급여·보험정보, 가족·학력·경력·병역정보를 등록합니다.");
request.setAttribute("activeKey", "employee-register1");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<div class="employee-register-shell">
	<aside class="employee-register-side">
		<div class="employee-profile-box">
			<div class="employee-photo-placeholder">
				<span>사원사진</span><small>등록하세요</small>
			</div>
			<dl class="employee-mini-info">
				<dt>사원번호</dt>
				<dd>No-140036</dd>
				<dt>성명</dt>
				<dd></dd>
				<dt>부서</dt>
				<dd></dd>
				<dt>직위</dt>
				<dd></dd>
				<dt>입사일</dt>
				<dd></dd>
			</dl>
			<div class="mini-actions">
				<button type="button" class="btn btn-sm">등록</button>
				<button type="button" class="btn btn-sm">삭제</button>
			</div>
		</div>
		<div class="employee-page-menu">
			<div class="employee-page-label">사원정보 1page</div>
			<div class="employee-menu-grid">
				<a class="employee-menu-btn active"
					href="employee-register1.jsp#pay-insurance">급여<br>&amp;4대보험
				</a> <a class="employee-menu-btn active"
					href="employee-register1.jsp#family">부양<br>가족
				</a> <a class="employee-menu-btn active"
					href="employee-register1.jsp#education">학력</a> <a
					class="employee-menu-btn active"
					href="employee-register1.jsp#career">경력</a> <a
					class="employee-menu-btn active"
					href="employee-register1.jsp#military">병역</a>
			</div>
			<div class="employee-page-label second">사원정보 2page</div>
			<div class="employee-menu-grid">
				<a class="employee-menu-btn" href="employee-register2.jsp#license">자격<br>면허
				</a> <a class="employee-menu-btn" href="employee-register2.jsp#training">교육<br>훈련
				</a> <a class="employee-menu-btn" href="employee-register2.jsp#reward">상벌</a>
				<a class="employee-menu-btn"
					href="employee-register2.jsp#appointment">발령</a> <a
					class="employee-menu-btn" href="employee-register2.jsp#guarantee">추천<br>신원보증
				</a> <a class="employee-menu-btn"
					href="employee-register2.jsp#retirement">퇴직</a>
			</div>
		</div>
	</aside>
	<div class="employee-register-main">
		<section class="source-section">
			<div class="source-section-title">기본정보</div>
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>사원번호</th>
						<td class="span-1"><input type="text" class="input"
							value="No-140036"></td>
						<th>고용형태</th>
						<td class="span-1"><select class="select"><option
									selected>선택해주세요</option>
								<option>정규직</option>
								<option>계약직</option>
								<option>일용직</option></select></td>
					</tr>
					<tr>
						<th>성명(한글)</th>
						<td class="span-1"><input type="text" class="input"></td>
						<th>성명(영문)</th>
						<td class="span-1"><input type="text" class="input"></td>
					</tr>
					<tr>
						<th>입사일</th>
						<td class="span-1"><input type="date" class="input"></td>
						<th>퇴사일</th>
						<td class="span-1"><input type="date" class="input"></td>
					</tr>
					<tr>
						<th>부서</th>
						<td class="span-1"><div class="inline-control">
								<select class="select"><option selected>선택해주세요</option>
									<option>기획전략팀</option>
									<option>콘텐츠팀</option>
									<option>개발팀</option></select>
								<button type="button" class="btn btn-sm">관리</button>
							</div></td>
						<th>직위</th>
						<td class="span-1"><div class="inline-control">
								<select class="select"><option selected>선택해주세요</option>
									<option>사원</option>
									<option>대리</option>
									<option>과장</option>
									<option>부장</option></select>
								<button type="button" class="btn btn-sm">관리</button>
							</div></td>
					</tr>
					<tr>
						<th>내/외국인</th>
						<td class="span-1"><select class="select"><option
									selected>선택해주세요</option>
								<option>내국인</option>
								<option>외국인</option></select></td>
						<th>주민번호</th>
						<td class="span-1"><div class="rrn-fields">
								<input type="text" class="input"><span>-</span><input
									type="text" class="input">
							</div></td>
					</tr>
					<tr>
						<th>주소</th>
						<td class="span-3"><div class="address-control">
								<input class="input" type="text">
								<button type="button" class="btn btn-sm">우편번호</button>
							</div></td>
					</tr>
					<tr>
						<th>전화번호</th>
						<td class="span-1"><div class="phone-fields">
								<select class="select"><option selected>선택</option></select><input
									type="text" class="input"><input type="text"
									class="input">
							</div></td>
						<th>휴대폰</th>
						<td class="span-1"><div class="phone-fields">
								<select class="select"><option selected>선택</option></select><input
									type="text" class="input"><input type="text"
									class="input">
							</div></td>
					</tr>
					<tr>
						<th>이메일</th>
						<td class="span-1"><input type="email" class="input"></td>
						<th>SNS</th>
						<td class="span-1"><input type="text" class="input"></td>
					</tr>
				</tbody>
			</table>
		</section>
		<div class="source-page-caption">사원정보 1page</div>
		<section class="source-section" id="pay-insurance">
			<div class="source-section-title">급여 &amp; 4대보험</div>
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>급여</th>
						<td class="span-3"><label class="check-inline"><input
								type="checkbox" checked> 급여대상</label></td>
					</tr>
					<tr>
						<th>4대보험</th>
						<td class="span-3"><div class="check-list">
								<label><input type="checkbox" checked> 국민연금</label><label><input
									type="checkbox" checked> 건강보험(노인장기요양보험 포함)</label><label><input
									type="checkbox" checked> 고용보험</label>
							</div></td>
					</tr>
					<tr>
						<th>갑근세</th>
						<td class="span-3"><div class="check-list">
								<label><input type="radio" name="tax" checked>
									근로소득자 갑근세</label><label><input type="radio" name="tax">
									사업소득자 갑근세</label><label><input type="radio" name="tax">
									일용직 갑근세</label><label><input type="radio" name="tax">
									면제</label>
							</div></td>
					</tr>
					<tr>
						<th>기본급/월급</th>
						<td class="span-1"><div class="money-control">
								<input type="text" class="input number" value="0"><span>원</span>
							</div></td>
						<th>국민연금 기준소득월액</th>
						<td class="span-1"><div class="money-control">
								<input type="text" class="input number" value="0"><span>원</span>
							</div></td>
					</tr>
					<tr>
						<th>건강보험 보수월액</th>
						<td class="span-1"><div class="money-control">
								<input type="text" class="input number" value="0"><span>원</span>
							</div></td>
						<th>고용보험 보수월액</th>
						<td class="span-1"><div class="money-control">
								<input type="text" class="input number" value="0"><span>원</span>
							</div></td>
					</tr>
					<tr>
						<th>급여은행</th>
						<td class="span-1"><select class="select"><option
									selected>선택해주세요</option>
								<option>국민은행</option>
								<option>신한은행</option>
								<option>우리은행</option></select></td>
						<th>계좌번호</th>
						<td class="span-1"><input type="text" class="input"></td>
					</tr>
				</tbody>
			</table>
			<div class="table-toolbar compact">
				<strong>4대보험</strong>
			</div>
			<div class="table-wrap">
				<table class="data-table source-data-table">
					<thead>
						<tr>
							<th>구분</th>
							<th>기호번호</th>
							<th>취득일</th>
							<th>상실일</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>국민연금</td>
							<td><input class="input" type="text"></td>
							<td><input class="input" type="date"></td>
							<td><input class="input" type="date"></td>
						</tr>
						<tr>
							<td>건강보험</td>
							<td><input class="input" type="text"></td>
							<td><input class="input" type="date"></td>
							<td><input class="input" type="date"></td>
						</tr>
						<tr>
							<td>고용보험</td>
							<td><input class="input" type="text"></td>
							<td><input class="input" type="date"></td>
							<td><input class="input" type="date"></td>
						</tr>
						<tr>
							<td>산재보험</td>
							<td><input class="input" type="text"></td>
							<td><input class="input" type="date"></td>
							<td><input class="input" type="date"></td>
						</tr>
					</tbody>
				</table>
			</div>
		</section>
		<section class="source-section" id="family">
			<div class="source-section-title">부양가족</div>
			<div class="table-toolbar compact">
				<strong>부양가족</strong>
				<div class="actions">
					<button type="button" class="btn btn-sm">추가</button>
					<button type="button" class="btn btn-sm">선택삭제</button>
				</div>
			</div>
			<div class="table-wrap">
				<table class="data-table source-data-table wide">
					<thead>
						<tr>
							<th>선택</th>
							<th>관계</th>
							<th>성명</th>
							<th>구분</th>
							<th>주민등록번호</th>
							<th>장애여부</th>
							<th>인정공제대상</th>
							<th>건강보험등록</th>
							<th>동거여부</th>
							<th>다자녀</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><input type="checkbox"></td>
							<td><select class="select"><option>선택</option></select></td>
							<td><input class="input" type="text"></td>
							<td><select class="select"><option>선택</option></select></td>
							<td><input class="input" type="text"></td>
							<td><input type="checkbox"></td>
							<td><input type="checkbox"></td>
							<td><input type="checkbox"></td>
							<td><input type="checkbox"></td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td><input type="checkbox"></td>
							<td><select class="select"><option>선택</option></select></td>
							<td><input class="input" type="text"></td>
							<td><select class="select"><option>선택</option></select></td>
							<td><input class="input" type="text"></td>
							<td><input type="checkbox"></td>
							<td><input type="checkbox"></td>
							<td><input type="checkbox"></td>
							<td><input type="checkbox"></td>
							<td><input type="checkbox"></td>
						</tr>
					</tbody>
				</table>
			</div>
		</section>
		<section class="source-section" id="education">
			<div class="source-section-title">학력</div>
			<div class="table-toolbar compact">
				<strong>학력</strong>
				<div class="actions">
					<button type="button" class="btn btn-sm">추가</button>
					<button type="button" class="btn btn-sm">선택삭제</button>
				</div>
			</div>
			<div class="table-wrap">
				<table class="data-table source-data-table">
					<thead>
						<tr>
							<th>선택</th>
							<th>학교명</th>
							<th>전공</th>
							<th>입학일</th>
							<th>졸업일</th>
							<th>졸업구분</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><input type="checkbox"></td>
							<td><input class="input" type="text"></td>
							<td><input class="input" type="text"></td>
							<td><input class="input" type="date"></td>
							<td><input class="input" type="date"></td>
							<td><select class="select"><option>졸업</option>
									<option>재학</option>
									<option>중퇴</option></select></td>
						</tr>
					</tbody>
				</table>
			</div>
		</section>
		<section class="source-section" id="career">
			<div class="source-section-title">경력</div>
			<div class="table-toolbar compact">
				<strong>경력</strong>
				<div class="actions">
					<button type="button" class="btn btn-sm">추가</button>
					<button type="button" class="btn btn-sm">선택삭제</button>
				</div>
			</div>
			<div class="table-wrap">
				<table class="data-table source-data-table">
					<thead>
						<tr>
							<th>선택</th>
							<th>회사명</th>
							<th>부서</th>
							<th>직위</th>
							<th>입사일</th>
							<th>퇴사일</th>
							<th>담당업무</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><input type="checkbox"></td>
							<td><input class="input" type="text"></td>
							<td><input class="input" type="text"></td>
							<td><input class="input" type="text"></td>
							<td><input class="input" type="date"></td>
							<td><input class="input" type="date"></td>
							<td><input class="input" type="text"></td>
						</tr>
					</tbody>
				</table>
			</div>
		</section>
		<section class="source-section" id="military">
			<div class="source-section-title">병역</div>
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>병역구분</th>
						<td class="span-1"><select class="select"><option
									selected>선택해주세요</option>
								<option>군필</option>
								<option>미필</option>
								<option>면제</option>
								<option>해당없음</option></select></td>
						<th>군별</th>
						<td class="span-1"><select class="select"><option
									selected>선택해주세요</option>
								<option>육군</option>
								<option>해군</option>
								<option>공군</option>
								<option>기타</option></select></td>
					</tr>
					<tr>
						<th>복무 시작일</th>
						<td class="span-1"><input type="date" class="input"></td>
						<th>복무 종료일</th>
						<td class="span-1"><input type="date" class="input"></td>
					</tr>
					<tr>
						<th>병과</th>
						<td class="span-1"><input type="text" class="input"></td>
						<th>계급</th>
						<td class="span-1"><input type="text" class="input"></td>
					</tr>
					<tr>
						<th>특기</th>
						<td class="span-3"><input type="text" class="input"></td>
					</tr>
				</tbody>
			</table>
		</section>
		<div class="source-bottom-actions">
			<button type="button" class="btn btn-primary">저장하기</button>
			<button type="button" class="btn">취소하기</button>
			<button type="button" class="btn">리스트</button>
			<button type="button" class="btn btn-blue">신규사원등록</button>
		</div>
	</div>
</div>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
