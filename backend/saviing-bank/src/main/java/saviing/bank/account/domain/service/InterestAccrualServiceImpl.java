package saviing.bank.account.domain.service;

import org.springframework.stereotype.Service;

import saviing.bank.account.domain.exception.InvalidRateException;
import saviing.bank.account.domain.model.CompoundingType;
import saviing.bank.account.domain.vo.BasisPoints;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
public class InterestAccrualServiceImpl implements InterestAccrualService {
    
    private static final int SCALE = 6;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final BigDecimal DAYS_IN_YEAR = BigDecimal.valueOf(365);
    private static final BigDecimal MONTHS_IN_YEAR = BigDecimal.valueOf(12);
    
    @Override
    public BigDecimal computeAccrual(
        long principalWon,
        BigDecimal currentAccrued,
        BasisPoints baseRate,
        BasisPoints bonusRate,
        CompoundingType compoundingType,
        Instant lastAccrualTs,
        Instant asOf
    ) {
        Objects.requireNonNull(currentAccrued, "현재 누적이자는 필수입니다");
        Objects.requireNonNull(baseRate, "기본금리는 필수입니다");
        Objects.requireNonNull(bonusRate, "보너스금리는 필수입니다");
        Objects.requireNonNull(compoundingType, "복리방식은 필수입니다");
        Objects.requireNonNull(lastAccrualTs, "마지막 계산시점은 필수입니다");
        Objects.requireNonNull(asOf, "계산기준시점은 필수입니다");
        
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
     * BigDecimal 거듭제곱 계산 (간단한 구현)
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