package config.dnLItemSet.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

/**
 * 연차휴가 자동계산 (근로기준법 기준)
 *
 * 주 40시간제: 기본 15일. 3년 이상 근속 시 최초 1년 초과 2년마다 1일 가산 (최대 25일)
 * 주 44시간제: 기본 10일. 3년 이상 근속 시 매년 1일씩 가산 (가산 상한 없음, 화면 요청서 그대로)
 *
 * 계산 기준일(refDate)은 그 휴가항목의 "적용기간 시작일"을 사용한다.
 * (요청서 예시의 "OOOO년 1월 1일 연차계산시"와 같은 개념)
 */
public class AnnualLeaveCalculator {

    private AnnualLeaveCalculator() {}

    public static final String WORK_40H = "40";
    public static final String WORK_44H = "44";

    public static BigDecimal calculate(Date hireDate, Date refDate, String workTimeType) {

        if (hireDate == null || refDate == null) {
            return BigDecimal.ZERO;
        }

        LocalDate hire = hireDate.toLocalDate();
        LocalDate ref = refDate.toLocalDate();

        if (!ref.isAfter(hire)) {
            return BigDecimal.ZERO; // 입사일이 기준일보다 미래면 계산 불가
        }

        int baseDays = WORK_44H.equals(workTimeType) ? 10 : 15;
        int years = Period.between(hire, ref).getYears();

        if (years < 1) {
            // 입사 1년 미만: 기준일 * (근무일수/365), 소수 둘째자리에서 반올림(첫째자리까지 표시)
            long workDays = ChronoUnit.DAYS.between(hire, ref);
            double raw = baseDays * (workDays / 365.0);
            return BigDecimal.valueOf(raw).setScale(1, RoundingMode.HALF_UP);
        }

        if (years < 3) {
            // 입사 1년 이상 3년 미만: 기준일 그대로
            return BigDecimal.valueOf(baseDays);
        }

        // 입사 3년 이상
        int workYearForFormula = years - 1; // 요청서 예시와 맞춘 값 (최초 1년 초과분)

        if (WORK_44H.equals(workTimeType)) {
            return BigDecimal.valueOf(baseDays + workYearForFormula);
        }

        int addDays = workYearForFormula / 2; // 소수점 버림(정수 나눗셈)
        int total = Math.min(baseDays + addDays, 25); // 40시간제는 최대 25일
        return BigDecimal.valueOf(total);
    }
}
