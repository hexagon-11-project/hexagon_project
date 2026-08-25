package statistics.paymentstatisticspayitems.service;

/**
 * 선택한 사원의 해당 연·월 급여 기록이 없을 때 발생한다.
 * 選択した社員の該当年・月給与記録がないときに発生する。
 *
 */
public class EmployeeSalaryNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	// 조회한 연도
	// 照会した年。
	private final int year;
	// 조회한 월
	// 照会した月。
	private final int month;
	// 조회한 사원이름
	// 照会した社員名。
	private final String employeeName;

	/**
	 * 연·월·사원이름으로 안내 메시지를 만든다.
	 * 年・月・社員名で案内メッセージを作る。
	 *
	 */
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
