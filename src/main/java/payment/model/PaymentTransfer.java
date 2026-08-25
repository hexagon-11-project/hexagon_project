package payment.model;

/**
 * 급여이체 신청 목록의 사원 1행을 담는다.
 * 給与振込申請一覧の社員1行を格納する。
 *
 */
public class PaymentTransfer {

	// 급여작업 PK
	// 給与作業PK。
	private int payrollId;
	// 급여사원 PK
	// 給与社員PK。
	private int payrollEmployeeId;
	private int employeeId;
	private String employeeName;
	private String department;
	private String position;
	// 입금 은행명
	// 入金銀行名。
	private String bankName;
	// 입금 계좌번호
	// 入金口座番号。
	private String bankAccount;
	// 실지급액
	// 実支給額。
	private long netPayAmount;

	public PaymentTransfer() {
	}

	public int getPayrollId() {
		return payrollId;
	}

	public void setPayrollId(int payrollId) {
		this.payrollId = payrollId;
	}

	public int getPayrollEmployeeId() {
		return payrollEmployeeId;
	}

	public void setPayrollEmployeeId(int payrollEmployeeId) {
		this.payrollEmployeeId = payrollEmployeeId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
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

	public long getNetPayAmount() {
		return netPayAmount;
	}

	public void setNetPayAmount(long netPayAmount) {
		this.netPayAmount = netPayAmount;
	}
}
