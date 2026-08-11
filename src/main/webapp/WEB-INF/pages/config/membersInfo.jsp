<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setAttribute("pageTitle", "사용자 정보");
request.setAttribute("pageSection", "기본환경");
request.setAttribute("pageDescription", "회사·담당자·급여 지급정보와 로고·직인을 관리합니다.");
request.setAttribute("activeKey", "user-info");
request.setAttribute("pageCss", "environment.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<!-- ★ 1. action 경로를 데이터를 '조회'하는 read가 아니라 '수정'을 처리할 핸들러 주소로 변경합니다. (예: updateMembersInfo.do) -->
<form
	action="${pageContext.request.contextPath}/Config/updateMembersInfo.do"
	method="post">

	<!-- 어떤 회사의 데이터인지 식별하기 위해 companyId를 숨김 필드로 전달 -->
	<input type="hidden" name="companyId" value="${companyInfo.companyId}">

	<div class="user-info-layout">
		<div class="user-info-left">
			<section class="source-section">
				<div class="source-section-title">회사정보</div>
				<table class="source-form-table">
					<tbody>
						<tr>
							<th>상호</th>
							<td class="span-1"><input type="text" class="input"
								name="companyName" value="${companyInfo.companyName}"></td>
							<th>대표자명</th>
							<td class="span-1"><input type="text" class="input"
								name="ceoName" value="${companyInfo.ceoName}"></td>
						</tr>
						<tr>
							<th>사업자번호</th>
							<td class="span-1"><input type="text" class="input"
								name="businessNo" value="${companyInfo.businessNo}"></td>
							<th>법인등록번호</th>
							<td class="span-1"><input type="text" class="input"
								name="corpNo" value="${companyInfo.corpNo}"></td>
						</tr>
						<tr>
							<th>설립일</th>
							<!-- 참고: timestamp 데이터가 yyyy-MM-dd 형태로 나와야 input type="date"에 정상 표시됩니다. -->
							<td class="span-1"><input type="date" class="input"
								name="estDate" value="${companyInfo.estDate}"></td>
							<th>홈페이지</th>
							<td class="span-1"><input type="text" class="input"
								name="webSite" value="${companyInfo.webSite}"></td>
						</tr>
						<%-- <tr>
							<th>사업장 주소</th>
							<td class="span-3"><div class="address-control">
									<input class="input" type="text" name="address" value="${companyInfo.address}">
									<button type="button" class="btn btn-sm">우편번호</button>
								</div></td>
						</tr> --%>
						<tr>
							<th>전화번호</th>
							<td class="span-1"><input type="text" class="input"
								name="telNo" value="${companyInfo.telNo}"></td>
							<th>팩스번호</th>
							<td class="span-1"><input type="text" class="input"
								name="faxNo" value="${companyInfo.faxNo}"></td>
						</tr>
						<tr>
							<th>업태</th>
							<td class="span-1"><input type="text" class="input"
								name="businessType" value="${companyInfo.businessType}"></td>
							<th>종목</th>
							<td class="span-1"><input type="text" class="input"
								name="businessItem" value="${companyInfo.businessItem}"></td>
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
									당월 <select class="select" name="payPeriodStartDay">
										<option value="1" ${companyInfo.payDay == 1 ? 'selected' : ''}>01일</option>
										<option value="2" ${companyInfo.payDay == 2 ? 'selected' : ''}>02일</option>
										<option value="3" ${companyInfo.payDay == 3 ? 'selected' : ''}>03일</option>
										<option value="4" ${companyInfo.payDay == 4 ? 'selected' : ''}>04일</option>
										<option value="5" ${companyInfo.payDay == 5 ? 'selected' : ''}>05일</option>
										<option value="6" ${companyInfo.payDay == 6 ? 'selected' : ''}>06일</option>
										<option value="7" ${companyInfo.payDay == 7 ? 'selected' : ''}>07일</option>
										<option value="8" ${companyInfo.payDay == 8 ? 'selected' : ''}>08일</option>
										<option value="9" ${companyInfo.payDay == 9 ? 'selected' : ''}>09일</option>
										<option value="10"${companyInfo.payDay == 10 ? 'selected' : ''}>10일</option>
										<option value="11"${companyInfo.payDay == 11 ? 'selected' : ''}>11일</option>
										<option value="12"${companyInfo.payDay == 12 ? 'selected' : ''}>12일</option>
										<option value="13"${companyInfo.payDay == 13 ? 'selected' : ''}>13일</option>
										<option value="14"${companyInfo.payDay == 14 ? 'selected' : ''}>14일</option>
										<option value="15"${companyInfo.payDay == 15 ? 'selected' : ''}>15일</option>
										<option value="16"${companyInfo.payDay == 16 ? 'selected' : ''}>16일</option>
										<option value="17"${companyInfo.payDay == 17 ? 'selected' : ''}>17일</option>
										<option value="18"${companyInfo.payDay == 18 ? 'selected' : ''}>18일</option>
										<option value="19"${companyInfo.payDay == 19 ? 'selected' : ''}>19일</option>
										<option value="20"${companyInfo.payDay == 20 ? 'selected' : ''}>20일</option>
										<option value="21"${companyInfo.payDay == 21 ? 'selected' : ''}>21일</option>
										<option value="22"${companyInfo.payDay == 22 ? 'selected' : ''}>22일</option>
										<option value="23"${companyInfo.payDay == 23 ? 'selected' : ''}>23일</option>
										<option value="24"${companyInfo.payDay == 24 ? 'selected' : ''}>24일</option>
										<option value="25"${companyInfo.payDay == 25 ? 'selected' : ''}>25일</option>
										<option value="26"${companyInfo.payDay == 26 ? 'selected' : ''}>26일</option>
										<option value="27"${companyInfo.payDay == 27 ? 'selected' : ''}>27일</option>
										<option value="28"${companyInfo.payDay == 28 ? 'selected' : ''}>28일</option>
										<option value="29"${companyInfo.payDay == 29 ? 'selected' : ''}>29일</option>
										<option value="30"${companyInfo.payDay == 30 ? 'selected' : ''}>30일</option>
										<option value="31"${companyInfo.payDay == 31 ? 'selected' : ''}>31일</option>
									</select> ~ 당월 <select class="select" name="payPeriodEndDay">
										<option value="1" ${companyInfo.payDay == 1 ? 'selected' : ''}>01일</option>
										<option value="2" ${companyInfo.payDay == 2 ? 'selected' : ''}>02일</option>
										<option value="3" ${companyInfo.payDay == 3 ? 'selected' : ''}>03일</option>
										<option value="4" ${companyInfo.payDay == 4 ? 'selected' : ''}>04일</option>
										<option value="5" ${companyInfo.payDay == 5 ? 'selected' : ''}>05일</option>
										<option value="6" ${companyInfo.payDay == 6 ? 'selected' : ''}>06일</option>
										<option value="7" ${companyInfo.payDay == 7 ? 'selected' : ''}>07일</option>
										<option value="8" ${companyInfo.payDay == 8 ? 'selected' : ''}>08일</option>
										<option value="9" ${companyInfo.payDay == 9 ? 'selected' : ''}>09일</option>
										<option value="10"${companyInfo.payDay == 10 ? 'selected' : ''}>10일</option>
										<option value="11"${companyInfo.payDay == 11 ? 'selected' : ''}>11일</option>
										<option value="12"${companyInfo.payDay == 12 ? 'selected' : ''}>12일</option>
										<option value="13"${companyInfo.payDay == 13 ? 'selected' : ''}>13일</option>
										<option value="14"${companyInfo.payDay == 14 ? 'selected' : ''}>14일</option>
										<option value="15"${companyInfo.payDay == 15 ? 'selected' : ''}>15일</option>
										<option value="16"${companyInfo.payDay == 16 ? 'selected' : ''}>16일</option>
										<option value="17"${companyInfo.payDay == 17 ? 'selected' : ''}>17일</option>
										<option value="18"${companyInfo.payDay == 18 ? 'selected' : ''}>18일</option>
										<option value="19"${companyInfo.payDay == 19 ? 'selected' : ''}>19일</option>
										<option value="20"${companyInfo.payDay == 20 ? 'selected' : ''}>20일</option>
										<option value="21"${companyInfo.payDay == 21 ? 'selected' : ''}>21일</option>
										<option value="22"${companyInfo.payDay == 22 ? 'selected' : ''}>22일</option>
										<option value="23"${companyInfo.payDay == 23 ? 'selected' : ''}>23일</option>
										<option value="24"${companyInfo.payDay == 24 ? 'selected' : ''}>24일</option>
										<option value="25"${companyInfo.payDay == 25 ? 'selected' : ''}>25일</option>
										<option value="26"${companyInfo.payDay == 26 ? 'selected' : ''}>26일</option>
										<option value="27"${companyInfo.payDay == 27 ? 'selected' : ''}>27일</option>
										<option value="28"${companyInfo.payDay == 28 ? 'selected' : ''}>28일</option>
										<option value="29"${companyInfo.payDay == 29 ? 'selected' : ''}>29일</option>
										<option value="30"${companyInfo.payDay == 30 ? 'selected' : ''}>30일</option>
										<option value="31"${companyInfo.payDay == 31 ? 'selected' : ''}>31일</option>
									</select>
								</div></td>
							<th>급여지급일</th>
							<td class="span-1"><div class="date-rule">
									익월 <select class="select" name="payDay">
										<option value="1" ${companyInfo.payDay == 1 ? 'selected' : ''}>01일</option>
										<option value="2" ${companyInfo.payDay == 2 ? 'selected' : ''}>02일</option>
										<option value="3" ${companyInfo.payDay == 3 ? 'selected' : ''}>03일</option>
										<option value="4" ${companyInfo.payDay == 4 ? 'selected' : ''}>04일</option>
										<option value="5" ${companyInfo.payDay == 5 ? 'selected' : ''}>05일</option>
										<option value="6" ${companyInfo.payDay == 6 ? 'selected' : ''}>06일</option>
										<option value="7" ${companyInfo.payDay == 7 ? 'selected' : ''}>07일</option>
										<option value="8" ${companyInfo.payDay == 8 ? 'selected' : ''}>08일</option>
										<option value="9" ${companyInfo.payDay == 9 ? 'selected' : ''}>09일</option>
										<option value="10"${companyInfo.payDay == 10 ? 'selected' : ''}>10일</option>
										<option value="11"${companyInfo.payDay == 11 ? 'selected' : ''}>11일</option>
										<option value="12"${companyInfo.payDay == 12 ? 'selected' : ''}>12일</option>
										<option value="13"${companyInfo.payDay == 13 ? 'selected' : ''}>13일</option>
										<option value="14"${companyInfo.payDay == 14 ? 'selected' : ''}>14일</option>
										<option value="15"${companyInfo.payDay == 15 ? 'selected' : ''}>15일</option>
										<option value="16"${companyInfo.payDay == 16 ? 'selected' : ''}>16일</option>
										<option value="17"${companyInfo.payDay == 17 ? 'selected' : ''}>17일</option>
										<option value="18"${companyInfo.payDay == 18 ? 'selected' : ''}>18일</option>
										<option value="19"${companyInfo.payDay == 19 ? 'selected' : ''}>19일</option>
										<option value="20"${companyInfo.payDay == 20 ? 'selected' : ''}>20일</option>
										<option value="21"${companyInfo.payDay == 21 ? 'selected' : ''}>21일</option>
										<option value="22"${companyInfo.payDay == 22 ? 'selected' : ''}>22일</option>
										<option value="23"${companyInfo.payDay == 23 ? 'selected' : ''}>23일</option>
										<option value="24"${companyInfo.payDay == 24 ? 'selected' : ''}>24일</option>
										<option value="25"${companyInfo.payDay == 25 ? 'selected' : ''}>25일</option>
										<option value="26"${companyInfo.payDay == 26 ? 'selected' : ''}>26일</option>
										<option value="27"${companyInfo.payDay == 27 ? 'selected' : ''}>27일</option>
										<option value="28"${companyInfo.payDay == 28 ? 'selected' : ''}>28일</option>
										<option value="29"${companyInfo.payDay == 29 ? 'selected' : ''}>29일</option>
										<option value="30"${companyInfo.payDay == 30 ? 'selected' : ''}>30일</option>
										<option value="31"${companyInfo.payDay == 31 ? 'selected' : ''}>31일</option>

									</select>
								</div></td>
						</tr>
						<tr>
							<th>금융기관</th>
							<td class="span-1"><select class="select" name="bankName">
									<option value="국민은행"
										${companyInfo.bankName == '국민은행' ? 'selected' : ''}>국민은행</option>
									<option value="신한은행"
										${companyInfo.bankName == '신한은행' ? 'selected' : ''}>신한은행</option>
									<option value="우리은행"
										${companyInfo.bankName == '우리은행' ? 'selected' : ''}>우리은행</option>
							</select></td>
							<th>계좌번호</th>
							<td class="span-1"><input type="text" class="input"
								name="bankAccount" value="${companyInfo.bankAccount}"></td>
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
						<div class="brand-preview">${companyInfo.logoPath != null ? companyInfo.logoPath : '회사 로고'}</div>
						<div class="mini-actions">
							<button type="button" class="btn btn-sm">등록</button>
							<button type="button" class="btn btn-sm">삭제</button>
						</div>
					</div>
					<div class="brand-asset">
						<div class="brand-title">회사도장</div>
						<div class="brand-preview seal-preview">${companyInfo.sealPath != null ? companyInfo.sealPath : '직인'}</div>
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
						<!-- ★ 2. 담당자 정보(조인해온 데이터)를 value 속성에 연결하고 name을 명확히 지정합니다. -->
						<tr>
							<th>성명</th>
							<td class="span-3"><input type="text" class="input"
								name="managerName" value="${companyInfo.managerName}"></td>
						</tr>
						<tr>
							<th>부서</th>
							<td class="span-3"><div class="inline-control">
									<select class="select"><option selected>선택</option>
										<option>사장실</option>
										<option>개발팀</option>
										<option>콘텐츠팀</option>
										<option>업무지원팀</option>
										<option>디자인팀</option>
										<option>관리팀</option>
										<option>기획전략팀</option></select>
									<!-- 	<button type="button" class="btn btn-sm">관리</button> -->
								</div></td>
						</tr>
						<tr>
							<th>직위</th>
							<td class="span-3"><div class="inline-control">
									<select class="select"><option selected>선택</option>
										<option>이사</option>
										<option>차장</option>
										<option>사장</option>
										<option>부장</option>
										<option>과장</option>
										<option>대리</option>
										<option>주임</option>
										<option>사원</option>
										<option>실장</option></select>
									<!-- 	<button type="button" class="btn btn-sm">관리</button> -->
								</div></td>
						</tr>
						<tr>
							<th>전화번호</th>
							<td class="span-3"><input type="text" class="input"
								name="managerTel" value="${companyInfo.managerTel}"></td>
						</tr>
						<tr>
							<th>휴대전화</th>
							<td class="span-3"><input type="text" class="input"
								name="managerMobile" value="${companyInfo.managerMobile}"></td>
						</tr>
						<tr>
							<th>이메일</th>
							<td class="span-3"><input type="email" class="input"
								name="managerEmail" value="${companyInfo.managerEmail}"></td>
						</tr>
					</tbody>
				</table>
			</section>
		</div>
	</div>
	<div class="source-bottom-actions">
		<button type="submit" class="btn btn-primary">저장</button>
		<button type="reset" class="btn">취소</button>
	</div>
</form>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>