package saviing.bank.account.adapter.out.persistence.repository;

import java.util.Optional;
import java.util.List;
import java.time.LocalDate;
import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import saviing.bank.account.adapter.out.persistence.entity.AutoTransferScheduleJpaEntity;
import saviing.bank.account.domain.vo.AutoTransferScheduleId;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

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
     * 활성화된 자동이체 중 지정된 날짜 이전(포함) 실행 예정인 항목의 ID만을 조회한다.
     *
     * @param referenceDate 기준 날짜
     * @return 실행 대상 자동이체 목록
     */
    @Query("""
        select s.id
        from AutoTransferScheduleJpaEntity s
        where s.enabled = true and s.nextRunDate <= :referenceDate
    """)
    List<Long> findDueScheduleIds(@Param("referenceDate") LocalDate referenceDate);
    
    /**
     * 지정된 ID의 자동이체 스케줄을 조회한다.
     *
     * @param id 조회할 자동이체 스케줄 ID
     * @return 자동이체 스케줄(Optional)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AutoTransferScheduleJpaEntity s where s.id = :id")
    Optional<AutoTransferScheduleJpaEntity> findByIdForUpdate(@Param("id") Long id);
  

    /**
     * 활성화된 자동이체 스케줄의 실행일을 일괄 리셋한다.
     * flushAutomatically = true (실행 전 동기화)
     * clearAutomatically = true (실행 후 캐시 비우기)
     *
     * @param nextRunDate 리셋할 실행 예정일
     * @return 업데이트된 행 수
     */
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AutoTransferScheduleJpaEntity s set s.nextRunDate = :nextRunDate, s.lastExecutedAt = null, s.updatedAt = :updatedAt where s.enabled = true")
    int resetAllNextRunDate(
        @Param("nextRunDate") LocalDate nextRunDate,
        @Param("updatedAt") Instant updatedAt
    );
}
