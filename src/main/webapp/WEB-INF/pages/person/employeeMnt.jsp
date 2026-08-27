<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
request.setAttribute("pageTitle", "社員状況/管理");
request.setAttribute("pageSection", "人事管理");
request.setAttribute("pageDescription", "在職・休職・退職社員を照会し、選択した社員の関連画面へ移動します。");
request.setAttribute("activeKey", "employee-list");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<div class="employee-status-strip">
	<button type="button" class="employee-status active">
		<span>在職</span><strong>${countMap.active}</strong>
	</button>
	<button type="button" class="employee-status">
		<span>正社員</span><strong>${countMap.regular}</strong>
	</button>
	<button type="button" class="employee-status">
		<span>契約社員</span><strong>${countMap.contract}</strong>
	</button>
	<button type="button" class="employee-status">
		<span>臨時社員</span><strong>${countMap.temp}</strong>
	</button>
	<button type="button" class="employee-status">
		<span>日雇い</span><strong>${countMap.daily}</strong>
	</button>
	<button type="button" class="employee-status">
		<span>退職</span><strong>${countMap.retire}</strong>
	</button>
	<button type="button" class="employee-status dark">
		<span>全件</span><strong>${countMap.total}</strong>
	</button>
</div>

<div class="employee-list-tools"> 

	<form action="${pageContext.request.contextPath}/Person/employeeMnt.do" method="GET" style="display:inline;">
		<div class="search-strip">
			<select class="select" name="searchType">
				<option value="name" ${param.searchType == 'name' ? 'selected' : ''}>氏名</option>
				<option value="empNo" ${param.searchType == 'empNo' ? 'selected' : ''}>社員番号</option>
				<option value="dept" ${param.searchType == 'dept' ? 'selected' : ''}>部署</option>
			</select>
			<input class="input" type="text" name="keyword" value="${param.keyword}" placeholder="検索キーワードを入力">
			
			<button type="submit" class="btn btn-primary">検索</button>
			<button type="button" class="btn btn-primary" onclick="location.href='${pageContext.request.contextPath}/Person/employeeMnt.do'">全件表示</button>
		</div>
	</form>

	<div class="search-strip">
	</div>
</div>

<form id="deleteForm" action="${pageContext.request.contextPath}/Person/employeeMntDelete.do" method="POST">
	<div class="table-wrap">
		<table class="data-table source-data-table employee-master-table">
			<thead>
				<tr>
					<th>選択</th>
					<th>区分</th>
					<th>社員番号</th>
					<th>氏名</th>
					<th>部署</th>
					<th>役職</th>
					<th>生年月日</th>
					<th>入社日</th>
					<th>携帯電話</th>
					<th>メールアドレス</th>
					<th>ステータス</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="emp" items="${employeePage.content}">
					<tr style="transition: background-color 0.2s;" onmouseover="this.style.backgroundColor='#f8f9fa'" onmouseout="this.style.backgroundColor='transparent'">
						<td><input type="checkbox" name="empId" value="${emp.employeeId}"></td>
						<td>${emp.employmentType}</td>
						<td>${emp.employeeNo}</td>
						
						<td>
							<strong>
								<a href="${pageContext.request.contextPath}/Config/employeeIns1.do?employeeId=${emp.employeeId}" style="text-decoration: underline; color: #0056b3;">
									${emp.employeeName}
								</a>
							</strong>
						</td>
						
						<td>${emp.department}</td>
						<td>${emp.position}</td>
						<td>${emp.birthDate}</td>
						<td>${emp.hireDate}</td>
						<td>${emp.mobile}</td>
						<td>${emp.email}</td>
						<td>${emp.retirementYn == 'Y' ? '退職' : '在職'}</td>
					</tr>
				</c:forEach>
				<c:if test="${employeePage.hasNoEmployees()}">
					<tr>
						<td colspan="11">登録されている社員はいません。</td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</div>
</form> 

<div class="source-pagination">
	
	<!-- 이전 구간으로 이동 (‹ 이전페이지) -->
	<!-- 前の区間へ移動 (‹ 前のページ) -->
	<c:if test="${employeePage.startPage > 5}">
		<a href="employeeMnt.do?page=${employeePage.startPage - 5}&searchType=${param.searchType}&keyword=${param.keyword}">‹ 前のページ</a>
	</c:if>

	<!-- 페이지 번호 출력 -->
	<!-- ページ番号を出力 -->
	<c:forEach var="pNo" begin="${employeePage.startPage}" end="${employeePage.endPage}">
		<c:choose>
			<c:when test="${employeePage.currentPage == pNo}">
				<strong>${pNo}</strong>
			</c:when>
			<c:otherwise>
				<a href="employeeMnt.do?page=${pNo}&searchType=${param.searchType}&keyword=${param.keyword}">${pNo}</a>
			</c:otherwise>
		</c:choose>
	</c:forEach>

	<!-- 다음 구간으로 이동 (다음페이지 ›) -->
	<!-- 次の区間へ移動 (次のページ ›) -->
	<c:if test="${employeePage.endPage < employeePage.totalPages}">
		<a href="employeeMnt.do?page=${employeePage.startPage + 5}&searchType=${param.searchType}&keyword=${param.keyword}">次のページ ›</a>
	</c:if>
</div>

<div class="source-bottom-actions">
	<button type="button" class="btn btn-primary"
		onclick="location.href='${pageContext.request.contextPath}/Config/employeeIns1.do'">新規社員登録</button>
	<button type="submit" form="deleteForm" class="btn">選択削除</button>
</div>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        //  "삭제 하시겠습니까?" 확인 창 로직
        //  "削除しますか？" 確認ウィンドウのロジック
        const deleteForm = document.getElementById('deleteForm');
        
        if (deleteForm) {
            deleteForm.addEventListener('submit', function(event) {
                // 체크된 사원이 있는지 먼저 검사합니다.
                // チェックされた社員がいるか先に検査します。
                const checkedBoxes = deleteForm.querySelectorAll('input[name="empId"]:checked');
                
                if (checkedBoxes.length === 0) {
                    alert('削除する社員を先に選択してください。');
                    event.preventDefault(); // 폼 전송 중단 // フォーム送信中断
                    return;
                }

                // 사용자가 '취소'를 누르면 폼 전송을 중단합니다.
                // ユーザーが「キャンセル」を押すとフォームの送信を中断します。
                if (!confirm('削除しますか？')) {
                    event.preventDefault();
                }
            });
        }

        //  "삭제 되었습니다." 알림창 로직
        //  "削除しました。" アラートウィンドウのロジック
        const urlParams = new URLSearchParams(window.location.search);
        
        // URL에 delete=success가 있으면 알림창 생성
        // URLにdelete=successがあればアラートウィンドウを作成
        if (urlParams.get('delete') === 'success') {
            alert('削除しました。');
            
            // 알림창이 뜬 후, 새로고침 시 다시 뜨지 않도록 URL에서 파라미터 삭제.
            // アラートウィンドウが出た後、更新時に再び出ないようにURLからパラメータを削除。
            const url = new URL(window.location);
            url.searchParams.delete('delete');
            window.history.replaceState({}, document.title, url);
        }
    });
</script>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>