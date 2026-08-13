<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>급여지급 사원선택 | HEXAGON PAY</title>
<style>
    body { font-family: 'Malgun Gothic', sans-serif; font-size: 12px; color: #333; margin: 0; padding: 15px; background: #fff; }
    .modal-header { font-size: 18px; font-weight: bold; margin-bottom: 15px; border-bottom: 2px solid #337ab7; padding-bottom: 10px; color: #555; }
    .search-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
    .search-input-group { display: flex; align-items: center; gap: 5px; }
    .filter-group { display: flex; align-items: center; gap: 5px; }
    select, input[type="text"] { border: 1px solid #ccc; padding: 4px; font-size: 12px; }
    .btn-search { background: #f8f9fa; border: 1px solid #ccc; padding: 4px 8px; cursor: pointer; }

    .emp-table { width: 100%; border-collapse: collapse; text-align: center; }
    .emp-table th, .emp-table td { border: 1px solid #ddd; padding: 8px; }
    .emp-table th { background: #f9f9f9; color: #337ab7; font-weight: bold; }

    .modal-footer { text-align: center; margin-top: 20px; }
    .btn-select { background: #337ab7; color: white; border: none; padding: 8px 25px; font-weight: bold; cursor: pointer; border-radius: 3px; }
    .btn-cancel { background: #aaa; color: white; border: none; padding: 8px 25px; font-weight: bold; cursor: pointer; border-radius: 3px; }
</style>
</head>
<body>

    <div class="modal-header">급여지급 사원선택</div>

    <!-- 검색 및 필터 영역 -->
    <form id="empSearchForm" action="${pageContext.request.contextPath}/Payment/dayWorkerEmployeeAddModal.do" method="GET" onsubmit="return validateSearch()">
        <div class="search-bar">
            <div class="search-input-group">
                <input type="text" id="empNameInput" name="empName" placeholder="사원검색" style="width: 120px;" value="${empName}">
                <button type="submit" class="btn-search"><i class="fas fa-search" style="color:#337ab7;"></i></button>
            </div>

            <div class="filter-group">
                <select name="department" onchange="this.form.submit()">
                    <option value="">부서별</option>
                    <c:forEach var="dept" items="${deptList}">
                        <option value="${dept}" <c:if test="${dept == selectedDept}">selected</c:if>>${dept}</option>
                    </c:forEach>
                </select>

                <select name="status" onchange="this.form.submit()">
                    <option value="">상태별</option>
                    <option value="재직" <c:if test="${selectedStatus == '재직'}">selected</c:if>>재직</option>
                    <option value="퇴직" <c:if test="${selectedStatus == '퇴직'}">selected</c:if>>퇴직</option>
                </select>
            </div>
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
                <th>직위</th>
                <th>상태</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="emp" items="${availableEmployeeList}">
                <tr>
                    <td><input type="checkbox" class="emp-checkbox" value="${emp.employeeId}"></td>
                    <td>${emp.employmentType}</td>
                    <td>${emp.employeeNo}</td>
                    <td>${emp.employeeName}</td>
                    <td>${emp.department}</td>
                    <td>${emp.position}</td>
                    <td>${emp.status}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty availableEmployeeList}">
                <tr><td colspan="7" style="padding: 30px; color: #777;">검색된 일용직 근로자가 없습니다.</td></tr>
            </c:if>
        </tbody>
    </table>

    <div class="modal-footer">
        <button type="button" class="btn-select" onclick="addSelectedEmployees()">사원선택</button>
        <button type="button" class="btn-cancel" onclick="window.close()">선택취소</button>
    </div>

    <script>
        function validateSearch() {
            var keyword = document.getElementById("empNameInput").value.trim();
            if (event.submitter && event.submitter.className === 'btn-search') {
                if (keyword.length > 0 && keyword.length < 2) {
                    alert("검색어를 확인해주세요.\n\n검색어는 최소 2자 이상이어야 합니다.");
                    return false;
                }
            }
            return true;
        }

        function toggleAllCheckboxes(source) {
            document.querySelectorAll(".emp-checkbox").forEach(function (cb) { cb.checked = source.checked; });
        }

        function addSelectedEmployees() {
            var selectedIds = [];
            document.querySelectorAll(".emp-checkbox:checked").forEach(function (cb) { selectedIds.push(cb.value); });

            if (selectedIds.length === 0) {
                alert("사원목록에서 적용할 사원을 체크해 주세요.");
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
