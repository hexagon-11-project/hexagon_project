package payment.model;

/**
 * 급여이체 신청 조회 리스트 한 행.
 * 給与振込申請照会リストの1行。
 *
 */
public class PaymentTransferRequest {

	// ===== 사원 이체 정보 (EMPLOYEE + PAYROLL_EMPLOYEE) =====
	// ===== 社員振込情報 (EMPLOYEE + PAYROLL_EMPLOYEE) =====
	// 은행이름
	// 銀行名。
	private String bankName;
	// 계좌번호
	// 口座番号。
	private String bankAccount;
	// 이름
	// 氏名。
	private String employeeName;
	// 이체금액 (실지급액)
	// 振込金額 (実支給額)。
	private long transferAmount;

	// ===== 회사 출금계좌 (COMPANY_INFO) =====
	// ===== 会社出金口座 (COMPANY_INFO) =====
	// 은행명
	// 銀行名。
	private String companyBankName;
	// 예금주
	// 口座名義人。
	private String companyAccountHolder;
	// 계좌번호
	// 口座番号。
	private String companyBankAccount;

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
