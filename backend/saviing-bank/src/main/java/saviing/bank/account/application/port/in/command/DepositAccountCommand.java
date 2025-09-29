package saviing.bank.account.application.port.in.command;

import saviing.bank.common.vo.MoneyWon;
import saviing.bank.account.domain.vo.AccountId;

/**
 * 계좌 입금 명령.
 *
 * @param accountId 계좌 ID
 * @param amount 입금 금액
 */
public record DepositAccountCommand(
    AccountId accountId,
    MoneyWon amount
) {

    /**
     * 기본 타입으로부터 Command를 생성합니다.
     *
     * @param accountId 계좌 ID
     * @param amount 입금 금액 (원 단위)
     * @return DepositAccountCommand
     */
    public static DepositAccountCommand of(Long accountId, Long amount) {
        return new DepositAccountCommand(
            AccountId.of(accountId),
            MoneyWon.of(amount)
        );
    }
}

