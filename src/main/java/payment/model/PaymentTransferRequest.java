package payment.model;

/**
 * 급여이체 신청 조회 리스트 한 행.
 *
 * - 사원(이체 대상): 은행이름, 계좌번호, 이름, 이체금액
 * - 회사(COMPANY_INFO 출금계좌): 은행명, 예금주, 계좌번호
 */
public class PaymentTransferRequest {

	// ===== 사원 이체 정보 (EMPLOYEE + PAYROLL_EMPLOYEE) =====
	private String bankName;       // 은행이름
	private String bankAccount;    // 계좌번호
	private String employeeName;   // 이름
	private long transferAmount;   // 이체금액 (실지급액)

	// ===== 회사 출금계좌 (COMPANY_INFO) =====
	private String companyBankName;        // 은행명
	private String companyAccountHolder;   // 예금주
	private String companyBankAccount;     // 계좌번호

	public PaymentTransferRequest() {
	}

	public PaymentTransferRequest(String bankName, String bankAccount, String employeeName, long transferAmount) {
		this.bankName = bankName;
		this.bankAccount = bankAccount;
		this.employeeName = employeeName;
		this.transferAmount = transferAmount;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public long getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(long transferAmount) {
		this.transferAmount = transferAmount;
	}

	public String getCompanyBankName() {
		return companyBankName;
	}

	public void setCompanyBankName(String companyBankName) {
		this.companyBankName = companyBankName;
	}

	public String getCompanyAccountHolder() {
		return companyAccountHolder;
	}

	public void setCompanyAccountHolder(String companyAccountHolder) {
		this.companyAccountHolder = companyAccountHolder;
	}

	public String getCompanyBankAccount() {
		return companyBankAccount;
	}

	public void setCompanyBankAccount(String companyBankAccount) {
		this.companyBankAccount = companyBankAccount;
	}
}
