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
request.setAttribute("pageTitle", "급여항목 설정");
request.setAttribute("pageSection", "기본환경");
request.setAttribute("pageDescription", "급여 계산에 사용할 지급항목과 공제항목을 설정합니다.");
request.setAttribute("activeKey", "pay-item-settings");
request.setAttribute("pageCss", "environment.css");
request.setAttribute("pageJs", "pay-item-settings.js");
%>

<%@ include file="/WEB-INF/jspf/head.jspf"%><%@ include
	file="/WEB-INF/jspf/app-start.jspf"%>
<section class="source-config-block">
	<div class="source-config-list">
		<div class="source-section-title">지급항목 설정</div>
		<div class="table-wrap">
			<table class="data-table source-data-table">
				<thead>
					<tr>
						<th>지급항목</th>
						<th>과세여부</th>
						<th>비과세한도액</th>
						<th>절사단위</th>
						<th>근태연결/일괄지급</th>
						<th>사용여부</th>
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
		<div class="source-editor-head">지급항목</div>
		<form id="payItemForm" method="post">
			<input type="hidden" name="payItemId"
				value="<%=hasSelected ? selected.getPayItemId() : ""%>">
			<input type="hidden" name="nonTaxId"
				value="<%=hasSelected && selected.getNonTaxId() != null ? selected.getNonTaxId() : ""%>">
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>지급항목</th>
						<td class="span-3"><input type="text" class="input"
							name="payItemName" placeholder="지급 항목을 입력하세요."
							value="<%=hasSelected && selected.getPayItemName() != null ? selected.getPayItemName() : ""%>">
						</td>
					</tr>
					<tr>
						<th>과세여부</th>
						<td class="span-3">
							<div class="check-list"
								data-nontax-popup-url="<%=ctx%>/Config/nontaxdetailpopup.do">
								<label> <input type="radio" name="taxableYn" value="Y"
									<%=!hasSelected || !"N".equalsIgnoreCase(selected.getTaxableYn()) ? "checked" : ""%>>
									전체과세
								</label> <label> <input type="radio" name="taxableYn" value="N"
									<%=hasSelected && "N".equalsIgnoreCase(selected.getTaxableYn()) ? "checked" : ""%>>
									비과세
								</label>
							</div>
						</td>
					</tr>
					<tr>
						<th>비과세명</th>
						<td class="span-3"><input type="text" class="input"
							name="nonTaxCategory"
							placeholder="팝업에서 선택하거나 직접입력하세요."
							value="<%=hasSelected && selected.getNonTaxCategory() != null ? selected.getNonTaxCategory() : ""%>">
						</td>
					</tr>
					<tr>
						<th>비과세 한도액</th>
						<td class="span-3">
							<div class="money-control">
								<input type="text" class="input number" name="nonPayAmount"
									inputmode="numeric" pattern="[0-9,]*"
									placeholder="한도액을 입력하세요."
									value="<%=hasSelected ? selected.getNonPayAmountLabel() : ""%>">
								<span>원</span>
							</div>
						</td>
					</tr>
					<tr>
						<th>계산방법</th>
						<td class="span-3"><input type="text" class="input"
							name="calculationMethod" placeholder="계산방법을 입력하세요."
							value="<%=hasSelected && selected.getCalculationMethod() != null ? selected.getCalculationMethod() : ""%>">
						</td>
					</tr>
					<tr>
						<th>절사단위</th>
						<td class="span-3"><select class="select"
							name="truncationUnit">
								<option value="0"
									<%=!hasSelected || selected.getTruncationUnit() == null || selected.getTruncationUnit() == 0 ? "selected" : ""%>>없음</option>
								<option value="1"
									<%=hasSelected && Integer.valueOf(1).equals(selected.getTruncationUnit()) ? "selected" : ""%>>1원
									단위</option>
								<option value="10"
									<%=hasSelected && Integer.valueOf(10).equals(selected.getTruncationUnit()) ? "selected" : ""%>>10원
									단위</option>
								<option value="100"
									<%=hasSelected && Integer.valueOf(100).equals(selected.getTruncationUnit()) ? "selected" : ""%>>100원
									단위</option>
						</select></td>
					</tr>
					<tr>
						<th>근태연결/일괄지급</th>
						<td class="span-3"><select class="select"
							name="attendancePayRule">
								<option value=""
									<%=selectedAttendanceName.isBlank() ? "selected" : ""%>>선택해주세요</option>
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
						<th>일괄지급액</th>
						<td class="span-3">
							<div class="money-control">
								<input type="text" class="input number" name="bulkPayAmount"
									value="<%=showBulkPayAmount ? selected.getBulkPayAmountLabel() : ""%>">
								<span>원</span>
							</div>
						</td>
					</tr>
					<tr>
						<th>사용여부</th>
						<td class="span-3">
							<div class="check-list">
								<label> <input type="radio" name="useYn" value="Y"
									<%=!hasSelected || !"N".equalsIgnoreCase(selected.getUseYn()) ? "checked" : ""%>>
									사용
								</label> <label> <input type="radio" name="useYn" value="N"
									<%=hasSelected && "N".equalsIgnoreCase(selected.getUseYn()) ? "checked" : ""%>>
									사용안함
								</label>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			<div class="source-editor-actions">
				<button type="submit" class="btn btn-primary"
					formaction="<%=ctx%>/Config/payitemsetinsert.do"
					onclick="if (!document.querySelector('[name=payItemName]').value.trim()) { alert('지급항목을 입력하세요.'); location.href='<%=ctx%>/Config/payitemsetlist.do'; return false; }">추가</button>
				<button type="submit" class="btn btn-blue"
					formaction="<%=ctx%>/Config/payitemsetupdate.do"
					onclick="if (!document.querySelector('[name=payItemId]').value) { alert('수정할 항목을 리스트에서 선택하세요.'); return false; }">수정</button>
				<button type="submit" class="btn"
					formaction="<%=ctx%>/Config/payitemsetdelete.do"
					onclick="if (!document.querySelector('[name=payItemId]').value) { alert('삭제할 항목을 리스트에서 선택하세요.'); return false; } return confirm('선택한 지급항목을 삭제하시겠습니까?');">삭제</button>
				<button type="button" class="btn"
					onclick="location.href='<%=ctx%>/Config/payitemsetclear.do'">내용
					지우기</button>
			</div>
		</form>
	</div>
