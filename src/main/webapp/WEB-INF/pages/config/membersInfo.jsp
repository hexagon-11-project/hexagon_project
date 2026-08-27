<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
request.setAttribute("pageTitle", "ユーザー情報");
request.setAttribute("pageSection", "基本設定");
request.setAttribute("pageDescription", "会社・担当者・給与支払情報、ロゴ・社印を管理します。");
request.setAttribute("activeKey", "user-info");
request.setAttribute("pageCss", "environment.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>


<form
	action="${pageContext.request.contextPath}/Config/updateMembersInfo.do"
	method="post">

	<input type="hidden" name="companyId" value="${companyInfo.companyId}">

	<div class="user-info-layout">
		<div class="user-info-left">
			<section class="source-section">
				<div class="source-section-title">会社情報</div>
				<table class="source-form-table">
					<tbody>
						<tr>
							<th>商号</th>
							<td class="span-1"><input type="text" class="input"
								name="companyName" value="${companyInfo.companyName}"></td>
							<th>代表者名</th>
							<td class="span-1"><input type="text" class="input"
								name="ceoName" value="${companyInfo.ceoName}"></td>
						</tr>
						<tr>
							<th>事業者番号</th>
							<td class="span-1"><input type="text" class="input"
								name="businessNo" value="${companyInfo.businessNo}"></td>
							<th>法人登録番号</th>
							<td class="span-1"><input type="text" class="input"
								name="corpNo" value="${companyInfo.corpNo}"></td>
						</tr>
						<tr>
							<th>設立日</th>
							<td class="span-1"><input type="date" class="input"
								name="estDate" value="${companyInfo.estDate}"></td>
							<th>ホームページ</th>
							<td class="span-1"><input type="text" class="input"
								name="webSite" value="${companyInfo.webSite}"></td>
						
						<tr>
							<th>電話番号</th>
							<td class="span-1"><input type="text" class="input"
								name="telNo" value="${companyInfo.telNo}"></td>
							<th>FAX番号</th>
							<td class="span-1"><input type="text" class="input"
								name="faxNo" value="${companyInfo.faxNo}"></td>
						</tr>
						<tr>
							<th>業態</th>
							<td class="span-1"><input type="text" class="input"
								name="businessType" value="${companyInfo.businessType}"></td>
							<th>業種</th>
							<td class="span-1"><input type="text" class="input"
								name="businessItem" value="${companyInfo.businessItem}"></td>
						</tr>
					</tbody>
				</table>
			</section>
			<section class="source-section">
				<div class="source-section-title">給与支払情報</div>
				<table class="source-form-table">
					<tbody>
						<tr>
							<th>給与計算期間</th>
							<td class="span-1"><div class="date-rule">
									当月 <select class="select" name="payPeriodStartDay">
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
									</select> ~ 当月 <select class="select" name="payPeriodEndDay">
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
							<th>給与支給日</th>
							<td class="span-1"><div class="date-rule">
									翌月 <select class="select" name="payDay">
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
							<th>金融機関</th>
							<td class="span-1"><select class="select" name="bankName">
									<option value="KB国民銀行"
										${companyInfo.bankName == 'KB国民銀行' ? 'selected' : ''}>KB国民銀行</option>
									<option value="新韓銀行"
										${companyInfo.bankName == '新韓銀行' ? 'selected' : ''}>新韓銀行</option>
									<option value="ウリィ銀行"
										${companyInfo.bankName == 'ウリィ銀行' ? 'selected' : ''}>ウリィ銀行</option>
							</select></td>
							<th>口座番号</th>
							<td class="span-1"><input type="text" class="input"
								name="bankAccount" value="${companyInfo.bankAccount}"></td>
						</tr>
						<tr>
							<th>給与振込バンキング</th>
							<td class="span-3"><span class="muted">外部銀行への振込機能はプロジェクトの対象範囲外とします。</span></td>
						</tr>
					</tbody>
				</table>
			</section>
			<section class="source-section">
				<div class="source-section-title">会社ロゴ / 会社印</div>
				<div class="brand-assets">
					<div class="brand-asset">
						<div class="brand-title">会社ロゴ</div>
						<div class="brand-preview">
									<img src="${pageContext.request.contextPath}/assets/images/Logo.png" alt="회사로고" style="max-height: 80px; max-width: 100%;">

									会社ロゴ
						</div>
						<div class="mini-actions">
						</div>
					</div>
					<div class="brand-asset">
						<div class="brand-title">会社印</div>
						<div class="brand-preview seal-preview">
									<img src="${pageContext.request.contextPath}/assets/images/Seal.png" alt="직인" style="max-height: 80px; max-width: 100%;">
									社印
						</div>
						<div class="mini-actions">
						</div>
					</div>
				</div>
			</section>
		</div>
		<div class="user-info-right">
			<section class="source-section">
				<div class="source-section-title">担当者情報</div>
				<table class="source-form-table">
					<tbody>
						<tr>
							<th>氏名</th>
							<td class="span-3"><input type="text" class="input"
								name="managerName" value="${companyInfo.managerName}"></td>
						</tr>
						<tr>
							<th>부서</th>
							<td class="span-3"><div class="inline-control">
									<select class="select"><option selected>選択</option>
										<option>社長室</option>
										<option>開発チーム</option>
										<option>コンテンツチーム</option>
										<option>業務支援チーム</option>
										<option>デザインチーム</option>
										<option>管理チーム</option>
										<option>企画戦略チーム</option></select>
									<!-- 	<button type="button" class="btn btn-sm">관리</button> -->
								</div></td>
						</tr>
						<tr>
							<th>役職</th>
							<td class="span-3"><div class="inline-control">
									<select class="select"><option selected>選択</option>
										<option>取締役</option>
										<option>次長</option>
										<option>社長</option>
										<option>部長</option>
										<option>課長</option>
										<option>代理</option>
										<option>主任</option>
										<option>社員</option>
										<option>室長</option></select>
								</div></td>
						</tr>
						<tr>
							<th>電話番号</th>
							<td class="span-3"><input type="text" class="input"
								name="managerTel" value="${companyInfo.managerTel}"></td>
						</tr>
						<tr>
							<th>携帯電話番号</th>
							<td class="span-3"><input type="text" class="input"
								name="managerMobile" value="${companyInfo.managerMobile}"></td>
						</tr>
						<tr>
							<th>メールアドレス</th>
							<td class="span-3"><input type="email" class="input"
								name="managerEmail" value="${companyInfo.managerEmail}"></td>
						</tr>
					</tbody>
				</table>
			</section>
		</div>
	</div>
	<div class="source-bottom-actions">
		<button type="submit" class="btn btn-primary">保存</button>
	</div>
</form>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const urlParams = new URLSearchParams(window.location.search);
        
        // save 파라미터 값이 'success'이면 알림창을 띄웁니다.
        //saveパラメータの値が「success」の場合、アラートを表示します。
        if (urlParams.get('save') === 'success') {
            alert('保存しました。');
            
            // 알림창이 뜬 후, 새로고침 시 다시 뜨지 않도록 URL에서 파라미터를 정리합니다.
            //アラート表示後、再読み込み時に再度表示されないよう、URLからパラメータを削除します。
            const companyId = urlParams.get('id') || '1001';
            const cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname + "?id=" + companyId;
            window.history.replaceState({path: cleanUrl}, '', cleanUrl);
        }
    });
</script>

<script>
// 保存時にポップアップを表示
    document.addEventListener("DOMContentLoaded", function() {
        
        const form = document.querySelector('form');
        form.addEventListener('submit', function(event) {
            if (!confirm('保存しますか？')) {
                event.preventDefault();
            }
        });

       
        const urlParams = new URLSearchParams(window.location.search);
        
        if (urlParams.get('save') === 'success') {
            alert('保存しました。');
            
            const companyId = urlParams.get('id') || '1001';
            const cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname + "?id=" + companyId;
            window.history.replaceState({path: cleanUrl}, '', cleanUrl);
        }
    });
</script>