package saviing.bank.account.domain.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record BasisPoints(short value) {
    
    public BasisPoints {
        if (value < 0) {
            throw new IllegalArgumentException("베이시스 포인트는 음수일 수 없습니다: " + value);
        }
    }
    
    public static BasisPoints of(int value) {
        return new BasisPoints((short) value);
    }
    
    public static BasisPoints zero() {
        return new BasisPoints((short) 0);
    }

    /**
     * 퍼센트 값으로부터 베이시스 포인트를 생성합니다.
     * 예: 3.5% -> 350bp
     *
     * @param percentage 퍼센트 값 (예: 3.5)
     * @return BasisPoints
     */
    public static BasisPoints fromPercentage(double percentage) {
        return new BasisPoints((short) Math.round(percentage * 100));
    }
    
    public BasisPoints add(BasisPoints other) {
        return new BasisPoints((short) (this.value + other.value));
    }
    
    public BigDecimal toPercent() {
        return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_EVEN);
    }

    /**
     * 퍼센트 값을 double로 반환합니다.
     * 예: 350bp -> 3.5
     *
     * @return 퍼센트 값
     */
    public double toPercentage() {
        return value / 100.0;
    }
    
    public BigDecimal toDecimal() {
        return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(10000), 6, RoundingMode.HALF_EVEN);
    }
    
    public boolean isGreaterThan(BasisPoints other) {
        return this.value > other.value;
    }
    
    public boolean isZero() {
        return this.value == 0;
    }
    
    @Override
    public String toString() {
        return value + "bp (" + toPercent() + "%)";
    }
}