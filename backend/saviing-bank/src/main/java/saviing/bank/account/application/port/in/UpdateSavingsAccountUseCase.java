package saviing.bank.account.application.port.in;

import saviing.bank.account.application.port.in.command.UpdateSavingsAccountCommand;
import saviing.bank.account.application.port.in.result.GetAccountResult;

/**
 * 적금 계좌의 설정을 수정하는 유스케이스입니다.
 */
public interface UpdateSavingsAccountUseCase {

    /**
     * 적금 계좌의 목표 금액과 만기 출금 계좌를 수정합니다.
     *
     * @param command 수정에 필요한 정보
     * @return 수정된 계좌 정보
     */
    GetAccountResult updateSavingsAccount(UpdateSavingsAccountCommand command);
}
