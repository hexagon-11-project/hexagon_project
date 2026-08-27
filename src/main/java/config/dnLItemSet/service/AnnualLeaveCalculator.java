package config.dnLItemSet.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

// 연차(휴가) 일수 계산하는 유틸
// 年次有給休暇の日数を計算するユーティリティ
public class AnnualLeaveCalculator {

	private AnnualLeaveCalculator() {
	}

	public static final String WORK_40H = "40";
	public static final String WORK_44H = "44";

	// 입사일, 기준일, 근무제(40시간/44시간) 받아서 연차 며칠 줄지 계산해서 던져줌
	// 入社日、基準日、勤務制（40時間/44時間）を受け取って、何日付与するか計算して返す
	public static BigDecimal calculate(Date hireDate, Date refDate, String workTimeType) {

		// 날짜 세팅 안 됐거나 기준일이 입사일보다 전이면 얄짤없이 0일
		// 日付が未設定、または基準日が入社日前なら0日
		if (hireDate == null || refDate == null) {
			return BigDecimal.ZERO;
		}

		LocalDate hire = hireDate.toLocalDate();
		LocalDate ref = refDate.toLocalDate();

		if (!ref.isAfter(hire)) {
			return BigDecimal.ZERO; // 입사일이 기준일보다 미래면 계산 불가
		}

		// 44시간제면 기본 10일 40시간제면 기본 15일 깔고 시작
		// 44時間制なら基本10日、40時間制なら基本15日スタート
		int baseDays = WORK_44H.equals(workTimeType) ? 10 : 15;
		int years = Period.between(hire, ref).getYears();

		if (years < 1) {
			// 입사 1년 안 됐을 때 근무일수 비율로 일할 계산하고 0.5일 단위로 반올림 쳐줌
			// 入社1年未満：勤務日数の割合で日割り計算し、0.5日単位で丸める
			long workDays = ChronoUnit.DAYS.between(hire, ref);
			double raw = baseDays * (workDays / 365.0);
			// 0.5 단위 반올림 raw * 2 → 반올림 → / 2
			double rounded = Math.round(raw * 2) / 2.0;
			return BigDecimal.valueOf(rounded).stripTrailingZeros();
		}

		if (years < 3) {
			// 1년 이상 3년 미만 가산 연차 없이 기본일수 그대로 줌
			// 入社1年以上3年未満：加算休暇なしで基本日数をそのまま付与
			return BigDecimal.valueOf(baseDays);
		}

		// 3년차 이상 이때부터 가산 연차 붙기 시작함
		// 入社3年以上：ここから加算休暇がつき始める
		int workYearForFormula = years - 1; // 요청서 예시와 맞춘 값 (최초 1년 초과분)

		if (WORK_44H.equals(workTimeType)) {
			return BigDecimal.valueOf(baseDays + workYearForFormula);
		}

		int addDays = workYearForFormula / 2; // 소수점 버림(정수 나눗셈)
		// 40시간제는 아무리 오래 다녀도 연차 최대 25일 맥스
		// 40時間制はどれだけ長く勤めても最大25日まで
		int total = Math.min(baseDays + addDays, 25); 
		return BigDecimal.valueOf(total);
	}
}