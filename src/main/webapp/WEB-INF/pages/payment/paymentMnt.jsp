<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
// ★ 공통 상단(app-start.jspf)이 pageSection/pageTitle/pageDescription을 그대로 출력하는데,
//   이 값들을 안 채워두면 화면 위쪽에 "null"이 그대로 보이므로 미리 채워둔다.
// ★共通ヘッダー（app-start.jspf）がpageSection/pageTitle/pageDescriptionをそのまま出力するため、
//   これらの値を埋めておかないと画面上部に「null」がそのまま表示されるため、あらかじめ設定しておく。
request.setAttribute("pageSection", "給与管理");
request.setAttribute("pageTitle", "給与入力・管理");
request.setAttribute("pageDescription", "月別、社員別に給与および賞与情報を入力・保存・管理するメニューです。帰属年月、給与回をご確認ください！！");
%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>給与入力・管理 | HEXAGON PAY</title>
<%@ include file="../../jspf/head.jspf"%>
<style>
/* ★ 공통 상단(app-start.jspf)의 제목 영역은 이 화면 자체의 아이콘 제목과 중복되므로 숨긴다 / ★共通ヘッダー（app-start.jspf）のタイトル領域はこの画面自体のアイコン付きタイトルと重複するため非表示にする */
.page-heading {
	display: none;
}
/* 화면이 좁아져도 좌우 영역이 찌그러지거나 아래로 떨어지지 않도록 고정하는 스타일 / 画面が狭くなっても左右の領域が崩れたり下に落ちたりしないように固定するスタイル */
body {
	min-width: 1200px; /* 화면 전체의 최소 가로폭을 강제로 잡아줍니다 / 画面全体の最小横幅を強制的に固定します */
}
/* 계산방법 off 시 계산방법 입력 행 숨기기 / 計算方法offの時、計算方法入力行を非表示にする */
.calc-method-row {
	display: table-row;
}

.calc-method-row.off {
	display: none;
}

/* 스위치 토글 디자인 / スイッチトグルデザイン */
.switch-wrap {
	display: flex;
	align-items: center;
	gap: 6px;
	background: #fff;
	padding: 3px 8px;
	border-radius: 3px;
	cursor: pointer;
	user-select: none;
}

.switch-btn {
	background: #28a745;
	color: white;
	padding: 1px 10px;
	border-radius: 12px;
	font-weight: bold;
	font-size: 11px;
	display: inline-block;
	transition: background 0.2s;
}

.switch-btn.off {
	background: #6c757d;
}
</style>
</head>
<body>
	<%@ include file="../../jspf/app-start.jspf"%>
	<%@ include file="../../jspf/sidebar.jspf"%>

	<main class="content-area">
		<!-- 화면 상단 타이틀 영역 / 画面上部タイトル領域 -->
		<div class="page-header"
			style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
			<img src="https://img.payzon.co.kr/_commonImg/pay_tit_img.gif"
				width="50" height="45" alt="給与入力および管理">
			<div>
				<h2 style="margin: 0; font-size: 20px; font-weight: bold;">給与入力・管理</h2>
				<p class="text-muted"
					style="margin: 3px 0 0 0; font-size: 12px; color: #666;">
					月別、社員別に給与および賞与情報を入力・保存・管理するメニューです。 <span
						style="color: #d9534f; font-weight: bold;">帰属年月、給与回を
						ご確認ください！！</span>
				</p>
			</div>
		</div>

		<!-- 1. 현재 시스템의 연도와 월을 구합니다 / 1. 現在のシステムの年度と月を取得します -->
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate value="${now}" pattern="yyyy" var="currentYear" />
<fmt:formatDate value="${now}" pattern="MM" var="currentMonth" />

<!-- 2. 현재 월에서 1을 뺀 전월(prev)을 계산합니다 / 2. 現在の月から1を引いた前月(prev)を計算します -->
<c:set var="prevMonth" value="${currentMonth - 1}" />
<c:set var="prevYear" value="${currentYear}" />

<c:if test="${prevMonth == 0}">
    <c:set var="prevMonth" value="12" />
    <c:set var="prevYear" value="${currentYear - 1}" />
</c:if>

<fmt:formatNumber value="${prevMonth}" pattern="00" var="formattedPrevMonth" />

