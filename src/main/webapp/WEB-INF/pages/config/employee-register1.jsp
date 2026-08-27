<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="config.employee.model.Employee"%>
<%
// 社員一覧から名前クリックで遷移した場合 - DBから取得した社員データ
// 사원현황에서 이름 클릭으로 진입한 경우 - DB에서 불러온 사원 데이터
Employee loadEmployee = (Employee) request.getAttribute("loadEmployee");
%>
<%!
	// nullの場合は空文字列に変換 / null이면 빈 문자열로
	private String nz(String s) { return s == null ? "" : s; }

	// requestのattribute → getParameterの順で値を取得 / request attribute → getParameter 순으로 값 읽기
	private String getAttrOrParam(javax.servlet.http.HttpServletRequest req, String key) {
		Object attr = req.getAttribute(key);
		if (attr != null) return attr.toString();
		return nz(req.getParameter(key));
	}

	// 電話番号を市外局番・局番・番号の3パーツに分割 / 전화번호를 지역번호-국번-번호 3부분으로 분리
	private String[] splitPhone(String phone) {
		if (phone == null || phone.isBlank()) return new String[]{"", "", ""};
		String[] parts = phone.split("-");
		if (parts.length == 3) return parts;
		if (parts.length == 2) return new String[]{parts[0], parts[1], ""};
		return new String[]{phone, "", ""};
	}
	// selectのoptionにselected属性を付与 / select의 option에 selected 속성
	private String eq(String submitted, String optionValue) {
		return optionValue.equals(submitted) ? "selected" : "";
	}
	// radio/checkboxにchecked属性を付与 / radio/checkbox에 checked 속성
	private String chk(String submitted, String optionValue) {
		return optionValue.equals(submitted) ? "checked" : "";
	}
