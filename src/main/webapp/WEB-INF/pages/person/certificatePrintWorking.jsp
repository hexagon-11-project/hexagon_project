<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
request.setAttribute("pageTitle", "諸証明書の発行");
request.setAttribute("pageSection", "人事管理");
request.setAttribute("pageDescription", "社員を選択し、在職・経歴・退職証明書を作成して印刷します。");
request.setAttribute("activeKey", "certificate-issue");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<!--  퇴직증명서 예외 처리 알림창  -->
<!--  退職証明書の例外処理アラート  -->
<c:if test="${not empty alertMessage}">
	<div
		style="color: #d9534f; background-color: #f2dede; border: 1px solid #ebccd1; padding: 15px; margin-bottom: 20px; border-radius: 4px; font-weight: bold;">
		※ ${alertMessage}</div>
</c:if>

<div class="certificate-source-layout">
	<section class="source-list-panel">
		<div class="source-list-search">
			<!-- 검색 폼: GET 방식으로 검색어(searchName) 전송 -->
			<!-- 検索フォーム：GET方式で検索キーワード(searchName)を送信 -->
			<form
				action="${pageContext.request.contextPath}/Person/certificatePrintWorking.do"
				method="GET" style="display: flex; gap: 5px; width: 100%;">
				<!-- 선택된 사원 정보와 증명서 타입 유지를 위한 hidden 필드 -->
				<!-- 選択された社員情報と証明書タイプを維持するためのhiddenフィールド -->
				<input type="hidden" name="employeeNo" value="${selectedEmpNo}">
				<input type="hidden" name="certType" value="${selectedCertType}">

				<input class="input" type="text" name="searchName"
					placeholder="名前を入力" value="${param.searchName}">
				<button class="btn btn-primary" type="submit">検索</button>
				<button class="btn btn-primary" type="button"
					onclick="location.href='${pageContext.request.contextPath}/Person/certificatePrintWorking.do'"
					style="background-color: #6c757d; border-color: #6c757d;">全件表示</button>
			</form>
		</div>
		<div class="table-wrap">
			<table class="data-table source-data-table compact-list">
				<thead>
					<tr>
						<th>区分</th>
						<th>社員番号</th>
						<th>氏名</th>
						<th>部署</th>
						<th>役職</th>
						<th>ステータス</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="emp" items="${empList}">
						<tr
							<c:if test="${emp.employeeNo == selectedEmpNo}">style="background-color: #f0f8ff;"</c:if>>
							<td>${emp.employmentType}</td>
							<td>${emp.employeeNo}</td>
							<td><a
								href="${pageContext.request.contextPath}/Person/certificatePrintWorking.do?employeeNo=${emp.employeeNo}&certType=${selectedCertType}&searchName=${param.searchName}"
								style="color: #0056b3; text-decoration: underline; font-weight: bold;">
									${emp.employeeName} </a></td>
							<td>${emp.department}</td>
							<td>${emp.position}</td>
							<td><c:choose>
									<c:when test="${emp.retirementYn == 'Y'}">
										<span style="color: gray;">退職</span>
									</c:when>
									<c:otherwise>
										<span style="color: blue;">在職</span>
									</c:otherwise>
								</c:choose></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</section>

	<section class="certificate-workspace">
		<div class="certificate-tabs">
			<a
				href="${pageContext.request.contextPath}/Person/certificatePrintWorking.do?employeeNo=${selectedEmpNo}&certType=在職証明書"
				class="btn ${selectedCertType == '在職証明書' || selectedCertType == '재직증명서' ? 'active' : ''}"
				style="text-decoration: none;">在職証明書</a> <a
				href="${pageContext.request.contextPath}/Person/certificatePrintWorking.do?employeeNo=${selectedEmpNo}&certType=経歴証明書"
				class="btn ${selectedCertType == '経歴証明書' || selectedCertType == '경력증명서' ? 'active' : ''}"
				style="text-decoration: none;">経歴証明書</a> <a
				href="${pageContext.request.contextPath}/Person/certificatePrintWorking.do?employeeNo=${selectedEmpNo}&certType=退職証明書"
				class="btn ${selectedCertType == '退職証明書' || selectedCertType == '퇴직증명서' ? 'active' : ''}"
				style="text-decoration: none;">退職証明書</a>
		</div>

		<form
			action="${pageContext.request.contextPath}/Person/certificatePrintWorkingInsert.do"
			method="POST">

			<input type="hidden" name="employeeNo" value="${selectedEmpNo}">
			<input type="hidden" name="certificateTypeCode"
				value="${selectedCertType}">
			<input type="hidden" name="issueNo" value="ISSUE-${today}">

			<div class="document-sheet source-certificate">
				<h2 class="document-title">
					<c:choose>
						<c:when test="${selectedCertType == '経歴証明書' || selectedCertType == '경력증명서'}">経 歴 証 明 書</c:when>
						<c:when test="${selectedCertType == '退職証明書' || selectedCertType == '퇴직증명서'}">退 職 証 明 書</c:when>
						<c:otherwise>在 職 証 明 書</c:otherwise>
					</c:choose>
				</h2>

				<table class="certificate-table">
					<tbody>
						<tr>
							<th rowspan="2">人的事項</th>
							<th>氏名</th>
							<td>${empDetail.employeeName}</td>
							<th>住民登録番号<br>(生年月日)
							</th>
							<td>${empDetail.residentRegNo}</td>
						</tr>
						<tr>
							<th>住所</th>
							<td colspan="3"></td>
						</tr>
						<tr>
							<th rowspan="3">在職事項</th>
							<th>会社名</th>
							<td>(주)헥사곤테크</td>
							<th>事業者番号</th>
							<td>123-45-67890</td>
						</tr>
						<tr>
							<th>部署</th>
							<td>${empDetail.department}</td>
							<th>役職</th>
							<td>${empDetail.position}</td>
						</tr>
						<tr>
							<th>入社日</th>
							<td>${empDetail.hireDate}</td>
							<th>勤続期間</th>
							<td>${workPeriod}</td>
						</tr>

						<tr>
							<th>発行用途</th>
							<td colspan="4"><select class="select" name="purpose"
								required>
									<option value="">選択</option>
									<option value="金融機関提出用">金融機関提出用</option>
									<option value="官公庁提出用">官公庁提出用</option>
									<option value="他社提出用">他社提出用</option>
							</select></td>
						</tr>

						<tr>
							<th>提出先</th>
							<td colspan="4"><input type="text" class="input"
								name="submissionTarget" placeholder="提出先を入力してください (例：〇〇銀行)"
								style="width: 100%;"></td>
						</tr>

						<tr>
							<td colspan="5"><textarea
									class="textarea certificate-message">${certText}</textarea></td>
						</tr>
						<tr>
							<td colspan="5" class="certificate-date">${today}</td>
						</tr>
						<tr>
							<th>発行部署</th>
							<td colspan="2"><select class="select">
									<option>選択</option>
									<option value="人事チーム">人事チーム</option>
									<option value="経営支援チーム">経営支援チーム</option>
							</select></td>
							<th>連絡先</th>
							<td>02-0000-0000</td>
						</tr>
					</tbody>
				</table>
				<div class="signature-area"
					style="display: flex; align-items: center; justify-content: flex-end; gap: 10px;">
					<p style="margin: 0;">
						<strong>(주)헥사곤테크 代表取締役</strong>
					</p>
					<div class="seal-box">
						<img
							src="${pageContext.request.contextPath}/assets/images/Seal.png"
							alt="職印" style="width: 60px; height: 60px;">
					</div>
				</div>

				<div class="source-bottom-actions">
					<button type="submit" class="btn btn-primary">保存する</button>
				</div>
		</form>
	</section>
