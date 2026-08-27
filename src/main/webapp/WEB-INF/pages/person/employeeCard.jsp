<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
request.setAttribute("pageTitle", "人事記録カード");
request.setAttribute("pageSection", "人事管理");
request.setAttribute("pageDescription", "選択した社員の人的事項、学歴、経歴などの人事記録をカード形式で確認します。");
request.setAttribute("activeKey", "personnel-card");
request.setAttribute("pageCss", "employee.css");
request.setAttribute("pageJs", null);
%>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
<%@ include file="/WEB-INF/jspf/app-start.jspf"%>

<form action="" method="GET" id="searchForm">
	<section class="filter-bar">
		<div class="field ">
			<label>社員</label>
			<!-- Controller에서 넘어온 empList(사원목록)를 반복문으로 동적 세팅 -->
			<!-- Controllerから渡されたempList(社員リスト)をループで動的セッティング -->
			<select class="select" name="employeeId">
				<option value="">社員を選択してください</option>
				<c:forEach var="emp" items="${empList}">
					<option value="${emp.employeeId}"
						${param.employeeId == emp.employeeId ? 'selected' : ''}>
						${emp.employeeName} (${emp.employeeNo})</option>
				</c:forEach>
			</select>
		</div>
		<div class="actions">
			<button type="submit" class="btn btn-primary">照会</button>
		</div>
	</section>
</form>

