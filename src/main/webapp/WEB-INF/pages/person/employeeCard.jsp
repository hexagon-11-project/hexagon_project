<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
request.setAttribute("pageTitle", "인사기록 카드");
request.setAttribute("pageSection", "인사관리");
request.setAttribute("pageDescription", "선택한 사원의 인적사항, 학력, 경력 등 인사기록을 카드 형식으로 확인합니다.");
request.setAttribute("activeKey", "personnel-card");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<!-- 상단 검색바 (form 태그 추가) -->
<form action="" method="GET" id="searchForm">
	<section class="filter-bar">
		<div class="field ">
			<label>사원</label>
			<!-- Controller에서 넘어온 empList(사원목록)를 반복문으로 동적 세팅 -->
			<select class="select" name="employeeId">
				<option value="">사원을 선택하세요</option>
				<c:forEach var="emp" items="${empList}">
					<option value="${emp.employeeId}" ${param.employeeId == emp.employeeId ? 'selected' : ''}>
						${emp.employeeName} (${emp.employeeNo})
					</option>
				</c:forEach>
			</select>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">조회</button>
		</div>
	</section>
</form>

<section class="card ">
	<div class="card-header">
		<h2 class="section-title">인사기록 카드</h2>
	</div>
	<div class="card-body" style="overflow-x: auto;">
		
		<!-- 인사기록카드 시작 (양면 레이아웃) -->
		<div class="document-sheet" style="display: flex; gap: 30px; width: 100%; min-width: 1400px; font-family: 'Malgun Gothic', '맑은 고딕', sans-serif; font-size: 12px; color: #333;">
			
			<!-- ==================== [왼쪽 페이지] ==================== -->
			<div style="flex: 1; border: 2px solid #333; padding: 2px;">
				<table border="1" bordercolor="#333" style="width: 100%; border-collapse: collapse; text-align: center; table-layout: fixed;">
					<colgroup>
						<col style="width: 8%;">
						<col style="width: 92%;">
					</colgroup>
					
					<!-- 1. 기본 인적사항 -->
					<tr>
						<th style="background-color: #f5f5f5;">사진</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333" style="width: 100%; height: 100%; border-collapse: collapse; margin: -1px;">
								<colgroup>
									<col style="width: 15%;"><col style="width: 25%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 15%;">
								</colgroup>
								<tr style="height: 28px;">
									<th colspan="4" rowspan="3" style="font-size: 20px; font-weight: bold; letter-spacing: 2px;">인사기록카드</th>
									<th style="background-color: #ffffe0;">사원번호</th>
									<td>${card.employeeNo}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">입사일</th>
									<td>${card.hireDate}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">퇴사일</th>
									<td>${card.retireDate}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">성명(한글)</th>
									<td>${card.employeeName}&nbsp;</td>
									<th style="background-color: #ffffe0;">성명(영문)</th>
									<td colspan="3">${card.employeeNameEn}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">주민등록번호</th>
									<!-- 보안을 위해 실제 번호 대신 마스킹 처리된 형식으로 출력 -->
									<td>${not empty card.residentRegNo ? '******-*******' : ''}&nbsp;</td>
									<th style="background-color: #ffffe0;">사원구분</th>
									<td colspan="3">${card.employmentType}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">주소</th>
									<td colspan="5">&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">휴대전화</th>
									<td colspan="2">${card.mobile}&nbsp;</td>
									<th style="background-color: #ffffe0;">연락처</th>
									<td colspan="2">${card.phone}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">E-Mail</th>
									<td colspan="5">${card.email}&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
					
					<!-- 2. 가족 및 보험사항 (병합구조가 복잡하여 인덱스 직접 매핑) -->
					<tr>
						<th style="background-color: #f5f5f5;">가<br>족<br>사<br>항</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333" style="width: 100%; height: 100%; border-collapse: collapse; margin: -1px; text-align: center; table-layout: fixed;">
								<colgroup>
									<col style="width: 8%;"><col style="width: 12%;"><col style="width: 18%;"><col style="width: 12%;">
									<col style="width: 10%;"><col style="width: 12%;"><col style="width: 10%;"><col style="width: 10%;"><col style="width: 8%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 26px;">
									<th>관계</th><th>성명</th><th>주민등록번호</th><th>동거여부</th>
									<th rowspan="2">국민<br>연금</th><th>기호번호</th><td colspan="3" style="background-color: #fff;">${card.insuranceList[0].insuranceNo}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>${card.dependentList[0].relationCode}&nbsp;</td>
									<td>${card.dependentList[0].dependentName}&nbsp;</td>
									<td>${card.dependentList[0].birthDate}&nbsp;</td>
									<td>${card.dependentList[0].cohabitationYn}&nbsp;</td>
									<th style="background-color: #ffffe0;">취득일</th><td>${card.insuranceList[0].acquisitionDate}&nbsp;</td>
									<th style="background-color: #ffffe0;">상실일</th><td>${card.insuranceList[0].lossDate}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>${card.dependentList[1].relationCode}&nbsp;</td>
									<td>${card.dependentList[1].dependentName}&nbsp;</td>
									<td>${card.dependentList[1].birthDate}&nbsp;</td>
									<td>${card.dependentList[1].cohabitationYn}&nbsp;</td>
									<th rowspan="2" style="background-color: #ffffe0;">건강<br>보험</th>
									<th style="background-color: #ffffe0;">기호번호</th><td colspan="3">${card.insuranceList[1].insuranceNo}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>&nbsp;</td><td>&nbsp;</td><td>-</td><td>&nbsp;</td>
									<th style="background-color: #ffffe0;">취득일</th><td>${card.insuranceList[1].acquisitionDate}&nbsp;</td>
									<th style="background-color: #ffffe0;">상실일</th><td>${card.insuranceList[1].lossDate}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>${card.dependentList[2].relationCode}&nbsp;</td>
									<td>${card.dependentList[2].dependentName}&nbsp;</td>
									<td>${card.dependentList[2].birthDate}&nbsp;</td>
									<td>${card.dependentList[2].cohabitationYn}&nbsp;</td>
									<th rowspan="2" style="background-color: #ffffe0;">고용<br>보험</th>
									<th style="background-color: #ffffe0;">기호번호</th><td colspan="3">${card.insuranceList[2].insuranceNo}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>&nbsp;</td><td>&nbsp;</td><td>-</td><td>&nbsp;</td>
									<th style="background-color: #ffffe0;">취득일</th><td>${card.insuranceList[2].acquisitionDate}&nbsp;</td>
									<th style="background-color: #ffffe0;">상실일</th><td>${card.insuranceList[2].lossDate}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>${card.dependentList[3].relationCode}&nbsp;</td>
									<td>${card.dependentList[3].dependentName}&nbsp;</td>
									<td>${card.dependentList[3].birthDate}&nbsp;</td>
									<td>${card.dependentList[3].cohabitationYn}&nbsp;</td>
									<th rowspan="2" style="background-color: #ffffe0;">산재<br>보험</th>
									<th style="background-color: #ffffe0;">기호번호</th><td colspan="3">${card.insuranceList[3].insuranceNo}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>&nbsp;</td><td>&nbsp;</td><td>-</td><td>&nbsp;</td>
									<th style="background-color: #ffffe0;">취득일</th><td>${card.insuranceList[3].acquisitionDate}&nbsp;</td>
									<th style="background-color: #ffffe0;">상실일</th><td>${card.insuranceList[3].lossDate}&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>

					<!-- 3. 학력 -->
					<tr>
						<th style="background-color: #f5f5f5;">학<br>력</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333" style="width: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 25%;"><col style="width: 20%;"><col style="width: 20%;"><col style="width: 25%;"><col style="width: 10%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>학교명</th><th>입학년월</th><th>졸업년월</th><th>전공</th><th>이수</th>
								</tr>
								<c:forEach begin="0" end="3" var="i">
									<tr style="height: 28px;">
										<td>${card.educationList[i].schoolName}&nbsp;</td>
										<td>${card.educationList[i].startDate}&nbsp;</td>
										<td>${card.educationList[i].endDate}&nbsp;</td>
										<td>${card.educationList[i].majorName}&nbsp;</td>
										<td>${card.educationList[i].graduationStatus}&nbsp;</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>

					<!-- 4. 병역 -->
					<tr>
						<th style="background-color: #f5f5f5;">병<br>역</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333" style="width: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 12%;"><col style="width: 13%;"><col style="width: 12%;"><col style="width: 13%;"><col style="width: 12%;"><col style="width: 13%;"><col style="width: 12%;"><col style="width: 13%;">
								</colgroup>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">제대구분</th><td colspan="3">${card.militaryInfo.militaryStatusCode}&nbsp;</td>
									<th style="background-color: #ffffe0;">미필사유</th><td colspan="3">${card.militaryInfo.militaryExemptReason}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">군별</th><td>${card.militaryInfo.militaryBranchCode}&nbsp;</td>
									<th style="background-color: #ffffe0;">최종계급</th><td>${card.militaryInfo.militaryGrade}&nbsp;</td>
									<th style="background-color: #ffffe0;">병과</th><td>${card.militaryInfo.militarySpecialty}&nbsp;</td>
									<th style="background-color: #ffffe0;">복무기간</th>
									<td>
										<c:if test="${not empty card.militaryInfo.serviceStartDate}">
											${card.militaryInfo.serviceStartDate} ~ ${card.militaryInfo.serviceEndDate}
										</c:if>
										&nbsp;
									</td>
								</tr>
							</table>
						</td>
					</tr>

					<!-- 5. 경력 -->
					<tr>
						<th style="background-color: #f5f5f5;">경<br>력</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333" style="width: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 25%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 30%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>회사명</th><th>입사일자</th><th>퇴사일자</th><th>최종직위</th><th>담당업무</th>
								</tr>
								<c:forEach begin="0" end="4" var="i">
									<tr style="height: 28px;">
										<td>${card.careerList[i].companyName}&nbsp;</td>
										<td>${card.careerList[i].startDate}&nbsp;</td>
										<td>${card.careerList[i].endDate}&nbsp;</td>
										<td>${card.careerList[i].position}&nbsp;</td>
										<td>${card.careerList[i].careerDescription}&nbsp;</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>

					<!-- 6. 자격/면허 -->
					<tr>
						<th style="background-color: #f5f5f5;">자<br>격<br>/<br>면<br>허</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333" style="width: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 30%;"><col style="width: 20%;"><col style="width: 30%;"><col style="width: 20%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>종류</th><th>취득일</th><th>발행기관</th><th>비고</th>
								</tr>
								<c:forEach begin="0" end="3" var="i">
									<tr style="height: 28px;">
										<td>${card.qualificationList[i].qualificationName}&nbsp;</td>
										<td>${card.qualificationList[i].acquisitionDate}&nbsp;</td>
										<td>${card.qualificationList[i].issuingOrganization}&nbsp;</td>
										<td>${card.qualificationList[i].memo}&nbsp;</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>

					<!-- 7. 어학능력 -->
					<tr>
						<th style="background-color: #f5f5f5;">어<br>학<br>능<br>력</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333" style="width: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 20%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 20%;"><col style="width: 10%;"><col style="width: 10%;"><col style="width: 10%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>외국어명</th><th>시험</th><th>공인점수</th><th>취득일</th><th>독해</th><th>작문</th><th>회화</th>
								</tr>
								<c:forEach begin="0" end="2" var="i">
									<tr style="height: 28px;">
										<td>${card.languageList[i].languageName}&nbsp;</td>
										<td>${card.languageList[i].testName}&nbsp;</td>
										<td>${card.languageList[i].officialScore}&nbsp;</td>
										<td>${card.languageList[i].acquisitionDate}&nbsp;</td>
										<td>${card.languageList[i].readingLevelCode}&nbsp;</td>
										<td>${card.languageList[i].writingLevelCode}&nbsp;</td>
										<td>${card.languageList[i].speakingLevelCode}&nbsp;</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>
				</table>
			</div>

			<!-- ==================== [오른쪽 페이지] ==================== -->
			<div style="flex: 1; border: 2px solid #333; padding: 2px; display: flex; flex-direction: column;">
				
				<div style="text-align: center; font-size: 20px; font-weight: bold; letter-spacing: 2px; padding: 30px 0;">인사기록카드</div>
				
				<table border="1" bordercolor="#333" style="width: 100%; border-collapse: collapse; text-align: center; table-layout: fixed; flex-grow: 1;">
					<colgroup>
						<col style="width: 8%;">
						<col style="width: 92%;">
					</colgroup>
					
					<!-- 8. 교육사항 -->
					<tr>
						<th style="background-color: #f5f5f5;">교<br>육<br>사<br>항</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333" style="width: 100%; height: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 15%;"><col style="width: 20%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 10%;"><col style="width: 10%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>교육구분</th><th>교육명</th><th>기간(부터)</th><th>기간(까지)</th><th>교육기관</th><th>교육비</th><th>환급교육비</th>
								</tr>
								<c:forEach begin="0" end="8" var="i">
									<tr style="height: 28px;">
										<td>${card.trainingList[i].trainingTypeCode}&nbsp;</td>
										<td>${card.trainingList[i].trainingName}&nbsp;</td>
										<td>${card.trainingList[i].startDate}&nbsp;</td>
										<td>${card.trainingList[i].endDate}&nbsp;</td>
										<td>${card.trainingList[i].trainingInstitution}&nbsp;</td>
										<td>
											<c:if test="${not empty card.trainingList[i].trainingCost}">
												<fmt:formatNumber value="${card.trainingList[i].trainingCost}" pattern="#,###"/>
											</c:if>
											&nbsp;
										</td>
										<td>
											<c:if test="${not empty card.trainingList[i].refundTrainingCost}">
												<fmt:formatNumber value="${card.trainingList[i].refundTrainingCost}" pattern="#,###"/>
											</c:if>
											&nbsp;
										</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>

					<!-- 9. 상벌사항 -->
					<tr>
						<th style="background-color: #f5f5f5;">상<br>벌<br>사<br>항</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333" style="width: 100%; height: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 15%;"><col style="width: 20%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 25%;"><col style="width: 10%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>구분</th><th>상벌명</th><th>상벌권자</th><th>상벌일자</th><th>상벌내용</th><th>비고</th>
								</tr>
								<c:forEach begin="0" end="5" var="i">
									<tr style="height: 28px;">
										<td>${card.rewardPunishmentList[i].typeCode}&nbsp;</td>
										<td>${card.rewardPunishmentList[i].name}&nbsp;</td>
										<td>${card.rewardPunishmentList[i].authorityName}&nbsp;</td>
										<td>${card.rewardPunishmentList[i].date}&nbsp;</td>
										<td>${card.rewardPunishmentList[i].content}&nbsp;</td>
										<td>${card.rewardPunishmentList[i].memo}&nbsp;</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>

					<!-- 10. 인사발령 -->
					<tr>
						<th style="background-color: #f5f5f5;">인<br>사<br>발<br>령</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333" style="width: 100%; height: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 15%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 15%;"><col style="width: 30%;"><col style="width: 10%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>발령구분</th><th>발령일자</th><th>부서</th><th>직위</th><th>직책 및 담당업무</th><th>비고</th>
								</tr>
								<c:forEach begin="0" end="5" var="i">
									<tr style="height: 28px;">
										<td>${card.appointmentList[i].typeCode}&nbsp;</td>
										<td>${card.appointmentList[i].date}&nbsp;</td>
										<td>${card.appointmentList[i].department}&nbsp;</td>
										<td>${card.appointmentList[i].position}&nbsp;</td>
										<td>${card.appointmentList[i].dutyTitle}&nbsp;</td>
										<td>${card.appointmentList[i].memo}&nbsp;</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>

					<!-- 11. 퇴직사항 -->
					<tr>
						<th style="background-color: #f5f5f5;">퇴<br>직<br>사<br>항</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333" style="width: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 20%;"><col style="width: 20%;"><col style="width: 20%;"><col style="width: 20%;"><col style="width: 20%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>퇴직구분</th><th>퇴직일자</th><th>퇴직사유</th><th>퇴직금</th><th>퇴직 후 연락처</th>
								</tr>
								<tr style="height: 28px;">
									<td>${card.retireType}&nbsp;</td>
									<td>${card.retireDate}&nbsp;</td>
									<td>${card.retireReason}&nbsp;</td>
									<td>&nbsp;</td> <!-- 퇴직금 DTO 컬럼 없음 -->
									<td>${card.retirePhone}&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>

				<!-- 하단 서명란 -->
				<div style="margin-top: 30px; margin-bottom: 20px; padding: 0 40px; display: flex; justify-content: space-between; align-items: center;">
					<div>
						<label style="cursor: pointer;"><input type="checkbox" checked> 대표자 표기</label>
					</div>
					<div style="text-align: center; font-size: 16px; font-weight: bold; line-height: 1.5;">
						(주)예스폼<br>대표이사 이응열
					</div>
					<div style="border: 1px solid #aaa; color: #888; padding: 15px 10px; font-size: 11px; text-align: center; width: 80px; height: 30px; display: flex; align-items: center; justify-content: center;">
						회사 도장을<br>넣어주세요
					</div>
				</div>

			</div>
		</div>
		<!-- 인사기록카드 끝 -->

	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>