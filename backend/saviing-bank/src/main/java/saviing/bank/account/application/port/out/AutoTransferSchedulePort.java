package saviing.bank.account.application.port.out;

import java.util.Optional;

import saviing.bank.account.domain.model.AutoTransferSchedule;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.AutoTransferScheduleId;

/**
 * 자동이체 스케줄 영속화를 위한 포트 인터페이스.
 */
public interface AutoTransferSchedulePort {

    /**
     * 계좌별 자동이체 스케줄을 조회한다.
     *
     * @param accountId 조회할 계좌 식별자
     * @return 자동이체 스케줄(Optional)
     */
    Optional<AutoTransferSchedule> findByAccountId(AccountId accountId);

    /**
     * 새로운 자동이체 스케줄을 저장하고 식별자를 반환한다.
     *
     * @param schedule 저장할 자동이체 스케줄
     * @return 생성된 스케줄 식별자
     */
    AutoTransferScheduleId create(AutoTransferSchedule schedule);

    /**
     * 기존 자동이체 스케줄을 갱신한다.
     *
     * @param schedule 갱신할 자동이체 스케줄
     */
    void update(AutoTransferSchedule schedule);
}