<section class="card ">
	<div class="card-header">
		<h2 class="section-title">人事記録カード</h2>
	</div>
	<div class="card-body" style="overflow-x: auto;">

		<!-- 인사기록카드 시작 (양면 레이아웃) -->
		<!-- 人事記録カード開始 (両面レイアウト) -->
		<div class="document-sheet"
			style="display: flex; gap: 30px; width: 100%; min-width: 1400px; font-family: 'Malgun Gothic', '맑은 고딕', sans-serif; font-size: 12px; color: #333;">

			<!-- ==================== [왼쪽 페이지] ==================== -->
			<!-- ==================== [左ページ] ==================== -->
			<div style="flex: 1; border: 2px solid #333; padding: 2px;">
				<table border="1" bordercolor="#333"
					style="width: 100%; border-collapse: collapse; text-align: center; table-layout: fixed;">
					<colgroup>
						<col style="width: 8%;">
						<col style="width: 92%;">
					</colgroup>

					<!-- 1. 기본 인적사항 -->
					<!-- 1. 基本人的事項 -->
					<tr>
						<th style="background-color: #f5f5f5;">写真</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333"
								style="width: 100%; height: 100%; border-collapse: collapse; margin: -1px;">
								<colgroup>
									<col style="width: 15%;">
									<col style="width: 25%;">
									<col style="width: 15%;">
									<col style="width: 15%;">
									<col style="width: 15%;">
									<col style="width: 15%;">
								</colgroup>
								<tr style="height: 28px;">
									<th colspan="4" rowspan="3"
										style="font-size: 20px; font-weight: bold; letter-spacing: 2px;">人事記録カード</th>
									<th style="background-color: #ffffe0;">社員番号</th>
									<td>${card.employeeNo}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">入社日</th>
									<td>${card.hireDate}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">退社日</th>
									<td>${card.retireDate}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">氏名(韓国語)</th>
									<td>${card.employeeName}&nbsp;</td>
									<th style="background-color: #ffffe0;">氏名(英語)</th>
									<td colspan="3">${card.employeeNameEn}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">住民登録番号</th>
									<!-- 보안을 위해 실제 번호 대신 마스킹 처리된 형식으로 출력 -->
									<!-- セキュリティのため、実際の番号の代わりにマスキング処理された形式で出力 -->
									<td>${not empty card.residentRegNo ? '******-*******' : ''}&nbsp;</td>
									<th style="background-color: #ffffe0;">社員区分</th>
									<td colspan="3">${card.employmentType}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">住所</th>
									<td colspan="5">&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">携帯電話</th>
									<td colspan="2">${card.mobile}&nbsp;</td>
									<th style="background-color: #ffffe0;">連絡先</th>
									<td colspan="2">${card.phone}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">E-Mail</th>
									<td colspan="5">${card.email}&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>

					<!-- 2. 가족 및 보험사항  -->
					<!-- 2. 家族および保険事項 -->
					<tr>
						<th style="background-color: #f5f5f5;">家<br>族<br>事<br>項
						</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333"
								style="width: 100%; height: 100%; border-collapse: collapse; margin: -1px; text-align: center; table-layout: fixed;">
								<colgroup>
									<col style="width: 8%;">
									<col style="width: 12%;">
									<col style="width: 18%;">
									<col style="width: 12%;">
									<col style="width: 10%;">
									<col style="width: 12%;">
									<col style="width: 10%;">
									<col style="width: 10%;">
									<col style="width: 8%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 26px;">
									<th>続柄</th>
									<th>氏名</th>
									<th>住民登録番号</th>
									<th>同居の有無</th>
									<th rowspan="2">国民<br>年金
									</th>
									<th>記号番号</th>
									<td colspan="3" style="background-color: #fff;">${card.insuranceList[0].insuranceNo}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>${card.dependentList[0].relationCode}&nbsp;</td>
									<td>${card.dependentList[0].dependentName}&nbsp;</td>
									<td>${card.dependentList[0].birthDate}&nbsp;</td>
									<td>${card.dependentList[0].cohabitationYn}&nbsp;</td>
									<th style="background-color: #ffffe0;">取得日</th>
									<td>${card.insuranceList[0].acquisitionDate}&nbsp;</td>
									<th style="background-color: #ffffe0;">喪失日</th>
									<td>${card.insuranceList[0].lossDate}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>${card.dependentList[1].relationCode}&nbsp;</td>
									<td>${card.dependentList[1].dependentName}&nbsp;</td>
									<td>${card.dependentList[1].birthDate}&nbsp;</td>
									<td>${card.dependentList[1].cohabitationYn}&nbsp;</td>
									<th rowspan="2" style="background-color: #ffffe0;">健康<br>保険
									</th>
									<th style="background-color: #ffffe0;">記号番号</th>
									<td colspan="3">${card.insuranceList[1].insuranceNo}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>-</td>
									<td>&nbsp;</td>
									<th style="background-color: #ffffe0;">取得日</th>
									<td>${card.insuranceList[1].acquisitionDate}&nbsp;</td>
									<th style="background-color: #ffffe0;">喪失日</th>
									<td>${card.insuranceList[1].lossDate}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>${card.dependentList[2].relationCode}&nbsp;</td>
									<td>${card.dependentList[2].dependentName}&nbsp;</td>
									<td>${card.dependentList[2].birthDate}&nbsp;</td>
									<td>${card.dependentList[2].cohabitationYn}&nbsp;</td>
									<th rowspan="2" style="background-color: #ffffe0;">雇用<br>保険
									</th>
									<th style="background-color: #ffffe0;">記号番号</th>
									<td colspan="3">${card.insuranceList[2].insuranceNo}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>-</td>
									<td>&nbsp;</td>
									<th style="background-color: #ffffe0;">取得日</th>
									<td>${card.insuranceList[2].acquisitionDate}&nbsp;</td>
									<th style="background-color: #ffffe0;">喪失日</th>
									<td>${card.insuranceList[2].lossDate}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>${card.dependentList[3].relationCode}&nbsp;</td>
									<td>${card.dependentList[3].dependentName}&nbsp;</td>
									<td>${card.dependentList[3].birthDate}&nbsp;</td>
									<td>${card.dependentList[3].cohabitationYn}&nbsp;</td>
									<th rowspan="2" style="background-color: #ffffe0;">労災<br>保険
									</th>
									<th style="background-color: #ffffe0;">記号番号</th>
									<td colspan="3">${card.insuranceList[3].insuranceNo}&nbsp;</td>
								</tr>
								<tr style="height: 26px;">
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>-</td>
									<td>&nbsp;</td>
									<th style="background-color: #ffffe0;">取得日</th>
									<td>${card.insuranceList[3].acquisitionDate}&nbsp;</td>
									<th style="background-color: #ffffe0;">喪失日</th>
									<td>${card.insuranceList[3].lossDate}&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>

					<!-- 3. 학력 -->
					<!-- 3. 学歴 -->
					<tr>
						<th style="background-color: #f5f5f5;">学<br>歴
						</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333"
								style="width: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 25%;">
									<col style="width: 20%;">
									<col style="width: 20%;">
									<col style="width: 25%;">
									<col style="width: 10%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>学校名</th>
									<th>入学年月</th>
									<th>卒業年月</th>
									<th>専攻</th>
									<th>履修</th>
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
					<!-- 4. 兵役 -->
					<tr>
						<th style="background-color: #f5f5f5;">兵<br>役
						</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333"
								style="width: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 12%;">
									<col style="width: 13%;">
									<col style="width: 12%;">
									<col style="width: 13%;">
									<col style="width: 12%;">
									<col style="width: 13%;">
									<col style="width: 12%;">
									<col style="width: 13%;">
								</colgroup>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">除隊区分</th>
									<td colspan="3">${card.militaryInfo.militaryStatusCode}&nbsp;</td>
									<th style="background-color: #ffffe0;">未了理由</th>
									<td colspan="3">${card.militaryInfo.militaryExemptReason}&nbsp;</td>
								</tr>
								<tr style="height: 28px;">
									<th style="background-color: #ffffe0;">軍別</th>
									<td>${card.militaryInfo.militaryBranchCode}&nbsp;</td>
									<th style="background-color: #ffffe0;">最終階級</th>
									<td>${card.militaryInfo.militaryGrade}&nbsp;</td>
									<th style="background-color: #ffffe0;">兵科</th>
									<td>${card.militaryInfo.militarySpecialty}&nbsp;</td>
									<th style="background-color: #ffffe0;">服務期間</th>
									<td><c:if
											test="${not empty card.militaryInfo.serviceStartDate}">
											${card.militaryInfo.serviceStartDate} ~ ${card.militaryInfo.serviceEndDate}
										</c:if> &nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>

					<!-- 5. 경력 -->
					<!-- 5. 経歴 -->
					<tr>
						<th style="background-color: #f5f5f5;">経<br>歴
						</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333"
								style="width: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 25%;">
									<col style="width: 15%;">
									<col style="width: 15%;">
									<col style="width: 15%;">
									<col style="width: 30%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>会社名</th>
									<th>入社日付</th>
									<th>退社日付</th>
									<th>最終役職</th>
									<th>担当業務</th>
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
					<!-- 6. 資格/免許 -->
					<tr>
						<th style="background-color: #f5f5f5;">資<br>格<br>/<br>免<br>許
						</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333"
								style="width: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 30%;">
									<col style="width: 20%;">
									<col style="width: 30%;">
									<col style="width: 20%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>種類</th>
									<th>取得日</th>
									<th>発行機関</th>
									<th>備考</th>
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
					<!-- 7. 語学能力 -->
					<tr>
						<th style="background-color: #f5f5f5;">語<br>学<br>能<br>力
						</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333"
								style="width: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 20%;">
									<col style="width: 15%;">
									<col style="width: 15%;">
									<col style="width: 20%;">
									<col style="width: 10%;">
									<col style="width: 10%;">
									<col style="width: 10%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>外国語名</th>
									<th>試験</th>
									<th>公認スコア</th>
									<th>取得日</th>
									<th>読解</th>
									<th>作文</th>
									<th>会話</th>
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
			<!-- ==================== [右ページ] ==================== -->
			<div
				style="flex: 1; border: 2px solid #333; padding: 2px; display: flex; flex-direction: column;">

				<div
					style="text-align: center; font-size: 20px; font-weight: bold; letter-spacing: 2px; padding: 30px 0;">人事記録カード</div>

				<table border="1" bordercolor="#333"
					style="width: 100%; border-collapse: collapse; text-align: center; table-layout: fixed; flex-grow: 1;">
					<colgroup>
						<col style="width: 8%;">
						<col style="width: 92%;">
					</colgroup>

					<!-- 8. 교육사항 -->
					<!-- 8. 教育事項 -->
					<tr>
						<th style="background-color: #f5f5f5;">教<br>育<br>事<br>項
						</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333"
								style="width: 100%; height: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 15%;">
									<col style="width: 20%;">
									<col style="width: 15%;">
									<col style="width: 15%;">
									<col style="width: 15%;">
									<col style="width: 10%;">
									<col style="width: 10%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>教育区分</th>
									<th>教育名</th>
									<th>期間(から)</th>
									<th>期間(まで)</th>
									<th>教育機関</th>
									<th>教育費</th>
									<th>還付教育費</th>
								</tr>
								<c:forEach begin="0" end="8" var="i">
									<tr style="height: 28px;">
										<td>${card.trainingList[i].trainingTypeCode}&nbsp;</td>
										<td>${card.trainingList[i].trainingName}&nbsp;</td>
										<td>${card.trainingList[i].startDate}&nbsp;</td>
										<td>${card.trainingList[i].endDate}&nbsp;</td>
										<td>${card.trainingList[i].trainingInstitution}&nbsp;</td>
										<td><c:if
												test="${not empty card.trainingList[i].trainingCost}">
												<fmt:formatNumber
													value="${card.trainingList[i].trainingCost}"
													pattern="#,###" />
											</c:if> &nbsp;</td>
										<td><c:if
												test="${not empty card.trainingList[i].refundTrainingCost}">
												<fmt:formatNumber
													value="${card.trainingList[i].refundTrainingCost}"
													pattern="#,###" />
											</c:if> &nbsp;</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>

					<!-- 9. 상벌사항 -->
					<!-- 9. 賞罰事項 -->
					<tr>
						<th style="background-color: #f5f5f5;">賞<br>罰<br>事<br>項
						</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333"
								style="width: 100%; height: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 15%;">
									<col style="width: 20%;">
									<col style="width: 15%;">
									<col style="width: 15%;">
									<col style="width: 25%;">
									<col style="width: 10%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>区分</th>
									<th>賞罰名</th>
									<th>賞罰権者</th>
									<th>賞罰日付</th>
									<th>賞罰内容</th>
									<th>備考</th>
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
					<!-- 10. 人事発令 -->
					<tr>
						<th style="background-color: #f5f5f5;">人<br>事<br>発<br>令
						</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333"
								style="width: 100%; height: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 15%;">
									<col style="width: 15%;">
									<col style="width: 15%;">
									<col style="width: 15%;">
									<col style="width: 30%;">
									<col style="width: 10%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>発令区分</th>
									<th>発令日付</th>
									<th>部署</th>
									<th>役職</th>
									<th>役職および担当業務</th>
									<th>備考</th>
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
					<!-- 11. 退職事項 -->
					<tr>
						<th style="background-color: #f5f5f5;">退<br>職<br>事<br>項
						</th>
						<td style="padding: 0; border: none;">
							<table border="1" bordercolor="#333"
								style="width: 100%; border-collapse: collapse; margin: -1px; table-layout: fixed;">
								<colgroup>
									<col style="width: 20%;">
									<col style="width: 20%;">
									<col style="width: 20%;">
									<col style="width: 20%;">
									<col style="width: 20%;">
								</colgroup>
								<tr style="background-color: #ffffe0; height: 28px;">
									<th>退職区分</th>
									<th>退職日付</th>
									<th>退職理由</th>
									<th>退職金</th>
									<th>退職後の連絡先</th>
								</tr>
								<tr style="height: 28px;">
									<td>${card.retireType}&nbsp;</td>
									<td>${card.retireDate}&nbsp;</td>
									<td>${card.retireReason}&nbsp;</td>
									<td>&nbsp;</td>
									<td>${card.retirePhone}&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>

				<div
					style="margin-top: 30px; margin-bottom: 20px; padding: 0 40px; display: flex; justify-content: space-between; align-items: center;">
					<div
						style="text-align: center; font-size: 16px; font-weight: bold; line-height: 1.5;">
						(주)헥사곤테크<br>
					</div>
					<div
						style="width: 80px; display: flex; align-items: center; justify-content: center;">
						<img
							src="${pageContext.request.contextPath}/assets/images/Seal.png"
							alt="직인" style="width: 60px; height: 60px;">
					</div>
				</div>

			</div>
		</div>

	</div>
</section>
<%@ include file="/WEB-INF/jspf/app-end.jspf"%>