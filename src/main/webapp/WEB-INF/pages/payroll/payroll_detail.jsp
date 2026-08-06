<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div
	style="flex: 1.3; background: #fff; border: 1px solid #ddd; padding: 10px; font-size: 12px;">

	<!-- 탭 버튼 영역 (디자인 수정 적용) -->
	<div
		style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 10px; border-bottom: 2px solid #222;">
		<div style="display: flex; gap: 2px;">
			<!-- 일반소득 탭 -->
			<button type="button" onclick="switchIncomeTab('GENERAL')"
				style="background: ${param.incomeType == 'BUSINESS' ? '#999999' : '#009688'}; 
				color: white; 
				border: none; 
				border-radius: 5px 5px 0 0; 
				padding: 8px 25px; 
				font-weight: bold; 
				font-size: 13px; 
				cursor: pointer;">일반소득</button>

			<!-- 사업소득/기타소득 탭 -->
			<button type="button" onclick="switchIncomeTab('BUSINESS')"
				style="background: ${param.incomeType == 'BUSINESS' ? '#009688' : '#999999'}; 
				color: white; 
				border: none; 
				border-radius: 5px 5px 0 0; 
				padding: 8px 25px; 
				font-weight: bold; 
				font-size: 13px; 
				cursor: pointer;">사업소득/기타소득</button>
		</div>

		<!-- 우측 기능 버튼 -->
		<div style="display: flex; gap: 5px; margin-bottom: 5px;">
			<button type="button" class="btn btn-default btn-xs"
				style="background: #f8f9fa; border: 1px solid #ccc; padding: 3px 8px;">계산방법
				불러오기</button>
			<button type="button" class="btn btn-dark btn-xs" onclick="openTipModal()" style="background: #333; color: #fff; border: none; padding: 3px 8px;">
    <i class="fas fa-question-circle"></i> Tip
