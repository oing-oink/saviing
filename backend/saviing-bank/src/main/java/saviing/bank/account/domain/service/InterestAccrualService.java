package saviing.bank.account.domain.service;

import java.math.BigDecimal;
import java.time.Instant;

import saviing.bank.account.domain.model.CompoundingType;
import saviing.bank.account.domain.vo.BasisPoints;

public interface InterestAccrualService {
    
    /**
     * 이자를 계산한다.
     *
     * @param principalWon 원금 (KRW 원 단위)
     * @param currentAccrued 현재 누적된 이자 (정밀도 6자리)
     * @param baseRate 기본 금리 (베이시스 포인트)
     * @param bonusRate 보너스 금리 (베이시스 포인트)
     * @param compoundingType 복리 방식
     * @param lastAccrualTs 마지막 이자 계산 시점
     * @param asOf 계산 기준 시점
     * @return 추가로 발생한 이자 (scale=6 유지)
     */
    BigDecimal computeAccrual(
        long principalWon,
        BigDecimal currentAccrued,
        BasisPoints baseRate,
        BasisPoints bonusRate,
        CompoundingType compoundingType,
        Instant lastAccrualTs,
        Instant asOf
    );
}