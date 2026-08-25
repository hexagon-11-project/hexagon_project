<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.AttendanceType"%>
<%@ page import="config.model.DeductionItem"%>
<%@ page import="config.model.PayItem"%>
<%
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
if (attendanceTypeList != null && !selectedAttendanceName.isBlank()) {
	for (AttendanceType attendanceType : attendanceTypeList) {
		if (selectedAttendanceName.equals(attendanceType.getAttendanceName())) {
	selectedAttendanceInList = true;
	break;
		}
	}
}
boolean showBulkPayAmount = "일괄지급".equals(selectedAttendanceName);
%>

<%
request.setAttribute("pageTitle", "給与項目の設定");
request.setAttribute("pageSection", "基本環境");
request.setAttribute("pageDescription", "給与計算に使用する支給項目と控除項目を設定します。");
request.setAttribute("activeKey", "pay-item-settings");
request.setAttribute("pageCss", "environment.css");
request.setAttribute("pageJs", "pay-item-settings.js");
%>

<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<section class="source-config-block">
	<div class="source-config-list">
		<div class="source-section-title">支給項目設定</div>
		<div class="table-wrap">
			<table class="data-table source-data-table">
				<thead>
					<tr>
						<th>支給項目</th>
						<th>課税の有無</th>
						<th>非課税限度額</th>
						<th>端数処理単位</th>
						<th>勤怠連動／一括支給</th>
						<th>使用の有無</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (payItemList != null) {
						for (PayItem item : payItemList) {
					%>
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
		<form id="payItemForm" method="post">
			<input type="hidden" name="payItemId"
				value="<%=hasSelected ? selected.getPayItemId() : ""%>"> <input
				type="hidden" name="nonTaxId"
				value="<%=hasSelected && selected.getNonTaxId() != null ? selected.getNonTaxId() : ""%>">
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>支給項目</th>
						<td class="span-3"><input type="text" class="input"
							name="payItemName" placeholder="支給項目を入力してください。"
							value="<%=hasSelected && selected.getPayItemName() != null ? selected.getPayItemName() : ""%>">
						</td>
					</tr>
					<tr>
						<th>課税の有無</th>
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
						<th>非課税項目名</th>
						<td class="span-3"><input type="text" class="input"
							name="nonTaxCategory" placeholder="入力してください。"
							value="<%=hasSelected && selected.getNonTaxCategory() != null ? selected.getNonTaxCategory() : ""%>">
						</td>
					</tr>
					<tr>
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
						<th>計算方法</th>
						<td class="span-3"><input type="text" class="input"
							name="calculationMethod" placeholder="計算方法を入力してください。"
							value="<%=hasSelected && selected.getCalculationMethod() != null ? selected.getCalculationMethod() : ""%>">
						</td>
					</tr>
					<tr>
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
						<th>勤怠連動／一括支給</th>
						<td class="span-3"><select class="select"
							name="attendancePayRule">
								<option value=""
									<%=selectedAttendanceName.isBlank() ? "selected" : ""%>>選択してください</option>
								<%
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
								if (!selectedAttendanceName.isBlank() && !selectedAttendanceInList) {
								%>
								<option value="<%=selectedAttendanceName%>" selected><%=selectedAttendanceName%></option>
								<%
								}
								%>
						</select></td>
					</tr>
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
			<div class="source-editor-actions">
				<button type="submit" class="btn btn-primary"
					formaction="<%=ctx%>/Config/payitemsetinsert.do"
					onclick="if (!document.querySelector('[name=payItemName]').value.trim()) { alert('支給項目を入力してください。'); location.href='<%=ctx%>/Config/payitemsetlist.do'; return false; }">追加</button>
				<button type="submit" class="btn btn-blue"
					formaction="<%=ctx%>/Config/payitemsetupdate.do"
					onclick="if (!document.querySelector('[name=payItemId]').value) { alert('修正する項目をリストから選択してください。'); return false; }">修正</button>
				<button type="submit" class="btn"
					formaction="<%=ctx%>/Config/payitemsetdelete.do"
					onclick="if (!document.querySelector('[name=payItemId]').value) { alert('削除する項目をリストから選択してください。'); return false; } return confirm('選択した支給項目を削除しますか？');">削除</button>
				<button type="button" class="btn"
					onclick="location.href='<%=ctx%>/Config/payitemsetclear.do'">内容
					クリア</button>
			</div>
		</form>
	</div>
</section>
<section class="source-config-block">
	<div class="source-config-list">
		<div class="source-section-title">控除項目の設定</div>
		<div class="table-wrap">
			<table class="data-table source-data-table">
				<thead>
					<tr>
						<th>控除項目</th>
						<th>端数処理単位</th>
						<th>使用の有無</th>
						<th>備考</th>
					</tr>
				</thead>
				<tbody>
					<%
					if (deductionItemList != null) {
						for (DeductionItem item : deductionItemList) {
					%>
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
		<form id="deductionItemForm" method="post">
			<input type="hidden" name="deductionItemId"
				value="<%=hasSelectedDeduction ? selectedDeduction.getDeductionItemId() : ""%>">
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>控除項目</th>
						<td class="span-3"><input type="text" class="input"
							name="deductionItemName" placeholder="控除項目を入力してください。"
							value="<%=hasSelectedDeduction && selectedDeduction.getDeductionItemName() != null ? selectedDeduction.getDeductionItemName() : ""%>"></td>
					</tr>
					<tr>
						<th>計算方法</th>
						<td class="span-3"><input type="text" class="input"
							name="deductionCalculationMethod" placeholder="計算方法を入力してください。"
							value="<%=hasSelectedDeduction && selectedDeduction.getCalculationMethod() != null ? selectedDeduction.getCalculationMethod() : ""%>"></td>
					</tr>
					<tr>
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
						<th>備考</th>
						<td class="span-3"><input type="text" class="input"
							name="remark"
							value="<%=hasSelectedDeduction && selectedDeduction.getRemark() != null ? selectedDeduction.getRemark() : ""%>"></td>
					</tr>
					<tr>
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
			<div class="source-editor-actions">
				<button type="submit" class="btn btn-primary"
					formaction="<%=ctx%>/Config/deductionitemsetinsert.do"
					onclick="if (!document.querySelector('[name=deductionItemName]').value.trim()) { alert('控除項目を入力してください。'); return false; }">追加</button>
				<button type="submit" class="btn btn-blue"
					formaction="<%=ctx%>/Config/deductionitemsetupdate.do"
					onclick="if (!document.querySelector('[name=deductionItemId]').value) { alert('修正する項目をリストから選択してください。'); return false; }">修正</button>
				<button type="submit" class="btn"
					formaction="<%=ctx%>/Config/deductionitemsetdelete.do"
					onclick="if (!document.querySelector('[name=deductionItemId]').value) { alert('削除する項目をリストから選択してください。'); return false; } return confirm('選択した控除項目を削除しますか？');">削除</button>
				<button type="button" class="btn"
					onclick="location.href='<%=ctx%>/Config/deductionitemsetclear.do'">内容
					クリア</button>
			</div>
		</form>
	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
