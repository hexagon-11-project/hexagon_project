<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
request.setAttribute("pageTitle", "제 증명서 발급 대장");
request.setAttribute("pageSection", "인사관리");
request.setAttribute("pageDescription", "증명서 발급 이력을 기간·증명서·사원 기준으로 조회하고 인쇄합니다.");
request.setAttribute("activeKey", "certificate-ledger");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<form action="${pageContext.request.contextPath}/Person/certificateRegister.do" method="GET">
	<section class="filter-bar">
		<div class="field ">
			<label>발급기간</label>
			<div class="range">
				<input type="date" name="startDate" class="input" value="${startDate}"> 
				<span>~</span> 
				<input type="date" name="endDate" class="input" value="${endDate}">
			</div>
		</div>
		<div class="field ">
			<label>증명서</label>
			<select class="select" name="certType">
				<option value="전체" ${certType == '전체' ? 'selected' : ''}>전체</option>
				<option value="재직증명서" ${certType == '재직증명서' ? 'selected' : ''}>재직증명서</option>
				<option value="경력증명서" ${certType == '경력증명서' ? 'selected' : ''}>경력증명서</option>
				<option value="퇴직증명서" ${certType == '퇴직증명서' ? 'selected' : ''}>퇴직증명서</option>
			</select>
		</div>
		<div class="field ">
			<label>사원명</label>
			<input type="text" name="empName" class="input" value="${empName}">
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">조회</button>
		</div>
	</section>
</form>

<section class="card ">
	<div class="card-header">
		<h2 class="section-title">증명서 발급 대장</h2>
	</div>
	<div class="card-body">
		
		
		<form id="deleteForm" action="${pageContext.request.contextPath}/Person/certificateRegisterUpdate.do" method="POST">
			<div class="table-toolbar">
				
				<span class="table-count">총 ${certList.size()}건</span>
				<div class="actions">
					<button type="submit" class="btn btn-danger">선택 삭제</button>
				</div>
			</div>
			
			<div class="table-wrap">
				<table class="data-table ">
					<thead>
						<tr>
							<th>선택</th>
							<th>발급번호</th>
							<th>발급일</th>
							<th>성명</th>
							<th>증명서</th>
							<th>용도</th>
							<th>발급자</th>
							<th>상태</th>
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
											<span style="color: green;">발급</span>
										</c:when>
										<c:otherwise>
											<span style="color: red; font-weight: bold;">취소</span>
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
        // 1. "삭제 하시겠습니까?" 확인 창 로직
        const deleteForm = document.getElementById('deleteForm');
        
        if (deleteForm) {
            deleteForm.addEventListener('submit', function(event) {
                // 체크된 항목이 있는지 검사
                const checkedBoxes = deleteForm.querySelectorAll('input[name="issueNo"]:checked');
                
                if (checkedBoxes.length === 0) {
                    alert('삭제(취소)할 증명서를 먼저 선택해주세요.');
                    event.preventDefault(); 
                    return;
                }

                // 사용자가 '취소'를 누르면 폼 전송을 중단
                if (!confirm('삭제 하시겠습니까?')) {
                    event.preventDefault();
                }
            });
        }

        // 2. "삭제 되었습니다." 알림창 로직
        const urlParams = new URLSearchParams(window.location.search);
        
        // URL에 delete=success 파라미터가 있으면 알림창을 띄웁니다.
        if (urlParams.get('delete') === 'success') {
            alert('삭제 되었습니다.');
            
            // 알림창이 뜬 후, 새로고침 시 다시 뜨지 않도록 URL 파라미터 정리
            const url = new URL(window.location);
            url.searchParams.delete('delete');
            window.history.replaceState({}, document.title, url);
        }
    });
</script>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>