</section>
<section class="source-config-block">
	<div class="source-config-list">
		<div class="source-section-title">공제항목 설정</div>
		<div class="table-wrap">
			<table class="data-table source-data-table">
				<thead>
					<tr>
						<th>공제항목</th>
						<th>절사단위</th>
						<th>사용여부</th>
						<th>비고</th>
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
		<div class="source-editor-head">공제항목</div>
		<form id="deductionItemForm" method="post">
			<input type="hidden" name="deductionItemId"
				value="<%=hasSelectedDeduction ? selectedDeduction.getDeductionItemId() : ""%>">
			<table class="source-form-table">
				<tbody>
					<tr>
						<th>공제항목</th>
						<td class="span-3"><input type="text" class="input"
							name="deductionItemName" placeholder="공제 항목을 입력하세요."
							value="<%=hasSelectedDeduction && selectedDeduction.getDeductionItemName() != null ? selectedDeduction.getDeductionItemName() : ""%>"></td>
					</tr>
					<tr>
						<th>계산방법</th>
						<td class="span-3"><input type="text" class="input"
							name="deductionCalculationMethod" placeholder="계산방법을 입력하세요."
							value="<%=hasSelectedDeduction && selectedDeduction.getCalculationMethod() != null ? selectedDeduction.getCalculationMethod() : ""%>"></td>
					</tr>
					<tr>
						<th>절사단위</th>
						<td class="span-3"><select class="select"
							name="deductionTruncationUnit">
								<option value="0"
									<%=!hasSelectedDeduction || selectedDeduction.getTruncationUnit() == null || selectedDeduction.getTruncationUnit() == 0 ? "selected" : ""%>>없음</option>
								<option value="1"
									<%=hasSelectedDeduction && Integer.valueOf(1).equals(selectedDeduction.getTruncationUnit()) ? "selected" : ""%>>1원
									단위</option>
								<option value="10"
									<%=hasSelectedDeduction && Integer.valueOf(10).equals(selectedDeduction.getTruncationUnit()) ? "selected" : ""%>>10원
									단위</option>
								<option value="100"
									<%=hasSelectedDeduction && Integer.valueOf(100).equals(selectedDeduction.getTruncationUnit()) ? "selected" : ""%>>100원
									단위</option>
						</select></td>
					</tr>
					<tr>
						<th>비고</th>
						<td class="span-3"><input type="text" class="input"
							name="remark"
							value="<%=hasSelectedDeduction && selectedDeduction.getRemark() != null ? selectedDeduction.getRemark() : ""%>"></td>
					</tr>
					<tr>
						<th>사용여부</th>
						<td class="span-3">
							<div class="check-list">
								<label><input type="radio" name="deductionUseYn" value="Y"
									<%=!hasSelectedDeduction || !"N".equalsIgnoreCase(selectedDeduction.getUseYn()) ? "checked" : ""%>>
									사용</label> <label><input type="radio" name="deductionUseYn" value="N"
									<%=hasSelectedDeduction && "N".equalsIgnoreCase(selectedDeduction.getUseYn()) ? "checked" : ""%>>
									사용안함</label>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			<div class="source-editor-actions">
				<button type="submit" class="btn btn-primary"
					formaction="<%=ctx%>/Config/deductionitemsetinsert.do"
					onclick="if (!document.querySelector('[name=deductionItemName]').value.trim()) { alert('공제항목을 입력하세요.'); return false; }">추가</button>
				<button type="submit" class="btn btn-blue"
					formaction="<%=ctx%>/Config/deductionitemsetupdate.do"
					onclick="if (!document.querySelector('[name=deductionItemId]').value) { alert('수정할 항목을 리스트에서 선택하세요.'); return false; }">수정</button>
				<button type="submit" class="btn"
					formaction="<%=ctx%>/Config/deductionitemsetdelete.do"
					onclick="if (!document.querySelector('[name=deductionItemId]').value) { alert('삭제할 항목을 리스트에서 선택하세요.'); return false; } return confirm('선택한 공제항목을 삭제하시겠습니까?');">삭제</button>
				<button type="button" class="btn"
					onclick="location.href='<%=ctx%>/Config/deductionitemsetclear.do'">내용
					지우기</button>
			</div>
		</form>
	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>
