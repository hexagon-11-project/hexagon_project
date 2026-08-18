<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>급여대장 | HEXAGON PAY</title>
<%@ include file="../../jspf/head.jspf"%>
<style>
body { min-width: 1200px; background: #fff; }
.content-area { background: #fff; }

.prl-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.prl-table th { background: #f4f6f9; color: #337ab7; border: 1px solid #ddd; padding: 8px 6px; text-align: center; }
.prl-table td { border: 1px solid #ddd; padding: 8px 6px; text-align: center; }
.prl-table tbody tr.prl-row { cursor: pointer; }
.prl-table tbody tr.prl-row:hover { background: #e8f1fb; }
.prl-table a.seq-link { color: #337ab7; font-weight: bold; text-decoration: none; }
.prl-table a.seq-link:hover { text-decoration: underline; }
.prl-table tr.prl-total-row td { background: #fcf8e3; font-weight: bold; }
.prl-btn-del { background: #fff; border: 1px solid #ccc; color: #d9534f; padding: 3px 10px; font-size: 12px; border-radius: 3px; cursor: pointer; }
.prl-btn-del:hover { background: #fdf3f2; }

.prl-pagination { display: flex; justify-content: center; align-items: center; gap: 15px; margin-top: 15px; font-size: 13px; }
.prl-pagination a { color: #337ab7; text-decoration: none; }
.prl-pagination .prl-page-num { display: inline-block; min-width: 22px; text-align: center; padding: 2px 6px; border: 1px solid #337ab7; border-radius: 3px; color: #337ab7; font-weight: bold; }
</style>
</head>
<body>
	<%@ include file="../../jspf/app-start.jspf"%>
	<%@ include file="../../jspf/sidebar.jspf"%>

	<main class="content-area">
		<div class="page-header" style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
			<img src="https://img.payzon.co.kr/_commonImg/pay_tit_img.gif" width="50" height="45" alt="급여대장">
			<div>
				<h2 style="margin: 0; font-size: 20px; font-weight: bold;">급여대장</h2>
				<p class="text-muted" style="margin: 3px 0 0 0; font-size: 12px; color: #666;">
					귀속연월별 급여총액과 사원별 급여지급 현황을 보실 수 있습니다. 결재란을 만들어 사용할 수 있습니다.
				</p>
			</div>
		</div>

		<form id="searchForm" action="${pageContext.request.contextPath}/Payment/paymentRegisterList.do" method="GET">
			<div style="background: #fff; border-bottom: 1px solid #ddd; padding: 10px 5px; display: flex; align-items: center; gap: 10px; margin-bottom: 15px; font-size: 13px;">
				<strong>＊ 귀속연도</strong>
				<select name="payYear" id="payYear" class="form-control input-sm" style="display: inline-block; width: 90px; padding: 3px 5px;" onchange="reloadPayrollData()">
					<c:forEach var="year" begin="2005" end="2030">
						<option value="${year}" <c:if test="${payYear eq year}">selected</c:if>>${year} 년</option>
					</c:forEach>
				</select>
				<span style="color: #666;">귀속연도를 선택하시고 급여차수를 클릭하시면 상세내역을 확인하실 수 있습니다.</span>
			</div>
		</form>

		<table class="prl-table">
			<thead>
				<tr>
					<th>귀속연월</th>
					<th>급여차수</th>
					<th>정산기간</th>
					<th>지급일</th>
					<th>인원</th>
					<th>지급총액</th>
					<th>공제총액</th>
					<th>실지급액</th>
					<th>삭제</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="row" items="${payrollList}">
					<fmt:formatNumber value="${row.paySequence}" pattern="00" var="paySeqPadded" />
					<c:url var="rowDetailUrl" value="/Payment/paymentRegisterListDetail.do">
						<c:param name="payYear" value="${fn:substring(row.payYearMonth, 0, 4)}" />
						<c:param name="payMonth" value="${fn:substring(row.payYearMonth, 4, 6)}" />
						<c:param name="paySequence" value="${paySeqPadded}" />
					</c:url>
					<tr class="prl-row" data-payroll-id="${row.payrollId}" data-href="${rowDetailUrl}"
						data-pay="${row.totalPayAmount}" data-ded="${row.totalDeductionAmount}" data-net="${row.netPayAmount}"
						onclick="goRowDetail(event, this)">
						<td>${fn:substring(row.payYearMonth, 0, 4)}-${fn:substring(row.payYearMonth, 4, 6)}</td>
						<td>
							<a class="seq-link" href="${rowDetailUrl}">
								<fmt:formatNumber value="${row.paySequence}" pattern="'급여-'00'차'" />
							</a>
						</td>
						<td>${row.settlementStartDate} ~ ${row.settlementEndDate}</td>
						<td>${row.paymentDate}</td>
						<td>${row.employeeCount}</td>
						<td style="text-align: right; color: #337ab7;"><fmt:formatNumber value="${row.totalPayAmount}" pattern="#,###" /></td>
						<td style="text-align: right; color: #d9534f;"><fmt:formatNumber value="${row.totalDeductionAmount}" pattern="#,###" /></td>
						<td style="text-align: right;"><fmt:formatNumber value="${row.netPayAmount}" pattern="#,###" /></td>
						<td><button type="button" class="prl-btn-del" onclick="event.stopPropagation(); deletePayroll(this)">✕ 삭제</button></td>
					</tr>
				</c:forEach>
				<tr class="prl-total-row">
					<td colspan="4">합계</td>
					<td></td>
					<td id="prlTotalPay" style="text-align: right; color: #337ab7;"><fmt:formatNumber value="${totalPay}" pattern="#,###" /></td>
					<td id="prlTotalDed" style="text-align: right; color: #d9534f;"><fmt:formatNumber value="${totalDeduction}" pattern="#,###" /></td>
					<td id="prlTotalNet" style="text-align: right;"><fmt:formatNumber value="${totalNet}" pattern="#,###" /></td>
					<td></td>
				</tr>
			</tbody>
		</table>

		<div class="prl-pagination">
			<a href="javascript:void(0);">‹ 이전페이지</a>
			<span class="prl-page-num">1</span>
			<a href="javascript:void(0);">다음페이지 ›</a>
		</div>
	</main>

	<%@ include file="../../jspf/app-end.jspf"%>

	<script>
	var CTX = "${pageContext.request.contextPath}";

	function reloadPayrollData() {
	    document.getElementById("searchForm").submit();
	}

	function goRowDetail(evt, tr) {
	    if (evt.target.closest("a, button")) return;
	    var href = tr.getAttribute("data-href");
	    if (href) { location.href = href; }
	}

	function deletePayroll(btn) {
	    var tr = btn.closest("tr");
	    var payrollId = tr.getAttribute("data-payroll-id");
	    if (!payrollId) { alert("등록된 급여 데이터가 없어 삭제할 항목이 없습니다."); return; }

	    var noticeMsg = "[필독] - [삭제기능]\n\n선택하신 급여차수에 해당하는\n\n급여데이터가 전부 삭제됩니다.\n\n"
	                   + "삭제된 급여대장 및 급여데이터는\n\n복구가 불가능 하오니 다시한번 확인하시고 삭제해주세요.";
	    if (!confirm(noticeMsg)) return;
	    if (!confirm("[경고] 정말 삭제하시겠습니까?")) return;

	    var formData = new URLSearchParams();
	    formData.append("payrollId", payrollId);

	    fetch(CTX + "/Payment/paymentRegisterListDelete.do", {
	        method: "POST",
	        headers: { "Content-Type": "application/x-www-form-urlencoded" },
	        body: formData.toString()
	    }).then(function (res) { return res.text(); })
	      .then(function (result) {
	          if (result === "SUCCESS") {
	              alert("삭제 되었습니다.");
	              removeRowAndRecalcTotals(tr);
	          } else {
	              alert("삭제 중 문제가 발생했습니다.");
	          }
	      })
	      .catch(function () { alert("서버 통신에 실패했습니다."); });
	}

	// 삭제된 급여차수는 달력처럼 항상 채워지는 목록이라 새로고침해도 그 자리에 빈 줄로 다시 나타나므로,
	// 화면에서 줄 자체를 완전히 지우고 합계도 즉시 다시 계산한다.
	function removeRowAndRecalcTotals(tr) {
	    tr.parentNode.removeChild(tr);

	    var rows = document.querySelectorAll(".prl-row");
	    var totalPay = 0, totalDed = 0, totalNet = 0;
	    rows.forEach(function (row) {
	        totalPay += Number(row.getAttribute("data-pay")) || 0;
	        totalDed += Number(row.getAttribute("data-ded")) || 0;
	        totalNet += Number(row.getAttribute("data-net")) || 0;
	    });

	    document.getElementById("prlTotalPay").textContent = totalPay.toLocaleString("en-US");
	    document.getElementById("prlTotalDed").textContent = totalDed.toLocaleString("en-US");
	    document.getElementById("prlTotalNet").textContent = totalNet.toLocaleString("en-US");
	}
	</script>
</body>
</html>