%>
<%
request.setAttribute("pageTitle", "社員登録1");
request.setAttribute("pageSection", "基本環境");
request.setAttribute("pageDescription", "社員の基本情報・給与・保険・家族・学歴・経歴・兵役情報を登録します。");
request.setAttribute("activeKey", "employee-register1");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<div class="employee-register-shell">
	<aside class="employee-register-side">
		<div class="employee-profile-box">
			<!-- 社員写真エリア - クリックで写真選択ダイアログを開く / 사원사진 영역 - 클릭 시 모달 오픈 -->
			<div class="employee-photo-placeholder" id="photoBox" onclick="document.getElementById('photoSelectDialog').classList.add('open');" style="cursor:pointer;">
				<%
				String currentPhoto = loadEmployee != null && loadEmployee.getPhotoPath() != null ? loadEmployee.getPhotoPath() : nz(request.getParameter("photoPath"));
				if (!currentPhoto.isEmpty()) {
				%>
				<img src="<%=request.getContextPath()%><%=currentPhoto%>" style="width:100%;height:100%;object-fit:cover;">
				<%
				} else {
				%>
				<span>社員写真</span><small>クリックして選択</small>
				<%
				}
				%>
			</div>
			<!-- 社員基本情報サマリー / 사원 기본정보 요약 -->
			<dl class="employee-mini-info">
				<dt>社員番号</dt>
				<dd>${defaultEmpNo}</dd>
				<dt>氏名</dt>
				<dd><%=loadEmployee != null ? nz(loadEmployee.getEmployeeName()) : nz(request.getParameter("employeeName"))%></dd>
				<dt>部署</dt>
				<dd><%=loadEmployee != null ? nz(loadEmployee.getDepartment()) : nz(request.getParameter("department"))%></dd>
				<dt>役職</dt>
				<dd><%=loadEmployee != null ? nz(loadEmployee.getPosition()) : nz(request.getParameter("position"))%></dd>
				<dt>入社日</dt>
				<dd><%=loadEmployee != null && loadEmployee.getHireDate() != null ? loadEmployee.getHireDate().toString() : nz(request.getParameter("hireDate"))%></dd>
			</dl>
			<div class="mini-actions">
				<!-- 写真登録・削除ボタン / 사진 등록/삭제 버튼 -->
				<button type="button" class="btn btn-sm" onclick="document.getElementById('photoSelectDialog').classList.add('open');">登録</button>
				<button type="button" class="btn btn-sm" onclick="clearPhoto();">削除</button>
			</div>
		</div>
		<div class="employee-page-menu">
			<div class="employee-page-label">社員情報 1page</div>
			<div class="employee-menu-grid">
				<!-- 1ページ内セクションへスクロール / 1페이지 내 섹션 스크롤 이동 -->
				<a class="employee-menu-btn" href="#pay-insurance">給与<br>&amp;社会保険
				</a> <a class="employee-menu-btn" href="#family">扶養<br>家族
				</a> <a class="employee-menu-btn" href="#education">学歴</a> <a
					class="employee-menu-btn" href="#career">経歴</a> <a
					class="employee-menu-btn" href="#military">兵役</a>
			</div>
			<div class="employee-page-label second">社員情報 2page</div>
			<div class="employee-menu-grid">
				<!-- 2ページボタン - 基本情報保存後に移動するよう案内 / 2페이지 버튼 - 저장 후 이동 안내 -->
				<a class="employee-menu-btn" href="#"
					onclick="alert('基本情報を保存してから2ページへ移動してください。'); return false;">資格<br>免許
				</a> <a class="employee-menu-btn" href="#"
					onclick="alert('基本情報を保存してから2ページへ移動してください。'); return false;">教育<br>訓練
				</a> <a class="employee-menu-btn" href="#"
					onclick="alert('基本情報を保存してから2ページへ移動してください。'); return false;">賞罰</a>
				<a class="employee-menu-btn" href="#"
					onclick="alert('基本情報を保存してから2ページへ移動してください。'); return false;">異動</a>
				<a class="employee-menu-btn" href="#"
					onclick="alert('基本情報を保存してから2ページへ移動してください。'); return false;">退職</a>
			</div>
		</div>
	</aside>

	<div class="employee-register-main">
		<%
		// テーブルの表示行数をサーバーで管理 / 각 테이블에 몇 줄을 그릴지 서버가 기억
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
		<!-- フォームのactionをハンドラへ指定 / action을 핸들러로 지정 -->
		<form action="<%=request.getContextPath()%>/Config/employeeIns1.do"
			method="post" onsubmit="return validateForm()">
			<input type="hidden" name="familyRowCount" value="<%=familyRowCount%>">
			<input type="hidden" name="educationRowCount" value="<%=educationRowCount%>">
			<input type="hidden" name="careerRowCount" value="<%=careerRowCount%>">
			<input type="hidden" name="photoPath" id="photoPathInput" value="<%=currentPhoto%>">
			<%
			// 既存社員修正時 - IDと番号を保持 / 기존 사원 수정 시 - ID와 사원번호 유지
			String existingEmployeeId = loadEmployee != null
				? String.valueOf(loadEmployee.getEmployeeId())
				: nz(request.getParameter("existingEmployeeId"));
			String existingEmployeeNo = loadEmployee != null
				? loadEmployee.getEmployeeNo()
				: nz(request.getParameter("existingEmployeeNo"));
			if (!existingEmployeeId.isEmpty()) {
			%>
			<input type="hidden" name="existingEmployeeId" value="<%=existingEmployeeId%>">
			<input type="hidden" name="existingEmployeeNo" value="<%=existingEmployeeNo%>">
			<%
			}
			%>

			<section class="source-section">
				<div class="source-section-title">基本情報</div>
				<%
				// サーバー往復後もフォーム値を保持 / 서버 왕복 후에도 값을 유지
				if (loadEmployee != null) {
					request.setAttribute("_emp_loaded", "true");
					request.setAttribute("emp", loadEmployee);
				}
				String v_employmentType = loadEmployee != null ? nz(loadEmployee.getEmploymentType()) : nz(request.getParameter("employmentType"));
				String v_employeeName = loadEmployee != null ? nz(loadEmployee.getEmployeeName()) : nz(request.getParameter("employeeName"));
				String v_employeeNameEn = loadEmployee != null ? nz(loadEmployee.getEmployeeNameEn()) : nz(request.getParameter("employeeNameEn"));
				String v_hireDate = loadEmployee != null && loadEmployee.getHireDate() != null ? loadEmployee.getHireDate().toString() : nz(request.getParameter("hireDate"));
				String v_resignDate = loadEmployee != null && loadEmployee.getResignDate() != null ? loadEmployee.getResignDate().toString() : nz(request.getParameter("resignDate"));
				String v_department = loadEmployee != null ? nz(loadEmployee.getDepartment()) : nz(request.getParameter("department"));
				String v_position = loadEmployee != null ? nz(loadEmployee.getPosition()) : nz(request.getParameter("position"));
				String v_domForYn = loadEmployee != null ? nz(loadEmployee.getDomForYn()) : nz(request.getParameter("domForYn"));
				String rrn = loadEmployee != null ? nz(loadEmployee.getResidentRegNo()) : "";
				String v_rrnFront = loadEmployee != null ? (rrn.length() >= 6 ? rrn.substring(0, 6) : rrn) : nz(request.getParameter("residentRegNoFront"));
				String v_rrnBack = loadEmployee != null ? (rrn.length() > 6 ? rrn.substring(7) : "") : nz(request.getParameter("residentRegNoBack"));
				String[] phoneParts = loadEmployee != null ? splitPhone(nz(loadEmployee.getPhone())) : new String[]{"", "", ""};
				String v_phone1 = loadEmployee != null ? phoneParts[0] : nz(request.getParameter("phone1"));
				String v_phone2 = loadEmployee != null ? phoneParts[1] : nz(request.getParameter("phone2"));
				String v_phone3 = loadEmployee != null ? phoneParts[2] : nz(request.getParameter("phone3"));
				String[] mobileParts = loadEmployee != null ? splitPhone(nz(loadEmployee.getMobile())) : new String[]{"", "", ""};
				String v_mobile1 = loadEmployee != null ? mobileParts[0] : nz(request.getParameter("mobile1"));
				String v_mobile2 = loadEmployee != null ? mobileParts[1] : nz(request.getParameter("mobile2"));
				String v_mobile3 = loadEmployee != null ? mobileParts[2] : nz(request.getParameter("mobile3"));
				String v_email = loadEmployee != null ? nz(loadEmployee.getEmail()) : nz(request.getParameter("email"));
				String v_sns = loadEmployee != null ? nz(loadEmployee.getSns()) : nz(request.getParameter("sns"));
				String v_empIncomeType = loadEmployee != null ? nz(loadEmployee.getEmpIncomeType()) : nz(request.getParameter("empIncomeType"));
				if (v_empIncomeType.isEmpty()) { v_empIncomeType = "근로소득자 갑근세"; }
				String v_bankName = loadEmployee != null ? nz(loadEmployee.getBankName()) : nz(request.getParameter("bankName"));
				String v_bankAccount = loadEmployee != null ? nz(loadEmployee.getBankAccount()) : nz(request.getParameter("bankAccount"));
				String v_insNoNP = getAttrOrParam(request, "insuranceNoNP");
				String v_insAcqNP = getAttrOrParam(request, "acquisitionDateNP");
				String v_insLossNP = getAttrOrParam(request, "lossDateNP");
				String v_insNoHI = getAttrOrParam(request, "insuranceNoHI");
				String v_insAcqHI = getAttrOrParam(request, "acquisitionDateHI");
				String v_insLossHI = getAttrOrParam(request, "lossDateHI");
				String v_insNoEI = getAttrOrParam(request, "insuranceNoEI");
				String v_insAcqEI = getAttrOrParam(request, "acquisitionDateEI");
				String v_insLossEI = getAttrOrParam(request, "lossDateEI");
				String v_insNoII = getAttrOrParam(request, "insuranceNoII");
				String v_insAcqII = getAttrOrParam(request, "acquisitionDateII");
				String v_insLossII = getAttrOrParam(request, "lossDateII");
				String v_baseWageAmount = loadEmployee != null && loadEmployee.getBaseWageAmount() > 0
						? String.valueOf(loadEmployee.getBaseWageAmount()) : nz(request.getParameter("baseWageAmount"));
				if (v_baseWageAmount.isEmpty()) { v_baseWageAmount = "0"; }
				String v_nationalPensionBaseAmount = loadEmployee != null && loadEmployee.getNationalPensionBaseAmount() > 0
						? String.valueOf(loadEmployee.getNationalPensionBaseAmount()) : nz(request.getParameter("nationalPensionBaseAmount"));
				if (v_nationalPensionBaseAmount.isEmpty()) { v_nationalPensionBaseAmount = "0"; }
				String v_healthInsuranceBaseAmount = loadEmployee != null && loadEmployee.getHealthInsuranceBaseAmount() > 0
						? String.valueOf(loadEmployee.getHealthInsuranceBaseAmount()) : nz(request.getParameter("healthInsuranceBaseAmount"));
				if (v_healthInsuranceBaseAmount.isEmpty()) { v_healthInsuranceBaseAmount = "0"; }
				String v_employmentInsuranceAmount = loadEmployee != null && loadEmployee.getEmploymentInsuranceAmount() > 0
						? String.valueOf(loadEmployee.getEmploymentInsuranceAmount()) : nz(request.getParameter("employmentInsuranceAmount"));
				if (v_employmentInsuranceAmount.isEmpty()) { v_employmentInsuranceAmount = "0"; }
				String v_militaryStatus = getAttrOrParam(request, "militaryStatus");
				String v_militaryBranchCode = getAttrOrParam(request, "militaryBranchCode");
				String v_militaryStartDate = getAttrOrParam(request, "militaryStartDate");
				String v_militaryEndDate = getAttrOrParam(request, "militaryEndDate");
				String v_militaryGrade = getAttrOrParam(request, "militaryGrade");
				String v_militaryBranch = getAttrOrParam(request, "militaryBranch");
				String v_militarySpecialty = getAttrOrParam(request, "militarySpecialty");
				String v_militaryExemptReason = getAttrOrParam(request, "militaryExemptReason");
				%>
				<table class="source-form-table">
					<tbody>
						<tr>
							<th>社員番号</th>
							<td class="span-1"><input type="text" class="input" readonly
								name="employeeNo" value="${defaultEmpNo}"></td>
							<th><span class="required-mark">*</span>雇用形態</th>
							<td class="span-1">
								<select class="select" name="employmentType">
									<option value="" <%=v_employmentType.isEmpty()?"selected":""%>>選択してください</option>
									<option value="정규직" <%=eq(v_employmentType,"정규직")%>>正規職</option>
									<option value="계약직" <%=eq(v_employmentType,"계약직")%>>契約職</option>
									<option value="임시직" <%=eq(v_employmentType,"임시직")%>>臨時職</option>
									<option value="파견직" <%=eq(v_employmentType,"파견직")%>>派遣職</option>
									<option value="위촉직" <%=eq(v_employmentType,"위촉직")%>>嘱託職</option>
									<option value="일용직" <%=eq(v_employmentType,"일용직")%>>日雇職</option>
								</select>
							</td>
						</tr>
						<tr>
							<th><span class="required-mark">*</span>氏名（漢字）</th>
							<td class="span-1"><input type="text" class="input"
								name="employeeName" placeholder="氏名を入力してください" value="<%=v_employeeName%>"></td>
							<th>氏名（英語）</th>
							<td class="span-1"><input type="text" class="input"
								name="employeeNameEn" placeholder="English name" value="<%=v_employeeNameEn%>"></td>
						</tr>
						<tr>
							<th><span class="required-mark">*</span>入社日</th>
							<td class="span-1"><input type="date" class="input"
								name="hireDate" value="<%=v_hireDate%>"></td>
							<th>退社日</th>
							<td class="span-1"><input type="date" class="input"
								name="resignDate" value="<%=v_resignDate%>"></td>
						</tr>
						<tr>
							<th><span class="required-mark">*</span>部署</th>
							<td class="span-1">
								<div class="inline-control">
									<select class="select" name="department">
										<option value="" <%=v_department.isEmpty()?"selected":""%>>選択してください</option>
										<option value="사장실" <%=eq(v_department,"사장실")%>>社長室</option>
										<option value="개발팀" <%=eq(v_department,"개발팀")%>>開発チーム</option>
										<option value="업무지원팀" <%=eq(v_department,"업무지원팀")%>>業務支援チーム</option>
										<option value="디자인팀" <%=eq(v_department,"디자인팀")%>>デザインチーム</option>
										<option value="관리팀" <%=eq(v_department,"관리팀")%>>管理チーム</option>
										<option value="기획전략팀" <%=eq(v_department,"기획전략팀")%>>企画戦略チーム</option>
										<option value="콘텐츠팀" <%=eq(v_department,"콘텐츠팀")%>>コンテンツチーム</option>
										<option value="인사팀" <%=eq(v_department,"인사팀")%>>人事チーム</option>
										<option value="현장운영팀" <%=eq(v_department,"현장운영팀")%>>現場運営チーム</option>
										<option value="재무팀" <%=eq(v_department,"재무팀")%>>財務チーム</option>
										<option value="개발프로젝트" <%=eq(v_department,"개발프로젝트")%>>開発プロジェクト</option>
										<option value="연구소" <%=eq(v_department,"연구소")%>>研究所</option>
									</select>
								</div>
							</td>
							<th>役職</th>
							<td class="span-1">
								<div class="inline-control">
									<select class="select" name="position">
										<option value="" <%=v_position.isEmpty()?"selected":""%>>選択してください</option>
										<option value="사장" <%=eq(v_position,"사장")%>>社長</option>
										<option value="이사" <%=eq(v_position,"이사")%>>取締役</option>
										<option value="부장" <%=eq(v_position,"부장")%>>部長</option>
										<option value="차장" <%=eq(v_position,"차장")%>>次長</option>
										<option value="과장" <%=eq(v_position,"과장")%>>課長</option>
										<option value="대리" <%=eq(v_position,"대리")%>>代理</option>
										<option value="주임" <%=eq(v_position,"주임")%>>主任</option>
										<option value="실장" <%=eq(v_position,"실장")%>>室長</option>
										<option value="사원" <%=eq(v_position,"사원")%>>社員</option>
										<option value="전문위원" <%=eq(v_position,"전문위원")%>>専門委員</option>
										<option value="일용근로자" <%=eq(v_position,"일용근로자")%>>日雇労働者</option>
									</select>
								</div>
							</td>
						</tr>
						<tr>
							<th>国籍区分</th>
							<td class="span-1">
								<select class="select" name="domForYn">
									<option value="" <%=v_domForYn.isEmpty()?"selected":""%>>選択してください</option>
									<option value="Y" <%=eq(v_domForYn,"Y")%>>日本国籍</option>
									<option value="N" <%=eq(v_domForYn,"N")%>>外国籍</option>
								</select>
							</td>
							<th>マイナンバー</th>
							<td class="span-1">
								<div class="rrn-fields">
									<input type="text" class="input" name="residentRegNoFront" placeholder="前半" value="<%=v_rrnFront%>"><span>-</span><input
										type="text" class="input" name="residentRegNoBack" placeholder="後半" value="<%=v_rrnBack%>">
								</div>
							</td>
						</tr>
						<tr>
							<th>電話番号</th>
							<td class="span-1">
								<div class="phone-fields">
									<select class="select" name="phone1">
										<option value="" <%=v_phone1.isEmpty()?"selected":""%>>選択</option>
										<option <%=eq(v_phone1,"대표(없음)")%>>代表(なし)</option>
										<option <%=eq(v_phone1,"휴대폰(010)")%>>携帯(010)</option>
										<option <%=eq(v_phone1,"인터넷(050)")%>>IP電話(050)</option>
										<option <%=eq(v_phone1,"인터넷(0507)")%>>IP電話(0507)</option>
										<option <%=eq(v_phone1,"인터넷(070)")%>>IP電話(070)</option>
										<option <%=eq(v_phone1,"인터넷(0303)")%>>IP電話(0303)</option>
										<option <%=eq(v_phone1,"인터넷(0504)")%>>IP電話(0504)</option>
										<option <%=eq(v_phone1,"서울(02)")%>>ソウル(02)</option>
										<option <%=eq(v_phone1,"부산(051)")%>>釜山(051)</option>
										<option <%=eq(v_phone1,"대구(053)")%>>大邱(053)</option>
										<option <%=eq(v_phone1,"인천(032)")%>>仁川(032)</option>
										<option <%=eq(v_phone1,"광주(062)")%>>光州(062)</option>
										<option <%=eq(v_phone1,"대전(042)")%>>大田(042)</option>
										<option <%=eq(v_phone1,"울산(052)")%>>蔚山(052)</option>
										<option <%=eq(v_phone1,"세종(044)")%>>世宗(044)</option>
										<option <%=eq(v_phone1,"경기(031)")%>>京畿(031)</option>
										<option <%=eq(v_phone1,"강원(033)")%>>江原(033)</option>
										<option <%=eq(v_phone1,"충북(043)")%>>忠北(043)</option>
										<option <%=eq(v_phone1,"충남(041)")%>>忠南(041)</option>
										<option <%=eq(v_phone1,"전북(063)")%>>全北(063)</option>
										<option <%=eq(v_phone1,"전남(061)")%>>全南(061)</option>
										<option <%=eq(v_phone1,"경북(054)")%>>慶北(054)</option>
										<option <%=eq(v_phone1,"경남(055)")%>>慶南(055)</option>
										<option <%=eq(v_phone1,"제주(064)")%>>済州(064)</option>
									</select> <input type="text" class="input" name="phone2" placeholder="局番" value="<%=v_phone2%>"> <input
										type="text" class="input" name="phone3" placeholder="番号" value="<%=v_phone3%>">
								</div>
							</td>
							<th>携帯電話</th>
							<td class="span-1">
								<div class="phone-fields">
									<select class="select" name="mobile1">
										<option value="" <%=v_mobile1.isEmpty()?"selected":""%>>選択</option>
										<option <%=eq(v_mobile1,"010")%>>010</option>
										<option <%=eq(v_mobile1,"011")%>>011</option>
										<option <%=eq(v_mobile1,"016")%>>016</option>
										<option <%=eq(v_mobile1,"017")%>>017</option>
										<option <%=eq(v_mobile1,"018")%>>018</option>
										<option <%=eq(v_mobile1,"019")%>>019</option>
									</select> <input type="text" class="input" name="mobile2" placeholder="局番" value="<%=v_mobile2%>"> <input
										type="text" class="input" name="mobile3" placeholder="番号" value="<%=v_mobile3%>">
								</div>
							</td>
						</tr>
						<tr>
							<th>メールアドレス</th>
							<td class="span-1"><input type="email" class="input"
								name="email" placeholder="example@email.com" value="<%=v_email%>"></td>
							<th>SNS</th>
							<td class="span-1"><input type="text" class="input"
								name="sns" placeholder="SNSアカウント" value="<%=v_sns%>"></td>
						</tr>
					</tbody>
				</table>
			</section>

			<div class="source-page-caption">社員情報 1page</div>

			<!-- 給与・社会保険セクション / 급여 & 4대보험 섹션 -->
			<section class="source-section" id="pay-insurance">
				<div class="source-section-title">給与 &amp; 社会保険</div>
				<table class="source-form-table">
					<tbody>
						<tr>
							<th>給与</th>
							<td class="span-3"><label class="check-inline"><input
									type="checkbox" checked> 給与対象</label></td>
						</tr>
						<tr>
							<th>社会保険</th>
							<td class="span-3"><div class="check-list">
									<label><input type="checkbox" checked> 国民年金</label><label><input
										type="checkbox" checked> 健康保険（老人長期療養保険含む）</label><label><input
										type="checkbox" checked> 雇用保険</label>
								</div></td>
						</tr>
						<tr>
							<th>源泉徴収区分</th>
							<td class="span-3"><div class="check-list">
									<label><input type="radio" name="empIncomeType"
										value="근로소득자 갑근세" <%=chk(v_empIncomeType,"근로소득자 갑근세")%>> 給与所得者</label> <label><input
										type="radio" name="empIncomeType" value="사업소득자 갑근세" <%=chk(v_empIncomeType,"사업소득자 갑근세")%>>
										事業所得者</label> <label><input type="radio"
										name="empIncomeType" value="일용직 갑근세" <%=chk(v_empIncomeType,"일용직 갑근세")%>> 日雇労働者</label> <label><input
										type="radio" name="empIncomeType" value="면제" <%=chk(v_empIncomeType,"면제")%>> 免除</label>
								</div></td>
						</tr>
						<tr>
							<th><span class="required-mark">*</span>基本給／月給</th>
							<td class="span-1"><div class="money-control">
									<input type="text" class="input number" name="baseWageAmount"
										value="<%=v_baseWageAmount%>"><span>円</span>
								</div></td>
							<th>国民年金 基準所得月額</th>
							<td class="span-1"><div class="money-control">
									<input type="text" class="input number" name="nationalPensionBaseAmount"
										value="<%=v_nationalPensionBaseAmount%>"><span>円</span>
								</div></td>
						</tr>
						<tr>
							<th>健康保険 報酬月額</th>
							<td class="span-1"><div class="money-control">
									<input type="text" class="input number" name="healthInsuranceBaseAmount"
										value="<%=v_healthInsuranceBaseAmount%>"><span>円</span>
								</div></td>
							<th>雇用保険 報酬月額</th>
							<td class="span-1"><div class="money-control">
									<input type="text" class="input number" name="employmentInsuranceAmount"
										value="<%=v_employmentInsuranceAmount%>"><span>円</span>
								</div></td>
						</tr>
						<tr>
							<th>給与振込銀行</th>
							<td class="span-1"><select class="select" name="bankName">
									<option value="" <%=v_bankName.isEmpty() ? "selected" : ""%>>選択してください</option>
									<option <%=eq(v_bankName,"국민은행")%>>国民銀行</option>
									<option <%=eq(v_bankName,"기업은행")%>>企業銀行</option>
									<option <%=eq(v_bankName,"농협중앙회")%>>農協中央会</option>
									<option <%=eq(v_bankName,"농협은행")%>>農協銀行</option>
									<option <%=eq(v_bankName,"산업은행")%>>産業銀行</option>
									<option <%=eq(v_bankName,"신한은행")%>>新韓銀行</option>
									<option <%=eq(v_bankName,"스탠다드차타드은행")%>>スタンダードチャータード銀行</option>
									<option <%=eq(v_bankName,"우리은행")%>>ウリ銀行</option>
									<option <%=eq(v_bankName,"외환은행")%>>外換銀行</option>
									<option <%=eq(v_bankName,"하나은행")%>>ハナ銀行</option>
									<option <%=eq(v_bankName,"한국씨티은행")%>>韓国シティ銀行</option>
									<option <%=eq(v_bankName,"경남은행")%>>慶南銀行</option>
									<option <%=eq(v_bankName,"광주은행")%>>光州銀行</option>
									<option <%=eq(v_bankName,"지역농협")%>>地域農協</option>
									<option <%=eq(v_bankName,"대구은행")%>>大邱銀行</option>
									<option <%=eq(v_bankName,"부산은행")%>>釜山銀行</option>
									<option <%=eq(v_bankName,"전북은행")%>>全北銀行</option>
									<option <%=eq(v_bankName,"제주은행")%>>済州銀行</option>
									<option <%=eq(v_bankName,"카카오뱅크")%>>カカオバンク</option>
									<option <%=eq(v_bankName,"케이뱅크")%>>Kバンク</option>
									<option <%=eq(v_bankName,"토스뱅크")%>>Tossバンク</option>
									<option <%=eq(v_bankName,"산림조합")%>>山林組合</option>
									<option <%=eq(v_bankName,"상호저축은행")%>>相互貯蓄銀行</option>
									<option <%=eq(v_bankName,"새마을금고")%>>セマウル金庫</option>
									<option <%=eq(v_bankName,"신용협동조합")%>>信用協同組合</option>
									<option <%=eq(v_bankName,"수협중앙회")%>>水協中央会</option>
									<option <%=eq(v_bankName,"우체국")%>>郵便局</option>
									<option <%=eq(v_bankName,"도이치뱅크")%>>ドイツ銀行</option>
									<option <%=eq(v_bankName,"BOA")%>>BOA</option>
									<option <%=eq(v_bankName,"에이비엔암로")%>>ABNアムロ</option>
									<option <%=eq(v_bankName,"HSBC")%>>HSBC</option>
									<option <%=eq(v_bankName,"JP모간")%>>JPモルガン</option>
									<option <%=eq(v_bankName,"BNP파리바")%>>BNPパリバ</option>
									<option <%=eq(v_bankName,"OK저축은행")%>>OK貯蓄銀行</option>
									<option <%=eq(v_bankName,"골든브릿지투자증권")%>>ゴールデンブリッジ投資証券</option>
									<option <%=eq(v_bankName,"교보증권")%>>教保証券</option>
									<option <%=eq(v_bankName,"대신증권")%>>大信証券</option>
									<option <%=eq(v_bankName,"동부증권")%>>東部証券</option>
									<option <%=eq(v_bankName,"리딩투자증권")%>>リーディング投資証券</option>
									<option <%=eq(v_bankName,"메리츠종합금융증권")%>>メリッツ総合金融証券</option>
									<option <%=eq(v_bankName,"미래에셋대우")%>>ミレアセット大宇</option>
									<option <%=eq(v_bankName,"미래에셋증권")%>>ミレアセット証券</option>
									<option <%=eq(v_bankName,"바로투자증권")%>>バロ投資証券</option>
									<option <%=eq(v_bankName,"부국증권")%>>富国証券</option>
									<option <%=eq(v_bankName,"삼성증권")%>>サムスン証券</option>
									<option <%=eq(v_bankName,"신영증권")%>>新英証券</option>
									<option <%=eq(v_bankName,"신한금융투자")%>>新韓金融投資</option>
									<option <%=eq(v_bankName,"유안타증권")%>>ユアンタ証券</option>
									<option <%=eq(v_bankName,"유진투자증권")%>>ユジン投資証券</option>
									<option <%=eq(v_bankName,"유화증권")%>>油化証券</option>
									<option <%=eq(v_bankName,"이베스트투자증권")%>>イーベスト投資証券</option>
									<option <%=eq(v_bankName,"카카오페이증권")%>>カカオペイ証券</option>
									<option <%=eq(v_bankName,"코리아에셋투자증권")%>>コリアアセット投資証券</option>
									<option <%=eq(v_bankName,"키움증권")%>>キウム証券</option>
									<option <%=eq(v_bankName,"토스증권")%>>Toss証券</option>
									<option <%=eq(v_bankName,"하나금융투자")%>>ハナ金融投資</option>
									<option <%=eq(v_bankName,"하이투자증권")%>>ハイ投資証券</option>
									<option <%=eq(v_bankName,"한국투자증권")%>>韓国投資証券</option>
									<option <%=eq(v_bankName,"한양증권")%>>漢陽証券</option>
									<option <%=eq(v_bankName,"한화투자증권")%>>韓火投資証券</option>
									<option <%=eq(v_bankName,"현대증권")%>>現代証券</option>
									<option <%=eq(v_bankName,"흥국증권")%>>興国証券</option>
									<option <%=eq(v_bankName,"BNK투자증권")%>>BNK投資証券</option>
									<option <%=eq(v_bankName,"HMC투자증권")%>>HMC投資証券</option>
									<option <%=eq(v_bankName,"IBK투자증권")%>>IBK投資証券</option>
									<option <%=eq(v_bankName,"KB투자증권")%>>KB投資証券</option>
									<option <%=eq(v_bankName,"KTB투자증권")%>>KTB投資証券</option>
									<option <%=eq(v_bankName,"LIG투자증권")%>>LIG投資証券</option>
									<option <%=eq(v_bankName,"NH투자증권")%>>NH投資証券</option>
									<option <%=eq(v_bankName,"SK증권")%>>SK証券</option>
							</select></td>
							<th>口座番号</th>
							<td class="span-1"><input type="text" class="input" name="bankAccount" placeholder="口座番号を入力" value="<%=v_bankAccount%>"></td>
						</tr>
					</tbody>
				</table>
				<!-- 社会保険詳細テーブル / 4대보험 상세 -->
				<div class="table-toolbar compact">
					<strong>社会保険詳細</strong>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table">
						<thead>
							<tr>
								<th>区分</th>
								<th>記号番号</th>
								<th>取得日</th>
								<th>喪失日</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>国民年金</td>
								<td><input class="input" type="text" name="insuranceNoNP" value="<%=v_insNoNP%>"></td>
								<td><input class="input" type="date" name="acquisitionDateNP" value="<%=v_insAcqNP%>"></td>
								<td><input class="input" type="date" name="lossDateNP" value="<%=v_insLossNP%>"></td>
							</tr>
							<tr>
								<td>健康保険</td>
								<td><input class="input" type="text" name="insuranceNoHI" value="<%=v_insNoHI%>"></td>
								<td><input class="input" type="date" name="acquisitionDateHI" value="<%=v_insAcqHI%>"></td>
								<td><input class="input" type="date" name="lossDateHI" value="<%=v_insLossHI%>"></td>
							</tr>
							<tr>
								<td>雇用保険</td>
								<td><input class="input" type="text" name="insuranceNoEI" value="<%=v_insNoEI%>"></td>
								<td><input class="input" type="date" name="acquisitionDateEI" value="<%=v_insAcqEI%>"></td>
								<td><input class="input" type="date" name="lossDateEI" value="<%=v_insLossEI%>"></td>
							</tr>
							<tr>
								<td>労災保険</td>
								<td><input class="input" type="text" name="insuranceNoII" value="<%=v_insNoII%>"></td>
								<td><input class="input" type="date" name="acquisitionDateII" value="<%=v_insAcqII%>"></td>
								<td><input class="input" type="date" name="lossDateII" value="<%=v_insLossII%>"></td>
							</tr>
						</tbody>
					</table>
				</div>
			</section>

			<!-- 扶養家族セクション / 부양가족 섹션 -->
			<section class="source-section" id="family">
				<div class="source-section-title">扶養家族</div>
				<div class="table-toolbar compact">
					<strong>扶養家族</strong>
					<div class="actions">
						<!-- 追加・選択削除ボタン / 추가/선택삭제 버튼 -->
						<button type="submit" name="formAction" value="addFamilyRow" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns1.do#family">追加</button>
						<button type="submit" name="formAction" value="deleteFamilyRows" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns1.do#family">選択削除</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table wide" id="familyTable">
						<thead>
							<tr>
								<th>選択</th>
								<th>続柄</th>
								<th>氏名</th>
								<th>国籍区分</th>
								<th>マイナンバー</th>
								<th>障害有無</th>
								<th>控除対象</th>
								<th>健康保険登録</th>
								<th>同居有無</th>
								<th>多子女</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 1; i <= familyRowCount; i++) {
							    String relVal = getAttrOrParam(request, "familyRelation" + i);
							    String nameVal = getAttrOrParam(request, "familyName" + i);
							    String rrnVal = getAttrOrParam(request, "familyRrn" + i);
							    if (relVal == null) relVal = "";
							    if (nameVal == null) nameVal = "";
							    if (rrnVal == null) rrnVal = "";
							%>
							<tr>
								<td><input type="checkbox" name="familyDel<%=i%>"></td>
								<td><select class="select" name="familyRelation<%=i%>">
										<option value="" <%=relVal.isEmpty() ? "selected" : ""%>>選択</option>
										<option value="배우자" <%="배우자".equals(relVal) ? "selected" : ""%>>配偶者</option>
										<option value="아들" <%="아들".equals(relVal) ? "selected" : ""%>>息子</option>
										<option value="딸" <%="딸".equals(relVal) ? "selected" : ""%>>娘</option>
										<option value="부" <%="부".equals(relVal) ? "selected" : ""%>>父</option>
										<option value="모" <%="모".equals(relVal) ? "selected" : ""%>>母</option>
										<option value="형제" <%="형제".equals(relVal) ? "selected" : ""%>>兄弟</option>
										<option value="자매" <%="자매".equals(relVal) ? "selected" : ""%>>姉妹</option>
										<option value="장인" <%="장인".equals(relVal) ? "selected" : ""%>>義父（妻方）</option>
										<option value="장모" <%="장모".equals(relVal) ? "selected" : ""%>>義母（妻方）</option>
										<option value="시아버지" <%="시아버지".equals(relVal) ? "selected" : ""%>>義父（夫方）</option>
										<option value="시어머니" <%="시어머니".equals(relVal) ? "selected" : ""%>>義母（夫方）</option>
										<option value="조부" <%="조부".equals(relVal) ? "selected" : ""%>>祖父</option>
										<option value="조모" <%="조모".equals(relVal) ? "selected" : ""%>>祖母</option>
										<option value="손자" <%="손자".equals(relVal) ? "selected" : ""%>>孫息子</option>
										<option value="손녀" <%="손녀".equals(relVal) ? "selected" : ""%>>孫娘</option>
								</select></td>
								<td><input class="input" type="text" name="familyName<%=i%>" placeholder="氏名" value="<%=nameVal%>"></td>
								<td><select class="select" name="familyDomForYn<%=i%>">
										<option value="" <%=getAttrOrParam(request, "familyDomForYn"+i).isEmpty() ? "selected" : ""%>>選択</option>
										<option value="Y" <%="Y".equals(getAttrOrParam(request, "familyDomForYn"+i)) ? "selected" : ""%>>日本国籍</option>
										<option value="N" <%="N".equals(getAttrOrParam(request, "familyDomForYn"+i)) ? "selected" : ""%>>外国籍</option>
								</select></td>
								<td><input class="input" type="text" name="familyRrn<%=i%>" value="<%=rrnVal%>"></td>
								<td><input type="checkbox" name="familyDisabled<%=i%>" <%="on".equals(getAttrOrParam(request, "familyDisabled"+i)) ? "checked" : ""%>></td>
								<td><input type="checkbox" name="familyDeduction<%=i%>" <%="on".equals(getAttrOrParam(request, "familyDeduction"+i)) ? "checked" : ""%>></td>
								<td><input type="checkbox" name="familyHealthIns<%=i%>" <%="on".equals(getAttrOrParam(request, "familyHealthIns"+i)) ? "checked" : ""%>></td>
								<td><input type="checkbox" name="familyCohab<%=i%>" <%="on".equals(getAttrOrParam(request, "familyCohab"+i)) ? "checked" : ""%>></td>
								<td><input type="checkbox" name="familyMultiChild<%=i%>" <%="on".equals(getAttrOrParam(request, "familyMultiChild"+i)) ? "checked" : ""%>></td>
							</tr>
							<% } %>
						</tbody>
					</table>
				</div>
			</section>

			<!-- 学歴セクション / 학력 섹션 -->
			<section class="source-section" id="education">
				<div class="source-section-title">学歴</div>
				<div class="table-toolbar compact">
					<strong>学歴</strong>
					<div class="actions">
						<button type="submit" name="formAction" value="addEducationRow" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns1.do#education">追加</button>
						<button type="submit" name="formAction" value="deleteEducationRows" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns1.do#education">選択削除</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table" id="educationTable">
						<thead>
							<tr>
								<th>選択</th>
								<th>学校名</th>
								<th>専攻</th>
								<th>入学日</th>
								<th>卒業日</th>
								<th>区分</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 1; i <= educationRowCount; i++) {
							    String eduSchool = getAttrOrParam(request, "educationSchool" + i);
							    String eduMajor = getAttrOrParam(request, "educationMajor" + i);
							    String eduStart = getAttrOrParam(request, "educationStart" + i);
							    String eduEnd = getAttrOrParam(request, "educationEnd" + i);
							    String eduStatus = getAttrOrParam(request, "educationStatus" + i);
							%>
							<tr>
								<td><input type="checkbox" name="educationDel<%=i%>"></td>
								<td><input class="input" type="text" name="educationSchool<%=i%>" placeholder="学校名" value="<%=eduSchool%>"></td>
								<td><input class="input" type="text" name="educationMajor<%=i%>" placeholder="専攻" value="<%=eduMajor%>"></td>
								<td><input class="input" type="date" name="educationStart<%=i%>" value="<%=eduStart%>"></td>
								<td><input class="input" type="date" name="educationEnd<%=i%>" value="<%=eduEnd%>"></td>
								<td><select class="select" name="educationStatus<%=i%>">
										<option value="" <%=eduStatus.isEmpty() ? "selected" : ""%>>選択</option>
										<option value="초등학교" <%=eq(eduStatus,"초등학교")%>>小学校</option>
										<option value="중학교" <%=eq(eduStatus,"중학교")%>>中学校</option>
										<option value="고등학교" <%=eq(eduStatus,"고등학교")%>>高校</option>
										<option value="대학교" <%=eq(eduStatus,"대학교")%>>大学</option>
										<option value="석사" <%=eq(eduStatus,"석사")%>>修士</option>
										<option value="박사" <%=eq(eduStatus,"박사")%>>博士</option>
								</select></td>
							</tr>
							<% } %>
						</tbody>
					</table>
				</div>
			</section>

			<!-- 経歴セクション / 경력 섹션 -->
			<section class="source-section" id="career">
				<div class="source-section-title">経歴</div>
				<div class="table-toolbar compact">
					<strong>経歴</strong>
					<div class="actions">
						<button type="submit" name="formAction" value="addCareerRow" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns1.do#career">追加</button>
						<button type="submit" name="formAction" value="deleteCareerRows" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns1.do#career">選択削除</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table" id="careerTable">
						<thead>
							<tr>
								<th>選択</th>
								<th>会社名</th>
								<th>部署</th>
								<th>役職</th>
								<th>入社日</th>
								<th>退社日</th>
								<th>担当業務</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 1; i <= careerRowCount; i++) {
							    String carCompany = getAttrOrParam(request, "careerCompany" + i);
							    String carDept = getAttrOrParam(request, "careerDept" + i);
							    String carPosition = getAttrOrParam(request, "careerPosition" + i);
							    String carStart = getAttrOrParam(request, "careerStart" + i);
							    String carEnd = getAttrOrParam(request, "careerEnd" + i);
							    String carDuty = nz(request.getParameter("careerDuty" + i));
							%>
							<tr>
								<td><input type="checkbox" name="careerDel<%=i%>"></td>
								<td><input class="input" type="text" name="careerCompany<%=i%>" placeholder="会社名" value="<%=carCompany%>"></td>
								<td><input class="input" type="text" name="careerDept<%=i%>" placeholder="部署名" value="<%=carDept%>"></td>
								<td><input class="input" type="text" name="careerPosition<%=i%>" placeholder="役職" value="<%=carPosition%>"></td>
								<td><input class="input" type="date" name="careerStart<%=i%>" value="<%=carStart%>"></td>
								<td><input class="input" type="date" name="careerEnd<%=i%>" value="<%=carEnd%>"></td>
								<td><input class="input" type="text" name="careerDuty<%=i%>" placeholder="担当業務" value="<%=carDuty%>"></td>
							</tr>
							<% } %>
						</tbody>
					</table>
				</div>
			</section>

			<!-- 兵役セクション / 병역 섹션 -->
			<section class="source-section" id="military">
				<div class="source-section-title">兵役</div>
				<table class="source-form-table">
					<tbody>
						<tr>
							<th>兵役区分</th>
							<td class="span-1"><select class="select" name="militaryStatus">
									<option value="" <%=v_militaryStatus.isEmpty() ? "selected" : ""%>>選択してください</option>
									<option value="군필" <%=eq(v_militaryStatus,"군필")%>>兵役完了</option>
									<option value="미필" <%=eq(v_militaryStatus,"미필")%>>未服役</option>
									<option value="면제" <%=eq(v_militaryStatus,"면제")%>>免除</option>
									<option value="해당없음" <%=eq(v_militaryStatus,"해당없음")%>>該当なし</option>
							</select></td>
							<th>軍種</th>
							<td class="span-1"><select class="select" name="militaryBranchCode">
									<option value="" <%=v_militaryBranchCode.isEmpty() ? "selected" : ""%>>選択してください</option>
									<option value="육군" <%=eq(v_militaryBranchCode,"육군")%>>陸軍</option>
									<option value="해군" <%=eq(v_militaryBranchCode,"해군")%>>海軍</option>
									<option value="공군" <%=eq(v_militaryBranchCode,"공군")%>>空軍</option>
									<option value="해병대" <%=eq(v_militaryBranchCode,"해병대")%>>海兵隊</option>
									<option value="기타" <%=eq(v_militaryBranchCode,"기타")%>>その他</option>
							</select></td>
						</tr>
						<tr>
							<th>服役開始日</th>
							<td class="span-1"><input type="date" class="input" name="militaryStartDate" value="<%=v_militaryStartDate%>"></td>
							<th>服役終了日</th>
							<td class="span-1"><input type="date" class="input" name="militaryEndDate" value="<%=v_militaryEndDate%>"></td>
						</tr>
						<tr>
							<th>階級</th>
							<td class="span-1"><input type="text" class="input" name="militaryGrade" placeholder="階級を入力" value="<%=v_militaryGrade%>"></td>
							<th>兵科</th>
							<td class="span-1"><input type="text" class="input" name="militaryBranch" placeholder="兵科を入力" value="<%=v_militaryBranch%>"></td>
						</tr>
						<tr>
							<th>特技</th>
							<td class="span-1"><input type="text" class="input" name="militarySpecialty" placeholder="特技を入力" value="<%=v_militarySpecialty%>"></td>
							<th>未服役・免除事由</th>
							<td class="span-1"><input type="text" class="input" name="militaryExemptReason" placeholder="事由を入力" value="<%=v_militaryExemptReason%>"></td>
						</tr>
					</tbody>
				</table>
			</section>

			<!-- 保存・キャンセルボタンエリア / 저장/취소 버튼 영역 -->
			<div class="source-bottom-actions">
				<button type="submit" class="btn btn-primary">保存</button>
				<button type="reset" class="btn">キャンセル</button>
				<button type="button" class="btn"
					onclick="location.href='<%=request.getContextPath()%>/Person/employeeMnt.do'">リスト</button>
				<button type="submit" name="formAction" value="saveAndStay" class="btn btn-blue">新規社員登録</button>
			</div>

		</form>
		<script type="text/javascript">
			// フォームバリデーション - 必須項目チェック / 폼 유효성 검사 - 필수 항목 확인
			function validateForm() {
				var form = document.forms[0];

				// 1. 雇用形態の選択確認 / 고용형태 선택 확인
				if (form.employmentType.value === "") {
					alert("必須入力項目です：雇用形態を選択してください。");
					form.employmentType.focus();
					return false;
				}

				// 2. 氏名（漢字）の入力確認 / 성명(한글) 입력 확인
				if (form.employeeName.value.trim() === "") {
					alert("必須入力項目です：氏名を入力してください。");
					form.employeeName.focus();
					return false;
				}

				// 3. 入社日の選択確認 / 입사일 선택 확인
				if (form.hireDate.value === "") {
					alert("必須入力項目です：入社日を選択してください。");
					form.hireDate.focus();
					return false;
				}

				// 4. 部署の選択確認 / 부서 선택 확인
				if (form.department.value === "") {
					alert("必須入力項目です：部署を選択してください。");
					form.department.focus();
					return false;
				}

				// 全チェック通過 - サーバーへ送信 / 모든 검사 통과 - 서버로 전송
				return true;
			}
		</script>
	</div>
