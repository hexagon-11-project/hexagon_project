package payment.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 항목별 대장 한 건을 담는 모델.
 * 項目別台帳の1件を格納するモデル。
 */
public class PaymentItemLedger {

	// 지급 항목 구분값
	// 支給項目の区分値。
	public static final String TYPE_PAY = "PAY";
	// 공제 항목 구분값
	// 控除項目の区分値。
	public static final String TYPE_DEDUCTION = "DEDUCTION";

	// 항목 구분
	// 項目区分。
	private String itemType;

	// 지급항목아이디 또는 공제항목아이디
	// 支給項目IDまたは控除項目ID。
	private Long itemId;

	// 항목명
	// 項目名。
	private String itemName;

	// 사원아이디
	// 社員ID。
	private int employeeId;

	// 구분(고용형태)
	// 区分(雇用形態)。
	private String employmentType;

	// 성명
	// 氏名。
	private String employeeName;

	// 부서
	// 部署。
	private String department;

	// 직위
	// 職位。
	private String position;

	// 귀속연월
	// 帰属年月。
	private String payYearMonth;

	// 연도
	// 年度。
	private int year;

	// 월(1~12)
	// 月(1〜12)。
	private int month;

	// 급여차수
	// 給与次数。
	private Integer paySequence;

	// 해당 항목 금액
	// 当該項目の金額。
	private long amount;

	// 기간 안 월(차수)별 항목 내역
	// 期間内の月(次数)別項目明細。
	private List<PaymentItemLedger> details = new ArrayList<>();

	// 사원별 기간 내 총 합계
	// 社員別の期間内総計。
	private long totalAmount;

	public PaymentItemLedger() {
	}

	public PaymentItemLedger(String itemType, Long itemId, String itemName) {
		this.itemType = itemType;
		this.itemId = itemId;
		this.itemName = itemName;
	}

	public PaymentItemLedger(String payYearMonth, int year, int month, Integer paySequence, long amount) {
		this.payYearMonth = payYearMonth;
		this.year = year;
		this.month = month;
		this.paySequence = paySequence;
		this.amount = amount;
	}

	/**
	 * 셀렉트 박스 value를 만든다.
	 * セレクトボックスのvalueを作る。
	 */
	public String getSelectValue() {
		return itemType + ":" + itemId;
	}

	/**
	 * 지급 항목인지 판별한다.
	 * 支給項目かを判定する。
	 */
	public boolean isPayItem() {
		return TYPE_PAY.equals(itemType);
	}

	/**
	 * 공제 항목인지 판별한다.
	 * 控除項目かを判定する。
	 */
	public boolean isDeductionItem() {
		return TYPE_DEDUCTION.equals(itemType);
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
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

	public String getPayYearMonth() {
		return payYearMonth;
	}

	public void setPayYearMonth(String payYearMonth) {
		this.payYearMonth = payYearMonth;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public Integer getPaySequence() {
		return paySequence;
	}

	public void setPaySequence(Integer paySequence) {
		this.paySequence = paySequence;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public List<PaymentItemLedger> getDetails() {
		return details;
	}

	public void setDetails(List<PaymentItemLedger> details) {
		this.details = details != null ? details : new ArrayList<>();
	}

	public long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(long totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * 연월 라벨에 해당하는 금액을 구한다.
	 * 年月ラベルに該当する金額を求める。
	 */
	public long getAmountOf(String yearMonthLabel) {
		if (yearMonthLabel == null || yearMonthLabel.length() < 7) {
			return 0L;
		}
		try {
			int year = Integer.parseInt(yearMonthLabel.substring(0, 4));
			int month = Integer.parseInt(yearMonthLabel.substring(5, 7));
			return getAmountByYearMonth(year, month);
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	/**
	 * 해당 연월 금액을 구한다.
	 * 当該年月の金額を求める。
	 */
	public long getAmountByYearMonth(int year, int month) {
		long sum = 0L;
		if (details == null) {
			return sum;
		}
		for (PaymentItemLedger detail : details) {
			if (detail.getYear() == year && detail.getMonth() == month) {
				sum += detail.getAmount();
			}
		}
		return sum;
	}
}