<!-- 3. 파라미터가 없으면 기본값으로 '전월'을 사용합니다 / 3. パラメータがなければ初期値として「前月」を使用します -->
<c:set var="selectedYear" value="${not empty param.payYear ? param.payYear : prevYear}" />
<c:set var="selectedMonth" value="${not empty param.payMonth ? param.payMonth : formattedPrevMonth}" />

		<form id="searchForm"
		action="${pageContext.request.contextPath}/Payment/paymentMnt.do"
		method="GET">
		<input type="hidden" name="incomeType" id="incomeTypeParam"
			value="${not empty param.incomeType ? param.incomeType : 'GENERAL'}">
		<!-- ★ 추가 : 현재 귀속연월+차수의 정확한 PAYROLL_ID (신규 사원 추가 시 JS가 이 값을 사용) / ★追加：現在の帰属年月＋回の正確なPAYROLL_ID（新規社員追加時にJSがこの値を使用） -->
		<input type="hidden" name="payrollId" id="payrollId" value="${payrollId}">
			<div
				style="background: #d9534f; padding: 10px 15px; border-radius: 4px; display: flex; align-items: center; justify-content: space-between; color: white; margin-bottom: 15px; font-size: 13px;">
				<!-- 왼쪽 그룹 / 左側グループ -->
				<div style="display: flex; align-items: center; gap: 15px;">
					<div style="display: flex; align-items: center; gap: 5px;">
						<strong>* 帰属年月</strong>&nbsp;

						<!-- 연도 Select Box / 年度セレクトボックス -->
						<select name="payYear" id="payYear" class="form-control input-sm"
							style="display: inline-block; width: 80px; background: #fff; color: #333; padding: 3px 5px;"
							onchange="reloadPayrollData()">
							<c:forEach var="year" begin="2005" end="2027">
								<option value="${year}"
									<c:if test="${selectedYear eq year}">selected</c:if>>${year}年</option>
							</c:forEach>
						</select>&nbsp;

						<!-- 월 Select Box / 月セレクトボックス -->
						<select name="payMonth" id="payMonth"
							class="form-control input-sm"
							style="display: inline-block; width: 65px; background: #fff; color: #333; padding: 3px 5px;"
							onchange="reloadPayrollData()">
							<c:forEach var="month" begin="1" end="12">
								<fmt:formatNumber value="${month}" pattern="00"
									var="formattedMonth" />
								<option value="${formattedMonth}"
									<c:if test="${selectedMonth eq formattedMonth}">selected</c:if>>${formattedMonth}月</option>
							</c:forEach>
						</select>
					</div>

					<div style="display: flex; align-items: center; gap: 5px;">
						<strong>* 給与回</strong>&nbsp;
						<!-- 급여차수 Select Box (1차 ~ 10차 자동 생성) / 給与回セレクトボックス（1回～10回を自動生成） -->
						<select name="paySequence" id="paySequence"
							class="form-control input-sm"
							style="display: inline-block; width: 90px; background: #fff; color: #333; padding: 3px 5px;"
							onchange="reloadPayrollData()">
							<c:forEach var="seq" begin="1" end="10">
								<fmt:formatNumber value="${seq}" pattern="00" var="formattedSeq" />
								<option value="${formattedSeq}"
									<c:if test="${(empty param.paySequence and formattedSeq eq '01') or (param.paySequence eq formattedSeq)}">selected</c:if>>給与-${formattedSeq}回</option>
							</c:forEach>
						</select>
					</div>
				</div>

				<!-- 중간 그룹 / 中間グループ -->
				<div style="display: flex; align-items: center; gap: 15px;">
					<div>
						<strong>* 精算期間</strong>&nbsp;
						<!-- id="calcPeriodStart" 와 id="calcPeriodEnd" 추가 / id="calcPeriodStart"とid="calcPeriodEnd"を追加 -->
						<input type="text" id="calcPeriodStart"
							value="${payrollInfo.calcPeriodStart}" readonly
							class="form-control input-sm"
							style="display: inline-block; width: 95px; background: #fff; color: #333; padding: 3px 5px; text-align: center;">
						~&nbsp; <input type="text" id="calcPeriodEnd"
							value="${payrollInfo.calcPeriodEnd}" readonly
							class="form-control input-sm"
							style="display: inline-block; width: 95px; background: #fff; color: #333; padding: 3px 5px; text-align: center;">
					</div>
					<div>
						<strong>* 給与支給日</strong>&nbsp;
						<!-- id="payDate" 추가 / id="payDate"を追加 -->
						<input type="text" id="payDate" value="${payrollInfo.payDate}"
							readonly class="form-control input-sm"
							style="display: inline-block; width: 95px; background: #fff; color: #333; padding: 3px 5px; text-align: center;">
					<!-- 	[수정] 버튼 임시 주석 처리 / [修正]ボタンを一時的にコメントアウト
						<button type="button" class="btn btn-default btn-xs"
							style="padding: 3px 6px; background: #fff; border: 1px solid #ccc; color: #333; margin-left: 2px;">
							<i class="fas fa-sync-alt"></i> 수정
						</button> -->
					</div>
				</div>

				<!-- 오른쪽 그룹 (계산방법) / 右側グループ（計算方法） -->
				<div class="switch-wrap" onclick="toggleCalcMethod()">
					<strong style="font-size: 12px; color: #333;">* 計算方法</strong> <span
						id="calcSwitchBadge" class="switch-btn">on</span>
				</div>
			</div>
		</form>

		<div style="margin-bottom: 10px; display: flex; gap: 5px;">
		<button type="button" class="btn btn-default" onclick="openLoadPrevModal()"
    style="background: #fff; border: 1px solid #ccc; padding: 4px 10px; font-size: 12px;">
    <i class="fas fa-file-import"></i> 前回給与の読み込み
</button>
			<button type="button" class="btn btn-primary"
				onclick="openEmployeeSelectModal()"
				style="background: #337ab7; color: #fff; border: none; padding: 4px 10px; font-size: 12px;">
				<i class="fas fa-plus"></i> 新規追加
			</button>
			<!-- [선택삭제] 버튼 / [選択削除]ボタン -->
			<button type="button" class="btn btn-default"
				onclick="deleteSelectedEmployees()"
				style="background: #fff; border: 1px solid #ccc; padding: 4px 10px; font-size: 12px; cursor: pointer;">
				<i class="fas fa-trash-alt"></i> 選択削除
			</button>

			<!-- [전체삭제] 버튼 / [全体削除]ボタン -->
			<button type="button" class="btn btn-danger"
				onclick="deleteAllEmployees()"
				style="background: #d9534f; color: #fff; border: none; padding: 4px 10px; font-size: 12px; cursor: pointer;">
				<i class="fas fa-trash"></i> 全体削除
			</button>
		</div>

		<!-- 귀속연월에 맞춰 정산기간 및 급여지급일 자동 계산 스크립트 / 帰属年月に合わせて精算期間と給与支給日を自動計算するスクリプト -->
		<script>
    function updateAutoDates() {
        var yearSel = document.getElementById("payYear");
        var monthSel = document.getElementById("payMonth");

        if (!yearSel || !monthSel) return;

        var year = parseInt(yearSel.value, 10);
        var month = parseInt(monthSel.value, 10);

        var monthStr = month < 10 ? "0" + month : "" + month;
        var startDateStr = year + "-" + monthStr + "-01";
        var lastDay = new Date(year, month, 0).getDate();
        var endDateStr = year + "-" + monthStr + "-" + lastDay;

        // ★ 요구사항: 귀속연월의 다음달 5일 고정 / ★要件：帰属年月の翌月5日固定
        var nextYear = year;
        var nextMonth = month + 1;
        if (nextMonth > 12) { nextMonth = 1; nextYear++; }
        var nextMonthStr = nextMonth < 10 ? "0" + nextMonth : "" + nextMonth;
        var payDateStr = nextYear + "-" + nextMonthStr + "-05";

        document.getElementById("calcPeriodStart").value = startDateStr;
        document.getElementById("calcPeriodEnd").value = endDateStr;
        document.getElementById("payDate").value = payDateStr;
    }

    // ★ 핵심: 페이지 로드 시점에 '파라미터가 비어있다면' 전월로 세팅하고 계산! / ★ポイント：ページ読み込み時に「パラメータが空であれば」前月に設定して計算する！
    window.addEventListener("DOMContentLoaded", function() {
        // 서버에서 받아온 값이 비어있을 때만(처음 접속 시) 전월로 강제 세팅 / サーバーから受け取った値が空の時のみ（初回アクセス時）前月に強制設定する
        var yearSel = document.getElementById("payYear");
        var monthSel = document.getElementById("payMonth");

        // JSP에서 파라미터가 안 넘어왔다면(즉, 값이 비어있다면) 전월 계산 / JSPからパラメータが渡されなければ（つまり値が空であれば）前月を計算する
        if (!yearSel.value || !monthSel.value) {
            var d = new Date();
            d.setMonth(d.getMonth() - 1); // 전월 계산 / 前月を計算

            yearSel.value = d.getFullYear();
            monthSel.value = d.getMonth() + 1;
        }

        // 세팅된(혹은 기존의) 값으로 날짜 계산 함수 실행 / 設定された（あるいは既存の）値で日付計算関数を実行する
        updateAutoDates();
    });
