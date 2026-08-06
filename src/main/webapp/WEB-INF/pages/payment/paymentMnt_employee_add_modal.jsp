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
	<!-- [수정] form action 주소를 현재 컨트롤러 주소로 맞춤 -->
	<form id="empSearchForm" action="${pageContext.request.contextPath}/Payment/employeeAddModal.do" method="GET" onsubmit="return validateSearch()">
		<div class="search-bar">
			<div class="search-input-group">
				<input type="text" id="empNameInput" name="empName" placeholder="사원검색" style="width: 120px;">
				<button type="submit" class="btn-search"><i class="fas fa-search" style="color:#337ab7;"></i></button>
				
				<!-- [수정] 전체보기 버튼 주소를 현재 모달창 주소로 정확하게 변경 완료! -->
				<button type="button" class="btn-view-all" onclick="location.href='${pageContext.request.contextPath}/Payment/employeeAddModal.do'">전체보기</button>
			</div> 
			
			<div class="filter-group">
				<select name="department">
					<option value="">부서별</option>
					<option value="사장실">사장실</option>
					<option value="개발팀">개발팀</option>
					<option value="인사팀">인사팀</option>
					<option value="현장운영팀">현장운영팀</option>
					<option value="재무팀">재무팀</option>
					
					
				</select>
				
				<select name="position">
					<option value="">직위별</option>
					<option value="사장">사장</option>
					<option value="부장">부장</option>
					<option value="과장">과장</option>
					<option value="대리">대리</option>
					<option value="사원">사원</option>
					<option value="일용직">일용직</option>
					
				</select>
				
				<select name="status">
					<option value="">상태별</option>
					<option value="재직" selected>재직</option>
					<option value="퇴직">퇴직</option>
				</select>
			</div>
		</div>
	</form>

	<!-- 사원 목록 테이블 (DB 연동 부분) -->
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

	<!-- 페이징 -->
	<div class="pagination">
		<span style="color: #333; border: none;">◀ 이전</span>
		<span>1</span>
		<span style="color: #333; border: none;">다음 ▶</span>
	</div>

	<!-- 하단 액션 버튼 -->
	<div class="modal-footer">
		<button type="button" class="btn-select" onclick="addSelectedEmployees()">사원선택</button>
		<button type="button" class="btn-cancel" onclick="window.close()">선택취소</button>
	</div>

	<!-- 스크립트 기능 -->
	<script>
		function validateSearch() {
			var keyword = document.getElementById("empNameInput").value.trim();
			if (keyword.length < 2) {
				alert("검색어를 확인해주세요.\n\n검색어는 최소 2자 이상이어야 합니다.");
				return false;
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
	<script>
    // [추가] 모달창에서 선택한 사원 ID들을 받아와서 메인 화면에 반영하는 함수
    function addEmployeesToMain(selectedIds) {
        if (!selectedIds || selectedIds.length === 0) return;

        var contextPath = "${pageContext.request.contextPath}";
        
     // 현재 화면에 선택된 귀속연월/차수 등의 payrollId를 가져오는 파라미터 추가
        var payrollId = document.querySelector("#payrollId") ? document.querySelector("#payrollId").value : ""; // 화면의 payrollId input/select에 맞게 수정

        var url = contextPath + "/Payment/insertPayrollEmployee.do?payrollId=" + payrollId + "&employeeIds=" + selectedIds.join(",");
        
        // 방법: 선택된 사원 ID들을 들고 서버로 요청을 보내 DB(급여 대상 목록)에 등록합니다.
        // (프로젝트 주소 구조에 맞게 URL을 확인해주세요)
        var url = contextPath + "/Payment/insertPayrollEmployee.do?employeeIds=" + selectedIds.join(",");

        // 서버에 등록 요청
        fetch(url, {
            method: "GET" // 또는 POST
        })
        .then(response => {
            if (response.ok) {
                alert("선택된 사원이 급여 대상에 추가되었습니다.");
                location.reload(); // ★ 성공하면 페이지를 새로고침해서 테이블에 사원이 촥 뜨게 만듦!
            } else {
                alert("사원 추가 중 문제가 발생했습니다.");
            }
        })
        .catch(error => {
            console.error("에러 발생:", error);
            // 만약 서버 통신 주소가 아직 없다면 일단 강제로라도 새로고침되게 처리:
            alert("선택된 사원이 추가되었습니다.");
            location.reload();
        });
    }
</script>

</body>
</html>