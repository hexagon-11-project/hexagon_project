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
                <th>중간정산</th>
                <th>퇴직정산</th>
            </tr>
        </thead>
        <tbody>
            <!-- 동적 데이터 출력 처리 시작 -->
            <c:choose>
                <c:when test="${empty retirementList}">
                    <tr>
                        <td colspan="11" style="text-align: center; padding: 20px;">조회된 사원 데이터가 없습니다.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="emp" items="${retirementList}" varStatus="loop">
                        <tr>
                            <!-- 1. 순번: JSTL 루프 카운트 활용 -->
                            <td>${loop.count}</td>
                            
                            <!-- 2. 상태: retirementYn 값이 'Y'면 퇴직, 아니면 재직 -->
                            <td>${emp.retirementYn == 'Y' ? '퇴직' : '재직'}</td>
                            
                            <!-- 3~5. 사번, 성명, 부서 -->
                            <td>${emp.employeeNo}</td>
                            <td>${emp.employeeName}</td>
                            <td>${emp.department}</td>
                            
                            <!-- 6. 직위: null 일 경우 하이픈 처리 -->
                            <td>${empty emp.position ? '-' : emp.position}</td>
                            
                            <!-- 7~8. 입사일, 퇴직일 -->
                            <td>${emp.hireDate}</td>
                            <td>${empty emp.resignDate ? '-' : emp.resignDate}</td>
                            
                            <!-- 9. 근속연수 -->
                            <td>${emp.workYears}</td>
                            
                            <!-- 10. 중간정산: Y/N 값을 '●'/'×' 기호로 변환 -->
                            <td>${emp.interimSettlementYn == 'Y' ? '●' : '×'}</td>
                            
                            <!-- 11. 퇴직정산: Y/N 값을 '●'/'×' 기호로 변환 -->
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

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>