</script>

		<!-- 메인 컨텐츠 그리드 레이아웃 (좌/우 분할) / メインコンテンツグリッドレイアウト（左右分割） -->
		<div style="display: flex; gap: 15px; align-items: flex-start;">

			<!-- [조각 2] 좌측: 대상 사원 목록 그리드 불러오기 / [パーツ2] 左側：対象社員一覧グリッドの読み込み -->
			<div
				style="flex: 1.2; background: #fff; border: 1px solid #ddd; padding: 10px;">
				<table class="table table-bordered table-hover"
					style="width: 100%; border-collapse: collapse; font-size: 12px; margin-bottom: 0;">
					<thead style="background: #f4f6f9;">
						<tr>
							<th
								style="border: 1px solid #ddd; padding: 6px; text-align: center; color: #337ab7;">区分</th>
							<th
								style="border: 1px solid #ddd; padding: 6px; text-align: center; color: #337ab7;">氏名</th>
							<th
								style="border: 1px solid #ddd; padding: 6px; text-align: center; color: #337ab7;">部署</th>
							<th
								style="border: 1px solid #ddd; padding: 6px; text-align: center;">支給総額</th>
							<th
								style="border: 1px solid #ddd; padding: 6px; text-align: center;">控除総額</th>
							<th
								style="border: 1px solid #ddd; padding: 6px; text-align: center;">実支給額</th>
						</tr>
					</thead>
					<tbody id="employeeTableBody">
						<c:forEach var="emp" items="${employeeList}">
							<tr data-id="${emp.payrollEmployeeId}" data-basewage="${emp.baseWageAmount}" data-employee-id="${emp.employeeId}" onclick="selectEmployeeRow(this, '${emp.payrollEmployeeId}')"
								style="cursor: pointer;">
								<td
									style="border: 1px solid #ddd; padding: 6px; text-align: center;">${emp.employmentType}</td>
								<td
									style="border: 1px solid #ddd; padding: 6px; text-align: center;">${emp.employeeName}</td>
								<td
									style="border: 1px solid #ddd; padding: 6px; text-align: center;">${emp.department}</td>
								<td
									style="border: 1px solid #ddd; padding: 6px; text-align: right; color: #337ab7; font-weight: bold;"><fmt:formatNumber
										value="${emp.totalPayAmount}" pattern="#,###" /></td>
								<td
									style="border: 1px solid #ddd; padding: 6px; text-align: right; color: #d9534f; font-weight: bold;"><fmt:formatNumber
										value="${emp.totalDeductionAmount}" pattern="#,###" /></td>
								<td
									style="border: 1px solid #ddd; padding: 6px; text-align: right;"><fmt:formatNumber
										value="${emp.netPayAmount}" pattern="#,###" /></td>
							</tr>
						</c:forEach>
						<c:if test="${empty employeeList}">
							<tr id="emptyRow">
								<td colspan="6"
									style="border: 1px solid #ddd; padding: 25px; text-align: center; color: #666;">登録された
									社員データがありません。</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</div>

			<!-- [조각 3] 우측: 소득 탭 및 상세 금액 입력 폼 불러오기 / [パーツ3] 右側：所得タブおよび詳細金額入力フォームの読み込み -->
			<div
				style="flex: 1.3; background: #fff; border: 1px solid #ddd; padding: 10px; font-size: 12px;">

				<!-- 탭 버튼 영역 (디자인 수정 적용) / タブボタン領域（デザイン修正適用） -->
				<div
					style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 10px; border-bottom: 2px solid #222;">
					<div style="display: flex; gap: 2px;">
						<!-- 일반소득 탭 / 一般所得タブ -->
						<button type="button" onclick="switchIncomeTab('GENERAL')"
							style="background: ${param.incomeType == 'BUSINESS' ? '#999999' : '#009688'};
				color: white;
				border: none;
				border-radius: 5px 5px 0 0;
				padding: 8px 25px;
				font-weight: bold;
				font-size: 13px;
				cursor: pointer;">一般所得</button>

						<!-- 사업소득/기타소득 탭 / 事業所得/その他所得タブ -->
						<!--  <button type="button" onclick="switchIncomeTab('BUSINESS')"
				style="background: ${param.incomeType == 'BUSINESS' ? '#009688' : '#999999'};
				color: white;
				border: none;
				border-radius: 5px 5px 0 0;
				padding: 8px 25px;
				font-weight: bold;
				font-size: 13px;
				cursor: pointer;">사업소득/기타소득</button> -->
					</div>

					<!-- 우측 기능 버튼 / 右側機能ボタン -->
					<div style="display: flex; gap: 5px; margin-bottom: 5px;">

						<button type="button" class="btn btn-dark btn-xs"
							onclick="openTipModal()"
							style="background: #333; color: #fff; border: none; padding: 3px 8px;">
							<i class="fas fa-question-circle"></i> Tip
						</button>
					</div>
				</div>

				<!-- 폼 시작 / フォーム開始 -->
				<form action="${pageContext.request.contextPath}/Payment/save.do"
					method="POST" id="payrollDetailForm">
					<input type="hidden" name="payrollEmployeeId"
						id="selectedPayrollEmployeeId"
						value="${selectedEmployee.payrollEmployeeId}">
					<!-- ★ 아직 급여차수에 등록 안 된 사원을 저장할 때, 서버가 PAYROLL_EMPLOYEE를 새로 만들 수 있도록 필요한 값 / ★まだ給与回に登録されていない社員を保存する際、サーバーがPAYROLL_EMPLOYEEを新規作成できるよう必要な値 -->
					<input type="hidden" name="employeeId" id="selectedEmployeeId"
						value="${selectedEmployee.employeeId}">
					<input type="hidden" name="payrollId" id="selectedPayrollIdForSave"
						value="${payrollId}">

					<div style="display: flex; gap: 10px;">
<!-- 지급항목 테이블 / 支給項目テーブル -->
<div style="flex: 1;">
	<div
		style="display: flex; justify-content: space-between; align-items: center; background: #f4f6f9; padding: 4px 8px; border: 1px solid #ddd; font-weight: bold;">
		<span>支給項目 <span
			style="background: #009688; color: white; font-size: 9px; padding: 1px 4px; border-radius: 2px;">M</span></span>
	</div>
	<table class="table table-bordered table-condensed"
		style="width: 100%; border-collapse: collapse; margin-bottom: 0;">

		<c:forEach var="item" items="${payItemList}">
			<tr>
				<!-- 왼쪽 칸: 텍스트(항목 이름) 출력 / 左側セル：テキスト（項目名）出力 -->
				<td style="padding: 4px; background: #fafafa; width: 40%;">
					${item.payItemName}
					<c:if test="${item.payItemName.contains('식대') or item.payItemName.contains('차량')}">
						<span style="color: red;">[비]</span>
					</c:if>
				</td>

				<!-- 오른쪽 칸: 금액 입력창 출력 / 右側セル：金額入力欄出力 -->
				<td style="padding: 4px;">
					<input type="text"
						name="payItem_${item.payItemId}"
						id="input_payItem_${item.payItemId}"
						value="${item.bulkPayAmount != null && item.bulkPayAmount > 0 ? item.bulkPayAmount : 0}"
						data-default="${item.bulkPayAmount != null && item.bulkPayAmount > 0 ? item.bulkPayAmount : 0}"
						data-item-name="${item.payItemName}"
						oninput="calculateRealPay()"
						class="form-control input-sm pay-input"
						style="width: 100%; height: 24px; padding: 2px; text-align: right;">
				</td>
			</tr>
			<tr class="calc-method-row">
				<td style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">計算方法</td>
				<td style="padding: 4px;"><input type="text" readonly
					class="form-control input-sm"
					style="width: 100%; height: 22px; background: #f9f9f9;"
					value="${item.calculationMethod != null ? item.calculationMethod : ''}"></td>
			</tr>
		</c:forEach>

	</table>
