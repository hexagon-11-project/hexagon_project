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

		<form id="searchForm" action="${pageContext.request.contextPath}/Payment/paymentRegisterList.do" method="GET">
			<div style="background: #fff; border-bottom: 1px solid #ddd; padding: 10px 5px; display: flex; align-items: center; gap: 10px; margin-bottom: 15px; font-size: 13px;">
				<strong>＊ 帰属年度</strong>
				<select name="payYear" id="payYear" class="form-control input-sm" style="display: inline-block; width: 90px; padding: 3px 5px;" onchange="reloadPayrollData()">
					<c:forEach var="year" begin="2005" end="2030">
						<option value="${year}" <c:if test="${payYear eq year}">selected</c:if>>${year}年</option>
					</c:forEach>
				</select>
				<span style="color: #666;">帰属年度を選択し、給与回をクリックすると詳細内訳を確認できます。</span>
			</div>
		</form>

		<table class="prl-table">
			<thead>
				<tr>
					<th>帰属年月</th>
					<th>給与回</th>
					<th>精算期間</th>
					<th>支給日</th>
					<th>人員</th>
					<th>支給総額</th>
					<th>控除総額</th>
					<th>実支給額</th>
					<th>削除</th>
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
								<fmt:formatNumber value="${row.paySequence}" pattern="'給与-'00'回'" />
							</a>
						</td>
						<td>${row.settlementStartDate} ~ ${row.settlementEndDate}</td>
						<td>${row.paymentDate}</td>
						<td>${row.employeeCount}</td>
						<td style="text-align: right; color: #337ab7;"><fmt:formatNumber value="${row.totalPayAmount}" pattern="#,###" /></td>
						<td style="text-align: right; color: #d9534f;"><fmt:formatNumber value="${row.totalDeductionAmount}" pattern="#,###" /></td>
						<td style="text-align: right;"><fmt:formatNumber value="${row.netPayAmount}" pattern="#,###" /></td>
						<td><button type="button" class="prl-btn-del" onclick="event.stopPropagation(); deletePayroll(this)">✕ 削除</button></td>
					</tr>
				</c:forEach>
				<tr class="prl-total-row">
					<td colspan="4">合計</td>
					<td></td>
					<td id="prlTotalPay" style="text-align: right; color: #337ab7;"><fmt:formatNumber value="${totalPay}" pattern="#,###" /></td>
					<td id="prlTotalDed" style="text-align: right; color: #d9534f;"><fmt:formatNumber value="${totalDeduction}" pattern="#,###" /></td>
					<td id="prlTotalNet" style="text-align: right;"><fmt:formatNumber value="${totalNet}" pattern="#,###" /></td>
					<td></td>
				</tr>
			</tbody>
		</table>
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
	    if (!payrollId) { alert("登録された給与データがなく、削除する項目がありません。"); return; }

	    var noticeMsg = "[必読] - [削除機能]\n\n選択した給与回に該当する\n\n給与データがすべて削除されます。\n\n"
	                   + "削除された給与台帳および給与データは\n\n復元できませんので、再度ご確認の上削除してください。";
	    if (!confirm(noticeMsg)) return;
	    if (!confirm("[警告] 本当に削除しますか？")) return;

	    var formData = new URLSearchParams();
	    formData.append("payrollId", payrollId);

	    fetch(CTX + "/Payment/paymentRegisterListDelete.do", {
	        method: "POST",
	        headers: { "Content-Type": "application/x-www-form-urlencoded" },
	        body: formData.toString()
	    }).then(function (res) { return res.text(); })
	      .then(function (result) {
	          if (result === "SUCCESS") {
	              alert("削除されました。");
	              removeRowAndRecalcTotals(tr);
	          } else {
	              alert("削除中に問題が発生しました。");
	          }
	      })
	      .catch(function () { alert("サーバー通信に失敗しました。"); });
	}

	// 삭제된 급여차수는 달력처럼 항상 채워지는 목록이라 새로고침해도 그 자리에 빈 줄로 다시 나타나므로, / 削除された給与回はカレンダーのように常に埋まる一覧なので、再読み込みしてもその場所に空行として再び現れるため、
	// 화면에서 줄 자체를 완전히 지우고 합계도 즉시 다시 계산한다. / 画面上で行そのものを完全に削除し、合計も即座に再計算する。
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
