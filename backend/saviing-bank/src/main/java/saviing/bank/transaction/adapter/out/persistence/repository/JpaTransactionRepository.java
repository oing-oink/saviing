package saviing.bank.transaction.adapter.out.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import saviing.bank.transaction.adapter.out.persistence.entity.TransactionJpaEntity;

@Repository
public interface JpaTransactionRepository extends JpaRepository<TransactionJpaEntity, Long> {

    @Query("SELECT t FROM TransactionJpaEntity t WHERE t.accountId = :accountId ORDER BY t.postedAt DESC")
    List<TransactionJpaEntity> findByAccountIdOrderByPostedAtDesc(@Param("accountId") Long accountId);

    @Query("SELECT t FROM TransactionJpaEntity t WHERE t.accountId = :accountId ORDER BY t.postedAt DESC")
    List<TransactionJpaEntity> findByAccountIdOrderByPostedAtDesc(
        @Param("accountId") Long accountId,
        Pageable pageable
    );

    @Query("SELECT t FROM TransactionJpaEntity t WHERE t.accountId = :accountId AND t.idempotencyKey = :idempotencyKey")
    Optional<TransactionJpaEntity> findByAccountIdAndIdempotencyKey(
        @Param("accountId") Long accountId,
        @Param("idempotencyKey") String idempotencyKey
    );

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
           "FROM TransactionJpaEntity t WHERE t.accountId = :accountId AND t.idempotencyKey = :idempotencyKey")
    boolean existsByAccountIdAndIdempotencyKey(
        @Param("accountId") Long accountId,
        @Param("idempotencyKey") String idempotencyKey
    );
}