</div>

						<!-- 공제항목 테이블 / 控除項目テーブル -->
						<div style="flex: 1;">
							<div
								style="background: #f4f6f9; padding: 4px 8px; border: 1px solid #ddd; font-weight: bold; display: flex; justify-content: space-between; align-items: center;">
								<span>控除項目 <span
									style="background: #d9534f; color: white; font-size: 9px; padding: 1px 4px; border-radius: 2px;">M</span></span>
							</div>
							<table class="table table-bordered table-condensed"
								style="width: 100%; border-collapse: collapse; margin-bottom: 0;">
								<c:forEach var="dedItem" items="${deductionItemList}">
									<tr>
										<td style="padding: 4px; background: #fafafa; width: 40%;">${dedItem.deductionItemName}</td>
										<td style="padding: 4px;"><input type="text"
											name="dedItem_${dedItem.deductionItemId}"
											id="input_dedItem_${dedItem.deductionItemId}" value="0"
											oninput="calculateRealPay()"
											class="form-control input-sm deduction-input"
											style="width: 100%; height: 24px; padding: 2px; text-align: right;">
										</td>
									</tr>
									<tr class="calc-method-row">
										<td
											style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">計算方法</td>
										<td style="padding: 4px;"><input type="text" readonly
											class="form-control input-sm"
											style="width: 100%; height: 22px; background: #f9f9f9;"
											value="${dedItem.calculationMethod != null ? dedItem.calculationMethod : ''}"></td>
									</tr>
								</c:forEach>
								<!-- 줄 맞춤용 여백 / 行揃え用の余白 -->
								<tr>
									<td colspan="2"
										style="background: #fff; border: none; height: 48px;"></td>
								</tr>
							</table>
						</div>
					</div>

					<!-- 합계 및 버튼 영역 / 合計およびボタン領域 -->
					<div
						style="display: flex; border: 1px solid #ddd; margin-top: 10px; font-weight: bold; text-align: center;">
						<div
							style="flex: 1; background: #f4f6f9; padding: 6px; border-right: 1px solid #ddd; font-size: 13px;">
							支給総額：<span id="totalPayResult" style="color: #337ab7;"><fmt:formatNumber
									value="${selectedEmployee.totalPayAmount != null ? selectedEmployee.totalPayAmount : 0}"
									pattern="#,###" /></span>円
						</div>
						<div
							style="flex: 1; background: #f4f6f9; padding: 6px; font-size: 13px;">
							控除総額：<span id="totalDeductionResult" style="color: #d9534f;"><fmt:formatNumber
									value="${selectedEmployee.totalDeductionAmount != null ? selectedEmployee.totalDeductionAmount : 0}"
									pattern="#,###" /></span>円
						</div>
					</div>
					<div
						style="background: #204d74; color: white; text-align: center; padding: 10px; font-weight: bold; font-size: 15px; margin-top: 5px; border-radius: 2px;">
						実支給額：<span id="netPayResult"> <fmt:formatNumber
								value="${selectedEmployee.netPayAmount != null ? selectedEmployee.netPayAmount : 0}"
								pattern="#,###" />
						</span>円
					</div>

					<div
						style="text-align: right; margin-top: 10px; display: flex; justify-content: flex-end; gap: 5px;">
						<button type="submit" class="btn btn-primary btn-sm"
							style="background: #337ab7; color: white; border: none; padding: 6px 18px; font-weight: bold;">保存</button>
						<button type="button" class="btn btn-default btn-sm"
							onclick="clearPayrollForm()"
							style="background: #ccc; color: #333; border: 1px solid #bbb; padding: 6px 15px;">内容の
							クリア</button>
					</div>
				</form>
			</div>

		</div>

		<!-- 4. 하단 급여 종합정보 집계 영역 / 4. 下部給与総合情報集計領域 -->
		<div style="margin-top: 25px;">
			<div style="font-weight: bold; margin-bottom: 8px; font-size: 14px;">給与
				総合情報</div>
			<div style="display: flex; gap: 10px;">
				<div
					style="flex: 1; background: #95a5a6; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">月合計</div>
					<div style="font-size: 20px; font-weight: bold; margin-top: 5px;">${summaryInfo.totalCount}
						件</div>
				</div>
				<div
					style="flex: 2; background: #5bc0de; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">支給総額</div>
					<div style="font-size: 20px; font-weight: bold; margin-top: 5px;">
						<fmt:formatNumber value="${summaryInfo.totalGiveAmount}"
							pattern="#,###" />
						円
					</div>
				</div>
				<div
					style="flex: 2; background: #d9534f; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">控除総額</div>
					<div style="font-size: 20px; font-weight: bold; margin-top: 5px;">
						<fmt:formatNumber value="${summaryInfo.totalDeduAmount}"
							pattern="#,###" />
						円
					</div>
				</div>
				<div
					style="flex: 2; background: #4e5d6c; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">実支給額</div>
					<div style="font-size: 20px; font-weight: bold; margin-top: 5px;">
						<fmt:formatNumber value="${summaryInfo.totalRealAmount}"
							pattern="#,###" />
						円
					</div>
				</div>
			</div>
		</div>
	</main>
	<!-- ================= [Tip 모달창 시작] / [Tipモーダル開始] ================= -->
	<div id="tipModalOverlay"
		style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); z-index: 9999; align-items: center; justify-content: center;">
		<div
			style="background: white; width: 550px; max-height: 80vh; border-radius: 5px; overflow: hidden; display: flex; flex-direction: column; box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5); font-family: 'Malgun Gothic', sans-serif;">

			<!-- 모달 헤더 / モーダルヘッダー -->
			<div
				style="background: #333; color: white; padding: 10px 15px; display: flex; justify-content: space-between; align-items: center;">
				<span style="font-weight: bold; font-size: 14px;">Payzon Tip</span>
				<button type="button" onclick="closeTipModal()"
					style="background: transparent; border: none; color: white; font-size: 20px; cursor: pointer; line-height: 1;">×</button>
			</div>

			<!-- 모달 본문 (스크롤 영역) / モーダル本文（スクロール領域） -->
			<div style="padding: 20px; overflow-y: auto; flex: 1;">
				<h3
					style="color: #e82c6d; margin-top: 0; margin-bottom: 20px; font-weight: bold; font-size: 18px;">給与入力・管理</h3>

				<!-- 설명 항목 반복 템플릿 스타일 / 説明項目繰り返しテンプレートスタイル -->
				<style>
