package saviing.bank.account.application.port.in.result;

import saviing.bank.account.domain.model.Account;
import saviing.bank.common.vo.MoneyWon;

/**
 * 잔액 업데이트 처리 결과 (기본 타입 DTO).
 */
public record BalanceUpdateResult(
    Long accountId,
    Long previousBalance,
    Long currentBalance,
    Long transactionAmount
) {

    /**
     * 도메인 객체로부터 결과를 생성합니다.
     *
     * @param account 처리된 계좌
     * @param previousBalance 이전 잔액
     * @param transactionAmount 거래 금액
     * @return BalanceUpdateResult
     */
    public static BalanceUpdateResult from(
        Account account,
        MoneyWon previousBalance,
        MoneyWon transactionAmount
    ) {
        return new BalanceUpdateResult(
            account.getId().value(),
            previousBalance.amount(),
            account.getBalance().amount(),
            transactionAmount.amount()
        );
    }
}

