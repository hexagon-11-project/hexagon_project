<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.AttendanceType"%>
<%@ page import="config.model.DeductionItem"%>
<%@ page import="config.model.PayItem"%>
<%-- 급여항목 설정 화면이다. --%>
<%-- 給与項目設定画面である。 --%>
<%
// 핸들러가 request 에 담아 준 목록·선택 건을 꺼낸다
// ハンドラが request に入れた一覧・選択件を取り出す。
List<PayItem> payItemList = (List<PayItem>) request.getAttribute("payItemList");
List<AttendanceType> attendanceTypeList = (List<AttendanceType>) request.getAttribute("attendanceTypeList");
List<DeductionItem> deductionItemList = (List<DeductionItem>) request.getAttribute("deductionItemList");
PayItem selected = (PayItem) request.getAttribute("selectedPayItem");
DeductionItem selectedDeduction = (DeductionItem) request.getAttribute("selectedDeductionItem");
boolean hasSelected = selected != null;
boolean hasSelectedDeduction = selectedDeduction != null;
String selectedAttendanceName = hasSelected && selected.getAttendancePayRule() != null ? selected.getAttendancePayRule()
		: "";
boolean selectedAttendanceInList = false;
// 선택 근태명이 현재 근태유형 목록에 있는지 검사한다
// 選択した勤怠名が現在の勤怠類型一覧にあるかを検査する。
if (attendanceTypeList != null && !selectedAttendanceName.isBlank()) {
	for (AttendanceType attendanceType : attendanceTypeList) {
		if (selectedAttendanceName.equals(attendanceType.getAttendanceName())) {
	selectedAttendanceInList = true;
	break;
		}
	}
}
// 일괄지급이면 금액 행을 처음부터 보여 준다
// 一括支給なら金額行を最初から表示する。
boolean showBulkPayAmount = "일괄지급".equals(selectedAttendanceName);
%>

<%-- 공통 레이아웃에 제목·CSS·JS 파일명을 넘긴다. --%>
<%-- 共通レイアウトにタイトル・CSS・JSファイル名を渡す。 --%>
<%
request.setAttribute("pageTitle", "給与項目の設定");
request.setAttribute("pageSection", "基本環境");
request.setAttribute("pageDescription", "給与計算に使用する支給項目と控除項目を設定します。");
request.setAttribute("activeKey", "pay-item-settings");
request.setAttribute("pageCss", "environment.css");
request.setAttribute("pageJs", "pay-item-settings.js");
%>

