<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
request.setAttribute("pageTitle", "社員退職処理");
request.setAttribute("pageSection", "退職管理");
request.setAttribute("pageDescription", "在職・退職状態と退職給与の入力有無を確認し、退職処理または取り消します。");
request.setAttribute("activeKey", "retirement-process");
request.setAttribute("pageCss", "retirement.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<section class="filter-bar source-simple-filter">
    <form action="" method="get" style="display: flex; gap: 8px; align-items: center; margin: 0;">
        <select class="select">
            <option>氏名</option>
        </select>
        
        <input class="input" type="text" name="searchName" value="${searchName}" placeholder="検索ワードを入力してください">
        <button type="submit" class="btn btn-primary">検索</button>
        <button type="button" class="btn btn-primary" onclick="location.href='?'">全件表示</button>
        
        <select class="select" name="status" onchange="this.form.submit()">
            <option value="전체보기" ${status == '전체보기' ? 'selected' : ''}>ステータス別</option>
            <option value="N" ${status == 'N' ? 'selected' : ''}>在職</option>
            <option value="Y" ${status == 'Y' ? 'selected' : ''}>退職</option>
        </select>
    </form>
</section>

<div class="table-wrap">
    <table class="data-table source-data-table">
        <thead>
            <tr>
                <th>順番</th>
                <th>ステータス</th>
                <th>社員番号</th>
                <th>氏名</th>
                <th>部署</th>
                <th>役職</th>
                <th>入社日</th>
                <th>退職日</th>
                <th>勤続年数</th>
                <th>退職清算</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty retirementPage.content}">
                    <tr>
                        <td colspan="10" style="text-align: center; padding: 20px;">照会された社員データがありません。</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="emp" items="${retirementPage.content}" varStatus="loop">
                        <tr>
                            <!-- 순번 계산식 적용 (2페이지 가면 31번부터 시작) -->
                            <!--  順番計算式を適用 (2ページ目に移動すると31番から開始) -->
                            <td>${(retirementPage.currentPage - 1) * 30 + loop.count}</td>
                            
                            <td>${emp.retirementYn == 'Y' ? '退職' : '在職'}</td>
                            <td>${emp.employeeNo}</td>
                            <td>
                                <a href="javascript:void(0);" onclick="openRetireModal('${emp.employeeNo}', '${emp.employeeName}')" style="color: #0056b3; font-weight: bold; text-decoration: underline; cursor: pointer;">
                                    ${emp.employeeName}
                                </a>
                            </td>
                            <td>${emp.department}</td>
                            <td>${empty emp.position ? '-' : emp.position}</td>
                            <td>${emp.hireDate}</td>
                            <td>${empty emp.resignDate ? '-' : emp.resignDate}</td>
                            <td>${emp.workYears}</td>
                            <td>${emp.retirementSettlementYn == 'Y' ? '●' : '×'}</td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<div class="source-pagination" style="margin-top: 15px; text-align: center;">
    <c:if test="${retirementPage.startPage > 5}">
        <a href="?page=${retirementPage.startPage - 5}&searchName=${param.searchName}&status=${param.status}">‹ 前のページ</a>
    </c:if>

    <c:forEach var="pNo" begin="${retirementPage.startPage}" end="${retirementPage.endPage}">
        <c:choose>
            <c:when test="${retirementPage.currentPage == pNo}">
                <strong>${pNo}</strong>
            </c:when>
            <c:otherwise>
                <a href="?page=${pNo}&searchName=${param.searchName}&status=${param.status}">${pNo}</a>
            </c:otherwise>
        </c:choose>
    </c:forEach>

    <c:if test="${retirementPage.endPage < retirementPage.totalPages}">
        <a href="?page=${retirementPage.startPage + 5}&searchName=${param.searchName}&status=${param.status}">次のページ ›</a>
    </c:if>
</div>

<div id="modalOverlay" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); z-index:9998;" onclick="closeRetireModal()"></div>

