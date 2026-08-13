<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>급여입력/관리 | HEXAGON PAY</title>
<%@ include file="../../jspf/head.jspf"%>
<style>
/* 화면이 좁아져도 좌우 영역이 찌그러지거나 아래로 떨어지지 않도록 고정하는 스타일 */
body {
    min-width: 1200px; /* 화면 전체의 최소 가로폭을 강제로 잡아줍니다 */
}
/* 계산방법 off 시 계산방법 입력 행 숨기기 */
.calc-method-row {
	display: table-row;
}

.calc-method-row.off {
	display: none;
}

/* 스위치 토글 디자인 */
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
		<!-- 화면 상단 타이틀 영역 -->
		<div class="page-header"
			style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
			<img src="https://img.payzon.co.kr/_commonImg/pay_tit_img.gif"
				width="50" height="45" alt="급여입력 및 관리">
			<div>
				<h2 style="margin: 0; font-size: 20px; font-weight: bold;">급여입력/관리</h2>
				<p class="text-muted"
					style="margin: 3px 0 0 0; font-size: 12px; color: #666;">
					월별, 사원별 급여 및 상여금 정보를 입력, 저장, 관리하는 메뉴입니다. <span
						style="color: #d9534f; font-weight: bold;">귀속연월, 급여차수를
						확인하세요!!</span>
				</p>
			</div>
		</div>

		<!-- [조각 1] 상단 검색 바 및 기능 버튼 영역 불러오기 -->
		<jsp:include page="search_area.jsp" />

		<!-- 메인 컨텐츠 그리드 레이아웃 (좌/우 분할) -->
		<div style="display: flex; gap: 15px; align-items: flex-start;">

			<!-- [조각 2] 좌측: 대상 사원 목록 그리드 불러오기 -->
			<jsp:include page="employee_list.jsp" />

			<!-- [조각 3] 우측: 소득 탭 및 상세 금액 입력 폼 불러오기 -->
			<jsp:include page="payroll_detail.jsp" />

		</div>

		<!-- 4. 하단 급여 종합정보 집계 영역 -->
		<div style="margin-top: 25px;">
			<div style="font-weight: bold; margin-bottom: 8px; font-size: 14px;">급여
				종합정보</div>
			<div style="display: flex; gap: 10px;">
				<div
					style="flex: 1; background: #95a5a6; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">월 합계</div>
					<div style="font-size: 20px; font-weight: bold; margin-top: 5px;">${summaryInfo.totalCount}
						건</div>
				</div>
				<div
					style="flex: 2; background: #5bc0de; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">지급 총액</div>
					<div style="font-size: 20px; font-weight: bold; margin-top: 5px;">
						<fmt:formatNumber value="${summaryInfo.totalGiveAmount}"
							pattern="#,###" />
						원
					</div>
				</div>
				<div
					style="flex: 2; background: #d9534f; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">공제 총액</div>
					<div style="font-size: 20px; font-weight: bold; margin-top: 5px;">
						<fmt:formatNumber value="${summaryInfo.totalDeduAmount}"
							pattern="#,###" />
						원
					</div>
				</div>
				<div
					style="flex: 2; background: #4e5d6c; color: white; padding: 15px; border-radius: 4px; text-align: center;">
					<div style="font-size: 12px;">실지급액</div>
					<div style="font-size: 20px; font-weight: bold; margin-top: 5px;">
						<fmt:formatNumber value="${summaryInfo.totalRealAmount}"
							pattern="#,###" />
						원
					</div>
				</div>
			</div>
		</div>
	</main>
	<!-- ================= [Tip 모달창 시작] ================= -->
