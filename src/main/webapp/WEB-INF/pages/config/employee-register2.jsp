<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="config.employee.model.Employee"%>
<%!
	// nullの場合は空文字列に変換 / null이면 빈 문자열로
	private String nz(String s) {
		return s == null ? "" : s;
	}

	// requestのattribute → getParameterの順で値を取得 / request attribute → getParameter 순으로 값 읽기
	private String getAttrOrParam(javax.servlet.http.HttpServletRequest req, String key) {
		Object attr = req.getAttribute(key);
		if (attr != null) return attr.toString();
		return nz(req.getParameter(key));
	}

	// selectのoptionにselected属性を付与 / select의 option에 selected 속성
	private String eq(String submitted, String optionValue) {
		return optionValue.equals(submitted) ? "selected" : "";
	}

	// "-"で連結された電話番号を指定インデックスのパーツに分割 / "-"로 합쳐진 전화번호를 분리
	private String part(String joined, int index) {
		if (joined == null) return "";
		String[] parts = joined.split("-", -1);
		return index < parts.length ? parts[index] : "";
	}
%>
<%
request.setAttribute("pageTitle", "社員登録2");
request.setAttribute("pageSection", "基本環境");
request.setAttribute("pageDescription", "社員の資格・教育・賞罰・異動・退職関連の追加情報を登録します。");
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
			<!-- 社員写真表示エリア / 사원사진 표시 영역 -->
			<%
			config.employee.model.Employee photoEmpInfo = (config.employee.model.Employee) request.getAttribute("empInfo");
			String empPhotoPath = (photoEmpInfo != null && photoEmpInfo.getPhotoPath() != null) ? photoEmpInfo.getPhotoPath() : "";
			%>
			<div class="employee-photo-placeholder">
				<%
				if (!empPhotoPath.isEmpty()) {
				%>
				<img src="<%=request.getContextPath()%><%=empPhotoPath%>" style="width:100%;height:100%;object-fit:cover;">
				<%
				} else {
				%>
				<span>社員写真</span><small>登録してください</small>
				<%
				}
				%>
			</div>
			<!-- 社員基本情報サマリー / 사원 기본정보 요약 -->
			<dl class="employee-mini-info">
				<dt>社員番号</dt>
				<dd>${empInfo.employeeNo}</dd>
				<dt>氏名</dt>
				<dd>${empInfo.employeeName}</dd>
				<dt>部署</dt>
				<dd>${empInfo.department}</dd>
				<dt>役職</dt>
				<dd>${empInfo.position}</dd>
				<dt>入社日</dt>
				<dd>${empInfo.hireDate}</dd>
			</dl>
			<div class="mini-actions">
				<button type="button" class="btn btn-sm">登録</button>
				<button type="button" class="btn btn-sm">削除</button>
			</div>
		</div>
		<div class="employee-page-menu">
			<div class="employee-page-label">社員情報 1page</div>
			<div class="employee-menu-grid">
				<!-- 1ページ項目は非活性（2ページから操作不可） / 1페이지 항목은 비활성 -->
				<span class="employee-menu-btn is-disabled"
					title="この項目は社員登録1ページで入力します">給与<br>&amp;社会保険
				</span> <span class="employee-menu-btn is-disabled"
					title="この項目は社員登録1ページで入力します">扶養<br>家族
				</span> <span class="employee-menu-btn is-disabled"
					title="この項目は社員登録1ページで入力します">学歴</span> <span
					class="employee-menu-btn is-disabled"
					title="この項目は社員登録1ページで入力します">経歴</span> <span
					class="employee-menu-btn is-disabled"
					title="この項目は社員登録1ページで入力します">兵役</span>
			</div>
			<div class="employee-page-label second">社員情報 2page</div>
			<div class="employee-menu-grid">
				<!-- 2ページ内セクションへスクロール / 2페이지 내 섹션 스크롤 이동 -->
				<a class="employee-menu-btn" href="#license">資格<br>免許
				</a> <a class="employee-menu-btn" href="#training">教育<br>訓練
				</a> <a class="employee-menu-btn" href="#reward">賞罰</a> <a
					class="employee-menu-btn" href="#appointment">異動</a> <a
					class="employee-menu-btn" href="#retirement">退職</a>
			</div>
		</div>
	</aside>

	<div class="employee-register-main">
		<%
		// 各テーブルの表示行数をサーバーで管理 / 각 테이블에 몇 줄을 그릴지 서버가 기억
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
		<!-- 2ページデータをemployeeIns2.doへ送信するフォーム / 2페이지 데이터를 핸들러로 전송하는 폼 -->
		<form action="<%=request.getContextPath()%>/Config/employeeIns2.do"
			method="post">
			<input type="hidden" name="licenseRowCount"
				value="<%=licenseRowCount%>"> <input type="hidden"
				name="trainingRowCount" value="<%=trainingRowCount%>"> <input
				type="hidden" name="rewardRowCount" value="<%=rewardRowCount%>">
			<input type="hidden" name="appointmentRowCount"
				value="<%=appointmentRowCount%>">

			<!-- 基本情報セクション（1ページから引き継いだ情報） / 기본정보 섹션 (1페이지에서 이어받은 정보) -->
			<section class="source-section">
				<div class="source-section-title">基本情報</div>
				<table class="source-form-table">
					<tbody>
						<tr>
							<th>社員番号</th>
							<td class="span-1"><input type="text" class="input" readonly
								name="employeeNo" value="${empInfo.employeeNo}"></td>
							<th>雇用形態</th>
							<td class="span-1"><select class="select"
								name="employmentType">
									<option value=""
										${empty empInfo.employmentType ? 'selected' : ''}>選択してください</option>
									<option value="정규직"
										${empInfo.employmentType == '정규직' ? 'selected' : ''}>正規職</option>
									<option value="계약직"
										${empInfo.employmentType == '계약직' ? 'selected' : ''}>契約職</option>
									<option value="임시직"
										${empInfo.employmentType == '임시직' ? 'selected' : ''}>臨時職</option>
									<option value="파견직"
										${empInfo.employmentType == '파견직' ? 'selected' : ''}>派遣職</option>
									<option value="위촉직"
										${empInfo.employmentType == '위촉직' ? 'selected' : ''}>嘱託職</option>
									<option value="일용직"
										${empInfo.employmentType == '일용직' ? 'selected' : ''}>日雇職</option>
							</select></td>
						</tr>
						<tr>
							<th>氏名（漢字）</th>
							<td class="span-1"><input type="text" class="input"
								name="employeeName" value="${empInfo.employeeName}"></td>
							<th>氏名（英語）</th>
							<td class="span-1"><input type="text" class="input"
								name="employeeNameEn" value="${empInfo.employeeNameEn}"></td>
						</tr>
						<tr>
							<th>入社日</th>
							<td class="span-1"><input type="date" class="input"
								name="hireDate" value="${empInfo.hireDate}"></td>
							<th>退社日</th>
							<td class="span-1"><input type="date" class="input"
								name="resignDate" value="${empInfo.resignDate}"></td>
						</tr>
						<tr>
							<th>部署</th>
							<td class="span-1"><div class="inline-control">
									<select class="select" name="department">
										<option value="" ${empty empInfo.department ? 'selected' : ''}>選択してください</option>
										<option value="사장실"
											${empInfo.department == '사장실' ? 'selected' : ''}>社長室</option>
										<option value="개발팀"
											${empInfo.department == '개발팀' ? 'selected' : ''}>開発チーム</option>
										<option value="업무지원팀"
											${empInfo.department == '업무지원팀' ? 'selected' : ''}>業務支援チーム</option>
										<option value="디자인팀"
											${empInfo.department == '디자인팀' ? 'selected' : ''}>デザインチーム</option>
										<option value="관리팀"
											${empInfo.department == '관리팀' ? 'selected' : ''}>管理チーム</option>
										<option value="기획전략팀"
											${empInfo.department == '기획전략팀' ? 'selected' : ''}>企画戦略チーム</option>
										<option value="콘텐츠팀"
											${empInfo.department == '콘텐츠팀' ? 'selected' : ''}>コンテンツチーム</option>
										<option value="인사팀"
											${empInfo.department == '인사팀' ? 'selected' : ''}>人事チーム</option>
										<option value="현장운영팀"
											${empInfo.department == '현장운영팀' ? 'selected' : ''}>現場運営チーム</option>
										<option value="재무팀"
											${empInfo.department == '재무팀' ? 'selected' : ''}>財務チーム</option>
									</select>
								</div></td>
							<th>役職</th>
							<td class="span-1"><div class="inline-control">
									<select class="select" name="position">
										<option value="" ${empty empInfo.position ? 'selected' : ''}>選択してください</option>
										<option value="사장"
											${empInfo.position == '사장' ? 'selected' : ''}>社長</option>
										<option value="이사"
											${empInfo.position == '이사' ? 'selected' : ''}>取締役</option>
										<option value="부장"
											${empInfo.position == '부장' ? 'selected' : ''}>部長</option>
										<option value="차장"
											${empInfo.position == '차장' ? 'selected' : ''}>次長</option>
										<option value="과장"
											${empInfo.position == '과장' ? 'selected' : ''}>課長</option>
										<option value="대리"
											${empInfo.position == '대리' ? 'selected' : ''}>代理</option>
										<option value="주임"
											${empInfo.position == '주임' ? 'selected' : ''}>主任</option>
										<option value="실장"
											${empInfo.position == '실장' ? 'selected' : ''}>室長</option>
										<option value="사원"
											${empInfo.position == '사원' ? 'selected' : ''}>社員</option>
										<option value="전문위원"
											${empInfo.position == '전문위원' ? 'selected' : ''}>専門委員</option>
										<option value="일용근로자"
											${empInfo.position == '일용근로자' ? 'selected' : ''}>日雇労働者</option>
									</select>
								</div></td>
						</tr>
						<tr>
							<th>国籍区分</th>
							<td class="span-1"><select class="select" name="domForYn">
									<option value="" ${empty empInfo.domForYn ? 'selected' : ''}>選択してください</option>
									<option value="Y" ${empInfo.domForYn == 'Y' ? 'selected' : ''}>日本国籍</option>
									<option value="N" ${empInfo.domForYn == 'N' ? 'selected' : ''}>外国籍</option>
							</select></td>
							<th>マイナンバー</th>
							<td class="span-1"><div class="rrn-fields">
									<input type="text" class="input" name="residentRegNoFront"
										placeholder="前半"
										value="<%=part(empInfo != null ? empInfo.getResidentRegNo() : null, 0)%>"><span>-</span><input
										type="text" class="input" name="residentRegNoBack"
										placeholder="後半"
										value="<%=part(empInfo != null ? empInfo.getResidentRegNo() : null, 1)%>">
								</div></td>
						</tr>
						<tr>
							<th>電話番号</th>
							<td class="span-1"><div class="phone-fields">
									<select class="select" name="phone1">
										<option value=""
											<%=part(empInfo != null ? empInfo.getPhone() : null, 0).isEmpty() ? "selected" : ""%>>選択</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "대표(없음)")%>>代表(なし)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "휴대폰(010)")%>>携帯(010)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "인터넷(050)")%>>IP電話(050)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "인터넷(0507)")%>>IP電話(0507)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "인터넷(070)")%>>IP電話(070)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "인터넷(0303)")%>>IP電話(0303)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "인터넷(0504)")%>>IP電話(0504)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "서울(02)")%>>ソウル(02)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "부산(051)")%>>釜山(051)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "대구(053)")%>>大邱(053)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "인천(032)")%>>仁川(032)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "광주(062)")%>>光州(062)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "대전(042)")%>>大田(042)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "울산(052)")%>>蔚山(052)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "세종(044)")%>>世宗(044)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "경기(031)")%>>京畿(031)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "강원(033)")%>>江原(033)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "충북(043)")%>>忠北(043)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "충남(041)")%>>忠南(041)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "전북(063)")%>>全北(063)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "전남(061)")%>>全南(061)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "경북(054)")%>>慶北(054)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "경남(055)")%>>慶南(055)</option>
										<option <%=eq(part(empInfo != null ? empInfo.getPhone() : null, 0), "제주(064)")%>>済州(064)</option>
									</select><input type="text" class="input" name="phone2" placeholder="局番"
										value="<%=part(empInfo != null ? empInfo.getPhone() : null, 1)%>"><input
										type="text" class="input" name="phone3" placeholder="番号"
										value="<%=part(empInfo != null ? empInfo.getPhone() : null, 2)%>">
								</div></td>
							<th>携帯電話</th>
							<td class="span-1"><div class="phone-fields">
									<select class="select" name="mobile1">
										<option value=""
											<%=part(empInfo != null ? empInfo.getMobile() : null, 0).isEmpty() ? "selected" : ""%>>選択</option>
										<option <%=eq(part(empInfo != null ? empInfo.getMobile() : null, 0), "010")%>>010</option>
										<option <%=eq(part(empInfo != null ? empInfo.getMobile() : null, 0), "011")%>>011</option>
										<option <%=eq(part(empInfo != null ? empInfo.getMobile() : null, 0), "016")%>>016</option>
										<option <%=eq(part(empInfo != null ? empInfo.getMobile() : null, 0), "017")%>>017</option>
										<option <%=eq(part(empInfo != null ? empInfo.getMobile() : null, 0), "018")%>>018</option>
										<option <%=eq(part(empInfo != null ? empInfo.getMobile() : null, 0), "019")%>>019</option>
									</select><input type="text" class="input" name="mobile2" placeholder="局番"
										value="<%=part(empInfo != null ? empInfo.getMobile() : null, 1)%>"><input
										type="text" class="input" name="mobile3" placeholder="番号"
										value="<%=part(empInfo != null ? empInfo.getMobile() : null, 2)%>">
								</div></td>
						</tr>
						<tr>
							<th>メールアドレス</th>
							<td class="span-1"><input type="email" class="input"
								name="email" value="${empInfo.email}"></td>
							<th>SNS</th>
							<td class="span-1"><input type="text" class="input"
								name="sns" value="${empInfo.sns}"></td>
						</tr>
					</tbody>
				</table>
			</section>

			<div class="source-page-caption">社員情報 2page</div>

			<!-- 資格・免許セクション / 자격면허 섹션 -->
			<section class="source-section" id="license">
				<div class="source-section-title">資格・免許</div>
				<div class="table-toolbar compact">
					<div></div>
					<div class="actions">
						<!-- 追加・選択削除ボタン / 추가/선택삭제 버튼 -->
						<button type="submit" name="formAction" value="addLicenseRow"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#license">追加</button>
						<button type="submit" name="formAction" value="deleteLicenseRows"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#license">選択削除</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table" id="licenseTable">
						<thead>
							<tr>
								<th>選択</th>
								<th>資格・免許名</th>
								<th>取得日</th>
								<th>発行機関</th>
								<th>等級</th>
								<th>備考</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 1; i <= licenseRowCount; i++) {
								String licName = getAttrOrParam(request, "licenseName" + i);
								String licDate = getAttrOrParam(request, "licenseDate" + i);
								String licOrg = getAttrOrParam(request, "licenseOrg" + i);
								String licGrade = getAttrOrParam(request, "licenseGrade" + i);
								String licMemo = getAttrOrParam(request, "licenseMemo" + i);
							%>
							<tr>
								<td><input type="checkbox" name="licenseDel<%=i%>"></td>
								<td><input class="input" type="text"
									name="licenseName<%=i%>" placeholder="資格・免許名" value="<%=licName%>"></td>
								<td><input class="input" type="date"
									name="licenseDate<%=i%>" value="<%=licDate%>"></td>
								<td><input class="input" type="text"
									name="licenseOrg<%=i%>" placeholder="発行機関" value="<%=licOrg%>"></td>
								<td><input class="input" type="text"
									name="licenseGrade<%=i%>" placeholder="等級" value="<%=licGrade%>"></td>
								<td><input class="input" type="text"
									name="licenseMemo<%=i%>" placeholder="備考" value="<%=licMemo%>"></td>
							</tr>
							<%
							}
							%>
						</tbody>
					</table>
				</div>
			</section>

			<!-- 教育・訓練セクション / 교육훈련 섹션 -->
			<section class="source-section" id="training">
				<div class="source-section-title">教育 &amp; 訓練</div>
				<div class="table-toolbar compact">
					<div></div>
					<div class="actions">
						<button type="submit" name="formAction" value="addTrainingRow"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#training">追加</button>
						<button type="submit" name="formAction" value="deleteTrainingRows"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#training">選択削除</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table" id="trainingTable">
						<thead>
							<tr>
								<th>選択</th>
								<th>教育区分</th>
								<th>教育名</th>
								<th>教育機関</th>
								<th>開始日</th>
								<th>終了日</th>
								<th>教育費</th>
								<th>還付額</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 1; i <= trainingRowCount; i++) {
								String trType = getAttrOrParam(request, "trainingType" + i);
								String trName = getAttrOrParam(request, "trainingName" + i);
								String trOrg = getAttrOrParam(request, "trainingOrg" + i);
								String trStart = getAttrOrParam(request, "trainingStart" + i);
								String trEnd = getAttrOrParam(request, "trainingEnd" + i);
								String trCost = getAttrOrParam(request, "trainingCost" + i);
								String trRefund = getAttrOrParam(request, "trainingRefund" + i);
							%>
							<tr>
								<td><input type="checkbox" name="trainingDel<%=i%>"></td>
								<td><select class="select" name="trainingType<%=i%>">
										<option value="" <%=trType.isEmpty() ? "selected" : ""%>>選択</option>
										<option value="사내직무" <%=eq(trType, "사내직무")%>>社内職務</option>
										<option value="사외직무" <%=eq(trType, "사외직무")%>>社外職務</option>
										<option value="계층교육" <%=eq(trType, "계층교육")%>>階層教育</option>
										<option value="어학교육" <%=eq(trType, "어학교육")%>>語学教育</option>
										<option value="기타" <%=eq(trType, "기타")%>>その他</option>
								</select></td>
								<td><input class="input" type="text"
									name="trainingName<%=i%>" placeholder="教育名" value="<%=trName%>"></td>
								<td><input class="input" type="text"
									name="trainingOrg<%=i%>" placeholder="教育機関" value="<%=trOrg%>"></td>
								<td><input class="input" type="date"
									name="trainingStart<%=i%>" value="<%=trStart%>"></td>
								<td><input class="input" type="date"
									name="trainingEnd<%=i%>" value="<%=trEnd%>"></td>
								<td><input class="input number" type="text"
									name="trainingCost<%=i%>" placeholder="教育費" value="<%=trCost%>"></td>
								<td><input class="input number" type="text"
									name="trainingRefund<%=i%>" placeholder="還付額" value="<%=trRefund%>"></td>
							</tr>
							<%
							}
							%>
						</tbody>
					</table>
				</div>
			</section>

			<!-- 賞罰セクション / 상벌 섹션 -->
			<section class="source-section" id="reward">
				<div class="source-section-title">賞罰</div>
				<div class="table-toolbar compact">
					<div></div>
					<div class="actions">
						<button type="submit" name="formAction" value="addRewardRow"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#reward">追加</button>
						<button type="submit" name="formAction" value="deleteRewardRows"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#reward">選択削除</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table" id="rewardTable">
						<thead>
							<tr>
								<th>選択</th>
								<th>区分</th>
								<th>賞罰日付</th>
								<th>賞罰名</th>
								<th>賞罰内容</th>
								<th>備考</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 1; i <= rewardRowCount; i++) {
								String rwType = getAttrOrParam(request, "rewardType" + i);
								String rwDate = getAttrOrParam(request, "rewardDate" + i);
								String rwName = getAttrOrParam(request, "rewardName" + i);
								String rwContent = getAttrOrParam(request, "rewardContent" + i);
								String rwMemo = getAttrOrParam(request, "rewardMemo" + i);
							%>
							<tr>
								<td><input type="checkbox" name="rewardDel<%=i%>"></td>
								<td><select class="select" name="rewardType<%=i%>">
										<option value="" <%=rwType.isEmpty() ? "selected" : ""%>>選択</option>
										<option value="포상" <%=eq(rwType, "포상")%>>褒賞</option>
										<option value="표창" <%=eq(rwType, "표창")%>>表彰</option>
										<option value="시상" <%=eq(rwType, "시상")%>>授賞</option>
										<option value="면직" <%=eq(rwType, "면직")%>>免職</option>
										<option value="정직" <%=eq(rwType, "정직")%>>停職</option>
										<option value="감봉" <%=eq(rwType, "감봉")%>>減給</option>
										<option value="견책" <%=eq(rwType, "견책")%>>譴責</option>
										<option value="주의" <%=eq(rwType, "주의")%>>注意</option>
										<option value="경고" <%=eq(rwType, "경고")%>>警告</option>
										<option value="조치불가" <%=eq(rwType, "조치불가")%>>措置不可</option>
										<option value="해고" <%=eq(rwType, "해고")%>>解雇</option>
								</select></td>
								<td><input class="input" type="date"
									name="rewardDate<%=i%>" value="<%=rwDate%>"></td>
								<td><input class="input" type="text"
									name="rewardName<%=i%>" placeholder="賞罰名" value="<%=rwName%>"></td>
								<td><input class="input" type="text"
									name="rewardContent<%=i%>" placeholder="賞罰内容" value="<%=rwContent%>"></td>
								<td><input class="input" type="text"
									name="rewardMemo<%=i%>" placeholder="備考" value="<%=rwMemo%>"></td>
							</tr>
							<%
							}
							%>
						</tbody>
					</table>
				</div>
			</section>

			<!-- 異動セクション / 발령 섹션 -->
			<section class="source-section" id="appointment">
				<div class="source-section-title">異動</div>
				<div class="table-toolbar compact">
					<div></div>
					<div class="actions">
						<button type="submit" name="formAction" value="addAppointmentRow"
							class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#appointment">追加</button>
						<button type="submit" name="formAction"
							value="deleteAppointmentRows" class="btn btn-sm"
							formaction="<%=request.getContextPath()%>/Config/employeeIns2.do#appointment">選択削除</button>
					</div>
				</div>
				<div class="table-wrap">
					<table class="data-table source-data-table" id="appointmentTable">
						<thead>
							<tr>
								<th>選択</th>
								<th>異動区分</th>
								<th>異動日付</th>
								<th>部署</th>
								<th>役職</th>
								<th>職責・担当業務</th>
								<th>備考</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 1; i <= appointmentRowCount; i++) {
								String apType = getAttrOrParam(request, "apptType" + i);
								String apDate = getAttrOrParam(request, "apptDate" + i);
								String apDept = getAttrOrParam(request, "apptDept" + i);
								String apPosition = getAttrOrParam(request, "apptPosition" + i);
								String apDuty = getAttrOrParam(request, "apptDuty" + i);
								String apMemo = getAttrOrParam(request, "apptMemo" + i);
							%>
							<tr>
								<td><input type="checkbox" name="apptDel<%=i%>"></td>
								<td><select class="select" name="apptType<%=i%>">
										<option value="" <%=apType.isEmpty() ? "selected" : ""%>>選択</option>
										<option value="채용" <%=eq(apType, "채용")%>>採用</option>
										<option value="전보" <%=eq(apType, "전보")%>>転勤</option>
										<option value="승진" <%=eq(apType, "승진")%>>昇進</option>
										<option value="승격" <%=eq(apType, "승격")%>>昇格</option>
										<option value="승호" <%=eq(apType, "승호")%>>昇号</option>
										<option value="파견" <%=eq(apType, "파견")%>>派遣</option>
								</select></td>
								<td><input class="input" type="date" name="apptDate<%=i%>"
									value="<%=apDate%>"></td>
								<td><input class="input" type="text" name="apptDept<%=i%>"
									placeholder="部署名" value="<%=apDept%>"></td>
								<td><input class="input" type="text"
									name="apptPosition<%=i%>" placeholder="役職" value="<%=apPosition%>"></td>
								<td><input class="input" type="text" name="apptDuty<%=i%>"
									placeholder="担当業務" value="<%=apDuty%>"></td>
								<td><input class="input" type="text" name="apptMemo<%=i%>"
									placeholder="備考" value="<%=apMemo%>"></td>
							</tr>
							<%
							}
							%>
						</tbody>
					</table>
				</div>
			</section>

			<!-- 退職セクション / 퇴직 섹션 -->
			<section class="source-section" id="retirement">
				<div class="source-section-title">退職</div>
				<table class="source-form-table">
					<tbody>
						<tr>
							<th>退職区分</th>
							<td class="span-1"><select class="select" name="retireType">
									<option value=""
										${empty empInfo.retirementTypeCode ? 'selected' : ''}>選択してください</option>
									<option value="자진퇴사"
										${empInfo.retirementTypeCode == '자진퇴사' ? 'selected' : ''}>自己都合退職</option>
									<option value="계약만료"
										${empInfo.retirementTypeCode == '계약만료' ? 'selected' : ''}>契約満了</option>
									<option value="권고사직"
										${empInfo.retirementTypeCode == '권고사직' ? 'selected' : ''}>会社都合退職</option>
									<option value="정년퇴직"
										${empInfo.retirementTypeCode == '정년퇴직' ? 'selected' : ''}>定年退職</option>
							</select></td>
							<th>退職日付</th>
							<td class="span-1"><input type="date" class="input"
								name="retireDate" value="${empInfo.resignDate}"></td>
						</tr>
						<tr>
							<th>退職理由</th>
							<td class="span-3"><textarea class="textarea"
									name="retireReason" placeholder="退職理由を入力">${empInfo.retirementReason}</textarea></td>
						</tr>
						<tr>
							<th>退職後の連絡先</th>
							<td class="span-1"><input type="text" class="input"
								name="retirePhone" placeholder="退職後の連絡先" value="${empInfo.postRetirementPhone}"></td>
						</tr>
					</tbody>
				</table>
			</section>

			<!-- 保存・キャンセルボタンエリア / 저장/취소 버튼 영역 -->
			<div class="source-bottom-actions">
				<button type="submit" class="btn btn-primary">保存</button>
				<button type="button" class="btn"
					onclick="if(confirm('キャンセルすると入力した社員情報がすべて削除されます。続けますか？')) { document.getElementById('cancelForm').submit(); }">キャンセル</button>
				<button type="button" class="btn"
					onclick="location.href='<%=request.getContextPath()%>/Person/employeeMnt.do'">リスト</button>
				<button type="submit" class="btn btn-blue">新規社員登録</button>
			</div>

		</form>

		<!-- キャンセル時に社員データをDBから削除するフォーム / 취소 시 DB에서 사원 삭제하는 폼 -->
		<form id="cancelForm" method="post" action="<%=request.getContextPath()%>/Config/employeeRegisterCancel.do">
			<input type="hidden" name="employeeNo" value="${empInfo.employeeNo}">
		</form>
		<%
		// 保存完了アラート / 저장 완료 알림
		if (request.getAttribute("justSaved") != null) {
		%>
		<script type="text/javascript">
			alert('保存しました。');
		</script>
		<%
		}
		%>
	</div>
</div>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