</div>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        // 확인 창 로직
        // 確認ウィンドウのロジック
       
        const insertForm = document.querySelector('form[action$="certificatePrintWorkingInsert.do"]');
        
        if (insertForm) {
            insertForm.addEventListener('submit', function(event) {
                // '발급용도'가 선택되지 않았으면 폼 전송을 막고 안내를 띄웁니다.
                // 「発行用途」が選択されていない場合、フォーム送信を停止して案内を表示します。
                const purpose = insertForm.querySelector('select[name="purpose"]').value;
                if (!purpose) {
                    alert('発行用途を選択してください。');
                    event.preventDefault(); 
                    return;
                }

                if (!confirm('保存しますか？')) {
                    event.preventDefault();
                }
            });
        }

        //   알림창 로직
        //  アラートウィンドウのロジック
        const urlParams = new URLSearchParams(window.location.search);
        
        // URL에 save=success 파라미터가 있으면 알림창을 띄웁니다.
        // URLにsave=successパラメータがあればアラートウィンドウを表示します。
        if (urlParams.get('save') === 'success') {
            alert('保存しました。');
            
            // 알림창이 뜬 후, 새로고침 시 다시 뜨지 않도록 URL에서 파라미터를 정리합니다.
            // アラートウィンドウが表示された後、更新時に再び表示されないようURLからパラメータを整理します。
            const url = new URL(window.location);
            url.searchParams.delete('save');
            window.history.replaceState({}, document.title, url);
        }
    });
</script>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>