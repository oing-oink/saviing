package saviing.bank.account.domain.service;

import org.springframework.stereotype.Service;

import io.micrometer.common.lang.NonNull;
import saviing.bank.account.domain.model.CompoundingType;
import saviing.bank.account.domain.vo.BasisPoints;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class InterestAccrualServiceImpl implements InterestAccrualService {
    
    private static final int SCALE = 6;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final BigDecimal DAYS_IN_YEAR = BigDecimal.valueOf(365);
    private static final BigDecimal MONTHS_IN_YEAR = BigDecimal.valueOf(12);
    
    @Override
    public BigDecimal computeAccrual(
        long principalWon,
        @NonNull BigDecimal currentAccrued,
        @NonNull BasisPoints baseRate,
        @NonNull BasisPoints bonusRate,
        @NonNull CompoundingType compoundingType,
        @NonNull Instant lastAccrualTs,
        @NonNull Instant asOf
    ) { 
        if (principalWon <= 0) {
            return BigDecimal.ZERO;
        }
        
        if (!asOf.isAfter(lastAccrualTs)) {
            return BigDecimal.ZERO;
        }
        
        BasisPoints totalRate = baseRate.add(bonusRate);
        BigDecimal annualRate = totalRate.toDecimal();
        BigDecimal principal = BigDecimal.valueOf(principalWon);
        
        return switch (compoundingType) {
            case SIMPLE -> computeSimpleInterest(principal, annualRate, lastAccrualTs, asOf);
            case DAILY -> computeDailyCompoundInterest(principal, currentAccrued, annualRate, lastAccrualTs, asOf);
            case MONTH -> computeMonthlyCompoundInterest(principal, currentAccrued, annualRate, lastAccrualTs, asOf);
            case YEAR -> computeYearlyCompoundInterest(principal, currentAccrued, annualRate, lastAccrualTs, asOf);
        };
    }
    
    /**
     * 단리 이자를 계산합니다.
     * 공식: 원금 × 연이율 × (일수 / 365)
     *
     * @param principal 원금
     * @param annualRate 연이율 (소수점)
     * @param from 시작 시점
     * @param to 종료 시점
     * @return 단리 이자
     */
    private BigDecimal computeSimpleInterest(
        BigDecimal principal,
        BigDecimal annualRate,
        Instant from,
        Instant to
    ) {
        long daysBetween = ChronoUnit.DAYS.between(from, to);

        return principal
            .multiply(annualRate)
            .multiply(BigDecimal.valueOf(daysBetween))
            .divide(DAYS_IN_YEAR, SCALE, ROUNDING_MODE);
    }
    
    /**
     * 일복리 이자를 계산합니다.
     * 공식: (원금 + 누적이자) × ((1 + 일이율)^일수 - 1)
     *
     * @param principal 원금
     * @param currentAccrued 현재 누적된 이자
     * @param annualRate 연이율 (소수점)
     * @param from 시작 시점
     * @param to 종료 시점
     * @return 일복리 이자
     */
    private BigDecimal computeDailyCompoundInterest(
        BigDecimal principal,
        BigDecimal currentAccrued,
        BigDecimal annualRate,
        Instant from,
        Instant to
    ) {
        long daysBetween = ChronoUnit.DAYS.between(from, to);
        if (daysBetween == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal dailyRate = annualRate.divide(DAYS_IN_YEAR, SCALE + 2, ROUNDING_MODE);
        BigDecimal compoundBase = principal.add(currentAccrued);

        // (1 + dailyRate)^days - 1) * compoundBase
        BigDecimal onePlusDailyRate = BigDecimal.ONE.add(dailyRate);
        BigDecimal compoundFactor = pow(onePlusDailyRate, daysBetween);

        return compoundFactor.subtract(BigDecimal.ONE)
            .multiply(compoundBase)
            .setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * 월복리 이자를 계산합니다.
     * 1개월 미만인 경우 단리로 계산하고, 1개월 이상인 경우 월복리로 계산합니다.
     * 공식: (원금 + 누적이자) × ((1 + 월이율)^개월수 - 1)
     *
     * @param principal 원금
     * @param currentAccrued 현재 누적된 이자
     * @param annualRate 연이율 (소수점)
     * @param from 시작 시점
     * @param to 종료 시점
     * @return 월복리 이자
     */
    private BigDecimal computeMonthlyCompoundInterest(
        BigDecimal principal,
        BigDecimal currentAccrued,
        BigDecimal annualRate,
        Instant from,
        Instant to
    ) {
        long monthsBetween = ChronoUnit.MONTHS.between(from, to);
        if (monthsBetween == 0) {
            return computeSimpleInterest(principal, annualRate, from, to);
        }

        BigDecimal monthlyRate = annualRate.divide(MONTHS_IN_YEAR, SCALE + 2, ROUNDING_MODE);
        BigDecimal compoundBase = principal.add(currentAccrued);

        BigDecimal onePlusMonthlyRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal compoundFactor = pow(onePlusMonthlyRate, monthsBetween);

        return compoundFactor.subtract(BigDecimal.ONE)
            .multiply(compoundBase)
            .setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * 연복리 이자를 계산합니다.
     * 1년 미만인 경우 단리로 계산하고, 1년 이상인 경우 연복리로 계산합니다.
     * 공식: (원금 + 누적이자) × ((1 + 연이율)^년수 - 1)
     *
     * @param principal 원금
     * @param currentAccrued 현재 누적된 이자
     * @param annualRate 연이율 (소수점)
     * @param from 시작 시점
     * @param to 종료 시점
     * @return 연복리 이자
     */
    private BigDecimal computeYearlyCompoundInterest(
        BigDecimal principal,
        BigDecimal currentAccrued,
        BigDecimal annualRate,
        Instant from,
        Instant to
    ) {
        long yearsBetween = ChronoUnit.YEARS.between(from, to);
        if (yearsBetween == 0) {
            return computeSimpleInterest(principal, annualRate, from, to);
        }

        BigDecimal compoundBase = principal.add(currentAccrued);
        BigDecimal onePlusAnnualRate = BigDecimal.ONE.add(annualRate);
        BigDecimal compoundFactor = pow(onePlusAnnualRate, yearsBetween);

        return compoundFactor.subtract(BigDecimal.ONE)
            .multiply(compoundBase)
            .setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * BigDecimal 거듭제곱을 계산합니다.
     * 이진 거듭제곱법(Binary Exponentiation)을 사용하여 효율적으로 계산합니다.
     *
     * @param base 밑수
     * @param exponent 지수 (양의 정수)
     * @return base^exponent
     */
    private BigDecimal pow(BigDecimal base, long exponent) {
        if (exponent == 0) {
            return BigDecimal.ONE;
        }
        if (exponent == 1) {
            return base;
        }

        BigDecimal result = BigDecimal.ONE;
        BigDecimal currentBase = base;
        long exp = exponent;

        // 이진 거듭제곱법: O(log n) 시간 복잡도
        while (exp > 0) {
            if (exp % 2 == 1) {
                result = result.multiply(currentBase);
            }
            currentBase = currentBase.multiply(currentBase);
            exp /= 2;
        }

        return result.setScale(SCALE + 2, ROUNDING_MODE);
    }
}