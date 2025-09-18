package saviing.bank.transaction.application.port.out;

import saviing.bank.account.domain.model.Account;
import saviing.bank.common.vo.MoneyWon;

/**
 * 계좌 잔액 업데이트 포트
 * 거래 도메인에서 계좌 잔액을 업데이트하는 기능을 제공한다.
 */
public interface UpdateAccountBalancePort {

    /**
     * 계좌의 잔액을 새로운 값으로 업데이트한다
     *
     * @param account 업데이트할 계좌
     * @param newBalance 새로운 잔액
     */
    void updateBalance(Account account, MoneyWon newBalance);
}