.tip-item {
	margin-bottom: 18px;
}

.tip-title {
	font-weight: bold;
	font-size: 13px;
	margin-bottom: 6px;
	display: flex;
	align-items: center;
	gap: 6px;
}

.tip-num {
	background: #fb6a7e;
	color: white;
	padding: 2px 6px;
	border-radius: 4px;
	font-size: 11px;
}

.tip-desc {
	font-size: 12px;
	color: #555;
	line-height: 1.6;
	padding-left: 25px;
	word-break: keep-all;
}
</style>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">1</span> 帰属年月
					</div>
					<div class="tip-desc">給与計算の対象となる年度と月を選択します。</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">2</span> 給与回
					</div>
					<div class="tip-desc">初期値は[給与-1回]と表示され、1か月の給与を分割して支給する
						場合、最大給与-5回まで分けて給与計算および給与テーブルを作成できます。</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">3</span> 精算期間
					</div>
					<div class="tip-desc">[ユーザー情報]メニューで設定した給与算定期間に従って精算期間が自動
						表示され、精算期間に変動がある場合はユーザーが直接修正入力できます。</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">4</span> 給与支給日
					</div>
					<div class="tip-desc">選択した帰属年月の給与が実際に支給される給与支給日を選択します。</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">5</span> [前回給与の読み込み]
					</div>
					<div class="tip-desc">前月の給与情報を、選択した帰属年月にそのまま読み込みます。
						給与情報が同一であるか変動が大きくない場合、前月の給与情報を読み込んですぐに適用でき、変更点のみ
						修正入力できます。</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">6</span> [新規追加]
					</div>
					<div class="tip-desc">[新規追加]ボタンをクリックすると、社員リストレイヤーが表示されます。ここで
						給与を計算する対象社員にチェックを入れた後、下部の[社員選択]ボタンをクリックすると、レイヤーが閉じて給与情報
						リストに追加されます。</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">7</span> [全体削除]
					</div>
					<div class="tip-desc">選択した帰属年月の給与情報リストをすべて削除します。リストから削除された
						給与情報は復元できず、[新規追加]または[前回給与の読み込み]などで新たに給与情報を登録できます。</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">8</span> [削除]
					</div>
					<div class="tip-desc">選択した帰属年月の給与情報リストから、削除したい給与情報だけを
						選択して削除します。リストから削除された給与情報は復元できず、[新規追加]を通じて新たに給与情報を登録
						できます。</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">9</span> 給与情報リスト
					</div>
					<div class="tip-desc">給与情報リストで給与情報を入力する対象を選択します。右側の
						給与入力欄に選択した社員の給与を入力して給与情報を保存できます。すでに給与情報が保存されている社員を選択した
						場合、右側に保存済みの給与情報が表示され、確認および修正入力ができます。</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">10</span> 一般所得タブ
					</div>
					<div class="tip-desc">一般所得タブは、社員登録時の甲勤税設定で[勤労所得者
						甲勤税（勤労所得簡易税額表）]に設定された労働者が対象であり、一般所得タブでは勤労所得簡易税額表に基づいて甲勤税
						控除額を計算します。</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">11</span> [自動計算]
					</div>
					<div class="tip-desc">支給項目に該当する金額をすべて入力した後、[自動計算]ボタンを
						クリックすると、4大保険および甲勤税を自動計算して金額を表示します。</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">12</span> [保存]
					</div>
					<div class="tip-desc">支給額および控除額を入力し、実支給額が正常に計算された場合、
						[保存]ボタンをクリックすると、左側の給与情報リストに入力した給与情報が保存され、リストが更新されます。すでに給与情報が
						登録されている社員の場合、新しく入力した給与情報に修正されます。</div>
				</div>

				<div class="tip-item">
					<div class="tip-title">
						<span class="tip-num">13</span> [内容のクリア]
					</div>
					<div class="tip-desc">入力した支給額および控除額をすべて消去して、新しく作成できます。</div>
				</div>
			</div>
		</div>
	</div>
	<!-- ================= [Tip 모달창 끝] / [Tipモーダル終了] ================= -->


	<!-- 스크립트 기능 정의 / スクリプト機能定義 -->
	<script>
		function reloadPayrollData() {
			document.getElementById("searchForm").submit();
		}

		function selectEmployeeRow(rowElement, payrollEmployeeId) {
		    // 1. 클릭한 사원 행 배경색 변경 / 1. クリックした社員行の背景色を変更
		    var rows = document.querySelectorAll("#employeeTableBody tr");
		    if(rows.length > 0) {
		        rows.forEach(function(r) { r.style.background = ""; });
		    }
		    rowElement.style.background = "#eef4fb";

		    // 2. 선택된 사원 ID 저장 / 2. 選択した社員IDを保存
		    document.getElementById("selectedPayrollEmployeeId").value = payrollEmployeeId;
		    // ★ 저장 시 서버가 신규 등록에 쓸 수 있도록 사원 마스터 ID도 함께 저장 / ★保存時にサーバーが新規登録に使えるよう社員マスタIDも一緒に保存
		    document.getElementById("selectedEmployeeId").value = rowElement.getAttribute('data-employee-id') || '';

		    // 4. 우측 폼 입력칸 초기화 (무조건 0이 아니라 기본값이 있으면 기본값으로 리셋) / 4. 右側フォーム入力欄を初期化（無条件に0ではなく、初期値があれば初期値にリセット）
		    document.querySelectorAll('.pay-input, .deduction-input').forEach(function(input) {
		        var defaultVal = input.getAttribute('data-default');
		        if (defaultVal) {
		            input.value = defaultVal; // 식대 등은 200000 유지 / 食事代などは200000を維持
		        } else {
		            input.value = 0; // 나머지는 0으로 / それ以外は0に
		        }
		    });

		    // ★ 아직 이 귀속연월/급여차수의 급여 대상자로 등록되지 않은 사원(payrollEmployeeId 없음)은
		    //   서버에 저장된 지급/공제 상세가 아예 없으므로 AJAX를 호출하지 않는다. 대신 좌측 목록을
		    //   그릴 때 이미 함께 내려받은 사원 마스터 기본급(data-basewage)만 우측 기본급 칸에 채워준다.
		    // ★ まだこの帰属年月・給与回の給与対象者として登録されていない社員（payrollEmployeeIdなし）は
		    //   サーバーに保存された支給・控除詳細が全く存在しないためAJAXを呼び出さない。代わりに左側一覧を
		    //   描画する際すでに一緒に受け取っていた社員マスタの基本給（data-basewage）だけを右側の基本給欄に埋める。
		    if (!payrollEmployeeId) {
		        var baseWageInput = document.querySelector('.pay-input[data-item-name="기본급"]');
		        var baseWage = rowElement.getAttribute('data-basewage');
		        if (baseWageInput && baseWage && Number(baseWage) > 0) {
		            baseWageInput.value = baseWage;
		        }
		        calculateRealPay();
		        return;
		    }

		    // 3. DB에서 데이터 가져오기 (AJAX) / 3. DBからデータを取得（AJAX）
		    var url = "${pageContext.request.contextPath}/Payment/detailAjax.do?payrollEmployeeId=" + payrollEmployeeId;

		    fetch(url)
		        .then(response => response.json())
		        .then(data => {
		            // 5. DB에서 가져온 지급항목 데이터 꽂아넣기 (동적 ID 매칭) / 5. DBから取得した支給項目データを差し込む（動的IDマッチング）
		            if (data.payDetails) {
		                data.payDetails.forEach(function(item) {
		                    var inputField = document.getElementById('input_payItem_' + item.payItemId);
		                    if (inputField) {
		                        inputField.value = item.amount;
		                    }
		                });
		            }

		            // 6. DB에서 가져온 공제항목 데이터 꽂아넣기 (동적 ID 매칭) / 6. DBから取得した控除項目データを差し込む（動的IDマッチング）
		            if (data.deductionDetails && data.deductionDetails.length > 0) {
		                data.deductionDetails.forEach(function(item) {
		                    var inputField = document.getElementById('input_dedItem_' + item.deductionItemId);
		                    if (inputField) {
		                        inputField.value = item.amount;
		                    }
		                });
		            }

		            // 7. 하단 총액/실지급액 자동 재계산 / 7. 下部の総額/実支給額を自動再計算
		            calculateRealPay();
		        })
		        .catch(error => {
		            console.error("데이터 로드 에러:", error);
		        });
		}

		function toggleCalcMethod() {
			var badge = document.getElementById("calcSwitchBadge");
			var rows = document.querySelectorAll(".calc-method-row");
			if (badge.innerText === "on") {
				badge.innerText = "off";
				badge.classList.add("off");
				rows.forEach(function(r) { r.classList.add("off"); });
			} else {
				badge.innerText = "on";
				badge.classList.remove("off");
				rows.forEach(function(r) { r.classList.remove("off"); });
			}
		}

		function calculateRealPay() {
	        let totalPay = 0;
	        let totalDeduction = 0;

	        const payInputs = document.querySelectorAll('.pay-input');
	        payInputs.forEach(function(input) {
	            totalPay += parseInt(input.value) || 0;
	        });

	        const deductionInputs = document.querySelectorAll('.deduction-input');
	        deductionInputs.forEach(function(input) {
	            totalDeduction += parseInt(input.value) || 0;
	        });

	        const netPay = totalPay - totalDeduction;

	        const payResultElem = document.getElementById('totalPayResult');
	        const deduResultElem = document.getElementById('totalDeductionResult');
	        const netResultElem = document.getElementById('netPayResult');

	        if (payResultElem) payResultElem.innerText = totalPay.toLocaleString();
	        if (deduResultElem) deduResultElem.innerText = totalDeduction.toLocaleString();
	        if (netResultElem) netResultElem.innerText = netPay.toLocaleString();
	    }

	    function clearPayrollForm() {
	        const form = document.getElementById("payrollDetailForm");
	        if (form) {
	            form.reset();
	            calculateRealPay();
	        }
	    }

	    function openEmployeeSelectModal() {
	        var contextPath = "${pageContext.request.contextPath}";
	        var popupUrl = contextPath + "/Payment/employeeAddModal.do";
	        window.open(popupUrl, "EmpSelectModal", "width=700,height=600,left=200,top=100,scrollbars=yes");
	    }



	    function openTipModal() {
	        document.getElementById('tipModalOverlay').style.display = 'flex';
	    }

	    function closeTipModal() {
	        document.getElementById('tipModalOverlay').style.display = 'none';
	    }

	 // 금액 입력칸 관련 자동화 스크립트 / 金額入力欄関連の自動化スクリプト
	    document.querySelectorAll('.pay-input, .deduction-input').forEach(function(input) {

	        // 1. 문자 입력 방지 (숫자만 남기기) / 1. 文字入力を防止（数字のみ残す）
	        input.addEventListener('input', function() {
	            this.value = this.value.replace(/[^0-9]/g, '');
	        });

	        // 2. [추가] 클릭(포커스) 시 기본값 '0' 전체 블록 지정하기 / 2. [追加] クリック（フォーカス）時に初期値「0」を全選択する
	        input.addEventListener('focus', function() {
	            this.select(); // 입력칸 안의 텍스트를 전체 선택합니다! / 入力欄内のテキストを全選択します！
	        });

	    });
	</script>
	<script>
    // 1. [선택삭제] : 마우스로 클릭해서 배경색이 바뀐 행을 실제 DB에서도 삭제하는 함수 / 1. [選択削除]：マウスでクリックして背景色が変わった行を実際にDBからも削除する関数
    function deleteSelectedEmployees() {
        // 선택된 ID가 들어있는 히든 인풋 확인 / 選択されたIDが入っている隠しinputを確認
        var selectedIdInput = document.getElementById("selectedPayrollEmployeeId");

        // 배경색이 바뀌어 있는(#eef4fb 계열) 행 찾기 / 背景色が変わっている（#eef4fb系）行を探す
        var targetRow = document.querySelector("#employeeTableBody tr[style*='background']");

        // 만약 선택된 것이 없다면 / もし選択されたものがなければ
        if (!targetRow && (!selectedIdInput || !selectedIdInput.value)) {
            alert("削除する社員を選択してください。");
            return;
        }

        // 1번 사진 같은 확인창 띄우기 / 1番の画面のような確認ダイアログを表示
        if (!confirm("選択した社員を削除しますか？")) return;

        var payrollEmployeeId = selectedIdInput ? selectedIdInput.value : "";

        function removeRowAndReset() {
            // 화면에서 해당 행 삭제 / 画面から該当行を削除
            if (targetRow) {
                targetRow.remove();
            } else {
                // 만약 스타일로 못 찾았을 경우 #employeeTableBody 안의 모든 행을 돌며 처리 / スタイルで見つからなかった場合、#employeeTableBody内の全行を巡回して処理
                var coloredRow = document.querySelector("#employeeTableBody tr[style*='background']");
                if (coloredRow) coloredRow.remove();
            }
            // 히든 인풋 값 초기화 / 隠しinputの値を初期化
            if (selectedIdInput) selectedIdInput.value = "";
            alert("選択した社員が削除されました。");
        }

        // ★ 아직 이번 급여차수에 등록 안 된 사원(payrollEmployeeId 없음)은 DB에 지울 데이터가 없으므로
        //   화면에서만 지운다. / ★まだこの給与回に登録されていない社員（payrollEmployeeIdなし）はDBに削除するデータが
        //   ないため、画面からのみ削除する。
        if (!payrollEmployeeId) {
            removeRowAndReset();
            return;
        }

        fetch("${pageContext.request.contextPath}/Payment/deleteSelected.do", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "payrollEmployeeIds=" + encodeURIComponent(payrollEmployeeId)
        })
        .then(function(res) { return res.text(); })
        .then(function(data) {
            if (data === "SUCCESS") {
                removeRowAndReset();
            } else {
                alert("削除中に問題が発生しました。");
            }
        })
        .catch(function(error) {
            console.error("선택삭제 에러:", error);
            alert("削除中に問題が発生しました。");
        });
    }

    // 2. [전체삭제] : 2단계 경고창을 거쳐 일용직이 아닌 전체 사원의 급여 데이터를 실제 DB에서 삭제하는 함수 / 2. [全体削除]：2段階の警告ダイアログを経て日雇いではない全社員の給与データを実際にDBから削除する関数
    function deleteAllEmployees() {
        var rows = document.querySelectorAll("#employeeTableBody tr");

        if (rows.length === 0) {
            alert("削除する社員情報がありません。");
            return;
        }

        // 3번째 사진 같은 1차 경고창 / 3番目の画面のような1次警告ダイアログ
        if (!confirm("■■ 注意!! ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■\n■ 削除された給与入力情報は復元できません。 ■\n■ 削除しますか？ ■\n■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■")) return;

        // 2번째 사진 같은 2차 최종 경고창 / 2番目の画面のような2次最終警告ダイアログ
        if (!confirm("■ [全体]の給与入力情報を削除しますか？")) return;

        var payrollIdInput = document.getElementById("payrollId");
        var payrollId = payrollIdInput ? payrollIdInput.value : "";

        fetch("${pageContext.request.contextPath}/Payment/deleteAll.do", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "payrollId=" + encodeURIComponent(payrollId)
        })
        .then(function(res) { return res.text(); })
        .then(function(data) {
            if (data === "SUCCESS") {
                // 테이블의 모든 사원 행 삭제 / テーブルの全社員行を削除
                rows.forEach(function(r) { r.remove(); });
                // 선택된 ID 히든 인풋 초기화 / 選択されたID隠しinputを初期化
                var selectedIdInput = document.getElementById("selectedPayrollEmployeeId");
                if (selectedIdInput) selectedIdInput.value = "";
                alert("全社員および給与情報が削除されました。");
            } else {
                alert("削除中に問題が発生しました。");
            }
        })
        .catch(function(error) {
            console.error("전체삭제 에러:", error);
            alert("削除中に問題が発生しました。");
        });
    }
