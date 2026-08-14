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
    .btn-view-all { background: #f8f9fa; border: 1px solid #ccc; padding: 4px 8px; color: #777; cursor: pointer; }
    
    .emp-table { width: 100%; border-collapse: collapse; text-align: center; }
    .emp-table th, .emp-table td { border: 1px solid #ddd; padding: 8px; }
    .emp-table th { background: #f9f9f9; color: #337ab7; font-weight: bold; }
    
    .pagination { text-align: center; margin-top: 15px; font-size: 13px; }
    .pagination span { color: #d9534f; font-weight: bold; border: 1px solid #d9534f; padding: 2px 6px; margin: 0 5px; }
    
    .modal-footer { text-align: center; margin-top: 20px; }
    .btn-select { background: #337ab7; color: white; border: none; padding: 8px 25px; font-weight: bold; cursor: pointer; border-radius: 3px; }
    .btn-cancel { background: #aaa; color: white; border: none; padding: 8px 25px; font-weight: bold; cursor: pointer; border-radius: 3px; }
</style>
</head>
<body>

    <div class="modal-header">급여지급 사원선택</div>

    <!-- 검색 및 필터 영역 -->
    <form id="empSearchForm" action="${pageContext.request.contextPath}/Payment/employeeAddModal.do" method="GET" onsubmit="return validateSearch()">
        <div class="search-bar">
            <div class="search-input-group">
                <input type="text" id="empNameInput" name="empName" placeholder="사원검색" style="width: 120px;" value="${empName}">
                <button type="submit" class="btn-search"><i class="fas fa-search" style="color:#337ab7;"></i></button>
                <button type="button" class="btn-view-all" onclick="location.href='${pageContext.request.contextPath}/Payment/employeeAddModal.do'">전체보기</button>
            </div> 
            
            <div class="filter-group">
                <!-- ★ [수정완료] 값이 변경되면(onchange) 폼을 제출하여 즉시 필터링 되도록 적용 -->
                <select name="department" onchange="this.form.submit()">
                    <option value="">부서별</option>
                    <c:forEach var="dept" items="${deptList}">
                        <option value="${dept}" <c:if test="${dept == selectedDept}">selected</c:if>>${dept}</option>
                    </c:forEach>
                </select>
                
                <select name="position" onchange="this.form.submit()">
                    <option value="">직위별</option>
                    <c:forEach var="pos" items="${posList}">
                        <option value="${pos}" <c:if test="${pos == selectedPos}">selected</c:if>>${pos}</option>
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
                    <td><input type="checkbox" name="selectedEmpIds" class="emp-checkbox" value="${emp.employeeId}"></td>
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
                    <td colspan="7" style="padding: 30px; color: #777;">검색된 사원이 없습니다.</td>
                </tr>
            </c:if>
        </tbody>
    </table>

    <!-- 페이징 영역 (페이지 이동 시에도 필터 상태 유지) -->
    <div style="text-align: center; margin-top: 15px;">
        <c:if test="${currentPage > 1}">
            <a href="${pageContext.request.contextPath}/Payment/employeeAddModal.do?page=${currentPage - 1}&empName=${empName}&department=${selectedDept}&position=${selectedPos}&status=${selectedStatus}" style="text-decoration:none; color:black; margin-right:10px;">◀ 이전</a>
        </c:if>

        <c:forEach begin="1" end="${totalPage}" var="pageNum">
            <c:choose>
                <c:when test="${pageNum == currentPage}">
                    <strong style="color:red; border: 1px solid red; padding: 2px 6px; margin: 0 5px;">${pageNum}</strong>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/Payment/employeeAddModal.do?page=${pageNum}&empName=${empName}&department=${selectedDept}&position=${selectedPos}&status=${selectedStatus}" style="text-decoration:none; color:black; margin: 0 5px;">${pageNum}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:if test="${currentPage < totalPage}">
            <a href="${pageContext.request.contextPath}/Payment/employeeAddModal.do?page=${currentPage + 1}&empName=${empName}&department=${selectedDept}&position=${selectedPos}&status=${selectedStatus}" style="text-decoration:none; color:black; margin-left:10px;">다음 ▶</a>
        </c:if>
    </div>

    <!-- 하단 액션 버튼 -->
    <div class="modal-footer">
        <button type="button" class="btn-select" onclick="addSelectedEmployees()">사원선택</button>
        <button type="button" class="btn-cancel" onclick="window.close()">선택취소</button>
    </div>

    <script>
        // 드롭다운 변경 시 폼을 제출하므로, 이름이 1글자일 때 경고창 뜨는 로직을 버튼 클릭 시에만 동작하도록 개선
        function validateSearch() {
            var keyword = document.getElementById("empNameInput").value.trim();
            // 검색 버튼을 직접 눌렀는데 1글자인 경우에만 막음
            if (event.submitter && event.submitter.className === 'btn-search') {
                if (keyword.length > 0 && keyword.length < 2) {
                    alert("검색어를 확인해주세요.\n\n검색어는 최소 2자 이상이어야 합니다.");
                    return false;
                }
            }
            return true;
        }

        function toggleAllCheckboxes(source) {
            var checkboxes = document.querySelectorAll(".emp-checkbox");
            checkboxes.forEach(function(cb) {
                cb.checked = source.checked;
            });
        }

        function addSelectedEmployees() {
            var selectedIds = [];
            var checkboxes = document.querySelectorAll(".emp-checkbox:checked");
            
            checkboxes.forEach(function(cb) {
                selectedIds.push(cb.value);
            });

            if (selectedIds.length === 0) {
                alert("추가할 사원을 선택해주세요.");
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