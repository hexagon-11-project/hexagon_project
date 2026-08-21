<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
request.setAttribute("pageTitle", "퇴직급여 명세서");
request.setAttribute("pageSection", "퇴직관리");
request.setAttribute("pageDescription", "퇴직급여 지급액과 실지급액을 명세서 형식으로 확인합니다.");
request.setAttribute("activeKey", "retirement-slip");
request.setAttribute("pageCss", "retirement.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<section class="filter-bar">
    <form action="${pageContext.request.contextPath}/Retire/retirePayslip.do" method="get" style="display: flex; gap: 10px; width: 100%; align-items: flex-end;">
        
        <div class="field ">
            <label>사원</label>
            <!-- 옵션 선택 시 자동으로 form이 submit 되도록 onchange 속성 추가 -->
            <select name="employeeId" class="select" onchange="this.form.submit()">
                <option value="">사원 선택</option>
                <c:forEach var="emp" items="${empList}">
                    <option value="${emp.employeeId}" <c:if test="${param.employeeId eq emp.employeeId}">selected</c:if>>
                        ${emp.employeeName} (No-${emp.employeeNo})
                    </option>
                </c:forEach>
            </select>
        </div>
        
    </form>
</section>

<!-- 조회 전 (데이터가 없을 때) 보여줄 껍데기 영역 -->
<c:if test="${empty retirePayslip}">
<section class="card ">
    <div class="card-header">
        <h2 class="section-title">퇴직급여 명세서</h2>
    </div>
    <div class="card-body">
        <div class="document-sheet">
            <h2 class="document-title">퇴 직 급 여 명 세 서</h2>
            <div class="table-wrap">
                <table class="data-table ">
                    <thead>
                        <tr>
                            <th>항목</th>
                            <th>내용</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td colspan="2" style="text-align: center; padding: 20px;">
                                조회된 명세서 데이터가 없습니다. 대상을 선택해주세요.
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            <div class="signature-area">
                <p>
                    <jsp:useBean id="nowEmpty" class="java.util.Date" />
                    <fmt:formatDate value="${nowEmpty}" pattern="yyyy년 MM월 dd일"/><br>
                </p>
                <div class="seal-box">직인</div>
            </div>
        </div>
    </div>
</section>
</c:if>

<!-- 조회 후 (데이터가 있을 때) 진짜 명세서 내용 출력 -->
<c:if test="${not empty retirePayslip}">
<section class="card ">
    <div class="card-header">
        <h2 class="section-title">퇴직급여 명세서</h2>
    </div>
    <div class="card-body">
        <div class="document-sheet">
            <h2 class="document-title">퇴 직 급 여 명 세 서</h2>
            <div class="table-wrap">
                <table class="data-table ">
                    <thead>
                        <tr>
                            <th>항목</th>
                            <th>내용</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>성명</td>
                            <td>${retirePayslip.employeeName}</td>
                        </tr>
                        <tr>
                            <td>입사일</td>
                            <td>${retirePayslip.hireDate}</td>
                        </tr>
                        <tr>
                            <td>퇴직일</td>
                            <td>${retirePayslip.resignDate}</td>
                        </tr>
                        <tr>
                            <td>재직일수</td>
                            <td><fmt:formatNumber value="${retirePayslip.serviceDays}" pattern="#,###"/>일</td>
                        </tr>
                        <tr>
                            <td>평균임금</td>
                            <td><fmt:formatNumber value="${retirePayslip.averageDailyWage}" pattern="#,###"/>원</td>
                        </tr>
                        <tr>
                            <td>퇴직급여</td>
                            <td><fmt:formatNumber value="${retirePayslip.retirementPayAmount}" pattern="#,###"/>원</td>
                        </tr>
                        <tr>
                            <td>실지급액</td>
                            <td><fmt:formatNumber value="${retirePayslip.retirementPayAmount}" pattern="#,###"/>원</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            <div class="signature-area">
                <p>
                    <jsp:useBean id="now" class="java.util.Date" />
                    <fmt:formatDate value="${now}" pattern="yyyy년 MM월 dd일"/><br>
                    <strong>${company.companyName}</strong>
                </p>
                <div class="seal-box">
                    <c:choose>
                        <c:when test="${not empty company.sealPath}">
                            <img src="${company.sealPath}" alt="회사 직인" style="max-width: 100%; max-height: 100%;">
                        </c:when>
                        <c:otherwise>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</section>
</c:if>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>