<div id="retireModal" style="display:none; position:fixed; top:50%; left:50%; transform:translate(-50%, -50%); background:#fff; padding:25px; border:1px solid #ccc; box-shadow:0 4px 12px rgba(0,0,0,0.3); z-index:9999; border-radius: 8px; width: 450px;">
    <h3 style="margin-top:0; border-bottom: 2px solid #333; padding-bottom: 15px; font-size: 18px;">
        退職者退職処理 <span id="modalEmpName" style="font-size:15px; color:#555; font-weight: normal;"></span>
    </h3>
    
    <form action="${pageContext.request.contextPath}/Retire/retireProcessUpdate.do" method="post">
        <input type="hidden" id="modalEmployeeNo" name="employeeNo">
        
        <table style="width: 100%; border-collapse: separate; border-spacing: 0 12px;">
            <tr>
                <th style="text-align: left; width: 120px; font-weight: 600;">退職区分</th>
                <td>
                    <select name="retirementTypeCode" class="select" style="width: 100%; padding: 5px;">
                        <option value="선택">選択</option>
                        <option value="정년퇴직">定年退職</option>
                        <option value="정리해고">整理解雇</option>
                        <option value="자발적 퇴직">自己都合退職</option>
                        <option value="임원퇴직">役員退職</option>
                        <option value="기타">その他</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th style="text-align: left; font-weight: 600;">退職日付</th>
                <td><input type="date" name="resignDate" class="input" style="width: 100%; padding: 5px;" required></td>
            </tr>
            <tr>
                <th style="text-align: left; font-weight: 600;">退職理由</th>
                <td><input type="text" name="retirementReason" class="input" style="width: 100%; padding: 5px;" placeholder="理由を入力してください"></td>
            </tr>
            <tr>
                <th style="text-align: left; font-weight: 600;">退職後連絡先</th>
                <td><input type="text" name="postRetirementPhone" class="input" style="width: 100%; padding: 5px;" placeholder="例：010-1234-5678"></td>
            </tr>
        </table>
        
        <div style="text-align: center; margin-top: 25px;">
            <button type="submit" class="btn btn-primary" style="padding: 8px 20px; font-size: 14px;">保存</button>
            <button type="button" class="btn" onclick="closeRetireModal()" style="background:#eee; border:1px solid #ccc; padding: 8px 20px; font-size: 14px; margin-left: 10px;">キャンセル</button>
        </div>
    </form>
</div>

<script>
    function openRetireModal(empNo, empName) {
        document.querySelector("#retireModal form").reset();
        document.getElementById("modalEmployeeNo").value = empNo;
        document.getElementById("modalEmpName").innerText = "(" + empName + ")";
        
        document.getElementById("retireModal").style.display = "block";
        document.getElementById("modalOverlay").style.display = "block";
    }

    function closeRetireModal() {
        document.getElementById("retireModal").style.display = "none";
        document.getElementById("modalOverlay").style.display = "none";
    }
</script>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        // "저장하시겠습니까?" 확인 창 로직
        // 「保存しますか？」確認ウィンドウのロジック
        const retireForm = document.querySelector('#retireModal form');
        
        if (retireForm) {
            retireForm.addEventListener('submit', function(event) {
                if (!confirm('保存しますか？')) {
                    event.preventDefault();
                }
            });
        }

        // "저장되었습니다." 알림창 로직
        // 「保存されました。」アラートウィンドウのロジック
        const urlParams = new URLSearchParams(window.location.search);
        
        // URL에 save=success 파라미터가 있으면 알림창을 띄웁니다.
        // URLにsave=successパラメータがあればアラートウィンドウを表示します。
        if (urlParams.get('save') === 'success') {
            alert('保存されました。');
            
            const url = new URL(window.location);
            url.searchParams.delete('save');
            window.history.replaceState({}, document.title, url);
        }
    });
</script>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>