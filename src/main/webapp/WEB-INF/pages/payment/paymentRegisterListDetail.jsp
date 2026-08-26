<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>給与台帳 | HEXAGON PAY</title>
<%@ include file="../../jspf/head.jspf"%>
<style>
body { min-width: 1200px; background: #fff; }
.content-area { background: #fff; }

.prld-filter-bar { background: #e85c6b; padding: 10px 15px; border-radius: 4px; display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.prld-filter-bar select { padding: 4px 6px; font-size: 12px; border: none; border-radius: 3px; }

.prld-info-bar { background: #f4f6f9; border: 1px solid #ddd; padding: 10px 15px; display: flex; align-items: center; gap: 25px; margin-bottom: 15px; font-size: 13px; flex-wrap: wrap; }
.prld-info-bar .prld-static { display: inline-block; background: #fff; border: 1px solid #ccc; padding: 3px 8px; border-radius: 3px; min-width: 70px; text-align: center; }
.prld-info-bar label { display: flex; align-items: center; gap: 4px; }

.prld-table-wrap { border: 1px solid #ddd; }
.prld-table { border-collapse: collapse; font-size: 12px; white-space: nowrap; }
.prld-table th, .prld-table td { border: 1px solid #ddd; padding: 6px 8px; text-align: center; }
.prld-table thead th { background: #f4f6f9; color: #337ab7; }
.prld-table tbody tr:hover { background: #f8fafc; }
.prld-table .prld-num { text-align: right; }
.prld-table .prld-pay-total { background: #eaf2fb; color: #337ab7; font-weight: bold; }
.prld-table .prld-ded-total { background: #fdeeed; color: #d9534f; font-weight: bold; }
.prld-table .prld-net { background: #204d74; color: #fff; font-weight: bold; }
.prld-table tfoot td { background: #fcf8e3; font-weight: bold; }

#prldLongWrap { overflow-x: auto; max-width: 100%; }
#prldLongWrap .prld-table { min-width: 100%; }
.prld-sticky { position: sticky; background: #fff; z-index: 2; }
.prld-table thead .prld-sticky { z-index: 3; background: #f4f6f9; }

#prldShortWrap { overflow: auto; max-height: 640px; max-width: 100%; }
#prldShortWrap .prld-emp-block-end td { border-bottom: 2px solid #999; }

.prld-back-btn { display: inline-block; background: #d9d9d9; color: #333; font-size: 14px; font-weight: bold;
	padding: 10px 30px; border-radius: 20px; text-decoration: none; }
.prld-back-btn:hover { background: #c9c9c9; }
</style>
</head>
<body>
	<%@ include file="../../jspf/app-start.jspf"%>
	<%@ include file="../../jspf/sidebar.jspf"%>

	<main class="content-area">
		<div class="page-header" style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
			<img src="https://img.payzon.co.kr/_commonImg/pay_tit_img.gif" width="50" height="45" alt="給与台帳">
			<div>
				<h2 style="margin: 0; font-size: 20px; font-weight: bold;">給与台帳</h2>
				<p class="text-muted" style="margin: 3px 0 0 0; font-size: 12px; color: #666;">
					帰属年月別の給与総額と社員別の給与支給状況を確認できます。決裁欄を作成してご利用いただけます。
				</p>
			</div>
		</div>

		<form id="detailSearchForm" action="${pageContext.request.contextPath}/Payment/paymentRegisterListDetail.do" method="GET">
			<input type="hidden" name="payYear" value="${payYear}">
			<input type="hidden" name="payMonth" value="${payMonth}">
			<fmt:formatNumber value="${paySequence}" pattern="00" var="paySeqPadded" />
			<input type="hidden" name="paySequence" value="${paySeqPadded}">

			<div class="prld-filter-bar">
				<select name="empType" onchange="document.getElementById('detailSearchForm').submit()">
					<option value="">全体</option>
					<option value="정규직" <c:if test="${empType eq '정규직'}">selected</c:if>>正社員</option>
					<option value="계약직" <c:if test="${empType eq '계약직'}">selected</c:if>>契約社員</option>
					<option value="임시직" <c:if test="${empType eq '임시직'}">selected</c:if>>臨時職</option>
					<option value="파견직" <c:if test="${empType eq '파견직'}">selected</c:if>>派遣職</option>
					<option value="위촉직" <c:if test="${empType eq '위촉직'}">selected</c:if>>委嘱職</option>
					<option value="일용직" <c:if test="${empType eq '일용직'}">selected</c:if>>日雇い</option>
				</select>
				<select name="department" onchange="document.getElementById('detailSearchForm').submit()">
					<option value="">部署選択</option>
					<c:forEach var="d" items="${result.departmentList}">
						<option value="${d}" <c:if test="${department eq d}">selected</c:if>>${d}</option>
					</c:forEach>
				</select>
				<select name="incomeType" onchange="document.getElementById('detailSearchForm').submit()">
					<option value="">全体</option>
					<option value="근로소득자" <c:if test="${incomeType eq '근로소득자'}">selected</c:if>>勤労所得者</option>
					<option value="사업소득자" <c:if test="${incomeType eq '사업소득자'}">selected</c:if>>事業所得者</option>
					<option value="일용근로자" <c:if test="${incomeType eq '일용근로자'}">selected</c:if>>日雇い労働者</option>
				</select>
			</div>

			<div class="prld-info-bar">
				<label><strong>＊ 帰属年度</strong> <span class="prld-static">${payYear}年 ${payMonth}月</span></label>
				<label><strong>＊ 給与回</strong> <span class="prld-static">給与-${paySeqPadded}回</span></label>
				<label><strong>＊ 精算期間</strong> <span class="prld-static">${result.settlementStartDate}</span> ~ <span class="prld-static">${result.settlementEndDate}</span></label>
				<label><strong>＊ 支給日</strong> <span class="prld-static">${result.paymentDate}</span></label>
				<label style="margin-left: auto;">
					<strong>＊ 給与台帳様式</strong>
					<label><input type="radio" name="prldLayout" value="long" checked onclick="prldSwitchLayout('long')"> 長い横型</label>
					<label><input type="radio" name="prldLayout" value="short" onclick="prldSwitchLayout('short')"> 短い横型</label>
				</label>
			</div>
		</form>

		<c:set var="emptyList" value="${empty result.employeeList}" />

		<!-- ================= 긴 가로형 / 長い横型 ================= -->
		<div id="prldLongWrap" class="prld-table-wrap">
			<table class="prld-table">
				<thead>
					<tr>
						<th class="prld-sticky" style="left:0;">区分</th>
						<th class="prld-sticky" style="left:60px;">氏名</th>
						<th class="prld-sticky" style="left:130px;">入社日</th>
						<th class="prld-sticky" style="left:220px;">部署</th>
						<th class="prld-sticky" style="left:300px;">職位</th>
						<c:forEach var="item" items="${result.payItemList}"><th>${item.itemName}</th></c:forEach>
						<th>支給総額</th>
						<c:forEach var="item" items="${result.deductionItemList}"><th>${item.itemName}</th></c:forEach>
						<th>控除総額</th>
						<th>実支給額</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="emp" items="${result.employeeList}">
						<tr>
							<td class="prld-sticky" style="left:0;">${emp.employmentType}</td>
							<td class="prld-sticky" style="left:60px;">${emp.employeeName}</td>
							<td class="prld-sticky" style="left:130px;">${emp.hireDate}</td>
							<td class="prld-sticky" style="left:220px;">${emp.department}</td>
							<td class="prld-sticky" style="left:300px;">${emp.position}</td>
							<c:forEach var="item" items="${result.payItemList}">
								<td class="prld-num"><fmt:formatNumber value="${emp.getPayAmount(item.itemId)}" pattern="#,###" /></td>
							</c:forEach>
							<td class="prld-num prld-pay-total"><fmt:formatNumber value="${emp.totalPayAmount}" pattern="#,###" /></td>
							<c:forEach var="item" items="${result.deductionItemList}">
								<td class="prld-num"><fmt:formatNumber value="${emp.getDeductionAmount(item.itemId)}" pattern="#,###" /></td>
							</c:forEach>
							<td class="prld-num prld-ded-total"><fmt:formatNumber value="${emp.totalDeductionAmount}" pattern="#,###" /></td>
							<td class="prld-num prld-net"><fmt:formatNumber value="${emp.netPayAmount}" pattern="#,###" /></td>
						</tr>
					</c:forEach>
					<c:if test="${emptyList}">
						<tr>
							<td colspan="${8 + fn:length(result.payItemList) + fn:length(result.deductionItemList)}"
								style="padding: 30px; color: #666;">登録された社員データがありません。</td>
						</tr>
					</c:if>
				</tbody>
				<c:if test="${!emptyList}">
				<tfoot>
					<tr>
						<td class="prld-sticky" style="left:0;" colspan="5">合計</td>
						<c:forEach var="item" items="${result.payItemList}">
							<td class="prld-num"><fmt:formatNumber value="${result.totalRow.getPayAmount(item.itemId)}" pattern="#,###" /></td>
						</c:forEach>
						<td class="prld-num prld-pay-total"><fmt:formatNumber value="${result.totalRow.totalPayAmount}" pattern="#,###" /></td>
						<c:forEach var="item" items="${result.deductionItemList}">
							<td class="prld-num"><fmt:formatNumber value="${result.totalRow.getDeductionAmount(item.itemId)}" pattern="#,###" /></td>
						</c:forEach>
						<td class="prld-num prld-ded-total"><fmt:formatNumber value="${result.totalRow.totalDeductionAmount}" pattern="#,###" /></td>
						<td class="prld-num prld-net"><fmt:formatNumber value="${result.totalRow.netPayAmount}" pattern="#,###" /></td>
					</tr>
				</tfoot>
				</c:if>
			</table>
		</div>

		<!-- ================= 짧은 가로형 / 短い横型 ================= -->
		<div id="prldShortWrap" class="prld-table-wrap" style="display: none;">
			<table class="prld-table">
				<thead>
					<tr>
						<th>no.</th>
						<th>氏名</th>
						<c:forEach var="item" items="${result.payItemList}"><th>${item.itemName}</th></c:forEach>
						<th rowspan="3">支給総額</th>
						<c:forEach var="item" items="${result.deductionItemList}"><th>${item.itemName}</th></c:forEach>
						<th rowspan="3">控除総額</th>
						<th rowspan="3">実支給額</th>
					</tr>
					<tr>
						<th>区分</th>
						<th>入社日</th>
						<c:forEach var="item" items="${result.payItemList}"><th style="background:#fff;"></th></c:forEach>
						<c:forEach var="item" items="${result.deductionItemList}"><th style="background:#fff;"></th></c:forEach>
					</tr>
					<tr>
						<th>部署</th>
						<th>職位</th>
						<c:forEach var="item" items="${result.payItemList}"><th style="background:#fff;"></th></c:forEach>
						<c:forEach var="item" items="${result.deductionItemList}"><th style="background:#fff;"></th></c:forEach>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="emp" items="${result.employeeList}" varStatus="st">
						<tr>
							<td>${st.index + 1}</td>
							<td>${emp.employeeName}</td>
							<c:forEach var="item" items="${result.payItemList}">
								<td class="prld-num"><fmt:formatNumber value="${emp.getPayAmount(item.itemId)}" pattern="#,###" /></td>
							</c:forEach>
							<td rowspan="3" class="prld-num prld-pay-total"><fmt:formatNumber value="${emp.totalPayAmount}" pattern="#,###" /></td>
							<c:forEach var="item" items="${result.deductionItemList}">
								<td class="prld-num"><fmt:formatNumber value="${emp.getDeductionAmount(item.itemId)}" pattern="#,###" /></td>
							</c:forEach>
							<td rowspan="3" class="prld-num prld-ded-total"><fmt:formatNumber value="${emp.totalDeductionAmount}" pattern="#,###" /></td>
							<td rowspan="3" class="prld-num prld-net"><fmt:formatNumber value="${emp.netPayAmount}" pattern="#,###" /></td>
						</tr>
						<tr>
							<td>${emp.employmentType}</td>
							<td>${emp.hireDate}</td>
							<c:forEach var="i" begin="1" end="${fn:length(result.payItemList) + fn:length(result.deductionItemList)}"><td></td></c:forEach>
						</tr>
						<tr class="prld-emp-block-end">
							<td>${emp.department}</td>
							<td>${emp.position}</td>
							<c:forEach var="i" begin="1" end="${fn:length(result.payItemList) + fn:length(result.deductionItemList)}"><td></td></c:forEach>
						</tr>
					</c:forEach>
					<c:if test="${emptyList}">
						<tr>
							<td colspan="${6 + fn:length(result.payItemList) + fn:length(result.deductionItemList)}"
								style="padding: 30px; color: #666;">登録された社員データがありません。</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</div>
			<div style="text-align: center; margin-top: 20px;">
			<c:url var="prldListUrl" value="/Payment/paymentRegisterList.do">
				<c:param name="payYear" value="${payYear}" />
			</c:url>
			<a href="${prldListUrl}" class="prld-back-btn">全体一覧を見る</a>
		</div>
	</main>

	<%@ include file="../../jspf/app-end.jspf"%>

	<script>
	function prldSwitchLayout(mode) {
	    document.getElementById("prldLongWrap").style.display = (mode === "long") ? "" : "none";
	    document.getElementById("prldShortWrap").style.display = (mode === "short") ? "" : "none";
	}
	</script>
</body>
</html>