<%-- 공통 헤더·사이드바를 끼운다. --%>
<%-- 共通ヘッダー・サイドバーを挿入する。 --%>
<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<%-- 지급항목 목록 테이블과 우측 편집 영역이다. --%>
<%-- 支給項目一覧テーブルと右側編集領域である。 --%>
<section class="source-config-block">
	<div class="source-config-list">
		<%-- 일본어 支給項目設定 은 지급항목 설정 제목이다. --%>
		<%-- 日本語の 支給項目設定 は支給項目設定のタイトルである。 --%>
		<div class="source-section-title">支給項目設定</div>
		<%-- 지급항목 목록 테이블이다. --%>
		<%-- 支給項目一覧テーブルである。 --%>
		<div class="table-wrap">
			<table class="data-table source-data-table">
				<thead>
					<tr>
						<%-- 支給項目 = name=payItemName, getPayItemName 이다. --%>
						<%-- 支給項目 = name=payItemName, getPayItemName である。 --%>
						<th>支給項目</th>
						<%-- 課税の有無 = taxableYn 이다. --%>
						<%-- 課税の有無 = taxableYn である。 --%>
						<th>課税の有無</th>
						<%-- 非課税限度額 = nonPayAmount 이다. --%>
						<%-- 非課税限度額 = nonPayAmount である。 --%>
						<th>非課税限度額</th>
						<%-- 端数処理単位 = truncationUnit 이다. --%>
						<%-- 端数処理単位 = truncationUnit である。 --%>
						<th>端数処理単位</th>
						<%-- 勤怠連動／一括支給 = attendancePayRule 이다. --%>
						<%-- 勤怠連動／一括支給 = attendancePayRule である。 --%>
						<th>勤怠連動／一括支給</th>
						<%-- 使用の有無 = useYn 이다. --%>
						<%-- 使用の有無 = useYn である。 --%>
						<th>使用の有無</th>
					</tr>
				</thead>
				<tbody>
					<%
					// 지급항목 목록을 행으로 그린다
					// 支給項目一覧を行として描画する。
					if (payItemList != null) {
						for (PayItem item : payItemList) {
					%>
					<%-- 행 클릭으로 선택 조회를 보낸다. --%>
					<%-- 行クリックで選択照会を送る。 --%>
					<tr style="cursor: pointer;"
						onclick="location.href='<%=ctx%>/Config/payitemsetselect.do?payItemId=<%=item.getPayItemId()%>'">
						<td><%=item.getPayItemName()%></td>
						<td><%=item.getTaxableLabel()%></td>
						<td><%=item.getNonPayAmountLabel()%></td>
						<td><%=item.getTruncationLabel()%></td>
						<td><%=item.getAttendancePayRuleLabel()%></td>
						<td><%=item.getUseLabel()%></td>
					</tr>
					<%
					}
					}
					%>
				</tbody>
			</table>
		</div>
	</div>
	<div class="source-config-editor">
		<div class="source-editor-head">支給項目</div>
		<%-- 지급항목 추가·수정·삭제 폼이다. --%>
		<%-- 支給項目の追加・修正・削除フォームである。 --%>
		<form id="payItemForm" method="post">
			<%-- 선택 지급항목 PK 와 비과세 상세 PK 를 숨긴다. --%>
			<%-- 選択した支給項目PKと非課税詳細PKを隠す。 --%>
			<input type="hidden" name="payItemId"
				value="<%=hasSelected ? selected.getPayItemId() : ""%>"> <input
				type="hidden" name="nonTaxId"
				value="<%=hasSelected && selected.getNonTaxId() != null ? selected.getNonTaxId() : ""%>">
			<table class="source-form-table">
				<tbody>
					<tr>
						<%-- 支給項目 입력칸이다. --%>
						<%-- 支給項目の入力欄である。 --%>
						<th>支給項目</th>
						<td class="span-3"><input type="text" class="input"
							name="payItemName" placeholder="支給項目を入力してください。"
							value="<%=hasSelected && selected.getPayItemName() != null ? selected.getPayItemName() : ""%>">
						</td>
					</tr>
					<tr>
						<th>課税の有無</th>
						<%-- 과세 여부를 고른다. --%>
						<%-- 課税の有無を選ぶ。 --%>
						<td class="span-3">
							<div class="check-list"
								data-nontax-popup-url="<%=ctx%>/Config/nontaxdetailpopup.do">
								<label> <input type="radio" name="taxableYn" value="Y"
									<%=!hasSelected || !"N".equalsIgnoreCase(selected.getTaxableYn()) ? "checked" : ""%>>
									全体課税
								</label> <label> <input type="radio" name="taxableYn" value="N"
									<%=hasSelected && "N".equalsIgnoreCase(selected.getTaxableYn()) ? "checked" : ""%>>
									非課税
								</label>
							</div>
						</td>
					</tr>
					<tr>
						<%-- 非課税項目名 입력칸이다. --%>
						<%-- 非課税項目名の入力欄である。 --%>
						<th>非課税項目名</th>
						<td class="span-3"><input type="text" class="input"
							name="nonTaxCategory" placeholder="入力してください。"
							value="<%=hasSelected && selected.getNonTaxCategory() != null ? selected.getNonTaxCategory() : ""%>">
						</td>
					</tr>
					<tr>
						<%-- 非課税限度額 입력칸이다. --%>
						<%-- 非課税限度額の入力欄である。 --%>
						<th>非課税限度額</th>
						<td class="span-3">
							<div class="money-control">
								<input type="text" class="input number" name="nonPayAmount"
									inputmode="numeric" pattern="[0-9,]*"
									placeholder="限度額を入力してください"
									value="<%=hasSelected ? selected.getNonPayAmountLabel() : ""%>">
								<span>円</span>
							</div>
						</td>
					</tr>
					<tr>
						<%-- 計算方法 입력칸이다. --%>
						<%-- 計算方法の入力欄である。 --%>
						<th>計算方法</th>
						<td class="span-3"><input type="text" class="input"
							name="calculationMethod" placeholder="計算方法を入力してください。"
							value="<%=hasSelected && selected.getCalculationMethod() != null ? selected.getCalculationMethod() : ""%>">
						</td>
					</tr>
					<tr>
						<%-- 端数処理単位 셀렉트이다. --%>
						<%-- 端数処理単位のセレクトである。 --%>
						<th>端数処理単位</th>
						<td class="span-3"><select class="select"
							name="truncationUnit">
								<option value="0"
									<%=!hasSelected || selected.getTruncationUnit() == null || selected.getTruncationUnit() == 0 ? "selected" : ""%>>なし</option>
								<option value="1"
									<%=hasSelected && Integer.valueOf(1).equals(selected.getTruncationUnit()) ? "selected" : ""%>>1円
									単位</option>
								<option value="10"
									<%=hasSelected && Integer.valueOf(10).equals(selected.getTruncationUnit()) ? "selected" : ""%>>10円
									単位</option>
								<option value="100"
									<%=hasSelected && Integer.valueOf(100).equals(selected.getTruncationUnit()) ? "selected" : ""%>>100円
									単位</option>
						</select></td>
					</tr>
					<tr>
						<%-- 勤怠連動／一括支給 셀렉트이다. --%>
						<%-- 勤怠連動／一括支給のセレクトである。 --%>
						<th>勤怠連動／一括支給</th>
						<td class="span-3"><select class="select"
							name="attendancePayRule">
								<option value=""
									<%=selectedAttendanceName.isBlank() ? "selected" : ""%>>選択してください</option>
								<%
								// 근태유형 목록으로 option 을 만든다
								// 勤怠類型一覧から option を作る。
								if (attendanceTypeList != null) {
									for (AttendanceType attendanceType : attendanceTypeList) {
										String name = attendanceType.getAttendanceName();
										if (name == null || name.isBlank()) {
									continue;
										}
								%>
								<option value="<%=name%>"
									<%=selectedAttendanceName.equals(name) ? "selected" : ""%>><%=name%></option>
								<%
								}
								}
								%>
								<%-- 목록에 없는 근태명을 옵션으로 유지한다. --%>
								<%-- 一覧にない勤怠名をオプションとして維持する。 --%>
								<%
								if (!selectedAttendanceName.isBlank() && !selectedAttendanceInList) {
								%>
								<option value="<%=selectedAttendanceName%>" selected><%=selectedAttendanceName%></option>
								<%
								}
								%>
						</select></td>
					</tr>
					<%-- 일괄지급일 때만 금액 입력 행을 보여 준다. --%>
					<%-- 一括支給のときだけ金額入力行を表示する。 --%>
					<tr id="bulkPayAmountRow"
						style="<%=showBulkPayAmount ? "" : "display: none;"%>">
						<th>一括支給額</th>
						<td class="span-3">
							<div class="money-control">
								<input type="text" class="input number" name="bulkPayAmount"
									value="<%=showBulkPayAmount ? selected.getBulkPayAmountLabel() : ""%>">
								<span>원</span>
							</div>
						</td>
					</tr>
					<tr>
						<%-- 使用の有無 라디오이다. --%>
						<%-- 使用の有無のラジオである。 --%>
						<th>使用の有無</th>
						<td class="span-3">
							<div class="check-list">
								<label> <input type="radio" name="useYn" value="Y"
									<%=!hasSelected || !"N".equalsIgnoreCase(selected.getUseYn()) ? "checked" : ""%>>
									使用
								</label> <label> <input type="radio" name="useYn" value="N"
									<%=hasSelected && "N".equalsIgnoreCase(selected.getUseYn()) ? "checked" : ""%>>
									使用しない
								</label>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			<%-- 지급항목 저장·삭제·초기화 버튼이다. --%>
			<%-- 支給項目の保存・削除・初期化ボタンである。 --%>
			<div class="source-editor-actions">
				<%-- 추가 버튼이다. --%>
				<%-- 追加ボタンである。 --%>
				<button type="submit" class="btn btn-primary"
					formaction="<%=ctx%>/Config/payitemsetinsert.do"
					onclick="return validatePayItemSubmit(false);">追加</button>
				<%-- 수정 버튼이다. --%>
				<%-- 修正ボタンである。 --%>
				<button type="submit" class="btn btn-blue"
					formaction="<%=ctx%>/Config/payitemsetupdate.do"
					onclick="return validatePayItemSubmit(true);">修正</button>
				<%-- 삭제 버튼이다. --%>
				<%-- 削除ボタンである。 --%>
				<button type="submit" class="btn"
					formaction="<%=ctx%>/Config/payitemsetdelete.do"
					onclick="return validatePayItemDelete();">削除</button>
				<%-- 내용 지우기 버튼이다. --%>
				<%-- 内容クリアボタンである。 --%>
				<button type="button" class="btn"
					onclick="clearPayItemForm('<%=ctx%>/Config/payitemsetclear.do')">内容
					クリア</button>
			</div>
		</form>
	</div>
