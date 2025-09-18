package saviing.bank.account.application.port.in.command;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.account.domain.vo.AccountId;

/**
 * 계좌 출금 명령.
 *
 * @param accountId 계좌 ID
 * @param amount 출금 금액
 */
public record WithdrawAccountCommand(
    AccountId accountId,
    MoneyWon amount
) {

    /**
     * 기본 타입으로부터 Command를 생성합니다.
     *
     * @param accountId 계좌 ID
     * @param amount 출금 금액 (원 단위)
     * @return WithdrawAccountCommand
     */
    public static WithdrawAccountCommand of(Long accountId, Long amount) {
        return new WithdrawAccountCommand(
            AccountId.of(accountId),
            MoneyWon.of(amount)
        );
    }
}

