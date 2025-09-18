package saviing.bank.account.application.port.in;

import saviing.bank.account.application.port.in.command.CloseSavingsAccountCommand;
import saviing.bank.account.application.port.in.result.GetAccountResult;

/**
 * 적금 계좌 해지를 수행하는 유스케이스입니다.
 */
public interface CloseSavingsAccountUseCase {

    /**
     * 적금 계좌를 해지 상태로 변경합니다.
     *
     * @param command 해지에 필요한 정보
     * @return 해지된 계좌 정보
     */
    GetAccountResult closeSavingsAccount(CloseSavingsAccountCommand command);
}
