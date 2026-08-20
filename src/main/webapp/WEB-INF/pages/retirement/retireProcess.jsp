<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
request.setAttribute("pageTitle", "사원 퇴직처리");
request.setAttribute("pageSection", "퇴직관리");
request.setAttribute("pageDescription", "재직·퇴직 상태와 퇴직급여 입력 여부를 확인하고 퇴직처리 또는 취소합니다.");
request.setAttribute("activeKey", "retirement-process");
request.setAttribute("pageCss", "retirement.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<section class="filter-bar source-simple-filter">
    <!-- 폼 태그를 추가하여 Handler로 검색 조건 전달 -->
    <form action="" method="get" style="display: flex; gap: 8px; align-items: center; margin: 0;">
        <select class="select">
            <option>성명</option>
        </select>
        
        <!-- 검색어 유지 -->
        <input class="input" type="text" name="searchName" value="${searchName}" placeholder="검색어를 입력하세요">
        <button type="submit" class="btn btn-primary">검색</button>
        
        <!-- 전체보기 클릭 시 파라미터 초기화 후 이동 -->
        <button type="button" class="btn btn-primary" onclick="location.href='?'">전체보기</button>
        
        <!-- 상태별 셀렉트 박스 (변경 시 자동 submit) -->
        <select class="select" name="status" onchange="this.form.submit()">
            <option value="전체보기" ${status == '전체보기' ? 'selected' : ''}>상태별</option>
            <option value="N" ${status == 'N' ? 'selected' : ''}>재직</option>
            <option value="Y" ${status == 'Y' ? 'selected' : ''}>퇴직</option>
        </select>
    </form>
</section>

<div class="table-wrap">
    <table class="data-table source-data-table">
        <thead>
            <tr>
                <th>순번</th>
                <th>상태</th>
                <th>사원번호</th>
                <th>성명</th>
                <th>부서</th>
                <th>직위</th>
                <th>입사일</th>
                <th>퇴직일</th>
                <th>근속연수</th>
                <th>퇴직정산</th>
            </tr>
        </thead>
        <tbody>
            <!-- 동적 데이터 출력 처리 시작 -->
            <c:choose>
                <c:when test="${empty retirementList}">
                    <tr>
                        <td colspan="10" style="text-align: center; padding: 20px;">조회된 사원 데이터가 없습니다.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="emp" items="${retirementList}" varStatus="loop">
                        <tr>
                            <!-- 1. 순번: JSTL 루프 카운트 활용 -->
                            <td>${loop.count}</td>
                            
                            <!-- 2. 상태: retirementYn 값이 'Y'면 퇴직, 아니면 재직 -->
                            <td>${emp.retirementYn == 'Y' ? '퇴직' : '재직'}</td>
                            
                            <!-- 3. 사번 -->
                            <td>${emp.employeeNo}</td>
                            
                            <!-- 4. 성명 (클릭 시 모달 팝업 호출) -->
                            <td>
                                <a href="javascript:void(0);" onclick="openRetireModal('${emp.employeeNo}', '${emp.employeeName}')" style="color: #0056b3; font-weight: bold; text-decoration: underline; cursor: pointer;">
                                    ${emp.employeeName}
                                </a>
                            </td>
                            
                            <!-- 5. 부서 -->
                            <td>${emp.department}</td>
                            
                            <!-- 6. 직위: null 일 경우 하이픈 처리 -->
                            <td>${empty emp.position ? '-' : emp.position}</td>
                            
                            <!-- 7~8. 입사일, 퇴직일 -->
                            <td>${emp.hireDate}</td>
                            <td>${empty emp.resignDate ? '-' : emp.resignDate}</td>
                            
                            <!-- 9. 근속연수 -->
                            <td>${emp.workYears}</td>
                            
                            <!-- 10. 퇴직정산: Y/N 값을 '●'/'×' 기호로 변환 -->
                            <td>${emp.retirementSettlementYn == 'Y' ? '●' : '×'}</td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            <!-- 동적 데이터 출력 처리 끝 -->
        </tbody>
    </table>
</div>

<div class="source-pagination">
    ‹ 이전페이지 <strong>1</strong> 다음페이지 ›
</div>

<!-- ================= 모달 팝업 및 오버레이 영역 ================= -->
<div id="modalOverlay" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); z-index:9998;" onclick="closeRetireModal()"></div>