</div>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>

<!-- 写真選択ダイアログ / 사원사진 선택 모달 -->
<div class="dialog-backdrop" id="photoSelectDialog">
	<div class="dialog" style="width: min(520px, 100%);">
		<div class="dialog-header">
			<strong>社員写真選択</strong>
			<button type="button" class="btn btn-icon" onclick="document.getElementById('photoSelectDialog').classList.remove('open');">×</button>
		</div>
		<div class="dialog-body">
			<div style="display:flex; gap:16px; flex-wrap:wrap; justify-content:center; padding:8px 0;">
				<%
				// 写真候補を表示（1〜5番） / 사진 후보 표시
				for (int pi = 1; pi <= 5; pi++) {
					String pPath = "/assets/img/profile/" + pi + ".jpg";
					String currentSelected = nz(request.getParameter("photoPath"));
				%>
				<label style="display:flex;flex-direction:column;align-items:center;gap:6px;cursor:pointer;">
					<input type="radio" name="photoRadio" value="<%=pPath%>"
						<%=currentSelected.equals(pPath) ? "checked" : ""%>
						onchange="previewPhoto('<%=pPath%>')"
						style="width:18px;height:18px;accent-color:var(--accent);">
					<img src="<%=request.getContextPath()%><%=pPath%>"
						style="width:100px;height:130px;object-fit:cover;border-radius:6px;border:2px solid var(--line);">
				</label>
				<%
				}
				%>
			</div>
		</div>
		<div class="dialog-footer">
			<!-- 選択確定・閉じるボタン / 선택 확정/닫기 버튼 -->
			<button type="button" class="btn btn-primary" onclick="confirmPhoto();">選択</button>
			<button type="button" class="btn" onclick="document.getElementById('photoSelectDialog').classList.remove('open');">閉じる</button>
		</div>
	</div>
