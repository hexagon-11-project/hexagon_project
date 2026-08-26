<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>給与内訳照会社員選択 | HEXAGON PAY</title>
<style>
    body { font-family: 'Malgun Gothic', sans-serif; font-size: 12px; color: #333; margin: 0; padding: 15px; background: #fff; }
    .modal-header { font-size: 18px; font-weight: bold; margin-bottom: 15px; border-bottom: 2px solid #337ab7; padding-bottom: 10px; color: #555; }
    .search-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
    .search-input-group { display: flex; align-items: center; gap: 5px; }
    .filter-group { display: flex; align-items: center; gap: 5px; }
    select, input[type="text"] { border: 1px solid #ccc; padding: 4px; font-size: 12px; }
    .btn-search { background: #f8f9fa; border: 1px solid #ccc; padding: 4px 8px; cursor: pointer; }
    .btn-view-all { background: #f8f9fa; border: 1px solid #ccc; padding: 4px 8px; color: #777; cursor: pointer; }

    .emp-table { width: 100%; border-collapse: collapse; text-align: center; }
    .emp-table th, .emp-table td { border: 1px solid #ddd; padding: 8px; }
    .emp-table th { background: #f9f9f9; color: #337ab7; font-weight: bold; }
    .emp-table tbody tr { cursor: pointer; }
    .emp-table tbody tr:hover { background: #f5f9fd; }
    .emp-table tbody tr.selected { background: #e6f2fb; }

    .pagination { text-align: center; margin-top: 15px; font-size: 13px; }
    .pagination span { color: #d9534f; font-weight: bold; border: 1px solid #d9534f; padding: 2px 6px; margin: 0 5px; }

    .modal-footer { text-align: center; margin-top: 20px; }
    .btn-select { background: #337ab7; color: white; border: none; padding: 8px 25px; font-weight: bold; cursor: pointer; border-radius: 3px; }
    .btn-select:disabled { background: #a9c6de; cursor: not-allowed; }
    .btn-cancel { background: #aaa; color: white; border: none; padding: 8px 25px; font-weight: bold; cursor: pointer; border-radius: 3px; }
</style>
</head>
<body>

    <div class="modal-header">給与内訳照会社員選択</div>

    <form id="empSearchForm" action="${pageContext.request.contextPath}/Payment/paymentPayListEmployeeModal.do" method="GET" onsubmit="return validateSearch()">
        <div class="search-bar">
            <div class="search-input-group">
                <input type="text" id="empNameInput" name="empName" placeholder="社員検索" style="width: 120px;" value="${empName}">
                <button type="submit" class="btn-search">🔍</button>
                <button type="button" class="btn-view-all" onclick="location.href='${pageContext.request.contextPath}/Payment/paymentPayListEmployeeModal.do'">全体表示</button>
            </div>

            <div class="filter-group">
                <select name="department" onchange="this.form.submit()">
                    <option value="">部署別</option>
                    <c:forEach var="dept" items="${deptList}">
                        <option value="${dept}" <c:if test="${dept == selectedDept}">selected</c:if>>${dept}</option>
                    </c:forEach>
                </select>

                <select name="status" onchange="this.form.submit()">
                    <option value="">状態別</option>
                    <option value="재직" <c:if test="${selectedStatus == '재직'}">selected</c:if>>在職</option>
                    <option value="퇴직" <c:if test="${selectedStatus == '퇴직'}">selected</c:if>>退職</option>
                </select>
            </div>
        </div>
    </form>

    <table class="emp-table">
        <thead>
            <tr>
                <th>区分</th>
                <th>社員番号</th>
                <th>氏名</th>
                <th>部署</th>
                <th>職位</th>
                <th>状態</th>
            </tr>
        </thead>
        <tbody id="empTableBody">
            <c:forEach var="emp" items="${availableEmployeeList}">
                <tr data-name="${emp.employeeName}" onclick="selectRow(this)">
                    <td>${emp.employmentType}</td>
                    <td>${emp.employeeId}</td>
                    <td>${emp.employeeName}</td>
                    <td>${emp.department}</td>
                    <td>${emp.position}</td>
                    <td>${emp.status}</td>
                </tr>
            </c:forEach>

            <c:if test="${empty availableEmployeeList}">
                <tr>
                    <td colspan="6" style="padding: 30px; color: #777;">検索された社員がいません。</td>
                </tr>
            </c:if>
        </tbody>
    </table>

    <div style="text-align: center; margin-top: 15px;">
        <c:if test="${currentPage > 1}">
            <a href="${pageContext.request.contextPath}/Payment/paymentPayListEmployeeModal.do?page=${currentPage - 1}&empName=${empName}&department=${selectedDept}&status=${selectedStatus}" style="text-decoration:none; color:black; margin-right:10px;">◀ 前へ</a>
        </c:if>

        <c:forEach begin="1" end="${totalPage}" var="pageNum">
            <c:choose>
                <c:when test="${pageNum == currentPage}">
                    <strong style="color:red; border: 1px solid red; padding: 2px 6px; margin: 0 5px;">${pageNum}</strong>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/Payment/paymentPayListEmployeeModal.do?page=${pageNum}&empName=${empName}&department=${selectedDept}&status=${selectedStatus}" style="text-decoration:none; color:black; margin: 0 5px;">${pageNum}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:if test="${currentPage < totalPage}">
            <a href="${pageContext.request.contextPath}/Payment/paymentPayListEmployeeModal.do?page=${currentPage + 1}&empName=${empName}&department=${selectedDept}&status=${selectedStatus}" style="text-decoration:none; color:black; margin-left:10px;">次へ ▶</a>
        </c:if>
    </div>

    <div class="modal-footer">
        <button type="button" class="btn-select" id="btnSelect" onclick="confirmSelection()" disabled>社員選択</button>
        <button type="button" class="btn-cancel" onclick="window.close()">選択取消</button>
    </div>

    <script>
        var selectedName = null;

        function validateSearch() {
            var keyword = document.getElementById("empNameInput").value.trim();
            if (event.submitter && event.submitter.className === 'btn-search') {
                if (keyword.length > 0 && keyword.length < 2) {
                    alert("検索語をご確認ください。\n\n検索語は2文字以上入力してください。");
                    return false;
                }
            }
            return true;
        }

        function selectRow(tr) {
            document.querySelectorAll("#empTableBody tr").forEach(function (r) { r.classList.remove("selected"); });
            tr.classList.add("selected");
            selectedName = tr.getAttribute("data-name");
            document.getElementById("btnSelect").disabled = false;
        }

        function confirmSelection() {
            if (!selectedName) {
                alert("社員を選択してください。");
                return;
            }
            if (window.opener && !window.opener.closed) {
                window.opener.pplSetSelectedEmployee(selectedName);
                window.close();
            } else {
                alert("メイン画面が見つかりません。");
            }
        }
    </script>
</body>
</html>