</button>
		</div>
	</div>

	<!-- 폼 시작 -->
	<form action="${pageContext.request.contextPath}/payroll/save.do"
		method="POST" id="payrollDetailForm">
		<input type="hidden" name="payrollEmployeeId"
			id="selectedPayrollEmployeeId"
			value="${selectedEmployee.payrollEmployeeId}">

		<div style="display: flex; gap: 10px;">
			<!-- 지급항목 테이블 -->
			<div style="flex: 1;">
				<div
					style="display: flex; justify-content: space-between; align-items: center; background: #f4f6f9; padding: 4px 8px; border: 1px solid #ddd; font-weight: bold;">
					<span>지급항목 <span
						style="background: #009688; color: white; font-size: 9px; padding: 1px 4px; border-radius: 2px;">M</span></span>
					<button type="button" class="btn btn-xs btn-dark"
						onclick="executeAutoCalculation()"
						style="background: #222; color: #fff; font-size: 10px; padding: 1px 6px; border: none;">±
						자동계산</button>
				</div>
				<table class="table table-bordered table-condensed"
					style="width: 100%; border-collapse: collapse; margin-bottom: 0;">
					<tr>
						<td style="padding: 4px; background: #fafafa; width: 40%;">기본급</td>
						<!-- [수정] 클래스에 pay-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="baseWage" id="input_baseWage" value="0"
							oninput="calculateRealPay()" class="form-control input-sm pay-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">식비<span
							style="color: red;">[비]</span></td>
						<!-- [수정] 클래스에 pay-input 추가 (계산에는 포함되어야 함) -->
						<td style="padding: 4px;"><input type="number"
							name="mealAllowance" id="input_mealAllowance" value="200000"
							class="form-control input-sm pay-input" readonly
							style="width: 100%; height: 24px; padding: 2px; text-align: right; background-color: #e9ecef; cursor: not-allowed;">
						</td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;">
							<input
							type="text" readonly class="form-control input-sm"
							style="width: 100%; height: 22px; background-color: #e9ecef; cursor: not-allowed;">
						</td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">보육수당</td>
						<!-- [수정] 클래스에 pay-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="childAllowance" id="input_childAllowance" value="0"
							oninput="calculateRealPay()" class="form-control input-sm pay-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">직책수당</td>
						<!-- [수정] 클래스에 pay-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="positionAllowance" id="input_positionAllowance" value="0"
							oninput="calculateRealPay()" class="form-control input-sm pay-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">차량유지비<span
							style="color: red;">[비]</span></td>
						<!-- [수정] 클래스에 pay-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="carAllowance" id="input_carAllowance" value="0"
							oninput="calculateRealPay()" class="form-control input-sm pay-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">근속수당</td>
						<!-- [수정] 클래스에 pay-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="tenureAllowance" id="input_tenureAllowance" value="0"
							oninput="calculateRealPay()" class="form-control input-sm pay-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">당직수당</td>
						<!-- [수정] 클래스에 pay-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="nightAllowance" id="input_nightAllowance" value="0"
							oninput="calculateRealPay()" class="form-control input-sm pay-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">상여금</td>
						<!-- [수정] 클래스에 pay-input 추가 -->
						<td style="padding: 4px;"><input type="number" name="bonus"
							id="input_bonus" value="0" oninput="calculateRealPay()"
							class="form-control input-sm pay-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">휴일수당</td>
						<!-- [수정] 클래스에 pay-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="holidayAllowance" id="input_holidayAllowance" value="0"
							oninput="calculateRealPay()" class="form-control input-sm pay-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>
				</table>
			</div>

			<!-- 공제항목 테이블 -->
			<div style="flex: 1;">
				<div
					style="background: #f4f6f9; padding: 4px 8px; border: 1px solid #ddd; font-weight: bold; display: flex; justify-content: space-between; align-items: center;">
					<span>공제항목 <span
						style="background: #d9534f; color: white; font-size: 9px; padding: 1px 4px; border-radius: 2px;">M</span></span>
				</div>
				<table class="table table-bordered table-condensed"
					style="width: 100%; border-collapse: collapse; margin-bottom: 0;">
					<tr>
						<td style="padding: 4px; background: #fafafa; width: 40%;">국민연금</td>
						<!-- [수정] 클래스에 deduction-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="nationalPension" id="input_nationalPension" value="0"
							oninput="calculateRealPay()" class="form-control input-sm deduction-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">건강보험</td>
						<!-- [수정] 클래스에 deduction-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="healthInsurance" id="input_healthInsurance" value="0"
							oninput="calculateRealPay()" class="form-control input-sm deduction-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">장기요양보험</td>
						<!-- [수정] 클래스에 deduction-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="longTermCare" id="input_longTermCare" value="0"
							oninput="calculateRealPay()" class="form-control input-sm deduction-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">고용보험</td>
						<!-- [수정] 클래스에 deduction-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="employmentInsurance" id="input_employmentInsurance"
							value="0" oninput="calculateRealPay()"
							class="form-control input-sm deduction-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">소득세</td>
						<!-- [수정] 클래스에 deduction-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="incomeTax" id="input_incomeTax" value="0"
							oninput="calculateRealPay()" class="form-control input-sm deduction-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">지방소득세</td>
						<!-- [수정] 클래스에 deduction-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="localIncomeTax" id="input_localIncomeTax" value="0"
							oninput="calculateRealPay()" class="form-control input-sm deduction-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td style="padding: 4px; background: #fafafa;">상조회비</td>
						<!-- [수정] 클래스에 deduction-input 추가 -->
						<td style="padding: 4px;"><input type="number"
							name="mutualAid" id="input_mutualAid" value="0"
							oninput="calculateRealPay()" class="form-control input-sm deduction-input"
							style="width: 100%; height: 24px; padding: 2px; text-align: right;"></td>
					</tr>
					<tr class="calc-method-row">
						<td
							style="padding: 4px; background: #fafafa; font-size: 11px; color: #666;">계산방법</td>
						<td style="padding: 4px;"><input type="text" readonly
							class="form-control input-sm"
							style="width: 100%; height: 22px; background: #f9f9f9;"></td>
					</tr>

					<tr>
						<td colspan="2"
							style="background: #fff; border: none; height: 48px;"></td>
					</tr>
				</table>
			</div>
		</div>

		<!-- 합계 및 버튼 영역 -->
		<div
			style="display: flex; border: 1px solid #ddd; margin-top: 10px; font-weight: bold; text-align: center;">
			<div
				style="flex: 1; background: #f4f6f9; padding: 6px; border-right: 1px solid #ddd; font-size: 13px;">
				<!-- [수정] JS에서 값을 바꿀 수 있도록 id="totalPayResult" 로 변경 -->
				지급총액 : <span id="totalPayResult" style="color: #337ab7;"><fmt:formatNumber
						value="${selectedEmployee.totalPayAmount != null ? selectedEmployee.totalPayAmount : 0}" pattern="#,###" /></span> 원
			</div>
			<div
				style="flex: 1; background: #f4f6f9; padding: 6px; font-size: 13px;">
				<!-- [수정] JS에서 값을 바꿀 수 있도록 id="totalDeductionResult" 로 변경 -->
				공제총액 : <span id="totalDeductionResult" style="color: #d9534f;"><fmt:formatNumber
						value="${selectedEmployee.totalDeductionAmount != null ? selectedEmployee.totalDeductionAmount : 0}" pattern="#,###" /></span> 원
			</div>
		</div>
		<div style="background: #204d74; color: white; text-align: center; padding: 10px; font-weight: bold; font-size: 15px; margin-top: 5px; border-radius: 2px;">
			<!-- [수정] JS에서 값을 바꿀 수 있도록 실지급액 숫자를 감싸는 span 태그 추가 -->
			실지급액 :
			<span id="netPayResult">
				<fmt:formatNumber value="${selectedEmployee.netPayAmount != null ? selectedEmployee.netPayAmount : 0}" pattern="#,###" />
			</span>
			원
		</div>

		<div
			style="text-align: right; margin-top: 10px; display: flex; justify-content: flex-end; gap: 5px;">
			<button type="submit" class="btn btn-primary btn-sm"
				style="background: #337ab7; color: white; border: none; padding: 6px 18px; font-weight: bold;">저장</button>
			<button type="button" class="btn btn-default btn-sm"
				onclick="clearPayrollForm()"
				style="background: #ccc; color: #333; border: 1px solid #bbb; padding: 6px 15px;">내용
				지우기</button>
		</div>
	</form>
</div>