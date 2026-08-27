<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
request.setAttribute("pageTitle", "諸証明書発行台帳");
request.setAttribute("pageSection", "人事管理");
request.setAttribute("pageDescription", "証明書の発行履歴を期間・証明書・社員基準で照会し、印刷します。");
request.setAttribute("activeKey", "certificate-ledger");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<form action="${pageContext.request.contextPath}/Person/certificateRegister.do" method="GET">
	<section class="filter-bar">
		<div class="field ">
			<label>発行期間</label>
			<div class="range">
				<input type="date" name="startDate" class="input" value="${startDate}"> 
				<span>~</span> 
				<input type="date" name="endDate" class="input" value="${endDate}">
			</div>
		</div>
		<div class="field ">
			<label>証明書</label>
			<select class="select" name="certType">
				<option value="전체" ${certType == '全体' ? 'selected' : ''}>全体</option>
				<option value="在職証明書" ${certType == '在職証明書' ? 'selected' : ''}>在職証明書</option>
				<option value="経歴証明書" ${certType == '経歴証明書' ? 'selected' : ''}>経歴証明書</option>
				<option value="退職証明書" ${certType == '退職証明書' ? 'selected' : ''}>退職証明書</option>
			</select>
		</div>
		<div class="field ">
			<label>社員名</label>
			<input type="text" name="empName" class="input" value="${empName}">
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">照会</button>
		</div>
	</section>
</form>

<section class="card ">
	<div class="card-header">
		<h2 class="section-title">諸証明書発行台帳</h2>
	</div>
	<div class="card-body">
		
		
		<form id="deleteForm" action="${pageContext.request.contextPath}/Person/certificateRegisterUpdate.do" method="POST">
			<div class="table-toolbar">
				
				<span class="table-count">計 ${certList.size()}件</span>
				<div class="actions">
					<button type="submit" class="btn btn-danger">選択削除</button>
				</div>
			</div>
			
			<div class="table-wrap">
				<table class="data-table ">
					<thead>
						<tr>
							<th>選択</th>
							<th>発行番号</th>
							<th>発行日</th>
							<th>氏名</th>
							<th>証明書</th>
							<th>用途</th>
							<th>発行者</th>
							<th>ステータス</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="cert" items="${certList}">
							<tr>
								<td><input type="checkbox" name="issueNo" value="${cert.issueNo}"></td>
								<td>${cert.issueNo}</td>
								<td>${cert.issueDate}</td>
								<td>${cert.employeeName}</td>
								<td>${cert.certificateTypeCode}</td>
								<td>${cert.purpose}</td>
								<td>${cert.regId}</td>
								
								<td>
									<c:choose>
										<c:when test="${cert.certificateYn == 'Y'}">
											<span style="color: green;">発行</span>
										</c:when>
										<c:otherwise>
											<span style="color: red; font-weight: bold;">取消</span>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</form> 
		
	</div>
</section>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        // "삭제 하시겠습니까?" 확인 창 로직
        // 「削除しますか？」確認ウィンドウのロジック
        const deleteForm = document.getElementById('deleteForm');
        
        if (deleteForm) {
            deleteForm.addEventListener('submit', function(event) {
                // 체크된 항목이 있는지 검사
                // チェックされた項目があるか検査
                const checkedBoxes = deleteForm.querySelectorAll('input[name="issueNo"]:checked');
                
                if (checkedBoxes.length === 0) {
                    alert('削除(取消)する証明書を先に選択してください。');
                    event.preventDefault(); 
                    return;
                }

                // 사용자가 '취소'를 누르면 폼 전송을 중단
                // ユーザーが「キャンセル」を押すとフォーム送信を中断
                if (!confirm('削除しますか？')) {
                    event.preventDefault();
                }
            });
        }

        // "삭제 되었습니다." 알림창 로직
        //「削除されました。」アラートウィンドウのロジック
        const urlParams = new URLSearchParams(window.location.search);
        
        // URL에 delete=success 파라미터가 있으면 알림창을 띄웁니다.
        // URLにdelete=successパラメータがあればアラートウィンドウを表示します。
        if (urlParams.get('delete') === 'success') {
            alert('削除されました。');
            
            // 알림창이 뜬 후, 새로고침 시 다시 뜨지 않도록 URL 파라미터 정리
            // アラートウィンドウが表示された後、更新時に再び表示されないようURLパラメータを整理
            const url = new URL(window.location);
            url.searchParams.delete('delete');
            window.history.replaceState({}, document.title, url);
        }
    });
</script>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>