</script>
	<script>
    function addEmployeesToMain(selectedIds) {
        if (!selectedIds || selectedIds.length === 0) return;

        // 1. 메인 화면에서 payrollId 값을 안전하게 가져오기 (input, select, URL 파라미터 전부 다 뒤져서 찾음) / 1. メイン画面からpayrollId値を安全に取得（input、select、URLパラメータをすべて調べて探す）
        var payrollId = "";

        // ① input이나 select 중에 payrollId라는 이름이나 아이디가 있는지 찾기 / ① inputやselectの中にpayrollIdという名前やIDがあるか探す
        var payrollInput = document.querySelector("[name='payrollId']") || document.getElementById("payrollId");
        if (payrollInput && payrollInput.value) {
            payrollId = payrollInput.value;
        }

        // ② 화면에 없다면 현재 주소창(URL)의 파라미터에서 찾기 / ② 画面になければ現在のアドレスバー（URL）のパラメータから探す
        if (!payrollId) {
            var urlParams = new URLSearchParams(window.location.search);
            payrollId = urlParams.get('payrollId');
        }

        // ③ 그래도 없으면 화면에 있는 숨겨진 input이나 첫 번째 셀렉트 값 등에서 유추하거나 기본값 '1' 지정 / ③ それでもなければ画面にある隠しinputや最初のselect値などから推測するか、初期値「1」を指定
        if (!payrollId) {
            payrollId = "1";
        }

        // 2. 요청할 URL 설정 / 2. リクエストするURLを設定
        var contextPath = "${pageContext.request.contextPath}";
        var url = contextPath + "/Payment/paymentEmployeeInsert.do";

        // 3. POST 방식으로 보낼 데이터 세팅 / 3. POST方式で送るデータを設定
        var formData = new URLSearchParams();
        formData.append("payrollId", payrollId);
        formData.append("employeeIds", selectedIds.join(","));

        console.log("전송할 payrollId:", payrollId); // F12 개발자 도구 콘솔에서 확인 가능 / F12開発者ツールのコンソールで確認可能
        console.log("전송할 employeeIds:", selectedIds.join(","));

        // 4. AJAX(fetch) 전송 / 4. AJAX（fetch）送信
        // 전체 새로고침(location.reload)을 하지 않고 방금 추가된 사원 행만 화면에 붙인다. / 全体再読み込み（location.reload）をせず、今追加した社員行だけを画面に追加する。
        // (새로고침을 하면 [선택삭제]/[전체삭제]로 화면에서만 지워둔 사원들이 DB 기준으로 다시 나타나 버리기 때문) / （再読み込みすると、[選択削除]/[全体削除]で画面上だけ消した社員がDB基準で再び表示されてしまうため）
        fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: formData.toString()
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("서버 응답 코드: " + response.status);
            }
            return response.json();
        })
        .then(list => {
            if (!list || list.length === 0) {
                alert("追加された社員がいません。");
                return;
            }

            var emptyRow = document.getElementById("emptyRow");
            if (emptyRow) emptyRow.remove();

            list.forEach(function (emp) {
                var empId = String(emp.payrollEmployeeId);
                if (document.querySelector('#employeeTableBody tr[data-id="' + empId + '"]')) return; // 이미 화면에 있으면 중복 추가 방지 / すでに画面にあれば重複追加を防止

                var tr = document.createElement("tr");
                tr.setAttribute("data-id", empId);
                tr.style.cursor = "pointer";
                tr.onclick = function () { selectEmployeeRow(tr, empId); };
                tr.innerHTML =
                    '<td style="border: 1px solid #ddd; padding: 6px; text-align: center;">' + emp.employmentType + '</td>' +
                    '<td style="border: 1px solid #ddd; padding: 6px; text-align: center;">' + emp.employeeName + '</td>' +
                    '<td style="border: 1px solid #ddd; padding: 6px; text-align: center;">' + emp.department + '</td>' +
                    '<td style="border: 1px solid #ddd; padding: 6px; text-align: right; color: #337ab7; font-weight: bold;">' + Number(emp.totalPayAmount || 0).toLocaleString('ko-KR') + '</td>' +
                    '<td style="border: 1px solid #ddd; padding: 6px; text-align: right; color: #d9534f; font-weight: bold;">' + Number(emp.totalDeductionAmount || 0).toLocaleString('ko-KR') + '</td>' +
                    '<td style="border: 1px solid #ddd; padding: 6px; text-align: right;">' + Number(emp.netPayAmount || 0).toLocaleString('ko-KR') + '</td>';
                document.getElementById("employeeTableBody").appendChild(tr);
            });

            alert("新規社員が正常に追加されました。");
        })
        .catch(error => {
            console.error("통신 에러:", error);
            alert("追加中に問題が発生しました。(" + error.message + ")");
        });
    }
