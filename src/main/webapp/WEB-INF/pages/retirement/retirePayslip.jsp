<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
request.setAttribute("pageTitle", "退職給与明細書");
request.setAttribute("pageSection", "退職管理");
request.setAttribute("pageDescription", "退職給与の支給額と実支給額を明細書形式で確認します。");
request.setAttribute("activeKey", "retirement-slip");
request.setAttribute("pageCss", "retirement.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<section class="filter-bar">
	<form
		action="${pageContext.request.contextPath}/Retire/retirePayslip.do"
		method="get"
		style="display: flex; gap: 10px; width: 100%; align-items: flex-end;">

		<div class="field ">
			<label>社員</label>
			<!-- 옵션 선택 시 자동으로 form이 submit 되도록 onchange 속성 추가 -->
			<!-- オプション選択時に自動でフォームがsubmitされるようonchange属性を追加 -->
			<select name="employeeId" class="select"
				onchange="this.form.submit()">
				<option value="">社員選択</option>
				<c:forEach var="emp" items="${empList}">
					<option value="${emp.employeeId}"
						<c:if test="${param.employeeId eq emp.employeeId}">selected</c:if>>
						${emp.employeeName} (${emp.employeeNo})</option>
				</c:forEach>
			</select>
		</div>

	</form>
</section>

<!-- 조회 전 (데이터가 없을 때) -->
<!-- 照会前 (データがない時) -->
<c:if test="${empty retirePayslip}">
	<section class="card ">
		<div class="card-header">
			<h2 class="section-title">退職給与明細書</h2>
		</div>
		<div class="card-body">
			<div class="document-sheet">
				<h2 class="document-title">退 職 給 与 明 細 書</h2>
				<div class="table-wrap">
					<table class="data-table ">
						<thead>
							<tr>
								<th>項目</th>
								<th>内容</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td colspan="2" style="text-align: center; padding: 20px;">
									照会された明細書データがありません。対象を選択してください。</td>
							</tr>
						</tbody>
					</table>
				</div>

				<div class="signature-area"
					style="display: flex; align-items: center; justify-content: flex-end; gap: 10px;">
					<p style="margin: 0; text-align: right;">
						<jsp:useBean id="nowEmpty" class="java.util.Date" />
						<fmt:formatDate value="${nowEmpty}" pattern="yyyy年 MM月 dd日" />
						<br> <strong>(주)핵사곤테크</strong>
					</p>
					<div class="seal-box">
						<img
							src="${pageContext.request.contextPath}/assets/images/Seal.png"
							alt="職印" style="width: 60px; height: 60px;">
					</div>
				</div>
			</div>
		</div>
	</section>
</c:if>

<!-- 조회 후 (데이터가 있을 때) 진짜 명세서 내용 출력 -->
<!-- 照会後 (データがある時) の実際の明細書内容の出力 -->
<c:if test="${not empty retirePayslip}">
	<section class="card ">
		<div class="card-header">
			<h2 class="section-title">退職給与明細書</h2>
		</div>
		<div class="card-body">
			<div class="document-sheet">
				<h2 class="document-title">退 職 給 与 明 細 書</h2>
				<div class="table-wrap">
					<table class="data-table ">
						<thead>
							<tr>
								<th>項目</th>
								<th>内容</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>氏名</td>
								<td>${retirePayslip.employeeName}</td>
							</tr>
							<tr>
								<td>入社日</td>
								<td>${retirePayslip.hireDate}</td>
							</tr>
							<tr>
								<td>退職日</td>
								<td>${retirePayslip.resignDate}</td>
							</tr>
							<tr>
								<td>在職日数</td>
								<td><fmt:formatNumber value="${retirePayslip.serviceDays}"
										pattern="#,###" />日</td>
							</tr>
							<tr>
								<td>平均賃金</td>
								<td><fmt:formatNumber
										value="${retirePayslip.averageDailyWage}" pattern="#,###" />ウォン</td>
							</tr>
							<tr>
								<td>退職給与</td>
								<td><fmt:formatNumber
										value="${retirePayslip.retirementPayAmount}" pattern="#,###" />ウォン</td>
							</tr>
							<tr>
								<td>実支給額</td>
								<td><fmt:formatNumber
										value="${retirePayslip.retirementPayAmount}" pattern="#,###" />ウォン</td>
							</tr>
						</tbody>
					</table>
				</div>

				<!-- 조회 후 (데이터가 있을 때)  명세서 내용 출력 -->
<!-- 照会後 (データがある時) の明細書内容の出力 -->
<div class="signature-area"
    style="display: flex; align-items: center; justify-content: flex-end; gap: 10px;">
    <p style="margin: 0; text-align: right;">
        <jsp:useBean id="now" class="java.util.Date" />
        <fmt:formatDate value="${now}" pattern="yyyy年 MM月 dd日" />
        <br> <strong>${not empty company.companyName ? company.companyName : '(주)핵사곤테크'}</strong>
    </p>
    <div class="seal-box">
        <c:choose>
            <c:when test="${not empty company.sealPath}">
                <img src="${company.sealPath}" alt="会社職印"
                    style="max-width: 100%; max-height: 100%;">
            </c:when>
            <c:otherwise>
                <!-- DB에 등록된 회사 직인이 없을 경우 기본 이미지 출력 -->
                <!-- DBに登録された会社職印がない場合、基本画像を出力 -->
                <img
                    src="${pageContext.request.contextPath}/assets/images/Seal.png"
                    alt="職印" style="width: 60px; height: 60px;">
            </c:otherwise>
        </c:choose>
    </div>
</div>
			</div>
		</div>
	</section>
</c:if>

<%@ include file="/WEB-INF/jspf/app-end.jspf"%>