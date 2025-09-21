package saviing.bank.account.adapter.out.persistence.repository;

import java.util.Optional;
import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import saviing.bank.account.adapter.out.persistence.entity.AutoTransferScheduleJpaEntity;
import jakarta.persistence.LockModeType;

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

    /**
     * 활성화된 자동이체 중 지정된 날짜 이전(포함) 실행 예정인 항목을 조회한다.
     *
     * @param referenceDate 기준 날짜
     * @return 실행 대상 자동이체 목록
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AutoTransferScheduleJpaEntity s where s.enabled = true and s.nextRunDate <= :referenceDate")
    List<AutoTransferScheduleJpaEntity> findDueSchedulesForUpdate(LocalDate referenceDate);
}
