package saviing.bank.account.adapter.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import saviing.bank.account.adapter.out.persistence.entity.AutoTransferScheduleJpaEntity;

/**
 * 자동이체 스케줄을 관리하는 JPA 리포지토리.
 */
public interface JpaAutoTransferScheduleRepository extends JpaRepository<AutoTransferScheduleJpaEntity, Long> {

    /**
     * 계좌 ID로 자동이체 스케줄을 조회한다.
     *
     * @param accountId 조회할 계좌 ID
     * @return 자동이체 스케줄(Optional)
     */
    Optional<AutoTransferScheduleJpaEntity> findByAccountId(Long accountId);
}