<div id="retireModal" style="display:none; position:fixed; top:50%; left:50%; transform:translate(-50%, -50%); background:#fff; padding:25px; border:1px solid #ccc; box-shadow:0 4px 12px rgba(0,0,0,0.3); z-index:9999; border-radius: 8px; width: 450px;">
    <h3 style="margin-top:0; border-bottom: 2px solid #333; padding-bottom: 15px; font-size: 18px;">
        퇴사자 퇴직처리 <span id="modalEmpName" style="font-size:15px; color:#555; font-weight: normal;"></span>
    </h3>
    
    <!-- 업데이트를 수행할 핸들러로 폼 전송 -->
    <form action="${pageContext.request.contextPath}/Retire/retireProcessUpdate.do" method="post">
        <!-- 서버에서 식별할 사원번호 (hidden) -->
        <input type="hidden" id="modalEmployeeNo" name="employeeNo">
        
        <table style="width: 100%; border-collapse: separate; border-spacing: 0 12px;">
            <tr>
                <th style="text-align: left; width: 120px; font-weight: 600;">퇴직구분</th>
                <td>
                    <select name="retirementTypeCode" class="select" style="width: 100%; padding: 5px;">
                        <option value="선택">선택</option>
                        <option value="정년퇴직">정년퇴직</option>
                        <option value="정리해고">정리해고</option>
                        <option value="자발적 퇴직">자발적 퇴직</option>
                        <option value="임원퇴직">임원퇴직</option>
                        <option value="기타">기타</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th style="text-align: left; font-weight: 600;">퇴직일자</th>
                <td><input type="date" name="resignDate" class="input" style="width: 100%; padding: 5px;" required></td>
            </tr>
            <tr>
                <th style="text-align: left; font-weight: 600;">퇴직사유</th>
                <td><input type="text" name="retirementReason" class="input" style="width: 100%; padding: 5px;" placeholder="사유를 입력하세요"></td>
            </tr>
            <tr>
                <th style="text-align: left; font-weight: 600;">퇴직 후 연락처</th>
                <td><input type="text" name="postRetirementPhone" class="input" style="width: 100%; padding: 5px;" placeholder="예: 010-1234-5678"></td>
            </tr>
        </table>
        
        <div style="text-align: center; margin-top: 25px;">
            <button type="submit" class="btn btn-primary" style="padding: 8px 20px; font-size: 14px;">저장</button>
            <button type="button" class="btn" onclick="closeRetireModal()" style="background:#eee; border:1px solid #ccc; padding: 8px 20px; font-size: 14px; margin-left: 10px;">취소</button>
        </div>
    </form>
</div>

<!-- ================= 자바스크립트 ================= -->
<script>
    // 모달 띄우기 (데이터 세팅 포함)
    function openRetireModal(empNo, empName) {
        // 폼 초기화 (이전 입력값 삭제) - 위치 수정!
        document.querySelector("#retireModal form").reset();
        
        // 모달창 내부의 태그에 사번과 이름 세팅
        document.getElementById("modalEmployeeNo").value = empNo;
        document.getElementById("modalEmpName").innerText = "(" + empName + ")";
        
        // 화면에 모달 및 오버레이 노출
        document.getElementById("retireModal").style.display = "block";
        document.getElementById("modalOverlay").style.display = "block";
    }

    // 모달 닫기
    function closeRetireModal() {
        document.getElementById("retireModal").style.display = "none";
        document.getElementById("modalOverlay").style.display = "none";
    }
</script>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>