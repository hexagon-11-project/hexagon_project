package payment.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 항목별 대장 모델.
 * 지급/공제 통합 셀렉트 항목, 사원별 조회 결과, 월(차수) 내역을 한 파일에서 담는다.
 */
public class PaymentItemLedger {

	public static final String TYPE_PAY = "PAY";
	public static final String TYPE_DEDUCTION = "DEDUCTION";

	/** 항목 구분 — PAY(지급) 또는 DEDUCTION(공제) */
	private String itemType;

	/** 지급항목아이디 또는 공제항목아이디 */
	private Long itemId;

	/** 항목명 (기본급, 국민연금 등) */
	private String itemName;

	/** 사원아이디 */
	private int employeeId;

	/** 구분 (고용형태) */
	private String employmentType;

	/** 성명 */
	private String employeeName;

	/** 부서 */
	private String department;

	/** 직위 */
	private String position;

	/** 귀속연월 YYYYMM */
	private String payYearMonth;

	/** 연도 */
	private int year;

	/** 월 (1~12) */
	private int month;

	/** 급여차수. 같은 월에 차수가 여러 건이면 건별로 담는다 */
	private Integer paySequence;

	/** 해당 항목 금액 */
	private long amount;

	/** 기간 안 월(차수)별 항목 내역 */
	private List<PaymentItemLedger> details = new ArrayList<>();

	/** 사원별 기간 내 총 합계 */
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

	/** 셀렉트 박스 value. 예: PAY:1001, DEDUCTION:2001 */
	public String getSelectValue() {
		return itemType + ":" + itemId;
	}

	public boolean isPayItem() {
		return TYPE_PAY.equals(itemType);
	}

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

	/** 해당 연월 금액. yearMonthLabel 형식은 YYYY.MM (예: 2026.01). */
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

	/** 해당 연월 금액. 같은 달에 차수가 여러 건이면 합산한다. */
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