</script>

<script>
    // [저장] 버튼 AJAX 제출 로직 / [保存]ボタンのAJAX送信ロジック
    document.getElementById("payrollDetailForm").addEventListener("submit", function(e) {
        e.preventDefault(); // 기본 폼 전송(새로고침) 방지 / デフォルトのフォーム送信（再読み込み）を防止

        var formData = new FormData(this);
        var url = this.action;

        fetch(url, {
            method: "POST",
            body: new URLSearchParams(formData)
        })
        .then(response => response.text())
        .then(data => {
            if (data === "SUCCESS") {
                alert("正常に保存されました。");
                location.reload(); // 저장 완료 후 화면 깔끔하게 새로고침 (검색조건 유지됨) / 保存完了後、画面をきれいに再読み込み（検索条件は維持される）
            } else {
                alert("保存中に問題が発生しました。");
            }
        })
        .catch(error => {
            console.error("저장 에러:", error);
            alert("サーバー通信に失敗しました。");
        });
    });

 // 1. 모달창 열기 및 셀렉트 박스(최근 12개월) 자동 생성 / 1. モーダルを開いてセレクトボックス（直近12か月）を自動生成
    function openLoadPrevModal() {
        var select = document.getElementById("prevPayrollSelect");
        select.innerHTML = '<option value="">帰属年月・回数選択</option>';

        var today = new Date();
        var year = today.getFullYear();
        var month = today.getMonth() + 1;

        for (var i = 0; i < 12; i++) {
            var m = month - i;
            var y = year;
            if (m <= 0) { m += 12; y -= 1; }

            var mStr = m < 10 ? "0" + m : "" + m;
            var val = y + "" + mStr + "-1"; // 예: 202607-1 / 例：202607-1
            var text = y + "年 " + mStr + "月 01回";
            select.options.add(new Option(text, val));
        }
        document.getElementById('loadPrevModalOverlay').style.display = 'flex';
    }

    // 2. 모달창 닫기 / 2. モーダルを閉じる
    function closeLoadPrevModal() {
        document.getElementById('loadPrevModalOverlay').style.display = 'none';
    }

    // 3. [급여정보 불러오기] 클릭 시 실행 (3번, 4번 사진 로직) / 3. [給与情報の読み込み]クリック時に実行（3番、4番の画面のロジック）
    function executeLoadPrev() {
        var selectedVal = document.getElementById("prevPayrollSelect").value;
        if (!selectedVal) {
            alert("読み込む帰属年月・回数を選択してください。");
            return;
        }

        // 3번 사진: 경고 멘트 / 3番の画面：警告メッセージ
        var confirmMsg = "既存の給与テーブルは削除され、\n\n読み込んだ給与テーブルに置き換えられます。\n\n読み込みますか？";

        if (confirm(confirmMsg)) {
            var currYear = document.getElementById("payYear").value;
            var currMonth = document.getElementById("payMonth").value;
            var currSeq = document.getElementById("paySequence").value;

            var prevYearMonth = selectedVal.split("-")[0];
            var prevSeq = selectedVal.split("-")[1];

            var url = "${pageContext.request.contextPath}/Payment/loadPrevAjax.do";
            var formData = new URLSearchParams();
            formData.append("currYear", currYear);
            formData.append("currMonth", currMonth);
            formData.append("currSeq", currSeq || "1");
            formData.append("prevYearMonth", prevYearMonth);
            formData.append("prevSeq", prevSeq);

            // 연속 클릭(더블클릭)으로 같은 요청이 두 번 나가면 PAYROLL_EMPLOYEE_ID 채번이 겹쳐 / 連続クリック（ダブルクリック）で同じリクエストが2回発生すると、PAYROLL_EMPLOYEE_IDの採番が重複し
            // DB 에러가 날 수 있으므로, 요청이 끝날 때까지 버튼을 잠가둔다. / DBエラーが発生する可能性があるため、リクエストが終わるまでボタンをロックしておく。
            var btn = document.getElementById("btnExecuteLoadPrev");
            btn.disabled = true;

            fetch(url, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: formData.toString()
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === "SUCCESS") {
                    // 4번 사진: 성공 알림 및 불러온 건수 표시 / 4番の画面：成功通知および読み込んだ件数の表示
                    alert("[読み込み] 一般所得：" + data.count + "件");
                    location.reload(); // 성공 후 화면 갱신 / 成功後に画面を更新
                } else {
                    alert("読み込み中にエラーが発生しました。");
                    btn.disabled = false;
                }
            })
            .catch(error => {
                console.error("통신 에러:", error);
                alert("サーバー通信に失敗しました。");
                btn.disabled = false;
            });
        }
    }
