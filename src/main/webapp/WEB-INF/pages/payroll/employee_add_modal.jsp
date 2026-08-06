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
	<form id="empSearchForm" action="${pageContext.request.contextPath}/payroll/searchEmployee.do" method="GET" onsubmit="return validateSearch()">
		<!-- [수정] </div> 태그 꼬임 해결: search-bar 안에 검색칸과 필터가 나란히 오도록 묶음 -->
		<div class="search-bar">
			<div class="search-input-group">
				<input type="text" id="empNameInput" name="empName" placeholder="사원검색" style="width: 120px;">
				<button type="submit" class="btn-search"><i class="fas fa-search" style="color:#337ab7;"></i></button>
				<button type="button" class="btn-view-all" onclick="location.href='${pageContext.request.contextPath}/payroll/searchEmployee.do'">전체보기</button>
			</div> 
			
			<div class="filter-group">
				<select name="department">
					<option value="">부서별</option>
					<option value="사장실">사장실</option>
					<option value="개발팀">개발팀</option>
					<option value="콘텐츠팀">콘텐츠팀</option>
					<option value="업무지원팀">업무지원팀</option>
					<option value="디자인팀">디자인팀</option>
					<option value="관리팀">관리팀</option>
					<option value="기획전략팀">기획전략팀</option>
				</select>
				
				<select name="position">
					<option value="">직위별</option>
					<option value="이사">이사</option>
					<option value="차장">차장</option>
					<option value="사장">사장</option>
					<option value="부장">부장</option>
					<option value="과장">과장</option>
					<option value="대리">대리</option>
					<option value="주임">주임</option>
					<option value="사원">사원</option>
					<option value="실장">실장</option>
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
			<!-- 실제로는 JSTL로 DB 데이터를 가져와 뿌려줍니다 -->
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
			
			<!-- 데이터가 없을 때 방어 코드 -->
			<c:if test="${empty availableEmployeeList}">
				<tr>
					<td colspan="7" style="padding: 30px; color: #777;">검색된 사원이 없습니다.</td>
				</tr>
			</c:if>
		</tbody>
	</table>

	<!-- 페이징 (임시 하드코딩) -->
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
		// 1. 검색어 길이 검증 로직 (2글자 이상)
		function validateSearch() {
			// [수정] HTML에 있는 input 태그의 id인 'empNameInput'으로 값을 가져옵니다.
			var keyword = document.getElementById("empNameInput").value.trim();
			
			// 글자 수가 2보다 작을 때 (빈칸이거나 1글자만 썼을 때)
			if (keyword.length < 2) {
				alert("검색어를 확인해주세요.\n\n검색어는 최소 2자 이상이어야 합니다.");
				return false; // false를 반환하면 폼 제출을 멈춥니다.
			}
			
			// 정상적이면 true를 반환하여 폼을 제출합니다.
			return true;
		}

		// 2. 전체 선택 체크박스 로직
		function toggleAllCheckboxes(source) {
			var checkboxes = document.querySelectorAll(".emp-checkbox");
			checkboxes.forEach(function(cb) {
				cb.checked = source.checked;
			});
		}

		// 3. 사원 선택 버튼 클릭 시 부모 창(메인 화면)으로 데이터 전달
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

			// 부모 창(input.jsp)에 선택된 사원 ID 배열을 전달하여 폼 서브밋 유도
			if (window.opener && !window.opener.closed) {
				window.opener.addEmployeesToMain(selectedIds);
				window.close(); // 팝업 닫기
			} else {
				alert("메인 화면을 찾을 수 없습니다.");
			}
		}
	</script>

</body>
</html>