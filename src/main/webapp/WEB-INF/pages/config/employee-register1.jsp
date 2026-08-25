<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!
	// null이면 빈 문자열로 (input value에 "null" 글자가 그대로 찍히는 것 방지)
	private String nz(String s) { return s == null ? "" : s; }
	// select의 option에 넣을 selected 속성 문자열
	private String eq(String submitted, String optionValue) {
		return optionValue.equals(submitted) ? "selected" : "";
	}
	// radio/checkbox에 넣을 checked 속성 문자열
	private String chk(String submitted, String optionValue) {
		return optionValue.equals(submitted) ? "checked" : "";
	}
%>
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
				<dd>${defaultEmpNo}</dd>
				<dt>성명</dt>
				<dd><%=nz(request.getParameter("employeeName"))%></dd>
				<dt>부서</dt>
				<dd><%=nz(request.getParameter("department"))%></dd>
				<dt>직위</dt>
				<dd><%=nz(request.getParameter("position"))%></dd>
				<dt>입사일</dt>
				<dd><%=nz(request.getParameter("hireDate"))%></dd>
			</dl>
			<div class="mini-actions">
				<button type="button" class="btn btn-sm">등록</button>
				<button type="button" class="btn btn-sm">삭제</button>
			</div>
		</div>
		<div class="employee-page-menu">
			<div class="employee-page-label">사원정보 1page</div>
			<div class="employee-menu-grid">
				<!-- 1페이지 항목들은 클릭 시 해당 위치로 스크롤 이동 (#아이디) -->
				<a class="employee-menu-btn" href="#pay-insurance">급여<br>&amp;4대보험
				</a> <a class="employee-menu-btn" href="#family">부양<br>가족
				</a> <a class="employee-menu-btn" href="#education">학력</a> <a
					class="employee-menu-btn" href="#career">경력</a> <a
					class="employee-menu-btn" href="#military">병역</a>
			</div>
			<div class="employee-page-label second">사원정보 2page</div>
			<div class="employee-menu-grid">
				<!-- [추가 완료] 2페이지 버튼 클릭 시 이동을 막고(return false) 경고창(alert) 띄우기 -->
				<a class="employee-menu-btn" href="#"
					onclick="alert('기본(최소)정보 저장 후 2page로 이동해 주세요.'); return false;">자격<br>면허
				</a> <a class="employee-menu-btn" href="#"
					onclick="alert('기본(최소)정보 저장 후 2page로 이동해 주세요.'); return false;">교육<br>훈련
				</a> <a class="employee-menu-btn" href="#"
					onclick="alert('기본(최소)정보 저장 후 2page로 이동해 주세요.'); return false;">상벌</a>
				<a class="employee-menu-btn" href="#"
					onclick="alert('기본(최소)정보 저장 후 2page로 이동해 주세요.'); return false;">발령</a>
				 <a class="employee-menu-btn" href="#"
					onclick="alert('기본(최소)정보 저장 후 2page로 이동해 주세요.'); return false;">퇴직</a>
			</div>
		</div>
	</aside>

	<div class="employee-register-main">
		<%
		// 각 테이블에 몇 줄을 그릴지 서버가 기억한다.
		// [추가]/[선택삭제] 버튼을 누르면 Handler가 이 값을 다시 계산해서 request attribute로 넘겨준다.
		int familyRowCount = 2;
		if (request.getAttribute("familyRowCount") != null) {
		    familyRowCount = (Integer) request.getAttribute("familyRowCount");
		}
		int educationRowCount = 1;
		if (request.getAttribute("educationRowCount") != null) {
		    educationRowCount = (Integer) request.getAttribute("educationRowCount");
		}
		int careerRowCount = 1;
		if (request.getAttribute("careerRowCount") != null) {
		    careerRowCount = (Integer) request.getAttribute("careerRowCount");
		}
		%>
		<!-- [수정] action을 자바 핸들러(employeeIns1.do)로 지정 -->
		<form action="<%=request.getContextPath()%>/Config/employeeIns1.do"
			method="post" onsubmit="return validateForm()">
			<input type="hidden" name="familyRowCount" value="<%=familyRowCount%>">
			<input type="hidden" name="educationRowCount" value="<%=educationRowCount%>">
			<input type="hidden" name="careerRowCount" value="<%=careerRowCount%>">


			<section class="source-section">
				<div class="source-section-title">기본정보</div>
				<%
				// 서버 왕복(부양가족 [추가] 등) 후에도 그대로 채워 넣기 위해, 제출된 값을 미리 다 읽어둔다.
				String v_employmentType = nz(request.getParameter("employmentType"));
				String v_employeeName = nz(request.getParameter("employeeName"));
				String v_employeeNameEn = nz(request.getParameter("employeeNameEn"));
				String v_hireDate = nz(request.getParameter("hireDate"));
				String v_resignDate = nz(request.getParameter("resignDate"));
				String v_department = nz(request.getParameter("department"));
				String v_position = nz(request.getParameter("position"));
				String v_domForYn = nz(request.getParameter("domForYn"));
				String v_rrnFront = nz(request.getParameter("residentRegNoFront"));
				String v_rrnBack = nz(request.getParameter("residentRegNoBack"));
				String v_phone1 = nz(request.getParameter("phone1"));
				String v_phone2 = nz(request.getParameter("phone2"));
				String v_phone3 = nz(request.getParameter("phone3"));
				String v_mobile1 = nz(request.getParameter("mobile1"));
				String v_mobile2 = nz(request.getParameter("mobile2"));
				String v_mobile3 = nz(request.getParameter("mobile3"));
				String v_email = nz(request.getParameter("email"));
				String v_sns = nz(request.getParameter("sns"));
				String v_empIncomeType = nz(request.getParameter("empIncomeType"));
				if (v_empIncomeType.isEmpty()) { v_empIncomeType = "근로소득자 갑근세"; } // 최초 진입 시 기본 선택값
				String v_bankName = nz(request.getParameter("bankName"));
				String v_bankAccount = nz(request.getParameter("bankAccount"));
				String v_insNoNP = nz(request.getParameter("insuranceNoNP"));
				String v_insAcqNP = nz(request.getParameter("acquisitionDateNP"));
				String v_insLossNP = nz(request.getParameter("lossDateNP"));
				String v_insNoHI = nz(request.getParameter("insuranceNoHI"));
				String v_insAcqHI = nz(request.getParameter("acquisitionDateHI"));
				String v_insLossHI = nz(request.getParameter("lossDateHI"));
				String v_insNoEI = nz(request.getParameter("insuranceNoEI"));
				String v_insAcqEI = nz(request.getParameter("acquisitionDateEI"));
				String v_insLossEI = nz(request.getParameter("lossDateEI"));
				String v_insNoII = nz(request.getParameter("insuranceNoII"));
				String v_insAcqII = nz(request.getParameter("acquisitionDateII"));
				String v_insLossII = nz(request.getParameter("lossDateII"));
				String v_baseWageAmount = nz(request.getParameter("baseWageAmount"));
				if (v_baseWageAmount.isEmpty()) { v_baseWageAmount = "0"; }
				String v_nationalPensionBaseAmount = nz(request.getParameter("nationalPensionBaseAmount"));
				if (v_nationalPensionBaseAmount.isEmpty()) { v_nationalPensionBaseAmount = "0"; }
				String v_healthInsuranceBaseAmount = nz(request.getParameter("healthInsuranceBaseAmount"));
				if (v_healthInsuranceBaseAmount.isEmpty()) { v_healthInsuranceBaseAmount = "0"; }
				String v_employmentInsuranceAmount = nz(request.getParameter("employmentInsuranceAmount"));
				if (v_employmentInsuranceAmount.isEmpty()) { v_employmentInsuranceAmount = "0"; }
				String v_militaryStatus = nz(request.getParameter("militaryStatus"));
				String v_militaryBranchCode = nz(request.getParameter("militaryBranchCode"));
				String v_militaryStartDate = nz(request.getParameter("militaryStartDate"));
				String v_militaryEndDate = nz(request.getParameter("militaryEndDate"));
				String v_militaryGrade = nz(request.getParameter("militaryGrade"));
				String v_militaryBranch = nz(request.getParameter("militaryBranch"));
				String v_militarySpecialty = nz(request.getParameter("militarySpecialty"));
				String v_militaryExemptReason = nz(request.getParameter("militaryExemptReason"));
				%>
				<table class="source-form-table">
					<tbody>
						<tr>
							<th>사원번호</th>
							<td class="span-1"><input type="text" class="input" readonly
								name="employeeNo" value="${defaultEmpNo}"></td>
							<th><span class="required-mark">*</span>고용형태</th>
							<td class="span-1">
								<select class="select" name="employmentType">
									<option value="" <%=v_employmentType.isEmpty()?"selected":""%>>선택해주세요</option>
									<option value="정규직" <%=eq(v_employmentType,"정규직")%>>정규직</option>
									<option value="계약직" <%=eq(v_employmentType,"계약직")%>>계약직</option>
									<option value="임시직" <%=eq(v_employmentType,"임시직")%>>임시직</option>
									<option value="파견직" <%=eq(v_employmentType,"파견직")%>>파견직</option>
									<option value="위촉직" <%=eq(v_employmentType,"위촉직")%>>위촉직</option>
									<option value="일용직" <%=eq(v_employmentType,"일용직")%>>일용직</option>
							</select>
							</td>
						</tr>
						<tr>
							<th><span class="required-mark">*</span>성명(한글)</th>
							<td class="span-1"><input type="text" class="input"
								name="employeeName" value="<%=v_employeeName%>"></td>
							<th>성명(영문)</th>
							<td class="span-1"><input type="text" class="input"
								name="employeeNameEn" value="<%=v_employeeNameEn%>"></td>
						</tr>
						<tr>
							<th><span class="required-mark">*</span>입사일</th>
							<td class="span-1"><input type="date" class="input"
								name="hireDate" value="<%=v_hireDate%>"></td>
							<th>퇴사일</th>
							<td class="span-1"><input type="date" class="input"
								name="resignDate" value="<%=v_resignDate%>"></td>
						</tr>
						<tr>
							<th><span class="required-mark">*</span>부서</th>
							<td class="span-1">
								<div class="inline-control">
									<select class="select" name="department">
										<option value="" <%=v_department.isEmpty()?"selected":""%>>선택해주세요</option>
										<option value="사장실" <%=eq(v_department,"사장실")%>>사장실</option>
										<option value="개발팀" <%=eq(v_department,"개발팀")%>>개발팀</option>
										<option value="업무지원팀" <%=eq(v_department,"업무지원팀")%>>업무지원팀</option>
										<option value="디자인팀" <%=eq(v_department,"디자인팀")%>>디자인팀</option>
										<option value="관리팀" <%=eq(v_department,"관리팀")%>>관리팀</option>
										<option value="기획전략팀" <%=eq(v_department,"기획전략팀")%>>기획전략팀</option>
										<option value="콘텐츠팀" <%=eq(v_department,"콘텐츠팀")%>>콘텐츠팀</option>
									</select>
								</div>
							</td>
							<th>직위</th>
							<td class="span-1">
								<div class="inline-control">
									<select class="select" name="position">
										<option value="" <%=v_position.isEmpty()?"selected":""%>>선택해주세요</option>
										<option value="사장" <%=eq(v_position,"사장")%>>사장</option>
										<option value="이사" <%=eq(v_position,"이사")%>>이사</option>
										<option value="부장" <%=eq(v_position,"부장")%>>부장</option>
										<option value="차장" <%=eq(v_position,"차장")%>>차장</option>
										<option value="과장" <%=eq(v_position,"과장")%>>과장</option>
										<option value="대리" <%=eq(v_position,"대리")%>>대리</option>
										<option value="주임" <%=eq(v_position,"주임")%>>주임</option>
										<option value="실장" <%=eq(v_position,"실장")%>>실장</option>
										<option value="사원" <%=eq(v_position,"사원")%>>사원</option>
									</select>
								</div>
							</td>
						</tr>
						<tr>
							<th>내/외국인</th>
							<td class="span-1">
								<select class="select" name="domForYn">
									<option value="" <%=v_domForYn.isEmpty()?"selected":""%>>선택해주세요</option>
									<option value="Y" <%=eq(v_domForYn,"Y")%>>내국인</option>
									<option value="N" <%=eq(v_domForYn,"N")%>>외국인</option>
							</select>
							</td>
							<th>주민번호</th>
							<td class="span-1">
								<div class="rrn-fields">
									<input type="text" class="input" name="residentRegNoFront" value="<%=v_rrnFront%>"><span>-</span><input
										type="text" class="input" name="residentRegNoBack" value="<%=v_rrnBack%>">
								</div>
							</td>
						</tr>
						<tr>
							<th>전화번호</th>
							<td class="span-1">
								<div class="phone-fields">
									<select class="select" name="phone1">
										<option value="" <%=v_phone1.isEmpty()?"selected":""%>>선택</option>
										<option <%=eq(v_phone1,"대표(없음)")%>>대표(없음)</option>
										<option <%=eq(v_phone1,"휴대폰(010)")%>>휴대폰(010)</option>
										<option <%=eq(v_phone1,"인터넷(050)")%>>인터넷(050)</option>
										<option <%=eq(v_phone1,"인터넷(0507)")%>>인터넷(0507)</option>
										<option <%=eq(v_phone1,"인터넷(070)")%>>인터넷(070)</option>
										<option <%=eq(v_phone1,"인터넷(0303)")%>>인터넷(0303)</option>
										<option <%=eq(v_phone1,"인터넷(0504)")%>>인터넷(0504)</option>
										<option <%=eq(v_phone1,"서울(02)")%>>서울(02)</option>
										<option <%=eq(v_phone1,"부산(051)")%>>부산(051)</option>
										<option <%=eq(v_phone1,"대구(053)")%>>대구(053)</option>
										<option <%=eq(v_phone1,"인천(032)")%>>인천(032)</option>
										<option <%=eq(v_phone1,"광주(062)")%>>광주(062)</option>
										<option <%=eq(v_phone1,"대전(042)")%>>대전(042)</option>
										<option <%=eq(v_phone1,"울산(052)")%>>울산(052)</option>
										<option <%=eq(v_phone1,"세종(044)")%>>세종(044)</option>
										<option <%=eq(v_phone1,"경기(031)")%>>경기(031)</option>
										<option <%=eq(v_phone1,"강원(033)")%>>강원(033)</option>
										<option <%=eq(v_phone1,"충북(043)")%>>충북(043)</option>
										<option <%=eq(v_phone1,"충남(041)")%>>충남(041)</option>
										<option <%=eq(v_phone1,"전북(063)")%>>전북(063)</option>
										<option <%=eq(v_phone1,"전남(061)")%>>전남(061)</option>
										<option <%=eq(v_phone1,"경북(054)")%>>경북(054)</option>
										<option <%=eq(v_phone1,"경남(055)")%>>경남(055)</option>
										<option <%=eq(v_phone1,"제주(064)")%>>제주(064)</option>
									</select> <input type="text" class="input" name="phone2" value="<%=v_phone2%>"> <input
										type="text" class="input" name="phone3" value="<%=v_phone3%>">
								</div>
							</td>
							<th>휴대폰</th>
							<td class="span-1">
								<div class="phone-fields">
									<select class="select" name="mobile1">
										<option value="" <%=v_mobile1.isEmpty()?"selected":""%>>선택</option>
										<option <%=eq(v_mobile1,"010")%>>010</option>
										<option <%=eq(v_mobile1,"011")%>>011</option>
										<option <%=eq(v_mobile1,"016")%>>016</option>
										<option <%=eq(v_mobile1,"017")%>>017</option>
										<option <%=eq(v_mobile1,"018")%>>018</option>
										<option <%=eq(v_mobile1,"019")%>>019</option>
									</select> <input type="text" class="input" name="mobile2" value="<%=v_mobile2%>"> <input
										type="text" class="input" name="mobile3" value="<%=v_mobile3%>">
								</div>
							</td>
						</tr>
						<tr>
							<th>이메일</th>
							<td class="span-1"><input type="email" class="input"
								name="email" value="<%=v_email%>"></td>
							<th>SNS</th>
							<td class="span-1"><input type="text" class="input"
								name="sns" value="<%=v_sns%>"></td>
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
									<label><input type="radio" name="empIncomeType"
										value="근로소득자 갑근세" <%=chk(v_empIncomeType,"근로소득자 갑근세")%>> 근로소득자 갑근세</label> <label><input
										type="radio" name="empIncomeType" value="사업소득자 갑근세" <%=chk(v_empIncomeType,"사업소득자 갑근세")%>>
										사업소득자 갑근세</label> <label><input type="radio"
										name="empIncomeType" value="일용직 갑근세" <%=chk(v_empIncomeType,"일용직 갑근세")%>> 일용직 갑근세</label> <label><input
										type="radio" name="empIncomeType" value="면제" <%=chk(v_empIncomeType,"면제")%>> 면제</label>
								</div></td>
						</tr>
						<tr>
							<th>기본급/월급</th>
							<td class="span-1"><div class="money-control">
									<input type="text" class="input number" name="baseWageAmount"
										value="<%=v_baseWageAmount%>"><span>원</span>
								</div></td>
							<th>국민연금 기준소득월액</th>
							<td class="span-1"><div class="money-control">
									<input type="text" class="input number" name="nationalPensionBaseAmount"
										value="<%=v_nationalPensionBaseAmount%>"><span>원</span>
								</div></td>
						</tr>
						<tr>
							<th>건강보험 보수월액</th>
							<td class="span-1"><div class="money-control">
									<input type="text" class="input number" name="healthInsuranceBaseAmount"
										value="<%=v_healthInsuranceBaseAmount%>"><span>원</span>
								</div></td>
							<th>고용보험 보수월액</th>
							<td class="span-1"><div class="money-control">
									<input type="text" class="input number" name="employmentInsuranceAmount"
										value="<%=v_employmentInsuranceAmount%>"><span>원</span>
								</div></td>
						</tr>
						<tr>
							<th>급여은행</th>
							<td class="span-1"><select class="select" name="bankName">
									<option value="" <%=v_bankName.isEmpty() ? "selected" : ""%>>선택해주세요</option>
									<option <%=eq(v_bankName,"국민은행")%>>국민은행</option>
									<option <%=eq(v_bankName,"기업은행")%>>기업은행</option>
									<option <%=eq(v_bankName,"농협중앙회")%>>농협중앙회</option>
									<option <%=eq(v_bankName,"농협은행")%>>농협은행</option>
									<option <%=eq(v_bankName,"산업은행")%>>산업은행</option>
									<option <%=eq(v_bankName,"신한은행")%>>신한은행</option>
									<option <%=eq(v_bankName,"스탠다드차타드은행")%>>스탠다드차타드은행</option>
									<option <%=eq(v_bankName,"우리은행")%>>우리은행</option>
									<option <%=eq(v_bankName,"외환은행")%>>외환은행</option>
									<option <%=eq(v_bankName,"하나은행")%>>하나은행</option>
									<option <%=eq(v_bankName,"한국씨티은행")%>>한국씨티은행</option>
									<option <%=eq(v_bankName,"경남은행")%>>경남은행</option>
									<option <%=eq(v_bankName,"광주은행")%>>광주은행</option>
									<option <%=eq(v_bankName,"지역농협")%>>지역농협</option>
									<option <%=eq(v_bankName,"대구은행")%>>대구은행</option>
									<option <%=eq(v_bankName,"부산은행")%>>부산은행</option>
									<option <%=eq(v_bankName,"전북은행")%>>전북은행</option>
									<option <%=eq(v_bankName,"제주은행")%>>제주은행</option>
									<option <%=eq(v_bankName,"카카오뱅크")%>>카카오뱅크</option>
									<option <%=eq(v_bankName,"케이뱅크")%>>케이뱅크</option>
									<option <%=eq(v_bankName,"토스뱅크")%>>토스뱅크</option>
									<option <%=eq(v_bankName,"산림조합")%>>산림조합</option>
									<option <%=eq(v_bankName,"상호저축은행")%>>상호저축은행</option>
									<option <%=eq(v_bankName,"새마을금고")%>>새마을금고</option>
									<option <%=eq(v_bankName,"신용협동조합")%>>신용협동조합</option>
									<option <%=eq(v_bankName,"수협중앙회")%>>수협중앙회</option>
									<option <%=eq(v_bankName,"우체국")%>>우체국</option>
									<option <%=eq(v_bankName,"도이치뱅크")%>>도이치뱅크</option>
									<option <%=eq(v_bankName,"BOA")%>>BOA</option>
									<option <%=eq(v_bankName,"에이비엔암로")%>>에이비엔암로</option>
									<option <%=eq(v_bankName,"HSBC")%>>HSBC</option>
									<option <%=eq(v_bankName,"JP모간")%>>JP모간</option>
									<option <%=eq(v_bankName,"BNP파리바")%>>BNP파리바</option>
									<option <%=eq(v_bankName,"OK저축은행")%>>OK저축은행</option>
									<option <%=eq(v_bankName,"골든브릿지투자증권")%>>골든브릿지투자증권</option>
									<option <%=eq(v_bankName,"교보증권")%>>교보증권</option>
									<option <%=eq(v_bankName,"대신증권")%>>대신증권</option>
									<option <%=eq(v_bankName,"동부증권")%>>동부증권</option>
									<option <%=eq(v_bankName,"리딩투자증권")%>>리딩투자증권</option>
									<option <%=eq(v_bankName,"메리츠종합금융증권")%>>메리츠종합금융증권</option>
									<option <%=eq(v_bankName,"미래에셋대우")%>>미래에셋대우</option>
									<option <%=eq(v_bankName,"미래에셋증권")%>>미래에셋증권</option>
									<option <%=eq(v_bankName,"바로투자증권")%>>바로투자증권</option>
									<option <%=eq(v_bankName,"부국증권")%>>부국증권</option>
									<option <%=eq(v_bankName,"삼성증권")%>>삼성증권</option>
									<option <%=eq(v_bankName,"신영증권")%>>신영증권</option>
									<option <%=eq(v_bankName,"신한금융투자")%>>신한금융투자</option>
									<option <%=eq(v_bankName,"유안타증권")%>>유안타증권</option>
									<option <%=eq(v_bankName,"유진투자증권")%>>유진투자증권</option>
									<option <%=eq(v_bankName,"유화증권")%>>유화증권</option>
									<option <%=eq(v_bankName,"이베스트투자증권")%>>이베스트투자증권</option>
									<option <%=eq(v_bankName,"카카오페이증권")%>>카카오페이증권</option>
									<option <%=eq(v_bankName,"코리아에셋투자증권")%>>코리아에셋투자증권</option>
									<option <%=eq(v_bankName,"키움증권")%>>키움증권</option>
									<option <%=eq(v_bankName,"토스증권")%>>토스증권</option>
									<option <%=eq(v_bankName,"하나금융투자")%>>하나금융투자</option>
									<option <%=eq(v_bankName,"하이투자증권")%>>하이투자증권</option>
									<option <%=eq(v_bankName,"한국투자증권")%>>한국투자증권</option>
									<option <%=eq(v_bankName,"한양증권")%>>한양증권</option>
									<option <%=eq(v_bankName,"한화투자증권")%>>한화투자증권</option>
									<option <%=eq(v_bankName,"현대증권")%>>현대증권</option>
									<option <%=eq(v_bankName,"흥국증권")%>>흥국증권</option>
									<option <%=eq(v_bankName,"BNK투자증권")%>>BNK투자증권</option>
									<option <%=eq(v_bankName,"HMC투자증권")%>>HMC투자증권</option>
									<option <%=eq(v_bankName,"IBK투자증권")%>>IBK투자증권</option>
									<option <%=eq(v_bankName,"KB투자증권")%>>KB투자증권</option>
									<option <%=eq(v_bankName,"KTB투자증권")%>>KTB투자증권</option>
									<option <%=eq(v_bankName,"LIG투자증권")%>>LIG투자증권</option>
									<option <%=eq(v_bankName,"NH투자증권")%>>NH투자증권</option>
									<option <%=eq(v_bankName,"SK증권")%>>SK증권</option>
							</select></td>
							<th>계좌번호</th>
							<td class="span-1"><input type="text" class="input" name="bankAccount" value="<%=v_bankAccount%>"></td>
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
								<td><input class="input" type="text" name="insuranceNoNP" value="<%=v_insNoNP%>"></td>
								<td><input class="input" type="date" name="acquisitionDateNP" value="<%=v_insAcqNP%>"></td>
								<td><input class="input" type="date" name="lossDateNP" value="<%=v_insLossNP%>"></td>
							</tr>
							<tr>
								<td>건강보험</td>
								<td><input class="input" type="text" name="insuranceNoHI" value="<%=v_insNoHI%>"></td>
								<td><input class="input" type="date" name="acquisitionDateHI" value="<%=v_insAcqHI%>"></td>
								<td><input class="input" type="date" name="lossDateHI" value="<%=v_insLossHI%>"></td>
							</tr>
							<tr>
								<td>고용보험</td>
								<td><input class="input" type="text" name="insuranceNoEI" value="<%=v_insNoEI%>"></td>
								<td><input class="input" type="date" name="acquisitionDateEI" value="<%=v_insAcqEI%>"></td>
								<td><input class="input" type="date" name="lossDateEI" value="<%=v_insLossEI%>"></td>
							</tr>
							<tr>
								<td>산재보험</td>
								<td><input class="input" type="text" name="insuranceNoII" value="<%=v_insNoII%>"></td>
								<td><input class="input" type="date" name="acquisitionDateII" value="<%=v_insAcqII%>"></td>
								<td><input class="input" type="date" name="lossDateII" value="<%=v_insLossII%>"></td>
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
						<!-- type=submit + name/value로 "이 버튼이 눌렸다"는 걸 서버에 알림. 폼 전체가 그대로 제출됨
						     formaction에 #family를 붙여서, 새로고침 후 이 섹션으로 자동 스크롤되게 함 -->
						<button type="submit" name="formAction" value="addFamilyRow" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns1.do#family">추가</button>
						<button type="submit" name="formAction" value="deleteFamilyRows" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns1.do#family">선택삭제</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table wide" id="familyTable">
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
							<%
							for (int i = 1; i <= familyRowCount; i++) {
							    String relVal = request.getParameter("familyRelation" + i);
							    String nameVal = request.getParameter("familyName" + i);
							    String rrnVal = request.getParameter("familyRrn" + i);
							    if (relVal == null) relVal = "";
							    if (nameVal == null) nameVal = "";
							    if (rrnVal == null) rrnVal = "";
							%>
							<tr>
								<td><input type="checkbox" name="familyDel<%=i%>"></td>
								<td><select class="select" name="familyRelation<%=i%>">
										<option value="" <%=relVal.isEmpty() ? "selected" : ""%>>선택</option>
										<option value="배우자" <%="배우자".equals(relVal) ? "selected" : ""%>>배우자</option>
										<option value="아들" <%="아들".equals(relVal) ? "selected" : ""%>>아들</option>
										<option value="딸" <%="딸".equals(relVal) ? "selected" : ""%>>딸</option>
										<option value="부" <%="부".equals(relVal) ? "selected" : ""%>>부</option>
										<option value="모" <%="모".equals(relVal) ? "selected" : ""%>>모</option>
										<option value="형제" <%="형제".equals(relVal) ? "selected" : ""%>>형제</option>
										<option value="자매" <%="자매".equals(relVal) ? "selected" : ""%>>자매</option>
										<option value="장인" <%="장인".equals(relVal) ? "selected" : ""%>>장인</option>
										<option value="장모" <%="장모".equals(relVal) ? "selected" : ""%>>장모</option>
										<option value="시아버지" <%="시아버지".equals(relVal) ? "selected" : ""%>>시아버지</option>
										<option value="시어머니" <%="시어머니".equals(relVal) ? "selected" : ""%>>시어머니</option>
										<option value="조부" <%="조부".equals(relVal) ? "selected" : ""%>>조부</option>
										<option value="조모" <%="조모".equals(relVal) ? "selected" : ""%>>조모</option>
										<option value="손자" <%="손자".equals(relVal) ? "selected" : ""%>>손자</option>
										<option value="손녀" <%="손녀".equals(relVal) ? "selected" : ""%>>손녀</option>
								</select></td>
								<td><input class="input" type="text" name="familyName<%=i%>" value="<%=nameVal%>"></td>
								<td><select class="select" name="familyDomForYn<%=i%>">
										<option value="" <%=request.getParameter("familyDomForYn"+i) == null || request.getParameter("familyDomForYn"+i).isEmpty() ? "selected" : ""%>>선택</option>
										<option value="Y" <%="Y".equals(request.getParameter("familyDomForYn"+i)) ? "selected" : ""%>>내국인</option>
										<option value="N" <%="N".equals(request.getParameter("familyDomForYn"+i)) ? "selected" : ""%>>외국인</option>
								</select></td>
								<td><input class="input" type="text" name="familyRrn<%=i%>" value="<%=rrnVal%>"></td>
								<td><input type="checkbox" name="familyDisabled<%=i%>" <%="on".equals(request.getParameter("familyDisabled"+i)) ? "checked" : ""%>></td>
								<td><input type="checkbox" name="familyDeduction<%=i%>" <%="on".equals(request.getParameter("familyDeduction"+i)) ? "checked" : ""%>></td>
								<td><input type="checkbox" name="familyHealthIns<%=i%>" <%="on".equals(request.getParameter("familyHealthIns"+i)) ? "checked" : ""%>></td>
								<td><input type="checkbox" name="familyCohab<%=i%>" <%="on".equals(request.getParameter("familyCohab"+i)) ? "checked" : ""%>></td>
								<td><input type="checkbox" name="familyMultiChild<%=i%>" <%="on".equals(request.getParameter("familyMultiChild"+i)) ? "checked" : ""%>></td>
							</tr>
							<% } %>
						</tbody>
					</table>
				</div>
			</section>

			<section class="source-section" id="education">
				<div class="source-section-title">학력</div>
				<div class="table-toolbar compact">
					<strong>학력</strong>
					<div class="actions">
						<button type="submit" name="formAction" value="addEducationRow" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns1.do#education">추가</button>
						<button type="submit" name="formAction" value="deleteEducationRows" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns1.do#education">선택삭제</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table" id="educationTable">
						<thead>
							<tr>
								<th>선택</th>
								<th>학교명</th>
								<th>전공</th>
								<th>입학일</th>
								<th>졸업일</th>
								<th>구분</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 1; i <= educationRowCount; i++) {
							    String eduSchool = nz(request.getParameter("educationSchool" + i));
							    String eduMajor = nz(request.getParameter("educationMajor" + i));
							    String eduStart = nz(request.getParameter("educationStart" + i));
							    String eduEnd = nz(request.getParameter("educationEnd" + i));
							    String eduStatus = nz(request.getParameter("educationStatus" + i));
							%>
							<tr>
								<td><input type="checkbox" name="educationDel<%=i%>"></td>
								<td><input class="input" type="text" name="educationSchool<%=i%>" value="<%=eduSchool%>"></td>
								<td><input class="input" type="text" name="educationMajor<%=i%>" value="<%=eduMajor%>"></td>
								<td><input class="input" type="date" name="educationStart<%=i%>" value="<%=eduStart%>"></td>
								<td><input class="input" type="date" name="educationEnd<%=i%>" value="<%=eduEnd%>"></td>
								<td><select class="select" name="educationStatus<%=i%>">
										<option value="" <%=eduStatus.isEmpty() ? "selected" : ""%>>선택</option>
										<option value="초등학교" <%=eq(eduStatus,"초등학교")%>>초등학교</option>
										<option value="중학교" <%=eq(eduStatus,"중학교")%>>중학교</option>
										<option value="고등학교" <%=eq(eduStatus,"고등학교")%>>고등학교</option>
										<option value="대학교" <%=eq(eduStatus,"대학교")%>>대학교</option>
										<option value="석사" <%=eq(eduStatus,"석사")%>>석사</option>
										<option value="박사" <%=eq(eduStatus,"박사")%>>박사</option>
								</select></td>
							</tr>
							<% } %>
						</tbody>
					</table>
				</div>
			</section>

			<section class="source-section" id="career">
				<div class="source-section-title">경력</div>
				<div class="table-toolbar compact">
					<strong>경력</strong>
					<div class="actions">
						<button type="submit" name="formAction" value="addCareerRow" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns1.do#career">추가</button>
						<button type="submit" name="formAction" value="deleteCareerRows" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns1.do#career">선택삭제</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table" id="careerTable">
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
							<%
							for (int i = 1; i <= careerRowCount; i++) {
							    String carCompany = nz(request.getParameter("careerCompany" + i));
							    String carDept = nz(request.getParameter("careerDept" + i));
							    String carPosition = nz(request.getParameter("careerPosition" + i));
							    String carStart = nz(request.getParameter("careerStart" + i));
							    String carEnd = nz(request.getParameter("careerEnd" + i));
							    String carDuty = nz(request.getParameter("careerDuty" + i));
							%>
							<tr>
								<td><input type="checkbox" name="careerDel<%=i%>"></td>
								<td><input class="input" type="text" name="careerCompany<%=i%>" value="<%=carCompany%>"></td>
								<td><input class="input" type="text" name="careerDept<%=i%>" value="<%=carDept%>"></td>
								<td><input class="input" type="text" name="careerPosition<%=i%>" value="<%=carPosition%>"></td>
								<td><input class="input" type="date" name="careerStart<%=i%>" value="<%=carStart%>"></td>
								<td><input class="input" type="date" name="careerEnd<%=i%>" value="<%=carEnd%>"></td>
								<td><input class="input" type="text" name="careerDuty<%=i%>" value="<%=carDuty%>"></td>
							</tr>
							<% } %>
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
							<td class="span-1"><select class="select" name="militaryStatus">
									<option value="" <%=v_militaryStatus.isEmpty() ? "selected" : ""%>>선택해주세요</option>
									<option value="군필" <%=eq(v_militaryStatus,"군필")%>>군필</option>
									<option value="미필" <%=eq(v_militaryStatus,"미필")%>>미필</option>
									<option value="면제" <%=eq(v_militaryStatus,"면제")%>>면제</option>
									<option value="해당없음" <%=eq(v_militaryStatus,"해당없음")%>>해당없음</option>
							</select></td>
							<th>군별</th>
							<td class="span-1"><select class="select" name="militaryBranchCode">
									<option value="" <%=v_militaryBranchCode.isEmpty() ? "selected" : ""%>>선택해주세요</option>
									<option value="육군" <%=eq(v_militaryBranchCode,"육군")%>>육군</option>
									<option value="해군" <%=eq(v_militaryBranchCode,"해군")%>>해군</option>
									<option value="공군" <%=eq(v_militaryBranchCode,"공군")%>>공군</option>
									<option value="해병대" <%=eq(v_militaryBranchCode,"해병대")%>>해병대</option>
									<option value="기타" <%=eq(v_militaryBranchCode,"기타")%>>기타</option>
							</select></td>
						</tr>
						<tr>
							<th>복무 시작일</th>
							<td class="span-1"><input type="date" class="input" name="militaryStartDate" value="<%=v_militaryStartDate%>"></td>
							<th>복무 종료일</th>
							<td class="span-1"><input type="date" class="input" name="militaryEndDate" value="<%=v_militaryEndDate%>"></td>
						</tr>
						<tr>
							<th>계급</th>
							<td class="span-1"><input type="text" class="input" name="militaryGrade" value="<%=v_militaryGrade%>"></td>
							<th>병과</th>
							<td class="span-1"><input type="text" class="input" name="militaryBranch" value="<%=v_militaryBranch%>"></td>
						</tr>
						<tr>
							<th>특기</th>
							<td class="span-1"><input type="text" class="input" name="militarySpecialty" value="<%=v_militarySpecialty%>"></td>
							<th>미필/면제 사유</th>
							<td class="span-1"><input type="text" class="input" name="militaryExemptReason" value="<%=v_militaryExemptReason%>"></td>
						</tr>
					</tbody>
				</table>
			</section>

			<div class="source-bottom-actions">
				<!-- [수정] type="submit"으로 변경하여 저장 시 서버(employeeIns1.do)로 전송되도록 설정 -->
				<button type="submit" class="btn btn-primary">저장하기</button>
				<button type="reset" class="btn">취소하기</button>
				<button type="button" class="btn"
					onclick="location.href='employeeList.do'">리스트</button>
				<button type="submit" name="formAction" value="saveAndStay" class="btn btn-blue">신규사원등록</button>
			</div>

		</form>
		<script type="text/javascript">
			function validateForm() {
				// 현재 문서의 첫 번째 폼을 가져옵니다.
				var form = document.forms[0];

				// 1. 고용형태 선택 여부 검사
				if (form.employmentType.value === "") {
					alert("필수 입력 항목입니다: 고용형태를 선택해주세요.");
					form.employmentType.focus(); // 마우스 커서를 해당 칸으로 이동시킴
					return false; // 전송 중단
				}

				// 2. 성명(한글) 입력 여부 검사
				// trim()을 써서 스페이스바만 입력한 꼼수도 잡아냅니다.
				if (form.employeeName.value.trim() === "") {
					alert("필수 입력 항목입니다: 성명(한글)을 입력해주세요.");
					form.employeeName.focus();
					return false;
				}

				// 3. 입사일 선택 여부 검사
				if (form.hireDate.value === "") {
					alert("필수 입력 항목입니다: 입사일을 선택해주세요.");
					form.hireDate.focus();
					return false;
				}

				// 4. 부서 선택 여부 검사
				if (form.department.value === "") {
					alert("필수 입력 항목입니다: 부서를 선택해주세요.");
					form.department.focus();
					return false;
				}

				// 모든 검사를 무사히 통과하면 true를 반환하여 서버로 폼 데이터를 전송합니다!
				return true;
			}
		</script>
	</div>
</div>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>