</script>
	<%@ include file="../../jspf/app-end.jspf"%>

	<!-- [지난급여 불러오기] 모달창 / [前回給与の読み込み]モーダル -->
<div id="loadPrevModalOverlay" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); z-index: 9999; align-items: center; justify-content: center;">
    <div style="background: white; width: 320px; border-radius: 5px; box-shadow: 0 5px 15px rgba(0,0,0,0.5); padding: 25px; text-align: center; font-family: 'Malgun Gothic', sans-serif;">
        <h4 style="margin-top: 0; margin-bottom: 20px; font-weight: bold; text-align: left; font-size: 16px;">給与年月選択</h4>
        <select id="prevPayrollSelect" class="form-control" style="width: 100%; margin-bottom: 20px; height: 35px;">
            <option value="">帰属年月・回数選択</option>
            <!-- 자바스크립트가 이전 달 목록을 여기에 자동으로 채워줍니다 / JavaScriptが前月の一覧をここに自動で埋め込みます -->
        </select>
        <button type="button" id="btnExecuteLoadPrev" class="btn btn-primary" onclick="executeLoadPrev()" style="width: 100%; background: #337ab7; border: none; padding: 10px; font-weight: bold;">給与情報の読み込み</button>
        <button type="button" class="btn btn-default" onclick="closeLoadPrevModal()" style="width: 100%; margin-top: 8px; padding: 10px;">キャンセル</button>
    </div>
</div>

</body>
</html>
