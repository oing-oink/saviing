package saviing.bank.account.domain.service;

import java.math.BigDecimal;
import java.time.Instant;

import saviing.bank.account.domain.model.CompoundingType;
import saviing.bank.account.domain.vo.BasisPoints;

/**
 * 계좌 이자 계산을 담당하는 도메인 서비스입니다.
 *
 * 다양한 복리 방식(단리, 일복리, 월복리, 연복리)에 따른 이자 계산을 제공하며,
 * 정밀한 금융 계산을 위해 BigDecimal을 사용하여 부동소수점 오차를 방지합니다.
 *
 * 주요 기능:
 * - 원금과 기간에 따른 이자 계산
 * - 기본 금리 + 보너스 금리 적용
 * - 복리 방식별 차별화된 계산 로직
 * - 누적 이자 기반 복리 계산 지원
 */
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