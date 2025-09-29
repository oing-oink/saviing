package saviing.bank.account.application.port.in.result;

import saviing.bank.account.domain.model.Account;
import saviing.bank.account.domain.vo.BasisPoints;

/**
 * 이자율 업데이트 처리 결과 (기본 타입 DTO).
 */
public record UpdateInterestRateResult(
    Long accountId,
    Double currentBonusRatePercentage
) {

    /**
     * 도메인 객체로부터 결과를 생성합니다.
     *
     * @param account 이자율이 업데이트된 계좌
     * @return UpdateInterestRateResult
     */
    public static UpdateInterestRateResult from(Account account) {
        return new UpdateInterestRateResult(
            account.getId().value(),
            account.getBonusRate().toPercentage()
        );
    }

    /**
     * 계좌 ID와 보너스 금리로부터 결과를 생성합니다.
     *
     * @param accountId 계좌 ID
     * @param bonusRate 현재 보너스 금리
     * @return UpdateInterestRateResult
     */
    public static UpdateInterestRateResult of(Long accountId, BasisPoints bonusRate) {
        return new UpdateInterestRateResult(
            accountId,
            bonusRate.toPercentage()
        );
    }
}