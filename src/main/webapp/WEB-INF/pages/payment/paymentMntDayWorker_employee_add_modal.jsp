<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>일용직 근로자선택 | HEXAGON PAY</title>
<style>
    body { font-family: 'Malgun Gothic', sans-serif; font-size: 12px; color: #333; margin: 0; padding: 15px; background: #fff; }
    .modal-header { font-size: 18px; font-weight: bold; margin-bottom: 15px; border-bottom: 2px solid #337ab7; padding-bottom: 10px; color: #555; }
    .search-bar { display: flex; align-items: center; gap: 5px; margin-bottom: 10px; }
    select, input[type="text"] { border: 1px solid #ccc; padding: 4px; font-size: 12px; }
    .btn-search { background: #f8f9fa; border: 1px solid #ccc; padding: 4px 8px; cursor: pointer; }
    .btn-view-all { background: #f8f9fa; border: 1px solid #ccc; padding: 4px 8px; color: #777; cursor: pointer; }

    .emp-table { width: 100%; border-collapse: collapse; text-align: center; }
    .emp-table th, .emp-table td { border: 1px solid #ddd; padding: 8px; }
    .emp-table th { background: #f9f9f9; color: #337ab7; font-weight: bold; }

    .modal-footer { text-align: center; margin-top: 20px; }
    .btn-select { background: #337ab7; color: white; border: none; padding: 8px 25px; font-weight: bold; cursor: pointer; border-radius: 3px; }
    .btn-cancel { background: #aaa; color: white; border: none; padding: 8px 25px; font-weight: bold; cursor: pointer; border-radius: 3px; }
</style>
</head>
<body>

    <div class="modal-header">일용직 근로자선택</div>

    <form id="empSearchForm" action="${pageContext.request.contextPath}/Payment/dayWorkerEmployeeAddModal.do" method="GET">
        <div class="search-bar">
            <input type="text" id="empNameInput" name="empName" placeholder="근로자검색" style="width: 140px;" value="${empName}">
            <button type="submit" class="btn-search"><i class="fas fa-search" style="color:#337ab7;"></i></button>
            <button type="button" class="btn-view-all" onclick="location.href='${pageContext.request.contextPath}/Payment/dayWorkerEmployeeAddModal.do'">전체보기</button>
        </div>
    </form>

    <table class="emp-table">
        <thead>
            <tr>
                <th><input type="checkbox" id="checkAll" onclick="toggleAllCheckboxes(this)"></th>
                <th>구분</th>
                <th>사원번호</th>
                <th>성명</th>
                <th>부서</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="emp" items="${availableEmployeeList}">
                <tr>
                    <td><input type="checkbox" class="emp-checkbox" value="${emp.employeeId}"></td>
                    <td>${emp.employmentType}</td>
                    <td>${emp.employeeId}</td>
                    <td>${emp.employeeName}</td>
                    <td>${emp.department}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty availableEmployeeList}">
                <tr><td colspan="5" style="padding: 30px; color: #777;">검색된 일용직 근로자가 없습니다.</td></tr>
            </c:if>
        </tbody>
    </table>

    <div class="modal-footer">
        <button type="button" class="btn-select" onclick="addSelectedEmployees()">근로자선택</button>
        <button type="button" class="btn-cancel" onclick="window.close()">선택취소</button>
    </div>

    <script>
        function toggleAllCheckboxes(source) {
            document.querySelectorAll(".emp-checkbox").forEach(function (cb) { cb.checked = source.checked; });
        }

        function addSelectedEmployees() {
            var selectedIds = [];
            document.querySelectorAll(".emp-checkbox:checked").forEach(function (cb) { selectedIds.push(cb.value); });

            if (selectedIds.length === 0) {
                alert("추가할 근로자를 선택해주세요.");
                return;
            }
            if (window.opener && !window.opener.closed) {
                window.opener.addEmployeesToMain(selectedIds);
                window.close();
            } else {
                alert("메인 화면을 찾을 수 없습니다.");
            }
        }
    </script>
</body>
</html>