</div>

<script>
// 写真選択のプレビュー / 사진 선택 미리보기
function previewPhoto(path) {
	// ラジオ選択のみ - 確定は選択ボタンで行う / 라디오 선택만 - 확정은 [選択] 버튼
}

// 写真選択を確定する / 사진 선택 확정
function confirmPhoto() {
	var selected = document.querySelector('input[name="photoRadio"]:checked');
	if (!selected) {
		alert('写真を選択してください。');
		return;
	}
	var path = selected.value;
	document.getElementById('photoPathInput').value = path;
	var box = document.getElementById('photoBox');
	box.innerHTML = '<img src="<%=request.getContextPath()%>' + path + '" style="width:100%;height:100%;object-fit:cover;">';
	document.getElementById('photoSelectDialog').classList.remove('open');
}

// 写真をクリアする / 사진 초기화
function clearPhoto() {
	document.getElementById('photoPathInput').value = '';
	document.getElementById('photoBox').innerHTML = '<span>社員写真</span><small>クリックして選択</small>';
	var radios = document.querySelectorAll('input[name="photoRadio"]');
	radios.forEach(function(r) { r.checked = false; });
}

// ダイアログ外クリックで閉じる / 모달 외부 클릭 시 닫기
document.getElementById('photoSelectDialog').addEventListener('click', function(e) {
	if (e.target === this) this.classList.remove('open');
});
</script>