</section>
<%-- 공제항목 목록 테이블과 우측 편집 영역이다. --%>
<%-- 控除項目一覧テーブルと右側編集領域である。 --%>
<section class="source-config-block">
	<div class="source-config-list">
		<%-- 일본어 控除項目の設定 은 공제항목 설정 제목이다. --%>
		<%-- 日本語の 控除項目の設定 は控除項目設定のタイトルである。 --%>
		<div class="source-section-title">控除項目の設定</div>
		<%-- 공제항목 목록 테이블이다. --%>
		<%-- 控除項目一覧テーブルである。 --%>
		<div class="table-wrap">
			<table class="data-table source-data-table">
				<thead>
					<tr>
						<%-- 控除項目 = deductionItemName 이다. --%>
						<%-- 控除項目 = deductionItemName である。 --%>
						<th>控除項目</th>
						<%-- 端数処理単位 = truncationUnit, getTruncationLabel 이다. --%>
						<%-- 端数処理単位 = truncationUnit, getTruncationLabel である。 --%>
						<th>端数処理単位</th>
						<%-- 使用の有無 = useYn, getUseLabel 이다. --%>
						<%-- 使用の有無 = useYn, getUseLabel である。 --%>
						<th>使用の有無</th>
						<%-- 備考 = remark, getRemarkLabel 이다. --%>
						<%-- 備考 = remark, getRemarkLabel である。 --%>
						<th>備考</th>
					</tr>
				</thead>
				<tbody>
					<%
					// 공제항목 목록을 행으로 그린다
					// 控除項目一覧を行として描画する。
					if (deductionItemList != null) {
						for (DeductionItem item : deductionItemList) {
					%>
					<%-- 행 클릭으로 공제 선택 조회를 보낸다. --%>
					<%-- 行クリックで控除の選択照会を送る。 --%>
					<tr style="cursor: pointer;"
						onclick="location.href='<%=ctx%>/Config/deductionitemsetselect.do?deductionItemId=<%=item.getDeductionItemId()%>'">
						<td><%=item.getDeductionItemName() != null ? item.getDeductionItemName() : ""%></td>
						<td><%=item.getTruncationLabel()%></td>
						<td><%=item.getUseLabel()%></td>
						<td><%=item.getRemarkLabel()%></td>
					</tr>
					<%
						}
					}
					%>
				</tbody>
			</table>
		</div>
	</div>
	<div class="source-config-editor">
		<div class="source-editor-head">控除項目</div>
		<%-- 공제항목 추가·수정·삭제 폼이다. --%>
		<%-- 控除項目の追加・修正・削除フォームである。 --%>
		<form id="deductionItemForm" method="post">
			<%-- 선택 공제항목 PK 를 숨긴다. --%>
			<%-- 選択した控除項目PKを隠す。 --%>
			<input type="hidden" name="deductionItemId"
				value="<%=hasSelectedDeduction ? selectedDeduction.getDeductionItemId() : ""%>">
			<table class="source-form-table">
				<tbody>
					<tr>
						<%-- 控除項目 입력칸이다. --%>
						<%-- 控除項目の入力欄である。 --%>
						<th>控除項目</th>
						<td class="span-3"><input type="text" class="input"
							name="deductionItemName" placeholder="控除項目を入力してください。"
							value="<%=hasSelectedDeduction && selectedDeduction.getDeductionItemName() != null ? selectedDeduction.getDeductionItemName() : ""%>"></td>
					</tr>
					<tr>
						<%-- 計算方法 입력칸이다. --%>
						<%-- 計算方法の入力欄である。 --%>
						<th>計算方法</th>
						<td class="span-3"><input type="text" class="input"
							name="deductionCalculationMethod" placeholder="計算方法を入力してください。"
							value="<%=hasSelectedDeduction && selectedDeduction.getCalculationMethod() != null ? selectedDeduction.getCalculationMethod() : ""%>"></td>
					</tr>
					<tr>
						<%-- 端数処理単位 셀렉트이다. --%>
						<%-- 端数処理単位のセレクトである。 --%>
						<th>端数処理単位</th>
						<td class="span-3"><select class="select"
							name="deductionTruncationUnit">
								<option value="0"
									<%=!hasSelectedDeduction || selectedDeduction.getTruncationUnit() == null || selectedDeduction.getTruncationUnit() == 0 ? "selected" : ""%>>なし</option>
								<option value="1"
									<%=hasSelectedDeduction && Integer.valueOf(1).equals(selectedDeduction.getTruncationUnit()) ? "selected" : ""%>>1円
									単位</option>
								<option value="10"
									<%=hasSelectedDeduction && Integer.valueOf(10).equals(selectedDeduction.getTruncationUnit()) ? "selected" : ""%>>10円
									単位</option>
								<option value="100"
									<%=hasSelectedDeduction && Integer.valueOf(100).equals(selectedDeduction.getTruncationUnit()) ? "selected" : ""%>>100円
									単位</option>
						</select></td>
					</tr>
					<tr>
						<%-- 備考 입력칸이다. --%>
						<%-- 備考の入力欄である。 --%>
						<th>備考</th>
						<td class="span-3"><input type="text" class="input"
							name="remark"
							value="<%=hasSelectedDeduction && selectedDeduction.getRemark() != null ? selectedDeduction.getRemark() : ""%>"></td>
					</tr>
					<tr>
						<%-- 使用の有無 라디오이다. --%>
						<%-- 使用の有無のラジオである。 --%>
						<th>使用の有無</th>
						<td class="span-3">
							<div class="check-list">
								<label><input type="radio" name="deductionUseYn"
									value="Y"
									<%=!hasSelectedDeduction || !"N".equalsIgnoreCase(selectedDeduction.getUseYn()) ? "checked" : ""%>>
									使用</label> <label><input type="radio" name="deductionUseYn"
									value="N"
									<%=hasSelectedDeduction && "N".equalsIgnoreCase(selectedDeduction.getUseYn()) ? "checked" : ""%>>
									使用しない</label>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			<%-- 공제항목 저장·삭제·초기화 버튼이다. --%>
			<%-- 控除項目の保存・削除・初期化ボタンである。 --%>
			<div class="source-editor-actions">
				<%-- 공제 추가 버튼이다. --%>
				<%-- 控除の追加ボタンである。 --%>
				<button type="submit" class="btn btn-primary"
					formaction="<%=ctx%>/Config/deductionitemsetinsert.do"
					onclick="return validateDeductionItemSubmit(false);">追加</button>
				<%-- 공제 수정 버튼이다. --%>
				<%-- 控除の修正ボタンである。 --%>
				<button type="submit" class="btn btn-blue"
					formaction="<%=ctx%>/Config/deductionitemsetupdate.do"
					onclick="return validateDeductionItemSubmit(true);">修正</button>
				<%-- 공제 삭제 버튼이다. --%>
				<%-- 控除の削除ボタンである。 --%>
				<button type="submit" class="btn"
					formaction="<%=ctx%>/Config/deductionitemsetdelete.do"
					onclick="return validateDeductionItemDelete();">削除</button>
				<%-- 공제 내용 지우기 버튼이다. --%>
				<%-- 控除の内容クリアボタンである。 --%>
				<button type="button" class="btn"
					onclick="clearDeductionItemForm('<%=ctx%>/Config/deductionitemsetclear.do')">内容
					クリア</button>
			</div>
		</form>
	</div>
</section>
<%
String errorMessage = (String) request.getAttribute("errorMessage");
if (errorMessage == null) {
	errorMessage = "";
}
String errorMessageAttr = errorMessage.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
%>
<input type="hidden" id="payItemSetErrorMessage" value="<%=errorMessageAttr%>">
<%-- 공통 레이아웃을 닫고 페이지 JS 를 붙인다. --%>
<%-- 共通レイアウトを閉じてページJSを付ける。 --%>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
