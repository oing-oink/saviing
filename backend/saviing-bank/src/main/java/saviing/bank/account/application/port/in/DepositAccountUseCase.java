package saviing.bank.account.application.port.in;

import saviing.bank.account.application.port.in.command.DepositAccountCommand;
import saviing.bank.account.domain.model.Account;

/**
 * 계좌 입금 유스케이스.
 */
public interface DepositAccountUseCase {

    /**
     * 계좌에 금액을 입금한다.
     *
     * @param command 입금 명령
     * @return 입금 처리된 계좌 도메인 객체
     */
    Account deposit(DepositAccountCommand command);
}

