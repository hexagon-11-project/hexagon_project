package payment.paymentMnt.dto;

import java.util.Date;

// 급여 정보 DTO 클래스
// 給与情報DTOクラス
public class PaymentMntPayrollDTO {

	private int payrollId; // 급여아이디 (給与ID)
	private int companyId; // 회사아이디 (会社ID)
	private String payYearMonth; // 귀속연월 - YYYYMM (帰属年月)
	private int paySequence; // 급여차수 (給与次数)
	private Date settlementStartDate; // 정산시작일 (精算開始日)
	private Date settlementEndDate; // 정산종료일 (精算終了日)
	private Date paymentDate; // 급여지급일 (給与支給日)
	private String autoCalcYN; // 자동계산여부 (自動計算有無)

	// 사원별 급여 요약 필드 (사원 목록 조회용)
	// 社員別給与要約フィールド (社員リスト照会用)
	private String employeeName; // 성명 (氏名)
	private String departmentName; // 부서 (部署)
	private long totalPayAmount; // 지급총액 (支給総額)
	private long totalDeductionAmount; // 공제총액 (控除総額)
	private long netPayAmount; // 실지급액 (実支給額)

	// 기본 생성자
	// デフォルトコンストラクタ
	public PaymentMntPayrollDTO() {
	}

	// Getter 및 Setter 메서드
	// GetterおよびSetterメソッド
	public int getPayrollId() {
		return payrollId;
	}

	public void setPayrollId(int payrollId) {
		this.payrollId = payrollId;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getPayYearMonth() {
		return payYearMonth;
	}

	public void setPayYearMonth(String payYearMonth) {
		this.payYearMonth = payYearMonth;
	}

	public int getPaySequence() {
		return paySequence;
	}

	public void setPaySequence(int paySequence) {
		this.paySequence = paySequence;
	}

	public Date getSettlementStartDate() {
		return settlementStartDate;
	}

	public void setSettlementStartDate(Date settlementStartDate) {
		this.settlementStartDate = settlementStartDate;
	}

	public Date getSettlementEndDate() {
		return settlementEndDate;
	}

	public void setSettlementEndDate(Date settlementEndDate) {
		this.settlementEndDate = settlementEndDate;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getAutoCalcYN() {
		return autoCalcYN;
	}

	public void setAutoCalcYN(String autoCalcYN) {
		this.autoCalcYN = autoCalcYN;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public long getTotalPayAmount() {
		return totalPayAmount;
	}

	public void setTotalPayAmount(long totalPayAmount) {
		this.totalPayAmount = totalPayAmount;
	}

	public long getTotalDeductionAmount() {
		return totalDeductionAmount;
	}

	public void setTotalDeductionAmount(long totalDeductionAmount) {
		this.totalDeductionAmount = totalDeductionAmount;
	}

	public long getNetPayAmount() {
		return netPayAmount;
	}

	public void setNetPayAmount(long netPayAmount) {
		this.netPayAmount = netPayAmount;
	}
}