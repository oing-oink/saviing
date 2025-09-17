package saviing.bank.transaction.adapter.out.persistence.repository.ledger;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import saviing.bank.transaction.adapter.out.persistence.entity.ledger.LedgerPairJpaEntity;

/**
 * LedgerPair JPA 접근을 위한 Spring Data 리포지토리.
 */
public interface LedgerPairJpaRepository extends JpaRepository<LedgerPairJpaEntity, Long> {

    @EntityGraph(attributePaths = "entries")
    Optional<LedgerPairJpaEntity> findByIdempotencyKey(String idempotencyKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select lp from LedgerPairJpaEntity lp left join fetch lp.entries where lp.idempotencyKey = :idempotencyKey")
    Optional<LedgerPairJpaEntity> lockByIdempotencyKey(@Param("idempotencyKey") String idempotencyKey);
}
