<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="config.model.AttendanceType"%>
<%@ page import="config.model.PayItem"%>
<%
List<PayItem> payItemList = (List<PayItem>) request.getAttribute("payItemList");
List<AttendanceType> attendanceTypeList = (List<AttendanceType>) request.getAttribute("attendanceTypeList");
PayItem selected = (PayItem) request.getAttribute("selectedPayItem");
boolean hasSelected = selected != null;
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
							<div class="check-list">
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
							value="<%=hasSelected && selected.getNonTaxCategory() != null ? selected.getNonTaxCategory() : ""%>">
						</td>
					</tr>
					<tr>
						<th>비과세 한도액</th>
						<td class="span-3">
							<div class="money-control">
								<input type="text" class="input number" name="nonPayAmount"
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
					<tr>
						<td>국민연금</td>
						<td>10원 단위</td>
						<td>사용</td>
						<td>기본항목</td>
					</tr>
					<tr>
						<td>건강보험</td>
						<td>10원 단위</td>
						<td>사용</td>
						<td>기본항목</td>
					</tr>
					<tr>
						<td>장기요양보험</td>
						<td>10원 단위</td>
						<td>사용</td>
						<td>기본항목</td>
					</tr>
					<tr>
						<td>고용보험</td>
						<td>10원 단위</td>
						<td>사용</td>
						<td>기본항목</td>
					</tr>
					<tr>
						<td>소득세</td>
						<td>10원 단위</td>
						<td>사용</td>
						<td>기본항목</td>
					</tr>
					<tr>
						<td>지방소득세</td>
						<td>10원 단위</td>
						<td>사용</td>
						<td>기본항목</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	<div class="source-config-editor">
		<div class="source-editor-head">공제항목</div>
		<table class="source-form-table">
			<tbody>
				<tr>
					<th>공제항목</th>
					<td class="span-3"><input type="text" class="input"
						placeholder="공제 항목을 입력하세요."></td>
				</tr>
				<tr>
					<th>절사단위</th>
					<td class="span-3"><select class="select"><option
								selected>없음</option>
							<option>1원 단위</option>
							<option>10원 단위</option>
							<option>100원 단위</option></select></td>
				</tr>
				<tr>
					<th>비고</th>
					<td class="span-3"><input type="text" class="input"></td>
				</tr>
				<tr>
					<th>사용여부</th>
					<td class="span-3"><div class="check-list">
							<label><input type="radio" name="ded-use" checked>
								사용</label><label><input type="radio" name="ded-use">
								사용안함</label>
						</div></td>
				</tr>
			</tbody>
		</table>
		<div class="source-editor-actions">
			<button type="button" class="btn btn-primary">추가</button>
			<button type="button" class="btn btn-blue">수정</button>
			<button type="button" class="btn">삭제</button>
			<button type="button" class="btn">내용 지우기</button>
		</div>
	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>