<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.NonTaxDetail"%>
<%-- 비과세 상세 코드를 고르는 팝업 화면이다. --%>
<%-- 非課税詳細コードを選ぶポップアップ画面である。 --%>
<%
// 팝업은 head.jspf 를 안 쓰므로 ctx 를 여기서 만든다
// ポップアップは head.jspf を使わないので ctx をここで作る。
String ctx = request.getContextPath();
List<NonTaxDetail> nonTaxDetailList = (List<NonTaxDetail>) request.getAttribute("nonTaxDetailList");
%>
<!doctype html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<%-- 일본어 제목이다. --%>
<%-- 日本語のタイトルである。 --%>
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
	<%-- 비과세 코드 목록과 직접입력 버튼 영역이다. --%>
	<%-- 非課税コード一覧と直接入力ボタン領域である。 --%>
	<section class="source-config-block">
		<div class="source-config-list">
			<div class="popup-section-head">
				<div class="source-section-title">非課税および減免所得コード</div>
				<%-- 목록을 고르지 않고 항목명·한도를 직접 적게 한다. --%>
				<%-- 一覧を選ばず項目名・限度を直接入力させる。 --%>
				<button type="button" class="btn btn-primary"
					onclick="manualNonTaxInput()">直接入力</button>
			</div>
			<%-- 비과세 상세 목록 테이블이다. --%>
			<%-- 非課税詳細一覧テーブルである。 --%>
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
						int nonTaxRowCount = 0;
						if (nonTaxDetailList != null) {
							for (NonTaxDetail item : nonTaxDetailList) {
								nonTaxRowCount++;
								String category = item.getNonTaxCategory() != null ? item.getNonTaxCategory() : "";
								String limitLabel = item.getLimitAmountLabel();
								String categoryAttr = category.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
								String limitAttr = limitLabel.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
						%>
						<%-- 행 클릭으로 부모 폼에 반영한다. --%>
						<%-- 行クリックで親フォームに反映する。 --%>
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
						if (nonTaxRowCount == 0) {
						%>
						<tr>
							<td colspan="6">表示できる非課税項目がありません。</td>
						</tr>
						<%
						}
						%>
					</tbody>
				</table>
			</div>
		</div>
	</section>
	<script>
		// 선택한 비과세 행을 부모 창 폼에 반영한다
		// 選択した非課税行を親ウィンドウのフォームに反映する。
		function selectNonTaxDetail(row) {
			// 부모 창이 살아 있고 콜백이 있는지 본다
			// 親ウィンドウが生きていてコールバックがあるかを見る。
			if (!window.opener || window.opener.closed
					|| typeof window.opener.applyNonTaxDetail !== 'function') {
				window.close();
				return;
			}
			// 행 data 속성을 객체로 넘긴다
			// 行の data 属性をオブジェクトとして渡す。
			window.opener.applyNonTaxDetail({
				nonTaxId : row.getAttribute('data-non-tax-id'),
				nonTaxCategory : row.getAttribute('data-non-tax-category')
						|| '',
				limitAmountLabel : row.getAttribute('data-limit-amount-label')
						|| '',
				manual : false
			});
			// 값 전달이 끝나면 팝업을 닫는다
			// 値の受け渡しが終わったらポップアップを閉じる。
			window.close();
		}

		// 목록 PK 없이 부모 폼을 직접 입력 가능하게 한다
		// 一覧PKなしで親フォームを直接入力可能にする。
		function manualNonTaxInput() {
			// 부모에 직접입력 함수가 있으면 먼저 호출한다
			// 親に直接入力関数があれば先に呼び出す。
			if (window.opener
					&& !window.opener.closed
					&& typeof window.opener.enableManualNonTaxInput === 'function') {
				window.opener.enableManualNonTaxInput();
			}
			// 콜백 유무와 관계없이 팝업을 닫는다
			// コールバックの有無に関係なくポップアップを閉じる。
			window.close();
		}
	</script>
</body>
</html>