<div id="tipModalOverlay" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 9999; align-items: center; justify-content: center;">
    <div style="background: white; width: 550px; max-height: 80vh; border-radius: 5px; overflow: hidden; display: flex; flex-direction: column; box-shadow: 0 5px 15px rgba(0,0,0,0.5); font-family: 'Malgun Gothic', sans-serif;">
        
        <!-- 모달 헤더 -->
        <div style="background: #333; color: white; padding: 10px 15px; display: flex; justify-content: space-between; align-items: center;">
            <span style="font-weight: bold; font-size: 14px;">Payzon Tip</span>
            <button type="button" onclick="closeTipModal()" style="background: transparent; border: none; color: white; font-size: 20px; cursor: pointer; line-height: 1;">×</button>
        </div>

        <!-- 모달 본문 (스크롤 영역) -->
        <div style="padding: 20px; overflow-y: auto; flex: 1;">
            <h3 style="color: #e82c6d; margin-top: 0; margin-bottom: 20px; font-weight: bold; font-size: 18px;">급여입력/관리</h3>

            <!-- 설명 항목 반복 템플릿 스타일 -->
            <style>
                .tip-item { margin-bottom: 18px; }
                .tip-title { font-weight: bold; font-size: 13px; margin-bottom: 6px; display: flex; align-items: center; gap: 6px; }
                .tip-num { background: #fb6a7e; color: white; padding: 2px 6px; border-radius: 4px; font-size: 11px; }
                .tip-desc { font-size: 12px; color: #555; line-height: 1.6; padding-left: 25px; word-break: keep-all; }
            </style>

            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">1</span> 귀속연월</div>
                <div class="tip-desc">급여를 계산의 대상이 되는 연도와 월을 선택합니다.</div>
            </div>
            
            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">2</span> 급여차수</div>
                <div class="tip-desc">기본값으로 [급여-1차]로 표기되며, 한 달에 급여를 분할하여 지급할 경우 최대 급여-5차까지 나누어 급여계산 및 급여테이블을 생성하실 수 있습니다.</div>
            </div>

            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">3</span> 정산기간</div>
                <div class="tip-desc">[사용자 정보] 메뉴에서 설정한 급여 산정기간에 따라 정산기간이 자동 표시되며, 정산기간의 변동이 있을 경우 사용자가 직접 수정 입력할 수 있습니다.</div>
            </div>

            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">4</span> 급여지급일</div>
                <div class="tip-desc">선택한 귀속연월의 급여가 실제로 지급되는 급여지급일을 선택합니다.</div>
            </div>

            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">5</span> [지난급여 불러오기]</div>
                <div class="tip-desc">이전 월의 급여정보를 선택한 귀속연월로 동일하게 불러오기 합니다. 급여정보가 동일하거나 변동이 크지 않을 경우 이전 월의 급여정보를 불러오기 하여 바로 적용할 수 있으며, 변동사항만 수정 입력할 수 있습니다.</div>
            </div>

            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">6</span> [신규추가]</div>
                <div class="tip-desc">[신규추가] 버튼을 클릭하시면 사원 리스트 레이어가 표시됩니다. 여기서 급여를 계산하고자 하는 대상 사원들을 체크하신 후 하단의 [사원선택] 버튼을 클릭하시면 레이어가 닫힌 후 급여정보 리스트에 추가됩니다.</div>
            </div>

            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">7</span> [전체삭제]</div>
                <div class="tip-desc">선택한 귀속연월의 급여정보 리스트를 모두 삭제합니다. 리스트에서 삭제된 급여정보는 복구할 수 없으며, [신규추가] 또는 [지난급여 불러오기] 등으로 새로 급여정보를 등록할 수 있습니다.</div>
            </div>

            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">8</span> [삭제]</div>
                <div class="tip-desc">선택한 귀속연월의 급여정보 리스트에서 삭제하고자 하는 급여정보만 선택하여 삭제합니다. 리스트에서 삭제된 급여정보는 복구할 수 없으며, [신규추가]를 통하여 새로 급여정보를 등록할 수 있습니다.</div>
            </div>

            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">9</span> 급여정보 리스트</div>
                <div class="tip-desc">급여정보 리스트에서 급여정보를 입력할 대상을 선택합니다. 우측 급여입력란에 선택한 사원의 급여를 입력하여 급여정보를 저장할 수 있습니다. 이미 급여정보가 저장된 사원을 선택하였을 경우 우측에 저장된 급여정보가 표시되어 확인 및 수정 입력하실 수 있습니다.</div>
            </div>

            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">10</span> 일반소득 탭</div>
                <div class="tip-desc">일반소득 탭은 사원등록 시에 갑근세 설정에서 [근로소득자 갑근세(근로소득간이세액표)]로 설정한 근로자들이 해당되며, 일반소득 탭에서는 근로소득간이세액표에 근거하여 갑근세 공제액을 계산합니다.</div>
            </div>

            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">11</span> 사업소득 탭</div>
                <div class="tip-desc">사업소득 탭은 사원등록 시에 갑근세 설정에서 [사업소득자 갑근세(3.3% 공제)]로 설정한 근로자들이 해당되며, 사업소득 탭에서는 지급합계의 3.3%를 갑근세 공제액으로 계산합니다.</div>
            </div>

            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">12</span> [자동계산]</div>
                <div class="tip-desc">지급항목에 해당하는 금액을 모두 입력하신 후 [자동계산] 버튼을 클릭하시면 4대보험 및 갑근세를 자동으로 계산하여 금액을 표시합니다.</div>
            </div>

            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">13</span> [저장]</div>
                <div class="tip-desc">지급액 및 공제액을 입력하고 실지급액이 정상적으로 계산되었을 경우 [저장] 버튼을 클릭하시면 왼쪽에 급여정보 리스트에 입력한 급여정보가 저장되면서 리스트가 갱신됩니다. 이미 급여정보가 등록된 사원의 경우 새로 입력한 급여정보로 수정됩니다.</div>
            </div>

            <div class="tip-item">
                <div class="tip-title"><span class="tip-num">14</span> [내용지우기]</div>
                <div class="tip-desc">입력한 지급액 및 공제액을 모두 지우고 새로 작성할 수 있습니다.</div>
            </div>
        </div>
    </div>
</div>
<!-- ================= [Tip 모달창 끝] ================= -->
	

	<!-- 스크립트 기능 정의 -->
	<script>
		function reloadPayrollData() {
			document.getElementById("searchForm").submit();
		}

		function switchIncomeTab(incomeType) {
			document.getElementById("incomeTypeParam").value = incomeType;
			document.getElementById("searchForm").submit();
		}

		// ★ [핵심 수정] 좌측 사원 클릭 시 AJAX로 우측 데이터 갱신하기
		function selectEmployeeRow(rowElement, payrollEmployeeId) {
			// 1. 선택된 행 색상 변경 (기존 로직 유지)
			var rows = document.querySelectorAll("#employeeTableBody tr");
			if(rows.length > 0) {
				rows.forEach(function(r) { r.style.background = ""; });
			}
			rowElement.style.background = "#eef4fb";
			
			var selectedIdInput = document.getElementById("selectedPayrollEmployeeId");
			if(selectedIdInput) selectedIdInput.value = payrollEmployeeId;

			// 2. AJAX 비동기 통신으로 상세 데이터 요청 (화면 새로고침 없음)
			// 주의: commandHandlerUri.properties에 등록한 주소와 일치해야 합니다!
			var contextPath = "${pageContext.request.contextPath}";
			var url = contextPath + "/payroll/detailAjax.do?payrollEmployeeId=" + payrollEmployeeId;

			fetch(url)
				.then(response => response.json())
				.then(data => {
					console.log("서버에서 성공적으로 가져온 데이터:", data);
					
					// 3. 받아온 데이터(data.pays, data.deductions)를 우측 payroll_detail.jsp 폼에 채워넣기
					// 이 부분은 우측 폼의 input 태그 id 설정에 따라 맞춤 작성이 필요합니다.
					
					// [예시] 지급 항목 입력 처리
					if(data.pays && data.pays.length > 0) {
						data.pays.forEach(item => {
							// item.name ("기본급", "식비" 등) 을 이용해 해당 input을 찾아 item.amount 입력
							// 예: document.getElementById("pay_amount_" + item.name).value = item.amount;
						});
					}

					// [예시] 공제 항목 입력 처리
					if(data.deductions && data.deductions.length > 0) {
						data.deductions.forEach(item => {
							// 예: document.getElementById("deduction_amount_" + item.name).value = item.amount;
						});
					}
					
					// 값을 전부 채워넣은 후 하단 실지급액 등 재계산 (구현하신 함수 호출)
					calculateRealPay();
				})
				.catch(error => {
					console.error("데이터를 가져오는 중 오류 발생:", error);
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

	        // 지급항목(pay-input) 싹 다 더하기 (식비 20만 원도 여기에 자동으로 포함됩니다)
	        const payInputs = document.querySelectorAll('.pay-input');
	        payInputs.forEach(function(input) {
	            totalPay += parseInt(input.value) || 0;
	        });

	        // 공제항목(deduction-input) 싹 다 더하기
	        const deductionInputs = document.querySelectorAll('.deduction-input');
	        deductionInputs.forEach(function(input) {
	            totalDeduction += parseInt(input.value) || 0;
	        });

	        const netPay = totalPay - totalDeduction;

	        // 화면에 숫자 찍어주기 (요소가 존재하는지 먼저 확인 후 출력)
	        const payResultElem = document.getElementById('totalPayResult');
	        const deduResultElem = document.getElementById('totalDeductionResult');
	        const netResultElem = document.getElementById('netPayResult');

	        if (payResultElem) payResultElem.innerText = totalPay.toLocaleString();
	        if (deduResultElem) deduResultElem.innerText = totalDeduction.toLocaleString();
	        if (netResultElem) netResultElem.innerText = netPay.toLocaleString();
	    }

	    // 3. 내용 지우기 버튼
	    function clearPayrollForm() {
	        const form = document.getElementById("payrollDetailForm");
	        if (form) {
	            form.reset();
	            calculateRealPay(); // 다 지운 후 0원으로 갱신
	        }
	    }

	    // 4. 나머지 탭 및 팝업 관련 기능들
	    function reloadPayrollData() {
	        document.getElementById("searchForm").submit();
	    }

	    function switchIncomeTab(incomeType) {
	        document.getElementById("incomeTypeParam").value = incomeType;
	        document.getElementById("searchForm").submit();
	    }

	    function openEmployeeSelectModal() {
	        var contextPath = "${pageContext.request.contextPath}";
	        var popupUrl = contextPath + "/payroll/employeeAddModal.do";
	        window.open(popupUrl, "EmpSelectModal", "width=700,height=600,left=200,top=100,scrollbars=yes");
	    }

	    function addEmployeesToMain(selectedEmpIds) {
	        console.log("추가된 사원 ID 목록:", selectedEmpIds);
	        alert("선택된 사원이 목록에 추가되었습니다.");
	    }

		function clearPayrollForm() {
			document.getElementById("payrollDetailForm").reset();
		}

		// 1. 신규추가 팝업(모달) 열기
		function openEmployeeSelectModal() {
    var contextPath = "${pageContext.request.contextPath}";
    // 💡 핵심: .jsp가 아니라 반드시 우리가 매핑한 .do 주소를 불러야 합니다!
    var popupUrl = contextPath + "/payroll/employeeAddModal.do";
    
    window.open(popupUrl, "EmpSelectModal", "width=700,height=600,left=200,top=100,scrollbars=yes");
}

		// 2. 팝업창에서 선택된 사원들을 메인 화면으로 받아오는 함수
		function addEmployeesToMain(selectedEmpIds) {
			console.log("추가된 사원 ID 목록:", selectedEmpIds);
			alert("선택된 사원이 목록에 추가되었습니다.");
		}
		
		// Tip 모달창 열기
	    function openTipModal() {
	        document.getElementById('tipModalOverlay').style.display = 'flex';
	    }

	    // Tip 모달창 닫기
	    function closeTipModal() {
	        document.getElementById('tipModalOverlay').style.display = 'none';
	    }
	</script>

	<%@ include file="../../jspf/app-end.jspf"%>
</body>
</html>