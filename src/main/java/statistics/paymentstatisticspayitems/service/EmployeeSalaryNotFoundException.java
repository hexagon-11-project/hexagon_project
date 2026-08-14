package statistics.paymentstatisticspayitems.service;

/**
 * 선택한 사원의 해당 연·월 급여 기록이 없을 때 발생한다.
 */
public class EmployeeSalaryNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final int year;
	private final int month;
	private final String employeeName;

	public EmployeeSalaryNotFoundException(int year, int month, String employeeName) {
		super(year + "년 " + month + "월에 '" + employeeName + "' 사원의 급여 기록이 없습니다.");
		this.year = year;
		this.month = month;
		this.employeeName = employeeName;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public String getEmployeeName() {
		return employeeName;
	}
}
