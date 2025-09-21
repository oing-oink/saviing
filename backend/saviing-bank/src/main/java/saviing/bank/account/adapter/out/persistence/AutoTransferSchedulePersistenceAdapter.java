package saviing.bank.account.adapter.out.persistence;

import java.util.Optional;
import java.util.List;
import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import saviing.common.annotation.ExecutionTime;
import saviing.bank.account.adapter.out.persistence.entity.AutoTransferScheduleJpaEntity;
import saviing.bank.account.adapter.out.persistence.repository.JpaAutoTransferScheduleRepository;
import saviing.bank.account.application.port.out.AutoTransferSchedulePort;
import saviing.bank.account.domain.model.AutoTransferSchedule;
import saviing.bank.account.domain.vo.AccountId;
import saviing.bank.account.domain.vo.AutoTransferScheduleId;

/**
 * 자동이체 스케줄 도메인과 JPA 엔티티 간 변환을 담당하는 퍼시스턴스 어댑터.
 */
@ExecutionTime
@Repository
@RequiredArgsConstructor
public class AutoTransferSchedulePersistenceAdapter implements AutoTransferSchedulePort {

    private final JpaAutoTransferScheduleRepository repository;

    /**
     * 계좌 ID로 자동이체 스케줄을 조회한다.
     *
     * @param accountId 조회할 계좌 식별자
     * @return 자동이체 스케줄(Optional)
     */
    @Override
    public Optional<AutoTransferSchedule> findByAccountId(AccountId accountId) {
        return repository.findByAccountId(accountId.value())
            .map(AutoTransferScheduleJpaEntity::toDomain);
    }

    /**
     * 자동이체 스케줄을 새로 저장하고 식별자를 반환한다.
     *
     * @param schedule 저장할 자동이체 스케줄 도메인 모델
     * @return 생성된 자동이체 스케줄 식별자
     */
    @Override
    public AutoTransferScheduleId create(AutoTransferSchedule schedule) {
        AutoTransferScheduleJpaEntity entity = AutoTransferScheduleJpaEntity.fromDomain(schedule);
        AutoTransferScheduleJpaEntity saved = repository.save(entity);
        return AutoTransferScheduleId.of(saved.getId());
    }

    /**
     * 기존 자동이체 스케줄을 갱신한다.
     *
     * @param schedule 갱신할 자동이체 스케줄 도메인 모델
     */
    @Override
    public void update(AutoTransferSchedule schedule) {
        if (schedule.getId() == null) {
            throw new IllegalArgumentException("자동이체 스케줄 ID가 존재하지 않습니다");
        }
        AutoTransferScheduleJpaEntity entity = repository.findById(schedule.getId().value())
            .orElseThrow(() -> new IllegalStateException("자동이체 스케줄을 찾을 수 없습니다: " + schedule.getId().value()));
        entity.updateFromDomain(schedule);
        repository.save(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AutoTransferSchedule> findDueSchedules(LocalDate referenceDate) {
        return repository.findDueSchedulesForUpdate(referenceDate)
            .stream()
            .map(AutoTransferScheduleJpaEntity::toDomain)
            .toList();
    }
}
