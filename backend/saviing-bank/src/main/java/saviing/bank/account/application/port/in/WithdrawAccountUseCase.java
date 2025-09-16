package saviing.bank.account.application.port.in;

import saviing.bank.account.application.port.in.command.WithdrawAccountCommand;
import saviing.bank.account.domain.model.Account;

/**
 * 계좌 출금 유스케이스.
 */
public interface WithdrawAccountUseCase {

    /**
     * 계좌에서 금액을 출금한다.
     *
     * @param command 출금 명령
     * @return 출금 처리된 계좌 도메인 객체
     */
    Account withdraw(WithdrawAccountCommand command);
}

