package saviing.bank.account.application.port.in;

import saviing.bank.account.application.port.in.command.UpdateAutoTransferScheduleCommand;
import saviing.bank.account.application.port.in.result.GetAccountResult;

/**
 * 적금 자동이체 스케줄을 수정하는 유스케이스.
 */
public interface UpdateAutoTransferScheduleUseCase {

    /**
     * 자동이체 스케줄을 수정한다.
     *
     * @param command 자동이체 수정 명령
     * @return 수정 후 계좌 정보 결과
     */
    GetAccountResult updateAutoTransferSchedule(UpdateAutoTransferScheduleCommand command);
}
