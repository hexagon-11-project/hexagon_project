<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.NonTaxDetail"%>
<%
String ctx = request.getContextPath();
List<NonTaxDetail> nonTaxDetailList = (List<NonTaxDetail>) request.getAttribute("nonTaxDetailList");
%>
<!doctype html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>非課税および減免所得コード | HEXAGON PAY</title>
<link rel="stylesheet" href="<%=ctx%>/assets/css/base/variables.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/base/reset.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/base/typography.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/components/buttons.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/components/tables.css">
<link rel="stylesheet"
	href="<%=ctx%>/assets/css/pages/source-faithful.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/pages/environment.css">
<style>
body {
	margin: 0;
	padding: 16px;
	background: #fff;
}

.popup-section-head {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 10px;
	padding: 9px 11px;
	border-top: 2px solid #3f8fc4;
	border-bottom: 1px solid var(- -line);
}

.popup-section-head .source-section-title {
	padding: 0;
	border: 0;
}
</style>
</head>
<body>
	<section class="source-config-block">
		<div class="source-config-list">
			<div class="popup-section-head">
				<div class="source-section-title">非課税および減免所得コード</div>
				<button type="button" class="btn btn-primary"
					onclick="manualNonTaxInput()">直接入力</button>
			</div>
			<div class="table-wrap">
				<table class="data-table source-data-table">
					<thead>
						<tr>
							<th>法条文</th>
							<th>コード</th>
							<th>記載欄</th>
							<th>非課税項目</th>
							<th>限度額</th>
							<th>支払明細書を作成</th>
						</tr>
					</thead>
					<tbody>
						<%
						if (nonTaxDetailList != null) {
							for (NonTaxDetail item : nonTaxDetailList) {
								String category = item.getNonTaxCategory() != null ? item.getNonTaxCategory() : "";
								String limitLabel = item.getLimitAmountLabel();
								String categoryAttr = category.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
								String limitAttr = limitLabel.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
						%>
						<tr style="cursor: pointer;" onclick="selectNonTaxDetail(this)"
							data-non-tax-id="<%=item.getNonTaxId()%>"
							data-non-tax-category="<%=categoryAttr%>"
							data-limit-amount-label="<%=limitAttr%>">
							<td><%=item.getLegalProvision() != null ? item.getLegalProvision() : ""%></td>
							<td><%=item.getLegalCode() != null ? item.getLegalCode() : ""%></td>
							<td><%=item.getNonTaxNote() != null ? item.getNonTaxNote() : ""%></td>
							<td><%=category%></td>
							<td><%=limitLabel%></td>
							<td><%=item.getStatementPayment() != null ? item.getStatementPayment() : ""%></td>
						</tr>
						<%
						}
						}
						%>
					</tbody>
				</table>
			</div>
		</div>
	</section>
	<script>
		function selectNonTaxDetail(row) {
			if (!window.opener || window.opener.closed
					|| typeof window.opener.applyNonTaxDetail !== 'function') {
				window.close();
				return;
			}
			window.opener.applyNonTaxDetail({
				nonTaxId : row.getAttribute('data-non-tax-id'),
				nonTaxCategory : row.getAttribute('data-non-tax-category')
						|| '',
				limitAmountLabel : row.getAttribute('data-limit-amount-label')
						|| '',
				manual : false
			});
			window.close();
		}

		function manualNonTaxInput() {
			if (window.opener
					&& !window.opener.closed
					&& typeof window.opener.enableManualNonTaxInput === 'function') {
				window.opener.enableManualNonTaxInput();
			}
			window.close();
		}
	</script>
</body>
</html>
