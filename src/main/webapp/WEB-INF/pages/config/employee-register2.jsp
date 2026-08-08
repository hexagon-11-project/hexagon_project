<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="config.employee.model.Employee"%>
<%!private String nz(String s) {
		return s == null ? "" : s;
	}
	private String eq(String submitted, String optionValue) {
		return optionValue.equals(submitted) ? "selected" : "";
	}
	// "인터넷(050)-1233-2123" 처럼 "-"로 합쳐 저장된 값을, 화면의 쪼개진 입력칸에 다시 채우기 위해 분리
	private String part(String joined, int index) {
		if (joined == null)
			return "";
		String[] parts = joined.split("-", -1);
		return index < parts.length ? parts[index] : "";
	}%>
<%
request.setAttribute("pageTitle", "사원 등록2");
request.setAttribute("pageSection", "기본환경");
request.setAttribute("pageDescription", "사원의 자격·교육·상벌·발령·보증·퇴직 관련 추가정보를 등록합니다.");
request.setAttribute("activeKey", "employee-register2");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<style>
.employee-menu-btn.is-disabled {
	cursor: default;
	opacity: .5;
	pointer-events: none;
}
</style>
<div class="employee-register-shell">
	<aside class="employee-register-side">
		<div class="employee-profile-box">
			<div class="employee-photo-placeholder">
				<span>사원사진</span><small>등록하세요</small>
			</div>
			<!-- [수정 완료] 왼쪽 프로필 요약창에 데이터 꽂기 -->
			<dl class="employee-mini-info">
				<dt>사원번호</dt>
				<dd>${empInfo.employeeNo}</dd>
				<dt>성명</dt>
				<dd>${empInfo.employeeName}</dd>
				<dt>부서</dt>
				<dd>${empInfo.department}</dd>
				<dt>직위</dt>
				<dd>${empInfo.position}</dd>
				<dt>입사일</dt>
				<dd>${empInfo.hireDate}</dd>
			</dl>
			<div class="mini-actions">
				<button type="button" class="btn btn-sm">등록</button>
				<button type="button" class="btn btn-sm">삭제</button>
			</div>
		</div>
		<div class="employee-page-menu">
			<div class="employee-page-label">사원정보 1page</div>
			<div class="employee-menu-grid">
				<!-- history.back()으로 두면 1페이지의 alert이 무조건 다시 뜨고, 실수로 재저장 시
			     같은 사원이 아니라 새 사원번호로 중복 등록될 위험이 있어 클릭 불가능한 안내 라벨로 변경 -->
				<span class="employee-menu-btn is-disabled"
					title="이 항목은 사원등록 1페이지에서 입력합니다">급여<br>&amp;4대보험
				</span> <span class="employee-menu-btn is-disabled"
					title="이 항목은 사원등록 1페이지에서 입력합니다">부양<br>가족
				</span> <span class="employee-menu-btn is-disabled"
					title="이 항목은 사원등록 1페이지에서 입력합니다">학력</span> <span
					class="employee-menu-btn is-disabled"
					title="이 항목은 사원등록 1페이지에서 입력합니다">경력</span> <span
					class="employee-menu-btn is-disabled"
					title="이 항목은 사원등록 1페이지에서 입력합니다">병역</span>
			</div>
			<div class="employee-page-label second">사원정보 2page</div>
			<div class="employee-menu-grid">
				<!-- 2페이지 항목들은 클릭 시 해당 위치로 스크롤 이동 (#아이디) -->
				<a class="employee-menu-btn" href="#license">자격<br>면허
				</a> <a class="employee-menu-btn" href="#training">교육<br>훈련
				</a> <a class="employee-menu-btn" href="#reward">상벌</a> <a
					class="employee-menu-btn" href="#appointment">발령</a> <a
					class="employee-menu-btn" href="#retirement">퇴직</a>
			</div>
		</div>
	</aside>

	<div class="employee-register-main">
		<%
		Employee empInfo = (Employee) request.getAttribute("empInfo");
		int licenseRowCount = 1;
		if (request.getAttribute("licenseRowCount") != null) {
			licenseRowCount = (Integer) request.getAttribute("licenseRowCount");
		}
		int trainingRowCount = 1;
		if (request.getAttribute("trainingRowCount") != null) {
			trainingRowCount = (Integer) request.getAttribute("trainingRowCount");
		}
		int rewardRowCount = 1;
		if (request.getAttribute("rewardRowCount") != null) {
			rewardRowCount = (Integer) request.getAttribute("rewardRowCount");
		}
		int appointmentRowCount = 1;
		if (request.getAttribute("appointmentRowCount") != null) {
			appointmentRowCount = (Integer) request.getAttribute("appointmentRowCount");
		}
		%>
		<!-- 2페이지 데이터를 employeeIns2.do로 전송하기 위한 form 태그 시작 -->
		<form action="<%=request.getContextPath()%>/Config/employeeIns2.do"
			method="post">
			<input type="hidden" name="licenseRowCount"
				value="<%=licenseRowCount%>"> <input type="hidden"
				name="trainingRowCount" value="<%=trainingRowCount%>"> <input
				type="hidden" name="rewardRowCount" value="<%=rewardRowCount%>">
			<input type="hidden" name="appointmentRowCount"
				value="<%=appointmentRowCount%>">

			<section class="source-section">
				<div class="source-section-title">기본정보</div>
				<table class="source-form-table">
					<tbody>
						<tr>
							<th>사원번호</th>
							<td class="span-1"><input type="text" class="input" readonly
								name="employeeNo" value="${empInfo.employeeNo}"></td>
							<th>고용형태</th>
							<td class="span-1"><select class="select"
								name="employmentType">
									<option value=""
										${empty empInfo.employmentType ? 'selected' : ''}>선택해주세요</option>
									<option value="정규직"
										${empInfo.employmentType == '정규직' ? 'selected' : ''}>정규직</option>
									<option value="계약직"
										${empInfo.employmentType == '계약직' ? 'selected' : ''}>계약직</option>
									<option value="임시직"
										${empInfo.employmentType == '임시직' ? 'selected' : ''}>임시직</option>
									<option value="파견직"
										${empInfo.employmentType == '파견직' ? 'selected' : ''}>파견직</option>
									<option value="위촉직"
										${empInfo.employmentType == '위촉직' ? 'selected' : ''}>위촉직</option>
									<option value="일용직"
										${empInfo.employmentType == '일용직' ? 'selected' : ''}>일용직</option>
							</select></td>
						</tr>
						<tr>
							<th>성명(한글)</th>
							<td class="span-1"><input type="text" class="input"
								name="employeeName" value="${empInfo.employeeName}"></td>
							<th>성명(영문)</th>
							<td class="span-1"><input type="text" class="input"
								name="employeeNameEn" value="${empInfo.employeeNameEn}"></td>
						</tr>
						<tr>
							<th>입사일</th>
							<td class="span-1"><input type="date" class="input"
								name="hireDate" value="${empInfo.hireDate}"></td>
							<th>퇴사일</th>
							<td class="span-1"><input type="date" class="input"
								name="resignDate" value="${empInfo.resignDate}"></td>
						</tr>
						<tr>
							<th>부서</th>
							<td class="span-1"><div class="inline-control">
									<select class="select" name="department">
										<option value="" ${empty empInfo.department ? 'selected' : ''}>선택해주세요</option>
										<option value="사장실"
											${empInfo.department == '사장실' ? 'selected' : ''}>사장실</option>
										<option value="개발팀"
											${empInfo.department == '개발팀' ? 'selected' : ''}>개발팀</option>
										<option value="업무지원팀"
											${empInfo.department == '업무지원팀' ? 'selected' : ''}>업무지원팀</option>
										<option value="디자인팀"
											${empInfo.department == '디자인팀' ? 'selected' : ''}>디자인팀</option>
										<option value="관리팀"
											${empInfo.department == '관리팀' ? 'selected' : ''}>관리팀</option>
										<option value="기획전략팀"
											${empInfo.department == '기획전략팀' ? 'selected' : ''}>기획전략팀</option>
										<option value="콘텐츠팀"
											${empInfo.department == '콘텐츠팀' ? 'selected' : ''}>콘텐츠팀</option>
									</select>
								</div></td>
							<th>직위</th>
							<td class="span-1"><div class="inline-control">
									<select class="select" name="position">
										<option value="" ${empty empInfo.position ? 'selected' : ''}>선택해주세요</option>
										<option value="사장"
											${empInfo.position == '사장' ? 'selected' : ''}>사장</option>
										<option value="이사"
											${empInfo.position == '이사' ? 'selected' : ''}>이사</option>
										<option value="부장"
											${empInfo.position == '부장' ? 'selected' : ''}>부장</option>
										<option value="차장"
											${empInfo.position == '차장' ? 'selected' : ''}>차장</option>
										<option value="과장"
											${empInfo.position == '과장' ? 'selected' : ''}>과장</option>
										<option value="대리"
											${empInfo.position == '대리' ? 'selected' : ''}>대리</option>
										<option value="주임"
											${empInfo.position == '주임' ? 'selected' : ''}>주임</option>
										<option value="실장"
											${empInfo.position == '실장' ? 'selected' : ''}>실장</option>
										<option value="사원"
											${empInfo.position == '사원' ? 'selected' : ''}>사원</option>
									</select>
								</div></td>
						</tr>
						<tr>
							<th>내/외국인</th>
							<td class="span-1"><select class="select" name="domForYn">
									<option value="" ${empty empInfo.domForYn ? 'selected' : ''}>선택해주세요</option>
									<option value="Y" ${empInfo.domForYn == 'Y' ? 'selected' : ''}>내국인</option>
									<option value="N" ${empInfo.domForYn == 'N' ? 'selected' : ''}>외국인</option>
							</select></td>
							<th>주민번호</th>
							<td class="span-1"><div class="rrn-fields">
									<input type="text" class="input" name="residentRegNoFront"
										value="<%=part(empInfo != null ? empInfo.getResidentRegNo() : null, 0)%>"><span>-</span><input
										type="text" class="input" name="residentRegNoBack"
										value="<%=part(empInfo != null ? empInfo.getResidentRegNo() : null, 1)%>">
								</div></td>
						</tr>
						<tr>
							<th>전화번호</th>
							<td class="span-1"><div class="phone-fields">
									<select class="select" name="phone1">
										<option value=""
											<%=part(empInfo != null ? empInfo.getPhone() : null, 0).isEmpty() ? "selected" : ""%>>선택</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "대표(없음)")%>>대표(없음)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "휴대폰(010)")%>>휴대폰(010)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "인터넷(050)")%>>인터넷(050)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "인터넷(0507)")%>>인터넷(0507)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "인터넷(070)")%>>인터넷(070)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "인터넷(0303)")%>>인터넷(0303)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "인터넷(0504)")%>>인터넷(0504)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "서울(02)")%>>서울(02)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "부산(051)")%>>부산(051)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "대구(053)")%>>대구(053)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "인천(032)")%>>인천(032)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "광주(062)")%>>광주(062)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "대전(042)")%>>대전(042)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "울산(052)")%>>울산(052)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "세종(044)")%>>세종(044)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "경기(031)")%>>경기(031)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "강원(033)")%>>강원(033)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "충북(043)")%>>충북(043)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "충남(041)")%>>충남(041)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "전북(063)")%>>전북(063)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "전남(061)")%>>전남(061)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "경북(054)")%>>경북(054)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "경남(055)")%>>경남(055)</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "제주(064)")%>>제주(064)</option>
									</select><input type="text" class="input" name="phone2"
										value="<%=part(empInfo != null ? empInfo.getPhone() : null, 1)%>"><input
										type="text" class="input" name="phone3"
										value="<%=part(empInfo != null ? empInfo.getPhone() : null, 2)%>">
								</div></td>
							<th>휴대폰</th>
							<td class="span-1"><div class="phone-fields">
									<select class="select" name="mobile1">
										<option value=""
											<%=part(empInfo != null ? empInfo.getMobile() : null, 0).isEmpty() ? "selected" : ""%>>선택</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getMobile() : null, 0), "010")%>>010</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getMobile() : null, 0), "011")%>>011</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getMobile() : null, 0), "016")%>>016</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getMobile() : null, 0), "017")%>>017</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getMobile() : null, 0), "018")%>>018</option>
										<option
											<%=eq(part(empInfo != null ? empInfo.getMobile() : null, 0), "019")%>>019</option>
									</select><input type="text" class="input" name="mobile2"
										value="<%=part(empInfo != null ? empInfo.getMobile() : null, 1)%>"><input
										type="text" class="input" name="mobile3"
										value="<%=part(empInfo != null ? empInfo.getMobile() : null, 2)%>">
								</div></td>
						</tr>
						<tr>
							<th>이메일</th>
							<td class="span-1"><input type="email" class="input"
								name="email" value="${empInfo.email}"></td>
							<th>SNS</th>
							<td class="span-1"><input type="text" class="input"
								name="sns" value="${empInfo.sns}"></td>
						</tr>
					</tbody>
				</table>
			</section>

			<div class="source-page-caption">사원정보 2page</div>

			<!-- 아래 추가 정보 섹션(자격, 교육 등)은 새로 추가하는 항목들이므로 그대로 두시면 됩니다 -->
			<section class="source-section" id="license">
				<div class="source-section-title">자격면허</div>
				<div class="table-toolbar compact">
					<div></div>
					<div class="actions">
						<button type="submit" name="formAction" value="addLicenseRow"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#license">추가</button>
						<button type="submit" name="formAction" value="deleteLicenseRows"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#license">선택삭제</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table" id="licenseTable">
						<thead>
							<tr>
								<th>선택</th>
								<th>자격/면허명</th>
								<th>취득일</th>
								<th>발급기관</th>
								<th>등급</th>
								<th>비고</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 1; i <= licenseRowCount; i++) {
								String licName = nz(request.getParameter("licenseName" + i));
								String licDate = nz(request.getParameter("licenseDate" + i));
								String licOrg = nz(request.getParameter("licenseOrg" + i));
								String licGrade = nz(request.getParameter("licenseGrade" + i));
								String licMemo = nz(request.getParameter("licenseMemo" + i));
							%>
							<tr>
								<td><input type="checkbox" name="licenseDel<%=i%>"></td>
								<td><input class="input" type="text"
									name="licenseName<%=i%>" value="<%=licName%>"></td>
								<td><input class="input" type="date"
									name="licenseDate<%=i%>" value="<%=licDate%>"></td>
								<td><input class="input" type="text"
									name="licenseOrg<%=i%>" value="<%=licOrg%>"></td>
								<td><input class="input" type="text"
									name="licenseGrade<%=i%>" value="<%=licGrade%>"></td>
								<td><input class="input" type="text"
									name="licenseMemo<%=i%>" value="<%=licMemo%>"></td>
							</tr>
							<%
							}
							%>
						</tbody>
					</table>
				</div>
			</section>

			<section class="source-section" id="training">
				<div class="source-section-title">교육 &amp; 훈련</div>
				<div class="table-toolbar compact">
					<div></div>
					<div class="actions">
						<button type="submit" name="formAction" value="addTrainingRow"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#training">추가</button>
						<button type="submit" name="formAction" value="deleteTrainingRows"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#training">선택삭제</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table" id="trainingTable">
						<thead>
							<tr>
								<th>선택</th>
								<th>교육구분</th>
								<th>교육명</th>
								<th>교육기관</th>
								<th>시작일</th>
								<th>종료일</th>
								<th>교육비</th>
								<th>환급액</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 1; i <= trainingRowCount; i++) {
								String trType = nz(request.getParameter("trainingType" + i));
								String trName = nz(request.getParameter("trainingName" + i));
								String trOrg = nz(request.getParameter("trainingOrg" + i));
								String trStart = nz(request.getParameter("trainingStart" + i));
								String trEnd = nz(request.getParameter("trainingEnd" + i));
								String trCost = nz(request.getParameter("trainingCost" + i));
								String trRefund = nz(request.getParameter("trainingRefund" + i));
							%>
							<tr>
								<td><input type="checkbox" name="trainingDel<%=i%>"></td>
								<td><select class="select" name="trainingType<%=i%>">
										<option value="" <%=trType.isEmpty() ? "selected" : ""%>>선택</option>
										<option value="사내직무" <%=eq(trType, "사내직무")%>>사내직무</option>
										<option value="사외직무" <%=eq(trType, "사외직무")%>>사외직무</option>
										<option value="계층교육" <%=eq(trType, "계층교육")%>>계층교육</option>
										<option value="어학교육" <%=eq(trType, "어학교육")%>>어학교육</option>
										<option value="기타" <%=eq(trType, "기타")%>>기타</option>
								</select></td>
								<td><input class="input" type="text"
									name="trainingName<%=i%>" value="<%=trName%>"></td>
								<td><input class="input" type="text"
									name="trainingOrg<%=i%>" value="<%=trOrg%>"></td>
								<td><input class="input" type="date"
									name="trainingStart<%=i%>" value="<%=trStart%>"></td>
								<td><input class="input" type="date"
									name="trainingEnd<%=i%>" value="<%=trEnd%>"></td>
								<td><input class="input number" type="text"
									name="trainingCost<%=i%>" value="<%=trCost%>"></td>
								<td><input class="input number" type="text"
									name="trainingRefund<%=i%>" value="<%=trRefund%>"></td>
							</tr>
							<%
							}
							%>
						</tbody>
					</table>
				</div>
			</section>

			<section class="source-section" id="reward">
				<div class="source-section-title">상벌</div>
				<div class="table-toolbar compact">
					<div></div>
					<div class="actions">
						<button type="submit" name="formAction" value="addRewardRow"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#reward">추가</button>
						<button type="submit" name="formAction" value="deleteRewardRows"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#reward">선택삭제</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table" id="rewardTable">
						<thead>
							<tr>
								<th>선택</th>
								<th>구분</th>
								<th>상벌일자</th>
								<th>상벌명</th>
								<th>상벌내용</th>
								<th>비고</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 1; i <= rewardRowCount; i++) {
								String rwType = nz(request.getParameter("rewardType" + i));
								String rwDate = nz(request.getParameter("rewardDate" + i));
								String rwName = nz(request.getParameter("rewardName" + i));
								String rwContent = nz(request.getParameter("rewardContent" + i));
								String rwMemo = nz(request.getParameter("rewardMemo" + i));
							%>
							<tr>
								<td><input type="checkbox" name="rewardDel<%=i%>"></td>
								<td><select class="select" name="rewardType<%=i%>">
										<option value="" <%=rwType.isEmpty() ? "selected" : ""%>>선택</option>
										<option value="포상" <%=eq(rwType, "포상")%>>포상</option>
										<option value="표창" <%=eq(rwType, "표창")%>>표창</option>
										<option value="시상" <%=eq(rwType, "시상")%>>시상</option>
										<option value="면직" <%=eq(rwType, "면직")%>>면직</option>
										<option value="정직" <%=eq(rwType, "정직")%>>정직</option>
										<option value="감봉" <%=eq(rwType, "감봉")%>>감봉</option>
										<option value="견책" <%=eq(rwType, "견책")%>>견책</option>
										<option value="주의" <%=eq(rwType, "주의")%>>주의</option>
										<option value="경고" <%=eq(rwType, "경고")%>>경고</option>
										<option value="조치불가" <%=eq(rwType, "조치불가")%>>조치불가</option>
										<option value="해고" <%=eq(rwType, "해고")%>>해고</option>
								</select></td>
								<td><input class="input" type="date"
									name="rewardDate<%=i%>" value="<%=rwDate%>"></td>
								<td><input class="input" type="text"
									name="rewardName<%=i%>" value="<%=rwName%>"></td>
								<td><input class="input" type="text"
									name="rewardContent<%=i%>" value="<%=rwContent%>"></td>
								<td><input class="input" type="text"
									name="rewardMemo<%=i%>" value="<%=rwMemo%>"></td>
							</tr>
							<%
							}
							%>
						</tbody>
					</table>
				</div>
			</section>

			<section class="source-section" id="appointment">
				<div class="source-section-title">발령</div>
				<div class="table-toolbar compact">
					<div></div>
					<div class="actions">
						<button type="submit" name="formAction" value="addAppointmentRow"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#appointment">추가</button>
						<button type="submit" name="formAction"
							value="deleteAppointmentRows" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#appointment">선택삭제</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table" id="appointmentTable">
						<thead>
							<tr>
								<th>선택</th>
								<th>발령구분</th>
								<th>발령일자</th>
								<th>부서</th>
								<th>직위</th>
								<th>직책 및 담당업무</th>
								<th>비고</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 1; i <= appointmentRowCount; i++) {
								String apType = nz(request.getParameter("apptType" + i));
								String apDate = nz(request.getParameter("apptDate" + i));
								String apDept = nz(request.getParameter("apptDept" + i));
								String apPosition = nz(request.getParameter("apptPosition" + i));
								String apDuty = nz(request.getParameter("apptDuty" + i));
								String apMemo = nz(request.getParameter("apptMemo" + i));
							%>
							<tr>
								<td><input type="checkbox" name="apptDel<%=i%>"></td>
								<td><select class="select" name="apptType<%=i%>">
										<option value="" <%=apType.isEmpty() ? "selected" : ""%>>선택</option>
										<option value="채용" <%=eq(apType, "채용")%>>채용</option>
										<option value="전보" <%=eq(apType, "전보")%>>전보</option>
										<option value="승진" <%=eq(apType, "승진")%>>승진</option>
										<option value="승격" <%=eq(apType, "승격")%>>승격</option>
										<option value="승호" <%=eq(apType, "승호")%>>승호</option>
										<option value="파견" <%=eq(apType, "파견")%>>파견</option>
								</select></td>
								<td><input class="input" type="date" name="apptDate<%=i%>"
									value="<%=apDate%>"></td>
								<td><input class="input" type="text" name="apptDept<%=i%>"
									value="<%=apDept%>"></td>
								<td><input class="input" type="text"
									name="apptPosition<%=i%>" value="<%=apPosition%>"></td>
								<td><input class="input" type="text" name="apptDuty<%=i%>"
									value="<%=apDuty%>"></td>
								<td><input class="input" type="text" name="apptMemo<%=i%>"
									value="<%=apMemo%>"></td>
							</tr>
							<%
							}
							%>
						</tbody>
					</table>
				</div>
			</section>

			<section class="source-section" id="retirement">
				<div class="source-section-title">퇴직</div>
				<table class="source-form-table">
					<tbody>
						<tr>
							<th>퇴직구분</th>
							<td class="span-1"><select class="select" name="retireType">
									<option value=""
										${empty empInfo.retirementTypeCode ? 'selected' : ''}>선택해주세요</option>
									<option value="자진퇴사"
										${empInfo.retirementTypeCode == '자진퇴사' ? 'selected' : ''}>자진퇴사</option>
									<option value="계약만료"
										${empInfo.retirementTypeCode == '계약만료' ? 'selected' : ''}>계약만료</option>
									<option value="권고사직"
										${empInfo.retirementTypeCode == '권고사직' ? 'selected' : ''}>권고사직</option>
									<option value="정년퇴직"
										${empInfo.retirementTypeCode == '정년퇴직' ? 'selected' : ''}>정년퇴직</option>
							</select></td>
							<th>퇴직일자</th>
							<td class="span-1"><input type="date" class="input"
								name="retireDate" value="${empInfo.resignDate}"></td>
						</tr>
						<tr>
							<th>퇴직사유</th>
							<td class="span-3"><textarea class="textarea"
									name="retireReason">${empInfo.retirementReason}</textarea></td>
						</tr>
						<tr>
							<th>퇴직 후 연락처</th>
							<td class="span-1"><input type="text" class="input"
								name="retirePhone" value="${empInfo.postRetirementPhone}"></td>
						</tr>
					</tbody>
				</table>
			</section>

			<div class="source-bottom-actions">
				<button type="submit" class="btn btn-primary">저장하기</button>
				<button type="reset" class="btn">취소하기</button>
				<button type="button" class="btn"
					onclick="location.href='employeeList.do'">리스트</button>
				<button type="submit" class="btn btn-blue">신규사원등록</button>
			</div>

		</form>
		<%
		if (request.getAttribute("justSaved") != null) {
		%>
		<script type="text/javascript">
			alert('저장되었습니다.');
		</script>
		<%
		}
		%>
	</div>
</div>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>